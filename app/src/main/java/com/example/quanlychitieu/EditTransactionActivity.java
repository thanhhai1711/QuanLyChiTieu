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

public class EditTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etNote;
    private Spinner spinnerCategory;
    private Button btnSave;
    private ImageView btnBackEdit;
    private RadioGroup rgType;
    private RadioButton rbExpense, rbIncome;
    private DatabaseHelper db;
    private int transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        etAmount = findViewById(R.id.etAmountEdit);
        etNote = findViewById(R.id.etNoteEdit);
        spinnerCategory = findViewById(R.id.spinnerCategoryEdit);
        btnSave = findViewById(R.id.btnSaveEdit);
        btnBackEdit = findViewById(R.id.btnBackEdit);
        rgType = findViewById(R.id.rgTypeEdit);
        rbExpense = findViewById(R.id.rbExpenseEdit);
        rbIncome = findViewById(R.id.rbIncomeEdit);
        db = new DatabaseHelper(this);

        btnBackEdit.setOnClickListener(v -> finish());

        transactionId = getIntent().getIntExtra("TRANSACTION_ID", -1);
        String oldAmount = getIntent().getStringExtra("AMOUNT");
        String oldNote = getIntent().getStringExtra("NOTE");
        String oldCategory = getIntent().getStringExtra("CATEGORY");
        String oldType = getIntent().getStringExtra("TYPE");

        etAmount.setText(oldAmount);
        etNote.setText(oldNote);

        if ("income".equals(oldType)) {
            rbIncome.setChecked(true);
            updateCategorySpinner(true, oldCategory);
        } else {
            rbExpense.setChecked(true);
            updateCategorySpinner(false, oldCategory);
        }

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isIncome = (checkedId == R.id.rbIncomeEdit);
            updateCategorySpinner(isIncome, null);
        });

        btnSave.setOnClickListener(view -> {
            String amountStr = etAmount.getText().toString();
            String note = etNote.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();
            String type = rbIncome.isChecked() ? "income" : "expense";

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Nhập số tiền đi!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                boolean updated = db.updateTransaction(transactionId, amount, note, category, type);
                if (updated) {
                    Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi cập nhật!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Tiền phải là số!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategorySpinner(boolean isIncome, String selectCategory) {
        String[] categories = isIncome
                ? new String[]{"Lương", "Thưởng", "Đầu tư", "Bán hàng", "Thu nhập khác"}
                : new String[]{"Ăn uống", "Di chuyển", "Mua sắm", "Hóa đơn", "Giải trí", "Khác"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        if (selectCategory != null) {
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(selectCategory)) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }
    }
}