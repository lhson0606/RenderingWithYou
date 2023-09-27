package com.java.rendering_with_you_12.Application;

import android.content.Context;
import android.opengl.GLES30;

import com.java.rendering_with_you_12.Model.Obj3D;
import com.java.rendering_with_you_12.Model.TexturedCube;
import com.java.rendering_with_you_12.Model.Triangle2DEle;
import com.java.rendering_with_you_12.R;
import com.java.rendering_with_you_12.renderEngine.MyRenderer;
import com.java.rendering_with_you_12.utils.GLHelper;
import com.java.rendering_with_you_12.utils.ObjFactory;

import java.io.IOException;

public class Program extends GLBasicSurfaceView{
    public final String TAG = "Program";
    public Program(Context context) {
        super(context);
    }

    public void init(){
        super.init();
        TexturedCube cube = new TexturedCube(m_Context);
        m_Renderer.render(cube);
        //TexturedCube cube = new TexturedCube(m_Context);
        //m_Renderer.render(cube);
        /*Triangle2DEle trig = new Triangle2DEle(m_Context);
        m_Renderer.render(trig);*/
        try {
            /*Obj3D objSwitch = ObjFactory.getInstance().load(m_Context, R.raw.switch_wf, R.raw.switch_tex);
            m_Renderer.render(objSwitch);
            Obj3D objStall = ObjFactory.getInstance().load(m_Context, R.raw.stall_wf, R.raw.stall_tex);
            m_Renderer.render(objStall);*/
            Obj3D obj_Bill_Cipher = ObjFactory.getInstance().load(m_Context, R.raw.bill_cipher_wf, R.raw.bill_cipher_tex);
            m_Renderer.render(obj_Bill_Cipher);
        } catch (IOException e) {
            GLHelper.handleException(TAG, e.getMessage());
        }


    }

}
