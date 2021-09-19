attribute vec3 a_position;

uniform mat4 u_projTrans;
uniform mat4 u_worldView;

varying float v_fogDepth;

void main() {
   vec4 pos = vec4(a_position, 1.0);

   gl_Position = u_projTrans * u_worldView * pos;
   v_fogDepth = -(u_worldView * pos).z;
}