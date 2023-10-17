package com.dy.app.graphic;

import android.graphics.Bitmap;

import com.dy.app.graphic.model.Texture;

public class Skin implements Cloneable{
    private Material material;
    private Bitmap bitmap;

    public Skin(Material material, Bitmap bitmap) {
        this.material = material;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Material getMaterial() {
        return material;
    }

    public Texture getTexture() {
        Texture res = new Texture(bitmap);
        return res;
    }

    @Override
    public Skin clone(){
        Skin skin = new Skin(material, bitmap);
        return skin;
    }
}
