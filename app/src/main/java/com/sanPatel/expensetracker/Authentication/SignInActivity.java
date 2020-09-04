package com.sanPatel.expensetracker.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanPatel.expensetracker.AsyncTask.MyAsyncTask;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.HomeScreenActivity;
import com.sanPatel.expensetracker.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity {

    //widgets
    private LinearLayout linearLayoutLogin;
    private EditText etFirstName, etLastName , etEmail, etPassword;
    private CircleImageView cirImgProfilePhoto;
    private Button btnSignIn;
    private ContentLoadingProgressBar contentLoadingProgressBar;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //SqliteDatabaseHelper
    private SqliteDatabaseHelper databaseHelper;

    //async
    private MyAsyncTask myAsyncTask;

    //local variable
    private String firstName, lastName, email, password;
    private Uri imageUri;

    //final variables
    private final int GALLERY_REQUEST_CODE = 1, READ_EXTERNAL_STORAGE_REQUSET_CODE = 2;

    //flag variables
    private boolean photoSelected = false;
    private boolean storagePermission = false;

    public void signIn(View view) {
        // this handle the click listener for sign In button
        if (storagePermission) {
            if (validateFields()) {
                btnSignIn.setVisibility(View.INVISIBLE);
                contentLoadingProgressBar.setVisibility(View.VISIBLE);
                signIn();
            }
        } else {
            // show message "Storage permission not given.
            Toast.makeText(this, "Storage permission not given.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        initializeWidgets();
        widgetsClickListener();
        databaseHelper = new SqliteDatabaseHelper(SignInActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeFirebaseinstance();
        if (!storagePermission) {
            // if storage permission == false, request will initiate.
            getStoragePermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        photoSelected = false;
        storagePermission = checkStoragePermission();
    }

    private void initializeWidgets() {
        // this method will initialize all the widgets.
        linearLayoutLogin = findViewById(R.id.linear_layout_login);
        etFirstName = findViewById(R.id.edit_text_first_name);
        etLastName = findViewById(R.id.edit_text_last_name);
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
        cirImgProfilePhoto = findViewById(R.id.circular_image_user_account);
        btnSignIn = findViewById(R.id.button_sign_in);
        contentLoadingProgressBar = findViewById(R.id.content_loading_progress_bar_sign_in);
    }

    private void initializeFirebaseinstance() {
        // this method will initialize all the instance of firebase.
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }
    }

    private void widgetsClickListener() {
        // this method will handle click listener for all widgets.
        linearLayoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, LoginActivity.class));
            }
        });

        cirImgProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storagePermission) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                } else {
                    Toast.makeText(SignInActivity.this, "Storage permission not given.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateFields() {
        // this method will validate all required fields.
        firstName = etFirstName.getText().toString().trim();
        lastName = etLastName.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString();

        if (!(TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password))) {
            // all the required fields are validated.
            return true;
        } else {
            // fields are not validated.
            Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(SignInActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED) {
            return  true;
        } else {
            return false;
        }
    }

    private void getStoragePermission() {
        // this method will request storage permission from the user.
        ActivityCompat.requestPermissions(SignInActivity.this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_EXTERNAL_STORAGE_REQUSET_CODE);

        storagePermission = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            storagePermission = true;
        } else {
            storagePermission = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            // image is selected...
            if (data != null) {
                imageUri = data.getData();
            } else {
                // photo is not selected.
                photoSelected = false;
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                if (result != null) {
                    Uri resultUri = result.getUri();

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(resultUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        cirImgProfilePhoto.setImageBitmap(bitmap);
                        photoSelected = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void signIn() {
        // this method will create new account for the user.
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // new account created successful.
                    // store user data inside the database.
                    // navigate to the home screen.
                    DatabaseReference db = mDatabase.child("User").child(mAuth.getUid());
                    db.child("First_name").setValue(firstName);
                    db.child("Last_name").setValue(lastName);
                    myAsyncTask = new MyAsyncTask();
                    myAsyncTask.setAsyncTaskListener(new MyAsyncTask.AsyncTaskListener() {
                        @Override
                        public void setBackgroundTask() {
                            if (photoSelected) {
                                databaseHelper.insertUserData(mAuth.getUid(), firstName, lastName, email, imageViewToByte(cirImgProfilePhoto));
                            } else {
                                databaseHelper.insertUserData(mAuth.getUid(), firstName, lastName, email, null);
                            }

                        }

                        @Override
                        public void setPostExecuteTask() {
                            // navigate to the home screen
                            Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                    myAsyncTask.execute();
                } else {
                    // error in creating new account.
                    contentLoadingProgressBar.setVisibility(View.INVISIBLE);
                    btnSignIn.setVisibility(View.VISIBLE);
                    Toast.makeText(SignInActivity.this, "Error in creating account.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                contentLoadingProgressBar.setVisibility(View.INVISIBLE);
                btnSignIn.setVisibility(View.VISIBLE);
                if (e instanceof FirebaseAuthInvalidUserException) {
                    String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

                    if(errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                        Toast.makeText(SignInActivity.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
                    }
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    String errorCode = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();

                    if(errorCode.equals("ERROR_INVALID_EMAIL")) {
                        Toast.makeText(SignInActivity.this, "Incorrect email.", Toast.LENGTH_SHORT).show();
                    }
                } else if (e instanceof FirebaseAuthUserCollisionException) {
                    String errorCode = ((FirebaseAuthUserCollisionException) e).getErrorCode();
                    if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE"))
                    {
                        Toast.makeText(SignInActivity.this, "Email address is already in use.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private byte[] imageViewToByte(ImageView imageView) {
        // this method will convert image view into the byte.
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}