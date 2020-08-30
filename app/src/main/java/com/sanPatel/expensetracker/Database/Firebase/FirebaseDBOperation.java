package com.sanPatel.expensetracker.Database.Firebase;

import android.content.Context;
import android.database.Cursor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanPatel.expensetracker.Database.SqliteDatabase.SqliteDatabaseHelper;

public class FirebaseDBOperation {
    private Context context;
    public FirebaseDBOperation(Context context) {
        this.context = context;
    }
    public void insertEntry() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        SqliteDatabaseHelper databaseHelper = new SqliteDatabaseHelper(context);
        Cursor cursor = databaseHelper.getLastEntry();
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            int expense_id = cursor.getInt(0);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            DatabaseReference db = databaseReference.child("Expense")
                    .child(mAuth.getUid())
                    .child(Integer.toString(expense_id));;

            db.child("Title").setValue(cursor.getString(1));
            db.child("Desc").setValue(cursor.getString(2));
            db.child("Amount").setValue(cursor.getDouble(3));
            db.child("Date").setValue(cursor.getString(4));
            db.child("Time").setValue(cursor.getString(5));
            db.child("Type").setValue(cursor.getInt(6));
            db.child("sync").setValue(cursor.getInt(7));
        }
    }
}
