
package com.zhntd.opsmanager.provider;

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

    public static final String TABLE_NAME = "data_control_table";
    public static final String DATABASE_NAME = "com_zhntd_permmanager_data_db";
    public static int DATA_BASE_VERSION = 1;
    public static final int COLUMN_PACKAGENAME = 1;
    public static final int COLUMN_UID = 2;
    public static final int COLUMN_MODE = 3;

    // remember the white space
    private final String CREATE_TABLE_CMD = "CREATE TABLE"
            + " "
            + TABLE_NAME
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "PACKAGENAME TEXT, UID INTEGER, MODE INTEGER)";

    public DataBaseCreator(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete the table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // recreate
        onCreate(db);
    }

}
