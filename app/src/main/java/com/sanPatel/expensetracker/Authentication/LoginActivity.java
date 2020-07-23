package com.sanPatel.expensetracker.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.sanPatel.expensetracker.R;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout linearLayoutSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeWidgets();
        widgetsClickListener();
    }

    private void initializeWidgets() {
        // this method will initialize all the widgets.
        linearLayoutSignIn = findViewById(R.id.linear_layout_sign_in);
    }

    private void widgetsClickListener() {
        // this method will handle click listener for all widgets.
        linearLayoutSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignInActivity.class));
            }
        });
    }
}