package com.dy.app.manager;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.core.GameCore;
import com.dy.app.gameplay.Player;
import com.dy.app.gameplay.Rival;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.piece.Bishop;
import com.dy.app.gameplay.piece.King;
import com.dy.app.gameplay.piece.Knight;
import com.dy.app.gameplay.piece.Pawn;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.gameplay.piece.Queen;
import com.dy.app.gameplay.piece.Rook;
import com.dy.app.graphic.Skin;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.shader.PieceShader;
import com.dy.app.graphic.shader.ShaderHelper;
import com.dy.app.utils.DyConst;

import java.io.IOException;
import java.util.Vector;

public class PieceManager {
    private Vector<Piece> player_pieces;
    private Vector<Piece> enemy_pieces ;
    
    public static synchronized PieceManager getInstance(){
        return instance = (instance == null) ? new PieceManager() : instance;
    }

    private static PieceManager instance = null;

    public void init() throws IOException {
        Player player = Player.getInstance();
        Rival rival = Rival.getInstance();
        Skin playerSkin = AssetManger.getInstance().getSkin(AssetManger.SkinType.PLAYER);
        Skin rivalSkin = AssetManger.getInstance().getSkin(AssetManger.SkinType.RIVAL);
        boolean playerIsWhite = Player.getInstance().isWhitePiece();


        if(playerIsWhite) {
            player_pieces = loadWhitePieces(true, playerSkin);
            enemy_pieces = loadBlackPieces(false, rivalSkin);
        }else{
            enemy_pieces = loadWhitePieces(false, rivalSkin);
            player_pieces = loadBlackPieces(true, playerSkin);
        }

        for(Piece piece : player_pieces){
            piece.update(0f);
            EntityManger.getInstance().newEntity(piece);
        }

        for(Piece piece : enemy_pieces){
            piece.update(0f);
            EntityManger.getInstance().newEntity(piece);
        }
    }

    private Piece loadSinglePiece(Board board, boolean onPlayerSide, Skin skin, String pieceName, Vec2i pos, Piece.PieceColor pieceColor) throws IOException {
        Tile tile = board.getTile(pos);
        Obj3D obj = ObjManager.getInstance().getObj(pieceName);
        obj.setModelMat(tile.getObj().getModelMat().clone());
        obj.setTex(skin.getTexture());
        obj.setMaterial(skin.getMaterial());

        Piece piece = null;

        switch (pieceName){
            case DyConst.pawn:
                piece = new Pawn(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor
                );
                break;
            case DyConst.rook:
                piece = new Rook(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor
                );
                break;
            case DyConst.knight:
                piece = new Knight(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor
                );
                break;
            case DyConst.bishop:
                piece = new Bishop(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor
                );
                break;
            case DyConst.queen:
                piece = new Queen(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor
                );
                break;
            case DyConst.king:
                piece = new King(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor
                );
                break;
        }


        tile.setPiece(piece);
        PieceShader shader = new PieceShader(
                ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.piece_ver_glsl_path)),
                ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.piece_frag_glsl_path))
        );
        shader.setPiece(piece);
        piece.getObj().setShader(shader);
        return piece;
    }

    private Vector<Piece> loadBlackPieces(boolean onPlayerSide, Skin skin) throws IOException {
        Vector<Piece> pieces = new Vector<>();
        Board board = Board.getInstance();

        //8 pawns
        for(int i = 0; i<8; i++){
            pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.pawn, new Vec2i(i, 6), Piece.PieceColor.BLACK));
        }

        //2 rooks
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.rook, new Vec2i(0, 7), Piece.PieceColor.BLACK));
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.rook, new Vec2i(7, 7), Piece.PieceColor.BLACK));

        //2 knights
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.knight, new Vec2i(1, 7), Piece.PieceColor.BLACK));
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.knight, new Vec2i(6, 7), Piece.PieceColor.BLACK));

        //2 bishops
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.bishop, new Vec2i(2, 7), Piece.PieceColor.BLACK));
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.bishop, new Vec2i(5, 7), Piece.PieceColor.BLACK));

        //1 queen
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.queen, new Vec2i(3, 7), Piece.PieceColor.BLACK));

        //1 king
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.king, new Vec2i(4, 7), Piece.PieceColor.BLACK));

        return pieces;
    }

    private Vector<Piece> loadWhitePieces(boolean onPlayerSide, Skin skin) throws IOException {
        Vector<Piece> pieces = new Vector<>();
        Board board = Board.getInstance();
        String verCode  = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.piece_ver_glsl_path));
        String fragCode = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.piece_frag_glsl_path));
        //8 pawns
        for(int i = 0; i<8; i++){
            pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.pawn, new Vec2i(i, 1), Piece.PieceColor.WHITE));
        }

        //2 rooks
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.rook, new Vec2i(0, 0), Piece.PieceColor.WHITE));
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.rook, new Vec2i(7, 0), Piece.PieceColor.WHITE));

        //2 knights
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.knight, new Vec2i(1, 0), Piece.PieceColor.WHITE));
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.knight, new Vec2i(6, 0), Piece.PieceColor.WHITE));

        //2 bishops
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.bishop, new Vec2i(2, 0), Piece.PieceColor.WHITE));
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.bishop, new Vec2i(5, 0), Piece.PieceColor.WHITE));

        //1 queen
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.queen, new Vec2i(3, 0), Piece.PieceColor.WHITE));

        //1 king
        pieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.king, new Vec2i(4, 0), Piece.PieceColor.WHITE));

        return pieces;
    }

    private PieceManager(){

    }

    public void removePiece(Piece piece) {
        if(piece.isOnPlayerSide()){
            player_pieces.remove(piece);
        }else{
            enemy_pieces.remove(piece);
        }

        EntityManger.getInstance().removeEntity(piece);
    }
}
