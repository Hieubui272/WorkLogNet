package com.project.worklognet;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.os.Bundle;
import android.widget.Switch;

public class DarkMode extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sử dụng settings_layout.xml
        setContentView(R.layout.settings_layout);

        // Tìm nút bật/tắt Dark Mode trong settings_layout.xml
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);

        // Lấy trạng thái đã lưu từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("darkMode", false);

        // Áp dụng trạng thái đã lưu
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchDarkMode.setChecked(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Bật/tắt Dark Mode khi chuyển đổi Switch
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("darkMode", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("darkMode", false);
            }
            editor.apply();
        });
    }
}
