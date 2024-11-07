package com.project.worklognet;

public class Post {
    private String postId;
    private String userId;
    private String content;
    private String imageUrl;
    private long timestamp;
    private int likes = 0;  // Khởi tạo mặc định là 0
    private boolean liked = false; // Khởi tạo mặc định là chưa like

    // Constructor không tham số bắt buộc cho Firestore
    public Post() {}

    public Post(String postId, String userId, String content, String imageUrl, long timestamp, int likes, boolean liked) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.likes = Math.max(0, likes); // Đảm bảo không âm
        this.liked = liked;
    }

    // Getters và Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = Math.max(0, likes); }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }
}
