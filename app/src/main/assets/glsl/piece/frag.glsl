#version 300 es
//https://www.youtube.com/watch?v=bcxX0R8nnDs&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=11
//https://www.youtube.com/watch?v=GZ_1xOm-3qU&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&index=12
precision mediump float;

in vec2 bTexCoords;
in vec3 bNormalVec;
in vec3 bToLightDirVec;
in vec3 bToCameraVec;

out vec4 finalColor;

uniform sampler2D uTex2D0;
uniform vec3 uLightColor;
uniform float uAmbientFactor;

uniform float uShineDamper;
uniform float uReflectivity;

//uniform float uLightAttenuationConstant;
//uniform float uLightAttenuationLinear;
//uniform float uLightAttenuationQuadratic;

uniform vec4 uHighlightColor;

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

    finalColor = vec4(diffuse, 1.0) *texture(uTex2D0, bTexCoords) + vec4(finalSpecular, 1.0) + vec4(uHighlightColor.xyz, uHighlightColor.w);
}