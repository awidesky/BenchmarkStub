package com.awidesky;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public enum Lists {
	ARRAYLIST(ArrayList<Integer>::new),
	LINKEDLIST(LinkedList<Integer>::new);

	Supplier<List<Integer>> supp;
	
	Lists(Supplier<List<Integer>> supp) {
		this.supp = supp;
	}
	
	public List<Integer> generate() { return supp.get(); }
}
