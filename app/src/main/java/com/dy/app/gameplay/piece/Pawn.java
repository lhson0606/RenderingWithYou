package com.dy.app.gameplay.piece;

import android.util.Log;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.notation.ChessNotation;
import com.dy.app.gameplay.player.Player;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.manager.AssetManger;
import com.dy.app.utils.DyConst;

import java.io.IOException;
import java.util.Vector;

public class Pawn extends Piece{
    private boolean hasMoved = false;
    private boolean justAdvancedTwoTiles = false;

    public Pawn(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor, Board board){
        super(tile, obj, onPlayerSide, pieceColor, board);
    }

    @Override
    public Tile move(Vec2i pos){
        if(Math.abs(pos.y - tile.pos.y) == 2) {
            justAdvancedTwoTiles = true;
        }

        hasMoved = true;

        if(isAnEnPassantMove(pos)){
            captureEnPassant(pos);
        }

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

        startMoveAnimation(oldTile, newTile);
        return tile;
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

    @Override
    public void resetPieceState() {
        justAdvancedTwoTiles = false;
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
            if(!hasMoved){
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
            if(!hasMoved){
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

    public void promote(String piecePromotionNotation) throws IOException {
        Piece piece = null;
        switch (piecePromotionNotation){
            case ChessNotation.QUEEN:
                piece = board.getPieceManager().loadSinglePiece(board, isOnPlayerSide(),board.getAssetManger().getSkin(AssetManger.SkinType.PLAYER),DyConst.queen, tile.pos, pieceColor);
                break;
            case ChessNotation.ROOK:
                piece = board.getPieceManager().loadSinglePiece(board, isOnPlayerSide(),board.getAssetManger().getSkin(AssetManger.SkinType.PLAYER),DyConst.rook, tile.pos, pieceColor);
                break;
            case ChessNotation.BISHOP:
                piece = board.getPieceManager().loadSinglePiece(board, isOnPlayerSide(),board.getAssetManger().getSkin(AssetManger.SkinType.PLAYER),DyConst.bishop, tile.pos, pieceColor);
                break;
            case ChessNotation.KNIGHT:
                piece = board.getPieceManager().loadSinglePiece(board, isOnPlayerSide(),board.getAssetManger().getSkin(AssetManger.SkinType.PLAYER),DyConst.knight, tile.pos, pieceColor);
                break;
            default:
                throw new RuntimeException("Invalid piece promotion notation");
        }

        board.removePiece(this);
        board.addPiece(piece);
    }

    @Override
    public String getNotation(){
        return ChessNotation.PAWN;
    }

    public boolean justAdvancedTwoTiles(){
        return justAdvancedTwoTiles;
    }
}
