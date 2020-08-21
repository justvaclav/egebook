package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_ID;

public class SubscribeActivity extends AppCompatActivity implements IBillingHandler {
    String TAG = "SubscribeActivity";
    List<DocumentSnapshot> o;
    private String mSkuId = "onemonthsubscription.society.egebook";
    BillingProcessor bp;
    boolean one = false, three = false, six = false, nine = false;
    int selection = 0;
    FirebaseAuth mAuth;
    TextView subButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        final RecyclerView rw = findViewById(R.id.subscribeRecyclerView);
        final ArrayList<String[]> themes = new ArrayList<>();
        themes.add(new String[]{"Обществознание", "Эта подписка предоставляет вам доступ к видео, тестам и конспектам ЕГЭBook по всем темам Обществознания", "999\u20BD - 1 мес"});
        themes.add(new String[]{"Обществознание", "Эта подписка предоставляет вам доступ к видео, тестам и конспектам ЕГЭBook по всем темам Обществознания", "2490\u20BD - 3 мес"});
        themes.add(new String[]{"Обществознание", "Эта подписка предоставляет вам доступ к видео, тестам и конспектам ЕГЭBook по всем темам Обществознания", "4990\u20BD - 6 мес"});
        //themes.add(new String[]{"Обществознание", "Эта подписка предоставляет вам доступ к видео, тестам и конспектам ЕГЭBook по всем темам Обществознания", "7290\u20BD - 9 мес"});
        //themes.add(new String[]{"Обществознание", "Специальная отладочная бесплатная подписка на месяц (потом будет выпилено)", "0\u20BD - 54 мес"});
        RecyclerView.Adapter mAdapter;
        rw.setHasFixedSize(true);
        rw.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SubscribeAdapter(themes);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider));
        subButton = (TextView) findViewById(R.id.registerTextView6);
        rw.addItemDecoration(itemDecorator);
        rw.setAdapter(mAdapter);
        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAheNvLZOxfXajgqs0yVtQTrtRKOp5LKFeFYpWBAsHBAj2YJ5JU4oX5JAKyDFLcqRV4FoX56IXWfjVTsnMdfCPLyztyuP9Kmwst7QiJzACMV945Ix0J7FlVbm/MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAheNvLZOxfXajgqs0yVtQTrtRKOp5LKFeFYpWBAsHBAj2YJ5JU4oX5JAKyDFLcqRV4FoX56IXWfjVTsnMdfCPLyztyuP9Kmwst7QiJzACMV945Ix0J7FlVbm/vKoG2vsz7ycydByyVoHiWTFsQ5BNuq5qIeS1GEakXvXsIGciAZuTbAbnqvnnGqVoGHpMpPF8L6WWS0Y0kYO5iKmbqr1ap3HJEZXdrHWS41C5T5Cg0v1HdacO7fUwKs1fLVBqFDbvLSPuAqF58fo1VUgvAUuf5ruc8fsYcQOWBTfd6Rtq8Lt3RaDF972d8oiNHTeL6Wkj5FpjZmAGNNGb6rK7OwIDAQAB", this);
        bp.initialize();
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
                        o = document.getDocuments();
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        for (int i = 0; i < o.size(); i++) {
                            if (o.get(i).get("uid").equals(uid)) {
                                subs = (ArrayList<Map<String, Object>>) o.get(i).get("subscriptions");
                            }
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
                        Log.w(TAG, "1x:"+one+", 3x:"+three+", 6x:"+six+", 9x:"+nine);
                        Log.w(TAG, "SUBSCRIBES: 1x:"+bp.isSubscribed("onemonthsubscription.society.egebook")+", 3x:"+bp.isSubscribed("threemonthssubscription.society.egebook")+", 6x:"+bp.isSubscribed("sixmonthssubscription.society.egebook"));




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
                                        o = document.getDocuments();
                                        for (int i = 0; i < o.size(); i++) {
                                            if (o.get(i).get("uid").equals(getIntent().getStringExtra("id"))) {
                                                subs = (ArrayList<Map<String, Object>>) o.get(i).get("subscriptions");
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
                                        Intent intent = new Intent(SubscribeActivity.this, DispActivity.class);
                                        intent.putExtra("id", getIntent().getStringExtra("id"));
                                        startActivity(intent);
                                        finish();
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
                                            o = document.getDocuments();
                                            for (int i = 0; i < o.size(); i++) {
                                                if (o.get(i).get("uid").equals(getIntent().getStringExtra("id"))) {
                                                    subs = (ArrayList<Map<String, Object>>) o.get(i).get("subscriptions");
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
                                            Intent intent = new Intent(SubscribeActivity.this, DispActivity.class);
                                            intent.putExtra("id", getIntent().getStringExtra("id"));
                                            startActivity(intent);
                                            finish();
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
                                            o = document.getDocuments();
                                            for (int i = 0; i < o.size(); i++) {
                                                if (o.get(i).get("uid").equals(getIntent().getStringExtra("id"))) {
                                                    subs = (ArrayList<Map<String, Object>>) o.get(i).get("subscriptions");
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
                                            Intent intent = new Intent(SubscribeActivity.this, DispActivity.class);
                                            intent.putExtra("id", getIntent().getStringExtra("id"));
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Зарегистрируйтесь или войдите в свой аккаунт, чтобы оформить подписку", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        subButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                switch (selection) {
                                    case 0: {
                                        bp.subscribe(SubscribeActivity.this, "onemonthsubscription.society.egebook");
                                        break;
                                    }
                                    case 1: {
                                        bp.subscribe(SubscribeActivity.this, "threemonthssubscription.society.egebook");
                                        break;
                                    }
                                    case 2: {
                                        bp.subscribe(SubscribeActivity.this, "sixmonthssubscription.society.egebook");
                                        break;
                                    }
                                    case 3: {
                                        bp.subscribe(SubscribeActivity.this, "ninemonthssubscription.society.egebook");
                                        break;
                                    }
                                }
                            }
                        });

                        rw.addOnItemTouchListener(
                            new RecyclerItemClickListener(getApplicationContext(), rw, new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    //Toast.makeText(getApplicationContext(), "Politics", Toast.LENGTH_LONG).show();
                                    if (!getIntent().getStringExtra("id").equals("")) {
                                        switch (position) {
                                            case 0: {
                                                selection = 0;
                                                subButton.setText("ПРИОБРЕСТИ НА 1 МЕСЯЦ");
                                                bp.subscribe(SubscribeActivity.this, "onemonthsubscription.society.egebook");
                                                break;
                                            }
                                            case 1: {
                                                selection = 1;
                                                subButton.setText("ПРИОБРЕСТИ НА 3 МЕСЯЦА");
                                                bp.subscribe(SubscribeActivity.this, "threemonthssubscription.society.egebook");
                                                break;
                                            }
                                            case 2: {
                                                selection = 2;
                                                subButton.setText("ПРИОБРЕСТИ НА 6 МЕСЯЦЕВ");
                                                bp.subscribe(SubscribeActivity.this, "sixmonthssubscription.society.egebook");
                                                break;
                                            }
                                            case 3: {
                                                selection = 3;
                                                subButton.setText("ПРИОБРЕСТИ НА 9 МЕСЯЦЕВ");
                                                bp.subscribe(SubscribeActivity.this, "ninemonthssubscription.society.egebook");
                                                break;
                                            }
                                            case 4: {
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
                                                                o = document.getDocuments();
                                                                for (int i = 0; i < o.size(); i++) {
                                                                    if (o.get(i).get("uid").equals(getIntent().getStringExtra("id"))) {
                                                                        subs = (ArrayList<Map<String, Object>>) o.get(i).get("subscriptions");
                                                                    }
                                                                }
                                                                Map<String, Object> map = new HashMap<String, Object>();
                                                                map.put("isAdmin", true);
                                                                map.put("uid", getIntent().getStringExtra("id"));
                                                                Map<String, Object> mapSub = new HashMap<String, Object>();
                                                                mapSub.put("subscriptionID", 3);
                                                                mapSub.put("subscriptionStartDate", Timestamp.now());
                                                                mapSub.put("subscriptionExpireDate", new Timestamp(Timestamp.now().getSeconds() + 141952200, 0));
                                                                subs.add(mapSub);
                                                                map.put("subscriptions", subs);
                                                                db.collection("Users").document(getIntent().getStringExtra("id")).set(map);
                                                                Intent intent = new Intent(SubscribeActivity.this, DispActivity.class);
                                                                intent.putExtra("id", getIntent().getStringExtra("id"));
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Зарегистрируйтесь или войдите в свой аккаунт, чтобы оформить подписку", Toast.LENGTH_LONG).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                break;
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Зарегистрируйтесь или войдите в свой аккаунт, чтобы оформить подписку", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onLongItemClick(View view, int position) {
                                    //Toast.makeText(getApplicationContext(), themes.get(position) + " EP", Toast.LENGTH_LONG).show();
                                }
                            })
                    );
                    } else {
                        Toast.makeText(getApplicationContext(), "Зарегистрируйтесь или войдите в свой аккаунт, чтобы оформить подписку", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments())
        {
            if (fragment != null)
            {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Toast.makeText(getApplicationContext(), "Благодарим за оформление подписки!", Toast.LENGTH_LONG).show();
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
                        o = document.getDocuments();
                        for (int i = 0; i < o.size(); i++) {
                            if (o.get(i).get("uid").equals(getIntent().getStringExtra("id"))) {
                                subs = (ArrayList<Map<String, Object>>) o.get(i).get("subscriptions");
                            }
                        }
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("isAdmin", false);
                        map.put("uid", getIntent().getStringExtra("id"));
                        Map<String, Object> mapSub = new HashMap<String, Object>();
                        mapSub.put("subscriptionStartDate", Timestamp.now());
                        switch (productId) {
                            case "onemonthsubscription.society.egebook": {
                                mapSub.put("subscriptionID", 0);
                                mapSub.put("subscriptionExpireDate", new Timestamp(Timestamp.now().getSeconds() + 2678400, 0));
                                break;
                            }
                            case "threemonthssubscription.society.egebook":{
                                mapSub.put("subscriptionID", 1);
                                mapSub.put("subscriptionExpireDate", new Timestamp(Timestamp.now().getSeconds() + 7862400, 0));
                                break;
                            }
                            case "sixmonthssubscription.society.egebook":{
                                mapSub.put("subscriptionID", 2);
                                mapSub.put("subscriptionExpireDate", new Timestamp(Timestamp.now().getSeconds() + 15811200, 0));
                                break;
                            }
                        }
                        subs.add(mapSub);
                        map.put("subscriptions", subs);
                        db.collection("Users").document(getIntent().getStringExtra("id")).set(map);
                        Intent intent = new Intent(SubscribeActivity.this, DispActivity.class);
                        intent.putExtra("id", getIntent().getStringExtra("id"));
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onPurchaseHistoryRestored() {
        for(String sku : bp.listOwnedSubscriptions()) {

        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(getApplicationContext(), "Во время оформления подписки произошла ошибка: " + error.toString(), Toast.LENGTH_LONG).show();
        if (errorCode == 102) {
            switch (selection) {
                case 0: {
                    bp.subscribe(null, "onemonthsubscription.society.egebook");
                    break;
                }
                case 1: {
                    bp.subscribe(null, "threemonthssubscription.society.egebook");
                    break;
                }
                case 2: {
                    bp.subscribe(null, "sixmonthssubscription.society.egebook");
                    break;
                }
                case 3: {
                    bp.subscribe(null, "ninemonthssubscription.society.egebook");
                    break;
                }
            }
        }
    }

    @Override
    public void onBillingInitialized() {
        String str = "autoRenewings:\n";
        bp.loadOwnedPurchasesFromGoogle();
        try {if (bp.getSubscriptionTransactionDetails("onemonthsubscription.society.egebook").purchaseInfo.purchaseData.autoRenewing) {
            str += "onemonthsubscription.society.egebook is autoRenewing\n";
        }}
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            if (bp.getSubscriptionTransactionDetails("threemonthssubscription.society.egebook").purchaseInfo.purchaseData.autoRenewing) {
                str += "threemonthssubscription.society.egebook is autoRenewing\n";
            }}
            catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {if (bp.getSubscriptionTransactionDetails("sixmonthssubscription.society.egebook").purchaseInfo.purchaseData.autoRenewing) {
            str += "sixmonthssubscription.society.egebook is autoRenewing\n";
        }}
        catch (NullPointerException e) {
                e.printStackTrace();
                }
        //Toast.makeText(getApplicationContext(), "sixmonthssubscription.society.egebook is autoRenewing", Toast.LENGTH_SHORT).show();
    }
}
