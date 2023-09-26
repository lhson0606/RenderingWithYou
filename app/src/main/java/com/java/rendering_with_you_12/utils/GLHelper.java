package com.java.rendering_with_you_12.utils;

import android.opengl.GLES30;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class GLHelper {
    public static final String m_TAG = "GLHelper";
    //https://www.tabnine.com/code/java/methods/android.opengl.GLES30/glBufferData
    //this methods doesn't handle order, flipping data
    public static ByteBuffer createByteBuffer(float[] data){
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length*Float.BYTES);
        bb.order(ByteOrder.nativeOrder());
        return bb;
    }

    //allocate, put, order, flip buffer
    public static ShortBuffer createShortBuffer(short[] data){
        ShortBuffer res;
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length*Short.BYTES);
        //device order
        bb.order(ByteOrder.nativeOrder());
        //create our buffer
        res = bb.asShortBuffer();
        //store data into buffer
        res.put(data);
        //flip buffer
        res.position(0);
        return res;
    }

    //allocate, put, order, flip buffer
    public static IntBuffer createIntBuffer(int[] data){
        IntBuffer res;
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length*Integer.BYTES);
        bb.order(ByteOrder.nativeOrder());
        res = bb.asIntBuffer();
        res.put(data);
        res.position(0);
        return res;
    }

    //allocate, put, order, flip buffer
    public  static FloatBuffer createFloatBuffer(float[] data){
        FloatBuffer res;
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length*Float.BYTES);
        bb.order(ByteOrder.nativeOrder());
        res = bb.asFloatBuffer();
        res.put(data);
        res.position(0);
        return res;
    }

    public static  int genBuffer(){
        IntBuffer idBuffer = IntBuffer.allocate(1);
        GLES30.glGenBuffers(1, idBuffer);
        int vboID = idBuffer.get(0);

        if(vboID<0)
        {
            new RuntimeException(m_TAG + ": failed to load generate buffer");
        }

        return vboID;
    }

    public static int genVertexArray(){
        IntBuffer idBuffer = IntBuffer.allocate(1);
        GLES30.glGenVertexArrays(1, idBuffer);
        int vaoID = idBuffer.get(0);

        if(vaoID<0)
        {
            new RuntimeException(m_TAG + ": failed to load generate VAO");
        }

        return vaoID;
    }

    public static void handleException(String tag, Exception e){
        handleException(tag, e.getMessage());
    }

    public static void handleException(String tag, String msg){
        Log.e(tag, msg);
        throw new RuntimeException(tag + ": " + msg);
    }

    public static void cross3v(float[] dest, float a1, float a2, float a3, float b1, float b2, float b3)
    {
        dest[0] = a2*b3 - a3*b2;
        dest[1] = a3*b1 - a1*b3;
        dest[2] = a1*b2 - a2*b1;
    }

}
