package csci582_hw5;

public class Pair<T1, T2> {
	private T1 t1_ = null;
	private T2 t2_ = null;
	
	public Pair() {
		
	}
	
	public Pair(T1 t1, T2 t2) {
		t1_ = t1;
		t2_ = t2;
	}
	
	public T1 first() {
		return t1_;
	}
	
	public Pair<T1, T2> setFirst(T1 t) {
		t1_ = t;
		return this;
	}
	
	public T2 second() {
		return t2_;
	}
	
	public Pair<T1, T2> setSecond(T2 t) {
		t2_ = t;
		return this;
	}
}
