package com.dy.app.core;

import com.dy.app.common.maths.Mat4;

public interface GameEntity {
    void init();
    void update(float dt);
    void draw();
    void destroy();
}
