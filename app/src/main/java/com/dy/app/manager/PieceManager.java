package com.dy.app.manager;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.core.GameCore;
import com.dy.app.gameplay.Player;
import com.dy.app.gameplay.Rival;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.piece.Pawn;
import com.dy.app.gameplay.piece.Piece;
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
    
    public static PieceManager getInstance(){
        return instance = (instance == null) ? new PieceManager() : instance;
    }

    private static PieceManager instance = null;

    private void init() throws IOException {
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
            EntityManger.getInstance().newEntity(piece);
        }

        for(Piece piece : enemy_pieces){
            EntityManger.getInstance().newEntity(piece);
        }
    }

    private Vector<Piece> loadBlackPieces(boolean onPlayerSide, Skin skin) throws IOException {
        Vector<Piece> pieces = new Vector<>();
        Board board = Board.getInstance();
        String verCode  = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.piece_ver_glsl_path));
        String fragCode = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.piece_frag_glsl_path));
        //8 pawns
        for(int i = 0; i<8; i++){
            Tile tile = board.getTile(new Vec2i(i, 6));
            Obj3D obj = ObjManager.getInstance().getObj(DyConst.pawn);
            obj.setModelMat(tile.getObj().getModelMat().clone());
            obj.setTex(skin.getTexture());
            obj.setMaterial(skin.getMaterial());

            Pawn pawn = new Pawn(
                    tile,
                    obj,
                    onPlayerSide
            );

            tile.setPiece(pawn);
            PieceShader shader = new PieceShader(verCode, fragCode);
            shader.setPiece(pawn);
            pawn.getObj().setShader(shader);
            pieces.add(pawn);

        }

        return pieces;
    }

    private Vector<Piece> loadWhitePieces(boolean onPlayerSide, Skin skin) throws IOException {
        Vector<Piece> pieces = new Vector<>();
        Board board = Board.getInstance();
        String verCode  = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.piece_ver_glsl_path));
        String fragCode = ShaderHelper.getInstance().readShader(GameCore.getInstance().getGameActivity().getAssets().open(DyConst.piece_frag_glsl_path));
        //8 pawns
        for(int i = 0; i<8; i++){
            Tile tile = board.getTile(new Vec2i(i, 1));
            Obj3D obj = ObjManager.getInstance().getObj(DyConst.pawn);
            obj.setModelMat(tile.getObj().getModelMat().clone());
            obj.setTex(skin.getTexture());
            obj.setMaterial(skin.getMaterial());

            Pawn pawn = new Pawn(
                    tile,
                    obj,
                    onPlayerSide
            );

            tile.setPiece(pawn);
            PieceShader shader = new PieceShader(verCode, fragCode);
            shader.setPiece(pawn);
            pawn.getObj().setShader(shader);
            pieces.add(pawn);

        }

        return pieces;
    }

    private PieceManager(){
        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
