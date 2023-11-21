package com.dy.app.network;

import java.io.Serializable;

public class PlayerInitialInfo implements Serializable {
    private String name;

    public PlayerInitialInfo(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
