package com.dy.app.graphic.shader;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.dy.app.utils.GLHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderHelper {

    public final String TAG = "ShaderHelper";

    public int loadProgram(InputStream vStream, InputStream fStream){

        String verCode = readShader(vStream);
        String fragCode = readShader(fStream);
        int verID = compileShader(verCode, GLES20.GL_VERTEX_SHADER);
        int fragID = compileShader(fragCode, GLES20.GL_FRAGMENT_SHADER);
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

        return programID;
    }
    public int loadProgram(String vPath, String fPath){
        int programID = GLES30.glCreateProgram();
        String verCode = readShader(vPath);
        String fragCode = readShader(fPath);
        int verID = compileShader(verCode, GLES20.GL_VERTEX_SHADER);
        int fragID = compileShader(fragCode, GLES20.GL_FRAGMENT_SHADER);
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

        return programID;
    }

    public String readShader(InputStream is){
        StringBuilder strBuilder = new StringBuilder();

        try{
            BufferedReader reader  = new BufferedReader(new InputStreamReader(is));
            String curLine;

            while((curLine = reader.readLine())!= null){
                strBuilder.append(curLine).append("\n");
            }

            reader.close();
        } catch (IOException e) {
            GLHelper.handleException(TAG, e);
        }

        return strBuilder.toString();
    }

    public String readShader(String path){
        StringBuilder strBuilder = new StringBuilder();

        try{
            FileReader fileReader  = new FileReader(path);
            BufferedReader reader = new BufferedReader(fileReader);
            String curLine;

            while((curLine = reader.readLine())!= null){
                strBuilder.append(curLine).append("\n");
            }

            reader.close();
        } catch (IOException e) {
            GLHelper.handleException(TAG, e);
        }

        return strBuilder.toString();
    }

    public int compileShader(String shaderSource, int type){
        int shaderID = GLES20.glCreateShader(type);
        GLES30.glShaderSource(shaderID, shaderSource);
        GLES30.glCompileShader(shaderID);

        int[] compiled = new int[1];
        compiled[0] = -1;
        GLES20.glGetShaderiv(shaderID, GLES20.GL_COMPILE_STATUS, compiled, 0);

        if(compiled[0] == GLES20.GL_FALSE){
            String errorMsg = GLES20.glGetShaderInfoLog(shaderID);
            GLHelper.handleException(TAG, errorMsg);
        }

        return shaderID;
    }

    public static ShaderHelper getInstance(){
        return s_Instance = (s_Instance != null)? s_Instance : new ShaderHelper();
    }

    private ShaderHelper(){}
    private static ShaderHelper s_Instance;
}
