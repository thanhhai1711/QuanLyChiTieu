package com.example.quanlychitieu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        final EditText etAmount = findViewById(R.id.etAmount);
        final EditText etNote = findViewById(R.id.etNote);
        Button btnSave = findViewById(R.id.btnSave);
        final DatabaseHelper db = new DatabaseHelper(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = etAmount.getText().toString();
                if (!money.isEmpty()) {
                    db.addTransaction(Double.parseDouble(money), etNote.getText().toString());
                    Toast.makeText(AddTransactionActivity.this, "Xong rồi Hải!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}