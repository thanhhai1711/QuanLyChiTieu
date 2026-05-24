package com.example.quanlychitieu;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double amount;
    public String note;
    public String category;
    public String date;
    public String username;
    public String type; // "income" hoặc "expense"

    public TransactionEntity(double amount, String note, String category,
                              String date, String username, String type) {
        this.amount = amount;
        this.note = note;
        this.category = category;
        this.date = date;
        this.username = username;
        this.type = type;
    }
}
