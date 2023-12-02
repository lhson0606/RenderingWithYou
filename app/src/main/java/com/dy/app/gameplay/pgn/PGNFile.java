package com.dy.app.gameplay.pgn;

import android.content.Context;
import android.util.Log;

import com.dy.app.core.thread.ScriptsRunner;
import com.dy.app.gameplay.board.Board;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class PGNFile {
    private final Vector<Move> moves = new Vector<>();
    private String data;
    private final Map<String, String> meta = new HashMap<>();
    public static final String TAG = "PGNFile";

    public String getBlackPlayerName() {
        return meta.get("Black");
    }

    public String getWhitePlayerName() {
        return meta.get("White");
    }

    public String getBlackPlayerElo() {
        return meta.get("BlackElo");
    }

    public String getWhitePlayerElo() {
        return meta.get("WhiteElo");
    }

    public Vector<Move> getMoves() {
        return moves;
    }

    public class Move{
        public String white;
        public String black;
    }

    private static String preProcess(String data){
        //delete all '\r' characters
        return data.replaceAll("\r", "");
    }

    private PGNFile(){
    }

    public static PGNFile parsePGN(Context context, String path, Board board) throws PGNParseException, IOException {
        StringBuilder builder = new StringBuilder();
        String curLine = null;
        final int BUFFER_SIZE = 1024;
        char[] buffer = new char[BUFFER_SIZE];
        int count = -1;
        final InputStream is = context.getAssets().open(path);
        InputStreamReader reader = new InputStreamReader(is);
        PGNFile pgnFile = new PGNFile();

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

        pgnFile.data = builder.toString();
        pgnFile.data = preProcess(pgnFile.data);

        String[] splitResult = pgnFile.data.split("\n\n");

        if(splitResult.length != 2) {
            throw new PGNParseException("Invalid PGN file");
        }

        String metaStr = splitResult[0];
        String movesStr = splitResult[1];

        pgnFile.parseMeta(metaStr);

        //convert ".\s*" to ". "
        movesStr = movesStr.replaceAll("\\.\\s*", ". ");
        pgnFile.parseMoves(movesStr);
        return pgnFile;
    }

    private void parseMoves(String movesStr) throws PGNParseException {
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
                    if(curStr.equals("1-0") || curStr.equals("0-1") || curStr.equals("1/2-1/2")){
                        break;
                    }

                    throw new PGNParseException("Invalid PGN file");
                }

                if(index != i/3 + 1) throw new PGNParseException("Index error");
            }else if(i%3 == 1){
                move = new Move();
                move.white = curStr;
            }else{
                move.black = curStr;
                moves.add(move);
            }
        }
    }

    private void parseMeta(String metaStr) throws PGNParseException {
        String[] metaLines = metaStr.split("\n");
        for(String line : metaLines){
            line = line.trim();

            try{
                line = line.substring(1, line.length()-1);
                String[] splitResult = line.split("\\s+\"");
                if(splitResult.length != 2) throw new PGNParseException("Invalid PGN file");
                meta.put(splitResult[0].trim(), splitResult[1].trim().substring(0, splitResult[1].length()-1));
            }catch (StringIndexOutOfBoundsException e){
                throw new PGNParseException("Invalid PGN file");
            }
        }
    }

    public String getTitle(){
        return meta.get("Event") + " " + meta.get("Date") + " " + meta.get("Round");
    }
}
