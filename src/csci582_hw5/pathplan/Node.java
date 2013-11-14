package csci582_hw5.pathplan;

import java.util.Random;

import javax.vecmath.Point3f;

public class Node {
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
		y = y_min + (x_max-x_min)*r.nextFloat();
		z = z_min + (x_max-x_min)*r.nextFloat();
		return new Node(x, y, z);
	}
}
