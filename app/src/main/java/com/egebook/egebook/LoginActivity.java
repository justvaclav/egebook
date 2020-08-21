package com.egebook.egebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    final String TAG = "LoginActivity";
    EditText login, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (EditText) findViewById(R.id.loginEditText);
        password = findViewById(R.id.passwordEditText);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    //redirect
                    //Toast.makeText(getApplicationContext(), "onAuthStateChanged: user != null", Toast.LENGTH_LONG).show();

                } else {
                    // User is signed out
                    //Toast.makeText(getApplicationContext(), "onAuthStateChanged: user signed out", Toast.LENGTH_LONG).show();
                }

            }
        };
        TextView loginButton = findViewById(R.id.loginTextView);
        loginButton.setOnClickListener(view -> {
            //startSignInIntent();
            mAuth.signInWithEmailAndPassword(login.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmailAndPassword:success");
                            Intent intent = new Intent(LoginActivity.this, DispActivity.class);
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            intent.putExtra("id", uid);
                            //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Войти в аккаунт не удалось",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            GoogleSignInResult result =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "signin error";
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton("Login Fail", null).show();
            }
        } }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
    }*/
}
