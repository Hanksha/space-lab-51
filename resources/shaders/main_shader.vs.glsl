#version 150 core
in vec4 in_position;
in vec4 in_texCoord;
in vec4 in_color;

out vec2 v_texCoord;
out vec4 v_color;

uniform mat4 projection;
uniform mat4 modelView;

void main() {
	v_texCoord = in_texCoord.xy;
	v_color = in_color;
	
	gl_Position = projection * modelView * in_position;		
}