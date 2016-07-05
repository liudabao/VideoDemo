package com.example.lenovo.videodemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VideoFragment extends Fragment {


	private View view;
	private RecyclerView recyclerView;
	private LinearLayoutManager linearLayoutManager;
	private MyAdapter adapter;
	private ProgressDialog progress;
	private ArrayList<Video> list=new ArrayList<>();
	private Handler handler;
	private MediaPlayer player;
	private String time;
	private SwipeRefreshLayout swipeRefreshLayout;
	@Override	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		if(view==null){
			view=inflater.inflate(R.layout.layout_video, container, false);
			showProgressDialog();
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null)
		{
			parent.removeView(view);
		}
		handler=new Handler(){
			public void handleMessage(Message msg){
				switch (msg.what) {
					case 1:
						progress.dismiss();
						initView();
						break;
					case 2:
						initView();
						swipeRefreshLayout.setRefreshing(false);
						break;
					default:
						break;
				}
			}
		};
		init(1);
		return view;
	}

	private void initView(){
		recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
		linearLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
		adapter=new MyAdapter(GlobalContext.getContext(), list);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setAdapter(adapter);
		adapter.setOnItemClickLitener(new MyAdapter.OnRecyclerViewItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Intent intent=new Intent(getActivity(), VideoPlayActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("url",list.get(position).getUrl());
				if(position<list.size()-1){
					bundle.putString("next",list.get(position+1).getUrl());
				}
				else {
					bundle.putString("next",null);
				}
				if(position==0){
					bundle.putString("prev",null);
				}
				else {
					bundle.putString("prev",list.get(position).getUrl());
				}
				intent.putExtras(bundle);
				startActivity(intent);
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});

		swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
                 init(2);
			}
		});
	}

	private void init(final int type){
		//player = new MediaPlayer();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					//File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Download");
					File file=Environment.getExternalStorageDirectory();
					getFile(file);
					Message msg=new Message();
					msg.what=type;
					handler.sendMessage(msg);
				}
				else{
					Log.e("TAG", "no sdcard");
				}
				//player.setAudioStreamType(AudioManager.STREAM_MUSIC);


			}
		}).start();
	}

	private void showProgressDialog(){
		progress=new ProgressDialog(getActivity());
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setMessage("扫描本地所有视频文件");
		progress.setCancelable(false);
		progress.show();
	}

	private void getFile(File file){
		File[] subFiles=file.listFiles();
		//Log.e("file", file.getAbsolutePath());
		if(subFiles!=null){
			for(File f:subFiles){
				if(f.isFile()){
					String name=f.getName();
					if(name.trim().toLowerCase().endsWith(".mp4")||name.trim().toLowerCase().endsWith(".rmvb")||name.trim().toLowerCase().endsWith(".avi")
							||name.trim().toLowerCase().endsWith(".mkv")){

						Video video=new Video();
						try {
							Log.e("vedio", file.getPath()+": "+name+" "+formetFileSize(getFileSizes(f)));
						} catch (Exception e) {
							e.printStackTrace();
						}
						Bitmap bitmap=getImage(f);
						/*try {
							player.reset();
							player.setDataSource(f.getPath());
							player.prepare();
							time=getShowTime(player.getDuration());
							//time="00";
							//Log.e("time",getShowTime(player.getDuration()));
						} catch (IOException e) {
							e.printStackTrace();
						}*/
						video.setName(name);
						video.setSize(formetFileSize(getFileSizes(f)));
						//video.setFile(new File(f.getAbsolutePath(),name));
						//Log.e("path",f.getAbsolutePath());
						video.setUrl(f.getAbsolutePath());
						video.setTime(time);
						video.setBitmap(bitmap);
						list.add(video);
					}
				}
				else if(f.isDirectory()&&f.getPath().indexOf("/.")==-1){
					getFile(f);
				}

			}

			/*if(list.size()>0){
				int positon=file.getPath().lastIndexOf("/");
				Log.e("file",file.getPath().substring(positon+1,file.getPath().length()));

			}*/

		}

	}

	private Bitmap getImage(File file){
		MediaMetadataRetriever retriever=new MediaMetadataRetriever();
		//File file=new File(Environment.getExternalStorageDirectory(),"Download/test.mp4");
		retriever.setDataSource(file.getPath());
		String duration=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		//Log.e("url", file.getPath()+": "+time+" "+getShowTime(Long.parseLong(duration)));
        time=getShowTime(Long.parseLong(duration));
		Bitmap bitmap=retriever.getFrameAtTime(Long.parseLong(duration));
		return bitmap;

	}

	private long getFileSizes(File file){
		long size = 0;
		//File file=new File(path);
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				size = fis.available();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return size;
	}

	private  String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSize = "";
		if (fileS < 1024) {
			fileSize = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSize = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSize = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSize = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSize;
	}

	private String getShowTime(long milliseconds) {
		// 获取日历函数
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliseconds);
		SimpleDateFormat dateFormat = null;
		// 判断是否大于60分钟，如果大于就显示小时。设置日期格式
		if (milliseconds / 60000 > 60) {
			dateFormat = new SimpleDateFormat("hh:mm:ss");
		} else {
			dateFormat = new SimpleDateFormat("mm:ss");
		}
		return dateFormat.format(calendar.getTime());
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(player!=null){
			if (player.isPlaying()) {
				player.stop();
			}
			player.release();
		}

		// Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
	}
}
