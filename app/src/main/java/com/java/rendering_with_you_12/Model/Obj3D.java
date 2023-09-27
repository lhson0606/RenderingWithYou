package com.java.rendering_with_you_12.Model;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.java.rendering_with_you_12.utils.GLHelper;

public class Obj3D implements Entity{
    public static final String TAG = "Obj3D";

    int m_Program;
    int m_VertexCount;
    int m_VAOID;
    int m_VBOIDs[];
    int m_TexID;
    int m_TexPos;
    float m_TransMat[];
    int m_UniMVPMatIndx;
    int m_UniTransMaIndx;
    int mPosIndx;
    int mTexCoordIndx = 1;

    public Obj3D(int program, int vertexCount, int VAOID, int VBOIDs[],int texID, int texPos, int uMVPMatLocation, int uTransMatLocation){
        m_Program = program;
        m_VertexCount = vertexCount;
        m_VAOID = VAOID;
        m_VBOIDs = VBOIDs;
        m_TexID = texID;
        m_TexPos = texPos;
        m_UniMVPMatIndx = uMVPMatLocation;
        m_UniTransMaIndx = uTransMatLocation;
        m_TransMat = new float[16];
        Matrix.setIdentityM(m_TransMat, 0);
    }

    @Override
    public void draw(float[] MVPMat) {
        //use shader program
        GLES30.glUseProgram(m_Program);
        //load model view projection matrix
        GLES30.glUniformMatrix4fv(m_UniMVPMatIndx, 1, false, MVPMat, 0);
        //load transformation matrix
        GLES30.glUniformMatrix4fv(m_UniTransMaIndx, 1, false, m_TransMat, 0);

        //start
        GLES30.glBindVertexArray(m_VAOID);

        //GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + m_TexPos);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,  m_TexID);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, m_VertexCount, GLES30.GL_UNSIGNED_INT, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glUseProgram(0);
        GLES30.glBindVertexArray(0);
    }

    @Override
    public void destroy() {
        int VAOs[] = {m_VAOID};
        int textures[] = {m_TexID};

        GLES30.glDeleteBuffers(0, m_VBOIDs, m_VBOIDs.length);
        GLES30.glDeleteTextures(0, textures, 1);
        GLES30.glDeleteVertexArrays(0, VAOs,1);
        GLES30.glDeleteProgram(m_Program);
    }
}
