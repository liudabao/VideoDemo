package com.example.lenovo.videodemo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VideoPlayActivity extends AppCompatActivity implements
        SurfaceHolder.Callback, View.OnClickListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {
    /** Called when the activity is first created. */
    MediaPlayer player;
    SurfaceView surface;
    SurfaceHolder surfaceHolder;
    Button start;
    Button fast;
    SeekBar seekBar;
    TextView textView;
    Button capture;
    ProgressBar progressBar;
    //Button pause;
    //Button stop;
    String time;
    boolean flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        initView();
    }

    private void initView() {
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        textView=(TextView)findViewById(R.id.text);
        seekBar=(SeekBar)findViewById(R.id.progress);
        start=(Button)findViewById(R.id.start);
        fast=(Button)findViewById(R.id.fast);
        capture=(Button)findViewById(R.id.capture);
        //stop=(Button)findViewById(R.id.stop);
        surface = (SurfaceView) findViewById(R.id.view);
        surfaceHolder = surface.getHolder(); // SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(this); // 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this

        start.setOnClickListener(this);
        fast.setOnClickListener(this);
        capture.setOnClickListener(this);
       // pause.setOnClickListener(this);
       // stop.setOnClickListener(this);
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        Log.e("video", "media init");

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);
        // 设置显示视频显示在SurfaceView上
        try {
            player.setDataSource("http://101.200.164.87:8080/visa/video/1.f4v");
            player.prepare();

           // player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
        // Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
    }

    @Override
    public void onClick(View v) {
        if(v==start){
            if(!player.isPlaying()){
                Log.e("video", "start");
                player.start();
                start.setText("pause");
            }
            else{
                Log.e("video", "pause");
                player.pause();
                start.setText("start");
            }

        }
        if(v==fast){
            Log.e("video", "fast");
            seekBar.setProgress(player.getCurrentPosition()+2000);
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("video", "video end");
       // player.start();
        start.setText("start");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e("video", "prepare start");
        progressBar.setVisibility(View.GONE);
        player.start();
        start.setText("pause");
        surfaceHolder.setKeepScreenOn(true);
        // 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
        seekBar.setProgress(0);
        seekBar.setMax(player.getDuration());
        time=getShowTime(player.getDuration());
        textView.setText("00:00:00/" + time);
        capture.setOnClickListener(VideoPlayActivity.this);
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
                        }
                    }
                }
            }
        }).start();

    }

    public String getShowTime(long milliseconds) {
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
        }
        textView.setText(getShowTime(progress) + "/" +time);


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
