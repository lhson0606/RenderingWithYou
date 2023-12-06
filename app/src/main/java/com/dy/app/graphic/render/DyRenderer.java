package com.dy.app.graphic.render;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.view.GestureDetector;

import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.board.Board;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.display.GameSurface;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.manager.EntityManger;
import com.dy.app.utils.GLHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DyRenderer implements android.opengl.GLSurfaceView.Renderer{
    private GameSurface gameSurface;
    private TilePicker tilePicker;
    private boolean pickerIsSet = false;
    private Board board;
    private EntityManger entityManger;
    private Bitmap screenShot = null;
    private Semaphore screenShotSem = new Semaphore(0);
    private boolean isScreenShotRequested = false;

    public DyRenderer(GameSurface gameSurface, EntityManger entityManger, Board board) {
        this.board = board;
        this.entityManger = entityManger;
        this.gameSurface = gameSurface;
        tilePicker = new TilePicker(0,0, board);
        gameSurface.setGestureDetector(new GestureDetector(gameSurface.getContext(), tilePicker));
        entityManger.setRenderer(this);
    }

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
        entityManger.initEntities();
        sem.release();
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
        entityManger.drawEntities();

        //after all entities are drawn, check if screenshot is requested
        if(isScreenShotRequested){
            screenShot = takeScreenShot();
            screenShotSem.release();
        }
    }

    private Bitmap takeScreenShot(){
        Bitmap bmp = Bitmap.createBitmap(gameSurface.getWidth(), gameSurface.getHeight(), Bitmap.Config.ARGB_8888);
        int[] viewPort = new int[4];
        GLES30.glGetIntegerv(GLES30.GL_VIEWPORT, viewPort, 0);
        int width = gameSurface.getWidth();
        int height = gameSurface.getHeight();
        IntBuffer intBuffer = IntBuffer.allocate(width * height);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
        int[] pixelArray = intBuffer.array();

        // Convert the pixel data to the Bitmap, we need to flip the data, or it will be upside down
        for (int i = 0; i < height / 2; i++) {
            for (int j = 0; j < width; j++) {
                int temp = pixelArray[i * width + j];
                pixelArray[i * width + j] = pixelArray[(height - 1 - i) * width + j];
                pixelArray[(height - 1 - i) * width + j] = temp;
            }
        }
        bmp.copyPixelsFromBuffer(IntBuffer.wrap(pixelArray));
        return bmp;
    }

    public void addAndInitEntityGL(GameEntity entity, OnEntityAdded onEntityAdded){
        gameSurface.queueEvent(() -> {
            entityManger.addAndInitSingleEntity(entity);
            onEntityAdded.onEntityAdded();
        });
    }

    public interface OnEntityAdded{
        void onEntityAdded();
    }

    Semaphore sem = new Semaphore(0);

    public void waitForGLInit(){
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    interface OnGLInitiated{
        void onGLInitiated();
    }

    public TilePicker getTilePicker() {
        return tilePicker;
    }

    //only one thread can request screenshot at a time
    public synchronized Bitmap getScreenShot() throws InterruptedException {
        isScreenShotRequested = true;
        screenShotSem.acquire();
        isScreenShotRequested = false;
        return screenShot;
    }
}
