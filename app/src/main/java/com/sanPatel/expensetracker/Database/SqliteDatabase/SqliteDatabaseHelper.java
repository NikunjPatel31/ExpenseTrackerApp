package com.sanPatel.expensetracker.Database.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseTrackerUserData.db";
    private static final String USER_TABLE_NAME = "User";
    private static final String EXPENSE_TABLE_NAME = "Expense";

    public SqliteDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+USER_TABLE_NAME+" " +
                "(user_id Text, first_name Text, last_name Text, email Text, photo BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertUserData(String user_id, String firstName, String lastName, String email, byte[] photo) {
        // this method will insert record of the user data inside the User table.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id",user_id);
        contentValues.put("first_name",firstName);
        contentValues.put("last_name",lastName);
        contentValues.put("email",email);
        contentValues.put("photo",photo);
        long result = db.insert(USER_TABLE_NAME, null,contentValues);
        db.close();
        return result != -1;
    }
}
