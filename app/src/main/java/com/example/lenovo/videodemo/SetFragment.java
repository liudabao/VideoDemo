package com.example.lenovo.videodemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lenovo.videodemo.entity.Version;
import com.example.lenovo.videodemo.global.GlobalContext;
import com.example.lenovo.videodemo.global.GlobalValue;
import com.example.lenovo.videodemo.listener.OnHttpListener;
import com.example.lenovo.videodemo.service.DownloadService;
import com.example.lenovo.videodemo.util.DataManager;
import com.example.lenovo.videodemo.util.DialogUtil;
import com.example.lenovo.videodemo.util.HttpUtil;
import com.example.lenovo.videodemo.util.ParseUtil;

import java.io.File;

public class SetFragment extends Fragment {


	private View view;
	private LinearLayout clear;
	private LinearLayout comment;
	private LinearLayout versons;
	private LinearLayout about;
	private TextView size;
	private DownloadService downloadService;
	private ServiceConnection connection;
	private IntentFilter filter;
	private DownloadReceiver receiver;
	private Version version;
	private Handler handler;
	
	@Override	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view=inflater.inflate(R.layout.layout_set, container, false);
		connection=new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				downloadService=((DownloadService.DownloadBinder) service).getService();
				downloadService.download();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {

			}
		};
		handler=new Handler(){

			public void handleMessage(Message message){
				switch (message.what){
					case GlobalValue.VERSION:
						showDownDialog();
						break;
					default:
						break;
				}
			}
		};
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
		filter=new IntentFilter();
		filter.addAction("android.video.update");
		receiver=new DownloadReceiver();
	}

	private void initEvent(){
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					showClearDialog();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		versons.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAppVersion();
				//showDownDialog();

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

	private void showClearDialog(){

		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setMessage("确定清空应用数据")
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

	private void showDownDialog(){

		Log.e("update", "start");
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setTitle("版本更新");
		builder.setMessage(version.getInfo())
				.setCancelable(true);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				try {

					Intent intent=new Intent(getActivity(), DownloadService.class);
					getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
					getActivity().registerReceiver(receiver, filter);


				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create();
		builder.show();
		Log.e("update", "end");
	}

	private void getAppVersion(){

		new Thread(new Runnable() {
			@Override
			public void run() {

				HttpUtil.get(GlobalValue.VERSION_URL, new OnHttpListener() {
					@Override
					public void onSuccess(String response) {
						Log.e("Version",response);
						version= ParseUtil.parserJson(response);
						PackageManager manager=getActivity().getPackageManager();
						PackageInfo info= null;
						try {
							info = manager.getPackageInfo(getActivity().getPackageName(), 0);
							//Log.e("Version",info.versionName+" and "+version.getValue()+" and "+info.versionCode);
							if(info.versionName.compareTo(version.getValue())>0){
								Log.e("Version ",info.versionName+" and "+version.getValue());
								DialogUtil.showDialog(getActivity(),"当前已是最新版本，版本号为: "+info.versionName );

							}
							else {
								Log.e("Version update",info.versionName+" and "+version.getValue());
								//showDownDialog();
								Message msg=new Message();
								msg.what=GlobalValue.VERSION;
								handler.sendMessage(msg);
							}
						} catch (PackageManager.NameNotFoundException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailed() {
						DialogUtil.showDialog(getActivity(),"网络连接失败" );
					}
				});
			}
		}).start();
	}


	private void installApp(){
		//File file=new File(GlobalContext.getContext().getExternalCacheDir(), "test.apk");
		File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"test.apk");
		if(!file.exists()){
			Log.e("apk","not exist");
			return;
		}
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://"+file.toString()),"application/vnd.android.package-archive");
		startActivity(intent);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		getActivity().unbindService(connection);
		getActivity().unregisterReceiver(receiver);
	}


	class DownloadReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			getActivity().unbindService(connection);
			installApp();
		}
	}
	
}
