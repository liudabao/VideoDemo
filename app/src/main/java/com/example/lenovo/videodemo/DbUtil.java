package com.example.lenovo.videodemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public class DbUtil {
    private SqliteHelper helper;

    public DbUtil(Context context, String db, int version){
        helper=new SqliteHelper(context, db, null, version);
    }

    public void insert(List<Video> list, String table){
        SQLiteDatabase db=helper.getWritableDatabase();
        db.beginTransaction();
        if(list.size()>0){
            for(Video video:list){
                ContentValues values=new ContentValues();
                values.put("name", video.getName());
                values.put("size", video.getSize());
                values.put("time", video.getTime());
                values.put("url", video.getUrl());
                values.put("nextUrl", video.getNextUrl());
                values.put("prevUrl", video.getPrevUrl());
                values.put("position", video.getPosition());
                db.insert(table, null, values);
                Log.e("db insert", values.get("nextUrl")+"");
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void update( String id, ContentValues values, String table) {
        SQLiteDatabase db=helper.getWritableDatabase();
        db.beginTransaction();
        db.update(table, values, "id=?", new String[]{id});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void deleteAll(String table) {
        SQLiteDatabase db=helper.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor=db.query(table, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                db.delete(table, "id=?", new String[]{cursor.getString(cursor.getColumnIndex("id"))});
            }while(cursor.moveToNext());
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void delete(String id,String table) {
        SQLiteDatabase db=helper.getWritableDatabase();
        db.beginTransaction();
        db.delete(table, "id=?", new String[]{id});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<Video> queryAll(String table){
        List<Video> list=new ArrayList<>();
        SQLiteDatabase db=helper.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor=db.query(table, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Log.e("query nextUrl", cursor.getString(cursor.getColumnIndex("nextUrl")));
                Video video=new Video();
                video.setName(cursor.getString(cursor.getColumnIndex("name")));
                video.setSize(cursor.getString(cursor.getColumnIndex("size")));
                video.setTime(cursor.getString(cursor.getColumnIndex("time")));
                video.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                video.setNextUrl(cursor.getString(cursor.getColumnIndex("nextUrl")));
                video.setPrevUrl(cursor.getString(cursor.getColumnIndex("prevUrl")));
                video.setPosition(cursor.getLong(cursor.getColumnIndex("position")));
                list.add(video);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return list;
    }

    public Video queryByUrl(String table, String nextUrl){
        Video video=new Video();
        SQLiteDatabase db=helper.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor=db.query(table, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                //Log.e("query", name);
               // Log.e("query cursor", nextUrl+" &&&  "+cursor.getString(cursor.getColumnIndex("nextUrl")));
                if(nextUrl.equals(cursor.getString(cursor.getColumnIndex("url")))){
                    Log.e("query video", cursor.getString(cursor.getColumnIndex("name")));
                    video.setName(cursor.getString(cursor.getColumnIndex("name")));
                    video.setSize(cursor.getString(cursor.getColumnIndex("size")));
                    video.setTime(cursor.getString(cursor.getColumnIndex("time")));
                    video.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                    video.setNextUrl(cursor.getString(cursor.getColumnIndex("nextUrl")));
                    video.setPrevUrl(cursor.getString(cursor.getColumnIndex("prevUrl")));
                    video.setPosition(cursor.getLong(cursor.getColumnIndex("position")));
                    break;
                }

            }while(cursor.moveToNext());
        }
        cursor.close();
        //db.rawQuery("select * from person",null);
        db.setTransactionSuccessful();
        db.endTransaction();
        return video;
    }


    public boolean isExist(String table){
        SQLiteDatabase db=helper.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor=db.query(table, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                return true;

            }while(cursor.moveToNext());
        }
        cursor.close();
        //db.rawQuery("select * from person",null);
        db.setTransactionSuccessful();
        db.endTransaction();
        return false;
    }
}
