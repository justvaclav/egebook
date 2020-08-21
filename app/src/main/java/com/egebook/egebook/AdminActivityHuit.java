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
import java.util.Map;

public class AdminActivityHuit extends AppCompatActivity {
    int themeNum, unit;
    FirebaseFirestore db;
    String TAG = "AdminActivityHuit";
    Theme theme;
    Object o;
    boolean isAvailable = true, newUnit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_huit);
        Intent i = getIntent();
        themeNum = i.getIntExtra("theme", 0);
        unit = i.getIntExtra("unit", 0);
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        String colName = getString(R.string.colName);
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
                            titleTextView.setText("Создание нового урока");
                        }
                        else {
                            titleTextView.setText("Редактирование урока");
                        }
                        EditText titleEditText = findViewById(R.id.titleEditText);
                        try {
                            ArrayList<Map<String, Map<String, ArrayList<Map<String, Object>>>>> global = (ArrayList<Map<String, Map<String, ArrayList<Map<String, Object>>>>>) theme.getLessonInfo().get(0).get("additionalSectionsData");
                            ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                            try {titleEditText.setText(additional.get(getIntent().getIntExtra("position", 0)).get("title").toString());}
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
                                    ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                    additional.get(getIntent().getIntExtra("position", 0)).put("title", titleEditText.getText().toString());
                                    theme.getLessonInfo().get(unit - 1).put("additionalSectionsData", additional);
                                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                }
                                catch (IndexOutOfBoundsException e) {
                                    //theme.getLessonInfo().add(new HashMap<>());
                                    ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                    additional.add(new HashMap<>());
                                    additional.get(getIntent().getIntExtra("position", 0)).put("title", titleEditText.getText().toString());
                                    HashMap<String, Object> content = new HashMap<>();
                                    content.put("notes", new ArrayList<>());
                                    content.put("tests", new ArrayList<>());
                                    content.put("videosURL", new HashMap<>());
                                    additional.get(getIntent().getIntExtra("position", 0)).put("content", content);
                                    theme.getLessonInfo().get(unit - 1).put("additionalSectionsData", additional);
                                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    Toast.makeText(getApplicationContext(), "Updated with adding new lesson!", Toast.LENGTH_SHORT).show();

                                }
                                Intent intent = new Intent(AdminActivityHuit.this, AdminActivitySept.class);
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("newUnit", newUnit);
                                startActivity(intent);
                                finish();
                            }
                        });
                        TextView conspectButton = findViewById(R.id.loginTextView2);
                        conspectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AdminActivityHuit.this, AdminActivityQuatre.class);
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("newUnit", newUnit);
                                intent.putExtra("lesson", getIntent().getIntExtra("position", 0));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        TextView videoButton = findViewById(R.id.loginTextView6);
                        videoButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AdminActivityHuit.this, AdminActivitySix.class);
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("newUnit", newUnit);
                                intent.putExtra("lesson", getIntent().getIntExtra("position", 0));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        TextView testsButton = findViewById(R.id.loginTextView4);
                        testsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AdminActivityHuit.this, AdminActivityCinq.class);
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("newUnit", newUnit);
                                intent.putExtra("lesson", getIntent().getIntExtra("position", 0));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        TextView removeButton = findViewById(R.id.loginTextView10);
                        removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                additional.remove(getIntent().getIntExtra("position", 0));
                                theme.getLessonInfo().get(unit - 1).put("additionalSectionsData", additional);
                                db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                Toast.makeText(getApplicationContext(), "Removed!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AdminActivityHuit.this, AdminActivitySept.class);
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("newUnit", newUnit);
                                intent.putExtra("lesson", getIntent().getIntExtra("position", 0));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        /*TextView lessonButton = findViewById(R.id.loginTextView7);
                        lessonButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(AdminActivityHuit.this, AdminActivityQuatre.class);
                                intent.putExtra("theme", themeNum);
                                intent.putExtra("unit", unit);
                                intent.putExtra("lesson", getIntent().getIntExtra("position", 0));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });*/
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
