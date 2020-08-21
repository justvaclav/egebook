package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class VideoActivity extends AppCompatActivity {

    private final String TAG = "VideoActivity";
    TextView fullScreen;
    private MediaController mediaController;
    WebView videoView;
    SharedPreferences sPref;
    int a, themeNum, lesson, unit;
    ArrayList<ArrayList<String>> themes = new ArrayList<ArrayList<String>>();
    ArrayList<String> themesDeux = new ArrayList<>();
    ArrayList<String> themesTrois = new ArrayList<>();
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent ii = getIntent();
        int i = ii.getIntExtra("lesson", 0);
        lesson = ii.getIntExtra("lesson", 0);
        themeNum = ii.getIntExtra("theme", 0);
        unit = ii.getIntExtra("unit", 0);
        Log.d("rw intent getextra", "lesson: " + i + ", theme: " + themeNum);
        try {
            a = ii.getIntExtra("msec", 0);
        }
        catch (java.lang.NumberFormatException e) {
            e.printStackTrace();
            a = 0;
        }
        videoView = findViewById(R.id.videoView);
        //videoView.setRotation(90);
        fullScreen = findViewById(R.id.themesTextView);
        /*Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.test);
        videoView.setVideoURI(uri);*/
        final RecyclerView rw = findViewById(R.id.videosRecyclerView);
        /*final ArrayList<String> themes = new ArrayList<>();
        themes.add("Конспекты");
        themes.add("Тесты");*/
        final ArrayList<String> themes2 = new ArrayList<>();
        videoView.setWebViewClient(new BrowserHome() {
        } );
        videoView.setWebChromeClient(new MyChrome());
        videoView.getSettings().setJavaScriptEnabled(true);
        videoView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        videoView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        videoView.getSettings().setAppCacheEnabled(true);
        TextView fullScreen = findViewById(R.id.themesTextView);
        fullScreen.setVisibility(View.GONE);
        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if(savedInstanceState==null){
            videoView.post(() -> loadWebsite());
        }
        DocumentReference docRef = db.collection("Courses").document(themeNum+"");
        Log.d("rw docRef path", docRef.getPath() + " lesson:" + i);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Object o = document.getData();
                        Theme theme = document.toObject(Theme.class);
                        Log.d(TAG, "DocumentSnapshot data: " + theme.toString());
                        Log.d("themeLessonInfo size", theme.getLessonInfo().size() + "");
                        ArrayList<Map<String, Object>> adds = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit).get("additionalSectionsData");
                        Map<String, Object> mapAdd = adds.get(lesson);
                        Map<String, Object> mapContent = (Map<String, Object>) mapAdd.get("content");
                        ArrayList<Map<String, Object>> notesTitles = (ArrayList<Map<String, Object>>) mapContent.get("notes");
                        ArrayList<Map<String, Object>> testsTitles = (ArrayList<Map<String, Object>>) mapContent.get("tests");

                        for (int i = 0; i < 2; i++) {
                            ArrayList<String> array = new ArrayList<>();
                            if (i == 0) {array.add("Конспекты");
                                for (int j = 0 ; j < notesTitles.size(); j++) {
                                    array.add(notesTitles.get(j).get("noteTitle").toString());
                                }
                            }
                            else {array.add("Тесты");
                                for (int j = 0 ; j < testsTitles.size(); j++) {
                                    array.add(testsTitles.get(j).get("title").toString());
                                }
                            }
                            themes.add(array);
                        }

                        Map<String, Object> videos = (Map<String, Object>) mapContent.get("videosURL");
                        String url;
                        try {
                            url = videos.get("videoWithNormalCastingURL").toString();

                            Log.d(TAG, url);
                            videoView.loadData( new YouTubeVideo("<iframe src=\""+url+"\" width=\"100%\" height=\"100%\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>").getVideoUrl(), "text/html" , "utf-8" );
                        }
                        catch (NullPointerException e) {
                            url = "https://www.youtube.com/embed/dQw4w9WgXcQ";
                            videoView.loadData( new YouTubeVideo("<iframe src=\""+url+"\" width=\"100%\" height=\"100%\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>").getVideoUrl(), "text/html" , "utf-8" );
                        }
                        //videoView.loadData( new YouTubeVideo("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+ url+"\" frameborder=\"0\" allowfullscreen></iframe>").getVideoUrl(), "text/html" , "utf-8" );
                        //videoView.loadData( new YouTubeVideo("<iframe width=\"100%\" height=\"100%\" src=\""+ url+"\" frameborder=\"0\" allowfullscreen></iframe>").getVideoUrl(), "text/html" , "utf-8" );
                        //videoView.loadData( new YouTubeVideo("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/"+ url+"\" frameborder=\"0\" allowfullscreen></iframe>").getVideoUrl(), "text/html" , "utf-8" );

                        //videoView.loadUrl();
                        //videoView.loadData( new YouTubeVideo("<iframe src=\"https://player.vimeo.com/video/" + 383012865 +"\" width=\"100%\" height=\"100%\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>").getVideoUrl(), "text/html" , "utf-8" );
                        //videoView.loadData( new YouTubeVideo("<iframe src=\"https://www.youtube.com/embed/"+url+"\" width=\"100%\" height=\"100%\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>").getVideoUrl(), "text/html" , "utf-8" );
                        Log.d(TAG, "notesTitles.size() " + notesTitles.size());
                        for (int j = 0; j < notesTitles.size(); j++) {
                            themesDeux.add(notesTitles.get(j).get("noteTitle").toString());
                            Log.d(i + " " + j, notesTitles.get(j).get("noteTitle").toString());
                        }
                        for (int j = 0; j < testsTitles.size(); j++) {
                            themesTrois.add(testsTitles.get(j).get("title").toString());
                            Log.d(i + " " + j, testsTitles.get(j).get("title").toString());
                        }
                        /*videoView.setVideoPath("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
                        videoView.setMediaController(new MediaController(getApplicationContext()));
                        videoView.requestFocus(0);
                        mediaController = new FullScreenMediaController(getApplicationContext(), ii.getBooleanExtra("isFullScreen", false));
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                        videoView.seekTo(a+400);
                        videoView.start();*/
                        rw.setHasFixedSize(true);
                        rw.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        mAdapter = new VideoAdapterDeux(themes, R.layout.list_video_element, themeNum, unit, lesson);
                        //mAdapter = new VideoAdapter(themeNum, unit, lesson, themes, themesDeux, themesTrois, R.layout.list_video_element);
                        rw.setAdapter(mAdapter);
                        /*rw.addOnItemTouchListener(
                                new RecyclerItemClickListener(getApplicationContext(), rw ,new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override public void onItemClick(View view, int position) {
                                        Log.d(TAG, "rw item clicked:" + position);
                                        if (position == 0) {
                                            Intent pdfIntent = new Intent(VideoActivity.this, PDFActivity.class);
                                            pdfIntent.putExtra("theme", themeNum);
                                            pdfIntent.putExtra("unit", unit);
                                            pdfIntent.putExtra("lesson", lesson);
                                            //pdfIntent.putExtra("conspect", rw.getChildAdapterPosition(getCurrentFocus()));
                                            //pdfIntent.setDataAndType(path, "application/pdf");
                                            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                            try {
                                                startActivity(pdfIntent);
                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(getApplicationContext(), "Can't read pdf file", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        if (position == 1) {
                                            Intent pdfIntent = new Intent(VideoActivity.this, QuestionActivity.class);
                                            pdfIntent.putExtra("theme", themeNum);
                                            pdfIntent.putExtra("unit", unit);
                                            pdfIntent.putExtra("lesson", lesson);
                                            pdfIntent.putExtra("conspect", rw.getChildAdapterPosition(getCurrentFocus()));
                                            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            startActivity(pdfIntent);
                                        }
                                    }

                                    @Override public void onLongItemClick(View view, int position) {
                                        Toast.makeText(getApplicationContext(), themes.get(position) + " EP", Toast.LENGTH_LONG).show();
                                    }
                                })
                        );*/
                        fullScreen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(VideoActivity.this, FullVideoActivity.class);
                                //intent.putExtra("url",  videos.get("videoWithNormalCastingURL").toString());
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("lesson", lesson);
                                startActivity(intent);
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with with ", task.getException());
                }
            }
        });

        String[] data = {"Озвучка", "Озвучка"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
            /*adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

            Spinner spinner = findViewById(R.id.spinner2);
            spinner.setAdapter(adapter);
            // заголовок
            spinner.setPrompt("Title");
            // выделяем элемент
            spinner.setSelection(0);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // показываем позиция нажатого элемента
                    //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
    }

    protected void onDestroy() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        //ed.putString("msec", String.valueOf(videoView.getCurrentPosition()));
        ed.commit();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        videoView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        videoView.restoreState(savedInstanceState);
    }

    private void loadWebsite() {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            videoView.loadData( new YouTubeVideo("<iframe src=\"https://player.vimeo.com/video/" + 383012865 +"\" width=\"100%\" height=\"100%\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>").getVideoUrl(), "text/html" , "utf-8" );
        } else {
            videoView.setVisibility(View.INVISIBLE);
        }
    }

    public static class MediaContent {
        public URL videoUrl;
        public ArrayList<AdminActivity.PDF> notes = new ArrayList<>();
        public ArrayList<QuizModel> tests;

        public MediaContent(ArrayList<QuizModel> tests) {
            this.tests = tests;
        }
    }

    public static class QuizModel {
        public String title;
        public ArrayList<QuizQuestionModel> questions;

        public QuizModel(String title) {
            this.title = title;
        }
    }

    public static class QuizQuestionModel {
        public boolean isOpened;
        public String correctAnswerDescription, question, correctAnswer;
        public int score;
        public ArrayList<String> answers;
    }

    private static class Theme {
        Map<String, Object> courseInfo;
        ArrayList<Map<String, Object>> lessonInfo;

        public Theme(Map<String, Object> courseInfo, ArrayList<Map<String, Object>> lessonInfo) {
            this.courseInfo = courseInfo;
            this.lessonInfo = lessonInfo;
        }

        public Theme() {}

        public Map<String, Object> getCourseInfo() {
            return courseInfo;
        }

        public void setCourseInfo(Map<String, Object> courseInfo) {
            this.courseInfo = courseInfo;
        }

        public ArrayList<Map<String, Object>> getLessonInfo() {
            return lessonInfo;
        }

        public void setLessonInfo(ArrayList<Map<String, Object>> lessonInfo) {
            this.lessonInfo = lessonInfo;
        }
    }

    class BrowserHome extends WebViewClient {

        BrowserHome() {
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setTitle(view.getTitle());
            //progressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);

        }
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}