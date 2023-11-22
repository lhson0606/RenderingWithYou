package com.dy.app.manager;


import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.piece.Piece;

import org.w3c.dom.Entity;

import java.util.Vector;
import java.util.concurrent.Semaphore;

public class EntityManger {

    private Vector<GameEntity> entities;
    private final Semaphore mutex = new Semaphore(1);

    public void cleanUp(){
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(GameEntity e: entities){
            e.destroy();
        }
        entities.clear();
        mutex.release();
    }

    public static synchronized EntityManger getInstance(){
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
    }

    private static EntityManger instance = null;

    public synchronized Vector<GameEntity> getEntities() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return entities;
    }

    public void releaseMutex() {
        mutex.release();
    }
}
