package com.dy.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import com.dy.app.common.maths.Vec3;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

public class GLHelper {
    public static Bitmap loadBitmap(InputStream is) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        //https://developer.android.com/reference/android/graphics/BitmapFactory
        Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(-1,-1,-1,-1), options);
        //bitmap.recycle();
        return bitmap;
    }

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

    public static int getUniLocation(int program, String name) {
        int location;
        location = GLES30.glGetUniformLocation(program, name);

        if(location < 0){
            handleException(TAG, name+" uniform not found");
        }

        return location;
    }

    public static float[] toFloatArrayV3(Vector<Vec3> vec){
        float[] data = new float[vec.size()*3];

        for(int i = 0; i<vec.size(); ++i){
            data[i*3] = vec.elementAt(i).x;
            data[i*3 + 1] = vec.elementAt(i).y;
            data[i*3 + 2] = vec.elementAt(i).z;
        }

        return data;
    }

    public static int[] toIntArrayi(Vector<Integer> vec){
        int[] data = new int[vec.size()];

        for(int i = 0; i<vec.size(); ++i){
            data[i] = vec.elementAt(i);
        }

        return data;
    }

    public static int loadTexture(Bitmap bitmap) {
        IntBuffer texID = IntBuffer.allocate(1);
        GLES30.glGenTextures(1, texID);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D , texID.get(0));
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0,  bitmap, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D , texID.get(0));
        //trying to solve using https://community.khronos.org/t/flickering-textures/70481
        //#update, it seems that the flickering issue is gone after I changed the texture filtering to GL_LINEAR_MIPMAP_LINEAR
        //#ISSUE flickering issue
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D); //should go after you specified texture image
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return texID.get(0);
    }

    public static int createFrameBuffer(){
        int[] fboId = {-1};
        GLES30.glGenFramebuffers(1, fboId, 0);

        if(fboId[0] <=0){
            handleException(TAG, "failed to gen FBO");
        }

        return fboId[0];
    }

    public static int createNewTexture(){
        int[] newTexId = {-1};
        GLES30.glGenTextures(1, newTexId, 0);

        if(newTexId[0] <=0){
            handleException(TAG, "failed to gen new texture");
        }

        return newTexId[0];
    }

    public static void handleException(String tag, Exception e){
        handleException(tag, e.getMessage());
    }

    public static void handleException(String tag, String msg){
        Log.e(tag, msg);
        throw new RuntimeException("[" + tag + "]" + " : " + msg);
    }

    public static final String TAG = "GL ERROR";
}
