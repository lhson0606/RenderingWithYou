package com.dy.app.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dy.app.R;
import com.dy.app.utils.Utils;

public class FragmentSplashScreen1 extends Fragment {
    public static final int BG_RES[] = {R.raw.chess_wallpaper_1, R.raw.chess_wallpaper_2, R.raw.chess_wallpaper_3, R.raw.chess_wallpaper_4, R.raw.chess_wallpaper_5};

    public FragmentSplashScreen1 newInstance(){
        return new FragmentSplashScreen1();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.splash_screen_1, container, false);
        //rootContainer
        View bg = v.findViewById(R.id.rootContainer);
        int randomBgIndex = Utils.randomInt(0, BG_RES.length - 1);
        bg.setBackgroundResource(BG_RES[randomBgIndex]);
        return v;
    }
}
