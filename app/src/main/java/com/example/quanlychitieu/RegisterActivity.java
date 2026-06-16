package com.example.quanlychitieu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // QUAN TRỌNG: Dòng này fix lỗi báo đỏ ImageButton nè Hải
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        // 1. Ánh xạ các View từ XML
        EditText etUser = findViewById(R.id.etRegUser);
        EditText etPass = findViewById(R.id.etRegPass);
        EditText etConfirm = findViewById(R.id.etRegConfirm);
        Button btnReg = findViewById(R.id.btnRegister);
        ImageButton btnBack = findViewById(R.id.btnBackToLogin);

        // 2. Logic nút Quay lại (Back)
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish(); // Đóng màn hình này để quay về LoginActivity
            });
        }

        // 3. Logic nút Đăng ký
        btnReg.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Điền đủ thông tin đi", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            } else {
                // Gọi hàm lưu vào Database (Bản fix version 4 tao gửi lúc nãy)
                if (db.registerUser(user, pass)) {
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // Nếu nó vào đây thường là do trùng username
                    Toast.makeText(this, "Tên này có người dùng rồi hoặc lỗi DB!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}