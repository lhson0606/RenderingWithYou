package com.dy.app.core.thread;

import android.opengl.GLSurfaceView;

import com.dy.app.core.GameCore;
import com.dy.app.core.GameEntity;
import com.dy.app.graphic.display.GameSurface;
import com.dy.app.manager.EntityManger;

public class GameLoop extends Thread{
    private boolean isRunning = false;
    private GameSurface surfaceView;
    private final EntityManger entityManger;

    public GameLoop(GameSurface surfaceView, EntityManger entityManger){
        this.surfaceView = surfaceView;
        this.entityManger = entityManger;
    }

    @Override
    public void run(){
        isRunning = true;

        //surfaceView.getRenderer().waitForGLInit();

        while(isRunning){
            draw();
            update();
        }

        cleanUp();
    }


    private void update(){
        try {
            Thread.sleep(1000/120);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        entityManger.updateEntities(1.0f/120);
    }

    private void draw(){
        surfaceView.requestRender();
    }

    private void cleanUp(){
        entityManger.cleanUp();
    }

    public void shutDown(){
        isRunning = false;
    }
}
