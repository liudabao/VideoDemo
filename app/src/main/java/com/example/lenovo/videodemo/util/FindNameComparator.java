package com.example.lenovo.videodemo.util;

import com.example.lenovo.videodemo.entity.Find;
import com.example.lenovo.videodemo.entity.Video;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/7/14.
 */
public class FindNameComparator implements Comparator<Find> {

    @Override
    public int compare(Find lhs, Find rhs) {
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
