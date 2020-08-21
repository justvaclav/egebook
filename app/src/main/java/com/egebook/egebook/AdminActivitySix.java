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

public class AdminActivitySix extends AppCompatActivity {
    int themeNum, unit, lesson;
    FirebaseFirestore db;
    String TAG = "AdminActivitySix";
    Theme theme;
    Object o;
    ArrayList<Map<String, Object>> additional;
    boolean isAvailable = true, newUnit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_six);
        Intent i = getIntent();
        newUnit = i.getBooleanExtra("newUnit", false);
        themeNum = i.getIntExtra("theme", 0);
        unit = i.getIntExtra("unit", 0);
        lesson = i.getIntExtra("lesson", 0);
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        String colName = getString(R.string.colName);
        DocumentReference docRef = db.collection(colName).document(String.valueOf(themeNum));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        o = document.getData();
                        theme = document.toObject(Theme.class);
                        additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                        Map<String, ArrayList> content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                        Map<String, Object> conspect = (Map<String, Object>) content.get("videosURL");
                        EditText titleEditText = findViewById(R.id.titleEditText);
                        EditText linkEditText = findViewById(R.id.descEditText);
                        try {
                            //ArrayList<Map<String, Map<String, ArrayList<Map<String, Object>>>>> global = (ArrayList<Map<String, Map<String, ArrayList<Map<String, Object>>>>>) theme.getLessonInfo().get(0).get("additionalSectionsData");
                            additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit-1).get("additionalSectionsData");
                            content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                            try {conspect = (Map<String, Object>) content.get("videosURL");}
                            catch (NullPointerException e) {
                                additional.get(lesson).put("videosURL", new ArrayList<Map<String, Object>>());
                                conspect = (Map<String, Object>) content.get("videosURL");
                            }
                            Map<String, Object> notes = (Map<String, Object>) ((Map<String, Object>) additional.get(lesson).get("content")).get("videosURL");
                            try {
                                titleEditText.setText(notes.get("videoWithNormalCastingURL").toString());
                                linkEditText.setText(notes.get("videoWithSpecialCastingURL").toString());
                            }
                            catch (IndexOutOfBoundsException e) {
                                titleEditText.setText("");
                                linkEditText.setText("");
                            }
                        }
                        catch (NullPointerException e) {
                            titleEditText.setText("");
                            linkEditText.setText("");
                            e.printStackTrace();
                            /*theme.setCourseInfo(new HashMap<String, Object>());
                            theme.setLessonInfo(new ArrayList<Map<String, Object>>());*/
                        }
                        /*String[] dataLesson = new String[additional.size()+1];
                        for (int i = 0; i < additional.size()+1; i++) {
                            dataLesson[i] = String.valueOf(i);
                        }
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, dataLesson);
                        Spinner spinnerLesson = findViewById(R.id.spinner3);
                        spinnerLesson.setAdapter(adapter2);
                        spinnerLesson.setPrompt("Выбор конспекта");
                        spinnerLesson.setSelection(0);
                        spinnerLesson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });*/

                        TextView completeButton = findViewById(R.id.loginTextView2);
                        completeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                Map<String, Object> content = (Map<String, Object>) additional.get(lesson).get("content");
                                //try {
                                    Map<String, Object> notes;
                                    try {notes = (Map<String, Object>) content.get("videosURL");
                                        notes.put("videoWithNormalCastingURL", titleEditText.getText().toString());
                                        notes.put("videoWithSpecialCastingURL", linkEditText.getText().toString());
                                        db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                        Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();}
                                    catch (NullPointerException e) {
                                        content.put("videosURL", new HashMap<String, Object>());
                                        notes = (Map<String, Object>) content.get("videosURL");
                                        notes.put("videoWithNormalCastingURL", titleEditText.getText().toString());
                                        notes.put("videoWithSpecialCastingURL", linkEditText.getText().toString());
                                        db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                        Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                    }
                                    /*notes.put("videoWithNormalCastingURL", titleEditText.getText().toString());
                                    notes.put("videoWithSpecialCastingURL", linkEditText.getText().toString());
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();*/
                                /*}
                                catch (IndexOutOfBoundsException e) {
                                    content.get("videosURL").add(new HashMap<>());
                                    Map<String, Object> notes = (Map<String, Object>) content.get("videosURL").get(spinnerConspect.getSelectedItemPosition());
                                    notes.put("videoWithNormalCastingURL", titleEditText.getText().toString());
                                    notes.put("videoWithSpecialCastingURL", linkEditText.getText().toString());
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    //ArrayList<Object> array = db.collection(colName).document(String.valueOf(themeNum)).collection("lessonInfo").document(String.valueOf(unit)).collection("additionalSectionsData").document(String.valueOf(lesson)).collection("content").document("notes").collection(String.valueOf(spinnerConspect.getSelectedItemPosition()));
                                    //db.collection(colName).document(String.valueOf(themeNum)).collection("lessonInfo").document(String.valueOf(unit)).collection("additionalSectionsData").document(String.valueOf(lesson)).collection("content").document("notes").collection(String.valueOf(spinnerConspect.getSelectedItemPosition())).add(map);//document("pdfUrl").set(linkEditText.getText().toString());
                                    //db.collection(colName).document(String.valueOf(themeNum)).collection("lessonInfo").document(String.valueOf(unit)).collection("additionalSectionsData").document(String.valueOf(lesson)).collection("content").document("notes").collection(String.valueOf(spinnerConspect.getSelectedItemPosition())).document("noteTitle").set(titleEditText.getText().toString());
                                    Toast.makeText(getApplicationContext(), "Updated with adding new video", Toast.LENGTH_SHORT).show();
                                }*/
                                startActivity(new Intent(AdminActivitySix.this, AdminActivity.class));
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
}
