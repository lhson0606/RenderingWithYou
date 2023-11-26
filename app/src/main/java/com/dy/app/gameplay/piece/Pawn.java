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
    boolean hasMoved = false;

    public Pawn(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor, Board board){
        super(tile, obj, onPlayerSide, pieceColor, board);
    }

    @Override
    public Tile move(Vec2i pos){
        hasMoved = true;
        return super.move(pos);
    }

    @Override
    public void updatePossibleMoves(){
        super.updatePossibleMoves();
       if(isWhite()){
           getPossibleMovesWhite();
       }else{
           getPossibleMovesBlack();
       }

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
}
