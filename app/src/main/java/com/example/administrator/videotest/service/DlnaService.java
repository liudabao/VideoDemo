package com.example.administrator.videotest.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
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
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Log.e("TAG", wm.getConnectionInfo() + "");
        WifiManager.MulticastLock multicastLock = wm.createMulticastLock("multicastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();
        mControlPoint=new ControlPoint();
        dlnaThread=new DlnaThread(mControlPoint);

    }
}
