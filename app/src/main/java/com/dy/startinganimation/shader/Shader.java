package com.dy.startinganimation.shader;

import android.opengl.GLES20;
import android.opengl.GLES30;

import com.dy.startinganimation.animation.AnimatedModel;
import com.dy.startinganimation.gl.Vertex;
import com.dy.startinganimation.maths.Mat4;
import com.dy.startinganimation.maths.Vec3;
import com.dy.startinganimation.utils.GLHelper;

public class Shader {
    private int mProgram;
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

        mProgram = programID;

        GLES20.glDeleteShader(verID);
        GLES20.glDeleteShader(fragID);

        getAllUniLocations();
    }
    public void loadJointsTransform(Mat4[] mats) {
        int location;

        if(mats.length > AnimatedModel.MAX_BONES){
            GLHelper.handleException(TAG, "loading too many joints");
        }

        for(int i = 0; i<mats.length; ++i){
            location = GLHelper.getUniLocation(mProgram, JOINT_TRANSFORM_NAME + "[" + i+"]");
            loadMat4(location, mats[i].mData);
        }

    }
    public void loadProjectionMat(float[] proMat){

        loadMat4(mProMatLoc, proMat);
    }

    public void loadViewMat(float[] viewMat){

        loadMat4(mViewMatLoc, viewMat);
    }

    public void loadModelMat(float[] modelMat){
        loadMat4(mModelMatLoc, modelMat);
    }

    public void loadLight(Light light){
        loadVec3(mLightDirLoc, light.getPosition());
    }

    public void start(){
        GLES30.glUseProgram(mProgram);
    }

    public void stop(){
        GLES30.glUseProgram(0);
    }

    public int getProgram() {
        return mProgram;
    }

    public void destroy(){
        GLES30.glDeleteProgram(mProgram);
    }

    private void loadVec3(int location, Vec3 vec){
        GLES30.glUniform3f(location, vec.x, vec.y, vec.z);
    }
    private void loadMat4(int location, float[] mat4){
        if(mat4.length != 16){
            GLHelper.handleException(TAG, "loading invalid mat4");
        }

        GLES30.glUniformMatrix4fv(location, 1, false, mat4, 0);
    }

    private void getAllUniLocations() {
        //#TODO
        mModelMatLoc = GLHelper.getUniLocation(mProgram, MODEL_MATRIX_NAME);
        mViewMatLoc = GLHelper.getUniLocation(mProgram, VIEW_MATRIX_NAME);
        mProMatLoc = GLHelper.getUniLocation(mProgram, PROJECTION_MATRIX_NAME);
        mLightDirLoc = GLHelper.getUniLocation(mProgram, LIGHT_DIRECTION_NAME);
    }

    private int mProMatLoc;
    private static final String PROJECTION_MATRIX_NAME = "uProjMat";
    private int mViewMatLoc;
    private static final String VIEW_MATRIX_NAME = "uViewMat";
    private int mModelMatLoc;
    private static final String MODEL_MATRIX_NAME = "uModelMat";
    private int mJointsTransformLoc;
    private final String JOINT_TRANSFORM_NAME = "uJointMats";
    private int mLightDirLoc;
    private final String LIGHT_DIRECTION_NAME = "lightDirection";

    public static final String TAG = "Shader Helper";
    public static final int VERTEX_INDEX = 0;
    public static final int TEXTURE_COORD_INDEX = 1;
    public static final int NORMAL_INDEX = 2;
}
