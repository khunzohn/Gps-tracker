package com.hilllander.khunzohn.gpstracker.database.table;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by khunzohn on 1/2/16.
 */
public class DeviceTable {
    public static final String TABLE_NAME_DEVICE = "devices";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DEVICE_NAME = "name";
    public static final String COLUMN_DEVICE_TYPE = "type";
    public static final String COLUMN_SIM_NUMBER = "number";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AUTHORIZATION = "authorization";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TRACKED_DATE = "tracked_date";
    public static final String COLUMN_TRACKED_TIME = "tracked_time";
    private static final String COMMA = ",";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_REAL = " REAL";
    private static final String PRIMARY_KEY_AUTO_INCREMENT = " primary key autoincrement";
    private static final String NOT_NULL = "not null";

    private static final String SQL_CREATE_TABLE = "create table " + TABLE_NAME_DEVICE + "(" +
            COLUMN_ID + TYPE_INTEGER + PRIMARY_KEY_AUTO_INCREMENT + COMMA +
            COLUMN_DEVICE_NAME + TYPE_TEXT + NOT_NULL + COMMA +
            COLUMN_DEVICE_TYPE + TYPE_TEXT + NOT_NULL + COMMA +
            COLUMN_SIM_NUMBER + TYPE_TEXT + NOT_NULL + COMMA +
            COLUMN_PASSWORD + TYPE_TEXT + NOT_NULL + COMMA +
            COLUMN_AUTHORIZATION + TYPE_TEXT + NOT_NULL + COMMA +
            COLUMN_LATITUDE + TYPE_REAL + NOT_NULL + COMMA +
            COLUMN_LONGITUDE + TYPE_REAL + NOT_NULL + COMMA +
            COLUMN_TRACKED_DATE + TYPE_TEXT + NOT_NULL + COMMA +
            COLUMN_TRACKED_TIME + TYPE_TEXT + NOT_NULL + ");";
    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME_DEVICE;

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
