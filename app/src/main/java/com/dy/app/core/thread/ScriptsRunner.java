package com.dy.app.core.thread;

import android.util.Log;

import com.dy.app.common.maths.Vec2i;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.model.Obj3D;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class ScriptsRunner extends Thread{
    InputStream is;

    public static final String TAG = "ScriptsRunner";
    private InputStreamReader reader;
    private Board board;
    private final Semaphore whiteSem = new Semaphore(1);
    private final Semaphore blackSem = new Semaphore(0);

    public ScriptsRunner(InputStream is, Board board){
        this.is = is;
        reader = new InputStreamReader(is);
        moves = new Vector<>();
        meta = new HashMap<>();
        this.board = board;
    }

    @Override
    public void run() {
        try {
            parsePGN();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BlackRunner blackRunner = new BlackRunner(moves, board, whiteSem, blackSem);
        blackRunner.start();
        WhiteRunner whiteRunner = new WhiteRunner(moves, board, whiteSem, blackSem);
        whiteRunner.start();

        Log.d(TAG, " run: stopped");
    }

    private void parsePGN() throws IOException {
        StringBuilder builder = new StringBuilder();
        String curLine = null;
        final int BUFFER_SIZE = 1024;
        char[] buffer = new char[BUFFER_SIZE];
        int count = -1;

        while(reader.ready()){
            try {
                count = reader.read(buffer, 0, BUFFER_SIZE);
                if(count == -1) break;
                curLine = new String(buffer, 0, count);
                Log.d(TAG, curLine);
                builder.append(curLine);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        data = builder.toString();

        String[] splitResult = data.split("\n\n");

        if(splitResult.length != 2) {
            throw new RuntimeException("Invalid PGN file");
        }

        String metaStr = splitResult[0];
        String movesStr = splitResult[1];

        parseMeta(metaStr);
        parseMoves(movesStr);
    }

    private void parseMeta(String metaStr) {
        String[] metaLines = metaStr.split("\n");
        for(String line : metaLines){
            line = line.trim();

            try{
                line = line.substring(1, line.length()-1);
                String[] splitResult = line.split("\\s+\"");
                if(splitResult.length != 2) throw new RuntimeException("Invalid PGN file");
                meta.put(splitResult[0].trim(), splitResult[1].trim().substring(0, splitResult[1].length()-1));
            }catch (StringIndexOutOfBoundsException e){
                throw new RuntimeException("Invalid PGN file");
            }
        }
    }

    private void parseMoves(String movesStr) {
        String[] splitResults = movesStr.split("\\s+");
        Move move = null;

        for(int i = 0; i < splitResults.length; i++){
            String curStr = splitResults[i];
            curStr = curStr.trim();
            //check for correct index
            if(i%3 == 0) {
                int index = -1;

                try{
                    index = Integer.parseInt(curStr.substring(0, curStr.length()-1));
                }catch (NumberFormatException e){
                    throw new RuntimeException("Invalid PGN file");
                }

                if(index != i/3 + 1) throw new RuntimeException("Index error");
            }else if(i%3 == 1){
                move = new Move();
                move.white = curStr;
            }else{
                move.black = curStr;
                moves.add(move);
            }
        }
    }

    public class Move{
        public String white;
        public String black;
    }

    private final Vector<Move> moves;
    private final Map<String, String> meta;
    private String data;
}
