package com.dy.startinganimation.model;

import com.dy.startinganimation.utils.GLHelper;

public class Texture {
    private String mPath;
    private int mID;
    public Texture(int id, String path){
        mPath = path;
        mID = id;
    }
    //#TODO
    public int getID() {
        return mID;
    }

    public void destroy(){}
}
