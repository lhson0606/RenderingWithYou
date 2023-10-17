package com.dy.app.graphic.listener;

import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;

import com.dy.app.graphic.camera.Camera;

public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
{
    Camera mCamera;
    public boolean isOnScale= false;
    public ScaleListener(Camera camera){
        mCamera = camera;
    }
    @Override
    public boolean onScale(@NonNull ScaleGestureDetector scaleGestureDetector) {
        final float scalar = scaleGestureDetector.getScaleFactor();
        mCamera.scaleR(1f/scalar);
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
