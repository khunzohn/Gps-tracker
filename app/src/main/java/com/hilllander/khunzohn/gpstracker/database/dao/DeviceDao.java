package com.hilllander.khunzohn.gpstracker.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.hilllander.khunzohn.gpstracker.database.GpsDbHelper;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.database.table.DeviceTable;
import com.hilllander.khunzohn.gpstracker.util.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *Created by khunzohn on 1/2/16.
 * modified by khunzohn on 1/6/16.(add deleteDevice(),add column_photo_url
 */
public class DeviceDao {
    private static final String TAG = Logger.generateTag(DeviceDao.class);
    private final String[] ALL_COLUMN = {DeviceTable.COLUMN_ID, DeviceTable.COLUMN_DEVICE_NAME,
            DeviceTable.COLUMN_DEVICE_TYPE, DeviceTable.COLUMN_SIM_NUMBER, DeviceTable.COLUMN_PASSWORD,
            DeviceTable.COLUMN_AUTHORIZATION, DeviceTable.COLUMN_LATITUDE, DeviceTable.COLUMN_LONGITUDE,
            DeviceTable.COLUMN_TRACKED_DATE, DeviceTable.COLUMN_TRACKED_TIME, DeviceTable.COLUMN_PHOTO_URL};
    private SQLiteDatabase db;
    private GpsDbHelper dbHelper;

    public DeviceDao(Context context) {
        dbHelper = new GpsDbHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        dbHelper.close();
    }

    public Device createDevice(Device device) throws SQLException {
        long insertId = -1;
        ContentValues values = new ContentValues();
        values.put(DeviceTable.COLUMN_DEVICE_NAME, device.getDeviceName());
        values.put(DeviceTable.COLUMN_DEVICE_TYPE, device.getDeviceType());
        values.put(DeviceTable.COLUMN_SIM_NUMBER, device.getSimNumber());
        values.put(DeviceTable.COLUMN_PASSWORD, device.getPassword());
        values.put(DeviceTable.COLUMN_AUTHORIZATION, device.getAuthorization());
        values.put(DeviceTable.COLUMN_LATITUDE, device.getLatitude());
        values.put(DeviceTable.COLUMN_LONGITUDE, device.getLongitude());
        values.put(DeviceTable.COLUMN_TRACKED_DATE, device.getTrackedDate());
        values.put(DeviceTable.COLUMN_TRACKED_TIME, device.getTrackedTime());
        values.put(DeviceTable.COLUMN_PHOTO_URL, device.getPhotoUrl());
        try {
            db.beginTransaction();
            insertId = db.insertWithOnConflict(DeviceTable.TABLE_NAME_DEVICE, null,
                    values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Logger.e(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
        Device createdDevice = null;
        if (insertId > 0) {
            createdDevice = getDeviceById(String.valueOf(insertId));
        }
        Logger.d(TAG, "InsertId : " + insertId + "CreatedDevice : " +
                String.valueOf(createdDevice) + " photo url : " + device.getPhotoUrl());
        return createdDevice;
    }

    public boolean deleteDevice(String deviceId) throws SQLException {
        int numOfDeviceDeleted = db.delete(DeviceTable.TABLE_NAME_DEVICE, DeviceTable.COLUMN_ID + " = " + deviceId, null);
        if (numOfDeviceDeleted > 0) {
            Logger.d(TAG, "Device with id " + deviceId + " has been deleted from db");
            return true;
        } else
            Logger.d(TAG, "Device with id " + deviceId + " can't be deleted");
        return false;
    }

    public Device updateDevice(Device device) throws SQLException {
        int rowUpdated = 0;
        ContentValues values = new ContentValues();
        values.put(DeviceTable.COLUMN_DEVICE_NAME, device.getDeviceName());
        values.put(DeviceTable.COLUMN_DEVICE_TYPE, device.getDeviceType());
        values.put(DeviceTable.COLUMN_SIM_NUMBER, device.getSimNumber());
        values.put(DeviceTable.COLUMN_PASSWORD, device.getPassword());
        values.put(DeviceTable.COLUMN_AUTHORIZATION, device.getAuthorization());
        values.put(DeviceTable.COLUMN_LATITUDE, device.getLatitude());
        values.put(DeviceTable.COLUMN_LONGITUDE, device.getLongitude());
        values.put(DeviceTable.COLUMN_TRACKED_DATE, device.getTrackedDate());
        values.put(DeviceTable.COLUMN_TRACKED_TIME, device.getTrackedTime());
        values.put(DeviceTable.COLUMN_PHOTO_URL, device.getPhotoUrl());
        try {
            db.beginTransaction();
            rowUpdated = db.updateWithOnConflict(DeviceTable.TABLE_NAME_DEVICE, values,
                    DeviceTable.COLUMN_ID + " = " + device.getId(),
                    null, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Logger.e(TAG, e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }
        Device updatedDevice = null;
        if (rowUpdated > 0)
            updatedDevice = getDeviceById(String.valueOf(device.getId()));
        return updatedDevice;
    }

    public Device getDeviceById(String id) throws SQLException {
        Cursor cursor = db.query(DeviceTable.TABLE_NAME_DEVICE, ALL_COLUMN,
                DeviceTable.COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Device device = cursorToDevice(cursor);
        cursor.close();
        return device;
    }

    public List<Device> getAllDevices() throws SQLException {
        List<Device> devices = new ArrayList<>();
        Cursor cursor = db.query(DeviceTable.TABLE_NAME_DEVICE, ALL_COLUMN, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Device device = cursorToDevice(cursor);
            devices.add(device);
            cursor.moveToNext();
        }
        cursor.close();
        return devices;

    }

    private Device cursorToDevice(Cursor cursor) {
        Device device = new Device();
        device.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_ID)));
        device.setDeviceName(cursor.getString(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_DEVICE_NAME)));
        device.setDeviceType(cursor.getString(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_DEVICE_TYPE)));
        device.setSimNumber(cursor.getString(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_SIM_NUMBER)));
        device.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_PASSWORD)));
        device.setAuthorization(cursor.getString(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_AUTHORIZATION)));
        device.setLatitude(cursor.getFloat(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_LATITUDE)));
        device.setLongitude(cursor.getFloat(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_LONGITUDE)));
        device.setTrackedDate(cursor.getString(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_TRACKED_DATE)));
        device.setTrackedTime(cursor.getString(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_TRACKED_TIME)));
        device.setPhotoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DeviceTable.COLUMN_PHOTO_URL)));
        return device;
    }
}
