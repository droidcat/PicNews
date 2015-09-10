package com.droidroid.imageloaderdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Droidroid on 2015/8/19.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String CREATE_TABLE_NEWS = "create table news(" +
            "id integer primary key autoincrement," +
            "title text," +
            "url text," +
            "thumbnail text," +
            "updatetime text)";
    public static final String CREATE_TABLE_SLIDE = "create table slide(" +
            "id integer primary key autoincrement," +
            "url text," +
            "image text," +
            "description text)";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NEWS);
        db.execSQL(CREATE_TABLE_SLIDE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_TABLE_SLIDE);
    }
}
