package com.dy.app.gameplay.piece;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;
import com.dy.app.graphic.model.Obj3D;

import java.util.Vector;

public class Queen extends Piece{

    public Queen(Tile tile, Obj3D obj, boolean onPlayerSide, PieceColor pieceColor, Board board){
        super(tile, obj, onPlayerSide, pieceColor, board);
    }

    @Override
    public void updatePossibleMoves(){
        super.updatePossibleMoves();
        updatePossibleMovesDiagonal();
        updatePossibleMovesStraight();
    }

    private void updatePossibleMovesStraight() {
        Vec2i pos = tile.pos;
        //x axis
        for(int i = 1; i < 8; i++){
            if(pos.x + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x + i, pos.y));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        for(int i = 1; i < 8; i++){
            if(pos.x - i < 0) break;
            Tile tile = board.getTile(new Vec2i(pos.x - i, pos.y));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        //y axis
        for(int i = 1; i < 8; i++){
            if(pos.y + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x, pos.y + i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        for(int i = 1; i < 8; i++){
            if(pos.y - i <0) break;
            Tile tile = board.getTile(new Vec2i(pos.x, pos.y - i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }
    }

    private void updatePossibleMovesDiagonal() {
        Vec2i pos = tile.pos;

        //main diagonal
        for(int i = 1; i < 8; i++){
            if(pos.x + i > 7 || pos.y + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x + i, pos.y + i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        for(int i = 1; i<8; i++){
            if(pos.x - i < 0 || pos.y - i < 0) break;
            Tile tile = board.getTile(new Vec2i(pos.x - i, pos.y - i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        //anti diagonal
        for(int i = 1; i < 8; i++){
            if(pos.x + i > 7 || pos.y - i < 0) break;
            Tile tile = board.getTile(new Vec2i(pos.x + i, pos.y - i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }

        for(int i = 1; i < 8; i++){
            if(pos.x - i < 0 || pos.y + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x - i, pos.y + i));
            if(tile.hasPiece()){
                if(!tile.getPiece().isTheSameColor(this))
                    possibleMoves.add(tile);
                break;
            }
            possibleMoves.add(tile);
        }
    }

    @Override
    public String getNotation(){
        return ChessNotation.QUEEN;
    }

    private Vector<Tile> getStraightControlledTiles(){
        Vector<Tile> controlledTiles = new Vector<Tile>();
        Vec2i pos = tile.pos;

        //x axis
        for(int i = 1; i < 8; i++){
            if(pos.x + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x + i, pos.y));
            controlledTiles.add(tile);
            if(tile.hasPiece()){
                controlledTiles.add(tile);
                break;
            }
        }

        for(int i = 1; i < 8; i++){
            if(pos.x - i < 0) break;
            Tile tile = board.getTile(new Vec2i(pos.x - i, pos.y));
            controlledTiles.add(tile);
            if(tile.hasPiece()){
                controlledTiles.add(tile);
                break;
            }
        }

        //y axis
        for(int i = 1; i < 8; i++){
            if(pos.y + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x, pos.y + i));
            controlledTiles.add(tile);
            if(tile.hasPiece()){
                controlledTiles.add(tile);
                break;
            }
        }

        for(int i = 1; i < 8; i++){
            if(pos.y - i <0) break;
            Tile tile = board.getTile(new Vec2i(pos.x, pos.y - i));
            controlledTiles.add(tile);
            if(tile.hasPiece()){
                controlledTiles.add(tile);
                break;
            }
        }

        return controlledTiles;
    }

    private Vector<Tile> getDiagonalControlledTiles(){
        Vector<Tile> controlledTiles = new Vector<Tile>();
        Vec2i pos = tile.pos;

        //main diagonal
        for(int i = 1; i < 8; i++){
            if(pos.x + i > 7 || pos.y + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x + i, pos.y + i));
            controlledTiles.add(tile);
            if(tile.hasPiece()){
                controlledTiles.add(tile);
                break;
            }
        }

        for(int i = 1; i<8; i++){
            if(pos.x - i < 0 || pos.y - i < 0) break;
            Tile tile = board.getTile(new Vec2i(pos.x - i, pos.y - i));
            controlledTiles.add(tile);
            if(tile.hasPiece()){
                controlledTiles.add(tile);
                break;
            }
        }

        //anti diagonal
        for(int i = 1; i < 8; i++){
            if(pos.x + i > 7 || pos.y - i < 0) break;
            Tile tile = board.getTile(new Vec2i(pos.x + i, pos.y - i));
            controlledTiles.add(tile);
            if(tile.hasPiece()){
                controlledTiles.add(tile);
                break;
            }
        }

        for(int i = 1; i < 8; i++){
            if(pos.x - i < 0 || pos.y + i > 7) break;
            Tile tile = board.getTile(new Vec2i(pos.x - i, pos.y + i));
            controlledTiles.add(tile);
            if(tile.hasPiece()){
                controlledTiles.add(tile);
                break;
            }
        }

        return controlledTiles;
    }

    @Override
    public Vector<Tile> getControlledTiles() {
        Vector<Tile> controlledTiles = new Vector<Tile>();
        controlledTiles.addAll(getDiagonalControlledTiles());
        controlledTiles.addAll(getStraightControlledTiles());
        return controlledTiles;
    }
}
