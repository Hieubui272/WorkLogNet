package com.project.worklognet;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkEntry {
    private String documentId;  // Thêm thuộc tính documentId
    private String workPlace;
    private Timestamp startTime;
    private Timestamp endTime;
    private double hoursWorked;
    private double wagePerHour;
    private double totalEarnings;

    // Constructor không tham số
    public WorkEntry() {}

    // Constructor đầy đủ
    public WorkEntry(String documentId, String workPlace, Timestamp startTime, Timestamp endTime, double hoursWorked, double wagePerHour, double totalEarnings) {
        this.documentId = documentId;
        this.workPlace = workPlace;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hoursWorked = hoursWorked;
        this.wagePerHour = wagePerHour;
        this.totalEarnings = totalEarnings;
    }

    // Getter và Setter cho documentId
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Các getter và setter khác
    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public double getWagePerHour() {
        return wagePerHour;
    }

    public void setWagePerHour(double wagePerHour) {
        this.wagePerHour = wagePerHour;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    // Chuyển đổi Timestamp sang String để hiển thị
    public String getFormattedStartTime() {
        Date date = startTime.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public String getFormattedEndTime() {
        Date date = endTime.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}
