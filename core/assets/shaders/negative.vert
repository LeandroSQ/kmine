attribute vec2 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord;

uniform mat4 u_projModelView;
varying vec4 v_col;
varying vec2 v_tex0;

void main() {
   gl_Position = u_projModelView * vec4(a_position, 0.0, 1.0);
   gl_PointSize = 1.0;
   v_col = a_color;
   v_col.a *= 255.0 / 254.0;
   v_tex0= a_texCoord;
}