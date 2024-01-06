package com.dy.app.graphic.shader;

import com.dy.app.common.maths.Mat4;
import com.dy.app.utils.GLHelper;

public class ShadowMapShader extends BaseShader{
    public ShadowMapShader(String verCode, String fragCode) {
        super(verCode, fragCode);
    }

    @Override
    protected void getAllUniLocations() {
        mMvpLoc = GLHelper.getUniLocation(mProgram, MVP_NAME);
    }

    public void loadMvp(Mat4 mvp) {
        super.loadMat4(mMvpLoc, mvp.mData);
    }

    private final String MVP_NAME = "gMVP";
    private int mMvpLoc = -1;
}
