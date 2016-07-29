package com.example.administrator.videotest.entity;

import org.cybergarage.upnp.Device;

/**
 * Created by liumin on 2016/7/29.
 */
public class ShareDevice {

    private Device device;
    private Boolean isSelect;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Boolean getSelect() {
        return isSelect;
    }

    public void setSelect(Boolean select) {
        isSelect = select;
    }
}
