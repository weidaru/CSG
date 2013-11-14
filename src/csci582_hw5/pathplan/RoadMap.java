package csci582_hw5.pathplan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Group;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import csci582_hw5.Line;
import csci582_hw5.Sphere;
import csci582_hw5.csg.CSGNode;
import csci582_hw5.csg.CSGOperation;

public class RoadMap {
	//Assume number of components are small.
	private ArrayList<Component> components;
	private Map<Node, Set<Node>> edgeMap;
	private ArrayList<Node> nodes;
	private LocalPlanner planner;
	
	public int maxNode = 100;
	public int maxEdgePerNode = 30;
	
	public RoadMap() {
		components = new ArrayList<Component>();
		edgeMap = new TreeMap<Node, Set<Node>>();	//Replace with HashMap if query node becomes a bottleneck.
		nodes = new ArrayList<Node>();
		planner = new LocalPlanner();
	}
	
	public void load(CSGNode scene) {
		clear();
		planner.setScene(scene);
	
		//Start generating.
		Sphere boundSphere = CSGOperation.calculateBoundingSphere(scene);
		boundSphere.radius = boundSphere.radius * 1.2f;
		Point3f center = boundSphere.center;
		float radius = boundSphere.radius;
		float left = center.x - radius, right = center.x + radius,
			  down = center.y - radius, up = center.y + radius,
			  back = center.z - radius, front = center.z + radius;
		float distanceThreshold = radius/5.0f;
		
		int maxFailCount = Math.max(5, maxNode/10);
		for(int i=0; i<maxNode; i++) {
			int failCount = 0;
			while(failCount < maxFailCount) {
				Node cur = Node.generateRandomNode(left, right, down, up, back, front);
				if(planner.isCollide(cur)) {
					failCount++;
					continue;
				}
				nodes.add(cur);
				Component comp = new Component();
				comp.add(cur);
				components.add(comp);
				buildEdge(cur, distanceThreshold);
				break;
			}
		}
		
	}
	
	private void buildEdge(Node node, float distanceThreshold) {
		Set<Node> edges = new TreeSet<Node>();
		SortedSet<Node> heap = new TreeSet<Node>(new AnchorNodeComparator(node));

		for(int i=0; i<nodes.size(); i++) {
			Node cur = nodes.get(i);
			if(cur == node) {
				continue;
			}
			
			float d = distance(node, cur);
			if(d < distanceThreshold && planner.isConnected(node, cur)) {
				heap.add(cur);
				while(heap.size() > maxEdgePerNode * 2) {
					heap.remove(heap.last());
				}
			}
		}
		
		Iterator<Node> iterator = heap.iterator();
		while(iterator.hasNext()) {
			if(edges.size() == maxEdgePerNode )
				break;
			Node cur = iterator.next();
			int cur_index = findComponent(cur);
			Component cur_comp = components.get(cur_index);
			if(cur_comp.contains(node)) {
				continue;
			}
			edges.add(cur);
			edgeMap.get(cur).add(node);
			//update component
			int node_index = findComponent(node);
			Component node_comp = components.get(node_index);
			cur_comp.merge(node_comp);
			components.remove(node_index);
		}

		edgeMap.put(node, edges);
	}
	
	private int findComponent(Node node) {
		int result = -1;
		for(int i=0; i<components.size(); i++) {
			Component cur = components.get(i);
			if(cur.contains(node)) {
				result = i;
				break;
			}
		}
		
		assert(result != -1) : "Node is has not been added.";
			
		return result;
	}
	
	
	public void clear() {
		components.clear();
		edgeMap.clear();
		nodes.clear();
		planner.clear();
	}
	
	/*
	 * To Java3D representation.
	 */
	public Group toGroup() {
		Group result = new Group();
				
		Set<Edge> edges = new TreeSet<Edge>();
		Set<Map.Entry<Node, Set<Node>>> entries = edgeMap.entrySet();
		Iterator<Map.Entry<Node, Set<Node>>> entryIterator = entries.iterator();
		
		while(entryIterator.hasNext()) {
			Map.Entry<Node, Set<Node>> entry = entryIterator.next();
			Node node = entry.getKey();
			Set<Node> nodeSet = entry.getValue();
			Iterator<Node> nodeIterator = nodeSet.iterator();
			while(nodeIterator.hasNext()) {
				Node cur = nodeIterator.next();
				edges.add(new Edge(cur, node));
			}
		}
		
		Iterator<Edge> edgeIterator = edges.iterator();
		while(edgeIterator.hasNext()) {
			Edge curEdge = edgeIterator.next();
			Line line = new Line(curEdge.n1.getPosition(), curEdge.n2.getPosition());
			Shape3D shape = line.toShape3D();
			Appearance a = new Appearance();
			ColoringAttributes ca = new ColoringAttributes(new Color3f(1.0f, 1.0f, 0.0f),ColoringAttributes.SHADE_FLAT);
			a.setColoringAttributes(ca);
			shape.setAppearance(a);
			result.addChild(shape);
		}
		
		return result;
	}
	
	private static float distance(Node lhs, Node rhs) {
		return lhs.getPosition().distance(rhs.getPosition());
	}
	
	private class AnchorNodeComparator implements Comparator<Node> {
		private Node anchor;
		
		public AnchorNodeComparator(Node anchor) {
			assert(anchor != null) : "Anchor cannot be null";
			this.anchor = anchor;
		}
		
		@Override
		public int compare(Node lhs, Node rhs) {
			float d_lhs = lhs.getPosition().distance(anchor.getPosition());
			float d_rhs = rhs.getPosition().distance(anchor.getPosition());
			if(Math.abs(d_lhs - d_rhs) < (float)1e-5)
				return 0;
			if(d_lhs < d_rhs)
				return -1;
			else
				return 1;
		}
	}
}














