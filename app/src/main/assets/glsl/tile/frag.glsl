#version 300 es
precision mediump float;

in vec2 bTexCoords;

out vec4 finalColor;

uniform sampler2D uTex2D0;
uniform vec4 uHighlightColor;

void main()
{

	finalColor = texture(uTex2D0, bTexCoords) + uHighlightColor;

}