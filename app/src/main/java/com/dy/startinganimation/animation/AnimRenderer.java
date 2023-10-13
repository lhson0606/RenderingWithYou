package com.dy.startinganimation.animation;

import android.content.Context;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES31Ext;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;

import com.dy.startinganimation.activities.DyGLSurfaceView;
import com.dy.startinganimation.camera.Camera;
import com.dy.startinganimation.parser.AnimParser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.xml.parsers.ParserConfigurationException;

public class AnimRenderer implements GLSurfaceView.Renderer {

    Vector<Animator> animators;
    Vector<Animator> unInitAnimators;
    private float mPlaySpeed = 0.015f;
    void init(){

        for(Animator animator : animators){

            if(!animator.isIsInitialized()){
                animator.init();
            }

        }

    }

    public AnimRenderer(Context context){
        mContext = context;
        animators = new Vector<Animator>();
        unInitAnimators = new Vector<Animator>();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        init();
        GLES30.glClearColor ( 34, 130, 227, 1 );
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    }

    boolean isInit = false;
    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        Camera.getInstance().getInstance().setWidth(w);
        Camera.getInstance().getInstance().setHeight(h);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear ( GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        Camera.getInstance().getInstance().update();

        for(Animator animator : unInitAnimators){
            animator.init();
            animators.add(animator);
        }

        unInitAnimators.clear();

        for(Animator animator : animators){
            animator.update(mPlaySpeed);
            animator.draw();
        }

    }

    private Context mContext;

    public void render(Animator animator) {
        /*if(!animator.isIsInitialized()){
            animator.init();
        }*/
        unInitAnimators.add(animator);
    }

    public void destroy(){
        for(Animator animator : animators){
            animator.destroy();
        }
    }

    public void setDrawMode(int drawMode) {
        for(Animator animator : animators){
            animator.setDrawMode(drawMode);
        }
    }

    public void setPlaySpeed(float playSpeed) {
        mPlaySpeed = playSpeed;
    }
}
