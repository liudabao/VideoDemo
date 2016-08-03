package com.example.administrator.videotest;

import android.content.Intent;
import android.util.Log;

import com.example.administrator.videotest.global.GlobalContext;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;

/**
 * Created by lenovo on 2016/8/2.
 */
public class DlnaThread implements Runnable {

    private ControlPoint mControlPoint;
    private boolean mStartComplete=false;

    public DlnaThread(ControlPoint mControlPoint){
        this.mControlPoint=mControlPoint;
        mControlPoint.addDeviceChangeListener(deviceChangeListener);
    }

    @Override
    public void run() {
        if(mStartComplete){
            mControlPoint.search("ssdp:all");
            Log.e("TAG", "controlpoint search...");
        }
        else {
            mControlPoint.stop();
            boolean ret=mControlPoint.start();
            Log.e("TAG", "controlpoint search..." +ret);
            if(ret){
                mStartComplete=true;
            }
        }

    }

    private DeviceChangeListener deviceChangeListener=new DeviceChangeListener() {
        @Override
        public void deviceAdded(Device device) {
            Log.e("thread device add :", device.getFriendlyName()+" "+device.getLocation());
            DlnaUtil.getInstance().addDevice(device);
            Intent intent=new Intent("android.video.device");
            GlobalContext.getContext().sendBroadcast(intent);
            Service service=device.getService("urn:schemas-upnp-org:service:AVTransport:1");
            if(service==null){
                Log.e("service", "null");
            }
            else {

                Action action=service.getAction("SetAVTransportURI");
                if(action==null){
                    Log.e("action", "null");
                }
                else {
                    Log.e("action", "ok");

                }
            }
        }

        @Override
        public void deviceRemoved(Device device) {
            Log.e("thread device remove :", device.getFriendlyName());
            DlnaUtil.getInstance().removeDevice(device);
        }
    };

}
