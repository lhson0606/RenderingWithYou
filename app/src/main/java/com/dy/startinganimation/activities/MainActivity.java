package com.dy.startinganimation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new DyGLSurfaceView(this);
        setContentView(surfaceView);
    }

    private DyGLSurfaceView surfaceView;
}