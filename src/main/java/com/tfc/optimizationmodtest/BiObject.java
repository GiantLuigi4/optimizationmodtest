package com.tfc.optimizationmodtest;

public class BiObject<T,U> {
	public T object1;
	public U object2;
	
	public BiObject(T object1, U object2) {
		this.object1 = object1;
		this.object2 = object2;
	}
	
	@Override
	public String toString() {
		return "BiObject{" +
				"object1=" + object1.toString() +
				", object2=" + object2.toString() +
				'}';
	}
	
	@Override
	public boolean equals(Object obj) {
		return
				obj.getClass().equals(BiObject.class)&&((
				object1.equals(((BiObject)obj).object1)|| (
				object1.getClass().equals(((BiObject)obj).object1.getClass())&&
				object1.toString().equals(((BiObject)obj).object1.toString())
				))&&(
				object2.equals(((BiObject)obj).object2)|| (
				object2.getClass().equals(((BiObject)obj).object2.getClass())&&
				object2.toString().equals(((BiObject)obj).object2.toString())
				))||this.toString().equals(obj.toString()));
	}
}
