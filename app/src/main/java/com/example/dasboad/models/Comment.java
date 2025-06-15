package com.example.dasboad.models;

public class Comment {
    public String id;
    public String answerId;

    public String userId;
    public String content;
    public String image;     // nếu có ảnh trong bình luận
    public long timestamp;

    public Comment() {}

    public Comment(String id, String answerId, String userId, String content, String image, long timestamp) {
        this.id = id;
        this.answerId = answerId;

        this.userId = userId;
        this.content = content;
        this.image = image;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getQuestionId() { return answerId; }

    public void setAnswerId(String answerId) { this.answerId = answerId; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}