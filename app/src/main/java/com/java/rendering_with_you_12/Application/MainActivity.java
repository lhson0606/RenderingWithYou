package com.java.rendering_with_you_12.Application;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private GLBasicSurfaceView glProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glProgram = new Program(this);
        setContentView(glProgram);
    }
}