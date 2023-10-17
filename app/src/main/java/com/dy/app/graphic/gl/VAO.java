package com.dy.app.graphic.gl;

import android.opengl.GLES30;

import com.dy.app.utils.GLHelper;

public class VAO {
    public VAO(){
        mID = GLHelper.genVertexArray();
    }

    public void linkBufferAttribute(int index, VBO vbo, int offSet){
        vbo.bind();
        GLES30.glVertexAttribPointer(index, vbo.getSize(), vbo.getType(), vbo.isNormalized(), vbo.getStride(), offSet);
        vbo.unbind();
    }

    public void linkBufferAttribute(int index, VBOi vboi, int offSet){
        vboi.bind();
        GLES30.glVertexAttribIPointer(index, vboi.getSize(), vboi.getType(), vboi.getStride(), offSet);
        vboi.unbind();
    }

    public void bind(){
        GLES30.glBindVertexArray(mID);
    }

    public void unbind(){
        GLES30.glBindVertexArray(0);
    }
    public int getID(){
        return mID;
    }
    public void destroy(){
        int temp[] = {mID};
        GLES30.glDeleteVertexArrays(0, temp, 1);
    }
    //must be used outside of this.bind() and this.unbind() scope
    public void enableElements(EBO ebo){
        bind();
        ebo.bind();
        unbind();
        ebo.unbind();
    }
    private int mID;
}
