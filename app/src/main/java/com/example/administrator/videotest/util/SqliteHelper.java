package com.example.administrator.videotest.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.administrator.videotest.global.GlobalContext;
import com.example.administrator.videotest.global.GlobalValue;


/**
 * Created by Administrator on 2016/7/5.
 */
public class SqliteHelper extends SQLiteOpenHelper {

    public static String CREATE_VIDEO="create table video (id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar, size varchar, " +
            "time varchar,url varchar, nextUrl varchar, prevUrl varchar, position integer,selected boolean , imageUrl varchar)";
    private Context context;

    public static SqliteHelper helper;

    public synchronized static SqliteHelper getHelper(){
        // helper=new SqliteHelper(context, db, null, version);
        if(helper==null){
            helper=new SqliteHelper(GlobalContext.getContext(), GlobalValue.DB, null, 1);
        }
        return helper;
    }

    private SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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
