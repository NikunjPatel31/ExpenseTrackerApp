package com.sanPatel.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AddExpenseActivity extends Activity {

    private EditText etAmount, etExpenseTitle, etExpenseDesc;

    private String amount, expenseTitle, expenseDesc;

    public void addEntry(View view) {
        if (validateFields()) {
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
            materialAlertDialogBuilder.setTitle("Select category")
                    .setMessage("adgadf asfdka ldskfm sakdfjk asidfj ilajojl fdjlasf ")
                    .show();
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
    }

    private void initializeWidgets() {
        //this method will initialize all the widgets
        etAmount = findViewById(R.id.edit_text_amount_value);
        etExpenseTitle = findViewById(R.id.edit_text_expense_title_value);
        etExpenseDesc = findViewById(R.id.edit_text_expense_desc_value);
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
}