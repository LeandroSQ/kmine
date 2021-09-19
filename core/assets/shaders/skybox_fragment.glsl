#ifdef GL_ES
	precision mediump float;
#endif

vec4 skyColor = vec4(60.0 / 255.0, 174.0 / 255.0, 243.0 / 255.0, 1.0);
vec4 fogColor = vec4(0.7, 0.7, 0.7, 1.0);

float u_fogNear = 1.1;
float u_fogFar = 2.0;

varying float v_fogDepth;

void main() {
	float fogAmount = smoothstep(u_fogNear, u_fogFar, v_fogDepth);

	gl_FragColor = mix(skyColor, fogColor, fogAmount);
}