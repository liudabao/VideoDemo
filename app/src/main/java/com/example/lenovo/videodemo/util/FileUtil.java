package com.example.lenovo.videodemo.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.lenovo.videodemo.entity.Find;
import com.example.lenovo.videodemo.entity.Video;
import com.example.lenovo.videodemo.global.GlobalValue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/7/12.
 */
public class FileUtil {

    public static List<Find> getFile(File file){
        List<Find> list=new ArrayList<>();
        File[] subFiles=file.listFiles();
        Log.e("file", file.getAbsolutePath());
        if(subFiles!=null) {
            for (File f : subFiles) {
                boolean flag = true;
                if (f.isFile()) {
                    Log.e("file movie", f.getAbsolutePath());
                    String name = f.getName();
                    if (name.trim().toLowerCase().endsWith(".mp4") || name.trim().toLowerCase().endsWith(".rmvb") || name.trim().toLowerCase().endsWith(".avi")
                            || name.trim().toLowerCase().endsWith(".mkv")) {
                        Find find=new Find();
                        find.setUrl(f.getPath());
                        find.setParentUrl(file.getPath());
                        find.setName(name);
                        find.setType(GlobalValue.FILE_TYPE_MOVE);
                        list.add(find);
                    }
                }
                else {
                    Log.e("file", f.getAbsolutePath());
                    Find find=new Find();
                    find.setUrl(f.getPath());
                    find.setParentUrl(file.getPath());
                    find.setName(f.getName());
                    find.setType(GlobalValue.FILE_TYPE_DIRECTORY);
                    list.add(find);
                }

            }
        }

        return list;
    }
}
