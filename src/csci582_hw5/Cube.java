package csci582_hw5;

import javax.media.j3d.Group;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;

public class Cube {
	private float xDim, yDim, zDim;
	private Line edges[];
	
	
	//All dimensions are half the total length of line in that axis.
	public Cube(float xDim, float yDim, float zDim) {
		this.xDim = xDim;
		this.yDim = yDim;
		this.zDim = zDim;
		
		buildEdges();
	}
	
	public float getXDimension() {
		return xDim;
	}
	
	public float getYDimension() {
		return yDim;
	}
	
	public float getZDimension() {
		return zDim;
	}
	
	public Line getEdge(int index) {
		assert(index < 12 && index >= 0);
		return edges[index];
	}
	
	public Line[] getEdge() {
		return edges;
	}
	
	public Group toJava3dGroup() {
		Group group = new Group();
		
		for(int i=0; i<12; i++) {
			Shape3D l = edges[i].toShape3D();
			group.addChild(l);
		}
		
		return group;
	}
	
	
	private void buildEdges() {
		edges = new Line[12];
		
		float x = 2*xDim, y = 2*yDim, z = 2*zDim;
		Point3f bdl = new Point3f(0.0f, 0.0f, 0.0f);
		Point3f bdr = new Point3f(x, 0.0f, 0.0f);
		Point3f bur = new Point3f(x, y, 0.0f);
		Point3f bul = new Point3f(0.0f, y, 0.0f);
		
		Point3f fdl = new Point3f(0.0f, 0.0f, z);
		Point3f fdr = new Point3f(x, 0.0f, z);
		Point3f fur = new Point3f(x, y, z);
		Point3f ful = new Point3f(0.0f, y, z);
		
		//Back face edges
		edges[0] = new Line(bdl, bdr);
		edges[1] = new Line(bdr, bur);
		edges[2] = new Line(bur, bul);
		edges[3] = new Line(bul, bdl);
		
		//Front face edges
		edges[4] = new Line(fdl, fdr);
		edges[5] = new Line(fdr, fur);
		edges[6] = new Line(fur, ful);
		edges[7] = new Line(ful, fdl);
		
		//Other edges
		edges[8] = new Line(bdl, fdl);
		edges[9] = new Line(bdr, fdr);
		edges[10] = new Line(bur, fur);
		edges[11] = new Line(bul, ful);
	}
}
