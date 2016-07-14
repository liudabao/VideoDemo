package com.example.lenovo.videodemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.lenovo.videodemo.entity.Video;
import com.example.lenovo.videodemo.global.GlobalValue;
import com.example.lenovo.videodemo.util.DbUtil;
import com.example.lenovo.videodemo.util.MediaUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private Video video;
    private DbUtil dbUtil;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_play);
        Bundle bundle=getIntent().getExtras();
        video=(Video)bundle.getSerializable(GlobalValue.KEY);
        Log.e("video activity", video.getName()+" * "+video.getSelected());
        //video.setSelected("false");
        initView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void initView() {
        dbUtil=new DbUtil();
       // Log.e("video", video.getUrl()+" & "+video.getNextUrl()+" & "+video.getPrevUrl());
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
        if (video.getNextUrl()==null){
           // next.setClickable(false);
            next.setEnabled(false);
            //next.setBackgroundResource(R.drawable.next_back);
        }
        if(video.getPrevUrl()==null){
            //prev.setClickable(false);
            prev.setEnabled(false);
            //prev.setBackgroundResource(R.drawable.prev_back);
        }
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        back.setOnClickListener(this);
        progressBar.setVisibility(View.VISIBLE);

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
            start();
        }
        else if(v==next){
           next();
        }
        else if(v==prev){
            //url=prevUrl;
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
       // player.start();
       // start.setText("start");
       // textView.setText(getShowTime(0) + "/" +time);
      //  seekBar.setProgress(0);
    }

    private void play(){
        player.reset();
        player.setOnCompletionListener(VideoPlayActivity.this);
        player.setOnPreparedListener(VideoPlayActivity.this);
       // player.setOnErrorListener(VideoPlayActivity.this);
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

    private void start(){

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
            update(video);
            //start.setText("start");
            //update(video);
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
        //url=nextUrl;
        play();
    }

    private void prev(){

        if(player.isPlaying()){
            player.stop();
        }
        update(video);
        // dbUtil=new DbUtil();
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
        player.stop();
        Intent intent=new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("video", "prepare start "+video.getPosition());

        //progressBar.setVisibility(View.GONE);
        player.start();
      //  start.setText("pause");
        surfaceHolder.setKeepScreenOn(true);
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setProgress(video.getPosition());
        player.seekTo(video.getPosition());
        int p=player.getDuration();
        seekBar.setMax(player.getDuration());
        time= MediaUtil.getShowTime(player.getDuration());
        textView.setText("00:00:00/" + time);
        seekBar.setOnSeekBarChangeListener(VideoPlayActivity.this);
        flag=true;
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (flag){
                    if (null != VideoPlayActivity.this.player
                            &&  VideoPlayActivity.this.player.isPlaying()) {
                        seekBar.setProgress(player.getCurrentPosition());
                       // Log.e("position", player.getCurrentPosition()+"");
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

   /* private String getShowTime(long milliseconds) {
        // 获取日历函数
       // Calendar calendar = Calendar.getInstance();
       // calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
        if (milliseconds  > 3600*1000) {
            dateFormat = new SimpleDateFormat("HH:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
        }
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      //  return dateFormat.format(calendar.getTime());
        return dateFormat.format(milliseconds);
    }*/

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
        //super.onBackPressed();
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
        //update(video);
        // Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
    }

}
