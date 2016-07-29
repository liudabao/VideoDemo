package com.example.administrator.videotest.util;

import android.util.Log;

import com.example.administrator.videotest.entity.Video;
import com.example.administrator.videotest.global.GlobalValue;

import java.util.List;

/**
 * Created by lenovo on 2016/7/14.
 */
public class DbManager {

    public static  void insert(List<Video> list){

        DbUtil dbUtil=new DbUtil();
        for(Video video:list){
            if(!dbUtil.isExist(GlobalValue.TABLE, video.getName())){
                Log.e("video insert","insert");
                dbUtil.insert(video, GlobalValue.TABLE);
            }
            else {
                Log.e("video insert","update");
                dbUtil.update(video, GlobalValue.TABLE);
            }
        }
        //dbUtil.insert(list, GlobalValue.TABLE);
    }

    public static  void insert(Video video){

        DbUtil dbUtil=new DbUtil();
        if(!dbUtil.isExist(GlobalValue.TABLE, video.getName())){
            Log.e("video insert","insert");
            dbUtil.insert(video, GlobalValue.TABLE);
        }
        else {
            Log.e("video insert","update");
            dbUtil.update(video, GlobalValue.TABLE);
        }
        //dbUtil.insert(list, GlobalValue.TABLE);
    }

    public static  List<Video> query(){
        DbUtil dbUtil=new DbUtil();
        return dbUtil.queryAll(GlobalValue.TABLE);
       // Log.e("dbmanage query all", list.size()+"");
    }

}
