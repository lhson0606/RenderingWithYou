package com.dy.app.gameplay.piece;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.manager.EntityManger;
import com.dy.app.manager.PieceManager;
import com.dy.app.utils.DyConst;

import java.util.Map;
import java.util.Vector;

public class Piece implements GameEntity {
    protected Tile tile;
    protected boolean onPlayerSide;
    protected Obj3D obj;
    protected Vector<Tile> possibleMoves = new Vector<Tile>();
    private boolean isPicking = false;

    public void isPicking(boolean b) {
        isPicking = b;
    }

    public enum PieceColor{
        BLACK,
        WHITE
    }

    boolean isTheSameColor(Piece piece){
        return pieceColor == piece.pieceColor;
    }

    protected void updatePossibleMoves(){
        possibleMoves.clear();
    }

    public void showPossibleMoves(){
        for(Tile tile : possibleMoves){

            if(tile.hasPiece()){
                if(!tile.getPiece().isOnPlayerSide())
                    tile.getObj().changeState(Obj3D.State.ENDANGERED);
            }else{
                tile.getObj().changeState(Obj3D.State.SELECTED);
            }
        }
    }

    public  void unhighlightPossibleMoves(){
        for(Tile tile : possibleMoves){
            tile.getObj().changeState(Obj3D.State.NORMAL);
        }
    }

    private PieceColor pieceColor;

    public void setPieceColor(PieceColor pieceColor){
        this.pieceColor = pieceColor;
    }

    public boolean isBlack(){
        return pieceColor == PieceColor.BLACK;
    }

    public boolean isWhite(){
        return pieceColor == PieceColor.WHITE;
    }

    @Override
    public void init() {
        obj.init();
    }

    @Override
    public void update(float dt) {
        if(isPicking){
            showPossibleMoves();
            obj.changeState(Obj3D.State.SELECTED);
        }else{
            unhighlightPossibleMoves();
            obj.changeState(Obj3D.State.NORMAL);
        }
        updatePossibleMoves();
    }

    @Override
    public void draw() {
        obj.draw(Camera.getInstance().mViewMat, Camera.getInstance().mViewMat);
    }

    @Override
    public void destroy() {
        obj.destroy();
    }

    public Vec2i tilePos(){
        return tile.pos;
    }

    public Obj3D getObj(){
        return obj;
    }

    public Piece(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor){
        this.tile = tile;
        this.obj = obj;
        this.onPlayerSide = onPlayerSide;
        this.possibleMoves = new Vector<Tile>();
        this.pieceColor = pieceColor;
        updatePossibleMoves();
        EntityManger.getInstance().newEntity(this);
    }

    public boolean isOnPlayerSide(){
        return onPlayerSide;
    }

    public void setOnPlayerSide(boolean onPlayerSide){
        this.onPlayerSide = onPlayerSide;
    }

    public Tile getTile(){
        return tile;
    }

    public Tile move(Vec2i pos){
        tile.setPiece(null);
        Tile oldTile = tile;
        oldTile.getObj().changeState(Obj3D.State.HIGHLIGHTED);
        tile = Board.getInstance().getTile(pos);

        if(tile.getPiece() != null){
            capture(tile.getPiece());
        }

        Vec2i trans = new Vec2i(pos.x - oldTile.pos.x, pos.y - oldTile.pos.y);
        Vec3 translation = new Vec3(trans.x* DyConst.tile_size, 0, trans.y*DyConst.tile_size);
        obj.translate(translation);
        tile.setPiece(this);

        tile.getObj().changeState(Obj3D.State.SOURCE);
        //updatePossibleMoves();
        obj.changeState(Obj3D.State.NORMAL);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for(GameEntity entity : EntityManger.getInstance().getEntities()){
                    entity.update(0);
                }
            }
        });
        t.start();
        return oldTile;
    }

    public void capture(Piece piece){
        PieceManager.getInstance().removePiece(piece);
    }

    public Vector<Tile> getPossibleMoves(){
        return possibleMoves;
    }
}
