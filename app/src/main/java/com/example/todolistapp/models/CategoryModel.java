package com.example.todolistapp.models;

public class CategoryModel {
    String name;

    // constructor
    public CategoryModel(){}
    public CategoryModel(String name) {
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
