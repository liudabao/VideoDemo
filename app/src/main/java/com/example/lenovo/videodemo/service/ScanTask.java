package com.example.lenovo.videodemo.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.lenovo.videodemo.entity.Video;
import com.example.lenovo.videodemo.util.FileUtil;
import com.example.lenovo.videodemo.util.MediaUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/7/14.
 */
public class ScanTask extends AsyncTask<Void, Integer, Boolean>{
    String time;
    String imageUrl;
    List<Video> list=new ArrayList<>();

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
        MediaUtil.setNext(list);

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result){
        super.onPostExecute(result);
    }


}
