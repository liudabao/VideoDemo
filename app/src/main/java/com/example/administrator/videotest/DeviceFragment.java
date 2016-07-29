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
import com.example.administrator.videotest.global.GlobalContext;

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
    private List<Device> list=new ArrayList<>();
    private ControlPoint point;
    private Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_device, container, false);
        }
        initData();
        initView();
        handler=new Handler(){

            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        adapter.notifyDataSetChanged();
                }
            }


        };
        return view;
    }

    private void initView(){
        recyclerView=(RecyclerView) view.findViewById(R.id.device_view);
        linearLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
        if(linearLayoutManager==null){
            Log.e("manage","null");
            return;
        }
        adapter=new DeviceAdapter(GlobalContext.getContext(),list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initData(){
        list.clear();
        point=new ControlPoint();
        point.addDeviceChangeListener(new DeviceChangeListener() {
            @Override
            public void deviceAdded(Device device) {
                Log.e("device", device.getFriendlyName());
                list.add(device);
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
