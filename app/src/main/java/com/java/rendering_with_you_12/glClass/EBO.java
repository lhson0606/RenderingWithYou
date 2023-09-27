package com.java.rendering_with_you_12.glClass;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.utils.GLHelper;

public class EBO {
    private int m_ID;
    private int[] m_Data;
    public EBO(int ID, int[] data){
        m_ID = ID;
        m_Data = data;
        //put data
        put();
    }
    public EBO(int[] data){
        m_ID = GLHelper.genBuffer();
        m_Data = data;
        put();
    }
    void put(){
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, m_ID);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                m_Data.length*Integer.BYTES,
                GLHelper.createIntBuffer(m_Data),
                GLES20.GL_STATIC_DRAW
        );
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void bind(){
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, m_ID);
    }

    public void unbind(){
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void destroy(){
        int temp[] = new int[m_ID];
        GLES30.glDeleteBuffers(0, temp, 1);
    }

    public int getID(){
        return m_ID;
    }

    public int[] getData() {
        return m_Data;
    }
    //get current number of elements
    public int length(){
        return m_Data.length;
    }

    public int getType(){
        return GLES30.GL_UNSIGNED_INT;
    }

}
