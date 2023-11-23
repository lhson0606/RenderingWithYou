package com.dy.app.manager;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import com.dy.app.R;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class BackgroundManger {
    private Vector<BitmapDrawable> loading_bg;

    public BackgroundManger() {
        loading_bg = new Vector<BitmapDrawable>();
    }

    public void load_loading_bg(Context context){
        Integer count = 0;
        while(true){
            try {
                String path = DyConst.loading_bg_path + DyConst.loading_bg_name + count.toString() + ".jpg";
                InputStream is = context.getAssets().open(path);
                count++;
                loading_bg.add(new BitmapDrawable(is));
            } catch (IOException e) {
                break;
            }
        }
    }

    public BitmapDrawable getRandomLoadingBackground(){
        return loading_bg.get(Utils.randomInt(0, loading_bg.size()-1));
    }
}
