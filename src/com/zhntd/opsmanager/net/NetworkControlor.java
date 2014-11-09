
package com.zhntd.opsmanager.net;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhntd.opsmanager.provider.DataBaseCreator;
import com.zhntd.opsmanager.utils.Logger;

/**
 * Control network activities of the given application.
 * 
 * @author zhntd
 * @date Nov 8, 2014
 * @time 3:56:47 PM
 */
public class NetworkControlor {

    private Editor mEditor;
    private SharedPreferences mPreferences;
    private Context mContext;

    /* 4 modes */
    public static final int MODE_ASK = 3;
    public static final int MODE_ERROR = 2;
    public static final int MODE_ALLOWED = 0;
    public static final int MODE_DENIED = 1;

    /**
     * @param sharedPreferences
     * @param mContext
     */
    public NetworkControlor(Context mContext) {
        this.mContext = mContext;
        initialize();
    }

    private void initialize() {
        this.mPreferences = mContext.getSharedPreferences(Api.PREFS_NAME, 0);
        this.mEditor = mPreferences.edit();
    }

    /**
     * @param uid
     * @param type
     * @return
     */
    public synchronized boolean writeToSpf(String uid, DataType type) {
        String uids = mPreferences.getString(Api.PREF_WIFI_UIDS, "");
        uids = uids + "|" + uid;
        if (DataType.MOBILE == type)
            return mEditor.putString(Api.PREF_WIFI_UIDS, uids).commit();
        else {
            return mEditor.putString(Api.PREF_3G_UIDS, uids).commit();
        }
    }

    /**
     * @param uid
     * @return
     */
    public synchronized int getCurrentMode(int uid) {
        DataBaseCreator dataBaseCreator = null;
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            dataBaseCreator = new DataBaseCreator(mContext,
                    DataBaseCreator.DATABASE_NAME, null, DataBaseCreator.DATA_BASE_VERSION);
            sqLiteDatabase = dataBaseCreator.getWritableDatabase();
            cursor = sqLiteDatabase.query(DataBaseCreator.TABLE_NAME, null, "UID=" + uid,
                    null, null, null, "PACKAGENAME DESC");
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                // Resume there is no same uids.
                int mode = cursor.getInt(DataBaseCreator.COLUMN_MODE);
                return mode;
            } else {
                // If the uid is not ever saved, use the default mode.
                return MODE_ASK;
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
    public synchronized boolean setCurrentMode(int uid, String packageName, int mode) {
        DataBaseCreator dataBaseCreator = null;
        SQLiteDatabase sqLiteDatabase = null;
        try {
            dataBaseCreator = new DataBaseCreator(mContext,
                    DataBaseCreator.DATABASE_NAME, null, DataBaseCreator.DATA_BASE_VERSION);
            sqLiteDatabase = dataBaseCreator.getWritableDatabase();
            final ContentValues values = new ContentValues();
            values.put("PACKAGENAME", packageName);
            values.put("UID", uid);
            values.put("MODE", mode);
            long id = sqLiteDatabase.insert(DataBaseCreator.TABLE_NAME, null, values);
            if (id == -1) {
                Logger.logger("ERROR when insert to db...");
                return false;
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
     * @param dataBaseCreator
     * @param sqLiteDatabase
     * @param cursor
     */
    private void cleanUp(DataBaseCreator dataBaseCreator,
            SQLiteDatabase sqLiteDatabase,
            Cursor cursor) {
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
