package com.dy.app.common.maths;

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
    }

    //http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
    //code: https://github.com/TheThinMatrix/OpenGL-Animation
    public Quat(Mat4 m){
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
    public Mat4 toMat(){
        Mat4 ret = new Mat4();
        float x2 = x*x;
        float y2 = y*y;
        float z2 = z*z;
        float xy = x*y;
        float xz = x*z;
        float yz = y*z;
        float wx = w*x;
        float wy = w*y;
        float wz = w*z;

        ret.mData[0*4+0] = 1f - 2f*(y2 + z2);
        ret.mData[0*4+1] = 2f*(xy - wz);
        ret.mData[0*4+2] = 2f*(xz + wy);

        ret.mData[1*4+0] = 2f*(xy + wz);
        ret.mData[1*4+1] = 1f - 2f*(x2 + z2);
        ret.mData[1*4+2] = 2f*(yz - wx);

        ret.mData[2*4+0] = 2f*(xz - wy);
        ret.mData[2*4+1] = 2f*(yz + wx);
        ret.mData[2*4+2] = 1f - 2f*(x2 + y2);

        ret.mData[3*4+3] = 1f;

        return ret;
    }

    public Quat normalize(){
        float mag = (float)Math.sqrt(x*x + y*y + z*z + w*w);
        x /= mag;
        y /= mag;
        z /= mag;
        w /= mag;
        return this;
    }

    //http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
    //code: https://github.com/TheThinMatrix/OpenGL-Animation
    /*public static Quat interpolate(Quat a, float alpha, Quat b, float beta) {
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
    }*/

    public static Quat slerp(Quat q1, Quat q2, float t) {
        Quat ret = new Quat(0, 0, 0, 1);
        float dot = q1.dot(q2);

        if (dot < 0) {
            q2 = q2.multiply(-1);
            dot = -dot;
        }

        if (dot > 0.9995) {
            ret = q2.subtract(q1);
            ret = ret.multiply(t);
            ret = q1.add(ret);
            ret.normalize();
            return ret;
        }

        float theta = (float)Math.acos(dot);
        ret = q1.multiply((float)Math.sin(theta * (1 - t)));
        ret = ret.add(q2.multiply((float)Math.sin(theta * t)));
        ret = ret.multiply(1f / (float)Math.sin(theta));
        return ret;

    }

    public float dot(Quat q){
        return x*q.x + y*q.y + z*q.z + w*q.w;
    }

    public Quat multiply(float f){
        return new Quat(x*f, y*f, z*f, w*f);
    }

    public Quat add(Quat q){
        return new Quat(x+q.x, y+q.y, z+q.z, w+q.w);
    }

    public Quat subtract(Quat q){
        return new Quat(x-q.x, y-q.y, z-q.z, w-q.w);
    }

    public Quat slerp(Quat q, float t){
        float dot = dot(q);
        float theta = (float)Math.acos(dot)*t;
        Quat ret = new Quat(q.x, q.y, q.z, q.w);
        ret = ret.subtract(this.multiply(dot));
        ret = ret.normalize();
        ret = ret.multiply((float)Math.sin(theta));
        ret = ret.add(this.multiply((float)Math.cos(theta)));
        return ret;
    }

    public Quat negate(){
        return new Quat(-x, -y, -z, -w);
    }

    public Quat conjugate(){
        return new Quat(-x, -y, -z, w);
    }

    public Quat inverse(){
        return conjugate().multiply(1f/(x*x + y*y + z*z + w*w));
    }


}
