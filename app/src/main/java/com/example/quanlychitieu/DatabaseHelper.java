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
        super(context, "ChiTieuDB", null, 2); // Tăng version lên 2
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Thêm cột category kiểu TEXT
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, amount REAL, note TEXT, category TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }

    // Hàm lưu tiền giờ có thêm biến category
    public boolean insertTransaction(double amount, String note, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("note", note);
        values.put("category", category); // Lưu danh mục vào đây
        long result = db.insert("transactions", null, values);
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

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transactions ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")) // Lôi danh mục ra
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // HÀM MỚI ĐÃ ĐƯỢC CHO VÀO TRONG CLASS RỒI NHÉ
    public List<CategorySummary> getCategorySummaries() {
        List<CategorySummary> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Câu lệnh gom nhóm thần thánh
        Cursor cursor = db.rawQuery("SELECT category, SUM(amount) FROM transactions GROUP BY category", null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new CategorySummary(
                        cursor.getString(0), // Tên danh mục
                        cursor.getDouble(1)  // Tổng tiền của danh mục đó
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    // Hàm lấy danh sách chi tiết theo từng danh mục
    public List<Transaction> getTransactionsByCategory(String categoryName) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Lệnh lọc: CHỈ lấy những thằng có category khớp với tên truyền vào
        Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE category = ? ORDER BY id DESC", new String[]{categoryName});

        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}