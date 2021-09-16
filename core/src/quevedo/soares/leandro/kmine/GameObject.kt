package quevedo.soares.leandro.kmine

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Array

class GameObject : ModelInstance {

    private var bounds = BoundingBox()

    constructor(model: Model, node: String, mergeTransform: Boolean) : super(model, node, mergeTransform) {
        this.calculateBoundingBox(this.bounds)
    }

}