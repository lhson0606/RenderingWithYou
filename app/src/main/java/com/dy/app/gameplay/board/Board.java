package com.dy.app.gameplay.board;

import android.content.Context;
import android.graphics.Shader;
import android.util.Log;

import com.airbnb.lottie.L;
import com.dy.app.common.maths.Mat4;
import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameCore;
import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.move.ChessMove;
import com.dy.app.gameplay.notation.ChessNotation;
import com.dy.app.gameplay.piece.King;
import com.dy.app.gameplay.piece.Pawn;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.shader.ShaderHelper;
import com.dy.app.graphic.shader.TileShader;
import com.dy.app.manager.AssetManger;
import com.dy.app.manager.EntityManger;
import com.dy.app.manager.ObjManager;
import com.dy.app.manager.PieceManager;
import com.dy.app.utils.DyConst;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

public class Board implements GameEntity {
    private Tile tiles[][];
    private final Context context;
    private final EntityManger entityManger;
    private final ObjManager objManager;
    private final AssetManger assetManger;
    private PieceManager pieceManager;
    private Tile prevSrcTile = null;
    private Tile prevDesTile = null;
    private final ReentrantLock mutex = new ReentrantLock();

    public Board(Context context, EntityManger entityManger, ObjManager objManager, AssetManger assetManger){
        this.context = context;
        this.entityManger = entityManger;
        this.objManager = objManager;
        this.assetManger = assetManger;
    }

    public void setPieceManager(PieceManager pieceManager){
        this.pieceManager = pieceManager;
    }

