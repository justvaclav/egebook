package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AdminActivityQuatre extends AppCompatActivity {
    int themeNum, unit, lesson;
    FirebaseFirestore db;
    String TAG = "AdminActivityQuatre";
    Theme theme;
    Object o;
    ArrayList<Map<String, Object>> additional;
    boolean isAvailable = true, newUnit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quatre);
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
                        Map<String, ArrayList> content = new HashMap<>();
                        try {content = (Map<String, ArrayList>) additional.get(lesson).get("content");}
                        catch (NullPointerException e) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("content", new ArrayList<Map<String, Object>>());
                            additional.add(map);
                            content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                        }
                        ArrayList<Map<String, Object>> conspect = new ArrayList<Map<String, Object>>();
                        try {conspect = (ArrayList<Map<String, Object>>) content.get("notes");}
                        catch (NullPointerException e) {
                            content.put("notes", conspect);
                            conspect = (ArrayList<Map<String, Object>>) content.get("notes");
                        }
                        EditText titleEditText = findViewById(R.id.titleEditText);
                        EditText linkEditText = findViewById(R.id.descEditText);
                        try {
                            //ArrayList<Map<String, Map<String, ArrayList<Map<String, Object>>>>> global = (ArrayList<Map<String, Map<String, ArrayList<Map<String, Object>>>>>) theme.getLessonInfo().get(0).get("additionalSectionsData");
                            additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit-1).get("additionalSectionsData");
                            content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                            conspect = (ArrayList<Map<String, Object>>) content.get("notes");
                            ArrayList<Map<String, Object>> notes = (ArrayList<Map<String, Object>>) ((Map<String, Object>) additional.get(lesson).get("content")).get("notes");
                            try {
                                titleEditText.setText(notes.get(0).get("noteTitle").toString());
                                linkEditText.setText(notes.get(0).get("pdfUrl").toString());
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
                        String[] dataConspect;
                        try {dataConspect = new String[conspect.size()+1];
                            ArrayList<Map<String, Object>> notes = (ArrayList<Map<String, Object>>) ((Map<String, Object>) additional.get(lesson).get("content")).get("notes");
                            for (int i = 0; i < conspect.size()+1; i++) {
                                try {dataConspect[i] = notes.get(i).get("noteTitle").toString();}
                                catch (IndexOutOfBoundsException e) {dataConspect[i] = "Добавить новый конспект";}
                            }}
                        catch (NullPointerException e) {
                            dataConspect = new String[1];
                            dataConspect[0] = "Добавить новый конспект";
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, dataConspect);
                        Spinner spinnerConspect = findViewById(R.id.spinner3);
                        spinnerConspect.setAdapter(adapter);
                        spinnerConspect.setPrompt("Выбор конспекта");
                        spinnerConspect.setSelection(0);
                        spinnerConspect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                try {
                                    ArrayList<Map<String, Object>> notes = (ArrayList<Map<String, Object>>) ((Map<String, Object>) additional.get(lesson).get("content")).get("notes");
                                    try {
                                        titleEditText.setText(notes.get(position).get("noteTitle").toString());
                                        linkEditText.setText(notes.get(position).get("pdfUrl").toString());
                                    }
                                    catch (NullPointerException e) {
                                        titleEditText.setText("");
                                        linkEditText.setText("");
                                    }
                                }
                                catch (IndexOutOfBoundsException e) {
                                    titleEditText.setText("");
                                    linkEditText.setText("");
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
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
                                Map<String, ArrayList> content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                                try {
                                    Map<String, Object> notes;
                                    try {notes = (Map<String, Object>) content.get("notes").get(spinnerConspect.getSelectedItemPosition());}
                                    catch (NullPointerException e) {
                                        content.put("notes", new ArrayList<HashMap>());
                                        notes = (Map<String, Object>) content.get("notes").get(spinnerConspect.getSelectedItemPosition());
                                    }
                                    notes.put("noteTitle", titleEditText.getText().toString());
                                    notes.put("pdfUrl", linkEditText.getText().toString());
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                }
                                catch (IndexOutOfBoundsException e) {
                                    content.get("notes").add(new HashMap<>());
                                    Map<String, Object> notes = (Map<String, Object>) content.get("notes").get(spinnerConspect.getSelectedItemPosition());
                                    notes.put("noteTitle", titleEditText.getText().toString());
                                    notes.put("pdfUrl", linkEditText.getText().toString());
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    //ArrayList<Object> array = db.collection(colName).document(String.valueOf(themeNum)).collection("lessonInfo").document(String.valueOf(unit)).collection("additionalSectionsData").document(String.valueOf(lesson)).collection("content").document("notes").collection(String.valueOf(spinnerConspect.getSelectedItemPosition()));
                                    //db.collection(colName).document(String.valueOf(themeNum)).collection("lessonInfo").document(String.valueOf(unit)).collection("additionalSectionsData").document(String.valueOf(lesson)).collection("content").document("notes").collection(String.valueOf(spinnerConspect.getSelectedItemPosition())).add(map);//document("pdfUrl").set(linkEditText.getText().toString());
                                    //db.collection(colName).document(String.valueOf(themeNum)).collection("lessonInfo").document(String.valueOf(unit)).collection("additionalSectionsData").document(String.valueOf(lesson)).collection("content").document("notes").collection(String.valueOf(spinnerConspect.getSelectedItemPosition())).document("noteTitle").set(titleEditText.getText().toString());
                                    Toast.makeText(getApplicationContext(), "Updated with adding new conspect", Toast.LENGTH_SHORT).show();
                                }
                                startActivity(new Intent(AdminActivityQuatre.this, AdminActivity.class));
                                finish();
                            }});
                            TextView removeButton = findViewById(R.id.loginTextView11);
                            removeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                    Map<String, ArrayList> content = (Map<String, ArrayList>) additional.get(lesson).get("content");
                                    ArrayList<Map<String, Object>> notes = (ArrayList<Map<String, Object>>) content.get("notes");
                                    notes.remove(spinnerConspect.getSelectedItemPosition());
                                    content.put("notes", notes);
                                    additional.get(getIntent().getIntExtra("position", 0)).put("content", content);
                                    theme.getLessonInfo().get(unit - 1).put("additionalSectionsData", additional);
                                    db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminActivityQuatre.this, AdminActivity.class));
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
