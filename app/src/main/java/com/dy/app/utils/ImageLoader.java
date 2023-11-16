package com.dy.app.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {
    public static BitmapDrawable loadImage(InputStream is){
        BitmapDrawable res = new BitmapDrawable(is);
        return res;
    }

    //see https://stackoverflow.com/questions/3375166/android-drawable-images-from-url
    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap bitmap;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        bitmap = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

}
