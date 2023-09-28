package com.java.rendering_with_you_12.Application;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.java.rendering_with_you_12.Model.Entity;
import com.java.rendering_with_you_12.renderEngine.Camera;
import com.java.rendering_with_you_12.renderEngine.MyRenderer;
import com.java.rendering_with_you_12.utils.ScaleListener;

import java.util.ArrayList;
import java.util.List;

public class GLBasicSurfaceView extends GLSurfaceView {

    protected final MyRenderer m_Renderer;
    protected boolean m_IsRunning = false;
    protected List<Entity> m_Entities = new ArrayList<>();
    public  Camera m_Camera = new Camera();
    //public final float[] m_MVPMat = new float[16];
    protected Context m_Context;
    public GLBasicSurfaceView(Context context){
        super(context);
        m_Context = context;
        setEGLContextClientVersion(3);
        m_Renderer = new MyRenderer(this);
        setRenderer(m_Renderer);
        m_ScaleListener = new ScaleListener(m_Camera);
        m_ScaleDetector = new ScaleGestureDetector(context, m_ScaleListener);
    }

    public void init(){};

    float preX;
    float preY;
    boolean isOnScale = false;
    ScaleGestureDetector m_ScaleDetector;
    ScaleListener m_ScaleListener;

    @Override
    public boolean onTouchEvent(MotionEvent e){
        m_ScaleDetector.onTouchEvent(e);
        if(m_ScaleListener.isOnScale){
            return true;
        }

        float x = e.getX();
        float y = e.getY();

        if(isOnScale) return true;

        switch(e.getAction()){
            case MotionEvent.ACTION_MOVE:

                m_Renderer.dy = y-preY;
                m_Renderer.dx = x-preX;
                m_Camera.move(-(x-preX), y-preY);

                break;
        }

        preX = x;
        preY = y;
        return true;
    }
}
