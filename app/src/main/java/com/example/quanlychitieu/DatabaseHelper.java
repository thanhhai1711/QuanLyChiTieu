package com.example.quanlychitieu;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper {

    private final AppDatabase db;

    public DatabaseHelper(Context context) {
        db = AppDatabase.getInstance(context);
    }

    // --- AUTH ---

    public boolean registerUser(String username, String password) {
        if (db.userDao().isUserExists(username) > 0) return false;
        db.userDao().insert(new UserEntity(username, password));
        return true;
    }

    public boolean checkUser(String username, String password) {
        return db.userDao().checkUser(username, password) > 0;
    }

    // --- GIAO DỊCH ---

    public boolean insertTransaction(double amount, String note, String category,
                                     String username, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.transactionDao().insert(new TransactionEntity(amount, note, category, date, username, type));
        return true;
    }

    public boolean updateTransaction(int id, double amount, String note, String category, String type) {
        db.transactionDao().updateById(id, amount, note, category, type);
        return true;
    }

    public void deleteTransactionById(int id) {
        db.transactionDao().deleteById(id);
    }

    // --- THỐNG KÊ ---

    public double getTotalExpenseByMonth(int month, int year, String username) {
        return db.transactionDao().getTotalExpenseByMonth(monthFilter(month, year), username);
    }

    public double getTotalIncomeByMonth(int month, int year, String username) {
        return db.transactionDao().getTotalIncomeByMonth(monthFilter(month, year), username);
    }

    public List<CategorySummary> getCategorySummariesByMonth(int month, int year, String username) {
        return db.transactionDao().getExpenseSummaryByMonth(monthFilter(month, year), username);
    }

    public List<CategorySummary> getIncomeSummariesByMonth(int month, int year, String username) {
        return db.transactionDao().getIncomeSummaryByMonth(monthFilter(month, year), username);
    }

    public List<Transaction> getTransactionsByCategoryAndMonth(String category, int month, int year, String username) {
        List<TransactionEntity> entities = db.transactionDao()
                .getTransactionsByCategoryAndMonth(category, monthFilter(month, year), username);
        List<Transaction> list = new ArrayList<>();
        for (TransactionEntity e : entities)
            list.add(new Transaction(e.id, e.amount, e.note, e.category, e.date, e.type));
        return list;
    }

    // --- HELPER ---
    private String monthFilter(int month, int year) {
        return String.format(Locale.getDefault(), "%d-%02d%%", year, month);
    }
}
