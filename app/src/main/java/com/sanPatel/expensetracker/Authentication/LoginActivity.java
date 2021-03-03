package com.sanPatel.expensetracker.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanPatel.expensetracker.Database.Firebase.FirebaseDBOperation;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.Datas.Expense;
import com.sanPatel.expensetracker.Datas.Wallet;
import com.sanPatel.expensetracker.HomeScreenActivity;
import com.sanPatel.expensetracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    // widgets
    private LinearLayout linearLayoutSignIn;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ContentLoadingProgressBar contentLoadingProgressBar;

    // firebase instance
    FirebaseAuth mAuth;

    // local variable
    String email, password;

    public void login(View view) {
        // this method will handle login button click listener
        if (validateFields()) {
            btnLogin.setVisibility(View.INVISIBLE);
            contentLoadingProgressBar.setVisibility(View.VISIBLE);
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initializeWidgets() {
        // this method will initialize all the widgets.
        linearLayoutSignIn = findViewById(R.id.linear_layout_sign_in);
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
        btnLogin = findViewById(R.id.button_login);
        contentLoadingProgressBar = findViewById(R.id.content_loading_progress_bar_login);
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
                contentLoadingProgressBar.setVisibility(View.INVISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
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
                retrieveExpense();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveExpense() {
        // this method will retrieve all the expense records from firebase.
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference().child("Expense");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mAuth.getUid())) {
                    // there is node under expense node
                    getExpense();
                } else {
                    //user had not made any expense. hence, there is no child in expense.
                    // go to next screen.
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getExpense() {
        final int[] count = {0};
        final int counter[] = {0};

        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Expense").child(mAuth.getUid());

        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    counter[0]++;
                    // Log.d(TAG, "onChildAdded: counter: "+counter[0]);
                    try {
                        Expense expense = new Expense();
                        expense.setExpense_id(Integer.parseInt(snapshot.getKey()));
                        expense.setExpense_title(snapshot.child("Title").getValue().toString());
                        expense.setExpense_description(snapshot.child("Desc").getValue().toString());
                        expense.setExpense_amount(Double.parseDouble(snapshot.child("Amount").getValue().toString()));
                        expense.setExpense_date(new SimpleDateFormat("dd-MM-yyyy").parse(snapshot.child("Date").getValue().toString()));
                        expense.setExpense_type(Integer.parseInt(snapshot.child("Type").getValue().toString()));
                        expense.setTime(snapshot.child("Time").getValue().toString());
                        expense.setSync(Integer.parseInt(snapshot.child("sync").getValue().toString()));
                        if (snapshot.child("wallet_id").exists()) {
                            expense.setWalletID(Integer.parseInt(snapshot.child("wallet_id").getValue().toString()));
                        } else {
                            expense.setWalletID(-1);
                        }
                        boolean expense_type;
                        if (expense.getExpense_type() == 0) {
                            expense_type = false;
                        } else {
                            expense_type = true;
                        }
                        SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(getApplicationContext());
                        databaseHelper.insertEntry(expense.getExpense_title(),
                                expense.getExpense_description(),
                                expense.getExpense_amount(),
                                new SimpleDateFormat("dd-MM-yyyy").format(expense.getExpense_date()),
                                expense.getTime(),
                                expense_type,
                                expense.getSync(),
                                expense.getWalletID());

                        if (counter[0] == count[0]) {
                            Log.d(TAG, "onChildAdded: data fetched successfully.");
                            retrieveWallet();
                        } else {
                            //Log.d(TAG, "onChildAdded: counter: "+counter[0]+"   count: "+count[0]);
                        }
                    } catch (Exception e) {

                    }
                } else {
                    Log.d(TAG, "onDataChange: does not exists");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count[0] = (int) snapshot.getChildrenCount();
                databaseReference.addChildEventListener(childEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveWallet() {
        // this method will retrieve all the wallet records from the firebase.

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Wallet");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mAuth.getUid())) {
                    // there is child under wallet node.
                    getWallet();
                } else {
                    // there is no child under wallet node. Because, user has not created any wallet
                    // go to next screen.
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWallet() {
        final int[] recordCount = {0};
        final int[] fetchedRecordCount = {0};
        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "retrieve wallet: onChildAdded: "+snapshot.toString());
                if (snapshot.exists()) {
                    Log.d(TAG, "onChildAdded: wallet is there");
                    fetchedRecordCount[0]++;
                    try {
                        Wallet wallet = new Wallet();
                        wallet.setWalletID(Integer.parseInt(snapshot.getKey()));
                        wallet.setWalletName(snapshot.child("Wallet_name").getValue().toString());
                        wallet.setInitialBalance(Double.parseDouble(snapshot.child("Wallet_initial_bal").getValue().toString()));
                        wallet.setTimeStamp(snapshot.child("Wallet_time_stamp").getValue().toString());
                        wallet.setWalletSync(Integer.parseInt(snapshot.child("Wallet_sync").getValue().toString()));
                        wallet.setDate(new SimpleDateFormat("dd-MM-yyyy").parse(snapshot.child("Wallet_data").getValue().toString()));

                        SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(getApplicationContext());
                        databaseHelper.insertWallet(wallet);

                        if (fetchedRecordCount[0] == recordCount[0]) {
                            // go to next screen.
                            Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "onChildAdded: EXCEPTION: "+e.getLocalizedMessage());
                    }
                } else {
                    // go to next screen.
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Wallet").child(mAuth.getUid());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((int) snapshot.getChildrenCount() == 0) {
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                recordCount[0] = (int) snapshot.getChildrenCount();
                db.addChildEventListener(childEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}