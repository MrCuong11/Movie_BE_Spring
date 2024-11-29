package com.movie.Movie_BE.dto;

public class UpdateCommentDTO {
    private String username;
    private String newContent;

    // Getters và setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }
}