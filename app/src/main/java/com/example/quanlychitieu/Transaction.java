package com.example.quanlychitieu;

public class Transaction {
    private int id;
    private double amount;
    private String note;
    private String category;
    private String date;
    private String type; // "income" hoặc "expense"

    public Transaction(int id, double amount, String note, String category, String date, String type) {
        this.id = id;
        this.amount = amount;
        this.note = note;
        this.category = category;
        this.date = date;
        this.type = type;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public boolean isIncome() { return "income".equals(type); }
}