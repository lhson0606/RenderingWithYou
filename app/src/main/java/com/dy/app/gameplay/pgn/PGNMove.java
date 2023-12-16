package com.dy.app.gameplay.pgn;

import java.io.Serializable;

public class PGNMove implements Serializable{
    public PGNMove(String white, String black){
        this.white = white;
        this.black = black;
    }

    public PGNMove(){
        this.white = "";
        this.black = "";
    }

    public String white = "";
    public String black = "";
}
