package com.dy.startinganimation.utils;

import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;

import com.dy.startinganimation.camera.Camera;

public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
{
    Camera m_Camera;
    public boolean isOnScale= false;
    public ScaleListener(Camera camera){
        m_Camera = camera;
    }
    @Override
    public boolean onScale(@NonNull ScaleGestureDetector scaleGestureDetector) {
        final float scalar = scaleGestureDetector.getScaleFactor();
        m_Camera.scaleR(1f/scalar);
        return isOnScale=true;
    }

    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector scaleGestureDetector) {
        return isOnScale=true;
    }

    @Override
    public void onScaleEnd(@NonNull ScaleGestureDetector scaleGestureDetector) {
        isOnScale= false;
    }
}
