package com.sanPatel.expensetracker.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.sanPatel.expensetracker.R;

public class SignInActivity extends AppCompatActivity {

    private LinearLayout linearLayoutLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeWidgets();
        widgetsClickListener();
    }

    private void initializeWidgets() {
        // this method will initialize all the widgets.
        linearLayoutLogin = findViewById(R.id.linear_layout_login);
    }

    private void widgetsClickListener() {
        // this method will handle click listener for all widgets.
        linearLayoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, LoginActivity.class));
            }
        });
    }
}