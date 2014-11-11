package com.zhntd.opsmanager.provider;

import com.zhntd.opsmanager.utils.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper to handle db.
 * 
 * @author zhntd
 * @date Nov 9, 2014
 * @time 3:20:17 PM
 */
public class DataBaseCreator extends SQLiteOpenHelper {

	public static final String TABLE_NAME_WIFI = "data_control_table_wifi";
	public static final String TABLE_NAME_MOBILE = "data_control_table_mobile";
	public static final String DATABASE_NAME_WIFI = "com_zhntd_permmanager_data_db_wifi";
	public static final String DATABASE_NAME_MOBILE = "com_zhntd_permmanager_data_db_mobile";

	public static int DATA_BASE_VERSION = 1;

	public static final int COLUMN_PACKAGENAME = 1;
	public static final int COLUMN_UID = 2;
	public static final int COLUMN_MODE = 3;

	private String mTableName;

	// remember the white space
	private String CREATE_TABLE_CMD;

	public DataBaseCreator(Context context, String name, CursorFactory factory,
			int version, String tableName) {
		super(context, name, factory, version);
		this.mTableName = tableName;
		CREATE_TABLE_CMD = "CREATE TABLE" + " " + tableName
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
				+ "PACKAGENAME TEXT, UID INTEGER, MODE INTEGER)";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_CMD);
		Logger.logger("Create---->" + mTableName);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// delete the table
		db.execSQL("DROP TABLE IF EXISTS " + mTableName);
		// recreate
		onCreate(db);
	}

}
