package csci582_hw5;


public class CSGCubeNode implements CSGNode {
	private Cube cube;
	
	public CSGCubeNode(Cube cube) {
		assert(cube != null) : "Cube cannot be null";
		this.cube = cube;
	}

	public Cube getCube() {
		return cube;
	}
	
	public CSGCubeNode setCube(Cube cube) {
		assert(cube != null) : "Cube cannot be null";
		this.cube = cube;
		return this;
	}
}
