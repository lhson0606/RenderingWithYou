package com.dy.app.core.thread;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScriptsRunner extends Thread{
    boolean isRunning = false;
    InputStream is;
    public static final String TAG = "ScriptsRunner";
    private BufferedReader reader;

    public ScriptsRunner(InputStream is){
        this.is = is;
        reader = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void run() {
        isRunning = true;
        String curLine = null;

        while(isRunning){
            try {
                if(reader.ready()){
                    curLine = reader.readLine();
                    if(curLine == null) continue;
                    Log.d(TAG, curLine);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        Log.d(TAG, " run: stopped");
    }

    public void shutDown(){
        isRunning = false;
    }
}
