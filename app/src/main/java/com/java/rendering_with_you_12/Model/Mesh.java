package com.java.rendering_with_you_12.Model;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.java.rendering_with_you_12.glClass.EBO;
import com.java.rendering_with_you_12.glClass.Texture;
import com.java.rendering_with_you_12.glClass.VAO;
import com.java.rendering_with_you_12.glClass.VBO;
import com.java.rendering_with_you_12.maths.Vec3;
import com.java.rendering_with_you_12.shader.Shader;
import com.java.rendering_with_you_12.shader.ShaderHelper;
import com.java.rendering_with_you_12.utils.GLHelper;

import java.io.IOException;

public class Mesh implements Entity{
    public Mesh(float[] viewMat, float[] projMat){
        m_ViewMat = viewMat;
        m_ProjMat = projMat;
    }

    public Mesh(Shader shader, float[] vertices, float[] texCoords, float[]normals, int[] indices, Texture texture, float[] viewMat, float[] projMat){
        m_Shader = shader;
        m_VAO = new VAO();
        m_VBOVertices = new VBO(vertices, 3, 3*Float.BYTES, false);
        m_VBOTexCoords = new VBO(texCoords, 2, 2*Float.BYTES, false);
        m_VBONormals = new VBO(normals, 3, 3*Float.BYTES, false);
        m_EBOIndices = new EBO(indices);
        m_Texture = texture;
        bindAndEnableAttrib();

        m_ViewMat = viewMat;
        m_ProjMat = projMat;
        m_ModelMat = new float[16];
        Matrix.setIdentityM(m_ModelMat, 0);
    }

    private void bindAndEnableAttrib(){
        m_VAO.bind();
        m_VAO.linkBufferAttribute(Shader.VERTEX_INDEX, m_VBOVertices, 0);
        m_VAO.linkBufferAttribute(Shader.TEXTURE_COORD_INDEX, m_VBOTexCoords, 0);
        m_VAO.linkBufferAttribute(Shader.NORMAL_INDEX, m_VBONormals, 0);
        GLES30.glEnableVertexAttribArray(Shader.VERTEX_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.TEXTURE_COORD_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.NORMAL_INDEX);
        m_VAO.unbind();

        m_VAO.enableElements(m_EBOIndices);
    }

    Light sun = new Light(new Vec3(1,1,1), new Vec3(20, 20, -20));
    @Override
    public void draw() {
        m_Shader.start();
        //load uniforms
        m_Shader.loadLight(sun);                                        //3
        m_Shader.loadModelMat(m_ModelMat);                              //1
        m_Shader.loadViewMat(m_ViewMat);                                //1
        m_Shader.loadProjectionMat(m_ProjMat);                          //1
        m_Shader.loadShineDampener(m_Texture.getLightDampener());       //1
        m_Shader.loadReflectivity(m_Texture.getReflectivity());         //1
        m_Shader.loadAmbient(m_Texture.getAmbientFactor());             //1

        m_VAO.bind();
        GLES30.glEnableVertexAttribArray(Shader.VERTEX_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.TEXTURE_COORD_INDEX);
        GLES30.glEnableVertexAttribArray(Shader.NORMAL_INDEX);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,  m_Texture.getID()); //1
                                                                //Total://10
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, m_EBOIndices.length(), m_EBOIndices.getType(), 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisable(GLES30.GL_TEXTURE_2D);
        m_Shader.stop();
        m_VAO.unbind();
    }

    @Override
    public void destroy() {

        m_Texture.destroy();
        m_EBOIndices.destroy();
        m_VBONormals.destroy();
        m_VBOTexCoords.destroy();
        m_VBOVertices.destroy();
        m_VAO.destroy();
        m_Shader.destroy();
    }

    public static final String TAG = "Mesh";

    private VAO m_VAO;
    private VBO m_VBOVertices;
    private VBO m_VBOTexCoords;
    private VBO m_VBONormals;
    private EBO m_EBOIndices;
    private Shader m_Shader;
    private Texture m_Texture;
    private float[] m_ViewMat;
    private float[] m_ProjMat;
    private float[] m_ModelMat;
    private static final String VERTEX_SHADER_PATH = "dlsl/BasicObj/vertex.dles";
    private static final String FRAGMENT_SHADER_PATH = "dlsl/BasicObj/fragment.dles";

}
