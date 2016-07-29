package com.example.administrator.videotest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.videotest.dummy.DummyContent;
import com.example.administrator.videotest.entity.ShareDevice;
import com.example.administrator.videotest.global.GlobalContext;
import com.example.administrator.videotest.listener.OnRecyclerViewItemClickListener;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;

import java.util.ArrayList;
import java.util.List;


public class DeviceFragment extends Fragment {

    private DeviceAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private View view;
    private List<ShareDevice> list=new ArrayList<>();
    private ControlPoint point;
    private Handler handler;
    private boolean isLoad;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null){
            isLoad=false;
            view = inflater.inflate(R.layout.fragment_device, container, false);
        }
        else{
            isLoad=true;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null)
        {
            parent.removeView(view);
        }
        if(!isLoad){
            initView();
            initData();
        }

        handler=new Handler(){

            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Log.e("device refreash", list.size()+"");
                        adapter=new DeviceAdapter(GlobalContext.getContext(),list);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                }
            }


        };
        return view;
    }

    private void initView(){
        recyclerView=(RecyclerView) view.findViewById(R.id.device_view);
        linearLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
        adapter=new DeviceAdapter(GlobalContext.getContext(),list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        point=new ControlPoint();
        adapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShareDevice shareDevice=list.get(position);
                Log.e("device list", shareDevice.getDevice().getFriendlyName()+"");
                if (shareDevice.getSelect()) {
                    shareDevice.setSelect(false);
                }
                else {
                    shareDevice.setSelect(true);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void initData(){
        list.clear();

        point.addDeviceChangeListener(new DeviceChangeListener() {
            @Override
            public void deviceAdded(Device device) {
                Log.e("device", device.getFriendlyName());
                ShareDevice shareDevice=new ShareDevice();
                shareDevice.setDevice(device);
                shareDevice.setSelect(false);
                list.add(shareDevice);
                Message msg=new Message();
                msg.what=0;
                handler.sendMessage(msg);

            }

            @Override
            public void deviceRemoved(Device device) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                point.start();

            }
        }).start();
    }



}
