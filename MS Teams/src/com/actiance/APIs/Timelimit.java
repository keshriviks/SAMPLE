package com.actiance.APIs;

public class Timelimit {
	
	public static void main(String[] args) {
		
		
		
		long startTime = System.currentTimeMillis(); //fetch starting time
		while(false||(System.currentTimeMillis()-startTime)<60*1000)
		{
		   System.out.println("Raman");
		}
		
	}

}
