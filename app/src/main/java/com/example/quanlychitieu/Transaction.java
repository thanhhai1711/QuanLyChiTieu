package com.example.quanlychitieu;

public class Transaction {
    private int id; // Thêm mã định danh vào đây
    private double amount;
    private String note;
    private String category;

    // Constructor mới phải nhận thêm ID từ Database đổ vào
    public Transaction(int id, double amount, String note, String category) {
        this.id = id;
        this.amount = amount;
        this.note = note;
        this.category = category;
    }

    // Hàm lấy ID (Để thằng Adapter gọi lúc xóa)
    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getCategory() {
        return category;
    }
}