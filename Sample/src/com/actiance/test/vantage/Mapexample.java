package com.actiance.test.vantage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mapexample {
	
	public static void main(String[] args) {
	
	
	HashMap<Integer, String> hmap = new HashMap<>();
	
	int[] data ={1,2,4,7};
	
	for(int i=0; i<data.length; i++){
		System.out.println("Total value :"+ data.length);
	}
		
	String[] name= {"ram","soh","top","tony"};
	for(int k=0; k<name.length; k++){
		System.out.println("Total Name :"+ name.length);
	}
	
	/*int i = 1;
	int k = 1;*/
	
	for(int i=0;i<name.length;i++)
	{
	hmap.put(data[i], name[i]);
	}
	System.out.println(hmap);
	
	
	ArrayList<Integer> al = new ArrayList<Integer>();
	al.add(10);
	al.add(20);
	al.add(30);
	al.add(40);
	al.add(50);
	System.out.println(al);
	
	HashMap<Integer, ArrayList<Integer>> hm= new HashMap<Integer, ArrayList<Integer>>();
	for(int i=1; i<=al.size();i++){
		
	
	
	hm.put(1, al.get(i));
	
}
	System.out.println(hm);
	
	

}
}