package com.project.worklognet;

public class Comment {
    private String commentId; // ID duy nhất của bình luận
    private String commentText;
    private String userId;
    private String userName; // Tên người dùng
    private long timestamp;

    // Constructor trống cho Firestore
    public Comment() {}

    // Constructor đầy đủ bao gồm commentId
    public Comment(String commentId, String commentText, String userId, String userName, long timestamp) {
        this.commentId = commentId;
        this.commentText = commentText;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    // Getters và Setters cho commentId
    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }

    // Getters và Setters cho commentText
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    // Getters và Setters cho userId
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    // Getters và Setters cho userName
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    // Getters và Setters cho timestamp
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
