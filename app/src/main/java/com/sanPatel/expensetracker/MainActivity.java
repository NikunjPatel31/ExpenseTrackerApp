package com.sanPatel.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sanPatel.expensetracker.Authentication.LoginActivity;
import com.sanPatel.expensetracker.Authentication.SignInActivity;

public class MainActivity extends AppCompatActivity {

    // firebase instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebaseInstances();

        if (mAuth.getCurrentUser() == null) {
            // there is no user. navigate to the login screen.
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // there is user. navigate to homeScreen.
            Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeFirebaseInstances() {
        // this method will initialize firebase instance.
        mAuth = FirebaseAuth.getInstance();
    }
}
