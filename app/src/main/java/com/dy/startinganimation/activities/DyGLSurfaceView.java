package com.dy.startinganimation.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.dy.startinganimation.animation.AnimRenderer;
import com.dy.startinganimation.utils.ScaleListener;

public class DyGLSurfaceView extends android.opengl.GLSurfaceView {

    public DyGLSurfaceView(Context context) {
        super(context);
        mContext = context;
        setEGLContextClientVersion(3);
        mRenderer = new AnimRenderer(context);
        setRenderer(mRenderer);
    }

    public DyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected Context mContext;
    protected AnimRenderer mRenderer;
    float preX;
    float preY;
    boolean isOnScale = false;
    ScaleGestureDetector m_ScaleDetector;
    ScaleListener m_ScaleListener;

    @Override
    public boolean onTouchEvent(MotionEvent e){
        m_ScaleDetector.onTouchEvent(e);
        if(m_ScaleListener.isOnScale){
            return true;
        }

        float x = e.getX();
        float y = e.getY();

        if(isOnScale) return true;

        switch(e.getAction()){
            case MotionEvent.ACTION_MOVE:
                //AnimRenderer.camera.move(-(x-preX), y-preY);

                break;
        }

        preX = x;
        preY = y;
        return true;
    }
}
