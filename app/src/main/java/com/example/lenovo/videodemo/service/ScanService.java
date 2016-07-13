package com.example.lenovo.videodemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ScanService extends Service {

    ScanBinder binder=new ScanBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    class ScanBinder extends Binder {
        public ScanService getService(){
            return ScanService.this;
        }
    }
}
