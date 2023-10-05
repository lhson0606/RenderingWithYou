package com.dy.startinganimation.animation;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.dy.startinganimation.camera.Camera;
import com.dy.startinganimation.parser.AnimParser;
import com.dy.startinganimation.parser.Scene;
import com.example.startinganimation.R;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.xml.parsers.ParserConfigurationException;

public class AnimRenderer implements GLSurfaceView.Renderer {

    Animator animator;
    void init(){

        final String file = "models/model/model.dae";

        try {
            Scene scene = AnimParser.parse(mContext, mContext.getAssets().open(file));
            animator = scene.createAnimator(mContext);
            animator.init();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

    }

    public AnimRenderer(Context context){
        mContext = context;
    }
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES30.glClearColor ( 34, 130, 227, 1 );
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    }

    public static Camera camera;
    boolean isInit = false;
    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        if(!isInit){
            camera = new Camera(w, h);
        }
        camera.updateViewMatrix();
        init();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear ( GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        camera.updateViewMatrix();
        animator.update(0.0002f);
        animator.draw();
    }

    private Context mContext;
}