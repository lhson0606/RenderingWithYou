package com.dy.app.graphic.display;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.listener.ScaleListener;
import com.dy.app.graphic.render.DyRenderer;

public class GameSurface extends android.opengl.GLSurfaceView{

    public GameSurface(Context context) {
        super(context);
        mContext = context;
        setEGLContextClientVersion(3);
        mRenderer = new DyRenderer();
        setRenderer(mRenderer);
        mScaleListener = new ScaleListener(Camera.getInstance());
        mScaleDetector = new ScaleGestureDetector(context, mScaleListener);
        setPreserveEGLContextOnPause(true);
    }

    protected Context mContext;
    protected DyRenderer mRenderer;
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

    public DyRenderer getRenderer() {
        return mRenderer;
    }

}
