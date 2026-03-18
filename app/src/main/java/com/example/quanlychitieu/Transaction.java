package com.example.quanlychitieu;

public class Transaction {
    private double amount;
    private String note;

    public Transaction(double amount, String note) {
        this.amount = amount;
        this.note = note;
    }

    public double getAmount() { return amount; }
    public String getNote() { return note; }
}