#version 300 es

layout(location = 0) in vec3 aVerPos;
layout(location = 1) in vec2 aTexCoord;
layout(location = 2) in vec3 aNormalVec;

out vec2 bTexCoords;
out vec3 bUnitNormalVec;
out vec3 bToLightDirVec;
out vec3 bUnitVecToCameraVec;

uniform mat4 uModelMat;
uniform mat4 uViewMat;
uniform mat4 uProMat;
uniform mat4 uLightModelMat;
uniform vec3 uLightPos;

void main()
{
	//extra variables
	vec4 worldPos = uModelMat*vec4(aVerPos, 1.0);
	//handle gl_Position
	gl_Position = uProMat*uViewMat*worldPos;
	//handle texture coords to frag
	bTexCoords = aTexCoord;
	//handle normal vector: calculate its world position and normalize its
	bUnitNormalVec = normalize(uModelMat*vec4(aNormalVec, 0.0)).xyz;
	//handle light direction to mesh
	bToLightDirVec = (uLightModelMat*vec4(uLightPos,1.0)).xyz - worldPos.xyz;
	//handle direction to camera for caculating reflection
	vec3 cameraPos = (inverse(uViewMat)*vec4(0.0, 0.0, 0.0, 1.0)).xyz;
	bUnitVecToCameraVec = normalize(cameraPos - worldPos.xyz);
}