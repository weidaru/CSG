package csci582_hw5;

public class CSGOpNode implements CSGNode {
	public enum OpCode { UNION, DIFFERENCE, INTERSECTION };
	
	private OpCode opCode;
	private CSGNode left, right;
	
	public CSGOpNode(OpCode code, CSGNode l, CSGNode r) {
		assert(l!=null && r!=null) : "All CSGOpNode should be binary, left and right cannot be null";
		opCode = code;
		left = l;
		right = r;
	}
	
	public OpCode getOpCode() {
		return opCode;
	}

	public CSGNode getLeft() {
		return left;
	}
	
	public CSGNode getRight() {
		return right;
	}

	public CSGNode setLeft(CSGNode left) {
		assert(left != null) : "Left must not be null.";
		this.left = left;
		return this;
	}
	
	public CSGNode setRight(CSGNode right) {
		assert(right != null) : "Right must not be null.";
		this.right = right;
		return this;
	}

}
