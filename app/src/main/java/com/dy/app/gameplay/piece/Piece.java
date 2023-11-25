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
import java.util.concurrent.locks.ReentrantLock;

public class Piece implements GameEntity {
    protected Tile tile;
    protected boolean onPlayerSide;
    protected Obj3D obj;
    protected Vector<Tile> possibleMoves = new Vector<Tile>();
    protected boolean isPicking = false;
    protected final Board board;
    protected boolean isDoingAnimation = false;
    //for animation
    private Tile srcTile, dstTile = null;

    public void pickUp() {
        isPicking = true;
    }

    public void putDown(){
        isPicking = false;
        unhighlightPossibleMoves();
    }

    public enum PieceColor{
        BLACK,
        WHITE
    }

    boolean isTheSameColor(Piece piece){
        return pieceColor == piece.pieceColor;
    }

    public void updatePossibleMoves(){
        possibleMoves.clear();
    }

    public void showPossibleMoves(){
        for(Tile tile : possibleMoves){
            if(tile.hasPiece()){
                tile.getObj().changeState(Obj3D.State.ENDANGERED);
            }else{
                tile.getObj().changeState(Obj3D.State.SELECTED);
            }
        }

        obj.changeState(Obj3D.State.SELECTED);
    }

    private void unhighlightPossibleMoves(){
        for(Tile tile : possibleMoves){
            tile.getObj().changeState(Obj3D.State.NORMAL);
        }

        obj.changeState(Obj3D.State.NORMAL);
        tile.getObj().changeState(Obj3D.State.NORMAL);
    }

    private PieceColor pieceColor;

    public boolean isWhite(){
        return pieceColor == PieceColor.WHITE;
    }

    @Override
    public void init() {
        obj.init();
    }

    @Override
    public void update(float dt) {
        if(!isDoingAnimation){
            //updatePossibleMoves();

            if(isPicking) {
                showPossibleMoves();
            }
        } else {
            //do animation
            doAnimation(dt);
        }
    }

    private float fCurrentAnimationTime = 0f;
    private float fAnimationDuration  = 0.2f;

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

    public Piece(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor, Board board){
        this.tile = tile;
        this.obj = obj;
        this.onPlayerSide = onPlayerSide;
        this.possibleMoves = new Vector<Tile>();
        this.pieceColor = pieceColor;
        this.board = board;
    }

    public boolean isOnPlayerSide(){
        return onPlayerSide;
    }

    public Tile getTile(){
        return tile;
    }

    public Tile move(Vec2i pos){
        //perform move
        tile.setPiece(null);
        Tile newTile = board.getTile(pos);
        Tile oldTile = tile;

        tile.setPiece(null);
        tile = newTile;
        //check if there is a piece to capture
        if(tile.getPiece() != null){
            capture(tile.getPiece());
        }
        tile.setPiece(this);

        board.updateBoardState();

        startMoveAnimation(oldTile, newTile);
        return tile;
    }

    private void capture(Piece piece){
        board.getEntityManger().removeEntity(piece);
        board.getPieceManager().removePiece(piece);
    }

    private void doAnimation(float dt){
        fCurrentAnimationTime+= dt;
        float fProgress = fCurrentAnimationTime/fAnimationDuration;
        Vec2i trans = new Vec2i(dstTile.pos.x - srcTile.pos.x, dstTile.pos.y - srcTile.pos.y);
        Vec3 translation = new Vec3(trans.x* DyConst.tile_size, 0, trans.y*DyConst.tile_size).multiply(fProgress);
        obj.setTranslation(srcTile.getWorldPos());
        obj.translate(translation);

        //animation is finished
        if(fCurrentAnimationTime>fAnimationDuration){
            completeAnimation();
        }
    }

    private void completeAnimation(){
        Vec2i trans = new Vec2i(dstTile.pos.x - srcTile.pos.x, dstTile.pos.y - srcTile.pos.y);
        Vec3 translation = new Vec3(trans.x* DyConst.tile_size, 0, trans.y*DyConst.tile_size);
        // Update game state
        obj.setTranslation(srcTile.getWorldPos());
        obj.translate(translation);
        obj.changeState(Obj3D.State.NORMAL);
        // Reset animation variables
        isDoingAnimation = false;
        fCurrentAnimationTime = 0f;
        dstTile = null;
        srcTile = null;
    }

    private void startMoveAnimation(Tile srcTile, Tile dstTile){
        //set do animation to true
        isDoingAnimation = true;
        this.srcTile = srcTile;
        this.dstTile = dstTile;
    }

    public Vector<Tile> getPossibleMoves(){
        return possibleMoves;
    }

    public Vector<Tile> getControlledTiles(){
        return possibleMoves;
    }

    public String getNotation(){
        return "";
    }
}
