package com.example.voters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FingerprintDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context, String s) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Users (" +
                "SerialNumber TEXT PRIMARY KEY," +
                "VoterID TEXT UNIQUE," +
                "UIDAIId TEXT UNIQUE," +
                "Name TEXT," +
                "Age INTEGER," +
                "Gender TEXT," +
                "ConstituencyCode TEXT UNIQUE," +
                "StateCode TEXT UNIQUE," +
                "ElectionID TEXT," +
                "FingerprintData BLOB" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades
    }

    public void insertUserData(String serialNumber, String voterID, String uidaiId, String name, int age, String gender, String constituencyCode, String stateCode, String electionID, byte[] fingerprintData) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SerialNumber", serialNumber);
        values.put("VoterID", voterID);
        values.put("UIDAIId", uidaiId);
        values.put("Name", name);
        values.put("Age", age);
        values.put("Gender", gender);
        values.put("ConstituencyCode", constituencyCode);
        values.put("StateCode", stateCode);
        values.put("ElectionID", electionID);
        values.put("FingerprintData", fingerprintData);
        db.insert("Users", null, values);
    }


}
