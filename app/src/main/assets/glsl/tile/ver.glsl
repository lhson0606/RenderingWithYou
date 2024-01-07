#version 300 es

layout(location = 0) in vec3 aVerPos;
layout(location = 1) in vec2 aTexCoord;
layout(location = 2) in vec3 aNormalVec;

out vec2 bTexCoords;
out vec4 bLightSpacePos;

uniform mat4 uModelMat;
uniform mat4 uViewMat;
uniform mat4 uProMat;
uniform mat4 uLightMVP;

void main()
{
	gl_Position = uProMat*uViewMat*uModelMat*vec4(aVerPos, 1.0);
	bTexCoords = aTexCoord;
	bLightSpacePos = uLightMVP*vec4(aVerPos, 1.0);
}