package com.example.quanlychitieu;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {
    private TextView tvDetailTitle;
    private RecyclerView rvDetail;
    private DatabaseHelper db;
    private String categoryName; // Đưa ra ngoài để dùng chung

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        rvDetail = findViewById(R.id.rvDetailTransactions);
        rvDetail.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(this);

        // Nhận tên danh mục
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        if (categoryName != null) {
            tvDetailTitle.setText("Chi tiết: " + categoryName);
            // Gọi hàm load dữ liệu lần đầu
            loadData();
        }

        // Xử lý nút Back
        android.widget.ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // ĐÂY LÀ HÀM MÀ THẰNG ADAPTER ĐANG ĐÒI NÈ HẢI
    public void loadData() {
        if (categoryName == null) return;

        // 1. Lấy dữ liệu mới từ DB
        List<Transaction> list = db.getTransactionsByCategory(categoryName);

        // 2. Đổ vào Adapter
        TransactionAdapter adapter = new TransactionAdapter(list);
        rvDetail.setAdapter(adapter);

        // 3. (Tùy chọn) Nếu mày có TextView hiện tổng tiền ở màn hình này thì tính ở đây
        // double total = 0;
        // for (Transaction t : list) total += t.getAmount();
        // tvTotal.setText(new DecimalFormat("#,###").format(total) + " VNĐ");
    }
}