package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminActivityTrois extends AppCompatActivity {
    int themeNum, unit;
    FirebaseFirestore db;
    String TAG = "AdminActivityTrois";
    Theme theme;
    Object o;
    boolean isAvailable = true, newUnit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_trois);
        Intent i = getIntent();
        themeNum = i.getIntExtra("theme", 0);
        unit = i.getIntExtra("unit", 0);
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        String colName = getApplicationContext().getString(R.string.colName);
        Log.d(TAG, "theme" + themeNum);
        DocumentReference docRef = db.collection(colName).document(String.valueOf(themeNum));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        o = document.getData();
                        theme = document.toObject(Theme.class);
                        Log.d(TAG, "DocumentSnapshot data: " + theme.toString());
                        //Log.d("themeLessonInfo size", theme.getLessonInfo().size() + "");
                        TextView titleTextView = findViewById(R.id.registerTextView2);
                        newUnit = i.getBooleanExtra("newUnit", false);
                        if (newUnit) {
                            titleTextView.setText("Создание нового раздела");
                        }
                        else {
                            titleTextView.setText("Редактирование раздела");
                        }
                        EditText titleEditText = findViewById(R.id.titleEditText);
                        try {
                            //ArrayList<Map<String, Map<String, ArrayList<Map<String, Object>>>>> global = (ArrayList<Map<String, Map<String, ArrayList<Map<String, Object>>>>>) theme.getLessonInfo().get(0).get("additionalSectionsData");
                            //ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(0).get("additionalSectionsData");
                            try {titleEditText.setText(theme.getLessonInfo().get(unit-1).get("lessonTopic").toString());}
                            catch (IndexOutOfBoundsException e) {titleEditText.setText("");}
                        }
                        catch (NullPointerException e) {
                            titleEditText.setText("");
                            e.printStackTrace();
                            /*theme.setCourseInfo(new HashMap<String, Object>());
                            theme.setLessonInfo(new ArrayList<Map<String, Object>>());*/
                        }
                        TextView completeButton = findViewById(R.id.loginTextView5);
                        completeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    theme.getLessonInfo().get(unit - 1).put("lessonTopic", titleEditText.getText().toString());
                                    theme.getLessonInfo().get(unit - 1).put("opened", false);
                                    theme.getLessonInfo().get(unit - 1).put("mainSectionGradientImage", "lightPinkGradientCell");
                                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                /*Map<String, Object> map = new HashMap<String, Object>();
                                map.put("title", titleEditText.getText().toString());
                                map.put("isAvailable", isAvailable);
                                map.put("courseGradientImage", "Розовый градиент");
                                theme.setCourseInfo(map);*/
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                }
                                catch (IndexOutOfBoundsException e) {
                                    theme.getLessonInfo().add(new HashMap<>());
                                    theme.getLessonInfo().get(unit - 1).put("additionalSectionsData", new ArrayList<HashMap>());
                                    theme.getLessonInfo().get(unit - 1).put("lessonTopic", titleEditText.getText().toString());
                                    theme.getLessonInfo().get(unit - 1).put("opened", false);
                                    theme.getLessonInfo().get(unit - 1).put("mainSectionGradientImage", "lightPinkGradientCell");
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminActivityTrois.this, AdminActivity.class));
                                    finish();
                                }
                            }
                        });
                        /*TextView conspectButton = findViewById(R.id.loginTextView2);
                        conspectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AdminActivityTrois.this, AdminActivityQuatre.class);
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("newUnit", newUnit);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });*/
                        TextView lessonButton = findViewById(R.id.loginTextView7);
                        lessonButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AdminActivityTrois.this, AdminActivitySept.class);
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("newUnit", newUnit);
                                Log.d(TAG+"Sept", themeNum + "/" + unit + "/");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        TextView removeButton = findViewById(R.id.loginTextView9);
                        removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AdminActivityTrois.this, AdminActivity.class);
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("newUnit", newUnit);
                                theme.getLessonInfo().remove(unit-1);
                                Log.d(TAG+"Sept", themeNum + "/" + unit + "/");
                                db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                //db.collection(colName).document(String.valueOf(themeNum)).collection("lessonInfo").document(String.valueOf(unit - 1)).delete();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
    }
}
