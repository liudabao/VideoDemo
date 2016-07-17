package com.example.administrator.videotest;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.videotest.entity.Video;
import com.example.administrator.videotest.global.GlobalContext;
import com.example.administrator.videotest.global.GlobalValue;
import com.example.administrator.videotest.util.DbUtil;
import com.example.administrator.videotest.util.MediaUtil;

import java.io.File;

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
    private RelativeLayout progressLayout;
    private String time;
    private boolean flag;
    private boolean isFinish=false;
    private LinearLayout topLinearLayout;
    private RelativeLayout bottomRelativeLayout;
    private Video video;
    private DbUtil dbUtil;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_play);
        showProgressBar();
        Bundle bundle=getIntent().getExtras();
        video=(Video)bundle.getSerializable(GlobalValue.KEY);
        initView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void initView() {
        dbUtil=new DbUtil();
        bottomRelativeLayout=(RelativeLayout)findViewById(R.id.video_bottom);
        topLinearLayout=(LinearLayout)findViewById(R.id.video_top);
        //progressBar=(ProgressBar)findViewById(R.id.progressBar);
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
        if (video.getNextUrl()==null){
            next.setEnabled(false);
            //next.setBackgroundResource(R.drawable.next_back);
        }
        if(video.getPrevUrl()==null){
            prev.setEnabled(false);
            //prev.setBackgroundResource(R.drawable.prev_back);
        }
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        back.setOnClickListener(this);
        //progressBar.setVisibility(View.VISIBLE);
        seekBar.setOnSeekBarChangeListener(this);

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
                if(topLinearLayout.getVisibility()==View.GONE){
                    topLinearLayout.setVisibility(View.VISIBLE);
                    bottomRelativeLayout.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                }
                else {
                    topLinearLayout.setVisibility(View.GONE);
                    bottomRelativeLayout.setVisibility(View.GONE);
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
            start();
        }
        else if(v==next){
           next();
        }
        else if(v==prev){
           prev();
        }
        else if(v==back){
           back();
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("video", "video end");
        start.setBackgroundResource(R.drawable.play);
        update(video);
    }

    private void play(){
        Log.e("play","start");
        player.reset();
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);
        start.setBackgroundResource(R.drawable.pause);
        // 设置显示视频显示在SurfaceView上
        try {
            Log.e("play url",video.getUrl());
            File file=new File(video.getUrl());
            if(file.exists()){
                player.setDataSource(file.getPath());
                player.prepare();
                Log.e("media","prepare");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("video","prepare start");
        hideProgressBar();
       // Log.e("video", "prepare start "+video.getPosition());
        //progressBar.setVisibility(View.GONE);
        player.start();
        surfaceHolder.setKeepScreenOn(true);
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setProgress(video.getPosition());
        player.seekTo(video.getPosition());
        seekBar.setMax(player.getDuration());
        time= MediaUtil.getShowTime(player.getDuration());
        textView.setText("00:00:00/" + time);
        flag=true;
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (flag){
                    if (null != VideoPlayActivity.this.player
                            &&  VideoPlayActivity.this.player.isPlaying()) {
                        seekBar.setProgress(player.getCurrentPosition());
                        video.setPosition(player.getCurrentPosition());
                        if(seekBar.getProgress()==seekBar.getMax()){
                            flag=false;
                            isFinish=true;
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if(fromUser){
            player.seekTo(progress);
            video.setPosition(player.getCurrentPosition());
            if(progress==seekBar.getMax()){
                isFinish=true;
                start.setBackgroundResource(R.drawable.play);
            }
        }
        textView.setText( MediaUtil.getShowTime(progress) + "/" +time);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void update(Video video){
        dbUtil=new DbUtil();
        if(dbUtil.isExist(GlobalValue.TABLE, video.getName())){
            Log.e("update", "update");
            dbUtil.update(video, GlobalValue.TABLE);
        }
        else {
            Log.e("update","insert");
            dbUtil.insert(video, GlobalValue.TABLE);
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        back();
    }

    private void start(){
        if(!player.isPlaying()){
            Log.e("video", "start");
            if(!isFinish){
                player.start();
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
            update(video);
        }

    }

    private void next(){
        if(player.isPlaying()){
            player.stop();
        }
        update(video);
        Log.e("next", video.getNextUrl());
        video=dbUtil.queryByUrl(GlobalValue.TABLE, video.getNextUrl());
        if(video.getNextUrl()==null){
            next.setEnabled(false);
            //next.setBackgroundResource(R.drawable.next_back);
        }
        else {
            next.setEnabled(true);
            // next.setBackgroundResource(R.drawable.next);
        }
        if(video.getPrevUrl()==null){
            prev.setEnabled(false);
            // prev.setBackgroundResource(R.drawable.prev_back);
        }
        else {
            prev.setEnabled(true);
            // prev.setBackgroundResource(R.drawable.prev);
        }
        play();
    }

    private void prev(){
        if(player.isPlaying()){
            player.stop();
        }
        update(video);
        video=dbUtil.queryByUrl(GlobalValue.TABLE, video.getPrevUrl());
        if(video.getPrevUrl()==null){
            prev.setEnabled(false);
            // prev.setBackgroundResource(R.drawable.prev_back);
        }
        else {
            prev.setEnabled(true);
            // prev.setBackgroundResource(R.drawable.prev);
        }
        if(video.getNextUrl()==null){
            next.setEnabled(false);
            //next.setBackgroundResource(R.drawable.next_back);
        }
        else {
            next.setEnabled(true);
            //next.setBackgroundResource(R.drawable.next);
        }
        play();
    }

    private void back(){
        if(player!=null){
            if (player.isPlaying()) {
                player.stop();
            }
        }
        Intent intent=new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e("video","pause");
        if(player!=null){
            if (player.isPlaying()) {
                player.stop();
            }
        }
        update(video);
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
    }

    private void showProgressBar(){
        progressLayout=(RelativeLayout) findViewById(R.id.progress_layout);
        RelativeLayout.LayoutParams lParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
       // lParams.= Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
        lParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar=new ProgressBar(GlobalContext.getContext());
        progressBar.setVisibility(View.VISIBLE);
        //surfaceviewLayout.setVisibility(View.INVISIBLE);
        progressBar.setLayoutParams(lParams);
        progressLayout.addView(progressBar);
    }

    private void hideProgressBar(){
        progressLayout.setVisibility(View.INVISIBLE);
        //surfaceviewLayout.setVisibility(View.VISIBLE);
    }

}
