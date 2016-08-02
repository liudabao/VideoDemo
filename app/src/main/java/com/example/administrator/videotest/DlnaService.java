package com.example.administrator.videotest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.cybergarage.upnp.ControlPoint;

public class DlnaService extends Service {

    private ControlPoint mControlPoint;
    private DlnaThread dlnaThread;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(dlnaThread).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void init(){
        mControlPoint=new ControlPoint();
        dlnaThread=new DlnaThread(mControlPoint);
    }
}
