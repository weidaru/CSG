package csci582_hw5.csg;

import java.util.Map;
import java.util.TreeMap;

import javax.media.j3d.BranchGroup;

import csci582_hw5.Pair;

public class CSGCache {
	private Map<String, Pair<CSGNode, BranchGroup>> data;
	
	public CSGCache() {
		data = new TreeMap<String, Pair<CSGNode, BranchGroup>>();
	}
	
	public void insert(String name, CSGNode node) {
		assert(node != null) : "Null node is not allowed.";
		if(!data.containsKey(name)) {
			Pair<CSGNode, BranchGroup> pair = new Pair<CSGNode, BranchGroup>(node, null);
			data.put(name, pair);
		}
	}
	
	public void remove(String name) {
		data.remove(name);
	}
	
	public CSGNode get(String name) {
		Pair<CSGNode, BranchGroup> pair = data.get(name);
		if(pair != null)
			return pair.first();
		else
			return null;
	}
	
	public boolean contains(String name) {
		return data.containsKey(name);
	}
	
	//Assume the CSGNode will never get modified.
	public BranchGroup getCachedGroup(String name) {
		Pair<CSGNode, BranchGroup> pair = data.get(name);
		if(pair != null) {
			if(pair.second() != null)
				return pair.second();
			else {
				BranchGroup bg = new BranchGroup();
				bg.setCapability(BranchGroup.ALLOW_DETACH);
				bg.addChild(CSGOperation.generateGroup(pair.first()));
				pair.setSecond(bg);
				return pair.second();
			}
		}
		else
			return null;
	}
}













