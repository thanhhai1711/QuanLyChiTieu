package com.example.quanlychitieu;

public class Transaction {
    private int id;
    private double amount;
    private String note;
    private String category;
    private String date; // 1. Thêm biến này

    // 2. Sửa Constructor (Thêm tham số date vào cuối)
    public Transaction(int id, double amount, String note, String category, String date) {
        this.id = id;
        this.amount = amount;
        this.note = note;
        this.category = category;
        this.date = date;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
    public String getCategory() { return category; }
    public String getDate() { return date; } // 3. Thêm hàm lấy ngày
}