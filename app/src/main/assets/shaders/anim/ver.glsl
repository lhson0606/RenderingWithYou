#version 300 es

const int MAX_JOINTS =  50;
const int MAX_WEIGHTS = 4;

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec2 aTexCoord;
layout(location = 2) in vec3 aNormal;
layout(location = 3) in ivec4 aJointID;
layout(location = 4) in vec4 aWeights;

out vec2 bTexCoord;
out vec3 bNormal;

uniform mat4 uJointMats[MAX_JOINTS];
uniform mat4 uModelMat;
uniform mat4 uViewMat;
uniform mat4 uProjMat;

void main(){
	
	vec4 finalPos = vec4(0);
	vec4 finalNorm = vec4(0);
	
	for(int i = 0; i<MAX_WEIGHTS; ++i){
		finalPos+= uJointMats[aJointID[i]]*aWeights[i]*vec4(aPosition, 1);
		finalNorm+= uJointMats[aJointID[i]]*aWeights[i]*vec4(aNormal, 0);
	}

	finalPos = uProjMat*uViewMat*uModelMat*finalPos;
	gl_Position = finalPos;

	bNormal = (uModelMat*vec4(finalNorm.xyz,0)).xyz;
	bTexCoord = aTexCoord;
}