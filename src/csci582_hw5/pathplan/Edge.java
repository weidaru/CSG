package csci582_hw5.pathplan;

import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;

public class Edge implements Comparable<Edge> {
	public Node n1, n2;
	
	public Edge(Node lhs, Node rhs) {
		assert(lhs != rhs);
		n1 = lhs;
		n2 = rhs;

	}

	public Shape3D toShape3D() {
		Point3f[] pointArray = new Point3f[2];
		pointArray[0] = n1.getPosition();
		pointArray[1] = n2.getPosition();
		LineArray lineArray = new LineArray(2, LineArray.COORDINATES);
		lineArray.setCoordinates(0, pointArray);

		return new Shape3D(lineArray);
	}

	@Override
	public int compareTo(Edge otherEdge) {
		if(this.equals(otherEdge))
			return 0;
		else {
			float d = n1.getPosition().distance(n2.getPosition());
			float otherD = otherEdge.n1.getPosition().distance(otherEdge.n2.getPosition());
			if(d < otherD)
				return -1;
			else
				return 1;
		}

	}
	
	@Override
	public boolean equals(Object other) {
		Edge otherEdge = (Edge)other;
		if((otherEdge.n1 == n1 && otherEdge.n2 == n2) ||
			(otherEdge.n2 == n1 && otherEdge.n1 == n2))
			return true;
		else
			return false;
	}
}
