package com.java.rendering_with_you_12.renderEngine;

import com.java.rendering_with_you_12.maths.Vec3;

public class Camera {
    public float xpos;
    public float ypos;
    public float zpos;

    public float xUp;
    public float yUp;
    public float zUp;
    Vec3 vecUp;
    Vec3 vecRight;
    public int offSet;
    float distance;
    final Vec3 globalY = new Vec3(0, 1, 0);

    public Camera(){
        xpos = ypos = 0f;
        zpos = 10f;
        xUp = globalY.x;
        yUp = globalY.y;
        zUp = globalY.z;
        offSet = 0;
        Vec3 pos = new Vec3(xpos, ypos, zpos);
        distance = pos.length();
        vecUp = pos.normalize();
        Vec3 vecFor = (new Vec3(xpos, ypos, zpos)).normalize();
        vecRight = (vecFor.cross(vecUp)).scale(-1).normalize();
    }

    public void update(float t){
        xpos = 5*(float)Math.sin(t);
        ypos = 0;
        zpos = 5*(float)Math.cos(t);
    }

    final float sensitivity = 0.1f;
    final float MAX_MOVE_LENGTH = 20;
    final float MAX_DX = MAX_MOVE_LENGTH;
    final float MAX_DY = MAX_MOVE_LENGTH;
    final float MIN_DX = -MAX_MOVE_LENGTH;
    final float MIN_DY = -MAX_MOVE_LENGTH;
    final float MIN_ANGLE_TO_Y = (float)Math.toRadians(5.0f);

    public void move(float dx, float dy){

        if(dx>MAX_DX){
            dx = MAX_DX;
        }else if(dx<MIN_DX){
            dx = MIN_DX;
        }

        if(dy>MAX_DY){
            dy = MAX_DY;
        }else if(dx<MIN_DY){
            dy = MIN_DY;
        }

        Vec3 moveX = vecRight.scale(dx*sensitivity);
        Vec3 moveY = vecUp.scale(dy*sensitivity);
        Vec3 dst = new Vec3(xpos, ypos, zpos);
        dst = dst.translate(moveX).translate(moveY).normalize().scale(distance);
        //if new dst is too close to global y-axis we don't move (return)
        //references: https://youtu.be/45MIykWJ-C4?si=Tm9ze26qgZA8JVoL&t=4131
        if(dst.angle(globalY) <= MIN_ANGLE_TO_Y || dst.angle(globalY.scale(-1f)) <= MIN_ANGLE_TO_Y){
            return;
        }

        xpos = dst.x;
        ypos = dst.y;
        zpos = dst.z;
        Vec3 vecFor = (new Vec3(xpos, ypos, zpos)).normalize();
        vecUp = vecFor.cross(globalY).cross(vecFor).normalize();
        vecRight = (vecFor.cross(vecUp)).scale(-1).normalize();
        xUp = vecUp.x;
        yUp = vecUp.y;
        zUp = vecUp.z;
    }

    public void scaleR(float s){
        distance *= s;
        xpos *= s;
        ypos *= s;
        zpos *= s;
    }
}
