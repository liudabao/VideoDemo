package com.example.lenovo.videodemo.util;

import com.example.lenovo.videodemo.entity.Video;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/7/14.
 */
public class VideoNameComparator implements Comparator<Video> {
    @Override
    public int compare(Video lhs, Video rhs) {
        if(lhs.getName().compareTo(rhs.getName())>0){
            return 1;
        }
        else if(lhs.getName().compareTo(rhs.getName())<0){
            return -1;
        }
        else {
            return 0;
        }
    }
}
