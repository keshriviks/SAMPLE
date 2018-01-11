package com.actiance.APIs;

public class sample {
	
	public static void main(String[] args) {
		
		
		String a= "ssf";
		String b= "dsdfsd";
		String c= "sdfdsf";
		String query="Insert into TeamsSample(type,content,sentTime,receivedTime,TimeTaken,delay,status) values("+"'"+a+"'"+",b,c,receivetime,timetaken,delay,'status')";
		System.out.println(query);
	}

}
