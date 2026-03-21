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
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalBalance;
    private FloatingActionButton fabAdd;
    private RecyclerView rvTransactions;

    // Đã sửa lại thành CategoryAdapter cho mày
    private CategoryAdapter adapter;
    private DatabaseHelper db;
    private com.github.mikephil.charting.charts.PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ toàn bộ giao diện
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        fabAdd = findViewById(R.id.fabAdd);
        rvTransactions = findViewById(R.id.rvTransactions);
        pieChart = findViewById(R.id.pieChart);

        // 2. Khởi tạo Database
        db = new DatabaseHelper(this);

        // 3. Cài đặt RecyclerView
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        // 4. Nút bấm chuyển sang màn hình thêm giao dịch
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(intent);
            }
        });

        // 5. Load dữ liệu lúc mới vào app
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại mỗi khi quay về từ màn hình thêm (để nó update biểu đồ)
        updateUI();
    }

    public void updateUI() {
        // 1. Khai báo cái định dạng số (Chỉ khai báo 1 lần duy nhất ở đây)
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");

        // 2. Cập nhật tổng số dư trên cùng
        double total = db.getTotalAmount();
        // Dùng cái formatter đã khai báo ở trên
        String formattedTotal = formatter.format(total);
        tvTotalBalance.setText(formattedTotal + " VNĐ");

        // 3. Lấy danh sách GOM NHÓM từ database
        java.util.List<CategorySummary> summaryList = db.getCategorySummaries();

        // 4. Đổ danh sách vào màn hình
        adapter = new CategoryAdapter(summaryList);
        rvTransactions.setAdapter(adapter);

        // 5. Bơm dữ liệu cho biểu đồ
        setupPieChart(summaryList);
    }

    private void setupPieChart(java.util.List<CategorySummary> summaryList) {
        java.util.ArrayList<com.github.mikephil.charting.data.PieEntry> entries = new java.util.ArrayList<>();

        // Quét danh sách, cứ có danh mục nào là nhét vào một miếng bánh
        for (CategorySummary item : summaryList) {
            // Chỉ vẽ những danh mục có tiền lớn hơn 0 cho nó khỏi lỗi
            if (item.getTotalAmount() > 0) {
                entries.add(new com.github.mikephil.charting.data.PieEntry((float) item.getTotalAmount(), item.getCategory()));
            }
        }

        com.github.mikephil.charting.data.PieDataSet dataSet = new com.github.mikephil.charting.data.PieDataSet(entries, "");

        // Cấp cho nó cái bảng màu rực rỡ
        dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        com.github.mikephil.charting.data.PieData data = new com.github.mikephil.charting.data.PieData(dataSet);
        pieChart.setData(data);

        // Làm đẹp biểu đồ
        pieChart.getDescription().setEnabled(false); // Tắt chữ mô tả rườm rà
        pieChart.setDrawEntryLabels(false); // Tắt chữ đè lên bánh cho đỡ rối
        pieChart.setCenterText("Chi Tiêu"); // Chữ ở giữa vòng tròn
        pieChart.setCenterTextSize(18f);
        pieChart.animateY(1000); // Hiệu ứng xoay tròn tung chảo
        pieChart.invalidate(); // Bắt đầu vẽ
    }
}