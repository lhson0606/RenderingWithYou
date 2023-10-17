package com.dy.app.graphic.display;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.dy.app.common.maths.Vec3;
import com.dy.app.common.maths.Vec4;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.listener.ScaleListener;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.render.DyRenderer;
import com.dy.app.utils.DyConst;

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

    static float width;
    static float height;


    private void touchPickingCallback(float mouse_x, float mouse_y){
        width = getWidth();
        height = getHeight();
        Log.d("GameSurface", "x: " + mouse_x + " y: " + mouse_y);
        float x = (2.0f * mouse_x) / width - 1.0f;
        float y = 1.0f - (2.0f * mouse_y) / height;
        float z = 1.0f;
        //ray_nds: ray in normalized device coordinates ray
        Vec3 ray_nds = new Vec3(x, y, z);
        Vec4 ray_clip = new Vec4(ray_nds.x, ray_nds.y, -1.0f, 1.0f);
        Vec4 ray_eye = Camera.getInstance().mProjMat.invert().multiply(ray_clip);
        ray_eye = new Vec4(ray_eye.x, ray_eye.y, -1.0f, 0.0f);
        Vec3 ray_wor = Camera.getInstance().mViewMat.invert().multiply(ray_eye).xyz();
        ray_wor = ray_wor.normalize();
        Vec3 ray_origin = Camera.getInstance().mPos;
        float ground_height = DyConst.board_height;
        float t = (ground_height - ray_origin.y) / ray_wor.y;
        Vec3 intersection = ray_origin.add(ray_wor.multiply(t));
        try {
            Tile tile = Board.getInstance().getTile(intersection);
            tile.getObj().changeState(Obj3D.State.SELECTED);
            Piece piece = tile.getPiece();
            if(piece != null){
                if(piece.isOnPlayerSide())
                    piece.getObj().changeState(Obj3D.State.SOURCE);
                else piece.getObj().changeState(Obj3D.State.ENDANGERED);
            }
        }catch (Exception e) {
            Log.d("GameSurface", "No tile found");
        }



    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        mScaleDetector.onTouchEvent(e);
        if(mScaleListener.isOnScale){
            return true;
        }

        float x = e.getX();
        float y = e.getY();

        touchPickingCallback(x, y);

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
