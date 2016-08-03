package com.example.administrator.videotest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.administrator.videotest.adapter.DeviceAdapter;
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
    private IntentFilter intentFilter;
    private DeviceReceiver deviceReceiver;
    private ImageButton refreash;

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
            initEvent();
        }

        handler=new Handler(){

            public void handleMessage(Message msg){
                switch (msg.what){
                    case 0:
                        Log.e("device refreash", list.size()+"");
                        adapter=new DeviceAdapter(GlobalContext.getContext(),list);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        initEvent();
                }
            }


        };
        return view;
    }

    private void initView(){
        refreash=(ImageButton)view.findViewById(R.id.share_refreash);
        recyclerView=(RecyclerView) view.findViewById(R.id.device_view);
        linearLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
        adapter=new DeviceAdapter(GlobalContext.getContext(),list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
       // point=new ControlPoint();
        intentFilter=new IntentFilter();
        deviceReceiver=new DeviceReceiver();
        intentFilter.addAction("android.video.device");
        getActivity().registerReceiver(deviceReceiver, intentFilter);

    }

    private void initData(){
        list.clear();
        start();

        /*point.addDeviceChangeListener(new DeviceChangeListener() {
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
        }).start();*/
    }


    private void initEvent(){
        adapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShareDevice shareDevice=list.get(position);
                Log.e("device list", shareDevice.getDevice().getFriendlyName()+"");
                if (shareDevice.getSelect()) {
                    shareDevice.setSelect(false);
                }
                else {
                    for(ShareDevice device:list){
                        device.setSelect(false);
                    }
                    shareDevice.setSelect(true);
                   // DlnaUtil.getInstance().setSelectedDevice(shareDevice.getDevice());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        refreash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop();
                initData();
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().unregisterReceiver(deviceReceiver);
    }

    class DeviceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg=new Message();
            msg.what=0;
            handler.sendMessage(msg);
            list=DlnaUtil.getInstance().getDevices();
        }
    }


    private void start(){
        Intent intent=new Intent(getActivity(), DlnaService.class);
        GlobalContext.getContext().startService(intent);
    }

    private void stop(){
        Intent intent=new Intent(getActivity(), DlnaService.class);
        GlobalContext.getContext().stopService(intent);
    }
}
