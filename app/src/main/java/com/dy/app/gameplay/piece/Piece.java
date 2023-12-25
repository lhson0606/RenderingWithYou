package com.dy.app.gameplay.piece;

import android.util.Log;

import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.player.Player;
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
    private Vector<PieceState> rollbackStates = new Vector<>();
    private Vector<Piece> pseudoCapturedPieces = new Vector<>();
    private Vector<Boolean> hasPseudoCapturedAPiece = new Vector<>();

    public void putDown(){
        isPicking = false;
        unhighlightPossibleMoves();
    }

    public enum PieceColor{
        BLACK,
        WHITE
    }

    public boolean isLegalMove(Tile tile){
        if(!possibleMoves.contains(tile)){
            throw new RuntimeException("Tile is not a possible move");
        }else {
            pseudoMove(tile.pos);
            Vector<Tile> enemyControlledTiles = isWhite()? board.getBlackControlledTile() : board.getWhiteControlledTile();
            King king = isWhite()? board.getPieceManager().getWhiteKing() : board.getPieceManager().getBlackKing();
            Tile afterPseudoMoveKingTile = king.getTile();
            rollbackPseudoMove();

            if(enemyControlledTiles.contains(afterPseudoMoveKingTile)){
                return false;
            }else{
                return true;
            }
        }
    }

    protected void removeIllegalMoves(){
        Vector<Tile> illegalMoves = new Vector<>();
        Vector<Tile> currentPossibleMoves = new Vector<>();
        currentPossibleMoves.addAll(possibleMoves);

        for(Tile tile : currentPossibleMoves){
            if(!isLegalMove(tile)){
                illegalMoves.add(tile);
            }
        }

        possibleMoves.removeAll(illegalMoves);
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
        try{
            animationLock.lock();
            if(!isDoingAnimation){
                if(isPicking) {
                    showPossibleMoves();
                }
            } else {
                //do animation
                doAnimation(dt);
            }
        }finally {
            animationLock.unlock();
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
        //return onPlayerSide;
        if(Player.getInstance().isWhitePiece()){
            return pieceColor == PieceColor.WHITE;
        }else{
            return pieceColor == PieceColor.BLACK;
        }
    }

    public Tile getTile(){
        return tile;
    }

    public void pseudoMove(Vec2i pos){
        Tile dstTile = board.getTile(pos);

        if(!possibleMoves.contains(dstTile)){
            throw new RuntimeException("Pseudo move is not possible");
        }

        if(dstTile.getPiece() != null){
            pseudoCapture(dstTile.getPiece());
        }else{
            hasPseudoCapturedAPiece.add(false);
        }

        pushrollbackStates();
        currentState.pos = pos;
        tile.setPiece(null);
        tile = board.getTile(pos);
        tile.setPiece(this);
    }
    
    private void pushrollbackStates(){
        rollbackStates.add(currentState.clone());
    }
    
    private PieceState poprollbackStates(){
        if(rollbackStates.isEmpty()){
            return null;
        }
        return rollbackStates.remove(rollbackStates.size() - 1).clone();
    }

    private void pushPseudoCapturedPiece(Piece piece){
        pseudoCapturedPieces.add(piece);
    }

    private Piece popPseudoCapturedPiece(){
        if(pseudoCapturedPieces.isEmpty()){
            return null;
        }
        return pseudoCapturedPieces.remove(pseudoCapturedPieces.size() - 1);
    }

    public void pseudoCapture(Piece piece) {
        pushPseudoCapturedPiece(piece);
        piece.pushrollbackStates();
        board.getPieceManager().pseudoCapture(piece);
        hasPseudoCapturedAPiece.add(true);
    }

    public void rollbackPseudoMove(){
        PieceState rbState = poprollbackStates();
        if(rbState == null){
            throw new RuntimeException("rollback state is null");
        }
        tile.setPiece(null);
        tile = board.getTile(rbState.pos);
        tile.setPiece(this);
        currentState = rbState;
        Boolean hasCapturedAPiece = hasPseudoCapturedAPiece.remove(hasPseudoCapturedAPiece.size() - 1);
        if(hasCapturedAPiece){
            Piece pseudoCapturedPiece = popPseudoCapturedPiece();
            Log.d("Piece", "rollback captured piece: " + pseudoCapturedPiece.getNotation());
            board.getPieceManager().rollbackPseudoCapture(pseudoCapturedPiece);
            PieceState pseudoCapturedPieceState = pseudoCapturedPiece.poprollbackStates();
            pseudoCapturedPiece.currentState = pseudoCapturedPieceState;
            //set the piece back to the tile
            pseudoCapturedPiece.getTile().setPiece(pseudoCapturedPiece);
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

    public void setStateAndTileAtMoveNumber(int moveNumber){
        PieceState stateToGo = history.get(moveNumber);
        //update its state
        currentState = stateToGo.clone();
        //make tile point to the tile at that state
        this.tile = board.getTile(stateToGo.pos);
        this.tile.setPiece(this);
    }

    public void checkForUndoCaptureState(int moveNumber){
        PieceState stateToGo = history.get(moveNumber);
        assert stateToGo != null;
        //#todo: have deeper look at this
        if(!stateToGo.isCaptured && currentState.isCaptured){
            board.undoCapture(this);
        }else if(stateToGo.isCaptured && !currentState.isCaptured){
            board.getPieceManager().capturePiece(this);
        }
    }

    public void checkForUndoPromotionState(int moveNumber){
        PieceState stateToGo = history.get(moveNumber);
        assert stateToGo != null;
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

    /**
     * This method is used to go to a specific position at a specific move number
     * assumes the board is clean (means that there's tile has a piece on it)
     * @param moveNumber
     * @param isMoveAnimated
     */
    public void goToPositionAtState(int moveNumber, boolean isMoveAnimated){
        PieceState stateToGo = history.get(moveNumber);

        if(stateToGo == null){
            //ensure that state to go shouldn't be null
            throw new RuntimeException("stateToGo is null");
        }

        Tile dstTile = board.getTile(stateToGo.pos);
        Tile srcTile = board.getTile(currentState.pos);

        dstTile.setPiece(this);
        tile = dstTile;

        if(isMoveAnimated){
            startMoveAnimation(srcTile, dstTile, null);
        }
    }

    public void goToMove(int moveNumber) throws Exception{
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

    /**
     * Should only be called by game loop thread
     */
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

    /**
     * should only be called from doAnimation()
     */
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

    private ReentrantLock animationLock = new ReentrantLock();

    protected void startMoveAnimation(Tile srcTile, Tile dstTile, OnAnimationFinished onAnimationFinished){
        try{
            //this can be used by thread different from the game loop thread when doing animation we need to lock it
            animationLock.lock();
            //set do animation to true
            isDoingAnimation = true;
            this.srcTile = srcTile;
            this.dstTile = dstTile;
            this.onAnimationFinished = onAnimationFinished;
        }finally {
            animationLock.unlock();
        }
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
