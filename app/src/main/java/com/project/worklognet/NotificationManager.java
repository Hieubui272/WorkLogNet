package com.project.worklognet;

import com.project.worklognet.NotificationHelper; // Import lớp NotificationHelper

public class NotificationManager {

    private int notificationCount;
    private NotificationHelper notificationHelper;

    // Constructor để khởi tạo NotificationManager
    public NotificationManager(NotificationHelper notificationHelper) {
        this.notificationHelper = notificationHelper;
        this.notificationCount = 0; // Số thông báo ban đầu là 0
    }

    // Hàm để tăng số thông báo khi có like hoặc comment
    public void addNotification() {
        notificationCount++;
        notificationHelper.updateNotificationCount(notificationCount);
    }

    // Hàm để reset lại số thông báo
    public void resetNotifications() {
        notificationCount = 0;
        notificationHelper.updateNotificationCount(notificationCount);
    }

    // Hàm để lấy số thông báo hiện tại
    public int getNotificationCount() {
        return notificationCount;
    }
}
