package csci582_hw5.csg;

import javax.vecmath.Matrix4f;

public class CSGTransformNode implements CSGNode  {
	private Matrix4f transformMatrix;
	private CSGNode child;
	
	public CSGTransformNode(CSGNode c) {
		assert(c!=null) : "Child of CSGTransformNode cannot be null";
		
		transformMatrix = new Matrix4f();
		transformMatrix.setIdentity();
		child = c;
	}

	public Matrix4f getTransformMatrix() {
		return transformMatrix;
	}
	
	public CSGNode setTransformMatrix(Matrix4f newMatrix) {
		this.transformMatrix.set(newMatrix);
		return this;
	}
	
	public CSGNode getChild() {
		return child;
	}
	
	public CSGTransformNode setChild(CSGNode c) {
		assert(c!=null) : "Child of CSGTransformNode cannot be null";
		child = c;
		return this;
	}
	
}
