package com.dy.startinganimation.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dy.startinganimation.activities.DyGLSurfaceView;

public class GLFragmentView extends Fragment {
    private DyGLSurfaceView glSurfaceView;
    public GLFragmentView(Activity activity){
        super();
        glSurfaceView = new DyGLSurfaceView(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return glSurfaceView;
    }

    public DyGLSurfaceView getGlSurfaceView() {
        return glSurfaceView;
    }
}
