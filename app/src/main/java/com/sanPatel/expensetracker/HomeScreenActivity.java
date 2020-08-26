package com.sanPatel.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sanPatel.expensetracker.AccountSetting.SettingActivity;

public class HomeScreenActivity extends AppCompatActivity {

    // widgets.
    private FloatingActionButton fabSetting, fabAddExpense, fabViewExpenses;

    // animation.
    Animation fabOpen, fabClose;

    //flag variable.
    boolean isFabOpen = false;

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
    }

    private void initializeWidgets() {
        // this method will initialize all the widgets.
        fabSetting = findViewById(R.id.fab_setting);
        fabAddExpense = findViewById(R.id.fab_add_expense);
        fabViewExpenses = findViewById(R.id.fab_view_expense);
    }
}