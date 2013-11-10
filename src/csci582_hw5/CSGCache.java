package csci582_hw5;

import java.util.Map;
import java.util.TreeMap;

import javax.media.j3d.Group;

public class CSGCache {
	private Map<String, Pair<CSGNode, Group>> data;
	
	public CSGCache() {
		data = new TreeMap<String, Pair<CSGNode, Group>>();
	}
	
	public void insert(String name, CSGNode node) {
		assert(node != null) : "Null node is not allowed.";
		if(!data.containsKey(name)) {
			Pair<CSGNode, Group> pair = new Pair<CSGNode, Group>(node, null);
			data.put(name, pair);
		}
	}
	
	public void remove(String name) {
		data.remove(name);
	}
	
	public CSGNode get(String name) {
		Pair<CSGNode, Group> pair = data.get(name);
		if(pair != null)
			return pair.first();
		else
			return null;
	}
	
	public boolean contains(String name) {
		return data.containsKey(name);
	}
	
	//Assume the CSGNode will never get modified.
	public Group getCachedGroup(String name) {
		Pair<CSGNode, Group> pair = data.get(name);
		if(pair != null) {
			if(pair.second() != null)
				return pair.second();
			else {
				pair.setSecond(CSGOperation.generateDebugGroup(pair.first()));
				return pair.second();
			}
		}
		else
			return null;
	}
}













