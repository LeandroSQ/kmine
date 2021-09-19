#ifdef GL_ES
	precision mediump float;
#endif

uniform sampler2D u_texture;

varying vec2 v_texCoords;

void main() {
	gl_FragColor = texture2D(u_texture, v_texCoords);
}