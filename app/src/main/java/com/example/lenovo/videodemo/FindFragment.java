package com.example.lenovo.videodemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.lenovo.videodemo.entity.Find;
import com.example.lenovo.videodemo.global.GlobalContext;
import com.example.lenovo.videodemo.util.FileUtil;
import com.example.lenovo.videodemo.util.FindContentAdapter;
import com.example.lenovo.videodemo.util.FindHeadAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FindFragment extends Fragment {

    private View view;
    private RecyclerView head;
    private RecyclerView content;
    private ImageButton back;
    private FindHeadAdapter headAdapter;
    private FindContentAdapter contentAdapter;
    private LinearLayoutManager headLayoutManager;
    private LinearLayoutManager contentLayoutManager;
    private List<Find> moveList=new ArrayList<>();
    private List<String> fileList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.layout_find, container, false);
        initData();
        initView();
        initEvent();
        return view;
    }

    private void initData(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Download");
            File file=Environment.getExternalStorageDirectory();
            moveList= FileUtil.getFile(file);
            fileList.add(file.getPath());
        }

    }

    private void initView(){
        back=(ImageButton)view.findViewById(R.id.find_back);
        head=(RecyclerView)view.findViewById(R.id.head_recyclerview);
        content=(RecyclerView)view.findViewById(R.id.content_recyclerView);
        headLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
        headLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        contentLayoutManager=new LinearLayoutManager(GlobalContext.getContext());
        headAdapter=new FindHeadAdapter(GlobalContext.getContext(), fileList);
        contentAdapter=new FindContentAdapter(GlobalContext.getContext(), moveList);
        head.setLayoutManager(headLayoutManager);
        head.setAdapter(headAdapter);
        content.setLayoutManager(contentLayoutManager);
        content.setAdapter(contentAdapter);

    }

    private void initEvent(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

}
