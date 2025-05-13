package com.example.dasboad.models;

public class Answer {
    public String id;
    public String questionId;
    public String content;
    public String image;
    public String userId;
    public long timestamp;
    public int voteCount;

    public Answer() {}

    public Answer(String id, String questionId, String content,String image, String userId, long timestamp, int voteCount) {
        this.id = id;
        this.questionId = questionId;
        this.content = content;
        this.image= image;
        this.userId = userId;
        this.timestamp = timestamp;
        this.voteCount = voteCount;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
