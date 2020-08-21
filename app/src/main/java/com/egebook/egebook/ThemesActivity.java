package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemesActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    Map<String, Object> o;
    List<DocumentSnapshot> oo;
    Gson gson = new Gson();
    public String TAG = "ThemesActivity";
    Theme theme;
    boolean sub = false;
    ArrayList<ArrayList<String>> themes = new ArrayList<ArrayList<String>>();
    public ArrayList<String> themeNames = new ArrayList<>();
    BillingProcessor bp;
    boolean one = false, three = false, six = false, nine = false;
    FirebaseAuth mAuth;

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    private class CourseInfo {
        String courseGradientColor;
        String description;
        boolean isAvailable;
        String title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAheNvLZOxfXajgqs0yVtQTrtRKOp5LKFeFYpWBAsHBAj2YJ5JU4oX5JAKyDFLcqRV4FoX56IXWfjVTsnMdfCPLyztyuP9Kmwst7QiJzACMV945Ix0J7FlVbm/MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAheNvLZOxfXajgqs0yVtQTrtRKOp5LKFeFYpWBAsHBAj2YJ5JU4oX5JAKyDFLcqRV4FoX56IXWfjVTsnMdfCPLyztyuP9Kmwst7QiJzACMV945Ix0J7FlVbm/vKoG2vsz7ycydByyVoHiWTFsQ5BNuq5qIeS1GEakXvXsIGciAZuTbAbnqvnnGqVoGHpMpPF8L6WWS0Y0kYO5iKmbqr1ap3HJEZXdrHWS41C5T5Cg0v1HdacO7fUwKs1fLVBqFDbvLSPuAqF58fo1VUgvAUuf5ruc8fsYcQOWBTfd6Rtq8Lt3RaDF972d8oiNHTeL6Wkj5FpjZmAGNNGb6rK7OwIDAQAB", this);
        bp.initialize();
        FirebaseApp.initializeApp(getApplicationContext());
        CollectionReference docRef2 = db.collection("Users");
        docRef2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                   if (task.isSuccessful()) {
                                                       QuerySnapshot document = task.getResult();
                                                       if (!document.isEmpty()) {
                                                           Log.d(TAG, "DocumentSnapshot data: " + document.getDocuments());
                                                           ArrayList<Map<String, Object>> subs = new ArrayList<>();
                                                           oo = document.getDocuments();
                                                           mAuth = FirebaseAuth.getInstance();
                                                           FirebaseUser user = mAuth.getCurrentUser();
                                                           try {String uid = user.getUid();
                                                               for (int i = 0; i < oo.size(); i++) {
                                                                   if (oo.get(i).get("uid").equals(uid)) {
                                                                       subs = (ArrayList<Map<String, Object>>) oo.get(i).get("subscriptions");
                                                                   }
                                                               }
                                                           }
                                                           catch (NullPointerException e) {
                                                               e.printStackTrace();
                                                           }
                                                           for (int i = 0; i < subs.size(); i++) {
                                                               if (((Timestamp) subs.get(i).get("subscriptionExpireDate")).compareTo(Timestamp.now()) > 0) {
                                                                   switch (Integer.valueOf(subs.get(i).get("subscriptionID") + "")) {
                                                                       case 0:
                                                                           one = true;
                                                                           break;
                                                                       case 1:
                                                                           three = true;
                                                                           break;
                                                                       case 2:
                                                                           six = true;
                                                                           break;
                                                                       case 3:
                                                                           nine = true;
                                                                           break;
                                                                   }
                                                               }
                                                           }
                                                           Log.w(TAG, "1x:" + one + ", 3x:" + three + ", 6x:" + six + ", 9x:" + nine);
                                                           Log.w(TAG, "SUBSCRIBES: 1x:" + bp.isSubscribed("onemonthsubscription.society.egebook") + ", 3x:" + bp.isSubscribed("threemonthssubscription.society.egebook") + ", 6x:" + bp.isSubscribed("sixmonthssubscription.society.egebook"));


                                                           if (bp.isSubscribed("onemonthsubscription.society.egebook") && !one) {
                                                               //Toast.makeText(getApplicationContext(), "bp.isSubscribed(\"onemonthsubscription.society.egebook\") && !one", Toast.LENGTH_SHORT).show();
                                                               FirebaseApp.initializeApp(getApplicationContext());
                                                               FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                               CollectionReference docRef = db.collection("Users");
                                                               docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                       if (task.isSuccessful()) {
                                                                           QuerySnapshot document = task.getResult();
                                                                           if (!document.isEmpty()) {
                                                                               Log.d(TAG, "DocumentSnapshot data: " + document.getDocuments());
                                                                               ArrayList<Map<String, Object>> subs = new ArrayList<>();
                                                                               oo = document.getDocuments();
                                                                               for (int i = 0; i < o.size(); i++) {
                                                                                   if (oo.get(i).get("uid").equals(getIntent().getStringExtra("id"))) {
                                                                                       subs = (ArrayList<Map<String, Object>>) oo.get(i).get("subscriptions");
                                                                                   }
                                                                               }
                                                                               Map<String, Object> map = new HashMap<String, Object>();
                                                                               map.put("isAdmin", false);
                                                                               map.put("uid", getIntent().getStringExtra("id"));
                                                                               Map<String, Object> mapSub = new HashMap<String, Object>();
                                                                               mapSub.put("subscriptionID", 0);
                                                                               mapSub.put("subscriptionStartDate", Timestamp.now());
                                                                               mapSub.put("subscriptionExpireDate", new Timestamp(Timestamp.now().getSeconds() + 2678400, 0));
                                                                               subs.add(mapSub);
                                                                               map.put("subscriptions", subs);
                                                                               db.collection("Users").document(getIntent().getStringExtra("id")).set(map);
                                                                           } else {
                                                                               Toast.makeText(getApplicationContext(), "Зарегистрируйтесь или войдите в свой аккаунт, чтобы оформить подписку", Toast.LENGTH_LONG).show();
                                                                           }
                                                                       } else {
                                                                           Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   }
                                                               });
                                                           }
                                                           if (bp.isSubscribed("threemonthssubscription.society.egebook") && !three) {
                                                               //Toast.makeText(getApplicationContext(), "bp.isSubscribed(\"threemonthsubscription.society.egebook\") && !three", Toast.LENGTH_SHORT).show();
                                                               FirebaseApp.initializeApp(getApplicationContext());
                                                               FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                               CollectionReference docRef = db.collection("Users");
                                                               docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                       if (task.isSuccessful()) {
                                                                           QuerySnapshot document = task.getResult();
                                                                           if (!document.isEmpty()) {
                                                                               Log.d(TAG, "DocumentSnapshot data: " + document.getDocuments());
                                                                               ArrayList<Map<String, Object>> subs = new ArrayList<>();
                                                                               oo = document.getDocuments();
                                                                               for (int i = 0; i < oo.size(); i++) {
                                                                                   if (oo.get(i).get("uid").equals(getIntent().getStringExtra("id"))) {
                                                                                       subs = (ArrayList<Map<String, Object>>) oo.get(i).get("subscriptions");
                                                                                   }
                                                                               }
                                                                               Map<String, Object> map = new HashMap<String, Object>();
                                                                               map.put("isAdmin", false);
                                                                               map.put("uid", getIntent().getStringExtra("id"));
                                                                               Map<String, Object> mapSub = new HashMap<String, Object>();
                                                                               mapSub.put("subscriptionID", 1);
                                                                               mapSub.put("subscriptionStartDate", Timestamp.now());
                                                                               mapSub.put("subscriptionExpireDate", new Timestamp(Timestamp.now().getSeconds() + 7862400, 0));
                                                                               subs.add(mapSub);
                                                                               map.put("subscriptions", subs);
                                                                               db.collection("Users").document(getIntent().getStringExtra("id")).set(map);
                                                                           } else {
                                                                               Toast.makeText(getApplicationContext(), "Зарегистрируйтесь или войдите в свой аккаунт, чтобы оформить подписку", Toast.LENGTH_LONG).show();
                                                                           }
                                                                       } else {
                                                                           Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   }
                                                               });
                                                           }
                                                           if (bp.isSubscribed("sixmonthssubscription.society.egebook") && !six) {
                                                               //Toast.makeText(getApplicationContext(), "bp.isSubscribed(\"sixmonthsubscription.society.egebook\") && !six", Toast.LENGTH_SHORT).show();
                                                               FirebaseApp.initializeApp(getApplicationContext());
                                                               FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                               CollectionReference docRef = db.collection("Users");
                                                               docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                       if (task.isSuccessful()) {
                                                                           QuerySnapshot document = task.getResult();
                                                                           if (!document.isEmpty()) {
                                                                               Log.d(TAG, "DocumentSnapshot data: " + document.getDocuments());
                                                                               ArrayList<Map<String, Object>> subs = new ArrayList<>();
                                                                               oo = document.getDocuments();
                                                                               for (int i = 0; i < o.size(); i++) {
                                                                                   if (oo.get(i).get("uid").equals(getIntent().getStringExtra("id"))) {
                                                                                       subs = (ArrayList<Map<String, Object>>) oo.get(i).get("subscriptions");
                                                                                   }
                                                                               }
                                                                               Map<String, Object> map = new HashMap<String, Object>();
                                                                               map.put("isAdmin", false);
                                                                               map.put("uid", getIntent().getStringExtra("id"));
                                                                               Map<String, Object> mapSub = new HashMap<String, Object>();
                                                                               mapSub.put("subscriptionID", 2);
                                                                               mapSub.put("subscriptionStartDate", Timestamp.now());
                                                                               mapSub.put("subscriptionExpireDate", new Timestamp(Timestamp.now().getSeconds() + 15811200, 0));
                                                                               subs.add(mapSub);
                                                                               map.put("subscriptions", subs);
                                                                               db.collection("Users").document(getIntent().getStringExtra("id")).set(map);
                                                                           } else {
                                                                               Toast.makeText(getApplicationContext(), "Зарегистрируйтесь или войдите в свой аккаунт, чтобы оформить подписку", Toast.LENGTH_LONG).show();
                                                                           }
                                                                       } else {
                                                                           Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   }
                                                               });
                                                           }
                                                       }
                                                   }
                                               }
                                           });
        Log.d(TAG, FirebaseFirestore.getInstance().toString());
        DocumentReference docRef = db.collection("Courses").document(String.valueOf(getIntent().getIntExtra("theme", 0)));
        sub = getIntent().getBooleanExtra("sub", false);
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
                        Log.d("themeLessonInfo size", theme.getLessonInfo().size() + "");
                        for (int i = 0; i < theme.getLessonInfo().size(); i++) {
                            ArrayList<String> themesDeux = new ArrayList<>();
                            themesDeux.add(theme.getLessonInfo().get(i).get("lessonTopic").toString());
                            ArrayList<Map<String, Object>> adds = (ArrayList<Map<String, Object>>) theme.getLessonInfo().get(i).get("additionalSectionsData");
                            for (int j = 0; j < adds.size(); j++) {
                                themesDeux.add(adds.get(j).get("title").toString());
                                Log.d(i + " " + j, adds.get(j).get("title").toString());
                            }
                            themes.add(themesDeux);
                            Log.d(TAG, theme.getLessonInfo().get(i).get("lessonTopic").toString());
                        }
                        RecyclerView.Adapter mAdapter;
                        final RecyclerView rw = findViewById(R.id.recyclerViewThemes);
                        rw.setHasFixedSize(true);
                        rw.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        for (int i = 0; i < themes.size(); i++) {
                            themeNames.add(themes.get(i).get(0));
                            themes.get(i).remove(0);
                        }
                        mAdapter = new ThemesAdapter(themes, themeNames, getIntent().getIntExtra("theme", -12), R.layout.list_theme_element, sub);
                        rw.setAdapter(mAdapter);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with with ", task.getException());
                }
            }
        });



        /*Typeface face = Typeface.createFromAsset(getAssets(),
                "font/ralewayblack.ttf");*/
        //rw.setTypeface(face);
    }

    protected void onStart() {
        super.onStart();

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
