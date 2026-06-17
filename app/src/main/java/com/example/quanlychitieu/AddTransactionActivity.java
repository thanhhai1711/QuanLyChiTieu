package com.example.quanlychitieu;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
        setupAmountFormat(etAmount);

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            updateCategorySpinner(checkedId == R.id.rbIncome);
        });

        String username = getIntent().getStringExtra("USERNAME");

        btnSave.setOnClickListener(view -> {
            // Sử dụng replaceAll("[^0-9]", "") để loại bỏ tất cả ký tự không phải số (phẩy, chấm, khoảng cách...)
            String rawAmount = etAmount.getText().toString().replaceAll("[^0-9]", "").trim();
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
                    Toast.makeText(this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Báo cho MainActivity biết để cập nhật lại danh sách
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi lưu dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("AddTransaction", "Error saving transaction", e);
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class AmountTextWatcher implements TextWatcher {
        private final EditText editText;
        private boolean isFormatting = false;

        AmountTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isFormatting) return;

            String raw = s.toString().replaceAll("[^0-9]", "").trim();
            if (raw.isEmpty()) return;

            try {
                long number = Long.parseLong(raw);
                String formatted = new DecimalFormat("#,###").format(number);

                isFormatting = true;
                editText.setText(formatted);
                editText.setSelection(formatted.length());
                isFormatting = false;
            } catch (NumberFormatException e) {
                isFormatting = false;
            }
        }
    }

    private void setupAmountFormat(EditText editText) {
        editText.addTextChangedListener(new AmountTextWatcher(editText));
    }

    private void updateCategorySpinner(boolean isIncome) {
        String[] categories = isIncome
                ? new String[]{"Lương", "Thưởng", "Đầu tư", "Bán hàng", "Thu nhập khác"}
                : new String[]{"Ăn uống", "Di chuyển", "Mua sắm", "Hóa đơn", "Giải trí", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }
}