package com.example.lenovo.videodemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lenovo.videodemo.global.GlobalContext;
import com.example.lenovo.videodemo.util.DataManager;
import com.example.lenovo.videodemo.util.DialogUtil;

import java.io.File;

public class SetFragment extends Fragment {


	private View view;
	private LinearLayout clear;
	private LinearLayout comment;
	private LinearLayout versons;
	private LinearLayout about;
	private TextView size;
	
	@Override	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view=inflater.inflate(R.layout.layout_set, container, false);

		initView();
		initEvent();

		return view;
	}

	private void initView(){
		clear=(LinearLayout)view.findViewById(R.id.clear);
		comment=(LinearLayout)view.findViewById(R.id.comment);
		versons=(LinearLayout)view.findViewById(R.id.versions);
		about=(LinearLayout)view.findViewById(R.id.about);
		size=(TextView)view.findViewById(R.id.text_size);
		try {
			size.setText(DataManager.getCacheSize(GlobalContext.getContext().getExternalCacheDir()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initEvent(){
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					showDialog();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		versons.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAppVersion();
			}
		});

		comment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(), CommentActivity.class);
				startActivity(intent);
			}
		});

		about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogUtil.showDialog(getActivity(),"版权所有  违权必究" );
			}
		});
	}

	private void showDialog(){

		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setMessage("确定清空缓存")
				.setCancelable(true);
		builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				try {
					Log.e("size",DataManager.getCacheSize(GlobalContext.getContext().getExternalCacheDir()));
					DataManager.deleteFilesByDirectory(GlobalContext.getContext().getExternalCacheDir());
					DataManager.cleanDatabases(GlobalContext.getContext());
					size.setText(DataManager.getCacheSize(GlobalContext.getContext().getExternalCacheDir()));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create();
		builder.show();
	}

	private void getAppVersion(){

		PackageManager manager=getActivity().getPackageManager();
		try {
			PackageInfo info=manager.getPackageInfo(getActivity().getPackageName(), 0);
			DialogUtil.showDialog(getActivity(),"当前已是最新版本，版本号为: "+info.versionName );
			Log.e("Version",info.versionName+" and "+info.versionCode);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}


	private void installApp(){
		File file=new File(GlobalContext.getContext().getExternalCacheDir(), "app.apk");
		if(!file.exists()){
			Log.e("apk","not exist");
			return;
		}
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://"+file.toString()),"application/vnd.android.package-archive");
		startActivity(intent);
	}

}
