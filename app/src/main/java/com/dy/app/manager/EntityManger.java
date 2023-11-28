package com.dy.app.manager;


import android.util.Log;

import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.render.DyRenderer;

import org.w3c.dom.Entity;

import java.util.Vector;
import java.util.concurrent.Semaphore;

public class EntityManger {

    private final Vector<GameEntity> entities = new Vector<>();
    private final Semaphore mutex = new Semaphore(1, true);
    private DyRenderer renderer;

    public void cleanUp(){
        try {
            mutex.acquire();
            for(GameEntity e: entities){
                e.destroy();
            }
            entities.clear();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            mutex.release();
        }
    }

    public GameEntity newEntity(GameEntity entity){
        try {
            mutex.acquire();
            if(entities.contains(entity)){
                throw new RuntimeException("Entity already exists");
            }
            entities.add(entity);
            Log.d("GameEntity", "newEntity: " + entity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }

        return entity;
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
            if(!entities.contains(entity))
                throw new RuntimeException("Entity not found");
            entities.remove(entity);
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

    public void addAllEntities(Vector<Piece> allPieces) {
        try {
            mutex.acquire();
            for(Piece p: allPieces){
                if(entities.contains(p)){
                    throw new RuntimeException("Entity already exists");
                }
                entities.add(p);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }
}
