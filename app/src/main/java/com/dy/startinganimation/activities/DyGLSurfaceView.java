package com.dy.startinganimation.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.dy.startinganimation.animation.AnimRenderer;
import com.dy.startinganimation.camera.Camera;
import com.dy.startinganimation.utils.ScaleListener;

public class DyGLSurfaceView extends android.opengl.GLSurfaceView {

    public DyGLSurfaceView(Context context) {
        super(context);
        mContext = context;
        setEGLContextClientVersion(3);
        mRenderer = new AnimRenderer(context);
        setRenderer(mRenderer);
        mScaleListener = new ScaleListener(Camera.getInstance().getInstance());
        mScaleDetector = new ScaleGestureDetector(context, mScaleListener);
    }

    public DyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected Context mContext;
    protected AnimRenderer mRenderer;
    float preX;
    float preY;
    boolean isOnScale = false;
    private ScaleGestureDetector mScaleDetector;
    private ScaleListener mScaleListener;

    @Override
    public boolean onTouchEvent(MotionEvent e){
        mScaleDetector.onTouchEvent(e);
        if(mScaleListener.isOnScale){
            return true;
        }

        float x = e.getX();
        float y = e.getY();

        if(isOnScale) return true;

        switch(e.getAction()){
            case MotionEvent.ACTION_MOVE:
                Camera.getInstance().getInstance().move(x-preX, y-preY);
                break;
        }

        preX = x;
        preY = y;
        return true;
    }

    public AnimRenderer getRenderer() {
        return mRenderer;
    }
}
