package com.example.quanlychitieu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "ChiTieuDB", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, amount REAL, note TEXT, category TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }

    public boolean insertTransaction(double amount, String note, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("note", note);
        values.put("category", category);
        long result = db.insert("transactions", null, values);
        db.close();
        return result != -1;
    }

    public double getTotalAmount() {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM transactions", null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    // --- SỬA HÀM NÀY ĐỂ LẤY CẢ ID ---
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transactions ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")), // Lấy ID
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<CategorySummary> getCategorySummaries() {
        List<CategorySummary> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT category, SUM(amount) FROM transactions GROUP BY category", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new CategorySummary(
                        cursor.getString(0),
                        cursor.getDouble(1)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- SỬA HÀM NÀY ĐỂ LẤY CẢ ID CHO MÀN HÌNH CHI TIẾT ---
    public List<Transaction> getTransactionsByCategory(String categoryName) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE category = ? ORDER BY id DESC", new String[]{categoryName});

        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")), // Lấy ID quan trọng nhất ở đây
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Hàm xóa theo ID (Đã có sẵn)
    public void deleteTransactionById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Hàm xóa cả cụm danh mục (Nếu mày vẫn muốn giữ chức năng này ở màn hình chính)
    public void deleteTransactionsByCategory(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", "category = ?", new String[]{categoryName});
        db.close();
    }
}