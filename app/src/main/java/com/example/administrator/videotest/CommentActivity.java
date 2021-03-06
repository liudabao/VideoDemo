package com.example.administrator.videotest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class CommentActivity extends AppCompatActivity {

    private Button send;
    private ImageButton back;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_comment);
        initView();
    }

    private void initView(){
        send=(Button)findViewById(R.id.send);
        editText=(EditText)findViewById(R.id.comments);
        back=(ImageButton)findViewById(R.id.comment_back) ;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data=new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:790175007@qq.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "播放器改进");
                data.putExtra(Intent.EXTRA_TEXT, editText.getText());
                startActivity(data);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
