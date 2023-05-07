package com.example.kotprog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    EditText userNameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordAgainET;
    EditText phoneET;
    EditText adressET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secretKey = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secretKey != 99){
            finish();
        }

        userNameET = findViewById(R.id.editTextUsername);
        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);
        passwordAgainET = findViewById(R.id.editTextPasswordAgain);
        phoneET = findViewById(R.id.editTextPhone);
        adressET = findViewById(R.id.editTextAdress);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        userNameET.setText(userName);
        passwordET.setText(password);

        mAuth = FirebaseAuth.getInstance();

    }

    public void cancel(View view) {
        finish();
    }

    private void startShopping(){
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);
    }

    public void register(View view) {
        String username = userNameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordAgain = passwordAgainET.getText().toString();
        String phone = phoneET.getText().toString();
        String adress = adressET.getText().toString();

        if (!password.equals(passwordAgain)){
            Log.e(LOG_TAG, "PW != PWA");

            return;
        }
        Log.i(LOG_TAG, "Regisztráció: " + username + " " + email + " " + password);

        if (email.isEmpty() || password.isEmpty() ) {
            Toast.makeText(RegisterActivity.this, "Email-cím és jelszó megadása kötelező!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(LOG_TAG, "Succesful register.");
                    startShopping();
                }
                else {
                    Log.d(LOG_TAG, "Unsuccesful register.");
                    Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}