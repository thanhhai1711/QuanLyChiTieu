package com.example.quanlychitieu;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class YearlyStatsActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView tvYear, tvTotalIncome, tvTotalExpense, tvTotalBalance;
    private ImageView btnBack, btnPrevYear, btnNextYear;
    private DatabaseHelper db;
    private String username;
    private int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yearly_stats);

        barChart       = findViewById(R.id.barChart);
        tvYear         = findViewById(R.id.tvYear);
        tvTotalIncome  = findViewById(R.id.tvYearIncome);
        tvTotalExpense = findViewById(R.id.tvYearExpense);
        tvTotalBalance = findViewById(R.id.tvYearBalance);
        btnBack        = findViewById(R.id.btnBackStats);
        btnPrevYear    = findViewById(R.id.btnPrevYear);
        btnNextYear    = findViewById(R.id.btnNextYear);

        db = new DatabaseHelper(this);
        username = getIntent().getStringExtra("USERNAME");
        if (username == null) username = "default";

        currentYear = Calendar.getInstance().get(Calendar.YEAR);

        btnBack.setOnClickListener(v -> finish());
        btnPrevYear.setOnClickListener(v -> { currentYear--; updateUI(); });
        btnNextYear.setOnClickListener(v -> { currentYear++; updateUI(); });

        updateUI();
    }

    private void updateUI() {
        tvYear.setText("Năm " + currentYear);
        DecimalFormat fmt = new DecimalFormat("#,###");

        ArrayList<BarEntry> incomeEntries  = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();

        double totalIncome = 0, totalExpense = 0;

        for (int m = 1; m <= 12; m++) {
            double inc = db.getTotalIncomeByMonth(m, currentYear, username);
            double exp = db.getTotalExpenseByMonth(m, currentYear, username);
            incomeEntries.add(new BarEntry(m - 1, (float) inc));
            expenseEntries.add(new BarEntry(m - 1, (float) exp));
            totalIncome  += inc;
            totalExpense += exp;
        }

        tvTotalIncome.setText("Thu: +" + fmt.format(totalIncome) + " đ");
        tvTotalExpense.setText("Chi: -" + fmt.format(totalExpense) + " đ");
        double balance = totalIncome - totalExpense;
        tvTotalBalance.setText("Số dư: " + (balance >= 0 ? "+" : "") + fmt.format(balance) + " đ");
        tvTotalBalance.setTextColor(balance >= 0 ? Color.parseColor("#4CAF50") : Color.RED);

        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Thu nhập");
        incomeSet.setColor(Color.parseColor("#4CAF50"));
        incomeSet.setDrawValues(false);

        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Chi tiêu");
        expenseSet.setColor(Color.parseColor("#E53935"));
        expenseSet.setDrawValues(false);

        BarData barData = new BarData(incomeSet, expenseSet);
        float groupSpace = 0.3f, barSpace = 0.05f, barWidth = 0.3f;
        barData.setBarWidth(barWidth);

        barChart.setData(barData);
        barChart.groupBars(0f, groupSpace, barSpace);

        String[] months = {"T1","T2","T3","T4","T5","T6","T7","T8","T9","T10","T11","T12"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(12f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setTextColor(Color.DKGRAY);

        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setTextColor(Color.DKGRAY);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.setFitBars(true);
        barChart.animateY(800);
        barChart.invalidate();
    }
}