    public void load(){
        tiles = new Tile[DyConst.row_count][DyConst.col_count];
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++)
            {
                createTile(i, j);
            }

        }
        entityManger.newEntity(this);
    }

    private void createTile(int i, int j){
        tiles[i][j] = new Tile(objManager.getObj(DyConst.tile), new Vec2i(i, j), null);

        try {
            String verCode = null;
            verCode = ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.tile_ver_glsl_path));
            String fragCode = ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.tile_frag_glsl_path));
            TileShader shader = new TileShader(verCode, fragCode);
            shader.setTile(tiles[i][j]);
            tiles[i][j].getObj().setShader(shader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Tile getTile(Vec3 worldPos){
        Vec2i tilePos = Tile.getTilePos(worldPos);
        if(tilePos.x < 0 || tilePos.x >= DyConst.row_count || tilePos.y < 0 || tilePos.y >= DyConst.col_count)
            return null;
        return tiles[tilePos.x][tilePos.y];
    }


    public Tile getTile(Vec2i pos){
        return tiles[pos.x][pos.y];
    }

    private static Board instance = null;

    @Override
    public void init() {
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++){
                tiles[i][j].init(assetManger);
            }
        }
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void draw() {
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++)
                tiles[i][j].getObj().draw(Camera.getInstance().mViewMat, Camera.getInstance().mProjMat);
        }
        //tiles[0][0].getObj().draw(Camera.getInstance().mViewMat, Camera.getInstance().mProjMat);
    }

    @Override
    public void destroy() {
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++)
                tiles[i][j].getObj().destroy();
        }
    }

    public Vector<Piece> getWhitePiecesByNotation(String pieceNotation, Tile des){
        Vector<Piece> pieces = pieceManager.getWhitePieces();

        Vector<Piece> result = new Vector<>();

        if(pieceNotation.length() == 0){
            //can only be pawn
            for(Piece piece : pieces){
                if(piece.getPossibleMoves().contains(des) && piece.getNotation().equals(ChessNotation.PAWN)){
                    result.add(piece);
                }
            }
            return result;
        }else if(pieceNotation.length() == 1){
            //can be any piece name except pawn
            for(Piece piece : pieces){
                if(piece.getNotation().equals(pieceNotation) && piece.getPossibleMoves().contains(des)){
                    result.add(piece);
                }
            }

            if(result.size() == 1){
                return result;
            }

            final char file = pieceNotation.charAt(0);
            final int x = 7 - (file - 'a');
            //can be pawn
            for(Piece piece : pieces){
                if(piece.getTile().pos.x == x && piece.getPossibleMoves().contains(des) && piece.getNotation().equals(ChessNotation.PAWN)){
                    result.add(piece);
                }
            }

            if(result.size() == 1){
                return result;
            }

            //can be selected by file
            for(Piece piece : pieces){
                if(piece.getTile().pos.x == x && piece.getPossibleMoves().contains(des)){
                    result.add(piece);
                }
            }
            return result;
        } else if(pieceNotation.length() == 2){
            final String pieceName = pieceNotation.substring(0, 1);
            final char file = pieceNotation.charAt(1);
            final int x = 7 - (file - 'a');
            for(Piece piece : pieces){
                if(piece.getNotation().equals(pieceName) && piece.getTile().pos.x == x && piece.getPossibleMoves().contains(des)){
                    result.add(piece);
                }
            }
            return result;
        } else if(pieceNotation.length() == 3){
            final String pieceName = pieceNotation.substring(0, 1);
            final char file = pieceNotation.charAt(1);
            final int x = 7 - (file - 'a');
            final char rank = pieceNotation.charAt(2);
            final int y = rank - '1';
            for(Piece piece : pieces){
                if(piece.getNotation().equals(pieceName) && piece.getTile().pos.x == x && piece.getTile().pos.y == y && piece.getPossibleMoves().contains(des)){
                    result.add(piece);
                }
            }
            return result;
        }

        return result;
    }

    public Vector<Piece> getBlackPiecesByNotation(String pieceNotation, Tile des){
        Vector<Piece> pieces = pieceManager.getBlackPieces();

        Vector<Piece> result = new Vector<>();

        if(pieceNotation.length() == 0){
            //can only be pawn
            for(Piece piece : pieces){
                if(piece.getPossibleMoves().contains(des) && piece.getNotation().equals(ChessNotation.PAWN)){
                    result.add(piece);
                }

                if(piece.tilePos().x == 4 && piece.tilePos().y == 1){
                    Log.d("pawn", "pawn");
                }
            }
            return result;
        }else if(pieceNotation.length() == 1){
            //can be any piece name
            for(Piece piece : pieces){
                if(piece.getNotation().equals(pieceNotation) && piece.getPossibleMoves().contains(des)){
                    result.add(piece);
                }
            }
            //can be file
            final char file = pieceNotation.charAt(0);
            final int x = 7 - (file - 'a');
            for(Piece piece : pieces){
                if(piece.getTile().pos.x == x && piece.getPossibleMoves().contains(des)){
                    result.add(piece);
                }
            }
            return result;
        } else if(pieceNotation.length() == 2){
            final String pieceName = pieceNotation.substring(0, 1);
            final char file = pieceNotation.charAt(1);
            final int x = 7 - (file - 'a');
            for(Piece piece : pieces){
                if(piece.getNotation().equals(pieceName) && piece.getTile().pos.x == x && piece.getPossibleMoves().contains(des)){
                    result.add(piece);
                }
            }
            return result;
        } else if(pieceNotation.length() == 3){
            final String pieceName = pieceNotation.substring(0, 1);
            final char file = pieceNotation.charAt(1);
            final int x = 7 - (file - 'a');
            final char rank = pieceNotation.charAt(2);
            final int y = rank - '1';
            for(Piece piece : pieces){
                if(piece.getNotation().equals(pieceName) && piece.getTile().pos.x == x && piece.getTile().pos.y == y && piece.getPossibleMoves().contains(des)){
                    result.add(piece);
                }
            }
            return result;
        }

        return result;
    }

    public void moveByNotation(String moveNotation, boolean isWhite) throws Exception {
        try {
            mutex.lock();
            if(prevSrcTile != null){
                prevSrcTile.getObj().changeState(Obj3D.State.NORMAL);
            }
            if(prevDesTile != null){
                prevDesTile.getObj().changeState(Obj3D.State.NORMAL);
            }
            ChessMove move = new ChessMove(isWhite, moveNotation, this);
            Tile srcTile = move.getSrcTile();
            Piece piece = srcTile.getPiece();
            Tile desTile = move.getDesTile();

            if(!piece.getPossibleMoves().contains(desTile)){
                throw new RuntimeException("Invalid move");
            }

            piece.putDown();
            piece.move(desTile.pos);
            checkForPromotionMove(move);
            updateBoardState();

            srcTile.getObj().changeState(Obj3D.State.HIGHLIGHTED);
            desTile.getObj().changeState(Obj3D.State.SOURCE);
            prevSrcTile = srcTile;
            prevDesTile = desTile;
        }finally {
            mutex.unlock();
        }
    }

    private void checkForPromotionMove(ChessMove move){
        if(move.isPromotionMove()){
            Piece piece = move.getDesTile().getPiece();
            Pawn pawn = (Pawn) piece;
            try {
                pawn.promote(move.getPromotingPieceNotation());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Vector<Tile> getPieceColorControlledTile(boolean isWhite){
        if(isWhite){
            return getWhiteControlledTile();
        }else{
            return getBlackControlledTile();
        }
    }

    public Vector<Tile> getPieceDifferentColorControlledTile(boolean isWhite){
        if(isWhite){
            return getBlackControlledTile();
        }else{
            return getWhiteControlledTile();
        }
    }

    public Vector<Tile> getBlackControlledTile(){
        Vector<Piece> blackPieces = pieceManager.getBlackPieces();
        Vector<Tile> result = new Vector<>();

        for(Piece piece : blackPieces){
            for(Tile tile : piece.getControlledTiles()){
                if(!result.contains(tile)){
                    result.add(tile);
                }
            }
        }

        return result;
    }

    public Vector<Tile> getWhiteControlledTile(){
        Vector<Piece> whitePieces = pieceManager.getWhitePieces();
        Vector<Tile> result = new Vector<>();

        for(Piece piece : whitePieces){
            for(Tile tile : piece.getControlledTiles()){
                if(!result.contains(tile)){
                    result.add(tile);
                }
            }
        }

        return result;
    }

    public void updateBoardState(){
        for(Piece piece : pieceManager.getAllPieces()){
            piece.updatePossibleMoves();
        }

        for(Piece piece : pieceManager.getAllPieces()){
            piece.updatePieceState();
        }

        for(Piece piece : pieceManager.getAllPieces()){
            piece.resetPieceState();
        }
    }

    public King getKing(boolean isWhite){
        if(isWhite){
            return pieceManager.getWhiteKing();
        }else{
            return pieceManager.getBlackKing();
        }
    }

    public void unhighlightAllTiles(){
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++){
                tiles[i][j].getObj().changeState(Obj3D.State.NORMAL);
            }
        }
    }

    public EntityManger getEntityManger() {
        return entityManger;
    }

    public PieceManager getPieceManager() {
        return pieceManager;
    }

    public void removePiece(Piece piece) {
        pieceManager.removePiece(piece);
    }

    public void addPiece(Piece piece){
        piece.getTile().setPiece(piece);
        pieceManager.addPiece(piece);
    }

    public ObjManager getObjManager() {
        return objManager;
    }

    public AssetManger getAssetManger() {
        return assetManger;
    }
}
