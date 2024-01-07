package com.dy.app.graphic.shadow;

import static android.opengl.GLES20.GL_NONE;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.dy.app.graphic.Light;
import com.dy.app.utils.GLHelper;

public class ShadowFrameBuffer {
    private int width;
    private int height;
    private int fboId = 0;
    private int shadowMap;
    public static final String TAG = "ShadowFrameBuffer";

    public ShadowFrameBuffer(){

    }

    public void init(int width, int height){
        this.width = width;
        this.height = height;
        fboId = GLHelper.createFrameBuffer();
        shadowMap = GLHelper.createNewTexture();
        //see https://ogldev.org/www/tutorial23/tutorial23.html
        //init shadow map
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, shadowMap);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, width, height, 0,
                GLES30.GL_DEPTH_COMPONENT, GLES30.GL_FLOAT, null);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        //init frame buffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboId);
        //Now we need to attach the texture object to the FBO
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT,
                GLES30.GL_TEXTURE_2D, shadowMap, 0);//0 here is our mip level, with 0 is the highest resolution and our only mip level

        //disables
        //glBuffer(GL_NONE); //couldn't be resolved in GL 30
        GLES30.glReadBuffer(GL_NONE);

        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);

        if (status != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            GLHelper.handleException(TAG, "something went wrong :)");
        }

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
    }

    public void cleanUp(){
        int[] buffer = {fboId};
        GLES30.glDeleteFramebuffers(1,buffer,0);
        buffer[0] = shadowMap;
        GLES30.glDeleteTextures(1, buffer, 0);
    }

    public int getShadowMap() {
        return shadowMap;
    }

    public void bindForWriting(){
        GLES30.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, fboId);
    }

    public void bindForReading(int texUnit){
        GLES30.glActiveTexture(texUnit);
        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMap);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFboId() {
        return fboId;
    }
}
