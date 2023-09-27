package com.java.rendering_with_you_12.utils;

import android.content.Context;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.Model.Obj3D;
import com.java.rendering_with_you_12.maths.Vec2;
import com.java.rendering_with_you_12.maths.Vec3;
import com.java.rendering_with_you_12.shader.ShaderHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

public class ObjFactory {
    public final String VERTEX_NORMALS_PATTERN = "vn ";
    public final String VERTEX_TEXTURE_PATTERN = "vt ";
    public final String FACE_PATTERN = "f ";
    public final String VERTEX_PATTERN = "v ";
    public final static String TAG = "ObjFactory";
    public Obj3D load(Context context,int objRes, int texRes) throws IOException {
        //calculate
        int vCount = 0;
        int vnCount = 0;
        int vtCount = 0;
        int fCount = 0;

        BufferedReader reader = new BufferedReader( new InputStreamReader( context.getResources().openRawResource(objRes)));

        String curLine;

        while(true){

            if ((curLine = reader.readLine()) == null) {
                break;
            }

            if(curLine.startsWith(VERTEX_NORMALS_PATTERN)){
                ++vnCount;
            }
            else if(curLine.startsWith(VERTEX_TEXTURE_PATTERN)){
                ++vtCount;
            }
            else if(curLine.startsWith(FACE_PATTERN)){
                ++fCount;
            } else if(curLine.startsWith(VERTEX_PATTERN)) {
                ++vCount;
            }

        }

        reader.close();
        return processLoading(context,vCount, vnCount, vtCount, fCount, texRes, objRes);
    }

    Obj3D processLoading(Context context, int vCount, int vnCount, int vtCount, int fCount, int texRes, int objRes) throws IOException {
        int texID = -1;
        int texPos[] = {-1};
        texID = GLHelper.loadTexture(context, texRes, texPos);

        Vector<Vec3> vertices = new Vector<>();
        Vector<Integer> indices = new Vector<>();
        Vector<Vec3> normsSrc = new Vector<>();
        Vector<Vec2> texCoordsSrc = new Vector<>();

        Vec3 normsDst[]= new Vec3[vCount];
        Vec2 texCoordsDst[] = new Vec2[vCount];

        BufferedReader reader = new BufferedReader( new InputStreamReader( context.getResources().openRawResource(objRes)));

        String curLine;
        String[] buffer;
        String[] verPerFaceBuffer;

        int vIndx;
        int vnIndx;
        int vtIndx;

        while(true){

            if ((curLine = reader.readLine()) == null) {
                break;
            }


            if(curLine.startsWith(VERTEX_NORMALS_PATTERN)){
                buffer = curLine.split(" ");
                normsSrc.add(new Vec3(
                        Float.parseFloat(buffer[1]),
                        Float.parseFloat(buffer[2]),
                        Float.parseFloat(buffer[3])));
            }
            else if(curLine.startsWith(VERTEX_TEXTURE_PATTERN)){
                buffer = curLine.split(" ");
                texCoordsSrc.add(new Vec2(
                        Float.parseFloat(buffer[1]),
                        1f -Float.parseFloat(buffer[2])));
            }
            else if(curLine.startsWith(FACE_PATTERN)){
                buffer= curLine.split(" ");

                //3 vertices per plane (triangular)
                for(int i = 1 ;i< 4; ++i)
                {
                    verPerFaceBuffer = buffer[i].split("/");
                    vIndx = Integer.parseInt(verPerFaceBuffer[0])-1;
                    vtIndx = Integer.parseInt(verPerFaceBuffer[1])-1;
                    vnIndx = Integer.parseInt(verPerFaceBuffer[2])-1;

                    normsDst[vIndx] = normsSrc.get(vnIndx);
                    texCoordsDst[vIndx] = texCoordsSrc.get(vtIndx);
                    indices.add(vIndx);
                }

            }
            else if(curLine.startsWith(VERTEX_PATTERN)) {
                buffer = curLine.split(" ");
                vertices.add(new Vec3(Float.parseFloat(buffer[1]), Float.parseFloat(buffer[2]), Float.parseFloat(buffer[3])));
            }

        }

        reader.close();

        int shaderProgram = -1;
        try {
            InputStream vertexShaderStream = context.getAssets().open(VERTEX_SHADER_PATH);
            InputStream fragmentShaderStream = context.getAssets().open(FRAGMENT_SHADER_PATH);
            shaderProgram = ShaderHelper.getInstance().loadProgram(vertexShaderStream, fragmentShaderStream);
        } catch (IOException e) {
            GLHelper.handleException(TAG, e);
        }

        return createObj(shaderProgram,
                    GLHelper.createFloatBufferV3(vertices),
                    GLHelper.createIntBufferVi(indices),
                    GLHelper.createFloatBufferV2(texCoordsDst),
                    GLHelper.createFloatBufferV3(normsDst),
                    texID,
                    texPos[0]
                );
    }

    Obj3D createObj(int program, FloatBuffer vertices, IntBuffer indices, FloatBuffer txtCoords, FloatBuffer norms, int texID, int texPos){
        int VAOID[] = {-1};
        int VBOIDs[] = new int[3];

        GLES30.glGenVertexArrays(1, VAOID, 0);
        GLES30.glGenBuffers(3, VBOIDs, 0);

        GLES30.glBindVertexArray(VAOID[0]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBOIDs[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                vertices.capacity()*Float.BYTES,
                vertices,
                GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(VERTEX_ATTRIB_INDEX, 3,
                GLES30.GL_FLOAT,false, 12, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBOIDs[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                txtCoords.capacity()*Float.BYTES,
                txtCoords,
                GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(TEX_COORD_ATTRIB_INDEX, 2,
                GLES30.GL_FLOAT,false, 8, 0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, VBOIDs[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                indices.capacity()*Integer.BYTES,
                indices,
                GLES30.GL_STATIC_DRAW);

        GLES30.glEnableVertexAttribArray(VERTEX_ATTRIB_INDEX);
        GLES30.glEnableVertexAttribArray(TEX_COORD_ATTRIB_INDEX);//tex coords

        GLES30.glBindVertexArray(0);

        int uMVPMatLocation = GLES30.glGetUniformLocation(program, MVP_MAT_NAME);
        int uTransMatLocation = GLES30.glGetUniformLocation(program, TRANS_MAT_NAME);

        return new Obj3D(
                program,
                indices.capacity(),
                VAOID[0],
                VBOIDs,
                texID,
                texPos,
                uMVPMatLocation,
                uTransMatLocation
                );
    }

    public final String VERTEX_SHADER_PATH = "dlsl/BasicObj/vertex.dles";
    public final String FRAGMENT_SHADER_PATH = "dlsl/BasicObj/fragment.dles";
    public final int VERTEX_ATTRIB_INDEX =  0;
    public final int TEX_COORD_ATTRIB_INDEX =  1;
    public final String MVP_MAT_NAME = "uMVPMat";
    public final String TRANS_MAT_NAME = "uTransMat";

    public static ObjFactory getInstance(){
        return s_ObjFactory = (s_ObjFactory != null)? s_ObjFactory: new ObjFactory();
    }
    ObjFactory (){};
    static ObjFactory s_ObjFactory = null;
}
