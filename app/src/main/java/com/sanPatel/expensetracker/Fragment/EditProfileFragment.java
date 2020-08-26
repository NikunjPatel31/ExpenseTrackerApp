package com.sanPatel.expensetracker.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanPatel.expensetracker.AccountSetting.SettingActivity;
import com.sanPatel.expensetracker.AsyncTask.MyAsyncTask;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.R;

import static androidx.core.content.ContextCompat.getSystemService;

public class EditProfileFragment extends DialogFragment {

    public interface ButtonClickListener {
        void onButtonClickListener(String firstName, String lastName);
    }

    private static final String TAG = "EditProfileFragment";

    // widget
    private MaterialToolbar toolbar;
    private EditText etFirstName, etLastName;
    private Button btnSave;

    // firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    // SqliteDatabase
    private SqliteDatabaseHelper databaseHelper;

    // myAsynTask
    private MyAsyncTask myAsyncTask = new MyAsyncTask();

    // local variables.
    private String firstName, lastName;

    // buttonClickedInterface
    private static ButtonClickListener buttonClickListener;

    public static EditProfileFragment display(FragmentManager fragmentManager) {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        editProfileFragment.show(fragmentManager,TAG);
        return editProfileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile_dialog_fragment,container,false);
        initalizeWidgets(view);
        initializeFirebaseWidgets();
        widgetClickListener();
        return view;
    }

    private void initalizeWidgets(View view) {
        // this method will initialize all the widgets.
        toolbar = view.findViewById(R.id.material_toolbar);
        etFirstName = view.findViewById(R.id.edit_text_first_name);
        etLastName = view.findViewById(R.id.edit_text_last_name);
        btnSave = view.findViewById(R.id.button_save);
    }

    private void initializeFirebaseWidgets() {
        // this method will initialize firebase instances.
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void widgetClickListener() {
        // this method will handle click listener for all widgets.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // update user information.
                    buttonClickListener.onButtonClickListener(etFirstName.getText().toString().trim(),
                            etLastName.getText().toString().trim());
                    updateUserInformationInFirebase();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Required fields are empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        buttonClickListener = (ButtonClickListener) context;
    }

    private boolean validateFields() {
        // this method will validate all the required fields.
        firstName = etFirstName.getText().toString().trim();
        lastName = etLastName.getText().toString().trim();

        if (!(TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName))) {
            // all fields are validated.
            return true;
        } else {
            return false;
        }
    }

    private void updateUserInformationInFirebase() {
        // this method will update user information in firebase.
        DatabaseReference db = databaseReference.child("User").child(mAuth.getUid());
        db.child("First_name").setValue(firstName);
        db.child("Last_name").setValue(lastName);
        updateUserInformationInSqlite();
    }

    private void updateUserInformationInSqlite() {
        // this method will update user information in Sqlite.
        databaseHelper = new SqliteDatabaseHelper(getContext());
        databaseHelper.updateUserInfor(firstName, lastName);
    }
}
