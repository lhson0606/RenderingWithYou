package com.dy.app.gameplay.pgn;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dy.app.activity.MainActivity;
import com.dy.app.core.thread.ScriptsRunner;
import com.dy.app.gameplay.board.Board;
import com.dy.app.utils.DyConst;
import com.dy.app.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class PGNFile implements Serializable {
    private final Vector<PGNMove> moves = new Vector<>();
    private String data;
    private final Map<String, String> meta = new HashMap<>();
    public static final String TAG = "PGNFile";

    public static PGNFile parsePGN(MainActivity context, Uri openingPGNuri) throws PGNParseException {
        StringBuilder builder = new StringBuilder();
        String curLine = null;
        final int BUFFER_SIZE = 1024;
        char[] buffer = new char[BUFFER_SIZE];
        int count = -1;
        final InputStream is;
        InputStreamReader reader;
        PGNFile pgnFile = new PGNFile();

        try {
            is = context.getContentResolver().openInputStream(openingPGNuri);
            reader = new InputStreamReader(is);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while(true){
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
            throw new RuntimeException("Invalid PGN file");
        }

        String metaStr = splitResult[0];
        String movesStr = splitResult[1];

        pgnFile.parseMeta(metaStr);

        //convert ".\s*" to ". "
        movesStr = movesStr.replaceAll("\\.\\s*", ". ");
        pgnFile.parseMoves(movesStr);
        return pgnFile;
    }

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

    public Vector<PGNMove> getMoves() {
        return moves;
    }

    public PGNFile(String moveData){
        //parse move data
        try {
            parseMoves(moveData);
        } catch (PGNParseException e) {
            throw new RuntimeException(e);
        }

        //put initial meta data
        addEvent("?");
        addSite("DyChess 1.0");
        addDate("??.??.??");
        addRound("?");
        addResult(-1);
        addWhitePlayer("?");
        addBlackPlayer("?");
        addECO("?");
        addWhiteElo("?");
        addBlackElo("?");
        addPlyCount("?");
    }

    private static String preProcess(String data){
        //delete all '\r' characters
        return data.replaceAll("\r", "");
    }

    private PGNFile(){
    }

    public static PGNFile parsePGN(String data) throws PGNParseException {
        PGNFile pgnFile = new PGNFile();
        pgnFile.data = preProcess(data);
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

    public static PGNFile parsePGN(Context context, String path) throws PGNParseException, IOException {
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
        if(movesStr.length() == 0) return;
        String[] splitResults = movesStr.split("\\s+");
        PGNMove move = null;

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
                move = new PGNMove();
                move.white = curStr;
            }else{
                move.black = curStr;
                moves.add(move);
                move = null;
            }
        }

        if(move != null) moves.add(move);
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

    public void addEvent(String event){
        putMeta("Event", event);
    }

    public void addSite(String site){
        putMeta("Site", site);
    }

    public void addDate(String date){
        putMeta("Date", date);
    }

    public void addRound(String round){
        putMeta("Round", round);
    }

    public void addResult(int result){
        switch (result){
            case DyConst.GAME_DRAW:
                putMeta("Result", "1/2-1/2");
                break;
            case DyConst.GAME_WHITE_WIN:
                putMeta("Result", "1-0");
                break;
            case DyConst.GAME_BLACK_WIN:
                putMeta("Result", "0-1");
                break;
            default:
                putMeta("Result", "*");
                break;
        }
    }

    public void addWhitePlayer(String whitePlayer){
        putMeta("White", whitePlayer);
    }

    public void addBlackPlayer(String blackPlayer){
        putMeta("Black", blackPlayer);
    }

    public void addECO(String eco){
        putMeta("ECO", eco);
    }

    public void addWhiteElo(String whiteElo){
        putMeta("WhiteElo", whiteElo);
    }

    public void addBlackElo(String blackElo){
        putMeta("BlackElo", blackElo);
    }

    public void addPlyCount(String plyCount){
        putMeta("PlyCount", plyCount);
    }

    private void putMeta(String key, String value){
        meta.put(key, value);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //meta data
        for(Map.Entry<String, String> entry : meta.entrySet()){
            builder.append("[" + entry.getKey() + " \"" + entry.getValue() + "\"]\n");
        }

        builder.append("\n\n");

        //moves
        for(int i = 0; i < moves.size(); i++){
            if(i%MOVE_PER_LINE == 0) builder.append("\n");

            PGNMove move = moves.get(i);
            builder.append((i+1) + ". " + move.white + " " + move.black + " ");
        }

        return builder.toString().replaceAll("\\n{3,}", "\n\n");
    }

    public interface IOnSavePGNListener{
        void onSavePGN(String path);
    }

    public void savePGN(Context context, Uri uri, IOnSavePGNListener listener) throws IOException {
        String data = toString();
        ParcelFileDescriptor pfd = context.getContentResolver().
                openFileDescriptor(uri, "w");
        FileOutputStream fileOutputStream =
                new FileOutputStream(pfd.getFileDescriptor());
        fileOutputStream.write(data.getBytes());
        // Let the document provider know you're done by closing the stream.
        fileOutputStream.close();
        pfd.close();
        //get file from uri
        listener.onSavePGN(uri.getPath());
    }

    public static int MOVE_PER_LINE = 6;
    public int getGameResult(){
        String result = meta.get("Result");
        if(result.equals("1/2-1/2")) return DyConst.GAME_DRAW;
        if(result.equals("1-0")) return DyConst.GAME_WHITE_WIN;
        if(result.equals("0-1")) return DyConst.GAME_BLACK_WIN;
        return DyConst.GAME_NOT_END;
    }

    public String getGameDate() {
        return meta.get("Date");
    }

    public String getEvent(){
        return meta.get("Event");
    }

    public int getBothSideMoveCount(){
        if(moves.size() == 0) return 0;
        int result = moves.size()*2;
        if(moves.lastElement().black.equals("")) result--;
        return result;
    }
}
