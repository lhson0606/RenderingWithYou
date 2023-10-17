package com.dy.app.manager;


import com.dy.app.core.GameEntity;

import java.util.Vector;

public class EntityManger {

    private Vector<GameEntity> entities;

    public static EntityManger getInstance(){
        return instance = (instance == null) ? new EntityManger() : instance;
    }

    public GameEntity newEntity(GameEntity e){
        entities.add(e);
        return e;
    }

    private EntityManger(){
        entities = new Vector<>();
    }

    private static EntityManger instance = null;

    public Vector<GameEntity> getEntities() {
        return entities;
    }
}
