package com.example.quanlychitieu;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etNote;
    private Spinner spinnerCategory;
    private Button btnSave;
    private ImageView btnBackAdd;
    private RadioGroup rgType;
    private RadioButton rbExpense, rbIncome;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);
        btnBackAdd = findViewById(R.id.btnBackAdd);
        rgType = findViewById(R.id.rgType);
        rbExpense = findViewById(R.id.rbExpense);
        rbIncome = findViewById(R.id.rbIncome);
        db = new DatabaseHelper(this);

        btnBackAdd.setOnClickListener(v -> finish());

        updateCategorySpinner(false);

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isIncome = (checkedId == R.id.rbIncome);
            updateCategorySpinner(isIncome);
        });

        String username = getIntent().getStringExtra("USERNAME");

        btnSave.setOnClickListener(view -> {
            String amountStr = etAmount.getText().toString();
            String note = etNote.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();
            String type = (rbIncome.isChecked()) ? "income" : "expense";
            String user = (username != null) ? username : "default";

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Nhập số tiền đi!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                boolean isInserted = db.insertTransaction(amount, note, category, user, type);
                if (isInserted) {
                    Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi lưu dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Tiền phải là số!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategorySpinner(boolean isIncome) {
        String[] categories;
        if (isIncome) {
            categories = new String[]{"Lương", "Thưởng", "Đầu tư", "Bán hàng", "Thu nhập khác"};
        } else {
            categories = new String[]{"Ăn uống", "Di chuyển", "Mua sắm", "Hóa đơn", "Giải trí", "Khác"};
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }
}