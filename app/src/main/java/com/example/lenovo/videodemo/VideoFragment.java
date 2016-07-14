package com.example.lenovo.videodemo;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.lenovo.videodemo.entity.Video;
import com.example.lenovo.videodemo.global.GlobalContext;
import com.example.lenovo.videodemo.global.GlobalValue;
import com.example.lenovo.videodemo.service.ScanService;
import com.example.lenovo.videodemo.util.DbManager;
import com.example.lenovo.videodemo.util.DbUtil;
import com.example.lenovo.videodemo.util.FileUtil;
import com.example.lenovo.videodemo.util.ImageUtil;
import com.example.lenovo.videodemo.util.MediaUtil;
import com.example.lenovo.videodemo.util.VideoAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class VideoFragment extends Fragment {

	private View view;
	private RecyclerView recyclerView;
	private LinearLayoutManager linearLayoutManager;
	private VideoAdapter adapter;
	private ProgressDialog progress;
	private ImageButton menu;
	private List<Video> list=new ArrayList<>();
	private Handler handler;
	private String time;
	private String imageUrl;
	private SwipeRefreshLayout swipeRefreshLayout;
	private DbUtil dbUtil;
	//private Toolbar toolbar;
	private boolean isLoad=false;
	private PopupWindow popupWindow;
    private ListView listView;
	private IntentFilter intentFilter;
	private EditBroad editBroad;
	private ServiceConnection connection;
	private ScanService scanService;

	@Override	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		Log.e("fragment", "onCreateView");
		if(view==null){
			isLoad=true;
			//showProgressDialog();
			view=inflater.inflate(R.layout.layout_video, container, false);

		}
		else {
			isLoad=false;
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null)
		{
			parent.removeView(view);
		}
		connection=new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.e("ScanService", "connection");
				scanService=((ScanService.ScanBinder) service).getService();
				scanService.scan();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.e("ScanService", "disconnection");
			}
		};

		initHandler();
		if (isLoad){
			initData();
			init(GlobalValue.TYPE_ENTER);
		}
		return view;
	}

	private void initData(){
		list=DbManager.query();
		if(list.size()>0){
			Log.e("video query", list.size()+"");
			initView();
		}
		//showProgressDialog();

	}

	private void initView(){
		intentFilter=new IntentFilter();
		editBroad=new EditBroad();
		intentFilter.addAction("android.video.delete");
		//getActivity().registerReceiver(editBroad, intentFilter);
        menu=(ImageButton)view.findViewById(R.id.meun) ;
		recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
		linearLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
		adapter=new VideoAdapter(GlobalContext.getContext(), list);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setAdapter(adapter);
		swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe);

	}

	private void initEvent(){
		adapter.setOnItemClickLitener(new VideoAdapter.OnRecyclerViewItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Log.e("item click", list.get(position).getName());
				Intent intent=new Intent(getActivity(), VideoPlayActivity.class);
				Bundle bundle=new Bundle();
				//bundle.putString("url",list.get(position).getUrl());
				bundle.putSerializable(GlobalValue.KEY, list.get(position));
				intent.putExtras(bundle);
				startActivityForResult(intent, GlobalValue.REQUEST_CODE_PLAY);
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});

		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				init(GlobalValue.TYPE_REFREASH);
			}
		});
		//
		menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopWindow(v);
				getActivity().registerReceiver(editBroad, intentFilter);
			}
		});
	}

	private void initHandler(){
		handler=new Handler(){
			public void handleMessage(Message msg){
				switch (msg.what) {
					case 1:
						//progress.dismiss();
						initView();
						initEvent();
						//setNext();
						MediaUtil.setNext(list);
						DbManager.insert(list);
						break;
					case 2:
						//initView();
						adapter=new VideoAdapter(GlobalContext.getContext(), list);
						recyclerView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						initEvent();
						//setNext();
						MediaUtil.setNext(list);
						DbManager.insert(list);
						swipeRefreshLayout.setRefreshing(false);
						break;
					case 3:
						//initView();
						adapter=new VideoAdapter(GlobalContext.getContext(), list);
						recyclerView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						initEvent();
						//setNext();
						MediaUtil.setNext(list);
						DbManager.insert(list);
						break;
					case 4:
						//initView();
						list=DbManager.query();
						adapter=new VideoAdapter(GlobalContext.getContext(), list);
						recyclerView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						Log.e("delete", "refreash");
						initEvent();
						//setNext();
						MediaUtil.setNext(list);
						DbManager.insert(list);
						break;
					default:
						break;
				}
			}
		};
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
					//FileUtil.getFile(file, time, imageUrl, list);
					Message msg=new Message();
					msg.what=type;
					//handler.sendMessage(msg);
					Intent intent=new Intent(getActivity(), ScanService.class);
					getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
				}
				else{
					Log.e("TAG", "no sdcard");
				}
				//player.setAudioStreamType(AudioManager.STREAM_MUSIC);


			}
		}).start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case GlobalValue.REQUEST_CODE_PLAY:
				if(resultCode==getActivity().RESULT_OK){
					Log.e("result","ok");
					list=DbManager.query();
					adapter=new VideoAdapter(GlobalContext.getContext(), list);
					recyclerView.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					for(Video video:list){
						Log.e("result", video.getName()+" "+video.getPosition());
					}
					initEvent();
				}
				break;
			default:
				break;

		}
	}

	/*private void setNext(){
		for(int i=0;i<list.size();i++){
			if(i>0){
				list.get(i).setPrevUrl(list.get(i-1).getUrl());
			}
			if(i<list.size()-1){
				list.get(i).setNextUrl(list.get(i+1).getUrl());
			}

		}
	}*/

	private void showProgressDialog(){
		progress=new ProgressDialog(getActivity());
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setMessage("扫描本地所有视频文件");
		progress.setCancelable(false);
		progress.show();
	}

	private void showPopWindow(View parent){
		if(popupWindow==null){
			View view=LayoutInflater.from(GlobalContext.getContext()).inflate(R.layout.popmenu_layout, null);
			listView=(ListView)view.findViewById(R.id.popView);
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(GlobalContext.getContext(), android.R.layout.simple_list_item_1, GlobalValue.MENU);
			listView.setAdapter(adapter);
			popupWindow=new PopupWindow(view, 350, 450);
		}
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
		int xPos = windowManager.getDefaultDisplay().getWidth() / 2
				- popupWindow.getWidth() / 2;
		popupWindow.showAsDropDown(parent, xPos, 0);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch (position){
					case 0:
						EditFragment editFragment=new EditFragment();
						replace(editFragment);
						Toast.makeText(GlobalContext.getContext(),""+position, Toast.LENGTH_SHORT).show();
						break;
					case 1:
						FindFragment findFragment=new FindFragment();
						replace(findFragment);
						Toast.makeText(GlobalContext.getContext(),""+position, Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
				}
				if(popupWindow!=null){
					popupWindow.dismiss();
				}
			}
		});
	}

	private void replace(Fragment fragment){
		FragmentManager manager=getActivity().getSupportFragmentManager();
		FragmentTransaction transaction=manager.beginTransaction();
		transaction.replace(R.id.frame_main, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	/*private void getFile(File file){
		File[] subFiles=file.listFiles();
		//Log.e("file", file.getAbsolutePath());
		if(subFiles!=null){
			for(File f:subFiles){
				boolean flag=true;
				if(f.isFile()){
					String name=f.getName();
					if(name.trim().toLowerCase().endsWith(".mp4")||name.trim().toLowerCase().endsWith(".rmvb")||name.trim().toLowerCase().endsWith(".avi")
							||name.trim().toLowerCase().endsWith(".mkv")){

						Bitmap bitmap= ImageUtil.getImage(time,f);
						try {
							imageUrl=ImageUtil.saveBitmap(name, bitmap);

						} catch (IOException e) {
							e.printStackTrace();
						}
						for(Video v:list){
							if(v.getName().equals(name)){
								flag=false;
								break;
							}
						}
						if(flag){

							Video video=new Video();
							try {
								//Log.e("vedio", file.getPath()+": "+name+" "+formetFileSize(getFileSizes(f)));
							} catch (Exception e) {
								e.printStackTrace();
							}
							video.setName(name);
							video.setSize(FileUtil.formetFileSize(FileUtil.getFileSizes(f)));
							video.setUrl(f.getAbsolutePath());
							video.setTime(time);
							video.setImageUrl(imageUrl);
							list.add(video);
						}

					}
				}
				else if(f.isDirectory()&&f.getPath().indexOf("/.")==-1){
					getFile(f);
				}

			}
			/*if(list.size()>0){
				int positon=file.getPath().lastIndexOf("/");
				Log.e("file",file.getPath().substring(positon+1,file.getPath().length()));

			}

		}

	}*/

	/*private Bitmap getImage(File file){
		MediaMetadataRetriever retriever=new MediaMetadataRetriever();
		//File file=new File(Environment.getExternalStorageDirectory(),"Download/test.mp4");
		//Log.e("bitmap", file.getPath());
		retriever.setDataSource(file.getPath());
		String duration=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		//Log.e("url", file.getPath()+": "+time+" "+getShowTime(Long.parseLong(duration)));
        time=getShowTime(Long.parseLong(duration));
		Bitmap bitmap=retriever.getFrameAtTime(Long.parseLong(duration));
		return bitmap;

	}

	private String saveBitmap(String name,Bitmap bm) throws IOException {
		//File f = new File(getActivity().getFilesDir(), name);
		File f = new File(GlobalContext.getContext().getExternalCacheDir(), name+".png");
		//Log.e("path",f.getPath() );
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream out=null;
		try {
			out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();

			Log.i("Bitmap", "已经保存");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			out.close();
		}
		return f.getPath();
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
		Log.e("time size", milliseconds+" and "+dateFormat.format(milliseconds));
		return dateFormat.format(milliseconds);
	}*/

	/*private void insert(){

		dbUtil=new DbUtil();
		for(Video video:list){
			if(!dbUtil.isExist(GlobalValue.TABLE, video.getName())){
				Log.e("video insert","insert");
				dbUtil.insert(video, GlobalValue.TABLE);
			}
			else {
				Log.e("video insert","update");
				dbUtil.update(video, GlobalValue.TABLE);
			}
		}
		//dbUtil.insert(list, GlobalValue.TABLE);
	}

	private void query(){
		list.clear();
		dbUtil=new DbUtil();
		list=dbUtil.queryAll(GlobalValue.TABLE);

	}*/

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(editBroad);

	}

	class EditBroad extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("broad", "i receive");
			//list=dbUtil.queryAll(GlobalValue.TABLE);

			Message msg=new Message();
			msg.what=4;
			handler.sendMessage(msg);
		}
	}

}
