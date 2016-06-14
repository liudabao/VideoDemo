package com.example.lenovo.videodemo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {


    Button start;
    Button pause;
    Button stop;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    private void init(){
        videoView=(VideoView)findViewById(R.id.view);
        start=(Button)findViewById(R.id.start);
        pause=(Button)findViewById(R.id.pause);
        stop=(Button)findViewById(R.id.stop);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                compelete();
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                start();
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= Uri.parse("http://101.200.164.87:8080/visa/video/1.f4v");
                videoView.setVideoURI(uri);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });

    }

    private void compelete(){
        Log.e("video", "finish");
    }

    private void start(){
        Log.e("video", "start");
        videoView.start();
    }

    public void click(){
        if(videoView.isPlaying()){
            Log.e("video", "pause");
            videoView.pause();
        }
        else{
            Log.e("video", "start");
            videoView.start();
        }
    }

}
