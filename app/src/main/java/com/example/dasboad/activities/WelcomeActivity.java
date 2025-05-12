package com.example.dasboad.activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.dasboad.R;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Tìm nút "Bắt đầu" và xử lý sự kiện
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            // Chuyển sang MainActivity
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng WelcomeActivity để không quay lại
        });
    }
}