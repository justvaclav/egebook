package com.egebook.egebook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminActivityNeuf extends AppCompatActivity {
    int themeNum, unit;
    FirebaseFirestore db;
    String TAG = "AdminActivityNeuf";
    Theme theme;
    Object o;
    boolean isAvailable = true, newUnit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_neuf);
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
                        TextView completeButton = findViewById(R.id.loginTextView5);
                        completeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //theme.getLessonInfo().add(new HashMap<>());
                                ArrayList<Map<String, Object>> additional = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(unit - 1).get("additionalSectionsData");
                                additional.add(new HashMap<>());
                                //additional.get(getIntent().getIntExtra("position", 0)).put("title", titleEditText.getText().toString());
                                theme.getLessonInfo().get(unit - 1).put("additionalSectionsData", additional);
                                Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                db.collection(colName).document(String.valueOf(themeNum)).set(theme);
                                Toast.makeText(getApplicationContext(), "Updated with adding new lesson!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AdminActivityNeuf.this, AdminActivity.class));
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
