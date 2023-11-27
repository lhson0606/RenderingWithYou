package com.dy.app.manager;

import android.content.Context;

import com.anychart.charts.Pie;
import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.Rival;
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
import com.dy.app.setting.GameSetting;
import com.dy.app.utils.DyConst;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class PieceManager {
    private static PieceManager instance = null;
    private final Context context;
    private final Board board;
    private final ObjManager objManager;
    private final AssetManger assetManger;
    private final EntityManger entityManger;
    private final GameSetting gameSetting;
    private King blackKing;
    private King whiteKing;
    private final Vector<Piece> allPieces = new Vector<>();
    private final ReentrantLock mutex = new ReentrantLock(true);

    public PieceManager(Context context, EntityManger entityManger, Board board, ObjManager objManager, AssetManger assetManger, GameSetting gameSetting){
        this.entityManger = entityManger;
        this.context = context;
        this.board = board;
        this.objManager = objManager;
        this.assetManger = assetManger;
        this.gameSetting = gameSetting;
    }

    public void init() throws IOException {
        Player player = Player.getInstance();
        Rival rival = Rival.getInstance();
        Skin playerSkin = assetManger.getSkin(AssetManger.SkinType.PLAYER);
        Skin rivalSkin = assetManger.getSkin(AssetManger.SkinType.RIVAL);
        boolean playerIsWhite = Player.getInstance().isWhitePiece();
        Vector<Piece> player_pieces;
        Vector<Piece> enemy_pieces ;


        if(playerIsWhite) {
            player_pieces = loadWhitePieces(true, playerSkin);
            enemy_pieces = loadBlackPieces(false, rivalSkin);
        }else{
            enemy_pieces = loadWhitePieces(false, rivalSkin);
            player_pieces = loadBlackPieces(true, playerSkin);
        }

        for(Piece piece : allPieces){
            entityManger.newEntity(piece);
        }

    }

    public Piece loadSinglePiece(Board board, boolean onPlayerSide, Skin skin, String pieceName, Vec2i pos, Piece.PieceColor pieceColor) throws IOException {
        Tile tile = board.getTile(pos);
        Obj3D obj = objManager.getObj(pieceName);
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
                        pieceColor,
                        board
                );
                break;
            case DyConst.rook:
                piece = new Rook(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor,
                        board
                );
                break;
            case DyConst.knight:
                piece = new Knight(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor,
                        board
                );
                break;
            case DyConst.bishop:
                piece = new Bishop(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor,
                        board
                );
                break;
            case DyConst.queen:
                piece = new Queen(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor,
                        board
                );
                break;
            case DyConst.king:
                piece = new King(
                        tile,
                        obj,
                        onPlayerSide,
                        pieceColor,
                        board
                );
                break;
        }


        tile.setPiece(piece);
        PieceShader shader = new PieceShader(
                ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.piece_ver_glsl_path)),
                ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.piece_frag_glsl_path)),
                gameSetting
        );
        shader.setPiece(piece);
        piece.getObj().setShader(shader);
        return piece;
    }

    private Vector<Piece> loadBlackPieces(boolean onPlayerSide, Skin skin) throws IOException {
        Vector<Piece> blackPieces = new Vector<>();

        //8 pawns
        for(int i = 0; i<8; i++){
            blackPieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.pawn, new Vec2i(i, 6), Piece.PieceColor.BLACK));
        }

        //2 rooks
        blackPieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.rook, new Vec2i(0, 7), Piece.PieceColor.BLACK));
        blackPieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.rook, new Vec2i(7, 7), Piece.PieceColor.BLACK));

        //2 knights
        blackPieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.knight, new Vec2i(1, 7), Piece.PieceColor.BLACK));
        blackPieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.knight, new Vec2i(6, 7), Piece.PieceColor.BLACK));

        //2 bishops
        blackPieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.bishop, new Vec2i(2, 7), Piece.PieceColor.BLACK));
        blackPieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.bishop, new Vec2i(5, 7), Piece.PieceColor.BLACK));

        //1 queen
        blackPieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.queen, new Vec2i(4, 7), Piece.PieceColor.BLACK));

        //1 king
        blackKing = (King) loadSinglePiece(board, onPlayerSide, skin, DyConst.king, new Vec2i(3, 7), Piece.PieceColor.BLACK);
        blackPieces.add(blackKing);

        allPieces.addAll(blackPieces);
        return blackPieces;
    }

    private Vector<Piece> loadWhitePieces(boolean onPlayerSide, Skin skin) throws IOException {
        Vector<Piece> whitePieces = new Vector<>();
        String verCode  = ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.piece_ver_glsl_path));
        String fragCode = ShaderHelper.getInstance().readShader(context.getAssets().open(DyConst.piece_frag_glsl_path));
        //8 pawns
        for(int i = 0; i<8; i++){
            whitePieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.pawn, new Vec2i(i, 1), Piece.PieceColor.WHITE));
        }

        //2 rooks
        whitePieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.rook, new Vec2i(0, 0), Piece.PieceColor.WHITE));
        whitePieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.rook, new Vec2i(7, 0), Piece.PieceColor.WHITE));

        //2 knights
        whitePieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.knight, new Vec2i(1, 0), Piece.PieceColor.WHITE));
        whitePieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.knight, new Vec2i(6, 0), Piece.PieceColor.WHITE));

        //2 bishops
        whitePieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.bishop, new Vec2i(2, 0), Piece.PieceColor.WHITE));
        whitePieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.bishop, new Vec2i(5, 0), Piece.PieceColor.WHITE));

        //1 queen
        whitePieces.add(loadSinglePiece(board, onPlayerSide, skin, DyConst.queen, new Vec2i(4, 0), Piece.PieceColor.WHITE));

        //1 king
        whiteKing = (King) loadSinglePiece(board, onPlayerSide, skin, DyConst.king, new Vec2i(3, 0), Piece.PieceColor.WHITE);
        whitePieces.add(whiteKing);

        allPieces.addAll(whitePieces);
        return whitePieces;
    }

    public Vector<Piece> getBlackPieces(){
        try{
            mutex.lock();
            Vector<Piece> blackPieces = new Vector<>();

            for(Piece piece : allPieces){
                if(!piece.isWhite() && !piece.currentState.isCaptured){
                    blackPieces.add(piece);
                }
            }

            return blackPieces;
        } finally {
            mutex.unlock();
        }
    }

    public Vector<Piece> getWhitePieces(){
        try{
            mutex.lock();
            Vector<Piece> whitePieces = new Vector<>();

            for(Piece piece : allPieces){
                if(piece.isWhite() && !piece.currentState.isCaptured){
                    whitePieces.add(piece);
                }
            }

            return whitePieces;
        } finally {
            mutex.unlock();
        }
    }

    public King getBlackKing() {
        return blackKing;
    }

    public King getWhiteKing() {
        return whiteKing;
    }

    public Vector<Piece> getActivePieces() {
        Vector<Piece> activePieces = new Vector<>();

        for(Piece piece : allPieces){
            if(!piece.currentState.isCaptured){
                activePieces.add(piece);
            }
        }

        return activePieces;
    }

    public void replacePiece(Piece srcPiece, Piece dstPiece){
        try{
            mutex.lock();
            if(srcPiece == null) throw new RuntimeException("Src is null");
            if(dstPiece == null) throw new RuntimeException("Dst is null");
            if(srcPiece == blackKing || srcPiece == whiteKing) throw new RuntimeException("Cannot remove king");
            if(!allPieces.contains(srcPiece)) throw new RuntimeException("Src not found");
            if(allPieces.contains(dstPiece)) throw new RuntimeException("Dst already exists");
            allPieces.remove(srcPiece);
            allPieces.add(dstPiece);
            entityManger.removeEntity(srcPiece);
            //we need to add dstPiece to entity manager and wait for gl init
            Semaphore semaphore = new Semaphore(0);
            entityManger.getRenderer().addAndInitEntityGL(dstPiece, ()->{
                semaphore.release();
            });
            try {
                //we need to wait for entity to be added before executing next move
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }finally {
                semaphore.release();
            }
        } finally {
            mutex.unlock();
        }
    }

    public void capturePiece(Piece piece){
        try{
            mutex.lock();
            if(piece == null) throw new RuntimeException("Piece is null");
            if(piece == blackKing || piece == whiteKing) throw new RuntimeException("Cannot remove king");
            piece.currentState.isCaptured = true;
            entityManger.removeEntity(piece);
        } finally {
            mutex.unlock();
        }
    }

    public Vector<Piece> getAllPieces() {
        return allPieces;
    }

    public void ghostReplace(Piece src, Piece dst){
        try{
            mutex.lock();
            if(src == null) throw new RuntimeException("Src is null");
            if(dst == null) throw new RuntimeException("Dst is null");
            if(src == blackKing || src == whiteKing) throw new RuntimeException("Cannot remove king");
            if(!allPieces.contains(src)) throw new RuntimeException("Src not found");
            if(allPieces.contains(dst)) throw new RuntimeException("Dst already exists");
            allPieces.remove(src);
            allPieces.add(dst);
        } finally {
            mutex.unlock();
        }
    }

    public void pseudoReplace(Piece src, Piece dst) {
        //we just need to replace in all pieces, no need to replace in entity manager
        try{
            mutex.lock();
            if(src == null) throw new RuntimeException("Src is null");
            if(dst == null) throw new RuntimeException("Dst is null");
            if(src == blackKing || src == whiteKing) throw new RuntimeException("Cannot remove king");
            if(!allPieces.contains(src)) throw new RuntimeException("Src not found");
            if(allPieces.contains(dst)) throw new RuntimeException("Dst already exists");
            allPieces.remove(src);
            allPieces.add(dst);
        } finally {
            mutex.unlock();
        }
    }

    public void undoCapture(Piece piece) {
        try{
            mutex.lock();
            if(piece == null) throw new RuntimeException("Piece is null");
            if(piece == blackKing || piece == whiteKing) throw new RuntimeException("Cannot remove king");
            piece.currentState.isCaptured = false;
            //no need to call init gl, we just need to add to entity manager
            entityManger.newEntity(piece);
        } finally {
            mutex.unlock();
        }
    }
}
