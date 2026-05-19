package com.example.quanlychitieu;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalBalance, tvCurrentMonth, tvIncome, tvExpense;
    private FloatingActionButton fabAdd, fabStats;
    private RecyclerView rvExpense, rvIncome;
    private EditText etBudget;
    private PieChart pieChart;
    private ImageButton btnPrevMonth, btnNextMonth, btnLogout;

    private LinearLayout tabExpense, tabIncome, panelExpense, panelIncome;
    private TextView tvTabExpense, tvTabIncome;

    private CategoryAdapter adapterExpense;
    private IncomeCategoryAdapter adapterIncome;
    private DatabaseHelper db;
    private android.content.SharedPreferences sharedPreferences;

    private int currentMonth, currentYear;
    private String currentUsername;
    private boolean showingExpense = true;
    private boolean isFormattingBudget = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        tvIncome       = findViewById(R.id.tvIncome);
        tvExpense      = findViewById(R.id.tvExpense);
        btnPrevMonth   = findViewById(R.id.btnPrevMonth);
        btnNextMonth   = findViewById(R.id.btnNextMonth);
        btnLogout      = findViewById(R.id.btnLogout);
        fabAdd         = findViewById(R.id.fabAdd);
        fabStats       = findViewById(R.id.fabStats);
        pieChart       = findViewById(R.id.pieChart);
        etBudget       = findViewById(R.id.etBudget);
        rvExpense      = findViewById(R.id.rvExpense);
        rvIncome       = findViewById(R.id.rvIncome);
        tabExpense     = findViewById(R.id.tabExpense);
        tabIncome      = findViewById(R.id.tabIncome);
        tvTabExpense   = findViewById(R.id.tvTabExpense);
        tvTabIncome    = findViewById(R.id.tvTabIncome);
        panelExpense   = findViewById(R.id.panelExpense);
        panelIncome    = findViewById(R.id.panelIncome);

        currentUsername = getIntent().getStringExtra("USERNAME");
        if (currentUsername == null) currentUsername = "default";

        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH) + 1;
        currentYear  = cal.get(Calendar.YEAR);

        db = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("BudgetPrefs_" + currentUsername, MODE_PRIVATE);

        rvExpense.setLayoutManager(new LinearLayoutManager(this));
        rvExpense.setNestedScrollingEnabled(false);
        rvIncome.setLayoutManager(new LinearLayoutManager(this));
        rvIncome.setNestedScrollingEnabled(false);

        // Hiển thị budget đã lưu với dấu phẩy
        String savedBudget = sharedPreferences.getString("limit", "");
        if (!savedBudget.isEmpty() && !savedBudget.equals("0")) {
            try {
                long num = Long.parseLong(savedBudget);
                etBudget.setText(new DecimalFormat("#,###").format(num));
            } catch (Exception e) {
                etBudget.setText(savedBudget);
            }
        }

        // Format dấu phẩy hạn mức
        etBudget.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (isFormattingBudget) return;
                isFormattingBudget = true;
                String raw = s.toString().replace(",", "");
                if (!raw.isEmpty()) {
                    try {
                        long number = Long.parseLong(raw);
                        String formatted = new DecimalFormat("#,###").format(number);
                        etBudget.setText(formatted);
                        etBudget.setSelection(formatted.length());
                        sharedPreferences.edit().putString("limit", raw).apply();
                    } catch (NumberFormatException e) {}
                } else {
                    sharedPreferences.edit().putString("limit", "").apply();
                }
                isFormattingBudget = false;
                updateUI();
            }
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất không?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        // Xóa session đăng nhập
                        getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                                .edit().clear().apply();
                        // Quay về LoginActivity, xóa hết back stack
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Thôi", null)
                    .show();
        });

        tabExpense.setOnClickListener(v -> switchTab(true));
        tabIncome.setOnClickListener(v -> switchTab(false));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });

        fabStats.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, YearlyStatsActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });

        btnPrevMonth.setOnClickListener(v -> {
            if (currentMonth == 1) { currentMonth = 12; currentYear--; }
            else currentMonth--;
            updateUI();
        });

        btnNextMonth.setOnClickListener(v -> {
            if (currentMonth == 12) { currentMonth = 1; currentYear++; }
            else currentMonth++;
            updateUI();
        });

        switchTab(true);
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void switchTab(boolean expense) {
        showingExpense = expense;
        if (expense) {
            tabExpense.setBackgroundColor(Color.WHITE);
            tvTabExpense.setTextColor(Color.parseColor("#2E7D32"));
            tvTabExpense.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
            tabIncome.setBackgroundColor(Color.parseColor("#F1F8E9"));
            tvTabIncome.setTextColor(Color.parseColor("#9E9E9E"));
            tvTabIncome.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13);
            panelExpense.setVisibility(android.view.View.VISIBLE);
            panelIncome.setVisibility(android.view.View.GONE);
        } else {
            tabIncome.setBackgroundColor(Color.WHITE);
            tvTabIncome.setTextColor(Color.parseColor("#1565C0"));
            tvTabIncome.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
            tabExpense.setBackgroundColor(Color.parseColor("#F1F8E9"));
            tvTabExpense.setTextColor(Color.parseColor("#9E9E9E"));
            tvTabExpense.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13);
            panelExpense.setVisibility(android.view.View.GONE);
            panelIncome.setVisibility(android.view.View.VISIBLE);
        }
    }

    public void updateUI() {
        tvCurrentMonth.setText(String.format("Tháng %02d/%d", currentMonth, currentYear));
        DecimalFormat formatter = new DecimalFormat("#,###");

        double income  = db.getTotalIncomeByMonth(currentMonth, currentYear, currentUsername);
        double expense = db.getTotalExpenseByMonth(currentMonth, currentYear, currentUsername);
        double balance = income - expense;

        tvIncome.setText("+" + formatter.format(income) + "đ");
        tvExpense.setText("-" + formatter.format(expense) + "đ");
        tvTotalBalance.setText((balance >= 0 ? "+" : "") + formatter.format(balance) + " VNĐ");
        tvTotalBalance.setTextColor(balance >= 0 ? Color.parseColor("#2E7D32") : Color.parseColor("#C62828"));

        String budgetStr = etBudget.getText().toString().replace(",", "");
        double budget = 0;
        try { if (!budgetStr.isEmpty()) budget = Double.parseDouble(budgetStr); } catch (Exception e) {}
        if (budget > 0 && expense > budget)
            Toast.makeText(this, "⚠️ Chi tiêu vượt hạn mức!", Toast.LENGTH_SHORT).show();

        List<CategorySummary> expenseList = db.getCategorySummariesByMonth(currentMonth, currentYear, currentUsername);
        setupPieChart(expenseList);
        adapterExpense = new CategoryAdapter(expenseList, currentMonth, currentYear, currentUsername);
        rvExpense.setAdapter(adapterExpense);

        List<CategorySummary> incomeList = db.getIncomeSummariesByMonth(currentMonth, currentYear, currentUsername);
        adapterIncome = new IncomeCategoryAdapter(incomeList, currentMonth, currentYear, currentUsername);
        rvIncome.setAdapter(adapterIncome);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                if (showingExpense && adapterExpense != null) adapterExpense.filter(newText);
                else if (!showingExpense && adapterIncome != null) adapterIncome.filter(newText);
                return true;
            }
        });
    }

    private void setupPieChart(List<CategorySummary> summaryList) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (CategorySummary item : summaryList)
            if (item.getTotalAmount() > 0)
                entries.add(new PieEntry((float) item.getTotalAmount(), item.getCategory()));
        PieDataSet dataSet = new PieDataSet(entries, "Chi tiêu");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setDrawValues(false);
        pieChart.setDrawEntryLabels(false);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getLegend().setEnabled(true);
        pieChart.animateY(600);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }
}