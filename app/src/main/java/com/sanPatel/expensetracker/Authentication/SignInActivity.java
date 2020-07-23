package com.sanPatel.expensetracker.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sanPatel.expensetracker.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity {

    //widgets
    private LinearLayout linearLayoutLogin;
    private EditText etFirstName, etLastName , etEmail, etPassword;
    private CircleImageView cirImgProfilePhoto;

    //firebase
    private FirebaseAuth mAuth;

    //instance variable
    String firstName, lastName, email, password;
    Uri imageUri;

    //final variables
    private int GALLERY_REQUEST_CODE = 1, READ_EXTERNAL_STORAGE_REQUSET_CODE = 2;

    //flag variables
    private boolean storagePermission = false;

    public void signIn(View view) {
        // this handle the click listener for sign In button
        if (!storagePermission) {
            if (validateFields()) {

            }
        } else {
            // show message "Storage permission not given.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeWidgets();
        widgetsClickListener();
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

    private void initializeWidgets() {
        // this method will initialize all the widgets.
        linearLayoutLogin = findViewById(R.id.linear_layout_login);
        etFirstName = findViewById(R.id.edit_text_first_name);
        etLastName = findViewById(R.id.edit_text_last_name);
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
        cirImgProfilePhoto = findViewById(R.id.circular_image_user_account);
    }

    private void initializeFirebaseinstance() {
        // this method will initialize all the instance of firebase.
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
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
        firstName = etFirstName.getText().toString();
        lastName = etLastName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (!(TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password))) {
            // all the required fields are validated.
            return true;
        } else {
            // fields are not validated.
            Toast.makeText(this, "Fields are not validated", Toast.LENGTH_SHORT).show();
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
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            } else {
                // there is some problem in selecting image from the gallery.
                Toast.makeText(this, "Error in selecting image.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                if (result != null) {
                    Uri resultUri = result.getUri();
                    cirImgProfilePhoto.setImageURI(resultUri);
                }
            }
        }
    }
}