package com.example.administrator.videotest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cybergarage.upnp.Device;

import java.util.List;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private  List<Device> mDevices;
    private Context context;

    public DeviceAdapter(Context context,List<Device> mDevices) {
        this.context=context;
        this.mDevices=mDevices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Device device=mDevices.get(position);
        holder.mView.setText(device.getFriendlyName().toString());
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mView;


        public ViewHolder(View view) {
            super(view);
            mView = (TextView) view.findViewById(R.id.device_name);
        }

    }
}
