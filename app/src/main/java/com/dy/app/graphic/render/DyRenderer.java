package com.dy.app.graphic.render;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.view.GestureDetector;

import com.dy.app.common.maths.Mat4;
import com.dy.app.core.GameEntity;
import com.dy.app.gameplay.board.Board;
import com.dy.app.gameplay.board.Tile;
import com.dy.app.gameplay.piece.Piece;
import com.dy.app.graphic.camera.Camera;
import com.dy.app.graphic.display.GameSurface;
import com.dy.app.graphic.listener.TilePicker;
import com.dy.app.graphic.model.Obj3D;
import com.dy.app.graphic.shader.Obj3DShader;
import com.dy.app.graphic.shader.ShaderHelper;
import com.dy.app.graphic.shader.ShadowMapShader;
import com.dy.app.graphic.shadow.ShadowFrameBuffer;
import com.dy.app.graphic.shadow.ShadowMapTechnique;
import com.dy.app.manager.EntityManger;
import com.dy.app.setting.GameSetting;
import com.dy.app.utils.GLHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DyRenderer implements android.opengl.GLSurfaceView.Renderer{
    private GameSurface gameSurface;
    private TilePicker tilePicker;
    private boolean pickerIsSet = false;
    private EntityManger entityManger;
    private Bitmap screenShot = null;
    private Semaphore screenShotSem = new Semaphore(0);
    private boolean isScreenShotRequested = false;
    private ShadowFrameBuffer shadowFbo = new ShadowFrameBuffer();
    private ShadowMapShader shadowMapShader;
    private final ShadowMapTechnique shadowMapTechnique = new ShadowMapTechnique(GameSetting.getInstance().getLight());
    private Board board;
    public DyRenderer(GameSurface gameSurface, EntityManger entityManger, Board board) {
        this.entityManger = entityManger;
        this.gameSurface = gameSurface;
        this.board = board;
        tilePicker = new TilePicker(0,0, board);
        gameSurface.setGestureDetector(new GestureDetector(gameSurface.getContext(), tilePicker));
        entityManger.setRenderer(this);
        try {
            shadowMapShader = new ShadowMapShader(
                    ShaderHelper.getInstance().readShader(gameSurface.getContext().getAssets().open(ShadowMapTechnique.SHADOW_MAP_VER)),
                    ShaderHelper.getInstance().readShader(gameSurface.getContext().getAssets().open(ShadowMapTechnique.SHADOW_MAP_FRAG)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Vector<Tile> tileList = board.getAllTiles();

        for(Tile t: tileList){
            t.setShadowFrameBuffer(shadowFbo);
            t.setShadowMapTechnique(shadowMapTechnique);
        }
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
        shadowFbo.init(gameSurface.getWidth(), gameSurface.getHeight());
        shadowMapShader.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        GLES30.glViewport ( 0, 0, w, h );
        Camera.getInstance().getInstance().setWidth(w);
        Camera.getInstance().getInstance().setHeight(h);
        tilePicker.setScreenSize(w, h);
        if(!pickerIsSet) gameSurface.setOnTouchListener(tilePicker);
        shadowFbo.cleanUp();
        shadowFbo.init(w,h);
        shadowMapTechnique.updateView(w,h);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        shadowPass();
        lightPass();
        //after all entities are drawn, check if screenshot is requested
        if(isScreenShotRequested){
            screenShot = takeScreenShot();
            screenShotSem.release();
        }
    }

    private void lightPass(){
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glViewport ( 0, 0, gameSurface.getWidth(), gameSurface.getHeight() );
        GLES30.glClear ( GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        entityManger.drawEntities();
    }

    private void shadowPass(){
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glViewport ( 0, 0, shadowFbo.getWidth(), shadowFbo.getHeight());
        //user shadow map program
        shadowMapShader.start();
        Vector<Piece> pieceList = board.getPieceManager().getActivePieces();
        Vector<Tile> tileList = board.getAllTiles();
        Vector<Obj3D> objList = new Vector<>(33);

        for(Piece p: pieceList){
            objList.add(p.getObj());
        }

        for(Tile t: tileList){
            objList.add(t.getObj());
        }

        //binding fbo as the draw target
        shadowFbo.bindForWriting();
        //draw to shadow buffer
        for(Obj3D obj:objList){
            Mat4 lightTransform = shadowMapTechnique.getLightTransformMatrix();
            Mat4 modelMat = obj.getModelMat();
            Mat4 gMVP = lightTransform.multiplyMM(modelMat);
            shadowMapShader.loadMvp(gMVP);
            obj.getVAO().bind();
            GLES30.glEnableVertexAttribArray(Obj3DShader.VERTEX_INDEX);
            GLES30.glDrawElements(GameSetting.getInstance().getDrawMode(),
                    obj.getEBOIndices().length(),
                    obj.getEBOIndices().getType(), 0);
            obj.getVAO().unbind();
        }

        shadowMapShader.stop();
        //stop shadow map program
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
