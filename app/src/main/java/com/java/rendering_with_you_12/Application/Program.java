package com.java.rendering_with_you_12.Application;

import android.content.Context;

import com.java.rendering_with_you_12.Model.Triangle2DEle;
import com.java.rendering_with_you_12.renderEngine.MyRenderer;

public class Program extends GLBasicSurfaceView{

    public Program(Context context) {
        super(context);
    }

    public void init(){
        super.init();
        Triangle2DEle trig;
        Triangle2DEle trig2;

        float[] mVerticesData =
                {
                        0.0f,  0.5f, 0.0f,        // v0
                        -0.5f, -0.5f, 0.0f,        // v1
                        0.5f, -0.5f, 0.0f,        // v2
                };

        float[] mVerticesData2 =
                {
                        0.5f,  1f, 0.5f,        // v0
                        0f, -0f, 0.5f,        // v1
                        1f, 0f, 0.5f,        // v2
                };

        float[] mColorData = {
                1.0f,  0.0f, 0.0f, 1.0f,  // c0
                0.0f,  1.0f, 0.0f, 1.0f,  // c1
                0.0f,  0.0f, 1.0f, 1.0f,  // c2
        };

        int[] mIndicesData =
                {
                        0, 1, 2
                };

        trig= new Triangle2DEle(m_Context, mVerticesData, mIndicesData, mColorData);
        trig2 = new Triangle2DEle(m_Context, mVerticesData2, mIndicesData, mColorData);

        m_Renderer.render(trig);
        m_Renderer.render(trig2);
    }

}
