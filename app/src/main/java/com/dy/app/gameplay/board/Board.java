package com.dy.app.gameplay.board;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.move.ChessMove;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;
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
import java.util.Locale;
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
    private final ReentrantLock mutex = new ReentrantLock(true);
    public static int NO_CHECK = 0;
    public static int IS_CHECK = 1;
    public static int IS_CHECKMATE = 2;
    private int moveCount = 1;
    private final StringBuilder moveHistoryBuilder = new StringBuilder();

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

    public Vector<Piece> getPieceByNotation(boolean isWhite, String pieceNotation, Tile des){
        if(isWhite){
            return getWhitePiecesByNotation(pieceNotation, des);
        }else{
            return getBlackPiecesByNotation(pieceNotation, des);
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
            final char c = pieceNotation.charAt(1);

            if(isFile(c)){
                final int x = 7 - (c - 'a');
                for(Piece piece : pieces){
                    if(piece.getNotation().equals(pieceName) && piece.getTile().pos.x == x && piece.getPossibleMoves().contains(des)){
                        result.add(piece);
                    }
                }

                return result;
            }else if(isRank(c)){
                final int y = c - '1';
                for(Piece piece : pieces){
                    if(piece.getNotation().equals(pieceName) && piece.getTile().pos.y == y && piece.getPossibleMoves().contains(des)){
                        result.add(piece);
                    }
                }

                return result;
            }else{
                throw new RuntimeException("Unknown character");
            }
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

    private boolean isFile(char c){
        return c >= 'a' && c <= 'h';
    }

    private boolean isRank(char c){
        return c >= '1' && c <= '8';
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
            final char c = pieceNotation.charAt(1);

            if(isFile(c)){
                final int x = 7 - (c - 'a');
                for(Piece piece : pieces){
                    if(piece.getNotation().equals(pieceName) && piece.getTile().pos.x == x && piece.getPossibleMoves().contains(des)){
                        result.add(piece);
                    }
                }

                return result;
            }else if(isRank(c)){
                final int y = c - '1';
                for(Piece piece : pieces){
                    if(piece.getNotation().equals(pieceName) && piece.getTile().pos.y == y && piece.getPossibleMoves().contains(des)){
                        result.add(piece);
                    }
                }

                return result;
            }else{
                throw new RuntimeException("Unknown character");
            }
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

    public void pseudoMoveByNotation(String moveNotation, boolean isWhite) throws Exception {
        try {
            mutex.lock();
            ChessMove move = new ChessMove(isWhite, moveNotation, this);
            Tile srcTile = move.getSrcTile();
            Piece piece = srcTile.getPiece();
            Tile desTile = move.getDesTile();

            if(!piece.getPossibleMoves().contains(desTile)){
                throw new RuntimeException("Invalid move");
            }

            piece.pseudoMove(desTile.pos);
            pseudoUpdateBoardState();
        }finally {
            mutex.unlock();
        }
    }

    private void pseudoUpdateBoardState() {
        for(Piece piece : pieceManager.getActivePieces()){
            piece.updatePossibleMoves();
        }

        for(Piece piece : pieceManager.getActivePieces()){
            piece.updatePieceStateBeforeWritingToHistory();
        }

        //no need to write to history

        for(Piece piece : pieceManager.getAllPieces()){
            piece.updatePieceStateAfterWritingToHistory();
        }
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
            moveCount++;

            srcTile.getObj().changeState(Obj3D.State.HIGHLIGHTED);
            desTile.getObj().changeState(Obj3D.State.SOURCE);
            prevSrcTile = srcTile;
            prevDesTile = desTile;

            //save to board history
            if(isWhite) {
                moveHistoryBuilder.append(moveCount / 2).append(". ").append(moveNotation).append(" ");
            }else{
                moveHistoryBuilder.append(moveNotation).append(" ");
            }

            Log.d("Board", toString());
            Log.d("Board", "moveCount: " + moveCount);
            Log.d("Board", "moveHistory: " + moveHistoryBuilder.toString());
        }finally {
            mutex.unlock();
        }
    }

    private void checkForPromotionMove(ChessMove move){
        if(move.isPromotionMove()){
            Piece piece = move.getDesTile().getPiece();
            Pawn pawn = (Pawn) piece;
            try {
                pawn.promote(move.getPromotingPieceNotation(), true);
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
        for(Piece piece : pieceManager.getActivePieces()){
            piece.updatePossibleMoves();
        }

        for(Piece piece : pieceManager.getActivePieces()){
            piece.updatePieceStateBeforeWritingToHistory();
        }

        for(Piece piece : pieceManager.getAllPieces()){
            piece.addStateToHistory();
        }

        for(Piece piece : pieceManager.getActivePieces()){
            piece.updatePieceStateAfterWritingToHistory();
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

    public void capturePiece(Piece piece) {
        piece.getTile().setPiece(null);
        pieceManager.capturePiece(piece);
    }

    public void replacePiece(Piece srcPiece, Piece desPiece, boolean inheritState) {
        if(inheritState){
            desPiece.currentState = srcPiece.currentState;
        }
        srcPiece.getTile().setPiece(desPiece);
        pieceManager.replacePiece(srcPiece, desPiece);
    }

    public void pseudoReplacePiece(Piece srcPiece, Piece desPiece) {
        desPiece.currentState = srcPiece.currentState;
        srcPiece.getTile().setPiece(desPiece);
        pieceManager.pseudoReplace(srcPiece, desPiece);
    }

    public ObjManager getObjManager() {
        return objManager;
    }

    public AssetManger getAssetManger() {
        return assetManger;
    }

    /**
     * @param isWhite the king color to be checked
     * @return 0 if no check, 1 if check, 2 if checkmate
     */
    public int testForCheck(boolean isWhite){
        Log.d("Board", "testForCheck");
        Vector<Tile> controlledTiles = isWhite ? getBlackControlledTile() : getWhiteControlledTile();
        King king = isWhite ? pieceManager.getWhiteKing() : pieceManager.getBlackKing();

        if(!controlledTiles.contains(king.getTile())){
            Log.d("Board", "no check");
            return NO_CHECK;
        }

        Log.d("Board", "check");
        boolean isCheckmate = true;
        Vector<Piece> pieces = isWhite ? pieceManager.getWhitePieces() : pieceManager.getBlackPieces();
        //we need to try every possible moves of the ally pieces
        for(Piece piece : pieces){
            Vector<Tile> currentPossibleMove = new Vector<>(piece.getPossibleMoves());
            for(Tile tile : currentPossibleMove){
                //try pseudo move
                piece.pseudoMove(tile.pos);
                //update the board state for the pseudo move
                pseudoUpdateBoardState();
                //check if the king is still in check
                Vector<Tile> newControlledTiles = isWhite ? getBlackControlledTile() : getWhiteControlledTile();
                //there's a move that can get the king out of check
                if(!newControlledTiles.contains(king.getTile())){
                    isCheckmate = false;
                    Log.d("Board", toString());
                    piece.rollbackPseudoMove();
                    pseudoUpdateBoardState();
                    break;
                }
                piece.rollbackPseudoMove();
                pseudoUpdateBoardState();
            }

            //no need to continue if we detect there's move to parry the check
            if(!isCheckmate){
                break;
            }
        }

        Log.d("Board", "isCheckmate: " + isCheckmate);
        return isCheckmate ? IS_CHECKMATE : IS_CHECK;
    }

    private int testForWhiteCheck() {
        Vector<Tile> blackControlledTiles = getBlackControlledTile();
        King whiteKing = pieceManager.getWhiteKing();

        if(!blackControlledTiles.contains(whiteKing.getTile())){
            return NO_CHECK;
        }

        boolean isCheckmate = true;
        //we need to try every possible moves of the white pieces
        for(Piece piece : pieceManager.getWhitePieces()){
            for(Tile tile : piece.getPossibleMoves()){
                //try pseudo move
                piece.pseudoMove(tile.pos);
                //update the board state for the pseudo move
                pseudoUpdateBoardState();
                //check if the king is still in check
                Vector<Tile> newBlackControlledTiles = getBlackControlledTile();
                //there's a move that can get the king out of check
                if(!newBlackControlledTiles.contains(whiteKing.getTile())){
                    isCheckmate = false;
                    //rollback the pseudo move and break
                    piece.rollbackPseudoMove();
                    break;
                }
            }
        }

        //restore the board state
        for(Piece piece : pieceManager.getActivePieces()){
            piece.updatePossibleMoves();
        }

        return isCheckmate ? IS_CHECKMATE : IS_CHECK;
    }

    private int testForBlackCheck() {
        Vector<Tile> whiteControlledTiles = getWhiteControlledTile();
        King blackKing = pieceManager.getBlackKing();

        if(!whiteControlledTiles.contains(blackKing.getTile())){
            return NO_CHECK;
        }

        boolean isCheckmate = true;
        //we need to try every possible moves of the white pieces
        for(Piece piece : pieceManager.getBlackPieces()){
            for(Tile tile : piece.getPossibleMoves()){
                //try pseudo move
                piece.pseudoMove(tile.pos);
                //update the board state for the pseudo move
                pseudoUpdateBoardState();
                //check if the king is still in check
                Vector<Tile> newWhiteControlledTiles = getWhiteControlledTile();
                //there's a move that can get the king out of check
                if(!newWhiteControlledTiles.contains(blackKing.getTile())){
                    isCheckmate = false;
                    //rollback the pseudo move and break
                    piece.rollbackPseudoMove();
                    break;
                }
            }
        }

        //restore the board state
        for(Piece piece : pieceManager.getActivePieces()){
            piece.updatePossibleMoves();
        }

        return isCheckmate ? IS_CHECKMATE : IS_CHECK;
    }


    public void checkForUndoCapture(int moveNumber){
        for(Piece piece : pieceManager.getAllPieces()){
            piece.checkForUndoCapture(moveNumber);
        }
    }

    public void goToMove(int moveNumber){
        Log.d("Board", String.format(Locale.ENGLISH, "run: %d", moveNumber));
        try{
            mutex.lock();
            if(moveNumber < 0 || moveNumber > moveCount){
                throw new RuntimeException("Invalid move number");
            }

            if(moveNumber == moveCount) return;

            Log.d("Board", "perform move to the desired state");
            //perform move to the desired state
            for(Piece piece : pieceManager.getAllPieces()){
                if(!piece.isInitialized){
                    throw new RuntimeException("Piece not initialized");
                }
                piece.goToMove(moveNumber);
            }

            Log.d("Board", "check for undo capture");
            checkForUndoCapture(moveNumber);

            //refresh piece position
            for(int i = 0; i<8; i++){
                for(int j = 0; j<8; j++){
                    tiles[i][j].setPiece(null);
                }
            }

            Log.d("Board", "setStateAtMoveNumber");
            for(Piece piece : pieceManager.getAllPieces()){
                if(!piece.isInitialized){
                    throw new RuntimeException("Piece not initialized");
                }
                piece.setStateAtMoveNumber(moveNumber);
            }

            Log.d("Board", "refreshDisplayPosition");
            for(Piece piece: pieceManager.getAllPieces()){
                if(!piece.isInitialized){
                    throw new RuntimeException("Piece not initialized");
                }
                piece.refreshDisplayPosition();
            }

            Log.d("Board", "updatePossibleMoves");
            for(Piece piece : pieceManager.getAllPieces()){
                piece.updatePossibleMoves();
            }

            //update the current count
            //moveCount = moveNumber;
        }finally {
            mutex.unlock();
        }
    }

    public int getMoveCount(){
        try {
            mutex.lock();
            return moveCount;
        }finally {
            mutex.unlock();
        }
    }

    public void undoCapture(Piece piece) {
        pieceManager.undoCapture(piece);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < DyConst.row_count; i++){
            for(int j = 0; j < DyConst.col_count; j++){
                builder.append(tiles[j][i].toString());
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
