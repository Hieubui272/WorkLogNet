package com.project.worklognet;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Đảm bảo sử dụng đúng file giao diện
        setContentView(R.layout.fragment_my_page); // Thay activity_profile bằng fragment_my_page
    }
}
