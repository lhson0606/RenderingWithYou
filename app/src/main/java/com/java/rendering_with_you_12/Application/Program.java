package com.java.rendering_with_you_12.Application;

import android.content.Context;

import com.java.rendering_with_you_12.Model.TexturedCube;
import com.java.rendering_with_you_12.Model.Triangle2DEle;
import com.java.rendering_with_you_12.renderEngine.MyRenderer;

public class Program extends GLBasicSurfaceView{

    public Program(Context context) {
        super(context);
    }

    public void init(){
        super.init();

        TexturedCube cube;

        float[] mVerticesData =
                {
                        -0.5f,0.5f,-0.5f,
                        -0.5f,-0.5f,-0.5f,
                        0.5f,-0.5f,-0.5f,
                        0.5f,0.5f,-0.5f,

                        -0.5f,0.5f,0.5f,
                        -0.5f,-0.5f,0.5f,
                        0.5f,-0.5f,0.5f,
                        0.5f,0.5f,0.5f,

                        0.5f,0.5f,-0.5f,
                        0.5f,-0.5f,-0.5f,
                        0.5f,-0.5f,0.5f,
                        0.5f,0.5f,0.5f,

                        -0.5f,0.5f,-0.5f,
                        -0.5f,-0.5f,-0.5f,
                        -0.5f,-0.5f,0.5f,
                        -0.5f,0.5f,0.5f,

                        -0.5f,0.5f,0.5f,
                        -0.5f,0.5f,-0.5f,
                        0.5f,0.5f,-0.5f,
                        0.5f,0.5f,0.5f,

                        -0.5f,-0.5f,0.5f,
                        -0.5f,-0.5f,-0.5f,
                        0.5f,-0.5f,-0.5f,
                        0.5f,-0.5f,0.5f

                };

        int[] mIndicesData =
                {
                        0,1,3,
                        3,1,2,
                        4,5,7,
                        7,5,6,
                        8,9,11,
                        11,9,10,
                        12,13,15,
                        15,13,14,
                        16,17,19,
                        19,17,18,
                        20,21,23,
                        23,21,22
                };

        float[] texCoords=
                {
                        0,0,
                        0,1,
                        1,1,
                        1,0,
                        0,0,
                        0,1,
                        1,1,
                        1,0,
                        0,0,
                        0,1,
                        1,1,
                        1,0,
                        0,0,
                        0,1,
                        1,1,
                        1,0,
                        0,0,
                        0,1,
                        1,1,
                        1,0,
                        0,0,
                        0,1,
                        1,1,
                        1,0

                };

        cube= new TexturedCube(m_Context, mVerticesData, mIndicesData, texCoords);

        m_Renderer.render(cube);
    }

}
