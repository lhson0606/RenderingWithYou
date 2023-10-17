package com.dy.app.utils;

import android.content.Context;

import com.dy.app.common.maths.Vec2;
import com.dy.app.common.maths.Vec3;
import com.dy.app.graphic.model.Mesh;
import com.dy.app.graphic.shader.ShaderHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class MeshLoader {
    public final String VERTEX_NORMALS_PATTERN = "vn ";
    public final String VERTEX_TEXTURE_PATTERN = "vt ";
    public final String FACE_PATTERN = "f ";
    public final String VERTEX_PATTERN = "v ";
    public final static String TAG = "ObjFactory";
    public Mesh load(Context context, String path) throws IOException {
        //calculate
        int vCount = 0;
        int vnCount = 0;
        int vtCount = 0;
        int fCount = 0;

        BufferedReader reader = new BufferedReader( new InputStreamReader( context.getAssets().open(path)));

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
        return processLoading(context, path, vCount, vnCount, vtCount, fCount);
    }

    private Mesh processLoading(Context context, String path, int vCount, int vnCount, int vtCount, int fCount) throws IOException {
        Vector<Vec3> vertices = new Vector<>();
        Vector<Integer> indices = new Vector<>();
        Vector<Vec3> normsSrc = new Vector<>();
        Vector<Vec2> texCoordsSrc = new Vector<>();

        Vec3 normsDst[]= new Vec3[vCount];
        Vec2 texCoordsDst[] = new Vec2[vCount];

        BufferedReader reader = new BufferedReader( new InputStreamReader( context.getAssets().open(path)));

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
                //obj file format: f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
                //obj file index starts from 1
                for(int i = 1 ;i< 4; ++i)
                {
                    verPerFaceBuffer = buffer[i].split("/");

                    vIndx = Integer.parseInt(verPerFaceBuffer[0])-1;

                    if(!verPerFaceBuffer[1].equals("")){
                        vtIndx = Integer.parseInt(verPerFaceBuffer[1])-1;
                        texCoordsDst[vIndx] = texCoordsSrc.get(vtIndx);
                    }else{
                        texCoordsDst[vIndx] = new Vec2(0f, 0f);//unsupported
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

        Mesh mesh = new Mesh(
                GLHelper.toFloatArrayV3(vertices),
                texCoords,
                normals,
                GLHelper.toIntArrayi(indices)
        );

        return mesh;
    }

    public static MeshLoader getInstance(){
        return instance = (instance != null)? instance: new MeshLoader();
    }
    private MeshLoader(){};
    private static MeshLoader instance = null;
}
