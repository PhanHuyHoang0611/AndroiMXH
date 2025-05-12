package com.example.dasboad.activities;

import java.util.List;

public class Post {
    private String username;
    private String time;
    private String content;
    private Integer singleImage;
    private List<Integer> imageList;

    public Post(String username, String time, String content, Integer singleImage, List<Integer> imageList) {
        this.username = username;
        this.time = time;
        this.content = content;
        this.singleImage = singleImage;
        this.imageList = imageList;
    }

    public String getUsername() { return username; }
    public String getTime() { return time; }
    public String getContent() { return content; }
    public Integer getSingleImage() { return singleImage; }
    public List<Integer> getImageList() { return imageList; }
}