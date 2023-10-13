package com.dy.startinganimation.utils;

import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

public class Loader {
    public BitmapDrawable loadImage(InputStream is){
        BitmapDrawable res = new BitmapDrawable(is);
        return res;
    }

    public static Loader getInstance(){
        return instance = (instance == null) ? new Loader() : instance;
    }

    private static Loader instance = null;
}
