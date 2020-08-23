package com.sanPatel.expensetracker.AccountSetting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sanPatel.expensetracker.AsyncTask.MyAsyncTask;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.Datas.User;
import com.sanPatel.expensetracker.Fragment.EditProfileFragment;
import com.sanPatel.expensetracker.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity implements EditProfileFragment.ButtonClickListener {


    private static final String TAG = "SettingActivity";

    // widgets
    private Toolbar toolbar;
    private CircleImageView cirImgPhoto;
    private TextView tvUserName, tvEmail, tvPassword;

    // data variable
    private User user = new User();
    private Uri imageUri;

    // static variable
    private final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initializeWidgets();

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("");
        getUserDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_profile:
                //Toast.makeText(this, "Edit selected.", Toast.LENGTH_SHORT).show();
                EditProfileFragment.display(getSupportFragmentManager());
                break;
            case R.id.menu_change_photo:
                changeProfilePhoto();
                break;
            default:
        }
        return true;
    }

    private void initializeWidgets() {
        // this method will initialize all the widgets.
        toolbar = findViewById(R.id.toolbar_setting);
        tvUserName = findViewById(R.id.text_view_user_name);
        tvEmail = findViewById(R.id.text_view_email_value);
        tvPassword = findViewById(R.id.text_view_password_value);
        cirImgPhoto = findViewById(R.id.circular_image_user_account);
    }

    private void changeProfilePhoto() {
        // this method will change profile photo.
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            // photo is selected from gallery.
            if (data != null) {
                imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
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
                        cirImgPhoto.setImageBitmap(bitmap);
                        MyAsyncTask myAsyncTask = new MyAsyncTask();
                        myAsyncTask.setAsyncTaskListener(new MyAsyncTask.AsyncTaskListener() {
                            @Override
                            public void setBackgroundTask() {
                                SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(SettingActivity.this);
                                boolean isPhotoSaved = databaseHelper.updateUserPhoto(imageViewToByte(cirImgPhoto));
                                if (!isPhotoSaved) {
                                    // error in saving photo to database.
                                    cirImgPhoto.setImageResource(R.drawable.user_account); 
                                }
                            }

                            @Override
                            public void setPostExecuteTask() {

                            }
                        });
                        myAsyncTask.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void getUserDetails() {
        // this method will fetch photo and email of the user.
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setAsyncTaskListener(new MyAsyncTask.AsyncTaskListener() {
            @Override
            public void setBackgroundTask() {
                SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(SettingActivity.this);
                Cursor cursor = databaseHelper.getUserDetails();
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        user.setUser_id(cursor.getString(0));
                        user.setFirst_name(cursor.getString(1));
                        user.setLast_name(cursor.getString(2));
                        user.setEmail(cursor.getString(3));
                        byte[] userPhotoInByte = cursor.getBlob(4);
                        if (userPhotoInByte != null) {
                            Bitmap userPhoto = BitmapFactory.decodeByteArray(userPhotoInByte, 0, userPhotoInByte.length);
                            user.setPhoto(userPhoto);
                        }
                    }
                }
            }

            @Override
            public void setPostExecuteTask() {
                tvEmail.setText(user.getEmail());
                if (user.getPhoto() != null) {
                    cirImgPhoto.setImageBitmap(user.getPhoto());
                } else {
                 //   cirImgPhoto.setImageResource(R.drawable.ic_baseline_close_24);
                }
                String fullName = user.getFirst_name() + " " + user.getLast_name();
                tvUserName.setText(fullName);
            }
        });
        myAsyncTask.execute();
    }

    @Override
    public void onButtonClickListener(String firstName, String lastName) {
        tvUserName.setText(firstName+" "+lastName);
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