package com.example.kotprog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int  SECRET_KEY = 99;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    EditText userNameET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameET = findViewById(R.id.editTextUsername);
        passwordET = findViewById(R.id.editTextPassword);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        String username = userNameET.getText().toString();
        String password = passwordET.getText().toString();

        //Log.i(LOG_TAG, "Login: " + username + " " + password);

        if (username.isEmpty() || password.isEmpty() ) {
            Toast.makeText(MainActivity.this, "Email-cím és jelszó megadása kötelező!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startShopping();
                    Log.d(LOG_TAG, "Succesfullog in.");
                }
                else {
                    Log.d(LOG_TAG, "Unsuccesfullog in.");
                    Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginAsGuest(View view){
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startShopping();
                    Log.d(LOG_TAG, "Succesful anonymous log in.");
                }
                else {
                    Log.d(LOG_TAG, "Unsuccesful anonymous log in." + task.getException());
                    Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", 99);
        startActivity(intent);
    }

    private void startShopping(){
        Intent intent = new Intent(this, ShopListActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", userNameET.getText().toString());
        editor.putString("password", userNameET.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}