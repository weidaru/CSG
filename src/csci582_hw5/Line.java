package csci582_hw5;

import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

public class Line {
	private Point3f startPoint, endPoint;
	private float startParam, endParam;
	
	public Line(Point3f startPoint, Point3f endPoint) {
		this.startPoint = new Point3f(startPoint);
		this.endPoint = new Point3f(endPoint);
		this.startParam = 0.0f;
		this.endParam = 1.0f;
	}
	
	public float getStartParam() {
		return startParam;
	}
	
	public Line setStartParam(float startParm) {
		assert(startParam <= this.endParam);
		this.startParam = startParm;
		return this;
	}
	
	public float getEndParam() {
		return endParam;
	}
	
	public Line setEndParam(float endParam) {
		assert(endParam >= this.startParam);
		this.endParam = endParam;
		return this;
	}
	
	public Point3f getStartPoint() {
		return lerp(startPoint, endPoint, startParam);
	}
	
	public Point3f getStartPointRaw() {
		return startPoint;
	}
	
	public Point3f getEndPoint() {
		return lerp(startPoint, endPoint, endParam);
	}
	
	public Point3f getEndPointRaw() {
		return endPoint;
	}
	

	public Shape3D toShape3D() {
		Point3f[] pointArray = new Point3f[2];
		pointArray[0] = getStartPoint();
		pointArray[1] = getEndPoint();
		LineArray lineArray = new LineArray(2, LineArray.COORDINATES);
		lineArray.setCoordinates(0, pointArray);

		return new Shape3D(lineArray);
	}
	
	public Line copy() {
		Line c = new Line(startPoint, endPoint).
				setStartParam(startParam).
				setEndParam(endParam);
		return c;
	}
	
	public void transform(Matrix4f transform) {
		transform.transform(startPoint);
		transform.transform(endPoint);
	}
	
	public static Point3f lerp(Point3f p1, Point3f p2, float u) {
		Point3f result = new Point3f();
		result.x = p1.x * (1-u) + p2.x * u;
		result.y = p1.y * (1-u) + p2.y * u;
		result.z = p1.z * (1-u) + p2.z * u;
		
		return result;
	}
	
}
