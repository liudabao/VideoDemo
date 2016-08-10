package com.example.administrator.videotest;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.administrator.videotest.global.GlobalContext;
import com.example.administrator.videotest.global.GlobalValue;
import com.example.administrator.videotest.util.DialogUtil;
import com.example.administrator.videotest.util.ImageUtil;

import java.util.ArrayList;

public class LogoActivity extends Activity {

    private ImageView iv_ad;
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_logo);
        getPersimmions();

    }

    private void initView(){
        iv_ad=(ImageView)findViewById(R.id.iv_ad);
        //ImageUtil.display(iv_ad,R.mipmap.ic_launcher);
        ImageUtil.display(iv_ad, GlobalValue.LOGO_URL);
        onScaleWidth(iv_ad);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent intent=new Intent(LogoActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);


    }

    private void onScaleWidth(final View view) {
        Animator animator= AnimatorInflater.loadAnimator(GlobalContext.getContext(), R.animator.ad_scale);
        view.setPivotX(view.getX()+view.getWidth()/2);
        view.setPivotY(view.getY()+view.getHeight()/2);
        view.invalidate();
        animator.setTarget(view);
        animator.start();
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
                initView();
            }
        }
        else{
            initView();
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
                    initView();

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
}
