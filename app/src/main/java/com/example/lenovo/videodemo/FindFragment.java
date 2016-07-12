package com.example.lenovo.videodemo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class FindFragment extends Fragment {

    private View view;
    private RecyclerView head;
    private RecyclerView content;
    private ImageButton back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_find, container, false);
        return view;
    }

    private void initView(){
        back=(ImageButton)view.findViewById(R.id.find_back);
        head=(RecyclerView)view.findViewById(R.id.head_recyclerview);
        content=(RecyclerView)view.findViewById(R.id.content_recyclerView);
    }

}
