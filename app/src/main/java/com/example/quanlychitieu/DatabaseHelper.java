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
        super(context, "ChiTieuDB", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, amount REAL, note TEXT, category TEXT, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN date TEXT");
        }
    }

    public boolean insertTransaction(double amount, String note, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Lưu định dạng yyyy-MM-dd để dễ lọc (VD: 2026-03-25)
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("note", note);
        values.put("category", category);
        values.put("date", currentDate);

        long result = db.insert("transactions", null, values);
        db.close();
        return result != -1;
    }

    // --- CHỨC NĂNG QUẢN LÝ THEO THÁNG ---

    // 1. Lấy tổng tiền theo tháng (VD: month=3, year=2026)
    public double getTotalAmountByMonth(int month, int year) {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        // Tạo chuỗi lọc VD: "2026-03%"
        String monthFilter = String.format("%d-%02d%%", year, month);

        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM transactions WHERE date LIKE ?", new String[]{monthFilter});
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    // 2. Lấy tóm tắt hạng mục theo tháng (Cho PieChart và danh sách chính)
    public List<CategorySummary> getCategorySummariesByMonth(int month, int year) {
        List<CategorySummary> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String monthFilter = String.format("%d-%02d%%", year, month);

        String query = "SELECT category, SUM(amount) FROM transactions WHERE date LIKE ? GROUP BY category";
        Cursor cursor = db.rawQuery(query, new String[]{monthFilter});

        if (cursor.moveToFirst()) {
            do {
                list.add(new CategorySummary(cursor.getString(0), cursor.getDouble(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 3. Lấy tất cả giao dịch trong tháng (Nếu cần hiện danh sách phẳng)
    public List<Transaction> getAllTransactionsByMonth(int month, int year) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String monthFilter = String.format("%d-%02d%%", year, month);

        Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE date LIKE ? ORDER BY id DESC", new String[]{monthFilter});

        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- CÁC HÀM CŨ (GIỮ NGUYÊN ĐỂ KHÔNG LỖI CODE KHÁC) ---
    public void deleteTransactionById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public List<Transaction> getTransactionsByCategory(String categoryName) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn lấy tất cả các cột của những giao dịch thuộc hạng mục categoryName
        Cursor cursor = db.rawQuery("SELECT * FROM transactions WHERE category = ? ORDER BY id DESC", new String[]{categoryName});

        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}