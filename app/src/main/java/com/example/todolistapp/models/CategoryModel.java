package com.example.todolistapp.models;

public class CategoryModel {
    String name;

    public CategoryModel(){

    }

    public CategoryModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
