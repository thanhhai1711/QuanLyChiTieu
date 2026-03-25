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

    // Nâng version lên 4 để nó xóa cái cũ, tạo cái mới có bảng Users
    public DatabaseHelper(Context context) {
        super(context, "ChiTieuDB", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng người dùng
        db.execSQL("CREATE TABLE users (username TEXT PRIMARY KEY, password TEXT)");

        // 2. Tạo bảng giao dịch (Có cột date và username)
        db.execSQL("CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "amount REAL, " +
                "note TEXT, " +
                "category TEXT, " +
                "date TEXT, " +
                "username TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nếu version thay đổi, xóa sạch làm lại cho chắc chắn không lỗi cấu trúc
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // --- CHỨC NĂNG ĐĂNG KÝ / ĐĂNG NHẬP ---

    public boolean registerUser(String user, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra xem tên này đã có ai dùng chưa
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{user});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // Trùng tên rồi Hải ơi
        }
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

    // --- CHỨC NĂNG GIAO DỊCH ---

    public boolean insertTransaction(double amount, String note, String category, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("note", note);
        values.put("category", category);
        values.put("date", currentDate);
        values.put("username", username); // Lưu thêm tên người dùng vào đây

        long result = db.insert("transactions", null, values);
        db.close();
        return result != -1;
    }

    public double getTotalAmountByMonth(int month, int year) {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String monthFilter = String.format("%d-%02d%%", year, month);

        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM transactions WHERE date LIKE ?", new String[]{monthFilter});
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

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

    public List<Transaction> getTransactionsByCategory(String categoryName) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
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

    public void deleteTransactionById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("transactions", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}