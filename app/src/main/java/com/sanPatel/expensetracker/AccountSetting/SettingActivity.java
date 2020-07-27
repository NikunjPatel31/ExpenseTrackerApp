package com.sanPatel.expensetracker.AccountSetting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sanPatel.expensetracker.AsyncTask.MyAsyncTask;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.Datas.User;
import com.sanPatel.expensetracker.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    // widgets
    private Toolbar toolbar;
    private CircleImageView cirImgPhoto;
    private TextView tvUserName, tvEmail, tvPassword;

    // data variable
    User user = new User();

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
                try {
                    new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setTitle("Alert dialog")
                            .setMessage("This is a material dialog alert dialog.")
                            .show();
                    break;
                } catch (Exception e) {
                    Log.d(TAG, "onOptionsItemSelected: Exception: "+e.getLocalizedMessage());
                }
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
                }
                String fullName = user.getFirst_name() + " " + user.getLast_name();
                tvUserName.setText(fullName);
            }
        });
        myAsyncTask.execute();
    }
}