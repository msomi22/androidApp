package com.example.peter.myapplication2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by peter on 2/16/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Bluetooth";

    // Contacts table name
    private static final String TABLE_DEVICES = "devices";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String DEVICE_NAME = "name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DEVICES_TABLE = "CREATE TABLE " + TABLE_DEVICES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + DEVICE_NAME + " TEXT)";
        db.execSQL(CREATE_DEVICES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES);

        // Create tables again
        onCreate(db);
    }


    public void addDevice(String deviceName){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DEVICE_NAME, deviceName); // devicename

        // Inserting Row
        db.insert(TABLE_DEVICES, null, values);
        db.close(); // Closing database connection

    }

    public boolean deviceExist(String deviceName){
        boolean success = false;
       // deviceName = "test bt";
        SQLiteDatabase db = this.getReadableDatabase();

        /*Cursor cursor = db.query(TABLE_DEVICES, new String[] { KEY_ID, DEVICE_NAME},
                DEVICE_NAME + "=?",  new String[] { deviceName } , null, null, null);*/
        Cursor cursor = db.query(TABLE_DEVICES, null, DEVICE_NAME + "=?", new String[] { deviceName }, null, null, null, null);
        if (cursor.moveToFirst()) {

            success = true;
        }
        return  success;
    }

    public  ArrayList<String>  getDevices(){
        ArrayList<String> devicesList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                 devicesList.add(cursor.getString(1));


            } while (cursor.moveToNext());
        }


        return devicesList;
    }
}