package com.example.quanlychitieu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private FloatingActionButton fabAdd;
    private RecyclerView rvTransactions;
    private EditText etBudget;
    private PieChart pieChart;
    private ImageButton btnPrevMonth, btnNextMonth;

    private CategoryAdapter adapter;
    private DatabaseHelper db;
    private android.content.SharedPreferences sharedPreferences;

    private int currentMonth, currentYear;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        fabAdd = findViewById(R.id.fabAdd);
        rvTransactions = findViewById(R.id.rvTransactions);
        pieChart = findViewById(R.id.pieChart);
        etBudget = findViewById(R.id.etBudget);

        currentUsername = getIntent().getStringExtra("USERNAME");
        if (currentUsername == null) currentUsername = "default";

        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH) + 1;
        currentYear = cal.get(Calendar.YEAR);

        db = new DatabaseHelper(this);
        // SharedPreferences riêng theo từng user
        sharedPreferences = getSharedPreferences("BudgetPrefs_" + currentUsername, MODE_PRIVATE);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        String savedBudget = sharedPreferences.getString("limit", "0");
        etBudget.setText(savedBudget);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
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
        tvCurrentMonth.setText(String.format("Tháng %02d/%d", currentMonth, currentYear));

        DecimalFormat formatter = new DecimalFormat("#,###");

        double income = db.getTotalIncomeByMonth(currentMonth, currentYear, currentUsername);
        double expense = db.getTotalExpenseByMonth(currentMonth, currentYear, currentUsername);
        double balance = income - expense;

        tvIncome.setText("Thu: +" + formatter.format(income) + " VNĐ");
        tvExpense.setText("Chi: -" + formatter.format(expense) + " VNĐ");

        tvTotalBalance.setText((balance >= 0 ? "+" : "") + formatter.format(balance) + " VNĐ");
        tvTotalBalance.setTextColor(balance >= 0 ? Color.parseColor("#4CAF50") : Color.RED);

        String budgetStr = etBudget.getText().toString();
        double budget = 0;
        try { if (!budgetStr.isEmpty()) budget = Double.parseDouble(budgetStr); } catch (Exception e) {}
        if (budget > 0 && expense > budget) {
            Toast.makeText(this, "⚠️ Chi tiêu vượt hạn mức tháng này rồi!", Toast.LENGTH_SHORT).show();
        }

        List<CategorySummary> summaryList = db.getCategorySummariesByMonth(currentMonth, currentYear, currentUsername);
        setupPieChart(summaryList);

        adapter = new CategoryAdapter(summaryList, currentMonth, currentYear, currentUsername);
        rvTransactions.setAdapter(adapter);

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
            if (item.getTotalAmount() > 0)
                entries.add(new PieEntry((float) item.getTotalAmount(), item.getCategory()));
        }
        PieDataSet dataSet = new PieDataSet(entries, "Chi tiêu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieChart.setDrawEntryLabels(false);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        pieChart.getLegend().setEnabled(true);
        pieChart.animateY(800);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }
}