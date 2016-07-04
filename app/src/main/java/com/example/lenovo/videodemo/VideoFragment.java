package com.example.lenovo.videodemo;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class VideoFragment extends Fragment {


	private View view;
	private RecyclerView recyclerView;
	private LinearLayoutManager linearLayoutManager;
	private MyAdapter adapter;
	private ArrayList list;
	@Override	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view=inflater.inflate(R.layout.layout_video, container, false);
		initData();
		initView();
		return view;
	}
	private void  initData(){
		list=new ArrayList<String>();
		for(int i=0;i<10;i++){
			list.add("test");
		}

	}
	private void initView(){
		recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
		linearLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
		adapter=new MyAdapter(GlobalContext.getContext(), list);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setAdapter(adapter);
	}
}
