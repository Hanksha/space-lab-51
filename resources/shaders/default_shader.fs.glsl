#version 150 core
in vec2 v_texCoord;
in vec4 v_color;

out vec4 color;

uniform sampler2D u_colortexture;
uniform vec4 u_color;

void main() {
	color = (texture(u_colortexture, v_texCoord)) * v_color;
}