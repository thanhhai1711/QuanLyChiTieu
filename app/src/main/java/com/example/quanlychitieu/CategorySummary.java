package com.example.quanlychitieu;

public class CategorySummary {
    private String category;
    private double totalAmount;

    public CategorySummary(String category, double totalAmount) {
        this.category = category;
        this.totalAmount = totalAmount;
    }

    public String getCategory() { return category; }
    public double getTotalAmount() { return totalAmount; }
}