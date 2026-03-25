package com.example.quanlychitieu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton; // Thêm cái này
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar; // Thêm cái này để lấy thời gian thực
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalBalance, tvCurrentMonth;
    private FloatingActionButton fabAdd;
    private RecyclerView rvTransactions;
    private EditText etBudget;
    private PieChart pieChart;
    private ImageButton btnPrevMonth, btnNextMonth;

    private CategoryAdapter adapter;
    private DatabaseHelper db;
    private android.content.SharedPreferences sharedPreferences;

    // BIẾN LƯU THÁNG/NĂM ĐANG XEM
    private int currentMonth, currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ giao diện
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        fabAdd = findViewById(R.id.fabAdd);
        rvTransactions = findViewById(R.id.rvTransactions);
        pieChart = findViewById(R.id.pieChart);
        etBudget = findViewById(R.id.etBudget);

        // 2. Khởi tạo dữ liệu thời gian thực
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH) + 1; // Tháng trong Java từ 0-11
        currentYear = cal.get(Calendar.YEAR);

        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);

        // 3. Cài đặt RecyclerView
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        // 4. Load hạn mức
        String savedBudget = sharedPreferences.getString("limit", "0");
        etBudget.setText(savedBudget);

        // Trong MainActivity.java
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME"));
            startActivity(intent);
        });

        // Nút lùi tháng
        btnPrevMonth.setOnClickListener(v -> {
            if (currentMonth == 1) {
                currentMonth = 12;
                currentYear--;
            } else {
                currentMonth--;
            }
            updateUI();
        });

        // Nút tiến tháng
        btnNextMonth.setOnClickListener(v -> {
            if (currentMonth == 12) {
                currentMonth = 1;
                currentYear++;
            } else {
                currentMonth++;
            }
            updateUI();
        });

        // 6. Thay đổi hạn mức
        etBudget.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                sharedPreferences.edit().putString("limit", s.toString()).apply();
                updateUI();
            }
        });

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        // Cập nhật tiêu đề tháng hiển thị
        tvCurrentMonth.setText(String.format("Tháng %02d/%d", currentMonth, currentYear));

        // --- 1. Lấy dữ liệu THEO THÁNG từ DatabaseHelper ---
        double total = db.getTotalAmountByMonth(currentMonth, currentYear);
        List<CategorySummary> summaryList = db.getCategorySummariesByMonth(currentMonth, currentYear);

        // Hiển thị tiền
        DecimalFormat formatter = new DecimalFormat("#,###");
        tvTotalBalance.setText(formatter.format(total) + " VNĐ");

        // Logic hạn mức
        String budgetStr = etBudget.getText().toString();
        double budget = 0;
        try { if (!budgetStr.isEmpty()) budget = Double.parseDouble(budgetStr); } catch (Exception e) {}

        if (budget > 0 && total > budget) {
            tvTotalBalance.setTextColor(Color.RED);
            // Lưu ý: Tao tạm ẩn cái AlertDialog ở đây vì mỗi lần bấm nút chuyển tháng nó sẽ hiện lên liên tục gây phiền.
            // Tao dùng Toast cho nó gọn hơn.
            Toast.makeText(this, "⚠️ Tiêu quá hạn mức tháng này rồi!", Toast.LENGTH_SHORT).show();
        } else {
            tvTotalBalance.setTextColor(Color.parseColor("#4CAF50"));
        }

        // --- 2. Cập nhật Biểu đồ ---
        setupPieChart(summaryList);

        // --- 3. Cập nhật Danh sách ---
        adapter = new CategoryAdapter(summaryList);
        rvTransactions.setAdapter(adapter);

        // --- 4. Tìm kiếm ---
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                if (adapter != null) adapter.filter(newText);
                return true;
            }
        });
    }

    private void setupPieChart(List<CategorySummary> summaryList) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (CategorySummary item : summaryList) {
            if (item.getTotalAmount() > 0) {
                // Thêm dữ liệu có cả tên danh mục (để Legend vẫn hiện)
                entries.add(new PieEntry((float) item.getTotalAmount(), item.getCategory()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);


        pieChart.setDrawEntryLabels(false); // Ẩn nhãn (label)

        PieData data = new PieData(dataSet);

        // TÙY CHỈNH CHỮ SỐ TIỀN CHO NHỎ VÀ RÕ
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE); // Đổi sang màu trắng cho dễ nhìn

        pieChart.setData(data);

        // Giữ lại chú thích ở dưới
        pieChart.getLegend().setEnabled(true);

        pieChart.animateY(800);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }
}