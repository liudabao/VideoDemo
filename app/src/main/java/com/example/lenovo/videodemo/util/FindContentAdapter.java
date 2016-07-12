package com.example.lenovo.videodemo.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.videodemo.R;
import com.example.lenovo.videodemo.entity.Find;
import com.example.lenovo.videodemo.global.GlobalValue;
import com.example.lenovo.videodemo.listener.OnRecyclerViewItemClickListener;

import java.util.List;

/**
 * Created by lenovo on 2016/7/12.
 */
public class FindContentAdapter extends RecyclerView.Adapter<FindContentAdapter.ContentHolder>{

    Context context;
    List<Find> list;
    private OnRecyclerViewItemClickListener mListener;

    public FindContentAdapter(Context context, List<Find> list){
        this.context=context;
        this.list=list;
    }

    public void setOnItemClickLitener(OnRecyclerViewItemClickListener mOnItemClickLitener)
    {
        this.mListener = mOnItemClickLitener;
    }

    @Override
    public ContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.find_content_layout, parent, false);
        return new ContentHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContentHolder holder, int position) {

        Find find=list.get(position);
        if(find.getType()== GlobalValue.FILE_TYPE_DIRECTORY){
            holder.imageView.setBackgroundResource(R.drawable.direcotry);
        }
        else if(find.getType()== GlobalValue.FILE_TYPE_MOVE){
            holder.imageView.setBackgroundResource(R.drawable.movie);
        }
        holder.textView.setText(find.getName());
        Log.e("find", find.getUrl());
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
    public int getItemCount() {
        return list.size();
    }

    class ContentHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView textView;

        public ContentHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View view) {
            imageView=(ImageView)view.findViewById(R.id.content_image);
            textView=(TextView)view.findViewById(R.id.content_text);

        }
    }

}
