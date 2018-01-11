package com.actiance.test.exporterNew.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class RecursiveFileDumpRename {
	
	public static void main(String[] args) {
		 File file = new File("E:\\VijayTest\\Test");
//		 pass file one by one 
		 for(File files:file.listFiles()){
			 renameFile(files);
			 if(files.isDirectory()){
				 files.delete();
			 }
		 }
	}
	
	public static ArrayList<Integer> changeFileName(String filePath){
		 File file = new File(filePath);
		 ArrayList<Integer> getInterIds = new ArrayList<Integer>();
//		 pass file one by one 
		 for(File files:file.listFiles()){
			 getInterIds.addAll(renameFile(files));
			 if(files.isDirectory()){
				 files.delete();
			 }
		 }
		 return getInterIds;
	}
	public static ArrayList<Integer> renameFile(File file){
		String subject=null;
		ArrayList<Integer> interIds = new ArrayList<>();
//		if the file is a directory then recursively call the function
		if(file.isDirectory()){
			for(File tempFile:file.listFiles()){
				
				renameFile(tempFile);
			}
		}
//		rename the file.
		else{
			 InputStream source;

	      try{
	    	    source = new FileInputStream(file);
		          Properties props = System.getProperties();
		          props.put("mail.host", "smtp.dummydomain.com");
		          props.put("mail.transport.protocol", "smtp");
		          Session mailSession = Session.getDefaultInstance(props, null);
		          MimeMessage message = new MimeMessage(mailSession, source);
		         subject = message.getSubject();
		         Integer interId = Integer.valueOf(subject.split("#")[1]);
//		         System.out.println(interId);
		         interIds.add(interId);
		         source.close();
	      }
	      catch(Exception e){
	    	  
	      }
//	        System.out.println(file.getParentFile());
	         if(file.renameTo(new File("D:\\VijayTest\\Test"+"\\"+subject+".eml"))){
//				 System.out.println("renamed");
			 }
			 else{
				 System.out.println("not renamed");
			 }
		}
		return interIds;
	}
	
}
