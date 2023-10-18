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


       /* try {
            test1 = ObjLoader.load("model/piece/knight.obj");
            Skin skin1 = AssetManger.getInstance().getSkin(AssetManger.SkinType.BLACK_TILE);
            Texture texture1 = new Texture(skin1.getBitmap());
            test1.setTex(texture1);
            test1.setMaterial(DyConst.default_material);
            test1.translate(Tile.getOffSet(0, 0));
            test1.init();

            test2 = ObjLoader.load("model/piece/queen.obj");
            Skin skin2 = AssetManger.getInstance().getSkin(AssetManger.SkinType.WHITE_TILE);
            Texture texture2 = new Texture(skin2.getBitmap());
            test2.setTex(texture2);
            test2.setMaterial(DyConst.default_material);
            test2.translate(Tile.getOffSet(7, 7));
            test2.init();
            test2.changeState(Obj3D.State.SOURCE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

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
        for(GameEntity e: EntityManger.getInstance().getEntities()){
            e.draw();
        }
        //test1.draw(Camera.getInstance().mViewMat, Camera.getInstance().mProjMat);
        //test2.draw(Camera.getInstance().mViewMat, Camera.getInstance().mProjMat);
    }
}
