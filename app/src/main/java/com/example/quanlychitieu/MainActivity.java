package com.example.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalBalance;
    private FloatingActionButton fabAdd;
    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        fabAdd = findViewById(R.id.fabAdd);
        rvTransactions = findViewById(R.id.rvTransactions);

        // 2. Khởi tạo Database
        db = new DatabaseHelper(this);

        // 3. Cài đặt RecyclerView (THIẾU DÒNG NÀY LÀ VĂNG APP)
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        // 4. Nút bấm chuyển màn hình
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(intent);
            }
        });

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(); // Cập nhật lại mỗi khi quay về từ màn hình thêm
    }

    private void updateUI() {
        // Cập nhật số dư
        double total = db.getTotalAmount();
        tvTotalBalance.setText(total + " VNĐ");

        // Cập nhật danh sách
        List<Transaction> list = db.getAllTransactions();
        adapter = new TransactionAdapter(list);
        rvTransactions.setAdapter(adapter);
    }
}