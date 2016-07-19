package com.example.administrator.videotest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.administrator.videotest.entity.Find;
import com.example.administrator.videotest.entity.Video;
import com.example.administrator.videotest.global.GlobalContext;
import com.example.administrator.videotest.global.GlobalValue;
import com.example.administrator.videotest.listener.OnRecyclerViewItemClickListener;
import com.example.administrator.videotest.util.DbUtil;
import com.example.administrator.videotest.util.FileUtil;
import com.example.administrator.videotest.adapter.FindContentAdapter;
import com.example.administrator.videotest.adapter.FindHeadAdapter;
import com.example.administrator.videotest.adapter.FindNameComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private DbUtil dbUtil;

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
            FileUtil.getFile(file, moveList);
            Collections.sort(moveList, new FindNameComparator());
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
        dbUtil=new DbUtil();
    }

    private void initEvent(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        contentAdapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Log.e("item path", moveList.get(position).getUrl());
                Find find=moveList.get(position);
                if(find.getType()== GlobalValue.FILE_TYPE_DIRECTORY){
                    File file=new File(find.getUrl());
                    fileList.add(find.getUrl());
                    FileUtil.getFile(file, moveList);
                    Collections.sort(moveList, new FindNameComparator());
                    contentAdapter.notifyDataSetChanged();
                    headAdapter.notifyDataSetChanged();
                }
                else if(find.getType()==GlobalValue.FILE_TYPE_MOVE){
                    Video video=dbUtil.queryByName(GlobalValue.TABLE, find.getName());
                    Intent intent=new Intent(getActivity(), VideoPlayActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable(GlobalValue.KEY, video);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }


            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        headAdapter.setOnItemClickLitener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String path=fileList.get(position);
                File file=new File(path);
                FileUtil.getFile(file, moveList);
                for(int i=fileList.size()-1; i>position; i--){
                    fileList.remove(i);
                }
                Collections.sort(moveList, new FindNameComparator());
                contentAdapter.notifyDataSetChanged();
                headAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

}
