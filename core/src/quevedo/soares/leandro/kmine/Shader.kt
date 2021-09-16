package quevedo.soares.leandro.kmine

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram

val vertexShader = """
//incoming Position attribute from our SpriteBatch
attribute vec2 Position;

//the transformation matrix of our SpriteBatch
uniform mat4 u_projView;
 
void main() {
	//transform our 2D screen space position into 3D world space
	gl_Position = u_projView * vec4(Position, 0.0, 1.0);
}
"""

val fragmentShader = """
void main() {
	//final color: return opaque red
	gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
""";

//val shaderProgram = ShaderProgram(vertexShader, fragmentShader)