package com.example.quanlychitieu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "ChiTieuDB", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (username TEXT PRIMARY KEY, password TEXT)");
        db.execSQL("CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "amount REAL, note TEXT, category TEXT, date TEXT, username TEXT, " +
                "type TEXT DEFAULT 'expense')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            try { db.execSQL("ALTER TABLE transactions ADD COLUMN type TEXT DEFAULT 'expense'"); }
            catch (Exception e) {}
        }
    }

    public boolean registerUser(String user, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{user});
        if (cursor.getCount() > 0) { cursor.close(); return false; }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put("username", user);
        values.put("password", pass);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public boolean checkUser(String user, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{user, pass});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean insertTransaction(double amount, String note, String category, String username, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("note", note);
        values.put("category", category);
        values.put("date", currentDate);
        values.put("username", username);
        values.put("type", type);
        long result = db.insert("transactions", null, values);
        db.close();
        return result != -1;
    }

    // Cập nhật giao dịch
    public boolean updateTransaction(int id, double amount, String note, String category, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("note", note);
        values.put("category", category);
        values.put("type", type);
        int rows = db.update("transactions", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    public double getTotalExpenseByMonth(int month, int year, String username) {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String monthFilter = String.format(Locale.getDefault(), "%d-%02d%%", year, month);
        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) FROM transactions WHERE date LIKE ? AND type = 'expense' AND username = ?",
                new String[]{monthFilter, username});
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public double getTotalIncomeByMonth(int month, int year, String username) {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String monthFilter = String.format(Locale.getDefault(), "%d-%02d%%", year, month);
        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) FROM transactions WHERE date LIKE ? AND type = 'income' AND username = ?",
                new String[]{monthFilter, username});
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public List<CategorySummary> getCategorySummariesByMonth(int month, int year, String username) {
        List<CategorySummary> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String monthFilter = String.format(Locale.getDefault(), "%d-%02d%%", year, month);
        String query = "SELECT category, SUM(amount) FROM transactions " +
                "WHERE date LIKE ? AND type = 'expense' AND username = ? GROUP BY category";
        Cursor cursor = db.rawQuery(query, new String[]{monthFilter, username});
        if (cursor.moveToFirst()) {
            do { list.add(new CategorySummary(cursor.getString(0), cursor.getDouble(1))); }
            while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Transaction> getTransactionsByCategoryAndMonth(String categoryName, int month, int year, String username) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String monthFilter = String.format(Locale.getDefault(), "%d-%02d%%", year, month);
        Cursor cursor = db.rawQuery(
                "SELECT * FROM transactions WHERE category = ? AND date LIKE ? AND username = ? ORDER BY id DESC",
                new String[]{categoryName, monthFilter, username});
        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteTransactionById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}