package com.example.lenovo.videodemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/7/5.
 */
public class SqliteHelper extends SQLiteOpenHelper {

    public static String CREATE_VIDEO="create table video (name varchar primary key, size varchar, " +
            "time varchar,url varchar, nextUrl varchar, prevUrl varchar, long position )";
    private Context context;

    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_VIDEO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
