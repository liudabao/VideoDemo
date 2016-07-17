package com.example.lenovo.videodemo.listener;

import android.view.View;

/**
 * Created by lenovo on 2016/7/12.
 */
public interface OnRecyclerViewItemClickListener {

    public void onItemClick(View view, int position);
    public void onItemLongClick(View view, int position);

}
