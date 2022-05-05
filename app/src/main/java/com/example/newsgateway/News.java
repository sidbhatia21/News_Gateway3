package com.example.newsgateway;

import androidx.annotation.NonNull;

public class News {

    private final String id;
    private final String name;
    private final String category;

    public News(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @NonNull
    public String toString(){
        return id;
    }
}
