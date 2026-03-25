package com.example.quanlychitieu;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etNote;
    private Spinner spinnerCategory;
    private Button btnSave;
    private ImageView btnBackAdd;
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
        btnBackAdd = findViewById(R.id.btnBackAdd);
        db = new DatabaseHelper(this);

        // 2. Xử lý nút Lùi
        btnBackAdd.setOnClickListener(v -> finish());

        // 3. Đổ dữ liệu vào Spinner
        String[] categories = {"Ăn uống", "Di chuyển", "Mua sắm", "Hóa đơn", "Giải trí", "Khác"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // 4. Xử lý nút Lưu
        btnSave.setOnClickListener(view -> {
            String amountStr = etAmount.getText().toString();
            String note = etNote.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();

            // LẤY TÊN NGƯỜI DÙNG ĐỂ LƯU KÈM (QUAN TRỌNG)
            // Tạm thời tao lấy từ Intent, nếu không có thì để mặc định là "User_Hải"
            String username = getIntent().getStringExtra("USERNAME");
            if (username == null) username = "User_Hải";

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Nhập số tiền đi cu!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);

                // FIX LỖI Ở ĐÂY: Truyền đủ 4 tham số (amount, note, category, username)
                boolean isInserted = db.insertTransaction(amount, note, category, username);

                if (isInserted) {
                    Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi lưu dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Tiền phải là số nhé mày!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}