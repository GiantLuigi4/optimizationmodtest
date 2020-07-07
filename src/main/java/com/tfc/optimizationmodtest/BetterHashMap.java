package com.tfc.optimizationmodtest;

import java.util.ArrayList;

public class BetterHashMap<A,B> {
	private ArrayList<A> keys=new ArrayList<>();
	private ArrayList<B> vals=new ArrayList<>();
	
	public void add(A a,B b) {
		if (keys.contains(a)) {
			vals.set(keys.indexOf(a),b);
		} else {
			keys.add(a);
			vals.add(b);
		}
	}
	
	public void clear() {
		keys.clear();
		vals.clear();
	}
	
	public B get(A key) {
		return vals.get(keys.indexOf(key));
	}
	
	public boolean containsKey(A a) {
		return keys.contains(a);
	}
	
	public int size() {
		return keys.size();
	}
}
