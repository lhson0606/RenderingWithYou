package com.dy.app.graphic.listener;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.dy.app.common.maths.Vec3;
import com.dy.app.common.maths.Vec4;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.utils.DyConst;

public class TilePicker extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    private float width;
    private float height;
    private boolean isPicking = false;
    private Board board;

    public TilePicker(float w, float h, Board board){
        width = w;
        height = h;
        this.board = board;
    }

    public static Tile lastTile = null;
    private static Piece lastPiece = null;
    private boolean firstTouch = true;

    private void cancelPicking(){
        lastPiece.putDown();
        lastPiece = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //ugly diagram by me
        //https://app.diagrams.net/#G1kvrd5YVAJhFpZ6-JCbSDUIHSVGOq_gVz

        if(!Player.getInstance().isInTurn()) return false;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(firstTouch) {
                    firstTouch = false;
                    Tile tile = getTile(event.getX(), event.getY());
                    Piece piece = null;
                    if(lastPiece == null){
                        if(tile == null){
                            //do nothing :)
                            return false;
                        }else{
                            piece = tile.getPiece();
                            if(piece == null){
                                //do nothing :)
                                return false;
                            }else if(!piece.isOnPlayerSide()){
                                //#todo for testing
                                piece.pickUp();
                                lastPiece = piece;
                                return false;

                            }else{/*is player piece*/
                                piece.pickUp();
                                lastPiece = piece;
                                return false;
                            }
                        }
                    } else if(tile == null) {
                        cancelPicking();
                        return false;
                    } else if(!lastPiece.getPossibleMoves().contains(tile)){
                        cancelPicking();
                        return false;
                    }else if(!tile.hasPiece()){
                        //perform move
                        lastPiece.putDown();
                        lastPiece.move(tile.pos);
                        //#todo for debugging
                        board.updateBoardState();
                        cancelPicking();
                        return false;
                    }else{
                        //perform attack
                        lastPiece.putDown();
                        lastPiece.move(tile.pos);
                        //#todo for debugging
                        board.updateBoardState();
                        cancelPicking();
                        return false;
                    }

                }else{
                    return false;
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
        return board.getTile(intersection);
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
        //ugly diagram by me
        //https://app.diagrams.net/#G1kvrd5YVAJhFpZ6-JCbSDUIHSVGOq_gVz
        Tile tile = getTile(e.getX(), e.getY());
        if (tile == null) {
            //do nothing :)
        } else {
            Piece piece = tile.getPiece();
            if (piece == null) {
                //do nothing :))
            } else {
                if (lastPiece == null) {
                    //do nothing also :)))
                } else if (lastPiece == piece) {
//                    lastPiece.putDown();
//                    lastPiece = null;
                    //#todo for testing

                } else {
                    //do nothing also :))))
                }
            }
        }
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
