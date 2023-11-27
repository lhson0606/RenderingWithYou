package com.dy.app.gameplay.piece;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;
import com.dy.app.graphic.model.Obj3D;

import java.util.Vector;

public class King extends Piece{

    public King(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor, Board board){
        super(tile, obj, onPlayerSide, pieceColor, board);
    }

    @Override
    public Tile move(Vec2i pos) {
        if(!currentState.hasMoved){
            if(isCastlingMove(pos)){
                return performCastle(pos);
            }
        }
        currentState.hasMoved = true;
        return super.move(pos);
    }

    @Override
    public void pseudoMove(Vec2i pos) {
        if(!currentState.hasMoved){
            if(isCastlingMove(pos)){
                pseudoCastle(pos);
            }
        }
        currentState.hasMoved = true;
        super.pseudoMove(pos);
    }

    private void pseudoCastle(Vec2i pos) {
        //first moved the rook then the king
        //check if the move is long castling or short castling
        Tile rookTile = board.getTile(getCastlingRookPos(pos));
        Rook rook = (Rook) rookTile.getPiece();
        rook.pseudoMove(getRookPosAfterCastling(pos));
        super.pseudoMove(pos);
    }

    public Tile performCastle(Vec2i pos) {
        //first moved the rook then the king
        //check if the move is long castling or short castling
        Tile rookTile = board.getTile(getCastlingRookPos(pos));
        Rook rook = (Rook) rookTile.getPiece();
        rook.move(getRookPosAfterCastling(pos));
        return super.move(pos);
    }

    //check if the move is castling move
    private boolean isCastlingMove(Vec2i pos) {
        //there are two squares that the king can castle to
        //assumes that all conditions are met!
        Vec2i longCastlingPos = getLongCastlingPos();
        Vec2i shortCastlingPos = getShortCastlingPos();

        return pos.isEqual(longCastlingPos) || pos.isEqual(shortCastlingPos);
    }

    @Override
    public void updatePossibleMoves(){
        super.updatePossibleMoves();
        Vec2i pos = tile.pos;

        Vector<Tile> enemyControlledTiles = board.getPieceDifferentColorControlledTile(isWhite());

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if(pos.x + i < 0 || pos.x + i > 7 || pos.y + j < 0 || pos.y + j > 7) continue;
                Tile tile = board.getTile(new Vec2i(pos.x + i, pos.y + j));
                if(tile.hasPiece()){
                    if(!tile.getPiece().isTheSameColor(this))
                        possibleMoves.add(tile);
                    continue;
                }

                if(!enemyControlledTiles.contains(tile)){
                    possibleMoves.add(tile);
                }
            }
        }

        if(!currentState.hasMoved){
            possibleMoves.addAll(getPossibleCastlingSquare());
        }
    }

    private Vector<Tile> getPossibleCastlingSquare(){
        Vector<Tile> result = new Vector<Tile>();
        Vec2i longCastlingPos = getLongCastlingPos();
        Vec2i shortCastlingPos = getShortCastlingPos();

        if(isCastlingPossible(longCastlingPos)){
            result.add(board.getTile(longCastlingPos));
        }

        if(isCastlingPossible(shortCastlingPos)){
            result.add(board.getTile(shortCastlingPos));
        }

//        if(result.size() == 0) {
//            //for debugging
//            //board.unhighlightAllTiles();
//            for(Tile t: board.getPieceDifferentColorControlledTile(isWhite())){
//                if(isWhite()){
//                    //t.getObj().changeState(Obj3D.State.ENDANGERED);
//                }
//                else{
//                    t.getObj().changeState(Obj3D.State.SOURCE);
//                }
//            }
//        }

        return result;
    }

    private boolean isCastlingPossible(Vec2i dstPos){
        if(!dstPos.isEqual(getShortCastlingPos()) && !dstPos.isEqual(getLongCastlingPos())) {
            throw new RuntimeException("Invalid castling move");
        }
        //+ king has not moved
        if(currentState.hasMoved) return false;
        //+ rook has not moved
        Vec2i rookPos = getCastlingRookPos(dstPos);
        Piece currentPiece = board.getTile(getCastlingRookPos(dstPos)).getPiece();
        if(currentPiece == null) return false;
        if(!currentPiece.getNotation().equals(ChessNotation.ROOK)) {
            return false;
        }
        Rook rook = (Rook) currentPiece;
        if(rook.hasMoved()) return false;
        //+ no pieces between king and rook, king does not end up in check
        if(!canPassThrough(rookPos)) {
            return false;
        }
        //+ king is not in check
        if(isInCheck()) return false;
        return true;
    }

    private boolean isInCheck(){
        return board.getPieceDifferentColorControlledTile(this.isWhite()).contains(tile);
    }

    @Override
    public String getNotation(){
        return ChessNotation.KING;
    }

    public Vec2i getLongCastlingPos(){
        return new Vec2i(5, tile.pos.y);
    }

    public Vec2i getShortCastlingPos(){
        return new Vec2i(1, tile.pos.y);
    }

    public Vec2i getCastlingRookPos(Vec2i castlingPos){
        if(castlingPos.isEqual(getLongCastlingPos())) {
            return new Vec2i(7, tile.pos.y);
        }else if(castlingPos.isEqual(getShortCastlingPos())){
            return new Vec2i(0, tile.pos.y);
        }else{
            throw new RuntimeException("Invalid argument");
        }
    }

    public Vec2i getRookPosAfterCastling(Vec2i castlingPos){
        if(castlingPos.isEqual(getLongCastlingPos())) {
            return new Vec2i(4, tile.pos.y);
        }else if(castlingPos.isEqual(getShortCastlingPos())){
            return new Vec2i(2, tile.pos.y);
        }else{
            throw new RuntimeException("Invalid argument");
        }
    }

    private boolean canPassThrough(Vec2i dstPos){
        Vector<Tile> enemiesControlledTiles = board.getPieceDifferentColorControlledTile(isWhite());

        if(tile.pos.x > dstPos.x) {
            for (int i = tile.pos.x - 1; i > dstPos.x; i--) {
                //board.unhighlightAllTiles();
                Tile currentTile = board.getTile(new Vec2i(i, tile.pos.y));
                if(currentTile.hasPiece()){
                    return false;
                }

                if(enemiesControlledTiles.contains(currentTile)){
                    return false;
                }
            }
        }
        else {
            for (int i = tile.pos.x + 1; i < dstPos.x; i++) {
                //board.unhighlightAllTiles();
                Tile currentTile = board.getTile(new Vec2i(i, tile.pos.y));
                if(currentTile.hasPiece()){
                    return false;
                }

                if(enemiesControlledTiles.contains(currentTile)){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Vector<Tile> getControlledTiles() {
        synchronized (this){
            Vector<Tile> controlledTiles = new Vector<Tile>();
            Vec2i pos = tile.pos;

            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2; j++){
                    if(pos.x + i < 0 || pos.x + i > 7 || pos.y + j < 0 || pos.y + j > 7) continue;
                    if(i == 0 && j == 0) continue;
                    Tile tile = board.getTile(new Vec2i(pos.x + i, pos.y + j));
                    controlledTiles.add(tile);
                }
            }

            return controlledTiles;
        }
    }
}
