package com.dy.startinganimation.maths;

public class Quat {
    //http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
    //code: https://github.com/TheThinMatrix/OpenGL-Animation
    private float x;
    private float y;
    private float z;
    private float w;

    public Quat(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        //normalize
        float mag = (float)Math.sqrt(x*x + y*y + z*z + w*w);
        this.x /= mag;
        this.y /= mag;
        this.z /= mag;
        this.w /= mag;
    }

    //http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
    //code: https://github.com/TheThinMatrix/OpenGL-Animation
    public Quat(Mat4 m){
        float w, x, y, z;
        float diagonal = m.mData[0*4 + 0] + m.mData[1*4+1] + m.mData[2*4 + 2];

        if(diagonal>0){
            float w4 = (float) (Math.sqrt(diagonal+1f)*2f);
            w = w4/4f;
            x = (m.mData[2*4 + 1] - m.mData[1*4 + 2 ]) / w4;
            y = (m.mData[0*4 + 2] - m.mData[2*4 + 0 ]) / w4;
            z = (m.mData[1*4 + 0] - m.mData[0*4 + 1 ]) / w4;
        } else if ((m.mData[0*4+0] > m.mData[1*4+1]) && (m.mData[0*4+1] > m.mData[2*4+2])) {
            float x4 = (float) (Math.sqrt(1f + m.mData[0*4+0] - m.mData[1*4+1] - m.mData[2*4+2]) * 2f);
            w = (m.mData[2*4+1] - m.mData[1*4+2]) / x4;
            x = x4 / 4f;
            y = (m.mData[0*4+1] + m.mData[1*4+0]) / x4;
            z = (m.mData[0*4+2] + m.mData[2*4+0]) / x4;
        } else if (m.mData[1*4+1] > m.mData[2*4+2]) {
            float y4 = (float) (Math.sqrt(1f + m.mData[1*4+1] - m.mData[0*4+0] - m.mData[2*4+2]) * 2f);
            w = (m.mData[0*4+2] - m.mData[2*4+0]) / y4;
            x = (m.mData[0*4+1] + m.mData[1*4+0]) / y4;
            y = y4 / 4f;
            z = (m.mData[1*4+2] + m.mData[2*4+1]) / y4;
        } else {
            float z4 = (float) (Math.sqrt(1f + m.mData[2*4+2] - m.mData[0*4+0] - m.mData[1*4+1]) * 2f);
            w = (m.mData[1*4+0] - m.mData[0*4+1]) / z4;
            x = (m.mData[0*4+2] + m.mData[2*4+0]) / z4;
            y = (m.mData[1*4+2] + m.mData[2*4+1]) / z4;
            z = z4 / 4f;
        }
    }

    //http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
    //code: https://github.com/TheThinMatrix/OpenGL-Animation
    public Mat4 toMat() {
        Mat4 mat = new Mat4();
        final float xy = x * y;
        final float xz = x * z;
        final float xw = x * w;
        final float yz = y * z;
        final float yw = y * w;
        final float zw = z * w;
        final float xSquared = x * x;
        final float ySquared = y * y;
        final float zSquared = z * z;
        mat.mData[0*4 + 0] = 1 - 2 * (ySquared + zSquared);
        mat.mData[0*4 + 1] = 2 * (xy - zw);
        mat.mData[0*4 + 2] = 2 * (xz + yw);
        mat.mData[0*4 + 3] = 0;
        mat.mData[1*4 + 0] = 2 * (xy + zw);
        mat.mData[1*4 + 1] = 1 - 2 * (xSquared + zSquared);
        mat.mData[1*4 + 2] = 2 * (yz - xw);
        mat.mData[1*4 + 3] = 0;
        mat.mData[2*4 + 0] = 2 * (xz - yw);
        mat.mData[2*4 + 1] = 2 * (yz + xw);
        mat.mData[2*4 + 2] = 1 - 2 * (xSquared + ySquared);
        mat.mData[2*4 + 3] = 0;
        mat.mData[3*4 + 0] = 0;
        mat.mData[3*4 + 1] = 0;
        mat.mData[3*4 + 2] = 0;
        mat.mData[3*4 + 3] = 1;
        return mat;
    }

    public Quat normalize(){
        float mag = (float)Math.sqrt(x*x + y*y + z*z + w*w);
        return new Quat(x/mag, y/mag, z/mag, w/mag);
    }

    //http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
    //code: https://github.com/TheThinMatrix/OpenGL-Animation
    public static Quat interpolate(Quat a, float alpha, Quat b, float beta) {
        Quat ret = new Quat(0, 0, 0, 1);
        float dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;

        if (dot < 0) {
            ret.w = alpha * a.w + beta * -b.w;
            ret.x = alpha * a.x + beta * -b.x;
            ret.y = alpha * a.y + beta * -b.y;
            ret.z = alpha * a.z + beta * -b.z;
        } else {
            ret.w = alpha * a.w + beta * b.w;
            ret.x = alpha * a.x + beta * b.x;
            ret.y = alpha * a.y + beta * b.y;
            ret.z = alpha * a.z + beta * b.z;
        }

        ret.normalize();
        return ret;
    }
}
