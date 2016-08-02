package com.example.administrator.videotest;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.videotest.entity.ShareDevice;
import com.example.administrator.videotest.listener.OnRecyclerViewItemClickListener;

import org.cybergarage.upnp.Device;

import java.util.List;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private  List<ShareDevice> mDevices;
    private Context context;
    private OnRecyclerViewItemClickListener mListener;

    public DeviceAdapter(Context context,List<ShareDevice> mDevices) {
        this.context=context;
        this.mDevices=mDevices;
    }

    public void setOnItemClickLitener(OnRecyclerViewItemClickListener mOnItemClickLitener)
    {
        this.mListener = mOnItemClickLitener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Device device=mDevices.get(position).getDevice();
        holder.textView.setText(device.getFriendlyName().toString());
        if(mDevices.get(position).getSelect()){
            holder.imageView.setBackgroundResource(R.drawable.choose);
        }
        else {
            holder.imageView.setBackgroundResource(0);
        }

        Log.e("device list name", device.getFriendlyName());
        if(mListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = holder.getLayoutPosition();
                    Log.e("device list", pos+"");
                    mListener.onItemClick(holder.itemView,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mListener.onItemLongClick(holder.itemView,pos);
                    return false;
                }
            });
        }
        else {
            Log.e("listener", "null");
        }
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.device_name);
            imageView=(ImageView)view.findViewById(R.id.device_select);
        }

    }
}
