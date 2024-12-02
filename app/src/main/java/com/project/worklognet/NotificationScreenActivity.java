package com.project.worklognet;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationScreenActivity extends AppCompatActivity {

    private RecyclerView rvUserNotifications;
    private NotificationAdapter notificationAdapter;
    private List<String> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);

        // Khởi tạo danh sách thông báo
        notificationList = new ArrayList<>();

        // Gán RecyclerView và thiết lập Adapter
        rvUserNotifications = findViewById(R.id.rvUserNotifications);
        if (rvUserNotifications != null) {
            rvUserNotifications.setLayoutManager(new LinearLayoutManager(this));
            notificationAdapter = new NotificationAdapter(notificationList);
            rvUserNotifications.setAdapter(notificationAdapter);
        }

        // Thêm thông báo mô phỏng
        addNotification("User1 liked your post.");
        addNotification("User2 commented on your post.");
        addNotification("User3 shared your post.");
    }

    private void addNotification(String content) {
        notificationList.add(content);
        if (notificationAdapter != null) {
            notificationAdapter.notifyDataSetChanged();
        }
    }
}
