package com.example.lenovo.videodemo.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by lenovo on 2016/7/13.
 */
public class MediaUtil {

    public static String getMediaTime(File file){

        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        //File file=new File(Environment.getExternalStorageDirectory(),"Download/test.mp4");
        //Log.e("bitmap", file.getPath());
        retriever.setDataSource(file.getPath());
        String duration=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);;
        return getShowTime(Long.parseLong(duration));

    }
    public static String getShowTime(long milliseconds) {
        // 获取日历函数
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(milliseconds);

        SimpleDateFormat dateFormat = null;
        // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
        if (milliseconds > 3600*1000) {

            dateFormat = new SimpleDateFormat("HH:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //return dateFormat.format(calendar.getTime());
       // Log.e("time size", milliseconds+" and "+dateFormat.format(milliseconds));
        return dateFormat.format(milliseconds);
    }
}
