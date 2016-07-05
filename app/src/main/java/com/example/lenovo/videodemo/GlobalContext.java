package com.example.lenovo.videodemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

public class GlobalContext extends Application{

	public static Context context;


	@Override
	public void onCreate(){
		super.onCreate();
		context=getApplicationContext();
		//Log.e("global",context+"");
	}
	
	public static Context getContext(){
		return context;
	}


}
