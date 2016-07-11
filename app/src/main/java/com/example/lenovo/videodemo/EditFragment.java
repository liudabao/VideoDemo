package com.example.lenovo.videodemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.lenovo.videodemo.entity.Video;
import com.example.lenovo.videodemo.global.GlobalContext;
import com.example.lenovo.videodemo.global.GlobalValue;
import com.example.lenovo.videodemo.util.DbUtil;
import com.example.lenovo.videodemo.util.EditAdapter;

import java.util.List;

public class EditFragment extends Fragment {


	private View view;
	private ImageButton back;
	private ImageButton delete;
	private ImageButton select;
	private RecyclerView recyclerView;
	private List<Video> list;
	private LinearLayoutManager linearLayoutManager;
	private EditAdapter adapter;
	private boolean isSelected=false;
	private DbUtil dbUtil;

	@Override	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view=inflater.inflate(R.layout.layout_video_edit, container, false);

		initData();
		initView();
        initEvent();
		return view;
	}


	private void initData(){
		dbUtil=new DbUtil();
		list=dbUtil.queryAll(GlobalValue.TABLE);
	}

	private void initView(){
		back=(ImageButton)view.findViewById(R.id.btn_back);
		select=(ImageButton)view.findViewById(R.id.btn_select);
		delete=(ImageButton)view.findViewById(R.id.btn_delete);
		recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView_edit);
		linearLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
		adapter=new EditAdapter(GlobalContext.getContext(), list);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setAdapter(adapter);

	}

	private void initEvent(){
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});

		select.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isSelected==false){
					select.setBackgroundResource(R.drawable.select);
					for(Video video:list){
						video.setSelected("true");
					}
					isSelected=true;
				}
				else {
					select.setBackgroundResource(R.drawable.unselect);
					for(Video video:list){
						video.setSelected("false");
					}
					isSelected=false;
				}

				adapter.notifyDataSetChanged();
			}
		});
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDeleteDialog(getActivity());
			}
		});
		adapter.setOnItemClickLitener(new EditAdapter.OnRecyclerViewItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				if(list.get(position).getSelected().equals("false")){
					list.get(position).setSelected("true");
				}
				else{
					list.get(position).setSelected("false");
				}
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});
	}

	private  void showDeleteDialog(Context context){
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View myDeleteView = layoutInflater.inflate(R.layout.dialog_delete_layout, null);
		Dialog alertDialog = new AlertDialog.Builder(context).
				//setTitle("确认删除").
				setView(myDeleteView).
				setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						for(Video video:list){
							dbUtil.update(video, GlobalValue.TABLE);
						}
						Intent intent=new Intent("android.video.delete");
						getActivity().sendBroadcast(intent);
						getFragmentManager().popBackStack();
					}
				}).
				setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).
				create();
		alertDialog.show();
	}
}
