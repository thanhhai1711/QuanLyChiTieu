package com.example.quanlychitieu;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.text.ParseException;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etNote;
    private Spinner spinnerCategory;
    private Button btnSave;
    private ImageView btnBackAdd;
    private RadioGroup rgType;
    private RadioButton rbExpense, rbIncome;
    private DatabaseHelper db;
    private boolean isFormatting = false; // tránh vòng lặp vô tận

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
        setupAmountFormat(etAmount);

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            updateCategorySpinner(checkedId == R.id.rbIncome);
        });

        String username = getIntent().getStringExtra("USERNAME");

        btnSave.setOnClickListener(view -> {
            // Lấy số thực từ text đã format (bỏ dấu phẩy)
            String rawAmount = etAmount.getText().toString().replace(",", "");
            String note = etNote.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();
            String type = rbIncome.isChecked() ? "income" : "expense";
            String user = (username != null) ? username : "default";

            if (rawAmount.isEmpty()) {
                Toast.makeText(this, "Nhập số tiền đi!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(rawAmount);
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

    // Tự động thêm dấu phẩy khi nhập
    private void setupAmountFormat(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String raw = s.toString().replace(",", "");
                if (!raw.isEmpty()) {
                    try {
                        long number = Long.parseLong(raw);
                        DecimalFormat formatter = new DecimalFormat("#,###");
                        String formatted = formatter.format(number);
                        editText.setText(formatted);
                        editText.setSelection(formatted.length()); // giữ con trỏ cuối
                    } catch (NumberFormatException e) {
                        // bỏ qua nếu không parse được
                    }
                }

                isFormatting = false;
            }
        });
    }

    private void updateCategorySpinner(boolean isIncome) {
        String[] categories = isIncome
                ? new String[]{"Lương", "Thưởng", "Đầu tư", "Bán hàng", "Thu nhập khác"}
                : new String[]{"Ăn uống", "Di chuyển", "Mua sắm", "Hóa đơn", "Giải trí", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }
}