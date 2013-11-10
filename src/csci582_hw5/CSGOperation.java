package csci582_hw5;

import java.util.LinkedList;

import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

public class CSGOperation {
	public static Sphere calculateBoundingSphere(CSGNode node) {
		if(node == null)
			return new Sphere();
		
		Sphere result = null;
		
		if(node instanceof CSGOpNode) {
			CSGOpNode node_op = (CSGOpNode)node;
			if(node_op.getOpCode() == CSGOpNode.OpCode.INTERSECTION) {
				result = calculateBoundingSphere(node_op.getLeft());
				result.intersect(calculateBoundingSphere(node_op.getRight()));
			}
			//Ignore the margin case
			else if(node_op.getOpCode() == CSGOpNode.OpCode.UNION ||
					node_op.getOpCode() == CSGOpNode.OpCode.DIFFERENCE) {
				result = calculateBoundingSphere(node_op.getLeft());
				result.union(calculateBoundingSphere(node_op.getRight()));
			}
		}
		else if(node instanceof CSGTransformNode) {
			
			CSGTransformNode node_trans = (CSGTransformNode)node;
			result = calculateBoundingSphere(node_trans.getChild());
			
			Point3f leftPoint = new Point3f(result.center);
			leftPoint.x-=result.radius;
			Point3f rightPoint = new Point3f(result.center);
			rightPoint.x+=result.radius;
			
			Matrix4f tempMatrix = node_trans.getTransformMatrix();
			tempMatrix.transform(leftPoint);
			tempMatrix.transform(rightPoint);
			result.radius = leftPoint.distance(rightPoint)/2.0f;
			result.center.x = (leftPoint.x + rightPoint.x)/2.0f;
			result.center.y = (leftPoint.y + rightPoint.y)/2.0f;
			result.center.z = (leftPoint.z + rightPoint.z)/2.0f;
		}
		else if(node instanceof CSGCubeNode) {
			result = new Sphere();
			Cube e_cube = ((CSGCubeNode)node).getCube();
			float x = e_cube.getXDimension();
			float y = e_cube.getYDimension();
			float z = e_cube.getZDimension();
			result.center.x = e_cube.getXDimension();
			result.center.y = e_cube.getYDimension();
			result.center.z = e_cube.getZDimension();
			result.radius = (float)Math.sqrt(x*x+y*y+z*z);
		}
		else 
			assert(false) : "Unknown CSGNode type " + node.getClass().getName();
		
		return result;
	}
	
	public static Group generateDebugGroup(CSGNode node) {
		if(node == null)
			return new Group();
		else if(node instanceof CSGCubeNode) {
			Group group = new Group();
			CSGCubeNode cur_cube = (CSGCubeNode)node;		
			group.addChild(cur_cube.getCube().toJava3dGroup());
			
			return group;
		}
		else if(node instanceof CSGTransformNode) {
			TransformGroup tg = new TransformGroup();
			CSGTransformNode node_trans = (CSGTransformNode)node;
			Transform3D transform = new Transform3D();
			transform.set(node_trans.getTransformMatrix());
			tg.setTransform(transform);
			
			tg.addChild(generateDebugGroup(node_trans.getChild()));
			return tg;
		}
		else if(node instanceof CSGOpNode) {
			Group group = new Group();
			CSGOpNode node_op = (CSGOpNode)node;
			group.addChild(generateDebugGroup(node_op.getLeft()));
			group.addChild(generateDebugGroup(node_op.getRight()));
			
			return group;
		}
		else 
			assert(false) : "Unknown CSGNode type " + node.getClass().getName();
			
		return null;
	}
	
	public static Group generateGroup(CSGNode node) {
		//Gather all the edges (no transform).
		LinkedList<Line> edges = new LinkedList<Line>();
		LinkedList<Matrix4f> transformStack = new LinkedList<Matrix4f>();
		
		gatherEdges(node, edges, transformStack);

		TransformGroup group = new TransformGroup();
		while(!edges.isEmpty()) {
			Line cur = edges.poll();
			int result[] = lineCSGClassification(cur ,node);
			if(result[2] != result[3]) {
				cur.setStartParam(result[2]);
				cur.setEndParam(result[3]);
				group.addChild(cur.toShape3D());
			}
		}
		
		return group;
	}
	
	//All the edges get copyed and transformed.
	private static void gatherEdges(CSGNode cur, LinkedList<Line> edges, LinkedList<Matrix4f> stack) {
		if(cur == null)
			return;
		if(cur instanceof CSGCubeNode) {
			Matrix4f m = new Matrix4f();
			m.setIdentity();
			for(int i=0; i<stack.size(); i++) {
				m.mul(stack.get(i));
			}
			CSGCubeNode cur_cube = (CSGCubeNode)cur;
			Cube c =  cur_cube.getCube();
			for(int i=0; i<12; i++) {
				Line l = c.getEdge(i).copy();
				l.transform(m);
				edges.add(l);
			}
		}
		else if(cur instanceof CSGTransformNode) {
			CSGTransformNode cur_trans = (CSGTransformNode)cur;
			stack.addLast(cur_trans.getTransformMatrix());
			gatherEdges(cur_trans.getChild(), edges, stack);
			stack.removeLast();
		}
		else if(cur instanceof CSGOpNode) {
			CSGOpNode cur_op = (CSGOpNode)cur;
			gatherEdges(cur_op.getLeft(), edges, stack);
			gatherEdges(cur_op.getRight(), edges, stack);
		}
		else
			assert(false) : "Unknown CSGNode type " + cur.getClass().getName();
	}
	

	//parametric coefficient.
	public static int[] lineCSGClassification(Line line, CSGNode node) {
		LinkedList<Matrix4f> stack = new LinkedList<Matrix4f>();

		return _lineCSGClassification(line, node, stack);
	}
	
	private static int[] _lineCSGClassification(Line line, CSGNode node, LinkedList<Matrix4f> stack) {
		assert(node != null) : "Node cannot be null";
		
		if(node instanceof CSGCubeNode) {
			
		}
		else if(node instanceof CSGTransformNode) {
			
		}
		else if(node instanceof CSGOpNode) {
			
		}
		
		return null;
	}
}











