package com.java.rendering_with_you_12.shader;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.utils.GLHelper;

public class Shader {
    public static final String TAG = "Shader";
    private int m_Program;

    public Shader(String verCode, String fragCode){
        int verID = ShaderHelper.getInstance().compileShader(verCode, GLES20.GL_VERTEX_SHADER);
        int fragID = ShaderHelper.getInstance().compileShader(fragCode, GLES20.GL_FRAGMENT_SHADER);
        int programID = GLES30.glCreateProgram();
        GLES20.glAttachShader(programID, verID);
        GLES20.glAttachShader(programID, fragID);
        GLES20.glLinkProgram(programID);
        GLES20.glValidateProgram(programID);

        int[] compiled = new int[1];
        compiled[0] = -1;
        GLES20.glGetProgramiv(programID, GLES20.GL_VALIDATE_STATUS, compiled, 0);

        if(compiled[0] == GLES20.GL_FALSE){
            String errorMsg = GLES20.glGetProgramInfoLog(programID);
            GLHelper.handleException(TAG, errorMsg);
        }

        GLES20.glDeleteShader(verID);
        GLES20.glDeleteShader(fragID);

        m_Program = programID;
    }

    public void start(){
        GLES30.glUseProgram(m_Program);
    }

    public void stop(){
        GLES30.glUseProgram(0);
    }

    public int getProgram() {
        return m_Program;
    }

    public void destroy(){
        GLES30.glDeleteProgram(m_Program);
    }
}
