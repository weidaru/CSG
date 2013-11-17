package csci582_hw5;

import java.util.LinkedList;

import javax.vecmath.Point3f;

public class LineClassification {
	public enum LineClass {IN, ON, OUT, END};
	//Close, Open
	private LinkedList<Pair<Float,LineClass>> classes;
	private Line line;
	
	public LineClassification(Line line) {
		this.line = line;
		classes = new LinkedList<Pair<Float,LineClass>>();
		classes.add(new Pair<Float, LineClass>(line.getStartParam(), LineClass.OUT));
		classes.add(new Pair<Float, LineClass>(line.getEndParam(), LineClass.END));
	}
	
	public LineClassification copy() {
		LineClassification result = new LineClassification(line);
		result.classes.clear();
		for(int i=0; i<classes.size(); i++) {
			Pair<Float, LineClass> cur = classes.get(i), n = new Pair<Float, LineClass>();
			n.setFirst(cur.first());
			n.setSecond(cur.second());
			result.classes.add(n);
		}
		return result;
	}
	
	public int query(float param) {
		if(param < line.getStartParam()-1e-5 || param > line.getEndParam()+1e-5)
			return -1;
		for(int i=1; i<classes.size(); i++) {
			Pair<Float, LineClass> pair = classes.get(i);
			if(param < pair.first()) {
				return i-1;
			}
		}
		return classes.size()-2;
	}
	
	//Add to tail.
	public void add(float param, LineClass c) {
		float last = classes.get(classes.size()-2).first();
		if(Math.abs(param - last) < 1e-4)
			param = last;
		
		assert(param >= classes.get(classes.size()-2).first());
		if(param > classes.get(classes.size()-1).first())
			return;
		classes.add(classes.size()-1,new Pair<Float, LineClass>(param, c) );
	}
	
	public int count() {
		return classes.size()-1;
	}
	
	public void set(int index, LineClass c) {
		assert(index < classes.size()-1 && index>=0);
		classes.get(index).setSecond(c);
	}
	
	public Pair<Float, LineClass> get(int index) {
		assert(index < classes.size()-1 && index>=0);
		return classes.get(index);
	}
	
	public LinkedList<Line> generateIn() {
		return generate(LineClass.IN);
	}
	
	public LinkedList<Line> generateOn() {
		return generate(LineClass.ON);
	}
	
	public LinkedList<Line> generateOut() {
		return generate(LineClass.OUT);
	}
	
	public boolean isOut() {
		boolean result = true;
		for(int i=0; i<classes.size()-1; i++) {
			Pair<Float, LineClass> pair = classes.get(i);
			if(pair.second() != LineClass.OUT)
				return false;
		}
			
		return result;
	}
	
	private LinkedList<Line> generate(LineClass c) {
		LinkedList<Line> result = new LinkedList<Line>();
		for(int i=0; i<classes.size()-1; i++) {
			Pair<Float, LineClass> cur = classes.get(i);
			if(cur.second() == c) {
				Line l = line.copy();
				l.setStartParam(cur.first());
				l.setEndParam(classes.get(i+1).first());
				result.add(l);
			}
		}
		return result;
	}
	
	public LineClassification union(LineClassification other) {
		assert(this.line == other.line) : "Line must match.";
		LineClassification result = this.copy();
		
		for(int i=0; i<other.classes.size()-1; i++) {
			Pair<Float, LineClass> cur = other.classes.get(i);
			Pair<Float, LineClass> next = other.classes.get(i+1);
			if(cur.second() == LineClass.IN) {
				result.replace(cur.first(), next.first(), LineClass.IN);
			}else if(cur.second() == LineClass.ON) {
				int index = result.query(cur.first());
				Pair<Float, LineClass> p_index = result.get(index);
				LineClass save = result.get(result.query(next.first())).second();
				
				if(p_index.second() == LineClass.OUT || p_index.second() == LineClass.ON) {
					result.classes.add(index+1, new Pair<Float, LineClass>(cur.first(), LineClass.ON));
					index++;
				}
				int end_index = result.query(next.first());
				
				for(int j=index+1; j<=end_index; j++) {
					Pair<Float, LineClass> temp = result.classes.get(j);
					if(temp.second() == LineClass.OUT)
						temp.setSecond(LineClass.ON);
				}
				result.classes.add(end_index+1, new Pair<Float, LineClass>(next.first(), save));
			}
		}
		
		result.unify();
		
		return result;
	}
	
