#version 150 core
in vec2 v_texCoord;
in vec4 v_color;

out vec4 color;

uniform sampler2D u_colortexture;
uniform sampler2D u_lightmap;

uniform vec2 u_resolution;

uniform vec4 u_color;

void main() {
	
	color = (texture(u_colortexture, v_texCoord)) * v_color;
	
	vec2 lightCoord = (gl_FragCoord.xy / u_resolution.xy);
	vec4 lightColor = texture(u_lightmap, lightCoord);
	
	
	color.rgb = color.rgb * lightColor.rgb;
}
