package com.dy.app.gameplay.algebraicNotation;

import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.piece.King;
import com.dy.app.gameplay.piece.Piece;

import java.util.Vector;

public class AlgebraicChessInterpreter {

    public static final String LONG_CASTLE_NOTATION = "O-O-O";
    public static final String SHORT_CASTLE_NOTATION = "O-O";
    public static final String CAPTURE_NOTATION = "x";
    public static final String PROMOTION_NOTATION = "=";
    public static final String CHECK_NOTATION = "+";
    public static final String CHECKMATE_NOTATION = "#";
    public static final String WHITE_WIN_NOTATION = "1-0";
    public static final String BLACK_WIN_NOTATION = "0-1";
    public static final String DRAW_NOTATION = "1/2-1/2";
    public static final String WHITE_LOST_BY_DEFAULT_NOTATION = "-/1";
    public static final String BLACK_LOST_BY_DEFAULT_NOTATION = "1/-";
    public static final String NO_RESULT = "-/-";
    private static final int LONG_CASTLE_DX = 2;
    private static final int SHORT_CASTLE_DX = -2;
    private static final int NOT_CASTLING_MOVE = 0;
    private static int isCastlingMove(Tile srcTile, Tile dstTile){
        Piece piece = srcTile.getPiece();
        //piece is not null
        if(!piece.getNotation().equals(ChessNotation.KING)){
            return NOT_CASTLING_MOVE;
        }

        return dstTile.pos.x - srcTile.pos.x;
    }

    public static String convertToAlgebraicNotation(Board board, Tile srcTile, Tile dstTile){
        int dx = isCastlingMove(srcTile, dstTile);

        //check for special moves
        if(dx == LONG_CASTLE_DX) {
            return LONG_CASTLE_NOTATION;
        }
        else if(dx == SHORT_CASTLE_DX) {
            return SHORT_CASTLE_NOTATION;
        }

        //normal move formula: <src> (x) <dst> (=Notation) (+/#)
        //#todo for debugging code style
        final String srcNotation = getSrcNotation(board, srcTile, dstTile);
        final String captureNotation = isCaptureMove(srcTile, dstTile) ? CAPTURE_NOTATION : "";
        final String dstNotation = getDstNotation(dstTile);
        final String promotionNotation = isPromotionMove(srcTile, dstTile) ? PROMOTION_NOTATION + "?" : "";
        //#todo check notation
        final String checkNotation = "";

        return srcNotation + captureNotation + dstNotation + promotionNotation + checkNotation;
    }

    private static boolean isPromotionMove(Tile srcTile, Tile dstTile){
        Piece piece = srcTile.getPiece();
        if(!piece.getNotation().equals(ChessNotation.PAWN)){
            return false;
        }

        return dstTile.pos.y == 0 || dstTile.pos.y == 7;
    }

    private static String getDstNotation(Tile dstTile){
        return getFileNotation(dstTile.pos.x) + getRankNotation(dstTile.pos.y);
    }

    private static boolean isCaptureMove(Tile srcTile, Tile dstTile){
        //check if it's a pawn move
        Piece piece = srcTile.getPiece();

        if(piece.getNotation().equals(ChessNotation.PAWN)){
            //pawn move
            return isPawnCaptureMove(srcTile, dstTile);
        }else{
            //not a pawn move
            return dstTile.getPiece() != null;
        }
    }

    private static String getSrcNotation(Board board, Tile srcTile, Tile dstTile){

        if(!srcTile.getPiece().getNotation().equals(ChessNotation.PAWN)){
            //not a pawn move case
            Vector<Piece> queryResult = null;
            Piece piece = srcTile.getPiece();
            String pieceNotation = piece.getNotation();
            //start from the piece notation
            queryResult = board.getPieceByNotation(piece.isWhite(), pieceNotation, dstTile);

            if(queryResult.size() == 1) {
                //no ambiguity, return the piece notation
                return pieceNotation;
            }

            String fileNotation = getFileNotation(srcTile.pos.x);
            //try to resolve ambiguity by adding the file notation
            queryResult = board.getPieceByNotation(piece.isWhite(), pieceNotation + fileNotation, dstTile);

            if(queryResult.size() == 1) {
                //no ambiguity, return the piece notation
                return pieceNotation + fileNotation;
            }

            String rankNotation = getRankNotation(srcTile.pos.y);
            //try to resolve ambiguity by adding the rank notation
            queryResult = board.getPieceByNotation(piece.isWhite(), pieceNotation + rankNotation, dstTile);

            if(queryResult.size() == 1) {
                //no ambiguity, return the piece notation
                return pieceNotation + rankNotation;
            }

            //last resort, add the rank notation
            return pieceNotation + fileNotation + rankNotation;
        }else if(isPawnCaptureMove(srcTile, dstTile)){
            //pawn capture move, simply return the file notation
            return getFileNotation(srcTile.pos.x);

        }else{
            //normal pawn move, simply return the PAWN notation
            return ChessNotation.PAWN;
        }
    }

    private static boolean isPawnCaptureMove(Tile srcTile, Tile dstTile) {
        //if it moved diagonally then it's a capture move (both en passant and normal capture)
        return srcTile.pos.x != dstTile.pos.x;
    }

    private static String getFileNotation(int x){
        switch(x){
            case 0:
                return "h";
            case 1:
                return "g";
            case 2:
                return "f";
            case 3:
                return "e";
            case 4:
                return "d";
            case 5:
                return "c";
            case 6:
                return "b";
            case 7:
                return "a";
            default:
                throw new RuntimeException("Invalid file");
        }
    }

    private static String getRankNotation(int y){
        switch(y){
            case 0:
                return "1";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
            case 4:
                return "5";
            case 5:
                return "6";
            case 6:
                return "7";
            case 7:
                return "8";
            default:
                throw new RuntimeException("Invalid rank");
        }
    }
}
