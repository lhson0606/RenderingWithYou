package com.dy.app.graphic.listener;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.dy.app.common.maths.Vec3;
import com.dy.app.common.maths.Vec4;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.utils.DyConst;

public class TilePicker extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    private float width;
    private float height;
    private boolean isPicking = false;

    public TilePicker(float w, float h){
        width = w;
        height = h;
    }

    private Tile lastTile = null;
    private Piece lastPiece = null;
    private boolean firstTouch = true;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("TilePicker", "onTouch");
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(firstTouch){
                    Piece piece = getPiece(event.getX(), event.getY());
                    if(piece == null) return false;

                    if(piece != lastPiece && lastPiece != null)
                        lastPiece.getObj().changeState(Obj3D.State.NORMAL);

                    piece.getObj().changeState(Obj3D.State.SELECTED);
                    lastPiece = piece;
                }
            case MotionEvent.ACTION_UP:
                firstTouch = true;
                break;
        }



        return false;
    }

    private Tile getTile(float mouse_x, float mouse_y){
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
        return Board.getInstance().getTile(intersection);
    }

    private Piece getPiece(float mouse_x, float mouse_y){
        Tile tile = getTile(mouse_x, mouse_y);
        if(tile == null) return null;
        return tile.getPiece();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d("TilePicker", "onSingleTapConfirmed");



        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Piece piece = getPiece(e.getX(), e.getY());
        if(piece == null) return false;

        if(piece == lastPiece)
            piece.getObj().changeState(Obj3D.State.NORMAL);
        else if(lastPiece != null)
            lastPiece.getObj().changeState(Obj3D.State.NORMAL);

        lastPiece = null;
        return true;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setScreenSize(float width, float height){
        this.width = width;
        this.height = height;
    }
}
