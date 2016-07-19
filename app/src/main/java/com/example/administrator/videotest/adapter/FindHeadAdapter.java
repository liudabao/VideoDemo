package com.example.administrator.videotest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.administrator.videotest.R;
import com.example.administrator.videotest.listener.OnRecyclerViewItemClickListener;

import java.io.File;
import java.util.List;

/**
 * Created by lenovo on 2016/7/12.
 */
public class FindHeadAdapter extends RecyclerView.Adapter<FindHeadAdapter.HeadHolder>{

    Context context;
    List<String> list;
    private OnRecyclerViewItemClickListener mListener;

    public FindHeadAdapter(Context context, List<String> list){
        this.context=context;
        this.list=list;
    }

    public void setOnItemClickLitener(OnRecyclerViewItemClickListener mOnItemClickLitener)
    {
        this.mListener = mOnItemClickLitener;
    }

    @Override
    public HeadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.find_head_layout, parent, false);
        return new HeadHolder(view);
    }

    @Override
    public void onBindViewHolder(final HeadHolder holder, int position) {

        String path=list.get(position);

        File file=new File(path);
        Log.e("head", path+" "+file.getParent());
        holder.imageView.setBackgroundResource(R.drawable.find_next);
        holder.textView.setText(file.getName());
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
       // Log.e("head", list.size()+"");
        return list.size();
    }

    class HeadHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView textView;

        public HeadHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View view) {
            imageView=(ImageView)view.findViewById(R.id.head_image);
            textView=(TextView)view.findViewById(R.id.head_text);

        }
    }

}
