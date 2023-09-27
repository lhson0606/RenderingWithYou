package com.java.rendering_with_you_12.Model;

import android.content.Context;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.shader.ShaderHelper;
import com.java.rendering_with_you_12.utils.GLHelper;

import java.io.IOException;
import java.io.InputStream;

public class Triangle2DEle implements Entity{

    //positions data
    float[] m_PositionData;
    //indices data
    int[] m_IndicesData;
    float[] m_ColorData;

    int m_Program;

    public static final String TAG = "Triangle2DEle";

    private static final String VERTEX_SHADER_PATH = "dlsl/Triangle2DEle/vertexShaderCode.txt";
    private static final String FRAGMENT_SHADER_PATH = "dlsl/Triangle2DEle/fragmentShaderCode.txt";

    int m_VAOIDs[] = new int[1];
    int m_VBOIDs[] = new int[3];

    final int VERTEX_ATTRIB_INDEX = 0;
    final int COLOR_ATTRIB_INDEX = 1;
    int m_UniMVPMatIndx;

    public Triangle2DEle(Context context){
        m_PositionData = DEFAULT_VERTICES_DATA;
        m_IndicesData = DEFAULT_INDICES_DATA;
        m_ColorData = DEFAULT_COLOR_DATA;

        InputStream vertexShaderStream;
        InputStream fragmentShaderStream;
        try {
            vertexShaderStream = context.getAssets().open(VERTEX_SHADER_PATH);
            fragmentShaderStream = context.getAssets().open(FRAGMENT_SHADER_PATH);
            m_Program = ShaderHelper.getInstance().loadProgram(vertexShaderStream, fragmentShaderStream);
        } catch (IOException e) {
            GLHelper.handleException(TAG, e);
        }

        GLES30.glGenVertexArrays(1, m_VAOIDs, 0);
        GLES30.glGenBuffers(3, m_VBOIDs, 0);

        GLES30.glBindVertexArray(m_VAOIDs[0]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VBOIDs[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                m_PositionData.length*Float.BYTES,
                GLHelper.createFloatBuffer(m_PositionData),
                GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(VERTEX_ATTRIB_INDEX, 3,
                GLES30.GL_FLOAT,false, 12, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VBOIDs[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                m_ColorData.length*Float.BYTES,
                GLHelper.createFloatBuffer(m_ColorData),
                GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(COLOR_ATTRIB_INDEX, 4,
                GLES30.GL_FLOAT,false, 16, 0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, m_VBOIDs[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                m_IndicesData.length*Integer.BYTES,
                GLHelper.createIntBuffer(m_IndicesData),
                GLES30.GL_STATIC_DRAW);

        GLES30.glEnableVertexAttribArray(VERTEX_ATTRIB_INDEX);
        GLES30.glEnableVertexAttribArray(COLOR_ATTRIB_INDEX);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);
        //this has to be below  GLES30.glBindVertexArray(0); IF THIS IS ABOVE then we are telling openGL that we do not use our indices
        //watch here https://youtu.be/45MIykWJ-C4?si=XvZCooyk1Pc6Dw81&t=1913
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);

        getAllUniLocations();
    }

    public Triangle2DEle(Context context, float[] positionsData, int[] indicesData, float[] colorData){
        m_PositionData = positionsData;
        m_IndicesData = indicesData;
        m_ColorData = colorData;

        InputStream vertexShaderStream;
        InputStream fragmentShaderStream;
        try {
            vertexShaderStream = context.getAssets().open(VERTEX_SHADER_PATH);
            fragmentShaderStream = context.getAssets().open(FRAGMENT_SHADER_PATH);
            m_Program = ShaderHelper.getInstance().loadProgram(vertexShaderStream, fragmentShaderStream);
        } catch (IOException e) {
            GLHelper.handleException(TAG, e);
        }

        GLES30.glGenVertexArrays(1, m_VAOIDs, 0);
        GLES30.glGenBuffers(3, m_VBOIDs, 0);

        GLES30.glBindVertexArray(m_VAOIDs[0]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VBOIDs[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                positionsData.length*Float.BYTES,
                GLHelper.createFloatBuffer(positionsData),
                GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(VERTEX_ATTRIB_INDEX, 3,
                GLES30.GL_FLOAT,false, 12, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VBOIDs[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                colorData.length*Float.BYTES,
                GLHelper.createFloatBuffer(colorData),
                GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(COLOR_ATTRIB_INDEX, 4,
                GLES30.GL_FLOAT,false, 16, 0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, m_VBOIDs[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                indicesData.length*Integer.BYTES,
                GLHelper.createIntBuffer(indicesData),
                GLES30.GL_STATIC_DRAW);

        GLES30.glEnableVertexAttribArray(VERTEX_ATTRIB_INDEX);
        GLES30.glEnableVertexAttribArray(COLOR_ATTRIB_INDEX);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);
        //this has to be below  GLES30.glBindVertexArray(0); IF THIS IS ABOVE then we are telling openGL that we do not use our indices
        //watch here https://youtu.be/45MIykWJ-C4?si=XvZCooyk1Pc6Dw81&t=1913
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);

        getAllUniLocations();
    }

    void getAllUniLocations(){
        m_UniMVPMatIndx = GLES30.glGetUniformLocation(m_Program, "uMVPMat");
    }

    @Override
    public void draw(float[] MVPMat) {
        GLES30.glUseProgram(m_Program);
        GLES30.glUniformMatrix4fv(m_UniMVPMatIndx, 1, false, MVPMat, 0);
        GLES30.glBindVertexArray(m_VAOIDs[0]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, m_IndicesData.length, GLES30.GL_UNSIGNED_INT, 0);
        GLES30.glUseProgram(0);
    }

    @Override
    public void destroy() {
        GLES30.glDeleteBuffers(0, m_VBOIDs, m_VBOIDs.length);
        GLES30.glDeleteVertexArrays(0, m_VAOIDs, m_VAOIDs.length);
        GLES30.glDeleteProgram(m_Program);
    }

    final float[] DEFAULT_VERTICES_DATA = {
             0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
             0.5f, -0.5f, 0.0f
    };
    //indices data
    int[] DEFAULT_INDICES_DATA ={
            0, 1, 2
    };
    float[] DEFAULT_COLOR_DATA ={
            1.0f, 0.0f, 0.0f, 1.0f,//red
            0.0f, 1.0f, 0.0f, 1.0f,//green
            0.0f, 0.0f, 1.0f, 1.0f//blue
    };
}
