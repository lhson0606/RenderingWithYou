package com.dy.startinganimation.common;

import java.io.Serializable;

public class DemoDAE implements Serializable {
    public String path;
    String title;
    String description;
    int thumbnail;

    public DemoDAE(String path, String title, String description, int thumbnail) {
        this.path = path;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
    }
}

