package com.dy.startinganimation.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dy.startinganimation.activities.DemoGLActivity;
import com.dy.startinganimation.activities.DyGLSurfaceView;

public class GLFragmentView extends Fragment {

    public static GLFragmentView newInstance(){
        GLFragmentView fragment = new GLFragmentView();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        DyGLSurfaceView glSurfaceView = new DyGLSurfaceView(getActivity());
        Bundle b = new Bundle();
        b.putSerializable("glSurfaceView", glSurfaceView);
        setArguments(b);
        return glSurfaceView;
    }

    public DyGLSurfaceView getGlSurfaceView() {
        DyGLSurfaceView glSurfaceView = (DyGLSurfaceView) getArguments().getSerializable("glSurfaceView");
        return glSurfaceView;
    }
}
