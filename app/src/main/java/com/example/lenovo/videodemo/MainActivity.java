package com.example.lenovo.videodemo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义一个布局
    private LayoutInflater layoutInflater;
    //Tab选项卡的文字
    private String mTextviewArray[] = {"视频", "设置"};
    private Class fragmentArray[] = {VideoFragment.class,SetFragment.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        layoutInflater = LayoutInflater.from(this);
       //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content);

        for(int i = 0; i < 2; i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
           // mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }
    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(R.mipmap.ic_launcher);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

}
