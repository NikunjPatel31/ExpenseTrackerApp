package com.sanPatel.expensetracker.Database.Firebase;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

    public void insertEntry(Cursor cursor) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        DatabaseReference db = databaseReference.child("Expense")
                .child(mAuth.getUid())
                .child(Integer.toString(cursor.getInt(0)));;

        db.child("Title").setValue(cursor.getString(1));
        db.child("Desc").setValue(cursor.getString(2));
        db.child("Amount").setValue(cursor.getDouble(3));
        db.child("Date").setValue(cursor.getString(4));
        db.child("Time").setValue(cursor.getString(5));
        db.child("Type").setValue(cursor.getInt(6));
        db.child("sync").setValue(cursor.getInt(7));
    }

    public void deleteExpense(int expense_id) {
        // this method will delete expense from firebase.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Expense").child(mAuth.getUid()).child(Integer.toString(expense_id));

        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
