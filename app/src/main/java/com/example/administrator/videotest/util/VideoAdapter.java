package com.example.administrator.videotest.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.videotest.R;
import com.example.administrator.videotest.entity.Video;

import java.util.List;

/**
 * Created by lenovo on 2016/4/14.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyRecyclerViewHolder>{

    private List<Video> list;
    private Context context;


    private OnRecyclerViewItemClickListener mListener;

    public interface OnRecyclerViewItemClickListener {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }

    public void setOnItemClickLitener(OnRecyclerViewItemClickListener mOnItemClickLitener)
    {
        this.mListener = mOnItemClickLitener;
    }


    public VideoAdapter(Context context, List<Video> list){
        this.context=context;
        this.list=list;
       // Log.e("adapter",context+"");
    }


    @Override
    public void onBindViewHolder(final MyRecyclerViewHolder holder, int position) {
        // TODO Auto-generated method stub
        Video video=list.get(position);
        holder.tv_name.setText(video.getName());
        if(video.getName().length()>50){
            holder.tv_name.setText(video.getName().substring(0,50)+".....");
        }
        holder.tv_size.setText(video.getSize());
        holder.tv_time.setText(video.getTime());
        holder.iv.setImageBitmap(BitmapFactory.decodeFile(video.getImageUrl()));
        holder.tv_play_time.setText("看到 "+ MediaUtil.getShowTime(video.getPosition()));
        if(video.getPosition()==0){
            holder.tv_play_time.setVisibility(View.GONE);
        }
        else {
            holder.tv_play_time.setVisibility(View.VISIBLE);
        }
       // Log.e("adapter imagurl", video.getImageUrl());
       // holder.iv.setImageBitmap(video.getBitmap());
       // holder.iv.setBackgroundResource(R.mipmap.ic_launcher);
       //
        if(mListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
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


    }

    @Override
    public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO Auto-generated method stub
      //  CountryRecyclerViewHolder holder=new CountryRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.ui_recyclerview_item, parent,false));
       // return holder;
        if(context!=null){
           // Log.e("view", "start");
            View mView = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false);
            return new MyRecyclerViewHolder(mView);
        }
        else {
            Log.e("view", "context is null");
        }
        return null;

    }


    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv_name;
        TextView tv_size;
        TextView tv_time;
        TextView tv_play_time;
        public MyRecyclerViewHolder(View view){
            super(view);
            init(view);
        }

        private void init(View view) {
            iv=(ImageView)view.findViewById(R.id.image);
            tv_name=(TextView)view.findViewById(R.id.name);
            tv_size=(TextView)view.findViewById(R.id.size);
            tv_time=(TextView)view.findViewById(R.id.time);
            tv_play_time=(TextView)view.findViewById(R.id.play_time);
        }

    }

}
