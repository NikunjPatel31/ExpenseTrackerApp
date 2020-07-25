package com.sanPatel.expensetracker.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.HomeScreenActivity;
import com.sanPatel.expensetracker.R;

public class LoginActivity extends AppCompatActivity {
    // widgets
    private LinearLayout linearLayoutSignIn;
    private EditText etEmail, etPassword;

    // firebase instance
    FirebaseAuth mAuth;

    // local variable
    String email, password;

    public void login(View view) {
        // this method will handle login button click listener
        if (validateFields()) {
            loginUser();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initializeWidgets();
        initializeFirebaseWidgets();
        widgetsClickListener();
    }

    private void initializeWidgets() {
        // this method will initialize all the widgets.
        linearLayoutSignIn = findViewById(R.id.linear_layout_sign_in);
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
    }

    private void initializeFirebaseWidgets() {
        // this method will initialize firebase instance
        mAuth = FirebaseAuth.getInstance();
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

    private boolean validateFields() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (!(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))) {
            // all the required fields are validated.
            return true;
        }
        Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void loginUser() {
        // this method will login the user with email and password.
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // user login successful.
                            // retrieve user's first_name and last_name.
                            retrieveUserData();
                            //Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                        } else {
                            // error in user login
                            Toast.makeText(LoginActivity.this, "Error while login.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidUserException) {
                    String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

                    if(errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                        Toast.makeText(LoginActivity.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
                    }
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    String errorCode = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();

                    if(errorCode.equals("ERROR_INVALID_EMAIL")) {
                        Toast.makeText(LoginActivity.this, "Incorrect email.", Toast.LENGTH_SHORT).show();
                    }
                } else if (e instanceof FirebaseAuthUserCollisionException) {
                    String errorCode = ((FirebaseAuthUserCollisionException) e).getErrorCode();
                    if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                        Toast.makeText(LoginActivity.this, "Email address is already in use.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void retrieveUserData() {
        // retrieve user data.
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getUid());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SqliteDatabaseHelper db = new SqliteDatabaseHelper(LoginActivity.this);
                db.insertUserData(mAuth.getUid(),
                        snapshot.child("First_name").getValue().toString(),
                        snapshot.child("Last_name").getValue().toString(),
                        email,
                        null);
                Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}