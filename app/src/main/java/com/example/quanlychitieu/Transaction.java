package com.example.quanlychitieu;

public class Transaction {
    private double amount;
    private String note;
    private String category; // Mục mới thêm nè Hải

    public Transaction(double amount, String note, String category) {
        this.amount = amount;
        this.note = note;
        this.category = category;
    }

    public double getAmount() { return amount; }
    public String getNote() { return note; }
    public String getCategory() { return category; }
}