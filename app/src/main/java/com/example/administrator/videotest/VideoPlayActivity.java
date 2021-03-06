package com.example.administrator.videotest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.videotest.entity.Video;
import com.example.administrator.videotest.global.GlobalValue;
import com.example.administrator.videotest.ui.VerticalSeekBar;
import com.example.administrator.videotest.util.DbUtil;
import com.example.administrator.videotest.util.DlnaUtil;
import com.example.administrator.videotest.util.MediaUtil;

import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;

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
    private ImageButton tv;
    private SeekBar seekBar;
    private TextView textView;
    private ProgressBar progressBar;
    private RelativeLayout surfaceLayout;
    private RelativeLayout tvLayout;
    private Button cancle;
    private String time;
    private boolean flag;
    private boolean isFinish=false;
    private RelativeLayout topLinearLayout;
    private RelativeLayout bottomRelativeLayout;
    private Video video;
    private DbUtil dbUtil;
    private GestureDetector detector;
    public final static int FLING_MIN_DISTANCE=100;
    private AudioManager audioManager;
    private int maxVum;
    private int currentVum;
    float oldX = 0;
    float oldY = 0;
    int windowWidth;
    int windowHeight;
    private LinearLayout volumeLayout;
    private VerticalSeekBar volumeBar;
    //private boolean isShow=true;
    private boolean isChangeProgress=false;
    private boolean isChangeVolume=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_play);

        initData();
        initView();
        initEvent();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void initData(){
        dbUtil=new DbUtil();
        Bundle bundle=getIntent().getExtras();
        Log.e("TAG",bundle.getString(GlobalValue.VIDEO_KEY));
        video=dbUtil.queryByName(GlobalValue.TABLE, bundle.getString(GlobalValue.VIDEO_KEY));
        //video=(Video)bundle.getSerializable(GlobalValue.KEY);
        Display disp = getWindowManager().getDefaultDisplay();
        windowWidth = disp.getWidth();
        windowHeight = disp.getHeight();
    }

    private void initView() {


        bottomRelativeLayout=(RelativeLayout)findViewById(R.id.video_bottom);
        topLinearLayout=(RelativeLayout)findViewById(R.id.video_top);
        volumeLayout=(LinearLayout)findViewById(R.id.volume_layout);
        volumeBar=(VerticalSeekBar)findViewById(R.id.volume_seek);
        //progressBar=(ProgressBar)findViewById(R.id.progressBar);
        textView=(TextView)findViewById(R.id.text);
        seekBar=(SeekBar)findViewById(R.id.progress);
        start=(ImageButton)findViewById(R.id.start);
        next=(ImageButton)findViewById(R.id.next);
        prev=(ImageButton)findViewById(R.id.prev);
        back=(ImageButton)findViewById(R.id.back);
        tv=(ImageButton)findViewById(R.id.image_tv) ;
        if(DlnaUtil.getInstance().getSelectedDevice()!=null){
            tv.setVisibility(View.VISIBLE);
        }
        else {
            tv.setVisibility(View.GONE);
        }
        surface = (SurfaceView) findViewById(R.id.view);
        surfaceHolder = surface.getHolder(); // SurfaceHolder是SurfaceView的控制接口
        //surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        maxVum=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVum=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeBar.setMax(maxVum);
        volumeBar.setProgress(currentVum);
        volumeLayout.setVisibility(View.GONE);
    }

    private void initEvent(){
        surfaceHolder.addCallback(this); // 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
        seekBar.setOnSeekBarChangeListener(this);
        start.setOnClickListener(this);
        if (video.getNextUrl()==null){
            next.setEnabled(false);
            next.setBackgroundResource(R.drawable.next_back);
        }
        if(video.getPrevUrl()==null){
            prev.setEnabled(false);
            prev.setBackgroundResource(R.drawable.prev_back);
        }
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        back.setOnClickListener(this);
        //progressBar.setVisibility(View.VISIBLE);
        tv.setOnClickListener(this);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        super.onTouchEvent(event);
        //currentVum=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //detector.onTouchEvent(event);
        Boolean isShow=true;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                oldX=event.getX();
                oldY=event.getY();
                /*if(topLinearLayout.getVisibility()==View.GONE){
                    topLinearLayout.setVisibility(View.VISIBLE);
                    bottomRelativeLayout.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    //isShow=false;
                }
                else {
                    topLinearLayout.setVisibility(View.GONE);
                    bottomRelativeLayout.setVisibility(View.GONE);
                    seekBar.setVisibility(View.GONE);
                }*/
               // showTopBottom();
                isChangeVolume=false;
                isChangeProgress=false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getY()-oldY>FLING_MIN_DISTANCE){
                    //Log.e("上下滑动","下滑"+event.getY()+" "+oldY+" "+windowHeight);
                   // hideTopBottom();
                    volumeLayout.setVisibility(View.VISIBLE);
                    vumChange((oldY-event.getY())/windowHeight);
                    isChangeVolume=true;
                }
                else if(oldY-event.getY()>FLING_MIN_DISTANCE){
                    //Log.e("上下滑动","上滑"+event.getY()+" "+oldY+" "+windowHeight);
                    //hideTopBottom();
                    volumeLayout.setVisibility(View.VISIBLE);
                    vumChange((oldY-event.getY())/windowHeight);
                    isChangeVolume=true;
                }
                else if(event.getX()-oldX>FLING_MIN_DISTANCE){
                    showTopBottom();
                    isChangeProgress=true;
                }
                else if(oldX-event.getX()>FLING_MIN_DISTANCE){
                    showTopBottom();
                    isChangeProgress=true;
                }
               // isShow=true;
                break;
            case MotionEvent.ACTION_UP:
                if(event.getX()-oldX>FLING_MIN_DISTANCE){
                    Log.e("左右滑动","右滑"+event.getX()+" "+oldX);
                    progressChange(5000);
                }
                else if(oldX-event.getX()>FLING_MIN_DISTANCE){
                    Log.e("左右滑动","左滑");
                    progressChange(-5000);
                }
                currentVum=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volumeLayout.setVisibility(View.GONE);
                Log.e("TAG", isChangeProgress+" "+isChangeVolume);
                if(!isChangeProgress&&!isChangeVolume){
                    //showTopBottom();
                    //hideTopBottom();
                    if(topLinearLayout.getVisibility()==View.GONE){
                        topLinearLayout.setVisibility(View.VISIBLE);
                        bottomRelativeLayout.setVisibility(View.VISIBLE);
                        seekBar.setVisibility(View.VISIBLE);
                        //isShow=false;
                    }
                    else {
                        topLinearLayout.setVisibility(View.GONE);
                        bottomRelativeLayout.setVisibility(View.GONE);
                        seekBar.setVisibility(View.GONE);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        Log.e("video", "media init");
       // showProgressBar();
       // progressBar.setVisibility(View.VISIBLE);
        player = new MediaPlayer();
        play();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

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
        else if(v==tv){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("tv","play");
                    tvPlay();
                    //showTv();
                }
            }).start();
            showTv();
            start();
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
        //hideProgressBar();
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
       // Log.e("time", textView.getText().toString());
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
            next.setBackgroundResource(R.drawable.next_back);
        }
        else {
            next.setEnabled(true);
            next.setBackgroundResource(R.drawable.next);
        }
        if(video.getPrevUrl()==null){
            prev.setEnabled(false);
            prev.setBackgroundResource(R.drawable.prev_back);
        }
        else {
            prev.setEnabled(true);
            prev.setBackgroundResource(R.drawable.prev);
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
             prev.setBackgroundResource(R.drawable.prev_back);
        }
        else {
            prev.setEnabled(true);
            prev.setBackgroundResource(R.drawable.prev);
        }
        if(video.getNextUrl()==null){
            next.setEnabled(false);
            next.setBackgroundResource(R.drawable.next_back);
        }
        else {
            next.setEnabled(true);
            next.setBackgroundResource(R.drawable.next);
        }
        play();
    }

    private void tvPlay(){
        Device device=DlnaUtil.getInstance().getSelectedDevice();
        Service service=device.getService("urn:schemas-upnp-org:service:AVTransport:1");
        if(service==null){
            Log.e("service", "null");
        }
        else {

            Action action=service.getAction("SetAVTransportURI");
            if(action==null){
                Log.e("action", "null");
            }
            else {
                String path="http://"+HostInterface.getIPv4Address()+ ":8080"+ video.getUrl();
                Log.e("action path",path);
                action.setArgumentValue("InstanceID", 0);
                action.setArgumentValue("CurrentURI", path);
                action.setArgumentValue("CurrentURIMetaData", 0);
                if (!action.postControlAction()) {
                    Log.e("action", "false");
                }
                else{
                    Log.e("action", "ok");
                    Action playAction=service.getAction("Play");
                    playAction.setArgumentValue("InstanceID", 0);
                    playAction.setArgumentValue("Speed", "1");
                    if(!playAction.postControlAction()){
                        Log.e("play action", "false");
                    }
                    else {
                        Log.e("play action", "ok");
                    }
                }
            }
        }
    }

    private void tvStop(){
        Device device=DlnaUtil.getInstance().getSelectedDevice();
        Service service=device.getService("urn:schemas-upnp-org:service:AVTransport:1");
        if(service==null){
            Log.e("stop service", "null");
        }
        else {

            Action action=service.getAction("Stop");
            if(action==null){
                Log.e("stop action", "null");
            }
            else{
                action.setArgumentValue("InstanceID",0);
                if (!action.postControlAction()) {
                    Log.e("stop action", "false");
                }
            }

        }
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

    private void vumChange(float percent){

        int index = (int) (percent * maxVum) + currentVum;
        if (index > maxVum)
            index = maxVum;
        else if (index < 0)
            index = 0;
        Log.e("volum",index+" "+maxVum+" "+currentVum+" "+percent);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        volumeBar.setProgress(index);
    }

    private void progressChange(int seconds){

        boolean flag=false;
        if(player.isPlaying()){
            flag=true;
            player.pause();
        }
        int position=player.getCurrentPosition()+seconds;
        video.setPosition(position);
        player.seekTo(position);
        if(flag){
            player.start();
        }
        else {
            player.start();
            player.pause();
        }

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

    private void showTv(){
        tvLayout=(RelativeLayout) findViewById(R.id.tv_layout);
        cancle=(Button)findViewById(R.id.tv_cancle);
        tvLayout.setVisibility(View.VISIBLE);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        tvStop();
                    }
                }).start();
                tvLayout.setVisibility(View.GONE);

            }
        });
        //surfaceviewLayout=(RelativeLayout)findViewById(R.id.surfaceviewLayout);
       // RelativeLayout.LayoutParams lParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
       // lParams.= Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
       // lParams.addRule(RelativeLayout.CENTER_IN_PARENT);
       // progressBar=new ProgressBar(GlobalContext.getContext());
       // progressBar.setVisibility(View.VISIBLE);
        //surfaceviewLayout.setVisibility(View.INVISIBLE);
       // progressBar.setLayoutParams(lParams);
        //progressLayout.addView(progressBar);
    }


    private void showTopBottom(){
        if(topLinearLayout.getVisibility()==View.GONE){
            topLinearLayout.setVisibility(View.VISIBLE);
            bottomRelativeLayout.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            //isShow=false;
        }
    }

    private void hideTopBottom(){
        if(topLinearLayout.getVisibility()==View.VISIBLE) {
            topLinearLayout.setVisibility(View.GONE);
            bottomRelativeLayout.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
        }
    }

}
