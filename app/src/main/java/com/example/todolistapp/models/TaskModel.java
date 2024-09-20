package com.example.todolistapp.models;

public class TaskModel {
    String title;
    String description;
    String date;
    String category;
    int status;

    // constructor
    public TaskModel(){}
    public TaskModel(String title, String description, String date, String category, int status) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.status = status;
    }

    // getters and setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
