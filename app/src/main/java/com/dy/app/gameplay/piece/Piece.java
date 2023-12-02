package com.dy.app.gameplay.piece;

import android.util.Log;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.graphic.Skin;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.manager.AssetManger;
import com.dy.app.manager.EntityManger;
import com.dy.app.manager.PieceManager;
import com.dy.app.utils.DyConst;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Semaphore;
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
    public PieceState currentState = new PieceState();

    protected final Map<Integer, PieceState> history = new HashMap<Integer, PieceState>();

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

    public void updatePieceStateBeforeWritingToHistory(){
    }

    public void updatePieceStateAfterWritingToHistory(){
        //we need to prepare the piece state for the next turn
        currentState.movedInThisTurn = false;
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

    protected PieceColor pieceColor;

    public boolean isWhite(){
        return pieceColor == PieceColor.WHITE;
    }

    public boolean isInitialized = false;

    @Override
    public void init() {
        obj.init();
        isInitialized = true;
    }

    @Override
    public void update(float dt) {
        if(!isDoingAnimation){
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
        this.currentState.pos = tile.pos;
    }

    public void addStateToHistory(){
        currentState.moveNumber = board.getMoveCount();
        this.history.put(currentState.moveNumber, currentState.clone());
    }

    public boolean isOnPlayerSide(){
        return onPlayerSide;
    }

    public Tile getTile(){
        return tile;
    }

    private PieceState rollbackState = null;
    private Piece pseudoCapturedPiece = null;

    /**
     * This method is used to perform a pseudo move to check if it's a checkmate or not
     * it will not pseudo promote the pawn, because it's only be used when the king is in check so castling is not possible in pseudo move
     * after check the state of the game, we need to rollback the pseudo move by calling rollbackPseudoMove() of this piece
     * else the state of the board will be corrupted
     * @param pos
     */
    public void pseudoMove(Vec2i pos){
        Tile dstTile = board.getTile(pos);

        if(!possibleMoves.contains(dstTile)){
            throw new RuntimeException("Pseudo move is not possible");
        }

        if(dstTile.getPiece() != null){
            //we only need to perform pseudo capture if there is a piece to capture
            if(dstTile.getPiece().getNotation().equals(ChessNotation.KING)){
                return;//if we are capturing the king, we don't need to do anything
            }

            pseudoCapture(dstTile.getPiece());
        }

        rollbackState = currentState.clone();
        tile.setPiece(null);
        tile = board.getTile(pos);
        tile.setPiece(this);
    }

    public void pseudoCapture(Piece piece) {
        pseudoCapturedPiece = piece;
        pseudoCapturedPiece.rollbackState = pseudoCapturedPiece.currentState.clone();
        board.getPieceManager().pseudoCapture(pseudoCapturedPiece);
    }

    public void rollbackPseudoMove(){
        if(rollbackState == null){
            return;//nothing to rollback
        }
        tile.setPiece(null);
        tile = board.getTile(rollbackState.pos);
        tile.setPiece(this);
        currentState = rollbackState;
        rollbackState = null;
        if(pseudoCapturedPiece != null){
            Log.d("Piece", "rollback captured piece: " + pseudoCapturedPiece.getNotation());
            board.getPieceManager().rollbackPseudoCapture(pseudoCapturedPiece);
            pseudoCapturedPiece.currentState = pseudoCapturedPiece.rollbackState;
            //set the piece back to the tile
            pseudoCapturedPiece.getTile().setPiece(pseudoCapturedPiece);
            //prepare for the next pseudo move
            pseudoCapturedPiece = null;
        }
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
        //update state
        currentState.pos = pos;
        currentState.hasMoved = true;
        currentState.movedInThisTurn = true;

        startMoveAnimation(oldTile, newTile);
        return tile;
    }

    public void setStateAtMoveNumber(int moveNumber){
        PieceState stateToGo = history.get(moveNumber);
        currentState = stateToGo.clone();
        this.tile = board.getTile(stateToGo.pos);
        this.tile.setPiece(this);
    }

    public void checkForUndoCapture(int moveNumber){
        PieceState stateToGo = history.get(moveNumber);
        assert stateToGo != null;
        if(!stateToGo.isCaptured && currentState.isCaptured){
            board.undoCapture(this);
        }

        if(stateToGo.isPromoted && !currentState.isPromoted){

            try {
                ((Pawn)this).promote(stateToGo.promotingNotation, false);
            } catch (ClassCastException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }else if(!stateToGo.isPromoted && currentState.isPromoted){
            demote();
        }
    }

    public void goToMove(int moveNumber) {
        PieceState stateToGo = history.get(moveNumber);

//        if(stateToGo.equals(currentState)){
//            return;
//        }

        if(stateToGo == null){
            throw new RuntimeException("stateToGo is null");
        }

        tile.setPiece(null);

        if(!stateToGo.isCaptured && !currentState.isCaptured){
            //we need to reset the piece position
            Tile dstTile = board.getTile(stateToGo.pos);
            Tile srcTile = board.getTile(currentState.pos);
            //we need to undo its position
            srcTile.setPiece(null);
            //dstTile.setPiece(this);
            //this.tile = dstTile;
            startMoveAnimation(srcTile, dstTile,null);

        }else if(stateToGo.isCaptured && !currentState.isCaptured){
            board.capturePiece(this);
        }
//        else if(!stateToGo.isCaptured){
//            //we need to add the piece to the board
//            board.undoCapture(this);
//        }
    }

    private void demote() {
        Piece piece = null;
        Skin skin = null;

        if(isOnPlayerSide()){
            skin = board.getAssetManger().getSkin(AssetManger.SkinType.PLAYER);
        }else{
            skin = board.getAssetManger().getSkin(AssetManger.SkinType.RIVAL);
        }

        //create a new pawn
        try {
            piece = board.getPieceManager().loadSinglePiece(board, isOnPlayerSide(),skin ,DyConst.pawn, tile.pos, pieceColor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        piece.history.putAll(this.history);
        board.replacePiece(this, piece, false);
    }

    protected void capture(Piece piece){
        //board.removePiece(piece);
        board.capturePiece(piece);
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

    public void refreshDisplayPosition(){
        obj.setModelMat(tile.getObj().getModelMat().clone());
    }

    private void completeAnimation(){
        refreshDisplayPosition();
        // Reset animation variables
        isDoingAnimation = false;
        fCurrentAnimationTime = 0f;
        dstTile = null;
        srcTile = null;
        if(onAnimationFinished != null){
            onAnimationFinished.onAnimationFinished();
        }
        onAnimationFinished = null;
    }

    private OnAnimationFinished onAnimationFinished = null;

    private void startMoveAnimation(Tile srcTile, Tile dstTile){
        startMoveAnimation(srcTile, dstTile, null);
    }

    protected void startMoveAnimation(Tile srcTile, Tile dstTile, OnAnimationFinished onAnimationFinished){
        //set do animation to true
        isDoingAnimation = true;
        this.srcTile = srcTile;
        this.dstTile = dstTile;
        this.onAnimationFinished = onAnimationFinished;
    }

    private interface OnAnimationFinished{
        void onAnimationFinished();
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

    public class PieceState implements Cloneable{
        public int moveNumber;
        public Vec2i pos;
        public boolean hasMoved;
        public boolean justAdvancedTwoTiles;
        public boolean isCaptured;
        public boolean isPromoted;
        public String promotingNotation;
        public boolean movedInThisTurn;
        public PieceState(){
            moveNumber = -1;
            pos = null;
            hasMoved = false;
            justAdvancedTwoTiles = false;
            isCaptured = false;
            isPromoted = false;
            movedInThisTurn = false;
        }

        @Override
        public PieceState clone() {
            PieceState clone = new PieceState();
            clone.pos = pos;
            clone.hasMoved = hasMoved;
            clone.justAdvancedTwoTiles = justAdvancedTwoTiles;
            clone.isCaptured = isCaptured;
            clone.isPromoted = isPromoted;
            clone.promotingNotation = promotingNotation;
            clone.movedInThisTurn = movedInThisTurn;
            clone.moveNumber = moveNumber;
            return clone;
        }

        public boolean equals(PieceState pieceState){
            return pos.isEqual(pieceState.pos) && isCaptured == pieceState.isCaptured;
        }
    }
}
