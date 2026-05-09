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

    // Khai báo đủ 3 biến để nhận dữ liệu
    private String categoryName;
    private int month;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        rvDetail = findViewById(R.id.rvDetailTransactions);
        rvDetail.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(this);

        // NHẬN DỮ LIỆU TỪ CATEGORY_ADAPTER GỬI SANG
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        month = getIntent().getIntExtra("MONTH", 1);
        year = getIntent().getIntExtra("YEAR", 2026);

        if (categoryName != null) {
            // Hiển thị title rành mạch luôn để biết đang xem tháng mấy
            tvDetailTitle.setText(categoryName + " (Tháng " + month + "/" + year + ")");

            // Gọi hàm load dữ liệu
            loadData();
        }

        // Xử lý nút Back
        android.widget.ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    // HÀM LOAD DỮ LIỆU ĐÃ ĐƯỢC NÂNG CẤP
    public void loadData() {
        if (categoryName == null) return;

        // GỌI HÀM MỚI: Chỉ lấy đúng tên danh mục VÀ đúng cái tháng đó
        List<Transaction> list = db.getTransactionsByCategoryAndMonth(categoryName, month, year);

        // Đổ vào Adapter
        TransactionAdapter adapter = new TransactionAdapter(list);
        rvDetail.setAdapter(adapter);
    }
}