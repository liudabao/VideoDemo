package com.example.lenovo.videodemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.lenovo.videodemo.entity.Video;
import com.example.lenovo.videodemo.global.GlobalContext;
import com.example.lenovo.videodemo.global.GlobalValue;
import com.example.lenovo.videodemo.util.DbUtil;
import com.example.lenovo.videodemo.util.DialogUtil;
import com.example.lenovo.videodemo.util.EditAdapter;

import org.w3c.dom.Text;

import java.io.File;
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
	private boolean isDelete=false;
	private TextView text_select;
	private TextView text_delete;

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
		Log.e("delet list", list.size()+"");
	}

	private void initView(){
		back=(ImageButton)view.findViewById(R.id.btn_back);
		select=(ImageButton)view.findViewById(R.id.btn_select);
		delete=(ImageButton)view.findViewById(R.id.btn_delete);
		text_select=(TextView)view.findViewById(R.id.text_select);
		text_delete=(TextView)view.findViewById(R.id.text_delete);
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
					text_select.setTextColor(getResources().getColor(R.color.skyblue));
				}
				else {
					select.setBackgroundResource(R.drawable.unselect);
					for(Video video:list){
						video.setSelected("false");
					}
					isSelected=false;
					text_select.setTextColor(getResources().getColor(R.color.md_gray_500));
				}

				adapter.notifyDataSetChanged();
			}
		});
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean flag=false;
				for(Video video:list){
					if(video.getSelected().equals("true")){
						flag=true;
					}
				}
				if(flag){
					showDeleteDialog(getActivity());

				}
				else {
					DialogUtil.showDialog(getActivity(), "选择所要删除视频");
				}

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
		final ImageButton choose=(ImageButton)myDeleteView.findViewById(R.id.image_delete);
		TextView sure=(TextView) myDeleteView.findViewById(R.id.delete_sure);
		TextView cancle=(TextView) myDeleteView.findViewById(R.id.delete_cancle);
		choose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isDelete){
					isDelete=false;
					choose.setBackgroundResource(R.drawable.unselect);
				}
				else {
					isDelete=true;
					choose.setBackgroundResource(R.drawable.select);
				}
			}
		});
		final Dialog alertDialog = new AlertDialog.Builder(context).
				//setTitle("确认删除").
				setView(myDeleteView).
				create();
		alertDialog.show();
		sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				delete(alertDialog);
			}
		});
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
	}

	private void delete(Dialog dialog){
		for(Video video:list){
			if(video.getSelected().equals("true")){
				if(isDelete){
					dbUtil.delete(video.getName(), GlobalValue.TABLE);
					File file=new File(video.getUrl());
					if(file.exists()){
						file.delete();
					}

				}
				else {
					dbUtil.update(video, GlobalValue.TABLE);
				}
			}

		}
		Intent intent=new Intent("android.video.delete");
		getActivity().sendBroadcast(intent);
		getFragmentManager().popBackStack();
		dialog.dismiss();

	}

}
