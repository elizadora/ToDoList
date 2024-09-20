package com.example.todolistapp.models;

public class UserModel {
    String name;

    // constructor
    public UserModel(){}
    public UserModel(String name) {
        this.name = name;

    }

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
