package com.zhntd.opsmanager.net;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhntd.opsmanager.loader.ThreadPoolManager;
import com.zhntd.opsmanager.loader.ThreadPoolTask;
import com.zhntd.opsmanager.provider.DataBaseCreator;
import com.zhntd.opsmanager.utils.Logger;

/**
 * Control data activities of the given application.
 * 
 * @author zhntd
 * @date Nov 8, 2014
 * @time 3:56:47 PM
 */
public class NetworkControlor {

	public class EffectiveTask extends ThreadPoolTask {

		private Work work;
		private int mode;
		private Context context;

		/**
		 * @param uid
		 * @param packageName
		 * @param dataType
		 * @param work
		 * @param mode
		 */
		public EffectiveTask(String uid, String packageName, DataType dataType,
				Work work, int mode, Context context) {
			super(uid, packageName, dataType);
			this.work = work;
			this.mode = mode;
			this.context = context;
		}

		@Override
		public void run() {
			if (work == Work.WRITE_TO_SPF)
				writeToSpf(uid, dataType);
			if (work == Work.DELETE_FRM_SPF)
				deleteFrmSpf(uid, dataType);
			if (work == Work.SET_CURRENT_MODE)
				setCurrentMode(Integer.parseInt(uid), packageName, mode,
						dataType, context);
		}
	}

	public enum Work {
		WRITE_TO_SPF, DELETE_FRM_SPF, SET_CURRENT_MODE,
	}

	private ThreadPoolManager mPoolManager;
	public Editor mEditor;
	public SharedPreferences mPreferences;

	/* 4 modes */
	public static final int MODE_ASK = 3;
	public static final int MODE_ERROR = 2;
	public static final int MODE_ALLOWED = 0;
	public static final int MODE_DENIED = 1;

	private static final NetworkControlor mControlor = new NetworkControlor();

	/**
	 * @param context
	 * @return
	 */
	public static NetworkControlor prepare(Context context) {
		return mControlor.initialize(context);
	}

	/**
	 * @param context
	 * @return
	 */
	private NetworkControlor initialize(Context context) {
		if (mControlor.mPreferences == null)
			mControlor.mPreferences = context.getSharedPreferences(
					Api.PREFS_NAME, 0);
		if (mControlor.mEditor == null)
			mControlor.mEditor = mPreferences.edit();
		if (mPoolManager == null)
			mPoolManager = new ThreadPoolManager(ThreadPoolManager.TYPE_FIFO, 5);
		mPoolManager.start();
		return mControlor;
	}

	/**
	 * @param uid
	 * @param type
	 * @return
	 */
	public void writeToSpfAsync(String uid, DataType type, Context context) {
		mPoolManager.addAsyncTask(new EffectiveTask(uid, null, type,
				Work.WRITE_TO_SPF, -1, context));
	}

	/**
	 * @param uid
	 * @param type
	 * @return
	 */
	public void deleteFrmSpfAsync(String uid, DataType type, Context context) {
		mPoolManager.addAsyncTask(new EffectiveTask(uid, null, type,
				Work.DELETE_FRM_SPF, -1, context));
	}

	public void setCurrentModeAsync(int uid, String packageName, int mode,
			DataType dataType, Context context) {
		mPoolManager.addAsyncTask(new EffectiveTask(String.valueOf(uid),
				packageName, dataType, Work.SET_CURRENT_MODE, mode, context));
	}

	/**
	 * @param uid
	 * @param type
	 * @return
	 */
	private synchronized boolean writeToSpf(String uid, DataType type) {
		String uids = mPreferences.getString(Api.PREF_WIFI_UIDS, "");
		uids = uids + "|" + uid;
		if (DataType.MOBILE == type)
			return mEditor.putString(Api.PREF_3G_UIDS, uids).commit();
		else {
			return mEditor.putString(Api.PREF_WIFI_UIDS, uids).commit();
		}
	}

