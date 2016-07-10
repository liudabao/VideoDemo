package com.example.lenovo.videodemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoFragment extends Fragment {


	private View view;
	private RecyclerView recyclerView;
	private LinearLayoutManager linearLayoutManager;
	private MyAdapter adapter;
	private ProgressDialog progress;
	private ImageButton menu;
	private List<Video> list=new ArrayList<>();
	private Handler handler;
	//private MediaPlayer player;
	private String time;
	private String imageUrl;
	private SwipeRefreshLayout swipeRefreshLayout;
	private DbUtil dbUtil;
	private Toolbar toolbar;
	private boolean isLoad=false;
	private PopupWindow popupWindow;
    private ListView listView;

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
		handler=new Handler(){
			public void handleMessage(Message msg){
				switch (msg.what) {
					case 1:
						progress.dismiss();
						initView();
						setNext();
						insert();
						break;
					case 2:
						initView();
						setNext();
						insert();
						swipeRefreshLayout.setRefreshing(false);
						break;
					case 3:
						initView();
						setNext();
						insert();
						break;
					default:
						break;
				}
			}
		};

		//query();
		//initToolBar();
		if (isLoad){
			initData();
			init(GlobalValue.TYPE_ENTER);
		}
		return view;
	}

	/*@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.video_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}*/

	/*private void initToolBar(){
		setHasOptionsMenu(true);
		toolbar=(Toolbar)view.findViewById(R.id.toolbar);
		//toolbar.setLogo(R.mipmap.ic_launcher);
		toolbar.setTitle(GlobalValue.VIDEO);
		//toolbar.setSubtitle("Sub title");
		((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()){
					case R.id.add:
						Toast.makeText(GlobalContext.getContext(), "add", Toast.LENGTH_SHORT).show();
						break;
					case R.id.remove:
						Toast.makeText(GlobalContext.getContext(), "remove", Toast.LENGTH_SHORT).show();
						break;
				}
				return true;
			}
		});
	}*/

	private void initData(){
		query();
		if(list.size()>0){
			initView();
		}
		showProgressDialog();

	}

	private void initView(){
        menu=(ImageButton)view.findViewById(R.id.meun) ;
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
				//bundle.putString("url",list.get(position).getUrl());
                bundle.putSerializable(GlobalValue.KEY, list.get(position));
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
                 init(GlobalValue.TYPE_REFREASH);
			}
		});
		//
		menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopWindow(v);
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

	private void setNext(){
		for(int i=0;i<list.size();i++){
			if(i>0){
				list.get(i).setPrevUrl(list.get(i-1).getUrl());
			}
			if(i<list.size()-1){
				list.get(i).setNextUrl(list.get(i+1).getUrl());
			}

		}
	}

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
						FragmentManager manager=getActivity().getSupportFragmentManager();
						FragmentTransaction transaction=manager.beginTransaction();
						transaction.replace(R.id.frame_main, editFragment);
						transaction.addToBackStack(null);
						transaction.commit();
						Toast.makeText(GlobalContext.getContext(),""+position, Toast.LENGTH_SHORT).show();
						break;
					case 1:
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

	private void getFile(File file){
		File[] subFiles=file.listFiles();
		//Log.e("file", file.getAbsolutePath());
		if(subFiles!=null){
			for(File f:subFiles){
				boolean flag=true;
				if(f.isFile()){
					String name=f.getName();
					if(name.trim().toLowerCase().endsWith(".mp4")||name.trim().toLowerCase().endsWith(".rmvb")||name.trim().toLowerCase().endsWith(".avi")
							||name.trim().toLowerCase().endsWith(".mkv")){

						for(Video v:list){
							if(v.getName().equals(name)){
								flag=false;
								break;
							}
						}
						if(flag){
							Video video=new Video();
							try {
								Log.e("vedio", file.getPath()+": "+name+" "+formetFileSize(getFileSizes(f)));
							} catch (Exception e) {
								e.printStackTrace();
							}
							video.setName(name);
							video.setSize(formetFileSize(getFileSizes(f)));
							//video.setFile(new File(f.getAbsolutePath(),name));
							//Log.e("path",f.getAbsolutePath());
							video.setUrl(f.getAbsolutePath());
							video.setTime(time);
							// video.setImageUrl(imageUrl);
							//video.setBitmap(bitmap);
							list.add(video);
						}

						//Bitmap bitmap=getImage(f);
						//imageUrl=saveBitmap(name, bitmap);
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

	private String saveBitmap(String name,Bitmap bm) {
		File f = new File(getActivity().getFilesDir(), name);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			Log.i("Bitmap", "已经保存");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private void insert(){
		dbUtil=new DbUtil();
		for(Video video:list){
			if(!dbUtil.isExist(GlobalValue.TABLE, video.getName())){
				dbUtil.insert(video, GlobalValue.TABLE);
			}
		}
		dbUtil.insert(list, GlobalValue.TABLE);
	}

	private void query(){
		dbUtil=new DbUtil();
		list=dbUtil.queryAll(GlobalValue.TABLE);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		/*if(player!=null){
			if (player.isPlaying()) {
				player.stop();
			}
			player.release();
		}*/

		// Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
	}
}
