package com.project.worklognet;

import android.view.View;
import android.widget.TextView;

public class NotificationHelper {

    private TextView tvNotificationCount;

    // Constructor để khởi tạo NotificationHelper với TextView chứa số thông báo
    public NotificationHelper(TextView tvNotificationCount) {
        this.tvNotificationCount = tvNotificationCount;
    }

    // Hàm cập nhật số thông báo
    public void updateNotificationCount(int count) {
        if (count > 0) {
            tvNotificationCount.setText(String.valueOf(count));
            tvNotificationCount.setVisibility(View.VISIBLE); // Hiển thị số thông báo
        } else {
            tvNotificationCount.setVisibility(View.GONE); // Ẩn nếu không có thông báo
        }
    }
}
