package com.dy.app.gameplay.piece;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;
import com.dy.app.graphic.Skin;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.manager.AssetManger;
import com.dy.app.utils.DyConst;

import java.io.IOException;
import java.util.Vector;

public class Pawn extends Piece{

    public Pawn(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor, Board board){
        super(tile, obj, onPlayerSide, pieceColor, board);
    }

    @Override
    public Tile move(Vec2i pos){

        if(Math.abs(pos.y - tile.pos.y) == 2) {
            currentState.justAdvancedTwoTiles = true;
        }

        currentState.hasMoved = true;

        if(isAnEnPassantMove(pos)){
            captureEnPassant(pos);
        }

        return super.move(pos);
    }

    @Override
    public void pseudoMove(Vec2i pos) {
        if(Math.abs(pos.y - tile.pos.y) == 2) {
            currentState.justAdvancedTwoTiles = true;
        }

        currentState.hasMoved = true;

        if(isAnEnPassantMove(pos)){
            pseudoEnPassant(pos);
        }
        super.pseudoMove(pos);
    }

    private void pseudoEnPassant(Vec2i pos) {
        //get captured piece
        Vec2i capturedPos = new Vec2i(pos.x, this.tile.pos.y);
        pseudoCapture(board.getTile(capturedPos).getPiece());
    }

    private void captureEnPassant(Vec2i pos) {
        //get captured piece
        Vec2i capturedPos = new Vec2i(pos.x, this.tile.pos.y);
        capture(board.getTile(capturedPos).getPiece());
    }

    private boolean isAnEnPassantMove(Vec2i pos){
        Tile dstTile = board.getTile(pos);

        if(pos.x == tile.pos.x) {
            //evaluate if the pawn is moving diagonally
            return false;
        }

        return !dstTile.hasPiece();
    }

    @Override
    public void updatePieceState() {
        currentState.justAdvancedTwoTiles = false;
        super.updatePieceState();
    }

    @Override
    public void updatePossibleMoves(){
        super.updatePossibleMoves();
       if(isWhite()){
           getPossibleMovesWhite();
       }else{
           getPossibleMovesBlack();
       }

       //add any possible existing en passant moves
       possibleMoves.addAll(getPossibleEnPassantMoves());
    }

    public Vector<Tile> getPossibleEnPassantMoves(){
        Vector<Tile> possibleEnPassantMoves = new Vector<Tile>();
        Vec2i pos = tile.pos;
        Tile currentEvaluatedTile = null;
        Tile enpassantTile = null;

        //if it's white then it should be on the 5th rank
        if(isWhite() && pos.y == 4){
            //skip this if x is 0
            if(pos.x != 0){
                currentEvaluatedTile = board.getTile(new Vec2i(pos.x - 1, pos.y));

                if(canBeCapturedEnPassant(currentEvaluatedTile)){
                    enpassantTile = board.getTile(new Vec2i(pos.x - 1, pos.y + 1));
                    possibleEnPassantMoves.add(enpassantTile);
                }
            }

            //skip this if x is 7
            if(pos.x != 7){
                currentEvaluatedTile = board.getTile(new Vec2i(pos.x + 1, pos.y));

                if(canBeCapturedEnPassant(currentEvaluatedTile)){
                    enpassantTile = board.getTile(new Vec2i(pos.x + 1, pos.y + 1));
                    possibleEnPassantMoves.add(enpassantTile);
                }
            }
        }else if(!isWhite() && pos.y == 3){
            //skip this if x is 0
            if(pos.x != 0){
                currentEvaluatedTile = board.getTile(new Vec2i(pos.x - 1, pos.y));

                if(canBeCapturedEnPassant(currentEvaluatedTile)){
                    enpassantTile = board.getTile(new Vec2i(pos.x - 1, pos.y - 1));
                    possibleEnPassantMoves.add(enpassantTile);
                }
            }

            //skip this if x is 7
            if(pos.x != 7){
                currentEvaluatedTile = board.getTile(new Vec2i(pos.x + 1, pos.y));

                if(canBeCapturedEnPassant(currentEvaluatedTile)){
                    enpassantTile = board.getTile(new Vec2i(pos.x + 1, pos.y - 1));
                    possibleEnPassantMoves.add(enpassantTile);
                }
            }
        }

        return possibleEnPassantMoves;
    }

    private boolean canBeCapturedEnPassant(Tile tile){
        //same code above but use if-else
        if(!tile.hasPiece()){
            //evaluate if the tile has a piece
            return false;
        }else if(!tile.getPiece().getNotation().equals(ChessNotation.PAWN)){
            //evaluate if the piece is a pawn
            return false;
        }else if(tile.getPiece().isTheSameColor(this)){
            //evaluate if the piece has different color
            return false;
        }else if(!((Pawn)tile.getPiece()).justAdvancedTwoTiles()){
            //evaluate if the piece has just advanced two tiles
            return false;
        }

        return true;
    }

    @Override
    public Vector<Tile> getControlledTiles() {
        Vector<Tile> controlledTiles = new Vector<Tile>();
        Vec2i pos = tile.pos;

        if(!isWhite()){
            if(pos.x != 0 && pos.y != 0){
                controlledTiles.add(board.getTile(new Vec2i(pos.x - 1, pos.y - 1)));
            }
            if(pos.x != 7 && pos.y != 0){
                controlledTiles.add(board.getTile(new Vec2i(pos.x + 1, pos.y - 1)));
            }
        }else{
            if(pos.x != 0 && pos.y != 7){
                controlledTiles.add(board.getTile(new Vec2i(pos.x - 1, pos.y + 1)));
            }
            if(pos.x != 7 && pos.y != 7){
                controlledTiles.add(board.getTile(new Vec2i(pos.x + 1, pos.y + 1)));
            }
        }

        return controlledTiles;
    }

