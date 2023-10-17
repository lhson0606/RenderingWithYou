#version 300 es
precision mediump float;

in vec2 bTexCoords;
in vec3 bUnitNormalVec;
in vec3 bToLightDirVec;
in vec3 bUnitVecToCameraVec;

out vec4 finalColor;

uniform sampler2D uTex2D0;
uniform vec3 uLightColor;
uniform float uShineDampener;
uniform float uReflectivity;
uniform float uAmbientFactor;
uniform float uLightAttenuationConstant;
uniform float uLightAttenuationLinear;
uniform float uLightAttenuationQuadratic;
uniform vec4 uHighlightColor;

void main()
{
	vec3 unitToLightDirVec = normalize(bToLightDirVec);
    //handle light diffusion aka lighting
	float lightAngleFactor = dot(unitToLightDirVec, bUnitNormalVec);
	//calculate light attenuation factor
	float d = length(bToLightDirVec);
	float attenuation = 1.0/(uLightAttenuationQuadratic*d*d + uLightAttenuationLinear*d + uLightAttenuationConstant);
	float lightDiffusion = max(lightAngleFactor*attenuation, uAmbientFactor);
	vec3 lightingColor = lightDiffusion*uLightColor;
	//handle reflection
	vec3 reflectedDir = normalize(reflect(unitToLightDirVec, bUnitNormalVec));
	float lightReflectionFactor = dot(reflectedDir, bUnitVecToCameraVec);
	lightReflectionFactor = max(lightReflectionFactor, 0.0);
	float specularFactor = pow(lightReflectionFactor, uShineDampener);
	vec4 specularColor = vec4(specularFactor*uReflectivity*uLightColor, 1.0);	
	//final color
	vec4 brightness = vec4(lightingColor, 1.0);
	vec4 textureColor = texture(uTex2D0, bTexCoords);
	
	finalColor = brightness*textureColor + specularColor + uHighlightColor;

}