package com.actiance.test.exporterNew.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;

import com.facetime.ftcore.util.Base64;

public class TestUtils {
	ArrayList<File> files = new ArrayList();
	DBUtils dbUtils = new DBUtils();
	
//	verify all file under the filePath Folder
	public static boolean check_CSV_XSLT_Type_Validation(String filePath){
		boolean type = false;
		
		return type;
	}
	
	public static boolean check_XML_XSLT_Type_Validation(String filePath){
		boolean type = false;
		
		return type;
	}
	
	public static boolean check_HTML_XSLT_Type_Validation(String filePath){
		boolean type = false;
		return type;
	}
	
	public static boolean check_Text_XSLT_Type_Validation(String filePath){
		boolean type = false;
		return type;
	}
	

	public static String getFullEML(String filePath){
		String data = null;
		
		return data;
	}
	
	
//	Listing all the files from giving location
	
	public ArrayList listFiles(String directoryName) {
		
	    // .............list file
	    File directory = new File(directoryName);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();

	    for (File file : fList) {
	        if (file.isFile()) {
	        	files.add(file.getAbsoluteFile());
	        } else if (file.isDirectory()) {
	            listFiles(file.getAbsolutePath());
	        }
	    }
	    return files;
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public String getInterIdFromFile(String filePath){
		System.out.println("****************************");
		System.out.println("Source EML : "+ filePath);
		try{
			InputStream is = new FileInputStream(filePath);
			MimeMessage msg = new MimeMessage(null, is);
			String subject = (String)msg.getSubject();
	//		System.out.println("Subject : "+ subject);
			int index = subject.indexOf("#");
			String tempStr = subject.substring(index+1);
			System.out.println("Interaction Id : "+ tempStr);
			return tempStr.trim();
		}catch(Exception e){
			System.out.println("Error to fetch interaction id from emls");
			return null;
		}
		
		
	}
	
	public String getTime(Long unixTime){
	    Date date = new Date(unixTime); 
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT+530")); // give a timezone reference for formating (see comment at the bottom
	    String formattedDate = sdf.format(date);
	    String tempStr = formattedDate.replace(" ", "T").replace("GMT", "");
	    int indexOfColon = tempStr.lastIndexOf(":");
	    int indexOfT = tempStr.lastIndexOf("T");
	    StringBuilder sb = new StringBuilder(tempStr);
	    sb.deleteCharAt(indexOfColon);
	    sb.deleteCharAt(indexOfT);
	    return sb.toString();
	}
	
	public long getUnixTime(String timeStamp) throws ParseException{
		  String dateString = timeStamp.substring(0, timeStamp.indexOf("+")).replace("T", " ");
		  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  long unixTime = (long) date.getTime();
		  return unixTime;
	}
	
//	get Network for Reuter
	public String checkNetworkIDForReuter(Statement stmt, HashMap<String,String> map){
		String networkID = null  ;
		String networkName = null ;
//		System.out.println("Obtaining network filters");
		
		for (String name: map.keySet()){
			if(name.contains("filter.networkIDs"))
			{
				networkID = map.get(name);
			}
		}
		if(networkID !=null){
			networkName = dbUtils.getNetworkName(networkID);	
		}else{
			System.out.println("Network Id is not defined in properties file");
		}
		
		return networkName;
	}
	
//	get Base64 Decode 
	
	public String getBase64Text(String str){
		return new String(Base64.encode(str.getBytes()));
	}
	
//	verify Base64 
	
	
}
