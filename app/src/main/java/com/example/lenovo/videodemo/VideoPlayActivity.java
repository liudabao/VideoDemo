package com.example.lenovo.videodemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class VideoPlayActivity extends AppCompatActivity implements
        SurfaceHolder.Callback, View.OnClickListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {
    /** Called when the activity is first created. */
    private MediaPlayer player;
    private  SurfaceView surface;
    private SurfaceHolder surfaceHolder;
    private ImageButton start;
    private ImageButton next;
    private ImageButton prev;
    private ImageButton back;
    private SeekBar seekBar;
    private TextView textView;
    private ProgressBar progressBar;
    private String time;
    private boolean flag;
    private boolean isFinish=false;
    private String url;
    private String nextUrl;
    private String prevUrl;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_play);
        //getPersimmions();
        Bundle bundle=getIntent().getExtras();
        url=bundle.getString("url");
        nextUrl=bundle.getString("next");
        prevUrl=bundle.getString("prev");
        Log.e("URL",url+"#"+nextUrl+"#"+prevUrl);
        initView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void initView() {
        relativeLayout=(RelativeLayout)findViewById(R.id.video_bottom);
        linearLayout=(LinearLayout)findViewById(R.id.video_top);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        textView=(TextView)findViewById(R.id.text);
        seekBar=(SeekBar)findViewById(R.id.progress);
        start=(ImageButton)findViewById(R.id.start);
        next=(ImageButton)findViewById(R.id.next);
        prev=(ImageButton)findViewById(R.id.prev);
        back=(ImageButton)findViewById(R.id.back);
        surface = (SurfaceView) findViewById(R.id.view);
        surfaceHolder = surface.getHolder(); // SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(this); // 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this

        start.setOnClickListener(this);
        if (nextUrl==null){
            next.setClickable(false);
        }
        if(prevUrl==null){
            prev.setClickable(false);
        }
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        back.setOnClickListener(this);
        //fast.setOnClickListener(this);
       // capture.setOnClickListener(this);
       // pause.setOnClickListener(this);
       // stop.setOnClickListener(this);

        //progressBar.setVisibility(View.VISIBLE);

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        Log.e("video", "media init");
       // progressBar.setVisibility(View.VISIBLE);
        player = new MediaPlayer();
        play();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(linearLayout.getVisibility()==View.GONE){
                    linearLayout.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                }
                else {
                    linearLayout.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                    seekBar.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v==start){
            if(!player.isPlaying()){
                Log.e("video", "start");
                if(!isFinish){
                    player.start();
                    //start.setText("pause");
                    start.setBackgroundResource(R.drawable.pause);
                }
                else {
                    play();
                    isFinish=false;
                }

            }
            else{
                Log.e("video", "pause");
                player.pause();
                start.setBackgroundResource(R.drawable.play);
                //start.setText("start");
            }

        }
        else if(v==next){

            url=nextUrl;
            play();
        }
        else if(v==prev){
            url=prevUrl;
            play();
        }
        else if(v==back){
            player.stop();
            finish();
        }
        /*if(v==fast){
            Log.e("video", "fast");
            seekBar.setProgress(player.getCurrentPosition()+5000);
            player.seekTo(player.getCurrentPosition()+5000);
            //seekBar.setProgress(player.getCurrentPosition()+2000);
        }*/

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("video", "video end");
        start.setBackgroundResource(R.drawable.play);
       // player.start();
       // start.setText("start");
       // textView.setText(getShowTime(0) + "/" +time);
      //  seekBar.setProgress(0);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("video", "prepare start");
        progressBar.setVisibility(View.GONE);
        player.start();
      //  start.setText("pause");
        surfaceHolder.setKeepScreenOn(true);
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setProgress(0);
        seekBar.setMax(player.getDuration());
        time=getShowTime(player.getDuration());
        textView.setText("00:00:00/" + time);
        //capture.setOnClickListener(VideoPlayActivity.this);
        seekBar.setOnSeekBarChangeListener(VideoPlayActivity.this);
        flag=true;
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (flag){
                    if (null != VideoPlayActivity.this.player
                            &&  VideoPlayActivity.this.player.isPlaying()) {
                        seekBar.setProgress(player.getCurrentPosition());

                        if(seekBar.getProgress()==seekBar.getMax()){
                            flag=false;
                            isFinish=true;
                        }
                    }
                }
            }
        }).start();

    }

    private void play(){
        player.reset();
        player.setOnCompletionListener(VideoPlayActivity.this);
        player.setOnPreparedListener(VideoPlayActivity.this);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);
        // 设置显示视频显示在SurfaceView上
        try {
            //File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Download/test.mp4");
            Log.e("play url",url);
            File file=new File(url);
            //player.setDataSource("http://101.200.164.87:8080/visa/video/1.f4v");
            if(file.exists()){
                Log.e("path",file.getPath());
                player.setDataSource(file.getPath());
                player.prepare();
                Log.e("media","prepare");
            }

            // player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getShowTime(long milliseconds) {
        // 获取日历函数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if(fromUser){
            player.seekTo(progress);
            if(progress==seekBar.getMax()){
                isFinish=true;
                start.setBackgroundResource(R.drawable.play);
            }
        }
        textView.setText(getShowTime(progress) + "/" +time);


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        //super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e("video","destroy");
        flag=false;
        if(player!=null){
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
        }

        // Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
    }
}
