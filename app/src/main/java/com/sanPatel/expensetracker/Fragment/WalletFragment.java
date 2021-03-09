package com.sanPatel.expensetracker.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import com.sanPatel.expensetracker.AsyncTask.MyAsyncTask;
import com.sanPatel.expensetracker.Database.Firebase.FirebaseDBOperation;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.Datas.Wallet;
import com.sanPatel.expensetracker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WalletFragment extends DialogFragment {

    private static final String TAG = "WalletFragment";

    // widgets
    private MaterialToolbar toolbar;
    private Button btnCreate;
    private EditText etWalletName, etInitialBalance;

    public interface ButtonClickListener {
        void onButtonClickListener(Wallet wallet);
    }

    // buttonClickedInterface
    private ButtonClickListener buttonClickListener;

    // instance variable
    private Wallet wallet;

    // flag variable
    private boolean isEdit = false;

    public WalletFragment(Wallet wallet) {
        this.wallet = wallet;
        isEdit = true;
    }

    public WalletFragment() {
    }

    public static WalletFragment display(FragmentManager fragmentManager) {
        WalletFragment walletFragment = new WalletFragment();
        walletFragment.show(fragmentManager,TAG);
        return walletFragment;
    }

    public static WalletFragment display(FragmentManager fragmentManager, Wallet wallet) {
        Log.d(TAG, "display: "+wallet.getWalletName());
        WalletFragment walletFragment = new WalletFragment(wallet);
        walletFragment.show(fragmentManager,TAG);
        return walletFragment;
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
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wallet_fragment, container, false);
        initializeWidgets(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        widgetClickListener();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (isEdit) {
            populateWidgets();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        buttonClickListener = (ButtonClickListener) context;
    }

    private void populateWidgets() {
        toolbar.setTitle("Edit Wallet");
        etWalletName.setText(wallet.getWalletName());
        etInitialBalance.setText(Double.toString(wallet.getInitialBalance()));
        btnCreate.setText("Apply");
    }

    private void initializeWidgets(View view) {
        // this method will initialize all the widgets.
        toolbar = view.findViewById(R.id.material_toolbar);
        btnCreate = view.findViewById(R.id.button_create);
        etWalletName = view.findViewById(R.id.edit_text_wallet_name);
        etInitialBalance = view.findViewById(R.id.edit_text_initial_balance);
    }

    private void widgetClickListener() {
        // this method will handle all the click listeners.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!etWalletName.getText().toString().isEmpty() && !etInitialBalance.getText().toString().isEmpty())) {
                    if (isEdit) {
                        updateWallet();
                    } else {
                        insertWallet();
                    }
                } else {
                    Toast.makeText(getContext(), "Required fields are empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateWallet() {
        // update wallet in local as well as firebase db.
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setAsyncTaskListener(new MyAsyncTask.AsyncTaskListener() {
            @Override
            public void setBackgroundTask() {
                int isSynced = 1;
                if (isNetworkConnected()) {
                    isSynced = 1;
                } else {
                    isSynced = 0;
                }

                String currentDate = new SimpleDateFormat("dd-MM-yyyy",
                        Locale.getDefault()).format(new Date());

                String currentTime = new SimpleDateFormat("HH:mm:ss",
                        Locale.getDefault()).format(new Date());

                try {
                    Log.d(TAG, "setBackgroundTask: we are at the position");
                    wallet.setWalletName(etWalletName.getText().toString());
                    wallet.setInitialBalance(Double.parseDouble(etInitialBalance.getText().toString()));
                    wallet.setDate(new SimpleDateFormat("dd-MM-yyyy").parse(currentDate));
                    wallet.setTimeStamp(currentTime);
                    wallet.setWalletSync(isSynced);

                    SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(getContext());
                    if (databaseHelper.updateWallet(wallet)) {
                        Log.d(TAG, "setBackgroundTask: Update successful");
                    } else {
                        Log.d(TAG, "setBackgroundTask: Update not happen");
                    }

                } catch (Exception e) {
                    Log.d(TAG, "setBackgroundTask: EXCEPTION: "+e.getLocalizedMessage());
                }
            }

            @Override
            public void setPostExecuteTask() {
                if (wallet.getWalletSync() == 1) {
                    FirebaseDBOperation firebaseDBOperation = new FirebaseDBOperation(getContext());
                    firebaseDBOperation.insertWallet(wallet);
                }
                buttonClickListener.onButtonClickListener(wallet);
                dismiss();
            }
        });
        myAsyncTask.execute();
    }

    public void insertWallet() {
        // this method will insert wallet into the local database as well as firebase.
        final SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(getContext());
        final int[] isSynced = new int[1];
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setAsyncTaskListener(new MyAsyncTask.AsyncTaskListener() {
            @Override
            public void setBackgroundTask() {

                String currentDate = new SimpleDateFormat("dd-MM-yyyy",
                        Locale.getDefault()).format(new Date());

                String currentTime = new SimpleDateFormat("HH:mm:ss",
                        Locale.getDefault()).format(new Date());

                if (isNetworkConnected()) {
                    isSynced[0] = 1;
                } else {
                    isSynced[0] = 0;
                }
                try {
                    Wallet wallet = new Wallet();
                    wallet.setWalletID(-1);
                    wallet.setWalletName(etWalletName.getText().toString());
                    wallet.setInitialBalance(Double.parseDouble(etInitialBalance.getText().toString()));
                    wallet.setDate(new SimpleDateFormat("dd-MM-yyyy").parse(currentDate));
                    wallet.setTimeStamp(currentTime);
                    wallet.setWalletSync(isSynced[0]);
                    databaseHelper.insertWallet(wallet);
                } catch (Exception e) {
                    Log.d(TAG, "setBackgroundTask: Exception: "+e.getLocalizedMessage());
                }

                if (isSynced[0] == 1) {
                    Cursor cursor = databaseHelper.getLastWallet();
                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();
                        FirebaseDBOperation dbOperation = new FirebaseDBOperation(getContext());
                        dbOperation.insertWallet(cursor);
                    }
                }
            }

            @Override
            public void setPostExecuteTask() {
                try {
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy",
                            Locale.getDefault()).format(new Date());

                    String currentTime = new SimpleDateFormat("HH:mm:ss",
                            Locale.getDefault()).format(new Date());
                    Cursor walletcursor = databaseHelper.getLastWallet();
                    if (walletcursor.getCount() > 0) {
                        walletcursor.moveToNext();
                        buttonClickListener.onButtonClickListener(
                                new Wallet(etWalletName.getText().toString(),
                                        currentTime,
                                        Double.parseDouble(etInitialBalance.getText().toString()),
                                        new SimpleDateFormat("dd-MM-yyyy").parse(currentDate),
                                        walletcursor.getInt(0),
                                        isSynced[0]));
                    } else {
                        buttonClickListener.onButtonClickListener(
                                new Wallet(etWalletName.getText().toString(),
                                        currentTime,
                                        Double.parseDouble(etInitialBalance.getText().toString()),
                                        new SimpleDateFormat("dd-MM-yyyy").parse(currentDate),
                                        1,
                                        isSynced[0]));
                    }
                } catch (Exception e) {

                }
                dismiss();
            }
        });
        myAsyncTask.execute();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
