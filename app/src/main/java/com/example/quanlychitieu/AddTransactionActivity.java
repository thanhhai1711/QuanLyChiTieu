package com.example.quanlychitieu;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Nhớ có dòng này
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etNote;
    private Spinner spinnerCategory;
    private Button btnSave;
    private ImageView btnBackAdd; // Khai báo nút lùi
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // 1. Ánh xạ các thành phần
        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);
        btnBackAdd = findViewById(R.id.btnBackAdd); // Ánh xạ nút lùi
        db = new DatabaseHelper(this);

        // 2. Xử lý nút Lùi (Đây là đoạn quan trọng nhất)
        btnBackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Lệnh này để đóng màn hình hiện tại và quay về Main
            }
        });

        // 3. Đổ dữ liệu vào Spinner danh mục
        String[] categories = {"Ăn uống", "Di chuyển", "Mua sắm", "Hóa đơn", "Giải trí", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // 4. Xử lý nút Lưu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountStr = etAmount.getText().toString();
                String note = etNote.getText().toString();
                String category = spinnerCategory.getSelectedItem().toString();

                if (amountStr.isEmpty()) {
                    Toast.makeText(AddTransactionActivity.this, "Nhập số tiền đã cu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                boolean isInserted = db.insertTransaction(amount, note, category);

                if (isInserted) {
                    Toast.makeText(AddTransactionActivity.this, "Đã lưu!", Toast.LENGTH_SHORT).show();
                    finish(); // Lưu xong cũng tự động quay về Main
                }
            }
        });
    }
}