package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivitySept extends AppCompatActivity {

    String TAG = "AdminActivitySept";
    Map<String, Object> o;
    FirebaseFirestore db;
    Theme theme;
    int unit = -1, themeNum = 0;
    ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sept);
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        String colName = getString(R.string.colName);
        Log.d(TAG, FirebaseFirestore.getInstance().toString());
        themeNum = getIntent().getIntExtra("theme", -1);
        unit = getIntent().getIntExtra("unit", -1);
        CollectionReference docCol = db.collection(colName);//.document(getIntent().getStringExtra("unit")).collection("lessonInfo");
        docCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String,Object> updates = new HashMap<>();
                    final QuerySnapshot document = task.getResult();
                    if (!document.isEmpty()) {
                        Log.d(TAG, themeNum + "/" + unit + "/");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getDocuments());
                        final List<DocumentSnapshot> documents = document.getDocuments();
                        ArrayList<String> itemsDeux = new ArrayList<>();
                        //theme = documents.get(i).toObject(Theme.class);

                        //Log.d(TAG, theme.lessonInfo.get(getIntent().getIntExtra("unit", 0)).containsValue("additionalSectionsData") + " is " + "lessoninfo size, " + getIntent().getIntExtra("unit", 0) + " is unit ");
                        ArrayList<Map<String, Object>> lesson = (ArrayList<Map<String, Object>>) documents.get(getIntent().getIntExtra("theme", 0)).get("lessonInfo");
                        ArrayList<Map<String, Object>> adds = (ArrayList<Map<String, Object>>) lesson.get(getIntent().getIntExtra("unit", 0)-1).get("additionalSectionsData");
                        int size;
                        try {
                            size = adds.size();
                        } catch (NullPointerException e) {
                            size = 0;
                        }
                        try {
                            itemsDeux.add((String) lesson.get(getIntent().getIntExtra("unit", 0) - 1).get("lessonTopic"));
                        } catch (NullPointerException e) {
                            itemsDeux.add("Повреждённый урок");
                        }
                        for (int j = 0; j < size; j++) {
                            //ArrayList<Map<String, Object>> adds = (ArrayList<Map<String, Object>>) theme.lessonInfo.get(getIntent().getIntExtra("unit", 0)).get("additionalSectionsData");
                            itemsDeux.add((String) adds.get(j).get("title"));
                        }
                        itemsDeux.add("Добавить новый урок");
                        items.add(itemsDeux);

                        final int itemsSize = items.size() - 1;
                        RecyclerView.Adapter mAdapter;
                        final RecyclerView rw = findViewById(R.id.recyclerViewAdmin);
                        rw.setHasFixedSize(true);
                        rw.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        mAdapter = new AdminAdapterDeux(items, themeNum, unit, R.layout.list_theme_element);
                        rw.setAdapter(mAdapter);
                        /*rw.addOnItemTouchListener(
                                new RecyclerItemClickListener(getApplicationContext(), rw ,new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override public void onItemClick(View view, int position) {
                                        if (position != items.size()-1) {
                                            Intent i = new Intent(AdminActivity.this, AdminActivityTrois.class);
                                            startActivity(i);
                                        }
                                        else {
                                            Map<String, Object> map = new HashMap<>();
                                            ArrayList<Map<String, Object>> arr1 = new ArrayList<Map<String, Object>>();
                                            ArrayList<QuizModel> tests = new ArrayList<>();
                                            Map<String, Object> map2 = new HashMap<>();
                                            ArrayList<LessonContent> arr2 = new ArrayList<>();
                                            map.put("title", "Отладка админ-панели");
                                            map.put("isAvailable", true);
                                            map.put("description", "qwerty");
                                            map.put("courseGradientImage", "Розовый градиент");
                                            //map2.put("opened", true);//
                                            map2.put("lessonTopic", "Политика");//
                                            //map2.put("mainSectionGradientImage", "gradientPurpleCell");
                                            LessonContent content = new LessonContent("Теория происхождения человека", "", new MediaContent(tests));
                                            arr2.add(content);
                                            arr2.add(new LessonContent("2", "", new MediaContent(tests)));
                                            arr2.add(new LessonContent("3", "", new MediaContent(tests)));
                                            arr2.add(new LessonContent("4", "", new MediaContent(tests)));
                                            arr2.add(new LessonContent("5", "", new MediaContent(tests)));
                                            map2.put("additionalSectionsData", arr2);
                                            arr1.add(map2);
                                            Theme theme = new Theme(map, arr1);
                                            db.collection("Courses_AndroidTest").document("2").set(theme);
                                            startActivity(new Intent(AdminActivity.this, AdminActivity.class));
                                            finish();
                                        }
                                    }
                                    @Override public void onLongItemClick(View view, int position) {
                                    }
                                })
                        );*/
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with with ", task.getException());
                }
            }
        });
    }

    public static class Theme {
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

    public static class LessonContent {
        public String title, additionalSectionImage;
        public MediaContent content;

        public LessonContent() {
        }

        public LessonContent(String title, String additionalSectionImage, MediaContent content) {
            this.title = title;
            this.additionalSectionImage = additionalSectionImage;
            this.content = content;
        }
    }

    public static class MediaContent {
        public URL videoUrl;
        public ArrayList<PDF> notes = new ArrayList<>();
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

    public static class PDF {
        public String pdfUrl, noteTitle;
    }
}
