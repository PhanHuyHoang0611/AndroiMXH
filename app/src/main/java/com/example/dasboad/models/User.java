package com.example.dasboad.models;

public class User {
    public String username;
    public String email;
    public String phone;
    public String address;
    public String bio;
    public String avatarUrl;
    public String role;
    public int rank;
    public long joinTime;
    public User() {} // Firebase cần constructor rỗng

    public User(String username, String email, String phone, String address,
                String bio, String avatarUrl, String role, int rank,long joinTime) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.rank = rank;
        this.joinTime = joinTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }
}
