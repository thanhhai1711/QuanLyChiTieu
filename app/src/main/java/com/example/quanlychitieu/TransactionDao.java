package com.example.quanlychitieu;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(TransactionEntity transaction);

    @Query("UPDATE transactions SET amount=:amount, note=:note, category=:category, type=:type WHERE id=:id")
    void updateById(int id, double amount, String note, String category, String type);

    @Query("DELETE FROM transactions WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions " +
           "WHERE date LIKE :monthFilter AND type = 'expense' AND username = :username")
    double getTotalExpenseByMonth(String monthFilter, String username);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions " +
           "WHERE date LIKE :monthFilter AND type = 'income' AND username = :username")
    double getTotalIncomeByMonth(String monthFilter, String username);

    @Query("SELECT category, SUM(amount) as totalAmount FROM transactions " +
           "WHERE date LIKE :monthFilter AND type = 'expense' AND username = :username " +
           "GROUP BY category")
    List<CategorySummary> getExpenseSummaryByMonth(String monthFilter, String username);

    @Query("SELECT category, SUM(amount) as totalAmount FROM transactions " +
           "WHERE date LIKE :monthFilter AND type = 'income' AND username = :username " +
           "GROUP BY category")
    List<CategorySummary> getIncomeSummaryByMonth(String monthFilter, String username);

    @Query("SELECT * FROM transactions " +
           "WHERE category = :category AND date LIKE :monthFilter AND username = :username " +
           "ORDER BY id DESC")
    List<TransactionEntity> getTransactionsByCategoryAndMonth(
            String category, String monthFilter, String username);
}
