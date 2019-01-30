package com.example.tathagat.arduino4;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

public class Main2Activity extends AppCompatActivity {
    private MediaController mediacontroller;
    private Uri uri;
    private VideoView vv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        stopService(new Intent(this,MyService.class));


    }
    public void playimg(View v){
        //Switch sw =findViewById(R.id.sw);
        //sw.setChecked(false);
        WebView wb =(WebView) findViewById(R.id.wb);
        String piAddr = "http://192.168.43.135:5000";
        wb.loadUrl(piAddr);

    }
}
