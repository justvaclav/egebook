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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivityDeux extends AppCompatActivity {
    int themeNum;
    FirebaseFirestore db;
    String TAG = "AdminActivityDeux";
    Theme theme;
    Object o;
    boolean isAvailable = true, newUnit = false;
    int colSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_deux);
        Intent i = getIntent();
        newUnit = i.getBooleanExtra("newUnit", false);
        themeNum = i.getIntExtra("theme", 0);
        FirebaseApp.initializeApp(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        String colName = getApplicationContext().getString(R.string.colName);
        Log.d(TAG, "theme:" + themeNum);
        CollectionReference docCol = db.collection(colName);
        docCol.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final QuerySnapshot document = task.getResult();
                if (!document.isEmpty()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getDocuments());
                    final List<DocumentSnapshot> documents = document.getDocuments();
                    colSize = documents.size() - 1;
                }
            }
        });
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
                        EditText titleEditText = findViewById(R.id.titleEditText);
                        EditText descEditText = findViewById(R.id.descEditText);
                        try {
                            titleEditText.setText(theme.getCourseInfo().get("title").toString());
                            descEditText.setText(theme.getCourseInfo().get("description").toString());
                        }
                        catch (NullPointerException e) {
                            titleEditText.setText("");
                            descEditText.setText("");
                            theme.setCourseInfo(new HashMap<String, Object>());
                            theme.setLessonInfo(new ArrayList<Map<String, Object>>());
                        }
                        TextView trueButton = findViewById(R.id.loginTextView3);
                        TextView falseButton = findViewById(R.id.loginTextView);
                        TextView completeButton = findViewById(R.id.loginTextView2);
                        TextView removeButton = findViewById(R.id.loginTextView8);
                        trueButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                theme.getCourseInfo().put("isAvailable", true);
                                isAvailable = true;
                                Toast.makeText(getApplicationContext(), "isAvailable:true", Toast.LENGTH_SHORT).show();
                            }
                        });
                        falseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                theme.getCourseInfo().put("isAvailable", false);
                                isAvailable = false;
                                Toast.makeText(getApplicationContext(), "isAvailable:false", Toast.LENGTH_SHORT).show();
                            }
                        });
                        completeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //theme.getCourseInfo().put("title", titleEditText.getText());
                                //theme.getCourseInfo().put("description", descEditText.getText());
                                Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("title", titleEditText.getText().toString());
                                map.put("description", descEditText.getText().toString());
                                map.put("isAvailable", false);
                                map.put("courseGradientImage", "Розовый градиент");
                                theme.setCourseInfo(map);
                                db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                startActivity(new Intent(AdminActivityDeux.this, AdminActivity.class));
                                finish();
                            }
                        });
                        removeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //theme.getCourseInfo().put("title", titleEditText.getText());
                                //theme.getCourseInfo().put("description", descEditText.getText());
                                if (themeNum == colSize) {
                                    Toast.makeText(getApplicationContext(), "Deleted " + theme.getCourseInfo().get("title"), Toast.LENGTH_SHORT).show();
                                    db.collection(colName).document(String.valueOf(themeNum)).delete();
                                    startActivity(new Intent(AdminActivityDeux.this, AdminActivity.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "В целях безопасности можно удалить только самый последний курс. ", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getApplicationContext(), "Для удаления курса из середины обратитесь к администратору Firebase и убедитесь, чтобы после удаления номера курсов были по порядку.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "No such document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with with ", task.getException());
                }
            }
        });
    }
}
