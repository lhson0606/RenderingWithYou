#version 300 es

layout(location = 0) in vec3 aVerPos;

uniform mat4 gMVP;

void main()
{
	gl_Position = gMVP*vec4(aVerPos, 1.0);
}