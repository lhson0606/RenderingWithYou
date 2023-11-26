package com.dy.app.manager;


import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.render.DyRenderer;

import org.w3c.dom.Entity;

import java.util.Vector;
import java.util.concurrent.Semaphore;

public class EntityManger {

    private final Vector<GameEntity> entities = new Vector<>();
    private final Semaphore mutex = new Semaphore(1);
    private final Vector<GameEntity> removeList = new Vector<>();
    private DyRenderer renderer;

    public void cleanUp(){
        try {
            mutex.acquire();
            for(GameEntity e: entities){
                e.destroy();
            }
            for(GameEntity e: removeList){
                e.destroy();
            }
            entities.clear();
            removeList.clear();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            mutex.release();
        }
    }

    public GameEntity newEntity(GameEntity entity){
        try {
            mutex.acquire();
            if(entities.contains(entity))
                return entity;
            entities.add(entity);
            return entity;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    public EntityManger(){
    }

    public void initEntities(){
        try {
            mutex.acquire();
            for(GameEntity e: entities){
                e.init();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    public void updateEntities(float dt){
        try {
            mutex.acquire();
            for(GameEntity e: entities){
                e.update(dt);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    public void drawEntities(){
        try {
            mutex.acquire();
            for(GameEntity e: entities){
                e.draw();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    public void removeEntity(GameEntity entity){
        try {
            mutex.acquire();
            entities.remove(entity);
            removeList.add(entity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    public void destroyEntity(GameEntity entity){
        try {
            mutex.acquire();
            entities.remove(entity);
            entity.destroy();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    public void setRenderer(DyRenderer renderer){
        this.renderer = renderer;
    }

    public void addAndInitSingleEntity(GameEntity entity){
        try {
            mutex.acquire();
            entity.init();
            entities.add(entity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    public DyRenderer getRenderer(){
        return renderer;
    }
}
