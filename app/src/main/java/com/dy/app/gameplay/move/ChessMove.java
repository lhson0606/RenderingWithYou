package com.dy.app.gameplay.move;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;
import com.dy.app.gameplay.piece.King;
import com.dy.app.gameplay.piece.Piece;

import java.util.Vector;

public class ChessMove {
    private String moveNotation;
    private final Board board;
    private Vec2i srcTilePos;
    private Vec2i desTilePos;
    private boolean isWhiteMove;
    private boolean isPromotionMove = false;
    private String promotingPieceNotation;

    public boolean isPromotionMove(){
        return isPromotionMove;
    }

    public ChessMove(boolean isWhiteMove, String move, Board board) throws Exception {
        this.moveNotation = move.trim();
        this.board = board;
        this.isWhiteMove = isWhiteMove;
        interpretMove();
    }

    private void interpretMove() throws Exception {
        if(isWhiteMove){
            interpretWhiteMove();
        }else{
            interpretBlackMove();
        }
    }

    private boolean isSpecialMove(){
        return moveNotation.contains("O-O") || moveNotation.contains("=");
    }

    private Piece getWhitePieceNotation() throws Exception{
        Piece result = null;

        if(isSpecialMove()){
            King king = (King) board.getKing(true);
            result = king;

            if(moveNotation.contains("O-O-O")){
                desTilePos = king.getLongCastlingPos();
                return result;
            }else if(moveNotation.contains("O-O")){
                desTilePos = king.getShortCastlingPos();
                return result;
            }else if(moveNotation.contains("=")){
                isPromotionMove = true;
                //our promoting code is one char after the "="
                promotingPieceNotation = moveNotation.substring(moveNotation.indexOf("=") + 1, moveNotation.indexOf("=") + 2);
                switch (promotingPieceNotation){
                    case ChessNotation.QUEEN:
                    case ChessNotation.ROOK:
                    case ChessNotation.BISHOP:
                    case ChessNotation.KNIGHT:
                        break;
                    default:
                        throw new Exception("Invalid move");
                }
            }else{
                throw new Exception("Invalid move");
            }
        }

        for(int i = 0; i<4; i++){
            String notation = null;
            String desNotation = null;

            try{
                notation = moveNotation.substring(0, i);
                //dest notation is the next 2 chars
                desNotation = moveNotation.substring(i, i+2);
            }catch (StringIndexOutOfBoundsException e) {
                continue;
            }

            if(desNotation.charAt(0) - 'a' < 0 || desNotation.charAt(0) - 'a' > 7) continue;
            if(desNotation.charAt(1) - '1' < 0 || desNotation.charAt(1) - '1' > 7) continue;

            desTilePos = new Vec2i(7 - (desNotation.charAt(0) - 'a'), desNotation.charAt(1) - '1');
            Vector<Piece> pieces = board.getWhitePiecesByNotation(notation, board.getTile(desTilePos));

            if(pieces.size() == 1){
                result = pieces.get(0);
                return result;
            }
        }

        throw new Exception("Invalid move");
    }

    private Piece getBlackPieceNotation() throws Exception{
        Piece result = null;

        if(isSpecialMove()){
            King king = (King) board.getKing(false);
            result = king;

            if(moveNotation.contains("O-O-O")){
                desTilePos = king.getLongCastlingPos();
                return result;
            }else if(moveNotation.contains("O-O")){
                desTilePos = king.getShortCastlingPos();
                return result;
            }else if(moveNotation.contains("=")){
                isPromotionMove = true;
                //our promoting code is one char after the "="
                promotingPieceNotation = moveNotation.substring(moveNotation.indexOf("=") + 1, moveNotation.indexOf("=") + 2);
                switch (promotingPieceNotation){
                    case ChessNotation.QUEEN:
                    case ChessNotation.ROOK:
                    case ChessNotation.BISHOP:
                    case ChessNotation.KNIGHT:
                        break;
                    default:
                        throw new Exception("Invalid move");
                }
            }else{
                throw new Exception("Invalid move");
            }
        }

        for(int i = 0; i<3 ;i++){
            String notation = null;
            String desNotation = null;

            notation = moveNotation.substring(0, i);
            //dest notation is the next 2 chars
            try{
                desNotation = moveNotation.substring(i, i+2);
            }catch (StringIndexOutOfBoundsException e) {
                continue;
            }

            if(desNotation.charAt(0) - 'a' < 0 || desNotation.charAt(0) - 'a' > 7) continue;
            if(desNotation.charAt(1) - '1' < 0 || desNotation.charAt(1) - '1' > 7) continue;

            desTilePos = new Vec2i(7 - (desNotation.charAt(0) - 'a'), desNotation.charAt(1) - '1');
            Vector<Piece> pieces = board.getBlackPiecesByNotation(notation, board.getTile(desTilePos));

            if(pieces.size() == 1){
                result = pieces.get(0);
                return result;
            }
        }

        throw new Exception("Invalid move");
    }

    private void interpretWhiteMove() throws Exception{
        moveNotation = moveNotation.replace("x", "");
        srcTilePos = getWhitePieceNotation().getTile().pos;
    }

    private void interpretBlackMove() throws Exception{
        moveNotation = moveNotation.replace("x", "");
        srcTilePos = getBlackPieceNotation().getTile().pos;
    }

    public Tile getSrcTile(){
        return board.getTile(srcTilePos);
    }

    public Tile getDesTile(){
        return board.getTile(desTilePos);
    }

    public String getPromotingPieceNotation(){
        return promotingPieceNotation;
    }
}
