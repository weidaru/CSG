package csci582_hw5.pathplan;

import java.util.Set;
import java.util.TreeSet;

public class Component {
	private Set<Node> nodeSet;
	
	public Component() {
		nodeSet = new TreeSet<Node>();			//Replace with HashSet if node search becomes bottleneck.
	}
	
	public boolean contains(Node n) {
		return nodeSet.contains(n);
	}
	
	public Component add(Node n) {
		nodeSet.add(n);
		return this;
	}
	
	public void merge(Component other) {
		this.nodeSet.addAll(other.nodeSet);
	}
}
