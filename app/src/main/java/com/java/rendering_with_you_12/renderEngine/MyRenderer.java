package com.java.rendering_with_you_12.renderEngine;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.java.rendering_with_you_12.Application.GLBasicSurfaceView;
import com.java.rendering_with_you_12.Model.Entity;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {

    protected int m_Width;
    protected int m_Height;
    protected GLBasicSurfaceView m_Program;
    protected Camera m_Camera;
    public final float[] m_proMat = new float[16];

    public MyRenderer(GLBasicSurfaceView program){

        m_Program = program;
        m_Camera = program.m_Camera;
        Matrix.frustumM(m_proMat, 0, -0.496f, 0.496f, -1, 1, 3f, 50 );
    }

    public void render(Entity entity){
        entities.add(entity);
    }

    List<Entity> entities = new ArrayList<>();
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES30.glClearColor ( 34, 130, 227, 1 );
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        m_Program.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        m_Width = width;
        m_Height = height;
        GLES30.glViewport(0,0, m_Width, m_Height);
        float aspectRatio = (float)width/height;
        Matrix.frustumM(m_proMat, 0, -aspectRatio, aspectRatio, -1, 1, 3f, 50 );
    }
    float t = 0;
    public float dx =0f;
    public float dy =0f;

    void calculate(){
        float deltaT = 0.2f;
        m_Camera.update(deltaT);

    }
    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear ( GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        calculate();

        for(Entity entity : entities){
            entity.draw();
        }
    }

    public float[] getPproMat() {
        return m_proMat;
    }
}
