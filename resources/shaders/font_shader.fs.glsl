#version 150 core
in vec2 v_texCoord;
in vec4 v_color;

out vec4 color;

uniform sampler2D u_colortexture;
uniform vec4 u_color;
uniform vec4 u_outline_color;
uniform int u_isoutline;

void main() {

	color = texture(u_colortexture, v_texCoord) * v_color;
	
	if(u_isoutline == 1 && color.a == 0.0){
		//check neighbour pixels alpha
		vec2 offset = 1.0 / textureSize(u_colortexture, 0);
		vec4 c2 = texture(u_colortexture, vec2(v_texCoord.x, v_texCoord.y - offset.y));
		vec4 c4 = texture(u_colortexture, vec2(v_texCoord.x - offset.x, v_texCoord.y));
		vec4 c5 = texture(u_colortexture, vec2(v_texCoord.x + offset.x, v_texCoord.y));
		vec4 c7 = texture(u_colortexture, vec2(v_texCoord.x, v_texCoord.y + offset.y));
			
		if(c2.a != 0.0 || c4.a != 0.0 || c5.a != 0.0 || c7.a != 0.0)
			color = vec4(u_outline_color.r, u_outline_color.g, u_outline_color.b, v_color.a);
 	}
}