package com.dy.app.graphic.model;


import android.graphics.Bitmap;

import com.dy.app.utils.GLHelper;

public class Texture {
    private int ID;
    private Bitmap bitmap;
    public Texture(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    //#TODO
    public int getID() {
        return ID;
    }

    public void destroy(){}

    public void init() {
        ID = GLHelper.loadTexture(bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
