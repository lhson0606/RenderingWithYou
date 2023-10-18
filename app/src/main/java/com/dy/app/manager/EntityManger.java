package com.dy.app.manager;


import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.piece.Piece;

import org.w3c.dom.Entity;

import java.util.Vector;

public class EntityManger {

    private Vector<GameEntity> entities;
    private Vector<GameEntity> removeList = new Vector<>();

    public static EntityManger getInstance(){
        return instance = (instance == null) ? new EntityManger() : instance;
    }

    public GameEntity newEntity(GameEntity e){
        if(entities.contains(e))
            return e;
        entities.add(e);
        return e;
    }

    private EntityManger(){
        entities = new Vector<>();
        removeList = new Vector<>();
    }

    private static EntityManger instance = null;

    public synchronized Vector<GameEntity> getEntities() {

        entities.removeAll(removeList);
        removeList.clear();
        return entities;
    }

    public void removeEntity(GameEntity e) {
        removeList.add(e);
    }
}
