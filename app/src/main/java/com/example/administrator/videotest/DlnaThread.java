package com.example.administrator.videotest;

import android.content.Intent;
import android.util.Log;

import com.example.administrator.videotest.global.GlobalContext;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;

/**
 * Created by lenovo on 2016/8/2.
 */
public class DlnaThread implements Runnable {

    private ControlPoint mControlPoint;

    public DlnaThread(ControlPoint mControlPoint){
        this.mControlPoint=mControlPoint;
        mControlPoint.addDeviceChangeListener(deviceChangeListener);
    }

    @Override
    public void run() {
        mControlPoint.start();
    }

    private DeviceChangeListener deviceChangeListener=new DeviceChangeListener() {
        @Override
        public void deviceAdded(Device device) {
            Log.e("thread device add :", device.getFriendlyName());
            DlnaUtil.getInstance().addDevice(device);
            Intent intent=new Intent("android.video.device");
            GlobalContext.getContext().sendBroadcast(intent);
        }

        @Override
        public void deviceRemoved(Device device) {
            Log.e("thread device remove :", device.getFriendlyName());
            DlnaUtil.getInstance().removeDevice(device);
        }
    };
}
