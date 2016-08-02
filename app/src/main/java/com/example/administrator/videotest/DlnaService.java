package com.example.administrator.videotest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

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
        Log.e("service", "create");
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
        Log.e("service", "destroy");
        super.onDestroy();
    }

    private void init(){
        mControlPoint=new ControlPoint();
        dlnaThread=new DlnaThread(mControlPoint);
    }
}
