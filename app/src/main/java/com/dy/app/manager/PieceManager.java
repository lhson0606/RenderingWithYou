package com.dy.app.manager;

public class PieceManager {
    public static PieceManager getInstance(){
        return instance = (instance == null) ? new PieceManager() : instance;
    }

    private static PieceManager instance = null;

    public void init(){

    }

    public void destroy(){

    }
}
