package com.egebook.egebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.MediaController;
import android.widget.VideoView;

public class FullScreenVideoActivity extends AppCompatActivity {
    SharedPreferences sPref;
    int a;
    private MediaController mediaController;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        videoView = (findViewById(R.id.videoView));
        Intent i = getIntent();
        try {
            a = i.getIntExtra("msec", 0);
        }
        catch (java.lang.NumberFormatException e) {
            e.printStackTrace();
            a = 0;
        }
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"/*+R.raw.test*/);
        videoView.setVideoURI(uri);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus(0);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        videoView.setLayoutParams(new ConstraintLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));
        /*videoView.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        videoView.layout(videoView.getLeft(), videoView.getTop(), 1920, 1080);
        videoView.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        videoView.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;*/
        mediaController = new FullScreenMediaController(this, i.getBooleanExtra("isFullScreen", true));
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.seekTo(a+400);
        videoView.start();
    }

    @Override
    protected void onDestroy() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("msec", String.valueOf(videoView.getCurrentPosition()));
        ed.commit();
        super.onDestroy();
    }
}