	/**
	 * @param uid
	 * @param type
	 * @return
	 */
	public synchronized boolean deleteFrmSpf(String uid, DataType type) {
		String valueString = "";
		String[] values = new String[100];
		String newUids = "";
		if (DataType.MOBILE == type) {
			valueString = mPreferences.getString(Api.PREF_3G_UIDS, "");
			if (valueString != "") {
				values = valueString.split("\\|");

				for (int i = 0; i < values.length; i++) {
					if (!values[i].equals(uid) && !values[i].equals("")) {
						newUids = newUids + "|" + values[i];
					}
				}
			}
			return mEditor.putString(Api.PREF_3G_UIDS, newUids).commit();
		} else {
			valueString = mPreferences.getString(Api.PREF_WIFI_UIDS, "");
			if (valueString != "") {
				values = valueString.split("\\|");
				for (int i = 0; i < values.length; i++) {
				}
				for (int i = 0; i < values.length; i++) {
					if (!values[i].equals(uid) && !values[i].equals("")) {
						newUids = newUids + "|" + values[i];
					}
				}
			}
			return mEditor.putString(Api.PREF_WIFI_UIDS, newUids).commit();
		}
	}

	/**
	 * @param uid
	 * @return
	 */
	public int getCurrentMode(int uid, Context context, DataType dataType) {
		DataBaseCreator dataBaseCreator = null;
		SQLiteDatabase sqLiteDatabase = null;
		Cursor cursor = null;
		try {
			dataBaseCreator = new DataBaseCreator(context,
					DataBaseCreator.DATABASE_NAME, null,
					DataBaseCreator.DATA_BASE_VERSION);
			sqLiteDatabase = dataBaseCreator.getWritableDatabase();
			cursor = sqLiteDatabase.query(DataBaseCreator.TABLE_NAME, null,
					"UID=" + uid, null, null, null, "PACKAGENAME DESC");
			cursor.moveToFirst();

			if (cursor.getCount() > 0) {
				// Resume there is no same uids.
				int mode = cursor.getInt(DataBaseCreator.COLUMN_MODE);
				Logger.logger("getCurrentMode get called, and return a mode of : "
						+ mode);
				return mode;
			} else {
				// If the uid is not ever saved, use the default mode.
				Logger.logger("getCurrentMode get called, and return a mode of : "
						+ "MODE_ALLOWED");
				return MODE_ALLOWED;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Logger.logger("ERROR when query db, starting clean up...");
			cleanUp(dataBaseCreator, sqLiteDatabase, cursor);
			return MODE_ASK;
		} finally {
			cleanUp(dataBaseCreator, sqLiteDatabase, cursor);
		}
	}

	/**
	 * @param uid
	 * @return
	 */
	private synchronized boolean setCurrentMode(int uid, String packageName,
			int mode, DataType dataType, Context context) {

		// first we apply the iptable.
		if (mode == NetworkControlor.MODE_DENIED) {
			writeToSpf(String.valueOf(uid), dataType);
		} else if (mode == NetworkControlor.MODE_ALLOWED) {
			deleteFrmSpf(String.valueOf(uid), dataType);
		}
		// apply if any change.
		applySavedIptablesRules(context, true);

		// try to delete first.
		deleteFrmDb(packageName, uid, context);
		// now insert.
		DataBaseCreator dataBaseCreator = null;
		SQLiteDatabase sqLiteDatabase = null;
		try {
			dataBaseCreator = new DataBaseCreator(context,
					DataBaseCreator.DATABASE_NAME, null,
					DataBaseCreator.DATA_BASE_VERSION);
			sqLiteDatabase = dataBaseCreator.getWritableDatabase();
			final ContentValues values = new ContentValues();
			values.put("PACKAGENAME", packageName);
			values.put("UID", uid);
			values.put("MODE", mode);
			long id = sqLiteDatabase.insert(DataBaseCreator.TABLE_NAME, null,
					values);
			if (id == -1) {
				Logger.logger("ERROR when insert to db...");
				return false;
			} else {
				Logger.logger("Successfully to set a mode to > " + mode
						+ "packageName >: " + packageName + "dataType:>"
						+ (dataType == DataType.MOBILE ? "MOBILE" : "WIFI"));
			}
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Logger.logger("ERROR when insert to db, starting clean up...");
			cleanUp(dataBaseCreator, sqLiteDatabase, null);
			return false;
		} finally {
			cleanUp(dataBaseCreator, sqLiteDatabase, null);
		}
	}

	/**
	 * @param packageName
	 * @param uid
	 * @return
	 */
	private int deleteFrmDb(String packageName, int uid, Context context) {
		DataBaseCreator dataBaseCreator = null;
		SQLiteDatabase sqLiteDatabase = null;
		try {
			dataBaseCreator = new DataBaseCreator(context,
					DataBaseCreator.DATABASE_NAME, null,
					DataBaseCreator.DATA_BASE_VERSION);
			sqLiteDatabase = dataBaseCreator.getWritableDatabase();
			int rows = sqLiteDatabase.delete(DataBaseCreator.TABLE_NAME,
					"UID=?", new String[] { uid + "" });
			Logger.logger("Deleted frm db, package:" + packageName
					+ "total rows count:" + rows);
			return rows;

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Logger.logger("ERROR when insert to db, starting clean up...");
			cleanUp(dataBaseCreator, sqLiteDatabase, null);
			return 0;
		} finally {
			cleanUp(dataBaseCreator, sqLiteDatabase, null);
		}
	}

	/**
	 * @param dataBaseCreator
	 * @param sqLiteDatabase
	 * @param cursor
	 */
	private void cleanUp(DataBaseCreator dataBaseCreator,
			SQLiteDatabase sqLiteDatabase, Cursor cursor) {
		try {
			if (sqLiteDatabase != null)
				sqLiteDatabase.close();
			if (dataBaseCreator != null)
				dataBaseCreator.close();
			if (cursor != null)
				cursor.close();
		} catch (Exception e) {
			Logger.logger("ERROR when clean up the db...");
		}
	}

	/**
	 * @param ctx
	 */
	public void showLog(Context ctx) {
		Api.showLog(ctx);
	}

	/**
	 * @param ctx
	 */
	public void showIptablesRules(Context ctx) {
		Api.showIptablesRules(ctx);
	}

	/**
	 * @param ctx
	 * @param enabled
	 */
	public void setEnabled(Context ctx, boolean enabled) {
		Api.setEnabled(ctx, enabled);
	}

	/**
	 * @param ctx
	 */
	public void saveRules(Context ctx) {
		Api.saveRules(ctx);
	}

	/**
	 * @param ctx
	 * @param showErrors
	 * @return
	 */
	public boolean purgeIptables(Context ctx, boolean showErrors) {
		return Api.purgeIptables(ctx, showErrors);
	}

	/**
	 * @param ctx
	 * @return
	 */
	public boolean isEnabled(Context ctx) {
		return Api.isEnabled(ctx);
	}

	/**
	 * USAGE: 1. If you want to deny an app, please provide at least a uid. and
	 * call writeToSpf to save rules in system, then call this method. 2. If you
	 * wanna grant an app please also provide a uid, and remove them from spf,
	 * then apply rules.
	 * 
	 * @param ctx
	 * @param showErrors
	 * @return
	 */
	public boolean applySavedIptablesRules(Context ctx, boolean showErrors) {
		return Api.applySavedIptablesRules(ctx, showErrors);
	}

	/**
	 * @param ctx
	 * @param showErrors
	 * @return
	 */
	public boolean applyIptablesRules(Context ctx, boolean showErrors) {
		return Api.applyIptablesRules(ctx, showErrors);
	}

}
