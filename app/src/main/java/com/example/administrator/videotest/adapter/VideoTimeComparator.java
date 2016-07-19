package com.example.administrator.videotest.adapter;


import com.example.administrator.videotest.entity.Video;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/7/14.
 */
public class VideoTimeComparator implements Comparator<Video> {
    @Override
    public int compare(Video lhs, Video rhs) {
        if(lhs.getTime().compareTo(rhs.getTime())>0){
            return 1;
        }
        else if(lhs.getTime().compareTo(rhs.getTime())<0){
            return -1;
        }
        else {
            return 0;
        }
    }
}
