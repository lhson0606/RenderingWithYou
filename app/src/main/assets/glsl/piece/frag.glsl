#version 300 es
//https://www.youtube.com/watch?v=bcxX0R8nnDs&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=11
//https://www.youtube.com/watch?v=GZ_1xOm-3qU&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=12
precision mediump float;

in vec2 bTexCoords;
in vec3 bNormalVec;
in vec3 bToLightDirVec;
in vec3 bToCameraVec;
//in vec4 bLightSpacePos;

out vec4 finalColor;

uniform sampler2D uTex2D0;
uniform sampler2D shadowSampler;
uniform vec3 uLightColor;
uniform float uAmbientFactor;

uniform float uShineDamper;
uniform float uReflectivity;

//uniform float uLightAttenuationConstant;
//uniform float uLightAttenuationLinear;
//uniform float uLightAttenuationQuadratic;

uniform vec4 uHighlightColor;
//uniform mat4 uLightMVP;

//float calculateShadowFactor(vec4 lightSpacePos){
//    vec3 proPos = lightSpacePos.xyz/lightSpacePos.w;
//    vec2 uvCoord;
//    uvCoord.x = 0.5*proPos.x+0.5;
//    uvCoord.y = 0.5*proPos.y+0.5;
//    float z = 0.5*proPos.z + 0.5;
//    float bias = 0.0001;
//    float depth = texture(shadowSampler, uvCoord).x;
//    if(depth < z + bias){
//        return 0.2;
//    }else{
//        return 1.0;
//    }
//}

void main()
{

    vec3 unitNormal = normalize(bNormalVec);
    vec3 unitToLightDir = normalize(bToLightDirVec);

    float nDot = dot(unitNormal, unitToLightDir);
    float brightness = max(nDot, uAmbientFactor);
    vec3 diffuse = brightness * uLightColor;

    vec3 unitToCameraVec = normalize(bToCameraVec);
    vec3 lightDirection = -unitToCameraVec;
    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
    float specularFactor = dot(reflectedLightDirection, unitToCameraVec);
    specularFactor = max(specularFactor, 0.0);
    float dampedFactor = pow(specularFactor, uShineDamper);
    vec3 finalSpecular = dampedFactor * uReflectivity * uLightColor;
//    float shadowFactor = calculateShadowFactor(bLightSpacePos);

    finalColor = vec4(diffuse/**shadowFactor*/, 1.0) *texture(uTex2D0, bTexCoords) + vec4(finalSpecular, 1.0) + vec4(uHighlightColor.xyz, uHighlightColor.w);
}