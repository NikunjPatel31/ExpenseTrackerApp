package com.sanPatel.expensetracker.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.sanPatel.expensetracker.R;

public class ForgotPassword extends AppCompatActivity {

    // widgets
    private EditText etEmail;
    private CoordinatorLayout coorLayForgotPassword;

    // Firebase instances
    private FirebaseAuth mAuth;

    // instance
    String errorMessage = "Email is required";

    public void forgotPassword(View view) {
        // this method will handle send button click.
        // check if there is internet connectivity
        if (isNetworkConnected()) {
            // internet connectivity is available
            // check if fields are empty or not
            if (isValidate()) {
                // fields are not empty
                String email = etEmail.getText().toString();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // email is send successfully.
                        // show toast message
                        Toast.makeText(ForgotPassword.this, "Email is sent", Toast.LENGTH_SHORT).show();
                        // send the user back to login screen.
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed to sent email
                        // show toast message
                        Toast.makeText(ForgotPassword.this, "Error in sending email", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // fields are empty
                // show toast message
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        } else {
            // internet connectivity is not available
            // show snackbar
            final Snackbar snackbar = Snackbar.make(coorLayForgotPassword,"No Internet Connection",Snackbar.LENGTH_SHORT)
                    .setActionTextColor(Color.YELLOW)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackbar.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeWidgets();
        initializeFirebaseInstance();
    }

    private void initializeWidgets() {
        // this method will initialize all the widgets
        etEmail = findViewById(R.id.edit_text_email);
        coorLayForgotPassword = findViewById(R.id.coordinator_layout_forgot_password);
    }

    private void initializeFirebaseInstance() {
        // this method will initialize all firebase instance.
        mAuth = FirebaseAuth.getInstance();
    }

    private boolean isValidate() {
        String email = etEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            // field is empty
            errorMessage = "Email is required";
            return false;
        } else {
            // field is not empty
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // email is valid
                return true;
            } else {
                // email is not valid
                errorMessage = "Email is not valid";
                return false;
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}