	public LineClassification difference(LineClassification other) {
		assert(this.line == other.line) : "Line must match.";
		LineClassification result = this.copy();
		
		for(int i=0; i<other.classes.size()-1; i++) {
			Pair<Float, LineClass> cur = other.classes.get(i);
			Pair<Float, LineClass> next = other.classes.get(i+1);
			if(cur.second() == LineClass.ON) {
				result.replace(cur.first(), next.first(), LineClass.ON);
			}
			else if(cur.second() == LineClass.IN) {
				int index = result.query(cur.first());
				Pair<Float, LineClass> p_index = result.get(index);
				LineClass save = result.get(result.query(next.first())).second();
				if(p_index.second() == LineClass.IN) {
					result.classes.add(index+1, new Pair<Float, LineClass>(cur.first(), LineClass.OUT));
					index++;
				}
				else if(p_index.second() == LineClass.OUT) {
					result.classes.add(index+1, new Pair<Float, LineClass>(cur.first(), LineClass.IN));
					index++;
				}
				int end_index = result.query(next.first());
				
				for(int j=index+1; j<=end_index; j++) {
					Pair<Float, LineClass> temp = result.classes.get(j);
					if(temp.second() == LineClass.IN)
						temp.setSecond(LineClass.OUT);
					else 
						temp.setSecond(LineClass.IN);
				}
				result.classes.add(end_index+1, new Pair<Float, LineClass>(next.first(), save));
			}
				
		}
		result.unify();
		return result;
	}
	
	public LineClassification intersection(LineClassification other) {
		assert(this.line == other.line) : "Line must match.";
		LineClassification result = this.copy();
		
		for(int i=0; i<other.classes.size()-1; i++) {
			Pair<Float, LineClass> cur = other.classes.get(i);
			Pair<Float, LineClass> next = other.classes.get(i+1);
			if(cur.second() == LineClass.OUT) {
				result.replace(cur.first(), next.first(), LineClass.OUT);
			}
			else if(cur.second() == LineClass.ON) {
				int index = result.query(cur.first());
				Pair<Float, LineClass> p_index = result.get(index);
				LineClass save = result.get(result.query(next.first())).second();
				if(p_index.second() == LineClass.IN) {
					result.classes.add(index+1  , new Pair<Float, LineClass>(cur.first(), LineClass.ON));
					index++;
				}
				int end_index = result.query(next.first());
				for(int j=index+1; j<=end_index; j++) {
					Pair<Float, LineClass> temp = result.classes.get(j);
					if(temp.second() == LineClass.IN)
						temp.setSecond(LineClass.ON);
				}
				result.classes.add(end_index+1, new Pair<Float, LineClass>(next.first(), save));
			}
		}

		result.unify();
		return result;
	}

	public void replace(float start, float end, LineClass c) {
		int index = query(start);
		LineClass save = classes.get(query(end)).second();
		classes.add(index+1, new Pair<Float, LineClass>(start, c));
		index++;

		for(int j=index+1; j<classes.size(); j++) {
			if(classes.get(j).first() >= end ) {
				
				for(int k=index+1; k<j; k++)
					classes.remove(index+1);
				classes.add(index+1, new Pair<Float, LineClass>(end,save));
				break;
			}
		}
	}
	
	public void unify() {
		for(int i=1; i<classes.size(); ) {
			Pair<Float, LineClass> prev = classes.get(i-1);
			Pair<Float, LineClass> cur = classes.get(i);
			if(cur.second() == prev.second()) {
				classes.remove(i);
			}
			else if(Math.abs(cur.first()-prev.first()) < 1e-5) {
				classes.remove(i-1);
			}
			else
				i++;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<classes.size(); i++) {
			Pair<Float, LineClass> cur = classes.get(i);
			if(cur.second() == LineClass.IN) {
				sb.append("IN:");
				sb.append(cur.first());
				sb.append("\t");
			}
			else if(cur.second() == LineClass.OUT) {
				sb.append("OUT:");
				sb.append(cur.first());
				sb.append("\t");
			}
			else if(cur.second() == LineClass.ON) {
				sb.append("ON:");
				sb.append(cur.first());
				sb.append("\t");
			}
			else if(cur.second() == LineClass.END) {
				sb.append("END:");
				sb.append(cur.first());
				sb.append("\t");
			}
		}
		sb.append("\n");
		
		return sb.toString();
	}
	

	public static void main(String[] argv) {
		Point3f start = new Point3f(0.0f, 0.0f, 0.0f);
		Point3f end = new Point3f(1.0f, 0.0f, 0.0f);
		Line l = new Line(start, end);
		LineClassification c1 = new LineClassification(l);
		c1.add(0.1f, LineClass.IN);
		c1.add(0.4f, LineClass.OUT);
		c1.add(0.5f, LineClass.IN);
		LineClassification c2 = new LineClassification(l);
		c2.add(0.3f, LineClass.IN);
		c2.add(0.7f, LineClass.OUT);
		
		LineClassification result = c1.intersection(c2);
		System.out.println(result.toString());
	}

}














