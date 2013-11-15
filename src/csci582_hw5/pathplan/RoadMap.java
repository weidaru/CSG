package csci582_hw5.pathplan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
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
	private LinkedList<Component> components;
	private Map<Node, Set<Node>> edgeMap;
	private ArrayList<Node> nodes;
	private LocalPlanner planner;
	private Sphere boundSphere = null;
	private Group groupCache = null;
	
	private float distanceThreshold = -1.0f;
	
	public int maxNode = 100;
	public int maxEdgePerNode = 30;
	
	public RoadMap() {
		components = new LinkedList<Component>();
		edgeMap = new TreeMap<Node, Set<Node>>();	//Replace with HashMap if query node becomes a bottleneck.
		nodes = new ArrayList<Node>();
		planner = new LocalPlanner();
	}
	
	public void load(CSGNode scene) {
		clear();
		planner.setScene(scene);
	
		//Start generating.
		boundSphere = CSGOperation.calculateBoundingSphere(scene);
		boundSphere.radius = boundSphere.radius * 1.2f;
		Point3f center = boundSphere.center;
		float radius = boundSphere.radius;
		float left = center.x - radius, right = center.x + radius,
			  down = center.y - radius, up = center.y + radius,
			  back = center.z - radius, front = center.z + radius;
		distanceThreshold = radius/5.0f;
		
		int maxFailCount = Math.max(5, maxNode/10);
		for(int i=0; i<maxNode; i++) {
			int failCount = 0;
			while(failCount < maxFailCount) {
				Node cur = Node.generateRandomNode(left, right, down, up, back, front);
				if(planner.isCollided(cur)) {
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
	
	public ArrayList<Line> query(Node n1, Node n2) {
		ArrayList<Line> result = new ArrayList<Line>();
		
		if(planner.isCollided(n1) || planner.isCollided(n2))
			return result;
		
		Node start = findClosestConnectedNode(n1);
		Node end = findClosestConnectedNode(n2);
		
		if(findComponent(start) != findComponent(end))
			return result;
		
		//Start A*, assume the cost is distance of moving along axis.
		//At the same time, let heuristic be Hamilton distance. 
		//The heuristic function is defined in the StateComparator.
		PriorityQueue<State> heap = new PriorityQueue<State>(nodes.size(), new StateComparator(end));
		State startState = new State(null, start, 0.0f);
		State endState = null;
		heap.add(startState);
		
		while(!heap.isEmpty()) {
			State curState = heap.poll();
			
			if(curState.node == end) {
				endState = curState;
				break;
			}
			//Expand state.
			Set<Node> edges = edgeMap.get(curState.node);
			Iterator<Node> iterator = edges.iterator();
			while(iterator.hasNext()) {
				Node other = iterator.next();
				Point3f p1 = curState.node.getPosition();
				Point3f p2 = other.getPosition();
				float cost = Math.abs(p1.x-p2.x)+Math.abs(p1.y-p2.y)+Math.abs(p1.z-p2.z);
				State newState = new State(curState, other, cost);
				heap.add(newState);
			}
		}
		
		if(endState != null) {
			//Backtrack to get the path.
			State curState = endState;
			State prevState = endState.prev;
			while(prevState != null) {
				Line l = new Line(curState.node.getPosition(), prevState.node.getPosition());
				result.add(l);
				curState = curState.prev;
				prevState = prevState.prev;
			}
		}
		
		return result;
	}
	
	private Node findClosestConnectedNode(Node node) {
		Node result = null;
		
		float minDistance = Float.MAX_VALUE;
		for(int i=0; i<nodes.size(); i++) {
			Node cur = nodes.get(i);
			if(planner.isConnected(node, cur)) {
				float d = node.getPosition().distance(cur.getPosition());
				if(minDistance > d) {
					minDistance = d;
					result = cur;
				}
			}
		}
		
		return result;
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
		groupCache = null;
		boundSphere = null;
	}
	
	/*
	 * To Java3D representation.
	 */
	public Group toGroup() {
		if(groupCache != null)
			return groupCache;
		groupCache = new Group();
				
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
			Shape3D shape = curEdge.toShape3D();
			Appearance a = new Appearance();
			ColoringAttributes ca = new ColoringAttributes(new Color3f(1.0f, 1.0f, 0.0f),ColoringAttributes.SHADE_FLAT);
			a.setColoringAttributes(ca);
			shape.setAppearance(a);
			groupCache.addChild(shape);
		}
		
		return groupCache;
	}
	
	public Sphere getBoundSphere() {
		return boundSphere;
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
	
	private class State {
		public Node node = null;
		public State prev = null;
		public float cost = -1.0f;
		
		
		public State(State prev, Node node, float cost) {
			assert(node != null) : "Node cannot be null.";
			this.prev = prev;
			this.node = node;
			this.cost = cost;
		}
	}
	
	private class StateComparator implements Comparator<State> {
		private Node destination;
		
		public StateComparator(Node dest) {
			assert(dest != null) : "Destination must not be null.";
			destination = dest;
		}
		
		@Override
		public int compare(State lhs, State rhs) {
			float utility_lhs = lhs.cost + heuristic(lhs);
			float utility_rhs = lhs.cost + heuristic(rhs);
			if(Math.abs(utility_lhs - utility_rhs) < 1e-5)
				return 0;
			else if(utility_lhs < utility_rhs)
				return -1;
			else
				return 1;
		}
		
		private float heuristic(State state) {
			Point3f p1 = state.node.getPosition();
			Point3f p2 = destination.getPosition();
			return Math.abs(p1.x-p2.x) + Math.abs(p1.y-p2.y) + Math.abs(p1.z-p2.z);
		}
	}
}














