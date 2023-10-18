package com.dy.app.graphic.display;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.dy.app.common.maths.Vec3;
import com.dy.app.common.maths.Vec4;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.listener.ScaleListener;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.render.DyRenderer;
import com.dy.app.utils.DyConst;

public class GameSurface extends android.opengl.GLSurfaceView{

    public GameSurface(Context context) {
        super(context);
        mContext = context;
        setEGLContextClientVersion(3);
        mRenderer = new DyRenderer(this);
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
    private GestureDetector mGestureDetector;
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

        if(mGestureDetector != null){
            mGestureDetector.onTouchEvent(e);
        }

        return true;
    }

    public DyRenderer getRenderer() {
        return mRenderer;
    }
    public void setGestureDetector(GestureDetector gestureDetector){
        mGestureDetector = gestureDetector;
    }

}
