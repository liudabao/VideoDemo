package com.example.administrator.videotest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity {

    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    // private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    //定义一个布局
    private LayoutInflater layoutInflater;
    //Tab选项卡的文字
    private String mTextviewArray[] = {"视频", "共享", "设置"};
    private int mImageArray[] = {R.drawable.video_selector,R.drawable.share_selector, R.drawable.set_selector};
    private Class fragmentArray[] = {VideoFragment.class, DeviceFragment.class, SetFragment.class};
    // private Fragment mFragment[] = {new VideoFragment(),new SetFragment()};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // initEvent();
        //getPersimmions();
        //init();
        initView();
    }

    private void initView() {
        layoutInflater = LayoutInflater.from(this);
        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        //  mViewPager = (ViewPager) findViewById(R.id.view_pager);
        // mFragmentList = new ArrayList<Fragment>();
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content);
        mTabHost.getTabWidget().setDividerDrawable(null);
        //  mViewPager.setOffscreenPageLimit(2);
        for (int i = 0; i < fragmentArray.length; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            // mTabHost.getTabWidget().getChildAt(i).
            // mFragmentList.add(mFragment[i]);
        }
        //
      /*  mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });*/

    }


    /* private void initEvent(){
         mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
             @Override
             public void onTabChanged(String tabId) {
                 mViewPager.setCurrentItem(mTabHost.getCurrentTab());
             }
         });

         mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

             }

             @Override
             public void onPageSelected(int position) {
                 mTabHost.setCurrentTab(position);
             }

             @Override
             public void onPageScrollStateChanged(int state) {

             }
         });
     }*.

     /**
      * 给Tab按钮设置图标和文字
      */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        //imageView.setBackgroundResource(mImageArray[index]);
        // imageView.setBackground(mImageArray[index]);
        // Log.e("home image",mImageArray[0]+" * "+mImageArray[1]);
        imageView.setImageResource(mImageArray[index]);
        // imageView.setImageResource(R.mipmap.ic_launcher);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            super.onBackPressed();
        }else{
            getSupportFragmentManager().popBackStack();
        }
    }
}
