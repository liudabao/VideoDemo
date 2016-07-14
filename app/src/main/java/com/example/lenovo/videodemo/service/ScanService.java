package com.example.lenovo.videodemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ScanService extends Service {

    ScanBinder binder=new ScanBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    public class ScanBinder extends Binder {
        public ScanService getService(){
            return ScanService.this;
        }
    }

    public void scan(){
        Log.e("ScanService", "start scan");
        new ScanTask().execute();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("ScanService", "destroy");
    }
}
