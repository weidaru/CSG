package csci582_hw5.csg;

import javax.vecmath.Matrix4f;

import csci582_hw5.Cube;


public class CSGBuilder {
	public static CSGNode buildCube(float x, float y, float z) {
		Cube cube = new Cube(x, y, z);
		CSGCubeNode cubeNode = new CSGCubeNode(cube);
		return cubeNode;
	}
	
	public static CSGNode transform(CSGNode lhs, Matrix4f matrix) {
		CSGTransformNode node = new CSGTransformNode(lhs);
		node.setTransformMatrix(matrix);
		return node;
	}
	
	public static CSGNode union(CSGNode lhs, CSGNode rhs) {
		CSGOpNode opNode = new CSGOpNode(CSGOpNode.OpCode.UNION, lhs, rhs);
		return opNode;
	}
	
	
	public static CSGNode intersection(CSGNode lhs, CSGNode rhs) {
		CSGOpNode opNode = new CSGOpNode(CSGOpNode.OpCode.INTERSECTION, lhs, rhs);
		return opNode;
	}
	
	public static CSGNode difference(CSGNode lhs, CSGNode rhs) {
		CSGOpNode opNode = new CSGOpNode(CSGOpNode.OpCode.DIFFERENCE, lhs, rhs);
		return opNode;
	}
}
