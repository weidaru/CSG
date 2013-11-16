package csci582_hw5.pathplan;

import java.util.Random;

import javax.vecmath.Point3f;

public class Node implements Comparable<Node> {
	public enum Axis {X, Y, Z};
	
	private Point3f position;
	
	public Node(float x, float y, float z) {
		position = new Point3f(x, y, z);
	}
	
	public Node() {
		position = new Point3f();
	}
	
	public Point3f getPosition() {
		return position;
	}
	
	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
	}
	
	public float getZ() {
		return position.z;
	}
	
	public float get(Axis a) {
		switch(a) {
		case X:
			return getX();
		case Y:
			return getY();
		case Z:
			return getZ();
		default:
			assert(false) : "Unknown Axis";
			return 0.0f;
		}
	}
	
	public static Node generateRandomNode(
			float x_min, float x_max,
			float y_min, float y_max,
			float z_min, float z_max) {
		assert(x_min < x_max && y_min < y_max && z_min < z_max);
		Random r = new Random();
		float x=0.0f, y=0.0f, z=0.0f;
		x = x_min + (x_max-x_min)*r.nextFloat();
		y = y_min + (y_max-y_min)*r.nextFloat();
		z = z_min + (z_max-z_min)*r.nextFloat();
		return new Node(x, y, z);
	}

	@Override
	public int compareTo(Node otherNode) {
		if(this.equals(otherNode))
			return 0;
		else if(otherNode.getX() < getX())
			return -1;
		else 
			return 1;
		
	}
	
	public boolean equals(Object other) {
		Node otherNode = (Node)other;
		if(otherNode.getPosition().distance(getPosition()) < 1e-5)
			return true;
		else
			return false;
	}
}
