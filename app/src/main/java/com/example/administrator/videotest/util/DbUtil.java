package com.example.administrator.videotest.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.administrator.videotest.entity.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
public class DbUtil {
    SqliteHelper helper;
    SQLiteDatabase db;
    public DbUtil(){
        helper=SqliteHelper.getHelper();
        db=helper.getWritableDatabase();
    }
    public void insert(List<Video> list, String table){
        synchronized (helper){
            if (!db.isOpen()) {
                db = helper.getWritableDatabase();
            }
            db.beginTransaction();
            try{
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
                        values.put("selected", video.getSelected());
                        values.put("imageUrl", video.getImageUrl());
                        db.insert(table, null, values);
                        Log.e("db insert", values.get("selected")+"");
                    }
                }
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
                db.close();
            }
        }

    }

    public void insert(Video video, String table){
        synchronized (helper){
            if (!db.isOpen()) {
                db = helper.getWritableDatabase();
            }
            db.beginTransaction();
            try{
                ContentValues values=new ContentValues();
                values.put("name", video.getName());
                values.put("size", video.getSize());
                values.put("time", video.getTime());
                values.put("url", video.getUrl());
                values.put("nextUrl", video.getNextUrl());
                values.put("prevUrl", video.getPrevUrl());
                values.put("position", video.getPosition());
                values.put("selected", video.getSelected());
                values.put("imageUrl", video.getImageUrl());
                db.insert(table, null, values);
                //Log.e("db insert", values.get("selected")+"");
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
                db.close();
            }

        }

    }

    public void update( Video video, String table) {
        synchronized (helper){
            if (!db.isOpen()) {
                db = helper.getWritableDatabase();
            }
            db.beginTransaction();
            try{
                ContentValues values=new ContentValues();
                values.put("name", video.getName());
                values.put("size", video.getSize());
                values.put("time", video.getTime());
                values.put("url", video.getUrl());
                values.put("nextUrl", video.getNextUrl());
                values.put("prevUrl", video.getPrevUrl());
                values.put("position", video.getPosition());
                values.put("selected", video.getSelected());
                values.put("imageUrl", video.getImageUrl());
                db.update(table, values, "name=?", new String[]{video.getName()});
               // Log.e("update1", video.getName()+" * "+video.getSize()+" * "+video.getTime()+" * "+video.getUrl()+" * "+video.getNextUrl());
                Log.e("update", video.getName()+" * "+video.getSelected()+" * "+video.getPosition());
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
                db.close();
            }


        }

    }

    public void deleteAll(String table) {
        synchronized (helper){
            if (!db.isOpen()) {
                db = helper.getWritableDatabase();
            }
            db.beginTransaction();
            try{
                Cursor cursor=db.query(table, null, null, null, null, null, null);
                if(cursor.moveToFirst()){
                    do{
                        db.delete(table, "name=?", new String[]{cursor.getString(cursor.getColumnIndex("name"))});
                    }while(cursor.moveToNext());
                }
                cursor.close();
                db.setTransactionSuccessful();
            }catch (Exception e){

                e.printStackTrace();
            }finally {
                db.endTransaction();
                db.close();
            }

        }

    }

    public void delete(String name,String table) {
        synchronized (helper){
            if (!db.isOpen()) {
                db = helper.getWritableDatabase();
            }
            db.beginTransaction();
            try{
                db.delete(table, "name=?", new String[]{name});
                db.setTransactionSuccessful();
            }catch (Exception e){

                e.printStackTrace();
            }finally {
                db.endTransaction();
                db.close();
            }

        }

    }

    public List<Video> queryAll(String table){

        List<Video> list=new ArrayList<>();
        synchronized (helper){
            if (!db.isOpen()) {
                db = helper.getWritableDatabase();
            }
            db.beginTransaction();
            try{
                Cursor cursor=db.query(table, null, null, null, null, null, null);
                Log.e("query all", "start");
                if(cursor.moveToFirst()){
                    do{
                       // Log.e("query all", cursor.getString(cursor.getColumnIndex("selected")));
                        Video video=new Video();
                        video.setName(cursor.getString(cursor.getColumnIndex("name")));
                        video.setSize(cursor.getString(cursor.getColumnIndex("size")));
                        video.setTime(cursor.getString(cursor.getColumnIndex("time")));
                        video.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                        video.setNextUrl(cursor.getString(cursor.getColumnIndex("nextUrl")));
                        video.setPrevUrl(cursor.getString(cursor.getColumnIndex("prevUrl")));
                        video.setPosition(cursor.getInt(cursor.getColumnIndex("position")));
                        video.setSelected(cursor.getString(cursor.getColumnIndex("selected")));
                        video.setImageUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
                        if(video.getSelected().equals("false")){
                            //Log.e("query all", "add "+video.getName());
                            list.add(video);
                        }
                        //list.add(video);
                    }while(cursor.moveToNext());
                }
                cursor.close();
                db.setTransactionSuccessful();
                Log.e("query all", "end");
            }catch (Exception e){
                Log.e("query all", "error");
                e.printStackTrace();
            }finally {

                db.endTransaction();
                db.close();
            }

        }

        return list;
    }

    public Video queryByUrl(String table, String nextUrl){
        Video video=new Video();
        synchronized (helper){
            if (!db.isOpen()) {
                db = helper.getWritableDatabase();
            }
            db.beginTransaction();
            try{
                Cursor cursor=db.query(table, null, null, null, null, null, null);
                if(cursor.moveToFirst()){
                    do{
                        //Log.e("query", name);
                         //Log.e("query cursor", nextUrl+" &&&  "+cursor.getString(cursor.getColumnIndex("nextUrl")));
                        if(nextUrl.equals(cursor.getString(cursor.getColumnIndex("url")))){
                            Log.e("query video", cursor.getString(cursor.getColumnIndex("name"))+" "+cursor.getInt(cursor.getColumnIndex("position")));
                            video.setName(cursor.getString(cursor.getColumnIndex("name")));
                            video.setSize(cursor.getString(cursor.getColumnIndex("size")));
                            video.setTime(cursor.getString(cursor.getColumnIndex("time")));
                            video.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                            video.setNextUrl(cursor.getString(cursor.getColumnIndex("nextUrl")));
                            video.setPrevUrl(cursor.getString(cursor.getColumnIndex("prevUrl")));
                            video.setPosition(cursor.getInt(cursor.getColumnIndex("position")));
                            video.setSelected(cursor.getString(cursor.getColumnIndex("selected")));
                            video.setImageUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
                            break;
                        }

                    }while(cursor.moveToNext());
                }
                cursor.close();
                //db.rawQuery("select * from person",null);
                db.setTransactionSuccessful();
            }catch (Exception e){

            }finally {
                db.endTransaction();
                db.close();
            }
        }

        return video;
    }

    public Video queryByName(String table, String name){
        Video video=new Video();
        synchronized (helper){
            if (!db.isOpen()) {
                db = helper.getWritableDatabase();
            }
            db.beginTransaction();
            try{
                Cursor cursor=db.query(table, null, null, null, null, null, null);
                if(cursor.moveToFirst()){
                    do{
                        //Log.e("query", name);
                       // Log.e("query cursor", nextUrl+" &&&  "+cursor.getString(cursor.getColumnIndex("nextUrl")));
                        if(name.equals(cursor.getString(cursor.getColumnIndex("name")))){
                            Log.e("query video", cursor.getString(cursor.getColumnIndex("name"))+" "+cursor.getInt(cursor.getColumnIndex("position")));
                            video.setName(cursor.getString(cursor.getColumnIndex("name")));
                            video.setSize(cursor.getString(cursor.getColumnIndex("size")));
                            video.setTime(cursor.getString(cursor.getColumnIndex("time")));
                            video.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                            video.setNextUrl(cursor.getString(cursor.getColumnIndex("nextUrl")));
                            video.setPrevUrl(cursor.getString(cursor.getColumnIndex("prevUrl")));
                            video.setPosition(cursor.getInt(cursor.getColumnIndex("position")));
                            video.setSelected(cursor.getString(cursor.getColumnIndex("selected")));
                            video.setImageUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
                            break;
                        }

                    }while(cursor.moveToNext());
                }
                cursor.close();
                db.setTransactionSuccessful();
            }catch (Exception e){

            }finally {
                db.endTransaction();
                db.close();
            }
        }
        return video;
    }

    public boolean isExist(String table,String name){
        synchronized (helper){
            if (!db.isOpen()) {
                db = helper.getWritableDatabase();
            }
            db.beginTransaction();
            try{
               // Log.e("query name","query name start");
                Cursor cursor=db.query(table, null, null, null, null, null, null);
                if(cursor.moveToFirst()){
                   // Log.e("query name", name+" * "+cursor.getString(cursor.getColumnIndex("name")));
                    do{

                        if(name.equals(cursor.getString(cursor.getColumnIndex("name")))){
                            return true;
                        }

                    }while(cursor.moveToNext());
                }
                cursor.close();
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
                db.close();
            }
            return false;
        }

    }
}
