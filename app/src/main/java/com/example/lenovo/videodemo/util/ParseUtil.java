package com.example.lenovo.videodemo.util;

import com.example.lenovo.videodemo.entity.Version;
import com.google.gson.Gson;

/**
 * Created by lenovo on 2016/7/12.
 */
public class ParseUtil {

    public static Version parserJson(String data) {
        // Log.e("version",s);
        Gson gson=new Gson();
        Version version=gson.fromJson(data,Version.class);
        //  Log.e("version",version.getVersionInfo()+"*"+version.getInfo()+"*"+version.getUrl());
        return version;
    }
}
