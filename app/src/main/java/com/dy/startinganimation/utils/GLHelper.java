package com.dy.startinganimation.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GLHelper {
    public static  int genBuffer(){
        IntBuffer idBuffer = IntBuffer.allocate(1);
        GLES30.glGenBuffers(1, idBuffer);
        int vboID = idBuffer.get(0);

        if(vboID<0)
        {
            new RuntimeException(TAG + ": failed to load generate buffer");
        }

        return vboID;
    }

    public static int genVertexArray(){
        IntBuffer idBuffer = IntBuffer.allocate(1);
        GLES30.glGenVertexArrays(1, idBuffer);
        int vaoID = idBuffer.get(0);

        if(vaoID<0)
        {
            new RuntimeException(TAG + ": failed to load generate VAO");
        }

        return vaoID;
    }
    public static IntBuffer createIntBuffer(int[] data){
        IntBuffer res;
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length*Integer.BYTES);
        bb.order(ByteOrder.nativeOrder());
        res = bb.asIntBuffer();
        res.put(data);
        res.flip();
        res.position(0);
        return res;
    }
    public  static FloatBuffer createFloatBuffer(float[] data){
        FloatBuffer res;
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length*Float.BYTES);
        bb.order(ByteOrder.nativeOrder());
        res = bb.asFloatBuffer();
        res.put(data);
        res.flip();
        res.position(0);
        return res;
    }

    public static int loadTexture(Context context, int resID){
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
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        bitmap.recycle();

        return texID.get(0);
    }

    public static int loadTexture(Context context, String path) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        //https://developer.android.com/reference/android/graphics/BitmapFactory
        final Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(path), new Rect(-1,-1,-1,-1), options);
        //final Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open(path));
        IntBuffer texID = IntBuffer.allocate(1);
        GLES30.glGenTextures(1, texID);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D , texID.get(0));
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0,  bitmap, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        bitmap.recycle();

        return texID.get(0);
    }

    public static void handleException(String tag, Exception e){
        handleException(tag, e.getMessage());
    }

    public static void handleException(String tag, String msg){
        Log.e(tag, msg);
        throw new RuntimeException("[" + tag + "]" + " : " + msg);
    }
    public static int getUniLocation(int program, String name) {
        int location;
        location = GLES30.glGetUniformLocation(program, name);

        if(location < 0){
            handleException(TAG, name+" uniform not found");
        }

        return location;
    }

    public static final String TAG = "GL ERROR";
}
