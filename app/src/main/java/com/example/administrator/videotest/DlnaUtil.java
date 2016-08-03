package com.example.administrator.videotest;

import android.util.Log;

import com.example.administrator.videotest.entity.ShareDevice;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/8/2.
 */
public class DlnaUtil {
    private List<ShareDevice> mDevices;
    private Device mSelectedDevice;
    private static final DlnaUtil mDlnaUtil = new DlnaUtil();
    private static final String MEDIARENDER = "urn:schemas-upnp-org:device:MediaRenderer:1";

    private DlnaUtil() {
        mDevices = new ArrayList<ShareDevice>();
    }

    public static DlnaUtil getInstance() {
        return mDlnaUtil;
    }

    public static boolean isMediaRenderDevice(Device device) {
        if (device != null
                && MEDIARENDER.equalsIgnoreCase(device.getDeviceType())) {
            return true;
        }

        return false;
    }

    public synchronized void addDevice(Device d) {
        //if (!DLNAUtil.isMediaRenderDevice(d)) {
        //     return;
        // }
        int size = mDevices.size();
        for (int i = 0; i < size; i++) {
            String udnString = mDevices.get(i).getDevice().getUDN();
            if (d.getUDN().equalsIgnoreCase(udnString)) {
                return;
            }
        }
        ShareDevice shareDevice=new ShareDevice();
        shareDevice.setDevice(d);
        shareDevice.setSelect(false);
        mDevices.add(shareDevice);
        //mDevices.add(d);
        //Log.e("TAG", "Devices add a device" + d.getDeviceType());
        Log.e("TAG", "Devices add a device" + d.getLocation());
    }

    public synchronized void removeDevice(Device d) {
        //if (!DLNAUtil.isMediaRenderDevice(d)) {
       //     return;
       // }
        int size = mDevices.size();
        for (int i = 0; i < size; i++) {
            String udnString = mDevices.get(i).getDevice().getUDN();
            if (d.getUDN().equalsIgnoreCase(udnString)) {
                Device device = mDevices.remove(i).getDevice();
                Log.e("TAG", "Devices remove a device");

                boolean ret = false;
                if (mSelectedDevice != null) {
                    ret = mSelectedDevice.getUDN().equalsIgnoreCase(
                            device.getUDN());
                }
                if (ret) {
                    mSelectedDevice = null;
                }
                break;
            }
        }
    }

    public List<ShareDevice> getDevices() {
        return mDevices;
    }

    public Device getSelectedDevice() {
        for(ShareDevice shareDevice:mDevices){
            if(shareDevice.getSelect()){
                mSelectedDevice=shareDevice.getDevice();
                Log.e("device select", mSelectedDevice.getFriendlyName());
                break;
            }
        }

        return mSelectedDevice;
    }

}
