package com.sanPatel.expensetracker.Database.SqliteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.sanPatel.expensetracker.Datas.Expense;

public class SqliteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseTrackerUserData.db";
    private static final String USER_TABLE_NAME = "User";
    private static final String EXPENSE_TABLE_NAME = "Expense";

    public SqliteDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+USER_TABLE_NAME+" " +
                "(user_id Text, first_name Text, last_name Text, email Text, photo BLOB)");
        db.execSQL("CREATE TABLE "+EXPENSE_TABLE_NAME+"(entry_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "expense_title TEXT, " +
                "expense_desc TEXT, " +
                "expense_amount REAL, " +
                "expense_date TEXT, " +
                "expense_time_stamp TEXT, " +
                "expense_type INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE "+EXPENSE_TABLE_NAME+ " ADD COLUMN sync INTEGER DEFAULT 0");
        }
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

    public Cursor getUserDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("Select * from "+USER_TABLE_NAME,null);
    }

    public boolean updateUserInfor(String firstName, String lastName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("first_name",firstName);
        contentValues.put("last_name",lastName);
        long result = db.update(USER_TABLE_NAME,contentValues,null,null);
        db.close();
        return result != -1;
    }

    public boolean updateUserPhoto(byte[] userPhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("photo",userPhoto);
        long result = db.update(USER_TABLE_NAME, contentValues, null, null);
        db.close();
        return result != 1;
    }

    public boolean insertEntry(String title, String desc, double amount, String date, String time, boolean type, int sync) {
        // this method will insert new entry in the database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("expense_title",title);
        contentValues.put("expense_desc",desc);
        contentValues.put("expense_amount",amount);
        contentValues.put("expense_date",date);
        contentValues.put("expense_time_stamp",time);
        contentValues.put("expense_type",type);
        contentValues.put("sync",sync);
        long result = db.insert(EXPENSE_TABLE_NAME,null,contentValues);
        db.close();
        return result != -1;
    }

    public Cursor getAllExpense() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("Select * from "+EXPENSE_TABLE_NAME+" WHERE sync != 2",null);
    }

    public Cursor getLatestTransaction() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+EXPENSE_TABLE_NAME+" WHERE sync != 2 ORDER BY entry_id DESC LIMIT 5",null);
    }

    public boolean updateExpense(int expense_id,String title, String desc, double amount, String date, String time, boolean type, int sync) {
        // this method will edit expense.
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("expense_title",title);
        contentValues.put("expense_desc",desc);
        contentValues.put("expense_amount",amount);
        contentValues.put("expense_date", date);
        contentValues.put("expense_time_stamp",time);
        contentValues.put("expense_type",type);
        contentValues.put("sync",sync);
        long result = db.update(EXPENSE_TABLE_NAME, contentValues, "entry_id = ?", new String[] {String.valueOf(expense_id)});
        db.close();
        return result != -1;
    }

    public boolean deleteExpense(int expense_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(EXPENSE_TABLE_NAME,"entry_id = ?",new String[] {String.valueOf(expense_id)});
        return result != 0;
    }

    public Cursor getAmount(int type) {
        // this method will fetch all the expense amount row.
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("Select expense_amount from "+EXPENSE_TABLE_NAME+" WHERE expense_type = "+type,null);
    }

    public void deleteAllTableData() {
        // this method will delete all the record from all the tables.
        SQLiteDatabase db = getWritableDatabase();
        long result = db.delete(USER_TABLE_NAME,null,null);
        if (result != -1) {
            db.delete(EXPENSE_TABLE_NAME,null,null);
        }
    }

    public Cursor getRemainSync() {
        // this method will check is the syncing of entries is remaining
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("Select * from "+EXPENSE_TABLE_NAME+" WHERE sync = 0 OR 2",null);
    }

    public Cursor getLastEntry() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+EXPENSE_TABLE_NAME+" ORDER BY entry_id DESC LIMIT 1",null);
    }

    public boolean updateSyncValue(int expense_id, int sync) {
        // this method will update sync value of expense_id
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("sync",sync);
        long result = db.update(EXPENSE_TABLE_NAME,contentValues,"entry_id = ?",new String[] {String.valueOf(expense_id)});
        db.close();
        return result != -1;
    }

    public Cursor getExpenseDetail(int expense_id) {
        // this method will return expense detail of expense_id
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+EXPENSE_TABLE_NAME+" WHERE entry_id = "+expense_id,null);
    }
}
