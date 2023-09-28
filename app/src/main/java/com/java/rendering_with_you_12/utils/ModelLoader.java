package com.java.rendering_with_you_12.utils;

import android.content.Context;

import com.java.rendering_with_you_12.Model.Mesh;
import com.java.rendering_with_you_12.glClass.Texture;
import com.java.rendering_with_you_12.maths.Vec2;
import com.java.rendering_with_you_12.maths.Vec3;
import com.java.rendering_with_you_12.renderEngine.Camera;
import com.java.rendering_with_you_12.renderEngine.MyRenderer;
import com.java.rendering_with_you_12.shader.Shader;
import com.java.rendering_with_you_12.shader.ShaderHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Vector;

public class ModelLoader {
    public final String VERTEX_NORMALS_PATTERN = "vn ";
    public final String VERTEX_TEXTURE_PATTERN = "vt ";
    public final String FACE_PATTERN = "f ";
    public final String VERTEX_PATTERN = "v ";
    public final static String TAG = "ObjFactory";
    public Mesh load(Context context, MyRenderer renderer, Camera camera, int objRes, int texRes) throws IOException {
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
        return processLoading(context, renderer, camera,vCount, vnCount, vtCount, fCount, texRes, objRes);
    }

    Mesh processLoading(Context context,MyRenderer renderer, Camera camera, int vCount, int vnCount, int vtCount, int fCount, int texRes, int objRes) throws IOException {
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

                    if(!verPerFaceBuffer[1].equals("")){
                        vtIndx = Integer.parseInt(verPerFaceBuffer[1])-1;
                        texCoordsDst[vIndx] = texCoordsSrc.get(vtIndx);
                    }else{
                        texCoordsDst[vIndx] = new Vec2(0.352539f, 1-0.72205625f);
                    }

                    vnIndx = Integer.parseInt(verPerFaceBuffer[2])-1;
                    normsDst[vIndx] = normsSrc.get(vnIndx);

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
        String vertSource = new String();
        String fragSource = new String();
        try {
            InputStream vertexShaderStream = context.getAssets().open(VERTEX_SHADER_PATH);
            InputStream fragmentShaderStream = context.getAssets().open(FRAGMENT_SHADER_PATH);
            vertSource = ShaderHelper.getInstance().readShader(vertexShaderStream);
            fragSource = ShaderHelper.getInstance().readShader(fragmentShaderStream);
        } catch (IOException e) {
            GLHelper.handleException(TAG, e);
        }

        Shader shader = new Shader(vertSource, fragSource);

        float texCoords[] = new float[texCoordsDst.length*2];
        for(int i = 0;i<texCoordsDst.length; ++i){
            texCoords[i*2] = texCoordsDst[i].x;
            texCoords[i*2 + 1] = texCoordsDst[i].y;
        }

        float normals[] = new float[normsDst.length*3];
        for(int i = 0;i<normsDst.length; ++i){
            normals[i*3] = normsDst[i].x;
            normals[i*3 + 1] = normsDst[i].y;
            normals[i*3 + 2] = normsDst[i].z;
        }

        Texture texture = new Texture(texID, 0.1f, 0.5f, 10);

        return new Mesh(shader,
                GLHelper.toFloatArrayV3(vertices),
                texCoords,
                normals,
                GLHelper.toIntArrayi(indices),
                texture,
                camera.getViewMat(),
                renderer.getPproMat()
        );
    }


    public final String VERTEX_SHADER_PATH = "dlsl/BasicObj/vertex.dles";
    public final String FRAGMENT_SHADER_PATH = "dlsl/BasicObj/fragment.dles";
    public final int VERTEX_ATTRIB_INDEX =  0;
    public final int TEX_COORD_ATTRIB_INDEX =  1;

    public static ModelLoader getInstance(){
        return s_ObjFactory = (s_ObjFactory != null)? s_ObjFactory: new ModelLoader();
    }
    ModelLoader (){};
    private static ModelLoader s_ObjFactory = null;
}
