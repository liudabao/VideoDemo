package com.example.administrator.videotest.util;

import android.util.Log;

import com.example.administrator.videotest.adapter.VideoNameComparator;
import com.example.administrator.videotest.entity.Video;
import com.example.administrator.videotest.global.GlobalValue;

import java.io.File;
import java.util.Collections;
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
        List<Video> list=dbUtil.queryAll(GlobalValue.TABLE);
        for(int i=0; i< list.size(); i++){
            Video video=list.get(i);
            File file=new File(video.getUrl());
            if(!file.exists()){
                Log.e("TAG", file.getName());
                list.remove(video);
                dbUtil.delete(video.getName(), GlobalValue.TABLE);
            }
        }
        Collections.sort(list, new VideoNameComparator());
        MediaUtil.setNext(list);
        for(int i=0; i< list.size(); i++){
            dbUtil.update(list.get(i), GlobalValue.TABLE);
        }
        ;
        return list;
       // Log.e("dbmanage query all", list.size()+"");
    }

    public static  List<Video> queryAll(){
        DbUtil dbUtil=new DbUtil();
        List<Video> list=dbUtil.queryAll(GlobalValue.TABLE);
        Collections.sort(list, new VideoNameComparator());
        return list;

    }

}
