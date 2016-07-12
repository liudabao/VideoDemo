package com.example.lenovo.videodemo.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lenovo on 2016/7/12.
 */
public class FindHeadAdapter extends RecyclerView.Adapter<FindHeadAdapter.HeadHolder>{

    @Override
    public HeadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(HeadHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class HeadHolder extends RecyclerView.ViewHolder
    {

        public HeadHolder(View itemView) {
            super(itemView);
        }
    }

}
