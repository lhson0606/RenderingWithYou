package com.dy.app.core.thread;

import android.opengl.GLSurfaceView;

import com.dy.app.core.GameCore;
import com.dy.app.core.GameEntity;
import com.dy.app.graphic.display.GameSurface;
import com.dy.app.manager.EntityManger;

public class GameLoop extends Thread{
    private boolean isRunning = false;
    private GameSurface surfaceView;
    private final EntityManger entityManger = EntityManger.getInstance();

    public GameLoop(GameSurface surfaceView){
        this.surfaceView = surfaceView;
    }

    @Override
    public void run(){
        isRunning = true;
        Thread testThread = new Thread(()->{
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            shutDown();
        });
        testThread.start();
        while(isRunning){
            draw();
            update();
        }
        cleanUp();
    }


    private void update(){
        for(GameEntity e: entityManger.getEntities()){
            e.update(1.0f/60);
        }
        entityManger.releaseMutex();
    }

    private void draw(){
        try {
            Thread.sleep(1000/60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        surfaceView.requestRender();
    }

    private void cleanUp(){
        entityManger.cleanUp();
    }

    public void shutDown(){
        isRunning = false;
    }
}
