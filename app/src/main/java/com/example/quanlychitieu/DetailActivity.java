package com.example.quanlychitieu;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private TextView tvDetailTitle;
    private RecyclerView rvDetail;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        rvDetail = findViewById(R.id.rvDetailTransactions);
        rvDetail.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(this);

        // Nhận tên danh mục ("Ăn uống", "Mua sắm"...) từ màn hình chính gửi sang
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        if (categoryName != null) {
            tvDetailTitle.setText("Chi tiết: " + categoryName); // Đổi tiêu đề

            // Lấy dữ liệu và đổ vào RecyclerView (Tận dụng lại cái TransactionAdapter cũ cho khỏe)
            List<Transaction> list = db.getTransactionsByCategory(categoryName);
            TransactionAdapter adapter = new TransactionAdapter(list);
            rvDetail.setAdapter(adapter);
        }
        // Khai báo và xử lý nút Back
        android.widget.ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                finish(); // Lệnh finish() sẽ đóng màn hình hiện tại và tự động lùi về màn hình trước đó
            }
        });
    }
}