package com.example.lenovo.videodemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.lenovo.videodemo.global.GlobalValue;
import com.example.lenovo.videodemo.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    // private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    //定义一个布局
    private LayoutInflater layoutInflater;
    //Tab选项卡的文字
    private String mTextviewArray[] = {"视频", "设置"};
    private int mImageArray[] = {R.drawable.video_selector, R.drawable.set_selector};
    private Class fragmentArray[] = {VideoFragment.class, SetFragment.class};
    // private Fragment mFragment[] = {new VideoFragment(),new SetFragment()};
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initEvent();
        getPersimmions();
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
        for (int i = 0; i < 2; i++) {
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

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();

            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            } else {
                // init();
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SDK_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("permission", "success");
                    //init();

                } else {
                    Log.e("permission", "fail");
                    DialogUtil.showDialog(this, GlobalValue.PERMISSIONS_TIPS);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
