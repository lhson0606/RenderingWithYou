/*
package com.java.rendering_with_you_12.Model;

import android.content.Context;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.R;
import com.java.rendering_with_you_12.shader.ShaderHelper;
import com.java.rendering_with_you_12.utils.GLHelper;

import java.io.IOException;
import java.io.InputStream;

public class TexturedCube implements Entity {
    //positions data
    float[] m_PositionData;
    //indices data
    int[] m_IndicesData;
    float[] m_ColorData;
    float[] m_TexCoords;
    int m_TexPos;
    int m_Program;

    public static final String TAG = "TexturedCube";

    private static final String VERTEX_SHADER_PATH = "dlsl/TexturedCube/vertex.dles";
    private static final String FRAGMENT_SHADER_PATH = "dlsl/TexturedCube/fragment.dles";

    int m_VAOIDs[] = new int[1];
    int m_VBOIDs[] = new int[4];
    int m_TexID[] = new int[1];

    final int VERTEX_ATTRIB_INDEX = 0;
    final int TEX_COORD_ATTRIB_INDEX = 1;
    int m_V;

    public TexturedCube(Context context){
        m_PositionData = m_DefaultVerticesData;
        m_IndicesData = m_DefaultIndicesData;
        m_TexCoords = m_DefaultTexCoordsData;

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
                m_TexCoords.length*Float.BYTES,
                GLHelper.createFloatBuffer(m_DefaultTexCoordsData),
                GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(TEX_COORD_ATTRIB_INDEX, 2,
                GLES30.GL_FLOAT,false, 8, 0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, m_VBOIDs[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                m_IndicesData.length*Integer.BYTES,
                GLHelper.createIntBuffer(m_IndicesData),
                GLES30.GL_STATIC_DRAW);

        GLES30.glEnableVertexAttribArray(VERTEX_ATTRIB_INDEX);
        GLES30.glEnableVertexAttribArray(TEX_COORD_ATTRIB_INDEX);//tex coords

        GLES30.glBindVertexArray(0);

        getAllUniLocations();
        int texPos[] = {-1};
        m_TexID[0] = GLHelper.loadTexture(context, R.raw.android_logo, texPos);
        m_TexPos = texPos[0];
    }

    public TexturedCube(Context context, float[] positionsData, int[] indicesData, float[] texCoordData){
        m_PositionData = positionsData;
        m_IndicesData = indicesData;
        m_TexCoords = texCoordData;

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

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VBOIDs[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                positionsData.length*Float.BYTES,
                GLHelper.createFloatBuffer(positionsData),
                GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, m_VBOIDs[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                texCoordData.length*Float.BYTES,
                GLHelper.createFloatBuffer(texCoordData),
                GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, m_VBOIDs[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                indicesData.length*Integer.BYTES,
                GLHelper.createIntBuffer(indicesData),
                GLES30.GL_STATIC_DRAW);

        GLES30.glBindVertexArray(m_VAOIDs[0]);


        GLES30.glVertexAttribPointer(VERTEX_ATTRIB_INDEX, 3,
                GLES30.GL_FLOAT,false, 12, 0);


        GLES30.glVertexAttribPointer(TEX_COORD_ATTRIB_INDEX, 2,
                GLES30.GL_FLOAT,false, 8, 0);



        //GLES30.glEnableVertexAttribArray(VERTEX_ATTRIB_INDEX);
        //GLES30.glEnableVertexAttribArray(TEX_COORD_ATTRIB_INDEX);//tex coords

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);
        //this has to be below  GLES30.glBindVertexArray(0); IF THIS IS ABOVE then we are telling openGL that we do not use our indices
        //watch here https://youtu.be/45MIykWJ-C4?si=XvZCooyk1Pc6Dw81&t=1913
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);

        getAllUniLocations();

        int texPos[] = {-1};
        m_TexID[0] = GLHelper.loadTexture(context, R.raw.android_logo, texPos);
        m_TexPos = texPos[0];
    }

    @Override
    public void draw(float[] viewMat, float[] proMat) {
        GLES30.glUseProgram(m_Program);
        GLES30.glUniformMatrix4fv(m_UniMVPMatIndx, 1, false, MVPMat, 0);

        GLES30.glBindVertexArray(m_VAOIDs[0]);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,  m_TexID[0]);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, m_IndicesData.length, GLES30.GL_UNSIGNED_INT, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisable(GLES30.GL_TEXTURE_2D);
        GLES30.glUseProgram(0);
        GLES30.glBindVertexArray(0);
    }

    @Override
    public void destroy() {
        GLES30.glDeleteBuffers(0, m_VBOIDs, m_VBOIDs.length);
        GLES30.glDeleteTextures(0, m_TexID, m_TexID.length);
        GLES30.glDeleteVertexArrays(0, m_VAOIDs, m_VAOIDs.length);
        GLES30.glDeleteProgram(m_Program);
    }

    float[] m_DefaultVerticesData =
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

    int[] m_DefaultIndicesData =
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

    float[] m_DefaultTexCoordsData=
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
}
*/
