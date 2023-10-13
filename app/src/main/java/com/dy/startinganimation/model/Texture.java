package com.dy.startinganimation.model;

import android.graphics.Bitmap;

import com.dy.startinganimation.utils.GLHelper;

public class Texture {
    private int mID;
    private Bitmap mBitmap;
    public Texture(Bitmap bitmap){
        mBitmap = bitmap;
    }

    public void init(){
        mID = GLHelper.loadTexture(mBitmap);
    }

    //#TODO
    public int getID() {
        return mID;
    }

    public void destroy(){}
}
