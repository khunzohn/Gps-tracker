package com.hilllander.khunzohn.gpstracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hilllander.khunzohn.gpstracker.database.table.DeviceTable;
import com.hilllander.khunzohn.gpstracker.util.Logger;

/**
 *Created by khunzohn on 1/2/16.
 */
public class GpsDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "devices.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = Logger.generateTag(GpsDbHelper.class);

    public GpsDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DeviceTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.d(TAG, "update db version from : " + oldVersion + " to : " + newVersion);
        DeviceTable.onUpdate(db, oldVersion, newVersion);
    }
}
