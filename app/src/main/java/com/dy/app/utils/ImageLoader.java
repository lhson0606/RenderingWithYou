package com.dy.app.utils;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.InputStream;

public class ImageLoader {
    public static BitmapDrawable loadImage(InputStream is){
        BitmapDrawable res = new BitmapDrawable(is);
        return res;
    }

}
