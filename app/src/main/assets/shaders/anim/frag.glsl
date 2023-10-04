#version 300 es

precision mediump float;

in vec2 bTexCoord;
in vec3  bNormal;

out vec4 fragColor;

uniform sampler2D diffuseMap;
uniform vec3 lightDirection;

const vec4 LIGHT_COLOR = vec4(1,1,1,1);

void main(){

    vec4 diffuseColor = texture(diffuseMap, bTexCoord);
    vec3 unitNormal = normalize(bNormal);
    float diffuseLight = max(dot(-lightDirection, unitNormal), 0.5);
    fragColor = diffuseLight*diffuseColor*LIGHT_COLOR;

}

