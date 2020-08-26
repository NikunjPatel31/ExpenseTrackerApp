package com.sanPatel.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sanPatel.expensetracker.AccountSetting.SettingActivity;
import com.sanPatel.expensetracker.Adapter.MyExpenseRecyclerViewAdapter;
import com.sanPatel.expensetracker.AsyncTask.MyAsyncTask;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.Datas.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {

    // widgets.
    private FloatingActionButton fabSetting, fabAddExpense, fabViewExpenses;
    private RecyclerView myExpenseRecyclerView;

    // animation.
    private Animation fabOpen, fabClose;

    //flag variable.
    boolean isFabOpen = false;

    // instance variable
    private ArrayList<Expense> expenseList;
    private MyExpenseRecyclerViewAdapter adapter;

    public void viewAllExpense(View view) {
        // this method will show all the expense.
        startActivity(new Intent(getApplicationContext(), ViewAllExpense.class));
    }

    public void menu(View view) {
        // this method will handle click listener of fabMenu.
        if (isFabOpen) {
            // fab menu button is pressed.
            fabSetting.setEnabled(false);
            fabAddExpense.setEnabled(false);
            fabViewExpenses.setEnabled(false);
            fabSetting.startAnimation(fabClose);
            fabAddExpense.startAnimation(fabClose);
            fabViewExpenses.startAnimation(fabClose);

            isFabOpen = false;
        } else {
            // fab menu button is closed.
            fabSetting.startAnimation(fabOpen);
            fabAddExpense.startAnimation(fabOpen);
            fabViewExpenses.startAnimation(fabOpen);
            fabSetting.setEnabled(true);
            fabAddExpense.setEnabled(true);
            fabViewExpenses.setEnabled(true);
            isFabOpen = true;
        }
    }

    public void setting(View view) {
        // this method will handle click listener for fabSetting.
        startActivity(new Intent(HomeScreenActivity.this, SettingActivity.class));
    }

    public void addExpense(View view) {
        // this method will handle click listener for fabAddExpense
        startActivity(new Intent(getApplicationContext(), AddExpenseActivity.class));
    }

    public void viewExpense(View view) {
        // this method will handle click listener for fabViewExpense
        startActivity(new Intent(getApplicationContext(), ViewAllExpense.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        initializeWidgets();

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        myExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        myExpenseRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLatestTransaction();
    }

    private void initializeWidgets() {
        // this method will initialize all the widgets.
        fabSetting = findViewById(R.id.fab_setting);
        fabAddExpense = findViewById(R.id.fab_add_expense);
        fabViewExpenses = findViewById(R.id.fab_view_expense);
        myExpenseRecyclerView = findViewById(R.id.recycler_view_latest_transactions);
    }

    private void getLatestTransaction() {
        // this will get latest 5 transaction from the database.
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.setAsyncTaskListener(new MyAsyncTask.AsyncTaskListener() {
            @Override
            public void setBackgroundTask() {
                SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(getApplicationContext());
                Cursor cursor = databaseHelper.getLatestTransaction();
                expenseList = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        expenseList.add(new Expense(cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getLong(3),
                                cursor.getInt(6),
                                new SimpleDateFormat("dd-MM-yyyy").parse(cursor.getString(4))));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void setPostExecuteTask() {
                adapter = new MyExpenseRecyclerViewAdapter(expenseList, getApplicationContext());

                myExpenseRecyclerView.setAdapter(adapter);
            }
        });
        myAsyncTask.execute();
    }
}