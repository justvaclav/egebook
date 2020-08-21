package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DispActivity extends AppCompatActivity {

    String TAG = "DispActivity", id = "";
    List<DocumentSnapshot> o;
    Theme theme;
    RecyclerView rw;
    boolean sub = false, admin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp);
        rw = (findViewById(R.id.recyclerViewDisps));
        TextView loginButton = findViewById(R.id.dispTextView1);
        TextView logoutButton = findViewById(R.id.dispTextView5);
        TextView subButton = findViewById(R.id.dispTextView2);
        TextView adminButton = findViewById(R.id.dispTextView3);
        adminButton.setVisibility(View.INVISIBLE);
        final ArrayList<String> themes = new ArrayList<>();
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef;
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            id = auth.getCurrentUser() == null ? /*getIntent().getStringExtra("id")*/"" :  auth.getUid();
            Log.d(TAG, "user id before docRef:"+id);
            loginButton.setVisibility(auth.getCurrentUser() == null ? View.VISIBLE : View.INVISIBLE);
            logoutButton.setVisibility(auth.getCurrentUser() == null ? View.INVISIBLE : View.VISIBLE);
            subButton.setVisibility(auth.getCurrentUser() == null ? View.INVISIBLE : View.VISIBLE);
            docRef = db.collection("Users");
            docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot document = task.getResult();
                        if (!document.isEmpty()) {
                            ArrayList<Map<String, Object>> subs = new ArrayList<>();
                            o = document.getDocuments();
                            for (int i = 0; i < o.size(); i++) {
                                if (o.get(i).get("uid").equals(id)) {
                                    subs = (ArrayList<Map<String, Object>>) o.get(i).get("subscriptions");
                                    if ((boolean)o.get(i).get("isAdmin")) {
                                        admin = true;
                                    }
                                }
                            }
                            Log.d(TAG, "uid:" + id + ", subs:" + subs.toString());
                            CollectionReference docRef2 = db.collection("Courses");
                            ArrayList<Map<String, Object>> finalSubs = subs;
                            docRef2.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    QuerySnapshot document1 = task1.getResult();
                                    if (!document1.isEmpty()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document1.getDocuments());
                                        o = document1.getDocuments();
                                        for (int i = 0; i < finalSubs.size(); i++) {
                                            Log.d(TAG, "subscription time remained: " + ((Timestamp) finalSubs.get(i).get("subscriptionExpireDate")).compareTo(Timestamp.now()));
                                            if (((Timestamp) finalSubs.get(i).get("subscriptionExpireDate")).compareTo(Timestamp.now()) > 0) {
                                                switch (Long.valueOf(finalSubs.get(i).get("subscriptionID").toString()).toString()) {
                                                    case "0":    {
                                                        sub = true;
                                                        break;
                                                    }
                                                    case "1":    {
                                                        sub = true;
                                                        break;
                                                    }
                                                    case "2":    {
                                                        sub = true;
                                                        break;
                                                    }
                                                    case "3":    {
                                                        sub = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (id.equals("zPxivqmjdSSCFDG73dPPWcD6wGn2") || id.equals("89FbbVY52AcJykrqkItXqjtM3BZ2") || admin) {
                                            adminButton.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            adminButton.setVisibility(View.INVISIBLE);
                                        }
                                        for (int i = 0; i < o.size(); i++) {
                                            theme = o.get(i).toObject(Theme.class);
                                            themes.add(theme.courseInfo.get("title").toString());
                                        }
                                        RecyclerView.Adapter mAdapter;
                                        rw.setHasFixedSize(true);
                                        rw.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                                        mAdapter = new DispAdapter(themes, R.layout.list_disp_element);
                                        rw.setAdapter(mAdapter);
                                        rw.addOnItemTouchListener(
                                                new RecyclerItemClickListener(getApplicationContext(), rw, new RecyclerItemClickListener.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(View view, int position) {
                                                        Intent i = new Intent(DispActivity.this, ThemesActivity.class);
                                                        i.putExtra("theme", position);
                                                        i.putExtra("sub", sub);
                                                        i.putExtra("id", id);
                                                        startActivity(i);
                                                    }

                                                    @Override
                                                    public void onLongItemClick(View view, int position) {

                                                    }
                                                })
                                        );
                                    }
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
        catch (NullPointerException e) {
            //Toast.makeText(getApplicationContext(), "Log in to use all features", Toast.LENGTH_SHORT).show();
            docRef = db.collection("Courses");
            docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot document = task.getResult();
                        if (!document.isEmpty()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getDocuments());
                            o = document.getDocuments();
                            for (int i = 0; i < o.size(); i++) {
                                theme = o.get(i).toObject(Theme.class);
                                themes.add(theme.courseInfo.get("title").toString());
                            }
                            RecyclerView.Adapter mAdapter;
                            rw.setHasFixedSize(true);
                            rw.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                            mAdapter = new DispAdapter(themes, R.layout.list_disp_element);
                            rw.setAdapter(mAdapter);
                            rw.addOnItemTouchListener(
                                    new RecyclerItemClickListener(getApplicationContext(), rw, new RecyclerItemClickListener.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            Intent i = new Intent(DispActivity.this, ThemesActivity.class);
                                            i.putExtra("theme", position);
                                            i.putExtra("sub", sub);
                                            i.putExtra("id", id);
                                            startActivity(i);
                                        }

                                        @Override
                                        public void onLongItemClick(View view, int position) {

                                        }
                                    })
                            );
                        }
                    } else {
                        Log.d(TAG, "get failed with with ", task.getException());
                    }
                }
            });
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DispActivity.this, StartActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                Log.d(TAG, "id:" + getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                //rw.setVisibility(View.INVISIBLE);
                logoutButton.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                sub = false;
            }
        });
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DispActivity.this, SubscribeActivity.class);
                intent.putExtra("id", id);
                Log.d(TAG, "id:" + getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DispActivity.this, AdminActivity.class));
            }
        });
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
}
