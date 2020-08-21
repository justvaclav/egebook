package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class AdminActivity extends AppCompatActivity {

    String TAG = "AdminActivity";
    String colName;
    Map<String, Object> o;
    FirebaseFirestore db;
    Theme theme;
    ArrayList<ArrayList<String>> items = new ArrayList<ArrayList<String>>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        colName = getString(R.string.colName);

        Log.d(TAG, FirebaseFirestore.getInstance().toString());
        CollectionReference docCol = db.collection(colName);
        docCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String,Object> updates = new HashMap<>();
                    final QuerySnapshot document = task.getResult();
                    if (!document.isEmpty()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getDocuments());
                        final List<DocumentSnapshot> documents  = document.getDocuments();
                        for (int i = 0; i < documents.size(); i++) {
                            ArrayList<String> itemsDeux = new ArrayList<>();
                            theme = documents.get(i).toObject(Theme.class);
                            try {itemsDeux.add((String) theme.courseInfo.get("title"));}
                            catch (NullPointerException e) {
                                itemsDeux.add("Повреждённый курс");
                            }
                            itemsDeux.add("Редактирование предмета");
                            int size;
                            try {size = theme.lessonInfo.size();}
                            catch (NullPointerException e) {size = 0;}
                            for (int j = 0; j < size; j++) {
                                itemsDeux.add((String) theme.lessonInfo.get(j).get("lessonTopic"));
                            }
                            itemsDeux.add("Добавить новый раздел");
                            items.add(itemsDeux);
                        }
                        ArrayList<String> itemsDeux = new ArrayList<String>();
                        itemsDeux.add("");
                        items.add(itemsDeux);

                        final int itemsSize = items.size() - 1;
                        RecyclerView.Adapter mAdapter;
                        final RecyclerView rw = findViewById(R.id.recyclerViewAdmin);
                        rw.setHasFixedSize(true);
                        rw.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        mAdapter = new AdminAdapter(items, items.size(), R.layout.list_theme_element);
                        rw.setAdapter(mAdapter);
                        TextView addButton = findViewById(R.id.dispTextView);
                        addButton.setVisibility(View.INVISIBLE);
                        TextView updateButton = findViewById(R.id.dispTextView4);
                        addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(getApplicationContext(), "У вас нет прав для добавления курсов", Toast.LENGTH_SHORT).show();
                                Map<String, Object> map = new HashMap<>();
                                ArrayList<Map<String, Object>> arr1 = new ArrayList<Map<String, Object>>();
                                ArrayList<QuizModel> tests = new ArrayList<>();
                                Map<String, Object> map2 = new HashMap<>();
                                ArrayList<LessonContent> arr2 = new ArrayList<>();
                                map.put("title", "Новый курс #" + items.size());
                                map.put("isAvailable", true);
                                map.put("description", "Описание курса");
                                map.put("courseGradientColor", "Розовый градиент");
                                //map2.put("opened", true);//
                                map2.put("lessonTopic", "Новая тема");//
                                //map2.put("mainSectionGradientImage", "gradientPurpleCell");
                                LessonContent content = new LessonContent("Новый урок", "", new MediaContent(tests));
                                arr2.add(content);
                                map2.put("additionalSectionsData", arr2);
                                arr1.add(map2);
                                Theme theme = new Theme(map, arr1);
                                db.collection(colName).document(items.size()-1 + "").set(theme); //ОБЯЗАТЕЛЬНО ПЕРСМОТРЕТЬ ПРИ ИЗМЕНЕНИИ ЛЭЙАУТА У AdminAdapter
                                startActivity(new Intent(AdminActivity.this, AdminActivity.class));
                                finish();
                            }
                        });
                        updateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(AdminActivity.this, AdminActivity.class));
                                finish();
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
