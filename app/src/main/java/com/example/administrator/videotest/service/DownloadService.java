package com.example.administrator.videotest.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;


import com.example.administrator.videotest.R;
import com.example.administrator.videotest.global.GlobalContext;
import com.example.administrator.videotest.global.GlobalValue;
import com.example.administrator.videotest.listener.OnProgressListener;

import java.io.File;

public class DownloadService extends Service {


    DownloadBinder binder=new DownloadBinder();
    private DownloadTask task;
    private ProgressDialog progress;
    private int percent;
    private OnProgressListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    public class DownloadBinder extends Binder {
        public DownloadService getService(){
            return DownloadService.this;
        }
    }

    public void download(){
        Log.e("download", "start");
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"test.apk");
        showProgressDialog();
        task=new DownloadTask(GlobalValue.url, 3, file, progress);
       // task=new DownloadTask(GlobalValue.url, 3, file);
        task.execute();

    }

    private void showProgressDialog(){

        progress=new ProgressDialog(GlobalContext.getContext());
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIcon(R.drawable.download);
        progress.setTitle("下载");
        progress.setMessage("版本更新");
        progress.setCancelable(true);
        progress.setMax(100);
        progress.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        progress.show();

    }

    public void setOnProgressListener(OnProgressListener listener){
        this.listener=listener;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // task.cancel(true);
    }
}
