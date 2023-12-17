package com.dy.app.gameplay.board;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.common.maths.Vec3;
import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.move.ChessMove;
import com.dy.app.gameplay.algebraicNotation.ChessNotation;
import com.dy.app.gameplay.pgn.PGNFile;
import com.dy.app.gameplay.pgn.PGNMove;
import com.dy.app.gameplay.piece.King;
import com.dy.app.gameplay.piece.Pawn;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.gameplay.player.Player;
import com.dy.app.gameplay.player.Rival;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.shader.ShaderHelper;
import com.dy.app.graphic.shader.TileShader;
import com.dy.app.manager.AssetManger;
import com.dy.app.manager.EntityManger;
import com.dy.app.manager.ObjManager;
import com.dy.app.manager.PieceManager;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
    private int moveCount = 0;
    private final Vector<String> moveRecord = new Vector<>();

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

    public void pseudoUpdateBoardState() {
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
        if(moveNotation.equals("")) return;
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

            changeKingColorInCheckState();

            //save to move record
            saveMoveRecord(moveNotation);
        }finally {
            mutex.unlock();
        }
    }

    private void saveMoveRecord(String moveNotation){
        moveRecord.add(moveNotation);
    }

    public void changeKingColorInCheckState(){
        int testResult = -1;
        //test for white check
        testResult = testForCheck(true);
        King king = pieceManager.getWhiteKing();

        if(testResult == NO_CHECK){
            //change king color to normal
            king.getObj().changeState(Obj3D.State.NORMAL);
        }else{
            //change king color to check
            king.getObj().changeState(Obj3D.State.ENDANGERED);
        }

        //test for black check
        testResult = testForCheck(false);
        king = pieceManager.getBlackKing();

        if(testResult == NO_CHECK){
            //change king color to normal
            king.getObj().changeState(Obj3D.State.NORMAL);
        }else{
            //change king color to check
            king.getObj().changeState(Obj3D.State.ENDANGERED);
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

        moveCount++;
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

    public boolean isStalemate(boolean isWhite){
        Vector<Piece> pieces = isWhite ? pieceManager.getWhitePieces() : pieceManager.getBlackPieces();
        for(Piece piece : pieces){
            if(piece.getPossibleMoves().size() > 0){
                return false;
            }
        }
        return true;
    }

//    private int testForWhiteCheck() {
//        Vector<Tile> blackControlledTiles = getBlackControlledTile();
//        King whiteKing = pieceManager.getWhiteKing();
//
//        if(!blackControlledTiles.contains(whiteKing.getTile())){
//            return NO_CHECK;
//        }
//
//        boolean isCheckmate = true;
//        //we need to try every possible moves of the white pieces
//        for(Piece piece : pieceManager.getWhitePieces()){
//            for(Tile tile : piece.getPossibleMoves()){
//                //try pseudo move
//                piece.pseudoMove(tile.pos);
//                //update the board state for the pseudo move
//                pseudoUpdateBoardState();
//                //check if the king is still in check
//                Vector<Tile> newBlackControlledTiles = getBlackControlledTile();
//                //there's a move that can get the king out of check
//                if(!newBlackControlledTiles.contains(whiteKing.getTile())){
//                    isCheckmate = false;
//                    //rollback the pseudo move and break
//                    piece.rollbackPseudoMove();
//                    break;
//                }
//            }
//        }
//
//        //restore the board state
//        for(Piece piece : pieceManager.getActivePieces()){
//            piece.updatePossibleMoves();
//        }
//
//        return isCheckmate ? IS_CHECKMATE : IS_CHECK;
//    }
//
//    private int testForBlackCheck() {
//        Vector<Tile> whiteControlledTiles = getWhiteControlledTile();
//        King blackKing = pieceManager.getBlackKing();
//
//        if(!whiteControlledTiles.contains(blackKing.getTile())){
//            return NO_CHECK;
//        }
//
//        boolean isCheckmate = true;
//        //we need to try every possible moves of the white pieces
//        for(Piece piece : pieceManager.getBlackPieces()){
//            for(Tile tile : piece.getPossibleMoves()){
//                //try pseudo move
//                piece.pseudoMove(tile.pos);
//                //update the board state for the pseudo move
//                pseudoUpdateBoardState();
//                //check if the king is still in check
//                Vector<Tile> newWhiteControlledTiles = getWhiteControlledTile();
//                //there's a move that can get the king out of check
//                if(!newWhiteControlledTiles.contains(blackKing.getTile())){
//                    isCheckmate = false;
//                    //rollback the pseudo move and break
//                    piece.rollbackPseudoMove();
//                    break;
//                }
//            }
//        }
//
//        //restore the board state
//        for(Piece piece : pieceManager.getActivePieces()){
//            piece.updatePossibleMoves();
//        }
//
//        return isCheckmate ? IS_CHECKMATE : IS_CHECK;
//    }


    public void goToMove(int moveNumber) throws Exception {
        Log.d("Board", String.format(Locale.ENGLISH, "run: %d", moveNumber));
        try{
            //we have to lock mutex to prevent concurrent access
            mutex.lock();
            if(moveNumber < 0){
                throw new RuntimeException("Invalid move number");
            }

            if(moveNumber == moveCount - 1) {
                //moveNumber is always 1 ahead so we have to subtract 1
                //if the desired move number is the current move number, we don't have to do anything
                return;
            }

            //remove all the pieces from tiles
            for(int i = 0; i<8; i++){
                for(int j = 0; j<8; j++){
                    tiles[i][j].setPiece(null);
                }
            }

            //we now undo capture state to all pieces, if it's captured at moveNumber, it will be captured again
            //if it's not captured at moveNumber, it will be uncaptured
            //only perform when the captured state is different from the current state
            //note that its position is not reset, only captured state is reset (now it's present when draw)
            for(Piece piece: pieceManager.getAllPieces()){
                piece.checkForUndoCaptureState(moveNumber);
            }

            //move all the active pieces on the board to its corresponding position at moveNumber
            //all pieces on the board capture state is valid now, we need to move them to correct position
            for(Piece piece : pieceManager.getActivePieces()){
                if(!piece.isInitialized){// for extra safety
                    throw new RuntimeException("Piece not initialized");
                }
                piece.goToPositionAtState(moveNumber, true);
            }

            //we now need to undoPromotionState to the active pieces
            for(Piece piece : pieceManager.getActivePieces()){
                piece.checkForUndoPromotionState(moveNumber);
            }

            //set its state at moveNumber including make its tile point to it and update current state to corresponding state
            for(Piece piece : pieceManager.getActivePieces()){
                if(!piece.isInitialized){
                    throw new RuntimeException("Piece not initialized");
                }
                piece.setStateAndTileAtMoveNumber(moveNumber);
            }

//            Log.d("Board", "refreshDisplayPosition");
//            for(Piece piece: pieceManager.getActivePieces()){
//                if(!piece.isInitialized){
//                    throw new RuntimeException("Piece not initialized");
//                }
//                piece.refreshDisplayPosition();
//            }

            for(Piece piece : pieceManager.getActivePieces()){
                piece.updatePossibleMoves();
            }

            Log.d("Board", "board at " + moveNumber + "is restated\n" + toString());

            //update the current count
            moveCount = moveNumber + 1;
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
        for(int i = DyConst.row_count - 1; i >=0 ; i--){
            for(int j = DyConst.col_count - 1; j >=0 ; j--){
                builder.append(tiles[j][i].toString());
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public String getMoveHistory(){
        StringBuilder builder = new StringBuilder();
        int moveNumber = 0;

        for(String move : moveRecord){
            if(moveNumber%2 == 0){
                builder.append(String.format(Locale.ENGLISH, "%d. %s ", moveNumber/2 + 1, move));
            }else{
                builder.append(String.format(Locale.ENGLISH, "%s ", move));
            }
            moveNumber++;
        }

        return builder.toString();
    }

    public boolean hasPossibleMove(boolean isWhite){
        Vector<Piece> pieces = isWhite ? pieceManager.getWhitePieces() : pieceManager.getBlackPieces();
        for(Piece piece : pieces){
            if(piece.getPossibleMoves().size() > 0){
                return true;
            }
        }
        return false;
    }

    public boolean isUndoAllowed(boolean isWhitePiece) {
        try{
            mutex.lock();

            if(moveCount == 1){
                return false;
            }

            if(isWhitePiece && moveCount%2 == 0){
                return true;
            }else if(!isWhitePiece && moveCount%2 == 1) {
                return true;
            }

            return false;

        }finally {
            mutex.unlock();
        }
    }

    public void undoMove() throws Exception {
        try{
            //we have to lock mutex to prevent concurrent access
            mutex.lock();
            moveRecord.remove(moveRecord.size() - 1);
            int moveNumber = moveCount - 2;
            Log.d("Board", String.format(Locale.ENGLISH, "run: %d", moveNumber));
            if(moveNumber < 0){
                throw new RuntimeException("Invalid move number");
            }

            if(moveNumber == moveCount - 1) {
                //moveNumber is always 1 ahead so we have to subtract 1
                //if the desired move number is the current move number, we don't have to do anything
                return;
            }

            //remove all the pieces from tiles
            for(int i = 0; i<8; i++){
                for(int j = 0; j<8; j++){
                    tiles[i][j].setPiece(null);
                }
            }

            //we now undo capture state to all pieces, if it's captured at moveNumber, it will be captured again
            //if it's not captured at moveNumber, it will be uncaptured
            //only perform when the captured state is different from the current state
            //note that its position is not reset, only captured state is reset (now it's present when draw)
            for(Piece piece: pieceManager.getAllPieces()){
                piece.checkForUndoCaptureState(moveNumber);
            }

            //move all the active pieces on the board to its corresponding position at moveNumber
            //all pieces on the board capture state is valid now, we need to move them to correct position
            for(Piece piece : pieceManager.getActivePieces()){
                if(!piece.isInitialized){// for extra safety
                    throw new RuntimeException("Piece not initialized");
                }
                piece.goToPositionAtState(moveNumber, true);
            }

            //we now need to undoPromotionState to the active pieces
            for(Piece piece : pieceManager.getActivePieces()){
                piece.checkForUndoPromotionState(moveNumber);
            }

            //set its state at moveNumber including make its tile point to it and update current state to corresponding state
            for(Piece piece : pieceManager.getActivePieces()){
                if(!piece.isInitialized){
                    throw new RuntimeException("Piece not initialized");
                }
                piece.setStateAndTileAtMoveNumber(moveNumber);
            }

            for(Piece piece : pieceManager.getActivePieces()){
                piece.updatePossibleMoves();
            }

            Log.d("Board", "board at " + moveNumber + "is restated\n" + toString());

            //update the current count
            moveCount = moveNumber + 1;
        }finally {
            mutex.unlock();
        }
    }
}
