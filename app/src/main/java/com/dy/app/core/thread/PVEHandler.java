package com.dy.app.core.thread;

import android.util.Log;

import com.dy.app.activity.MultiPlayerOnSameDeviceActivity;
import com.dy.app.activity.PVEActivity;
import com.dy.app.gameplay.algebraicNotation.AlgebraicChessInterpreter;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.player.Player;
import com.dy.app.graphic.camera.CameraEntity;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.manager.SoundManager;
import com.dy.app.setting.GameSetting;
import com.dy.app.utils.DyConst;

public class PVEHandler extends MultiPlayerSameDeviceHandler implements PVEActivity.EngineMessageListener {

    private final PVEActivity pveActivity;

    public PVEHandler(PVEActivity pveActivity, Board board, TilePicker tilePicker) {
        super(pveActivity, board, tilePicker);
        this.pveActivity = pveActivity;
        this.pveActivity.setEngineMessageListener(this);
        this.setStockFishSkillLevel(11);
    }

    @Override
    protected void handleFinalMove(String moveNotation){
        // Send player move to engine
        String playerMoveUCI = convertAlgebraicToEngineMove(moveNotation);

        try {
            board.moveByNotation(moveNotation, player.isWhitePiece());
            board.changeKingColorInCheckState();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int testResult = board.testForCheck(!player.isWhitePiece());

        if(testResult == Board.IS_CHECK){
            moveNotation += "+";
        }else if(testResult == Board.IS_CHECKMATE){
            moveNotation += "#";
        }
        //check for checkmate
        if(moveNotation.contains("#")){
            synchronized (gameEnd){
                if(gameEnd){
                    return;
                }
                endGame(player.isWhitePiece()? DyConst.GAME_WHITE_WIN:DyConst.GAME_BLACK_WIN);
            }
            return;
        }

        player.setInTurn(false);

        handleMoveSoundEffect(moveNotation);
        //do camera animation if needed
        if(GameSetting.getInstance().isCinematicCameraEnabled){
            CameraEntity.getInstance().setState(
                    player.isWhitePiece()? CameraEntity.CameraState.BLACK_TO_WHITE: CameraEntity.CameraState.WHITE_TO_BLACK
            );
        }

        computeNextEngineMove();
    }

    @Override
    public void onEngineMessage(String line) {
        Log.d(PVEActivity.TAG, "ENGINE: [RECEIVED]\t" + line);
        if(line.startsWith("bestmove ")) {
            // Parse "bestmove e2e4" -> "e2e4"
            String[] parts = line.split(" ");
            if(parts.length >= 2) {
                String engineMove = parts[1];
                onEngineMoveRequest(engineMove);
            }
        }
    }

    private void onEngineMoveRequest(String engineMove) {
        // Convert engine move (e2e4) to algebraic notation if needed
        String moveNotation = convertEngineMoveToAlgebraic(engineMove);
        
        try {
            board.moveByNotation(moveNotation, !player.isWhitePiece());
            board.changeKingColorInCheckState();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int testResult = board.testForCheck(player.isWhitePiece());

        if(testResult == Board.IS_CHECK){
            moveNotation += "+";
        }else if(testResult == Board.IS_CHECKMATE){
            moveNotation += "#";
        }
        //check for checkmate
        if(moveNotation.contains("#")){
            synchronized (gameEnd){
                if(gameEnd){
                    return;
                }
                endGame(!player.isWhitePiece()? DyConst.GAME_WHITE_WIN:DyConst.GAME_BLACK_WIN);
            }
            return;
        }

        player.setInTurn(true);
        handleMoveSoundEffect(moveNotation);

        //do camera animation if needed
        if(GameSetting.getInstance().isCinematicCameraEnabled){
            CameraEntity.getInstance().setState(
                    player.isWhitePiece()? CameraEntity.CameraState.BLACK_TO_WHITE: CameraEntity.CameraState.WHITE_TO_BLACK
            );
        }
    }

    private void computeNextEngineMove() {
        long moveTime = Math.min(blackTimeRemainingMS, 5000); // Cap at 5 seconds
        // set up board position
        String positionCmd = "position startpos moves " + board.getEngineMoveHistory();
        pveActivity.sendEngineCommand(positionCmd);

        String goCmd = "go movetime " + moveTime;
        pveActivity.sendEngineCommand(goCmd);
    }

    private String convertAlgebraicToEngineMove(String algebraicMove) {
        // Remove check/checkmate symbols
        String cleanMove = algebraicMove.replaceAll("[+#]", "");
        
        // If already in UCI format (e2e4), return as-is
        if(cleanMove.length() >= 4 && cleanMove.matches("[a-h][1-8][a-h][1-8]")) {
            return cleanMove;
        }
        
        // Convert algebraic notation to UCI format
        try {
            // Create a temporary ChessMove to parse the algebraic notation
            com.dy.app.gameplay.move.ChessMove move = new com.dy.app.gameplay.move.ChessMove(player.isWhitePiece(), cleanMove, board);
            com.dy.app.gameplay.board.Tile srcTile = move.getSrcTile();
            com.dy.app.gameplay.board.Tile desTile = move.getDesTile();
            
            // Convert tile positions to UCI format
            String from = tileToUCI(srcTile.pos);
            String to = tileToUCI(desTile.pos);
            
            return from + to;
        } catch (Exception e) {
            Log.e(PVEActivity.TAG, "Failed to convert algebraic move: " + cleanMove, e);
            return cleanMove; // Fallback
        }
    }
    
    private String tileToUCI(com.dy.app.common.maths.Vec2i pos) {
        // Convert board coordinates to UCI format
        // Board uses (0,0) = top-left, UCI uses a8 = top-left
        char file = (char) ('a' + (7 - pos.x)); // Convert x to file (a-h)
        char rank = (char) ('1' + pos.y);       // Convert y to rank (1-8)
        return String.valueOf(file) + rank;
    }

    private String convertEngineMoveToAlgebraic(String engineMove) {
        // Convert UCI format (e2e4) to algebraic notation
        if(engineMove.length() < 4) {
            return engineMove; // Invalid format
        }
        
        try {
            String from = engineMove.substring(0, 2);
            String to = engineMove.substring(2, 4);
            
            // Convert UCI to board coordinates
            com.dy.app.common.maths.Vec2i fromPos = uciToTile(from);
            com.dy.app.common.maths.Vec2i toPos = uciToTile(to);
            
            // Get the piece that's moving
            com.dy.app.gameplay.board.Tile srcTile = board.getTile(fromPos);
            com.dy.app.gameplay.board.Tile desTile = board.getTile(toPos);
            
            if(srcTile == null || srcTile.getPiece() == null) {
                return engineMove; // Fallback
            }
            
            com.dy.app.gameplay.piece.Piece piece = srcTile.getPiece();
            String pieceNotation = piece.getNotation();
            
            // Handle different piece types
            if(pieceNotation.equals(com.dy.app.gameplay.algebraicNotation.ChessNotation.PAWN)) {
                // Pawn moves: e4, exd5, e8=Q
                if(desTile.getPiece() != null) {
                    return from.charAt(0) + "x" + to; // Capture: exd5
                } else {
                    return to; // Simple move: e4
                }
            } else {
                // Other pieces: Nf3, Qxd5, etc.
                if(desTile.getPiece() != null) {
                    return pieceNotation + "x" + to; // Capture: Qxd5
                } else {
                    return pieceNotation + to; // Simple move: Nf3
                }
            }
        } catch (Exception e) {
            Log.e(PVEActivity.TAG, "Failed to convert UCI move: " + engineMove, e);
            return engineMove; // Fallback
        }
    }
    
    private com.dy.app.common.maths.Vec2i uciToTile(String uci) {
        // Convert UCI format (e2) to board coordinates
        char file = uci.charAt(0);
        char rank = uci.charAt(1);
        
        int x = 7 - (file - 'a'); // Convert file to x coordinate
        int y = rank - '1';       // Convert rank to y coordinate
        
        return new com.dy.app.common.maths.Vec2i(x, y);
    }

    private void setStockFishSkillLevel(int value) {
        int cappedValue = Math.max(0, Math.min(20, value));
        pveActivity.sendEngineCommand("setoption name Skill Level value " + cappedValue);
    }
}
