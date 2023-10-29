package com.dy.app.graphic.render;

import android.opengl.GLES30;
import android.view.GestureDetector;

import com.dy.app.core.GameEntity;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.display.GameSurface;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.manager.EntityManger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DyRenderer implements android.opengl.GLSurfaceView.Renderer{
    private GameSurface gameSurface;
    private TilePicker tilePicker;
    private boolean pickerIsSet = false;

    public DyRenderer(GameSurface gameSurface) {
        this.gameSurface = gameSurface;
        tilePicker = new TilePicker(0,0);
        gameSurface.setGestureDetector(new GestureDetector(gameSurface.getContext(), tilePicker));
    }

    //private Obj3D test1 = null;
    //private Obj3D test2 = null;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        init();
        GLES30.glClearColor ( 255, 255, 255, 1 );
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
        gl.glFrontFace(GL10.GL_CCW);

    }

    private void init() {
        for(GameEntity e: EntityManger.getInstance().getEntities()){
            e.init();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        GLES30.glViewport ( 0, 0, w, h );
        Camera.getInstance().getInstance().setWidth(w);
        Camera.getInstance().getInstance().setHeight(h);
        tilePicker.setScreenSize(w, h);
        if(!pickerIsSet) gameSurface.setOnTouchListener(tilePicker);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear ( GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

//        for(GameEntity e: EntityManger.getInstance().getEntities()){
//            e.update(0f);
//        }

        for(GameEntity e: EntityManger.getInstance().getEntities()){
            e.draw();
        }
    }
}
