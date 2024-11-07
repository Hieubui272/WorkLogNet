package com.project.worklognet;

public class User {
    private String uid;
    private String nickname;
    private String email;

    // Constructor trống để Firestore có thể khởi tạo đối tượng
    public User() { }

    public User(String uid, String nickname, String email) {
        this.uid = uid;
        this.nickname = nickname;
        this.email = email;
    }

    // Getters và Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
