package com.dy.startinganimation.common;

import java.io.Serializable;

public class DemoOBJ implements Serializable {
    public String path;
    public String title;
    public String description;
    public String shaderPath;
    public DemoOBJ(String path, String title, String description, String shaderPath){
        this.path = path;
        this.title = title;
        this.description = description;
        this.shaderPath = shaderPath;
    }
}
