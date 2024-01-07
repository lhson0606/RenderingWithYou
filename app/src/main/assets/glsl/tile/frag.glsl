#version 300 es
precision mediump float;

in vec2 bTexCoords;
in vec4 bLightSpacePos;

out vec4 finalColor;

uniform sampler2D uTex2D0;
uniform sampler2D shadowSampler;
uniform vec4 uHighlightColor;

float calculateShadowFactor(vec4 lightSpacePos){
	vec3 proPos = lightSpacePos.xyz/lightSpacePos.w;
	vec2 uvCoord;
	uvCoord.x = 0.5*proPos.x+0.5;
	uvCoord.y = 0.5*proPos.y+0.5;
	float z = 0.5*proPos.z + 0.5;
	float bias = 0.0025;
	float depth = texture(shadowSampler, uvCoord).x;
	if(depth < z + bias){
		return 0.2;
	}else{
		return 1.0;
	}
}

void main()
{
	float shadowFactor = calculateShadowFactor(bLightSpacePos);
	finalColor = vec4(texture(uTex2D0, bTexCoords).xyz*shadowFactor,(1.0-uHighlightColor.w) )+ vec4(uHighlightColor.xyz, uHighlightColor.w);
	//finalColor = vec4(shadowFactor,shadowFactor,shadowFactor,(1.0-uHighlightColor.w) )+ vec4(uHighlightColor.xyz, uHighlightColor.w);
}