package csci582_hw5;

import java.util.LinkedList;

import javax.media.j3d.Group;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

import csci582_hw5.LineClassification.LineClass;

public class CSGOperation {
	public static Sphere calculateBoundingSphere(CSGNode node) {
		if(node == null)
			return new Sphere();
		
		Sphere result = null;
		
		if(node instanceof CSGOpNode) {
			CSGOpNode node_op = (CSGOpNode)node;
			if(node_op.getOpCode() == CSGOpNode.OpCode.INTERSECTION) {
				result = calculateBoundingSphere(node_op.getLeft());
				result = result.intersect(calculateBoundingSphere(node_op.getRight()));
			}
			//Ignore the margin case
			else if(node_op.getOpCode() == CSGOpNode.OpCode.UNION ||
					node_op.getOpCode() == CSGOpNode.OpCode.DIFFERENCE) {
				result = calculateBoundingSphere(node_op.getLeft());
				result =result.union(calculateBoundingSphere(node_op.getRight()));
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
			LineClassification c = lineCSGClassification(cur ,node);
			LinkedList<Line> on = c.generateOn();
			for(int i=0; i<on.size(); i++) {
				group.addChild(on.get(i).toShape3D());
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
	
	//assume line and cube are orthogonal
	public static LineClassification lineCSGClassification(Line line, CSGNode node) {
		LinkedList<Matrix4f> stack = new LinkedList<Matrix4f>();

		return _lineCSGClassification(line, node, stack);
	}
	
	private static LineClassification _lineCSGClassification(Line line, CSGNode node, LinkedList<Matrix4f> stack) {
		assert(node != null) : "Node cannot be null";
		LineClassification result = null;
		
		if(node instanceof CSGCubeNode) {
			Matrix4f m = new Matrix4f();
			m.setIdentity();
			for(int i=0; i<stack.size(); i++) {
				m.mul(stack.get(i));
			}
			//Do line cube intersection.
			CSGCubeNode node_cube = (CSGCubeNode)node;
			Cube cube = node_cube.getCube();

			result = lineCubeIntersection(line, cube, m);
		}
		else if(node instanceof CSGTransformNode) {
			CSGTransformNode trans_node = (CSGTransformNode)node;
			stack.addLast(trans_node.getTransformMatrix());
			result = _lineCSGClassification(line, trans_node.getChild(), stack);
			stack.removeLast();
		}
		else if(node instanceof CSGOpNode) {
			CSGOpNode node_op = (CSGOpNode)node;
			LineClassification c_left = _lineCSGClassification(line, node_op.getLeft(), stack);
			LineClassification c_right = _lineCSGClassification(line, node_op.getRight(), stack);
			
			if(node_op.getOpCode() == CSGOpNode.OpCode.UNION)
				result = c_left.union(c_right);
			else if(node_op.getOpCode() == CSGOpNode.OpCode.DIFFERENCE)
				result = c_left.difference(c_right);
			else if(node_op.getOpCode() == CSGOpNode.OpCode.INTERSECTION)
				result = c_left.intersection(c_right);
			else
				assert(false) : "Unknown OpCode";
		}
		
		return result;
	}
	
	private static LineClassification lineCubeIntersection(Line line, Cube cube, Matrix4f m) {
		LineClassification result = new LineClassification(line);
		Point3f center = cube.getCenter();
		m.transform(center);
		
		//Init face
		float epsilon = (float) 1e-6;
		float front=center.z+cube.getZDimension()+epsilon, back=center.z-cube.getZDimension()-epsilon;
		float left=center.x-cube.getXDimension()-epsilon, right=center.x+cube.getXDimension()+epsilon;
		float up=center.y+cube.getYDimension()+epsilon, down=center.y-cube.getZDimension()-epsilon;
		
		boolean isSet = false;
		
		Point3f s = line.getStartPointRaw(), e = line.getEndPointRaw();
		float s_p = line.getStartParam()-(float)1e-5, e_p = line.getEndParam()+(float)1e-5;
		
		//Intersect front and back
		if(Math.abs(e.z-s.z) > 1e-5) {
			if(s.y >= down && s.y <= up && s.x>=left && s.x<=right) {
				float u_f = (front-s.z)/(e.z-s.z);
				float u_b = (back-s.z)/(e.z-s.z);
				if(u_f > u_b) {
					float temp = u_f;
					u_f = u_b;
					u_b = temp;
				}
				if(u_f >= s_p && u_f <= e_p) {
					if(Math.abs(s.x-left) < 1e-4 || Math.abs(s.x-right) < 1e-4)
						result.add(u_f, LineClass.ON);
					else
						result.add(u_f, LineClass.IN);
				}
				else if(u_f < s_p) {
					if(Math.abs(s.x-left) < 1e-4 || Math.abs(s.x-right) < 1e-4)
						result.set(0, LineClass.ON);
					else
						result.set(0, LineClass.IN);
				}
				if(u_b >= s_p && u_b <= e_p) {
					result.add(u_b, LineClass.OUT);
				}
				else if(u_b < s_p) {
					result.set(0, LineClass.OUT);
				}
			}
		}
		//Intersect left and right
		if(Math.abs(e.x - s.x) > 1e-5) {
			assert(isSet == false) : "Line must be orthogonal";
			if(s.y >= down && s.y <= up && s.z>=back && s.z<=front) {
				float u_f = (left-s.x)/(e.x-s.x);
				float u_b = (right-s.x)/(e.x-s.x);
				if(u_f > u_b) {
					float temp = u_f;
					u_f = u_b;
					u_b = temp;
				}
				if(u_f >= s_p && u_f <= e_p) {
					if(Math.abs(s.z-front) < 1e-4 || Math.abs(s.z-back) < 1e-4)
						result.add(u_f, LineClass.ON);
					else
						result.add(u_f, LineClass.IN);
				}
				else if(u_f < s_p) {
					if(Math.abs(s.z-front) < 1e-4 || Math.abs(s.z-back) < 1e-4)
						result.set(0, LineClass.ON);
					else
						result.set(0, LineClass.IN);
				}
				if(u_b >= s_p && u_b <= e_p) {
					result.add(u_b, LineClass.OUT);
				}
				else if(u_b < s_p) {
					result.set(0, LineClass.OUT);
				}
			}
		}
		//Intersect up and down
		if(Math.abs(e.y - s.y) > 1e-5) {
			assert(isSet == false) : "Line must be orthogonal";
			if(s.z >= back && s.z <= front && s.x>=left && s.x<=right) {
				float u_f = (up-s.y)/(e.y-s.y);
				float u_b = (down-s.y)/(e.y-s.y);
				if(u_f > u_b) {
					float temp = u_f;
					u_f = u_b;
					u_b = temp;
				}
				if(u_f >= s_p && u_f <= e_p) {
					if(Math.abs(s.y-up) < 1e-4 || Math.abs(s.y-down) < 1e-4)
						result.add(u_f, LineClass.ON);
					else
						result.add(u_f, LineClass.IN);
				}
				else if(u_f < s_p) {
					if(Math.abs(s.y-up) < 1e-4 || Math.abs(s.y-down) < 1e-4)
						result.set(0, LineClass.ON);
					else
						result.set(0, LineClass.IN);
				}
				if(u_b >= s_p && u_b <= e_p) {
					result.add(u_b, LineClass.OUT);
				}
				else if(u_b < s_p) {
					result.set(0, LineClass.OUT);
				}
			}
		}
		assert(result.count()<=3);
		
		result.unify();
		return result;
	}
	
	/* for test use
	public static void main(String[] argv) {
		Cube cube = new Cube(0.5f, 0.5f, 0.5f);
		Point3f start = new Point3f(0.0f, 0.5f, 0.5f);
		Point3f end = new Point3f(1.5f, 0.5f, 0.5f);
		Line line = new Line(start, end);
		
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		LineClassification result = lineCubeIntersection(line, cube, m);
		System.out.println(result.toString());
	}
	*/
}











