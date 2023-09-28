/*
package com.java.rendering_with_you_12.glClass;

import android.content.Context;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.Model.Entity;
import com.java.rendering_with_you_12.R;
import com.java.rendering_with_you_12.shader.Shader;
import com.java.rendering_with_you_12.shader.ShaderHelper;
import com.java.rendering_with_you_12.utils.GLHelper;

import java.io.IOException;
import java.io.InputStream;

public class TestingCube implements Entity {

    int m_TexPos;

    public static final String TAG = "TexturedCube";

    private static final String VERTEX_SHADER_PATH = "dlsl/TexturedCube/vertex.dles";
    private static final String FRAGMENT_SHADER_PATH = "dlsl/TexturedCube/fragment.dles";

    VAO m_VAO;
    VBO m_VBOVertices;
    VBO m_VBOTexCoords;
    EBO m_EBOIndices;
    int m_TexID[] = new int[1];
    Shader m_Shader;

    final int VERTEX_ATTRIB_INDEX = 0;
    final int TEX_COORD_ATTRIB_INDEX = 1;
    int m_UniMVPMatIndx;

    public TestingCube(Context context){
        //create shader
        String verCode;
        String fragCode;
        try {
            verCode = ShaderHelper.getInstance().readShader(context.getAssets().open(VERTEX_SHADER_PATH));
            fragCode = ShaderHelper.getInstance().readShader(context.getAssets().open(FRAGMENT_SHADER_PATH));
            m_Shader = new Shader(verCode, fragCode);
        } catch (IOException e) {
            GLHelper.handleException(TAG, e);
        }

        //end of creating shader
        m_VAO = new VAO();
        m_VBOVertices = new VBO(m_DefaultVerticesData, 3, 3*4, false);
        m_VBOTexCoords = new VBO(m_DefaultTexCoordsData, 2, 2*4, false);
        m_EBOIndices = new EBO(m_DefaultIndicesData);

        m_VAO.bind();
        m_VAO.linkBufferAttribute(VERTEX_ATTRIB_INDEX, m_VBOVertices, 0);
        m_VAO.linkBufferAttribute(TEX_COORD_ATTRIB_INDEX, m_VBOTexCoords, 0);
        GLES30.glEnableVertexAttribArray(VERTEX_ATTRIB_INDEX);
        GLES30.glEnableVertexAttribArray(TEX_COORD_ATTRIB_INDEX);
        m_VAO.unbind();

        m_VAO.enableElements(m_EBOIndices);

        getAllUniLocations();
        int texPos[] = {-1};
        m_TexID[0] = GLHelper.loadTexture(context, R.raw.android_logo, texPos);
        m_TexPos = texPos[0];
    }



    void getAllUniLocations(){
        m_UniMVPMatIndx = GLES30.glGetUniformLocation(m_Shader.getProgram(), "uMVPMat");

        if(m_UniMVPMatIndx < 0){
            GLHelper.handleException(TAG, "uniform not found");
        }

    }

    @Override
    public void draw(float[] MVPMat) {
        m_Shader.start();
        GLES30.glUniformMatrix4fv(m_UniMVPMatIndx, 1, false, MVPMat, 0);

        m_VAO.bind();

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,  m_TexID[0]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, m_EBOIndices.length(), m_EBOIndices.getType(), 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisable(GLES30.GL_TEXTURE_2D);
        m_Shader.stop();

        m_VAO.unbind();
    }

    @Override
    public void destroy() {
        m_VBOVertices.destroy();
        m_VBOTexCoords.destroy();
        GLES30.glDeleteTextures(0, m_TexID, m_TexID.length);
        m_VAO.destroy();
        m_Shader.destroy();
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
