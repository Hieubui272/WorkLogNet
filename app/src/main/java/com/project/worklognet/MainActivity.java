package com.project.worklognet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log; // Import để sử dụng Logcat

import com.project.worklognet.NotificationHelper;
import com.project.worklognet.NotificationManager;

public class MainActivity extends AppCompatActivity {

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Tham chiếu tới layout activity_main.xml

        // Ánh xạ TextView hiển thị số thông báo
        TextView tvNotificationCount = findViewById(R.id.tvNotificationCount);

        // Khởi tạo NotificationHelper và NotificationManager
        NotificationHelper notificationHelper = new NotificationHelper(tvNotificationCount);
        notificationManager = new NotificationManager(notificationHelper);

        // Giả lập thêm thông báo mới khi người dùng like hoặc comment
        Button btnLike = findViewById(R.id.btnLike);
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Like button clicked");
                notificationManager.addNotification(); // Tăng số thông báo khi có like
            }
        });

        Button btnComment = findViewById(R.id.btnComment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Comment button clicked");
                notificationManager.addNotification(); // Tăng số thông báo khi có comment
            }
        });

        // Giả lập reset thông báo
        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Reset button clicked");
                notificationManager.resetNotifications(); // Reset số thông báo về 0
            }
        });
    }
}
