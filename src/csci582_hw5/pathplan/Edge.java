package csci582_hw5.pathplan;
public class Edge {
	public Node n1, n2;
	
	public Edge(Node lhs, Node rhs) {
		assert(lhs == rhs);
		n1 = lhs;
		n2 = rhs;
	}
	
	public boolean equals(Object other) {
		if(other instanceof Edge) {
			Edge otherEdge = (Edge)other;
			if(n1 == otherEdge.n1 && n2 == otherEdge.n2)
				return true;
			if(n1 == otherEdge.n2 && n2 == otherEdge.n1)
				return true;
			return false;
		}
		else
			return false;
	}
}
