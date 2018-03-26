package com.a3kings.falldetect;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBManager extends SQLiteOpenHelper {
    private static final String tablename = "EmergencyDetails";  // tablename
    private static final String id = "ID";  // auto generated ID column
    private static final String name = "name";  // column name
    private static final String email = "email"; // column name
    private static final String mobile = "mobile"; // column name
    private static final String databasename = "Emergency"; // Dtabasename
    private static final int versioncode = 1; //versioncode of the database

    public DBManager(Context context) {
        super(context, databasename, null, versioncode);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE IF NOT EXISTS " + tablename + "(" + id + " integer primary key, " + name + " text, " + email + " text, " + mobile + " text)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old,
                          int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS " + tablename;
        database.execSQL(query);
        onCreate(database);
    }

    public ArrayList<HashMap<String, String>> getAllData() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + tablename;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", cursor.getString(0));
                map.put("name", cursor.getString(1));
                map.put("email", cursor.getString(2));
                map.put("mobile", cursor.getString(3));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        // return contact list
        return wordList;
    }
}