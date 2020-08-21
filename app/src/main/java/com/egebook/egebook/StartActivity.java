package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    private final String TAG = "StartActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    List<DocumentSnapshot> o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD, null);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //getString(R.string.default_web_client_id);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                (OnCompleteListener<Void>) task -> {
                });
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        TextView loginButton = findViewById(R.id.registerTextView3);
        TextView authButton = findViewById(R.id.registerTextView4);
        TextView skipButton = findViewById(R.id.registerTextView5);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
                finish();
            }
        });
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, AuthActivity.class));
                finish();
            }
        });
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, DispActivity.class));
                finish();
            }
        });
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task;
            try {
                // Google Sign In was successful, authenticate with Firebase
                task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                //Toast.makeText(this, "onActivityResult successful", Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
                Log.w(TAG, "Google sign in failed", e);
                //Toast.makeText(this, "Google sign in failed with ApiException", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(StartActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseApp.initializeApp(getApplicationContext());
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference docRef;
                        //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                        onActivityResult(1, 1, new Intent());
                        docRef = db.collection("Users");
                        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot document = task.getResult();
                                    if (!document.isEmpty()) {
                                        ArrayList<Map<String, Object>> subs = new ArrayList<>();
                                        o = document.getDocuments();
                                        boolean exists = false;
                                        String id = user == null ? "" :  user.getUid();
                                        for (int i = 0; i < o.size(); i++) {
                                            if (o.get(i).get("uid").equals(id)) {
                                                exists = true;
                                                break;
                                            }
                                        }
                                        //Toast.makeText(getApplicationContext(), "user exists:" + exists, Toast.LENGTH_LONG).show();
                                        if (!exists) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("uid", user.getUid());
                                            map.put("subscriptions", new ArrayList<>());
                                            map.put("isAdmin", false);
                                            db.collection("Users").document(user.getUid()).set(map); //ОБЯЗАТЕЛЬНО ПЕРСМОТРЕТЬ ПРИ ИЗМЕНЕНИИ ЛЭЙАУТА У AdminAdapter
                                        }
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                        startActivity(new Intent(StartActivity.this, SubscribeActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        //Toast.makeText(getApplicationContext(), "signInWithCredential:failure", Toast.LENGTH_LONG).show();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
        //Toast.makeText(getApplicationContext(), "signIn", Toast.LENGTH_LONG).show();
    }
}
