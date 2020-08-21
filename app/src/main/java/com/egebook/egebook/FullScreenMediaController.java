package com.egebook.egebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class FullScreenMediaController extends MediaController {

    private ImageButton fullScreen;
    private boolean isFullScreen;
    SharedPreferences sPref;

    public FullScreenMediaController(Context context, boolean isFullScreen) {
        super(context);
        this.isFullScreen = isFullScreen;
    }

    @Override
    public void setAnchorView(View view) {

        super.setAnchorView(view);

        //image button for full screen to be added to media controller
        fullScreen = new ImageButton (super.getContext());

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.rightMargin = 80;
        addView(fullScreen, params);

        if("y".equals(isFullScreen)){
            fullScreen.setImageResource(R.drawable.fullscreen);
        }else{
            fullScreen.setImageResource(R.drawable.fullscreen);
        }

        //add listener to image button to handle full screen and exit full screen events
        fullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = ((VideoView)((Activity) getContext()).getCurrentFocus().findViewById(R.id.videoView)).getCurrentPosition();
                Log.d("media controller msec", a + "");
                if(isFullScreen){
                    Intent i = new Intent(((Activity) getContext()), VideoActivity.class);
                    i.putExtra("msec", a);
                    i.putExtra("fullScreen", isFullScreen);
                    ((Activity) getContext()).startActivity(i);
                    ((VideoView)((Activity) getContext()).getCurrentFocus().findViewById(R.id.videoView)).seekTo(a);
                    isFullScreen = false;
                    ((Activity) getContext()).finish();

                }else{
                    Intent i = new Intent(((Activity) getContext()), FullScreenVideoActivity.class);
                    i.putExtra("msec", a);
                    i.putExtra("fullScreen", isFullScreen);
                    ((Activity) getContext()).startActivity(i);
                    ((VideoView)((Activity) getContext()).getCurrentFocus().findViewById(R.id.videoView)).seekTo(a);
                    //((FullScreenVideoActivity) getContext()).getCurrentFocus().findViewById(R.id.videoView).getLayoutParams();
                    /*android.view.ViewGroup.LayoutParams params = ((FullScreenVideoActivity) getContext()).getCurrentFocus().findViewById(R.id.videoView).getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    ((FullScreenVideoActivity) getContext()).getCurrentFocus().findViewById(R.id.videoView).setLayoutParams(params);*/
                    isFullScreen = true;
                    ((Activity) getContext()).finish();
                }
            }
        });
    }
}