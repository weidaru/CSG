package csci582_hw5;

import java.util.LinkedList;

public class LineClassification {
	public enum LineClass {IN, ON, OUT, UNDEF};
	//Close, Open
	private LinkedList<Pair<Float,LineClass>> classes;
	private Line line;
	
	public LineClassification(Line line) {
		this.line = line;
		classes = new LinkedList<Pair<Float,LineClass>>();
		classes.add(new Pair<Float, LineClass>(line.getStartParam(), LineClass.OUT));
		
	}
	
	public LineClass query(float param) {
		if(param < line.getStartParam() || param > line.getEndParam())
			return LineClass.UNDEF;
		for(int i=1; i<classes.size(); i++) {
			Pair<Float, LineClass> pair = classes.get(i);
			if(param < pair.first()) {
				return classes.get(i-1).second();
			}
		}
		
		return classes.get(classes.size()-1).second();
	}
	
	public void add(float param, LineClass c) {
		assert(param > classes.get(classes.size()-1).first());
		classes.add(new Pair<Float, LineClass>(param, c) );
	}
	
	public LineClassification union(LineClassification other) {
		//stub
		return null;
	}
	
	public LineClassification difference(LineClassification other) {
		//stub
		return null;
	}
	
	public LineClassification intersection(LineClassification other) {
		//stub
		return null;
	}
	
	public Line[] generateIn() {
		return null;
	}
	
	public Line[] generateOn() {
		return null;
	}
	
	public Line[] generateOut() {
		return null;
	}
	
	
}














