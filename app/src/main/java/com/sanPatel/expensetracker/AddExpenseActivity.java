package com.sanPatel.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.sanPatel.expensetracker.AsyncTask.MyAsyncTask;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;
import com.sanPatel.expensetracker.Fragment.EntryCategoryDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity implements EntryCategoryDialog.SingleChoiceListener {

    private EditText etAmount, etExpenseTitle, etExpenseDesc;
    private Toolbar toolbar;

    private String amount, expenseTitle, expenseDesc;

    private MyAsyncTask myAsyncTask;

    public void addEntry(View view) {
        String str[] = {"Expense", "Income"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,str);

        if (validateFields()) {
            EntryCategoryDialog dialog = new EntryCategoryDialog();
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(),"Category dialog box");
        } else {
            Toast.makeText(this, "Required fields are empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.Theme_AppCompat_Light);
        setContentView(R.layout.activity_add_expense);
        initializeWidgets();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initializeWidgets() {
        //this method will initialize all the widgets
        etAmount = findViewById(R.id.edit_text_amount_value);
        etExpenseTitle = findViewById(R.id.edit_text_expense_title_value);
        etExpenseDesc = findViewById(R.id.edit_text_expense_desc_value);
        toolbar = findViewById(R.id.toolbar_add_expense);
    }

    public boolean validateFields() {
        amount = etAmount.getText().toString();
        expenseTitle = etExpenseTitle.getText().toString();
        expenseDesc = etExpenseDesc.getText().toString();

        if (TextUtils.isEmpty(amount)
            && TextUtils.isEmpty(expenseTitle)) {
            // fields are emply
            return false;
        }
        return true;
    }

    @Override
    public void setOnDialogPositiveClickListener(String[] list, final int selectedPosition) {
        // insert entry in the database.

        myAsyncTask = new MyAsyncTask();

        myAsyncTask.setAsyncTaskListener(new MyAsyncTask.AsyncTaskListener() {
            @Override
            public void setBackgroundTask() {
                String currentDate = new SimpleDateFormat("dd-MM-yyyy",
                        Locale.getDefault()).format(new Date());

                String currentTime = new SimpleDateFormat("HH:mm:ss",
                        Locale.getDefault()).format(new Date());

                SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(getApplicationContext());
                databaseHelper.insertEntry(expenseTitle, expenseDesc, Double.parseDouble(amount),currentDate,currentTime, selectedPosition != 0);
            }

            @Override
            public void setPostExecuteTask() {

            }
        });

        myAsyncTask.execute();
    }

}