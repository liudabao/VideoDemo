package com.example.administrator.videotest.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;


import com.example.administrator.videotest.entity.Video;
import com.example.administrator.videotest.global.GlobalContext;
import com.example.administrator.videotest.util.DbManager;
import com.example.administrator.videotest.util.FileUtil;
import com.example.administrator.videotest.util.ImageUtil;
import com.example.administrator.videotest.util.MediaUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by lenovo on 2016/7/14.
 */
public class ScanThread extends Thread{
    List<Video> list;
    int start;
    int end;
    int id;
    boolean isFinish=false;

    public ScanThread(List<Video> list, int start, int end, int id){
        this.list=list;
        this.start=start;
        this.end=end;
        this.id=id;
    }

    public void run(){
        //Log.e("Thread "+id, "start");
        for(int i=start;i<=end;i++){
            Video video=list.get(i);
            File f=new File(video.getUrl());
            Bitmap bitmap= ImageUtil.getImage(f, video.getPosition());
            String time= MediaUtil.getMediaTime(f);
            String name=video.getName();
            String imageUrl=null;
            try {
                imageUrl= ImageUtil.saveBitmap(name, bitmap);
                Log.e("vedio local", f.getPath()+": "+name+" "+ FileUtil.formetFileSize(FileUtil.getFileSizes(f)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            video.setTime(time);
            video.setImageUrl(imageUrl);
            DbManager.insert(video);
            Intent intent=new Intent();
            intent.setAction("android.video.delete");
            GlobalContext.getContext().sendBroadcast(intent);
        }
        isFinish=true;
       // Log.e("Thread "+id, "end");
    }

    public boolean getFinish(){
        return isFinish;
    }


}
