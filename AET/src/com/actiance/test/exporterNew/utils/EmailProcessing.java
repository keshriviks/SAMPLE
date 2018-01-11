package com.actiance.test.exporterNew.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class EmailProcessing {

	static EmailDBUtils db = new EmailDBUtils();
//	entry point for Email
//	Processing the Email Modality For Bloomberg Email's
	
//	entry point for Email
//	Processing the Email Modality for Reuter and Bloomberg Email only
	/**
	 * 	
	 * @param stmt
	 * @param networkName (Pass parameter as : loomberg, Reuter)
	 * @param emlFilePath
	 * @return
	 */
	public HashMap<Integer,ArrayList<String>> processingEmails(Statement stmt, String networkName, String emlFilePath){
		ArrayList<Integer> interIds = new ArrayList();
		HashMap<Integer,HashMap<String,ArrayList<String>>> emlData;
		HashMap<Integer,HashMap<String,ArrayList<String>>> dbData;
		
		HashMap<Integer,ArrayList<String>> result = new HashMap();
		String tempEmlFromDB ;
		File emlLocation = new File(emlFilePath);

		
		if(networkName.contains("Reuter")){
			getEmlFromEml(emlFilePath, interIds);
		}else{
			renameFile(emlLocation, interIds);
		}
		tempEmlFromDB = createMultipleEmls(stmt, interIds, emlFilePath);
		try{
			emlData = parseEml(emlFilePath,networkName);
			dbData = parseEml(tempEmlFromDB,networkName);
			System.out.println("\n ------------------Verification is Started for All Interactions----------------------\n\n");
			result = verifyEmails(emlData, dbData);
			System.out.println("\n ------------------Verification is Finished for All Interactions----------------------");
			
			deleteTempDirectory(tempEmlFromDB);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	
//	create temp emls
	
	public String createMultipleEmls(Statement stmt, ArrayList<Integer> interIds, String filePath){
		filePath = filePath+"/tmp";
		try {
			if(!new File(filePath).exists()){
				boolean createDirectory = new File(filePath).mkdirs();	
			}
			for(Integer interId : interIds){
				creationEmlFromDB(stmt, interId, filePath);
			}
		}catch(Exception ie){
				System.out.println("Not able to create temp Directory");
		}
		return filePath;
	}
	
//Parsing the Emls 
	public HashMap<Integer,HashMap<String,ArrayList<String>>> parseEml(String filePath, String networkName){
		
		HashMap<Integer,HashMap<String, ArrayList<String>>> result = new HashMap();
		HashMap<String,ArrayList<String>> temp;
		ArrayList<String> dataList;
		ArrayList<String> fileList;
		ArrayList<String> participantList;
		InputStream source = null;
		String subject;
		File allFile[] = new File(filePath).listFiles();
		ArrayList<File> files = new ArrayList();
		for(File file : allFile){
			if(!file.isDirectory()){
				files.add(file);
			}
		}
	      try{
	    	  	 for(File file : files){
	    	  		  temp =  new HashMap();
			          dataList = new ArrayList();
			          fileList = new ArrayList();
	    	  		  source = new FileInputStream(file);
			          Properties props = System.getProperties();
			          props.put("mail.host", "smtp.dummydomain.com");
			          props.put("mail.transport.protocol", "smtp");
			          Session mailSession = Session.getDefaultInstance(props, null);
			          MimeMessage message = new MimeMessage(mailSession, source);
			          subject = file.getName().replaceAll(".eml", "");
			          
//			          System.out.println(subject);
			          Object content = message.getContent();
			          
			          Multipart multipart  = (Multipart)content;

			          for(int i=0; i<multipart.getCount();i++){
			        	  
			    		   BodyPart bodyPart = multipart.getBodyPart(i);
			    		   if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
					               !StringUtils.isNotBlank(bodyPart.getFileName())) {
			    			   if(!networkName.contains("Reuter")){
			    				   dataList.add(multipart.getBodyPart(i).getContent().toString().replaceAll("\n", "").replaceAll("\r", ""));   
			    			   }else{
			    				   dataList.add(dumpPart(multipart.getBodyPart(i)));
			    			   }
			    			   
					        }else{
					        	fileList.add(bodyPart.getFileName());
					        }
			          }
			          participantList = getParticipants(file.getAbsolutePath());
//			          System.out.println(subject.split("#")[1]);
			          Integer interId = Integer.valueOf(subject.split("#")[1]);
			          source.close();
			          temp.put("messages", dataList);
			          temp.put("files", fileList);
			          if(participantList.size() > 0){
			        	  temp.put("participants", participantList);
			          }
			          result.put(interId, temp);
			          source.close();
	    	  	 }
	    	  	 
	      }
	      catch(Exception e){
	    	System.out.println("Not Able to Parse the Eml");  
	      }
		
		return result;
	}
	
	public void creationEmlFromDB(Statement stmt, int interID, String filePath) throws IOException{
		filePath = filePath+"/Conversation #"+ interID+".eml";
		File temp = new File(filePath);
		byte[] dbBytes = db.getFileBytesFromDB(stmt, interID);
		File file = db.getFile(dbBytes, temp.getAbsolutePath());
		
//		return filePath;
		
	}
	
//	verify text in Base 64 format
	public boolean verifyTextInBase64(String emlText, String dbText){
		boolean result = false;
		if(isEqual(emlText, dbText))
			return true;
		else{
			return false;
		}
	}
	
//	 Delete temporary directory
	public void deleteTempDirectory(String dirPath){
		try{
			FileUtils.deleteDirectory(new File(dirPath));	
		}catch(IOException e){
//			e.printStackTrace();
			System.out.println("Enable to Delete Temp Directory");
		}
		
	}
	
//	return Base64 string
	public boolean isEqual(String body, String textFromDB) {
		String str1 = new String(Base64.decodeBase64(body));
		String str2 = new String(Base64.decodeBase64(textFromDB));
		return str1.equals(str2);
	}
	
//	verify Email and return Failed Field with corresponding Interaction Id.
	
	public HashMap<Integer,ArrayList<String>> verifyEmails(HashMap<Integer,HashMap<String,ArrayList<String>>> emlData, HashMap<Integer,HashMap<String,ArrayList<String>>> dbData){
		ArrayList<String> failedFields;
		HashMap<String ,ArrayList<String>> dbText;
		HashMap<String ,ArrayList<String>> emlText;
		HashMap<Integer,ArrayList<String>> result = new HashMap();
		
		for(Integer interId : emlData.keySet()){
			System.out.println("**********Verification for Interaction Id : " + interId + " : *********************");
			emlText = emlData.get(interId);
			dbText = dbData.get(interId);
			failedFields = verifyData(emlText, dbText);
			result.put(interId, failedFields);
			System.out.println("**********Verification is Over for Interaction Id : " + interId + " : *************");
			System.out.println("\n");
			
		}
		return result;
	}
	
//	verify Email Data in Map
	public ArrayList<String> verifyData(HashMap<String,ArrayList<String>> emlData, HashMap<String ,ArrayList<String>> dbData){
		boolean flag = false;
		ArrayList<String> result = new ArrayList();
		ArrayList<String> inputText;
		ArrayList<String> dbText;
		for(String input : emlData.keySet()){
			for(String db : dbData.keySet()){
				if(input.equals(db)){
					if(emlData.get(input).size() == dbData.get(input).size()){
						System.out.println("Row Count is Same for :" + input);
					}
					inputText = emlData.get(input);
					dbText = dbData.get(input);
					for(int i = 0; i<inputText.size(); i++){
						String emlString = null;
						String dbString = null;
						for(int j = 0; j<dbText.size(); j++){
							emlString = inputText.get(i);
							dbString = dbText.get(j);
							if(isEqual(emlString, dbString)){
								System.out.println("Matched");
								flag = true;
								break;
								
							}else{
								flag = false;
								continue;
							}
						}
						if(!flag){
							System.out.println("Not Matched");
							result.add(input+" : "+ emlString + " is not match");
						}
					}
					
				}
			}
		}
		
		return result;
		
	}
	
//	getting the interaction id from file name
	public Integer getInterId(String fileName){
		return Integer.valueOf(fileName.split("#")[1].replace(".eml", ""));
	}
	
//	Renaming the File and return Interaction Id
	public  void renameFile(File file, ArrayList<Integer> interIds){
		String subject=null;
//		if the file is a directory then recursively call the function
		if(file.isDirectory()){
			for(File tempFile:file.listFiles()){
				
				renameFile(tempFile, interIds);
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
	         if(file.renameTo(new File("E:\\VijayTest\\Test"+"\\"+subject+".eml"))){
//				 System.out.println("renamed");
			 }
			 else{
				 System.out.println("not renamed");
			 }
		}
//		return interIds;
	}
	
//	get Participants ()
	
	public ArrayList<String> getParticipants(String filePath){
		InputStream source;
		String subject;
		ArrayList<String> participantName = new ArrayList();
		try{
			File file = new File(filePath);
			source = new FileInputStream(file);
	        Properties props = System.getProperties();
	        props.put("mail.host", "smtp.dummydomain.com");
	        props.put("mail.transport.protocol", "smtp");
	        Session mailSession = Session.getDefaultInstance(props, null);
	        MimeMessage message = new MimeMessage(mailSession, source);
	        Address[] address = message.getAllRecipients();
	        for(Address add : address){
	        	participantName.add(add.toString());
	        }
		}catch(Exception e){
//			System.out.println("No Participants");
		}
		return participantName;
	}
	
	public String dumpPart(Part p) throws Exception {
		// email = null;
		String mpMessage = null;
		String contentType = p.getContentType();
//		System.out.println("dumpPart : " + contentType);
		InputStream is = p.getInputStream();
		if (!(is instanceof BufferedInputStream)) {
			is = new BufferedInputStream(is);
		}
		int c;
		final StringWriter sw = new StringWriter();
		while ((c = is.read()) != -1) {
			sw.write(c);
		}

		if (!sw.toString().contains("<div>")) {
			mpMessage = sw.toString();
			// System.out.println(mpMessage);
		}
		return mpMessage;
	}
	
	
//	get Actual email from email for Reuters Network
	public void getEmlFromEml(String filePath, ArrayList<Integer> interIds) {
		ArrayList<String> deleteFilePath = new ArrayList();
		try{

			File file = new File(filePath);
			File allFiles[] = new File(filePath).listFiles();
			InputStream source;
			
//			for (File temp : file.listFiles()) {
//				if (temp.isDirectory()) {
					for (File emlFile : allFiles) {
	
						System.out.println("Current Processing File : "+emlFile.getAbsolutePath());
						ArrayList<File> attachments = new ArrayList<File>();
						
	
						source = new FileInputStream(emlFile);
	
						Properties props = System.getProperties();
						props.put("mail.host", "smtp.dummydomain.com");
						props.put("mail.transport.protocol", "smtp");
	
						Session mailSession = Session.getDefaultInstance(props,
								null);
						MimeMessage message = new MimeMessage(mailSession, source);
						String subject = message.getSubject();
	
						Multipart multipart = (Multipart) message.getContent();
						
					/*	for(int i=0; i<multipart.getCount(); i++){
							System.out.println(multipart.getContentType());
						}*/
						
						for (int x = 0; x < multipart.getCount(); x++) {
							BodyPart bodyPart = multipart.getBodyPart(x);
	
							if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())&& !StringUtils.isNotBlank(bodyPart.getFileName())) {
								continue; // dealing with attachments only
							}
							InputStream is = bodyPart.getInputStream();
//							System.out.println(filePath.substring(0,filePath.lastIndexOf("\\")));
							filePath = filePath.substring(0,emlFile.getAbsolutePath().lastIndexOf("\\"));
							interIds.add(Integer.valueOf(subject.split("#")[1]));
							File f = new File(filePath+"/" + subject + ".eml");
							FileOutputStream fos = new FileOutputStream(f);
							byte[] buf = new byte[1024*1024];
							int bytesRead;
							while ((bytesRead = is.read(buf)) != -1) {
								fos.write(buf, 0, bytesRead);
							}
							fos.close();
//							attachments.add(f);
	
						}
						source.close();
						System.out.println();
						deleteFilePath.add(emlFile.getAbsolutePath());
					}
//				}
//			}
		}catch(Exception e){
//			e.printStackTrace();
			System.out.println(e);
		}
//		deleteFile(deleteFilePath);
		
	}
	
	public void deleteFile(ArrayList<String> deleteFilePath){
		
		try{
			for(String filePath : deleteFilePath){
				File file  = new File(filePath);
				if(file.delete())
					System.out.println("File Delete Succesfully");
				else
					System.out.println("Not delete");
			}

		}catch(Exception e){
			System.out.println("Not able to delete File");
			e.printStackTrace();
		}
		
	}
	

}
