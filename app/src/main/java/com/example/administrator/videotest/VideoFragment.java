package com.example.administrator.videotest;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
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

import com.example.administrator.videotest.entity.Video;
import com.example.administrator.videotest.global.GlobalContext;
import com.example.administrator.videotest.global.GlobalValue;
import com.example.administrator.videotest.service.ScanService;
import com.example.administrator.videotest.util.DbManager;
import com.example.administrator.videotest.util.VideoAdapter;
import com.example.administrator.videotest.util.VideoNameComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoFragment extends Fragment {

	private View view;
	private RecyclerView recyclerView;
	private LinearLayoutManager linearLayoutManager;
	private VideoAdapter adapter;
	private ProgressDialog progress;
	private ImageButton menu;
	private List<Video> list=new ArrayList<>();
	private Handler handler;
	//private String time;
	//private String imageUrl;
	private SwipeRefreshLayout swipeRefreshLayout;
	//private DbUtil dbUtil;
	//private Toolbar toolbar;
	private boolean isLoad=false;
	private PopupWindow popupWindow;
    private ListView listView;
	private IntentFilter intentFilter;
	private IntentFilter videoFilter;
	private EditBroad editBroad;
	private VideoBroad videoBroad;

	private ScanService scanService;
	private int type;
	private boolean isBind=false;
	private ServiceConnection connection=new ServiceConnection() {
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

		initHandler();
		if (isLoad){
			initData();
			init(GlobalValue.TYPE_ENTER);
		}
		return view;
	}

	private void initData(){
		list= DbManager.query();
		Collections.sort(list, new VideoNameComparator());
		initView();
		initEvent();
		if(list.size()==0){
			showProgressDialog();
		}
	}

	private void initView(){
		intentFilter=new IntentFilter();
		editBroad=new EditBroad();
		videoBroad=new VideoBroad();
		intentFilter.addAction("android.video.delete");
		videoFilter=new IntentFilter();
		videoFilter.addAction("android.video.scan");
		//getActivity().registerReceiver(editBroad, intentFilter);
        menu=(ImageButton)view.findViewById(R.id.video_meun) ;
		recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
		linearLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
		adapter=new VideoAdapter(GlobalContext.getContext(), list);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setAdapter(adapter);
		swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe);
		getActivity().registerReceiver(videoBroad, videoFilter);
		getActivity().registerReceiver(editBroad, intentFilter);

	}

	private void initEvent(){
		adapter.setOnItemClickLitener(new VideoAdapter.OnRecyclerViewItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Log.e("item click", list.get(position).getName());
				Intent intent=new Intent(getActivity(), VideoPlayActivity.class);
				Bundle bundle=new Bundle();
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
				//getActivity().registerReceiver(editBroad, intentFilter);
			}
		});
	}

	private void initHandler(){
		handler=new Handler(){
			public void handleMessage(Message msg){
				switch (msg.what) {
					case GlobalValue.TYPE_ENTER:
						//initView();
						if(progress!=null&&progress.isShowing()){
							progress.dismiss();
						}
						list=DbManager.query();
						Collections.sort(list, new VideoNameComparator());
						Log.e("message 1",list.size()+"");
						adapter=new VideoAdapter(GlobalContext.getContext(), list);
						recyclerView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						initEvent();
						//setNext();
						//MediaUtil.setNext(list);
						//DbManager.insert(list);
						//swipeRefreshLayout.setRefreshing(false);
						break;
					case GlobalValue.TYPE_REFREASH:
						//initView();
						list=DbManager.query();
						Collections.sort(list, new VideoNameComparator());
						Log.e("message 2",list.size()+"");
						adapter=new VideoAdapter(GlobalContext.getContext(), list);
						recyclerView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						initEvent();
						//setNext();
						//MediaUtil.setNext(list);
						//DbManager.insert(list);
						swipeRefreshLayout.setRefreshing(false);
						break;
					default:
						break;
				}
			}
		};
	}

	private void init(int type){
		this.type=type;
		//player = new MediaPlayer();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					//File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Download");
					//File file=Environment.getExternalStorageDirectory();
					//FileUtil.getFile(file, time, imageUrl, list);
					//Message msg=new Message();
					//msg.what=type;
					//handler.sendMessage(msg);
					//Intent intent=new Intent(GlobalContext.getContext(), ScanService.class);
					//getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
					Intent intent=new Intent(GlobalContext.getContext(), ScanService.class);
					getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
                   // isBind=true;

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
					Collections.sort(list, new VideoNameComparator());
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
						type=GlobalValue.TYPE_ENTER;
						//Toast.makeText(GlobalContext.getContext(),""+position, Toast.LENGTH_SHORT).show();
						break;
					case 1:
						FindFragment findFragment=new FindFragment();
						replace(findFragment);
						//Toast.makeText(GlobalContext.getContext(),""+position, Toast.LENGTH_SHORT).show();
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

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(editBroad);
		getActivity().unregisterReceiver(videoBroad);

	}

	class EditBroad extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("broad", "i receive");
			//list=dbUtil.queryAll(GlobalValue.TABLE);
			Message msg=new Message();
			//msg.what=GlobalValue.TYPE_ENTER;
			msg.what=type;
			handler.sendMessage(msg);
		}
	}

	class VideoBroad extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("broad", "i receive 2");
			getActivity().unbindService(connection);
			Message msg=new Message();
			msg.what=type;
			handler.sendMessage(msg);
		}
	}

}
