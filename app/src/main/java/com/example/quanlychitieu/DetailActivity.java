package com.example.quanlychitieu;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private TextView tvDetailTitle;
    private RecyclerView rvDetail;
    private DatabaseHelper db;

    private String categoryName;
    private int month;
    private int year;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        rvDetail = findViewById(R.id.rvDetailTransactions);
        rvDetail.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(this);

        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        month = getIntent().getIntExtra("MONTH", 1);
        year = getIntent().getIntExtra("YEAR", 2026);
        username = getIntent().getStringExtra("USERNAME");
        if (username == null) username = "default";

        if (categoryName != null) {
            tvDetailTitle.setText(categoryName + " (Tháng " + month + "/" + year + ")");
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // QUAN TRỌNG: tự load lại data mỗi khi quay về màn hình này
    // (kể cả sau khi Sửa hoặc Xóa giao dịch ở màn hình khác)
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        if (categoryName == null) return;
        List<Transaction> list = db.getTransactionsByCategoryAndMonth(categoryName, month, year, username);
        TransactionAdapter adapter = new TransactionAdapter(list);
        rvDetail.setAdapter(adapter);
    }
}