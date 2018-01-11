package com.actiance.test.vantage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class mapXample {

	public static void main(String[] args) {

HashMap<Integer, String> hmap= new HashMap<Integer, String>();

 hmap.put(43, "kittu");
 hmap.put(22, "khushi");
 hmap.put(64, "anshu");
 hmap.put(3, "ram");
 hmap.put(3, "pooja");
 
 Set set = hmap.entrySet();
 Iterator iterator= set.iterator();
 while(iterator.hasNext()){
	 Map.Entry entry = (Map.Entry)iterator.next();
	 System.out.println("Key is: "+entry.getKey() +" & Value is: ");
	 System.out.println(entry.getValue());
 }
 
		String var=hmap.get(4);
		System.out.println("Value at index 2 is:+var");
		
		hmap.remove(2);
		System.out.println("Map key and value after removal:");
		Set set2 =hmap.entrySet();
		
		Iterator   itr2= set2.iterator();
		while(itr2.hasNext()){
			Map.Entry ent2 =(Map.Entry)itr2.next();
			System.out.println("Key"+ ent2.getKey() + " value : ");
			System.out.println(ent2.getValue());
		}
	}

}
