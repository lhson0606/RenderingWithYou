package com.java.rendering_with_you_12.glClass;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.utils.GLHelper;

public class VAO {
    private int m_ID;
    public VAO(){
        m_ID = GLHelper.genVertexArray();
    }

    public void linkBufferAttribute(int index, VBO vbo, int offSet){
        vbo.bind();
        GLES30.glVertexAttribPointer(index, vbo.getSize(), vbo.getType(), vbo.isNormalized(), vbo.getStride(), offSet);
        vbo.unbind();
    }

    public void bind(){
        GLES30.glBindVertexArray(m_ID);
    }

    public void unbind(){
        GLES30.glBindVertexArray(0);
    }
    int getID(){
        return m_ID;
    }
    public void destroy(){
        int temp[] = {m_ID};
        GLES30.glDeleteVertexArrays(0, temp, 1);
    }
    //must be used outside of this.bind() and this.unbind() scope
    public void enableElements(EBO ebo){
        bind();
        ebo.bind();
        unbind();
        ebo .unbind();
    }
}
