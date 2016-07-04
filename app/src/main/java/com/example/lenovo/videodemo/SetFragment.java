package com.example.lenovo.videodemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SetFragment extends Fragment {


	private View view;
	
	@Override	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		view=inflater.inflate(R.layout.layout_set, container, false);
		return view;
	}

}
