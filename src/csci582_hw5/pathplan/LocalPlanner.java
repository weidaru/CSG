package csci582_hw5.pathplan;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.vecmath.Point3f;

import csci582_hw5.Line;
import csci582_hw5.LineClassification;
import csci582_hw5.csg.CSGNode;
import csci582_hw5.csg.CSGOperation;
import csci582_hw5.csg.CSGOperation.PointClass;
import csci582_hw5.pathplan.Node.Axis;

public class LocalPlanner {
	private CSGNode scene;
	private Map<Edge, LinkedList<Line>> edgeCache;
	
	public LocalPlanner(CSGNode scene) {
		this.scene = scene;
		edgeCache = new TreeMap<Edge, LinkedList<Line>>();
	}
	
	public LocalPlanner() {
		scene = null;
		edgeCache = new TreeMap<Edge, LinkedList<Line>>();
	}
	
	public void clear() {
		scene = null;
		edgeCache.clear();
	}
	
	public LocalPlanner setScene(CSGNode scene) {
		clear();
		this.scene = scene;
		return this;
	}
	
	public CSGNode getScene() {
		return scene;
	}
	
	public boolean isConnected(Node lhs, Node rhs) {	
		Edge e = new Edge(lhs, rhs);
		if(edgeCache.containsKey(e))
			return true;
		LinkedList<Line> lineList = buildConnection(lhs, rhs);
		if(lineList == null)
			return false;
		else {
			edgeCache.put(e, lineList);
			return true;
		}
	}
	
	public LinkedList<Line> getConnection(Node lhs, Node rhs) {
		Edge e = new Edge(lhs, rhs);
		if(!isConnected(lhs, rhs))
			return null;
		assert(edgeCache.containsKey(e));
		return edgeCache.get(e);
	}
	
	public LinkedList<Line> getCachedConnection(Edge e) {
		return edgeCache.get(e);
	}

	private LinkedList<Line> buildConnection(Node lhs, Node rhs) {
		Axis dir[] = new Axis[3];
		LinkedList<Line> result = new LinkedList<Line>();
		
		dir[0]=Axis.X;
		dir[1]=Axis.Y;
		dir[2]=Axis.Z;
		if(_buildConnection(lhs, rhs, dir, result))
			return result;
		result.clear();
		
		dir[0]=Axis.X;
		dir[1]=Axis.Z;
		dir[2]=Axis.Y;
		if(_buildConnection(lhs, rhs, dir, result))
			return result;
		result.clear();
		
		dir[0]=Axis.Y;
		dir[1]=Axis.X;
		dir[2]=Axis.Z;
		if(_buildConnection(lhs, rhs, dir, result))
			return result;
		result.clear();
		
		dir[0]=Axis.Y;
		dir[1]=Axis.Z;
		dir[2]=Axis.X;
		if(_buildConnection(lhs, rhs, dir, result))
			return result;
		result.clear();
		
		dir[0]=Axis.Z;
		dir[1]=Axis.X;
		dir[2]=Axis.Y;
		if(_buildConnection(lhs, rhs, dir, result))
			return result;
		result.clear();
		
		dir[0]=Axis.Z;
		dir[1]=Axis.Y;
		dir[2]=Axis.X;
		if(_buildConnection(lhs, rhs, dir, result))
			return result;
		
		return null;
	}
	
	private boolean _buildConnection(Node lhs, Node rhs, Axis[] dir, LinkedList<Line> result) {
		float epsilon = (float)1e-5;
		Point3f p1 = lhs.getPosition();
		Point3f p2 = movePoint(p1, dir[0], rhs.get(dir[0]));
		Line l = new Line(p1, p2);
		if(l.length() > epsilon) {
			LineClassification c = CSGOperation.lineCSGClassification(l, scene);
			if(!c.isOut())
				return false;
			else
				result.add(l);
		}
		Point3f p3 = movePoint(p2, dir[1], rhs.get(dir[1]));
		l = new Line(p2,p3);
		if(l.length() > epsilon) {
			LineClassification c = CSGOperation.lineCSGClassification(l, scene);
			if(!c.isOut())
				return false;
			else
				result.add(l);
		}
		Point3f p4 = movePoint(p3, dir[2], rhs.get(dir[2]));
		l = new Line(p3,p4);
		if(l.length() > epsilon) {
			LineClassification c = CSGOperation.lineCSGClassification(l, scene);
			if(!c.isOut())
				return false;
			else
				result.add(l);
		}
		
		return true;
	}
	
	private Point3f movePoint(Point3f p, Axis a, float newValue) {
		Point3f newP = new Point3f();
		newP.x = p.x;
		newP.y = p.y;
		newP.z = p.z;
		switch(a) {
		case X:
			newP.x = newValue;
			break;
		case Y:
			newP.y = newValue;
			break;
		case Z:
			newP.z = newValue;
			break;
		default:
			assert(false) : "Unknown Axis";
		}
		return newP;
	}
	
	public boolean isCollided(Node node) {
		PointClass c = CSGOperation.pointCSGClassification(node.getPosition(), scene);
		if(c == PointClass.OUT)
			return false;
		else
			return true;
	}
	

}
