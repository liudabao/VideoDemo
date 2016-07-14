package com.example.lenovo.videodemo.service;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.lenovo.videodemo.entity.Video;
import com.example.lenovo.videodemo.global.GlobalContext;
import com.example.lenovo.videodemo.util.DbManager;
import com.example.lenovo.videodemo.util.FileUtil;
import com.example.lenovo.videodemo.util.MediaUtil;
import com.example.lenovo.videodemo.util.VideoNameComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lenovo on 2016/7/14.
 */
public class ScanTask extends AsyncTask<Void, Integer, Boolean>{
    String time;
    String imageUrl;
    List<Video> list=new ArrayList<>();
    int num=10;
    int block;
    boolean isFinish=false;

    public ScanTask(){

    }


    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.e("ScanTask", "start scan");
        File file= Environment.getExternalStorageDirectory();
        FileUtil.getFile(file, time, imageUrl, list);
        Collections.sort(list, new VideoNameComparator());
       // for(int i=0;i<list.size();i++){
       //     Log.e("sort list", list.get(i).getName());
       // }
        MediaUtil.setNext(list);
        ScanThread[] scanThread=new ScanThread[num];
        block=list.size()/num;
        for(int i=0;i<num;i++){
            if(i==num-1){
                scanThread[i]=new ScanThread(list, i*block, (i+1)*block-1+list.size()%num, i+1);
            }
            else {
                scanThread[i]=new ScanThread(list, i*block, (i+1)*block-1, i+1);
            }
            scanThread[i].start();
        }
        while (!isFinish){
            isFinish=true;
            for(int i=0; i<num; i++){
                if(!scanThread[i].getFinish()){
                    isFinish=false;
                    break;
                }
            }
        }
        DbManager.insert(list);
        //for(int i=0;i<list.size();i++){
        //    Log.e("task", list.get(i).getName()+" "+list.get(i).getImageUrl());
        //}
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result){
        super.onPostExecute(result);
        Log.e("ScanTask", "scan finish");
        if(result){
            Intent intent=new Intent();
            intent.setAction("android.video.scan");
            GlobalContext.getContext().sendBroadcast(intent);
        }
    }


}