    private void getPossibleMovesBlack(){
        Vec2i pos = tile.pos;
        Tile tile = null;

        //check if pawn is on the edge of the board
        if(pos.y<1){
            return;
        }

        //forward
        //check if first step is possible
        tile = board.getTile(new Vec2i(pos.x, pos.y - 1));
        if(!tile.hasPiece()){
            possibleMoves.add(tile);
            if(!currentState.hasMoved){
                tile = board.getTile(new Vec2i(pos.x, pos.y - 2));
                if(!tile.hasPiece()){
                    possibleMoves.add(tile);
                }
            }
        }

        //possible capturing moves
        if(pos.x != 0){
            tile = board.getTile(new Vec2i(pos.x - 1, pos.y - 1));
            if(tile.hasPiece() && !tile.getPiece().isTheSameColor(this)){
                possibleMoves.add(tile);
            }
        }

        if(pos.x != 7){
            tile = board.getTile(new Vec2i(pos.x + 1, pos.y - 1));
            if(tile.hasPiece() && !tile.getPiece().isTheSameColor(this)){
                possibleMoves.add(tile);
            }
        }

    }

    private void getPossibleMovesWhite(){
        Vec2i pos = tile.pos;
        Tile tile = null;

        if(pos.y>6){
            return;
        }

        //forward
        //check if first step is possible
        tile = board.getTile(new Vec2i(pos.x, pos.y + 1));
        if(!tile.hasPiece()){
            possibleMoves.add(tile);
            if(!currentState.hasMoved){
                tile = board.getTile(new Vec2i(pos.x, pos.y + 2));
                if(!tile.hasPiece()){
                    possibleMoves.add(tile);
                }
            }
        }

        //possible capturing moves
        if(pos.x != 0){
            tile = board.getTile(new Vec2i(pos.x - 1, pos.y + 1));
            if(tile.hasPiece() && !tile.getPiece().isTheSameColor(this)){
                possibleMoves.add(tile);
            }
        }

        if(pos.x != 7){
            tile = board.getTile(new Vec2i(pos.x + 1, pos.y + 1));
            if(tile.hasPiece() && !tile.getPiece().isTheSameColor(this)){
                possibleMoves.add(tile);
            }
        }
    }

    public void pseudoPromote(String piecePromotionNotation) throws IOException {
        Piece piece = null;

        switch (piecePromotionNotation){
            case ChessNotation.QUEEN:
                piece = new Queen(tile, obj, isOnPlayerSide(), pieceColor, board);
                tile.setPiece(piece);
                break;
            case ChessNotation.ROOK:
                piece = new Rook(tile, obj, isOnPlayerSide(), pieceColor, board);
                tile.setPiece(piece);
                break;
            case ChessNotation.BISHOP:
                piece = new Bishop(tile, obj, isOnPlayerSide(), pieceColor, board);
                tile.setPiece(piece);
                break;
            case ChessNotation.KNIGHT:
                piece = new Knight(tile, obj, isOnPlayerSide(), pieceColor, board);
                tile.setPiece(piece);
                break;
            default:
                throw new RuntimeException("Invalid piece promotion notation");
        }

        board.pseudoRemove(this);
        board.pseudoAdd(piece);
    }

    public void promote(String piecePromotionNotation) throws IOException {
        Piece piece = null;
        Skin skin = null;

        if(isOnPlayerSide()){
            skin = board.getAssetManger().getSkin(AssetManger.SkinType.PLAYER);
        }else{
            skin = board.getAssetManger().getSkin(AssetManger.SkinType.RIVAL);
        }

        switch (piecePromotionNotation){
            case ChessNotation.QUEEN:
                piece = board.getPieceManager().loadSinglePiece(board, isOnPlayerSide(),skin ,DyConst.queen, tile.pos, pieceColor);
                currentState.promotingNotation = ChessNotation.QUEEN;
                break;
            case ChessNotation.ROOK:
                piece = board.getPieceManager().loadSinglePiece(board, isOnPlayerSide(),skin ,DyConst.rook, tile.pos, pieceColor);
                currentState.promotingNotation = ChessNotation.ROOK;
                break;
            case ChessNotation.BISHOP:
                piece = board.getPieceManager().loadSinglePiece(board, isOnPlayerSide(),skin ,DyConst.bishop, tile.pos, pieceColor);
                currentState.promotingNotation = ChessNotation.BISHOP;
                break;
            case ChessNotation.KNIGHT:
                piece = board.getPieceManager().loadSinglePiece(board, isOnPlayerSide(),skin ,DyConst.knight, tile.pos, pieceColor);
                currentState.promotingNotation = ChessNotation.KNIGHT;
                break;
            default:
                throw new RuntimeException("Invalid piece promotion notation");
        }

        //set the state of the piece
        currentState.isPromoted = true;
        piece.currentState = currentState;
        board.removePiece(this);
        board.addPiece(piece);
    }

    @Override
    public String getNotation(){
        return ChessNotation.PAWN;
    }

    public boolean justAdvancedTwoTiles(){
        return currentState.justAdvancedTwoTiles;
    }
}
