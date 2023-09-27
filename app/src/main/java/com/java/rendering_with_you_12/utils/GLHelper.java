package com.java.rendering_with_you_12.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import com.java.rendering_with_you_12.R;
import com.java.rendering_with_you_12.maths.Vec2;
import com.java.rendering_with_you_12.maths.Vec3;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;
import java.util.Vector;

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

    static int count = 0;
    public static int loadTexture(Context context, int resID, int[] texPosition){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID, options);
        IntBuffer texID = IntBuffer.allocate(1);
        GLES30.glGenTextures(1, texID);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D , texID.get(0));
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0,  bitmap, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D , 0);
        bitmap.recycle();
        texPosition[0] = count;
        ++count;
        return texID.get(0);
    }

    public static FloatBuffer createFloatBufferV3(Vector<Vec3> vec){
        float[] data = new float[vec.size()*3];

        for(int i = 0; i<vec.size(); ++i){
            data[i*3] = vec.elementAt(i).x;
            data[i*3 + 1] = vec.elementAt(i).y;
            data[i*3 + 2] = vec.elementAt(i).z;
        }

        return createFloatBuffer(data);
    }

    public static FloatBuffer createFloatBufferV2(Vector<Vec2> vec){
        float[] data = new float[vec.size()*2];

        for(int i = 0; i<vec.size(); ++i){
            data[i*2] = vec.elementAt(i).x;
            data[i*2 + 1] = vec.elementAt(i).y;
        }

        return createFloatBuffer(data);
    }

    public static IntBuffer createIntBufferVi(Vector<Integer> vec){
        int[] data = new int[vec.size()];

        for(int i = 0; i<vec.size(); ++i){
            data[i] = vec.elementAt(i);
        }

        return createIntBuffer(data);
    }

    public static FloatBuffer createFloatBufferV3(Vec3[] vec){
        float[] data = new float[vec.length*3];

        for(int i = 0; i<vec.length; ++i){
            data[i*3] = vec[i].x;
            data[i*3 + 1] = vec[i].y;
            data[i*3 + 2] = vec[i].z;
        }

        return createFloatBuffer(data);
    }

    public static FloatBuffer createFloatBufferV2(Vec2[] vec){
        float[] data = new float[vec.length*2];

        for(int i = 0; i< vec.length; ++i){
            data[i*2] = vec[i].x;
            data[i*2 + 1] = vec[i].y;
        }

        return createFloatBuffer(data);
    }


}
