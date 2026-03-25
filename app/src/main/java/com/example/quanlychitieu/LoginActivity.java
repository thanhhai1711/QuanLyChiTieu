package com.example.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Nhập đủ thông tin đi mày!", Toast.LENGTH_SHORT).show();
            } else {
                // Gọi hàm checkUser trong DatabaseHelper (Hàm này tao bảo mày thêm lúc nãy đó)
                if (db.checkUser(user, pass)) {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình chính
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    // Gửi thêm cái tên để MainActivity biết ai đang tiêu tiền
                    intent.putExtra("USERNAME", user);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Sai tên hoặc mật khẩu rồi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Khi bấm vào "Đăng ký ngay"
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}