package com.sanPatel.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sanPatel.expensetracker.Adapter.MyExpenseRecyclerViewAdapter;
import com.sanPatel.expensetracker.AsyncTask.MyAsyncTask;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.Datas.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewAllExpense extends AppCompatActivity {

    private static final String TAG = "ViewAllExpense";
    
    // widgets
    private RecyclerView myExpenseRecyclerView;
    private Toolbar toolbar;
    private ImageView imgViewNoExpense;
    private Button btnSync;
    private CoordinatorLayout coordinatorLayout;

    private SqliteDatabaseHelper databaseHelper;

    private boolean isExpense = false;
    private int isSynced = 0;

    private MyAsyncTask myAsyncTask;
    private ArrayList<Expense> expenseList;
    private MyExpenseRecyclerViewAdapter adapter;
    private Cursor cursor;

    public void sync(View view) {
        // this method will sync all the offline entry.
        if (isNetworkConnected()) {
            // internet connectivity is available.
            if (cursor.getCount() > 0) {
                Toast.makeText(this, ""+cursor.getCount(), Toast.LENGTH_SHORT).show();
                while (cursor.moveToNext()) {
                    // sync entry data with firebase.
                }
            }
        } else {
            // internet connectivity is not available.
            final Snackbar snackbar = Snackbar.make(coordinatorLayout,"No Internet Connection",Snackbar.LENGTH_SHORT)
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
        setContentView(R.layout.activity_view_all_expense);

        initializeWidgets();

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        myExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        myExpenseRecyclerView.setHasFixedSize(true);
    }

    private void initializeWidgets() {
        // this method will initialize all widgets.
        myExpenseRecyclerView = findViewById(R.id.recycler_view_my_expense);
        toolbar = findViewById(R.id.toolbar_view_all_expense);
        imgViewNoExpense = findViewById(R.id.image_view_no_expense_image);
        btnSync = findViewById(R.id.button_sync);
        coordinatorLayout = findViewById(R.id.coordinator_layout_parent_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllExpense();
        btnSync.setEnabled(false);
        SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(getApplicationContext());
        cursor = databaseHelper.getRemainSync();
        if (cursor.getCount() > 0) {
            // entries are available for sync.
            btnSync.setEnabled(true);
        }
    }

    private void getAllExpense() {
        // this method will get all the expense from the database and will create arrayList for that.
        myAsyncTask = new MyAsyncTask();

        myAsyncTask.setAsyncTaskListener(new MyAsyncTask.AsyncTaskListener() {
            @Override
            public void setBackgroundTask() {
                Cursor cursor;
                databaseHelper = new SqliteDatabaseHelper(getApplicationContext());
                cursor = databaseHelper.getAllExpense();
                expenseList = new ArrayList<>();
                try {
                    if (cursor.getCount() > 0) {
                        isExpense = true;
                        while (cursor.moveToNext()) {
                            expenseList.add(new Expense(cursor.getInt(0),
                                    cursor.getString(1),
                                    cursor.getString(2),
                                    cursor.getLong(3),
                                    cursor.getInt(6),
                                    new SimpleDateFormat("dd-MM-yyyy").parse(cursor.getString(4))));
                        }
                    } else {
                        isExpense = false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void setPostExecuteTask() {
                adapter = new MyExpenseRecyclerViewAdapter(expenseList, getApplicationContext());

                myExpenseRecyclerView.setAdapter(adapter);
                if (!isExpense) {
                    // show image.
                    imgViewNoExpense.setVisibility(View.VISIBLE);
                } else {
                    imgViewNoExpense.setVisibility(View.INVISIBLE);
                }
            }
        });
        myAsyncTask.execute();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}