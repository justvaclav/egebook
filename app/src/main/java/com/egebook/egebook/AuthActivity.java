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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        TextView regButton = (findViewById(R.id.registerTextView));
        //TextView loginButtonDeux = (findViewById(R.id.loginTextView));
        final EditText loginText = (findViewById(R.id.loginEditText));
        final EditText passwordText = (findViewById(R.id.passwordEditText));


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regButton.setVisibility(View.INVISIBLE);
                mAuth.createUserWithEmailAndPassword(loginText.getText().toString(), passwordText.getText().toString())
                        .addOnCompleteListener(AuthActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();
                                Intent intent = new Intent(AuthActivity.this, DispActivity.class);
                                intent.putExtra("id", uid);
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
                                                Map<String, Object> map = new HashMap<String, Object>();
                                                map.put("isAdmin", false);
                                                map.put("uid", uid);
                                                map.put("subscriptions", subs);
                                                db.collection("Users").document(uid).set(map);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Зарегистрируйтесь или войдите в свой аккаунт, чтобы оформить подписку", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                Toast.makeText(getApplicationContext(), "Аккаунт создан.", Toast.LENGTH_LONG);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AuthActivity.this, "Войти не удалось. Проверьте\nправильность логина и пароля.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        /*loginButtonDeux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(loginText.getText().toString(), passwordText.getText().toString())
                        .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(AuthActivity.this, ThemesActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(AuthActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        });*/
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
}
