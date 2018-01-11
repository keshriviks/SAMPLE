package com.actiance.test.exporterNew.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.openqa.selenium.remote.server.handler.mobile.GetNetworkConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.actiance.test.exporterNew.selenium.CreateXslt;

	public class CommonUtils {
		
		static ArrayList<File> files = new ArrayList();
		static DBUtils dbUtils = new DBUtils();
		
		
		public static HashMap<Integer,ArrayList<String>> failedInterIDs;
		
		/**
		 * 
		 * @param file
		 * @param templateToUse
		 * @return
		 * @throws IOException
		 */
		public static HashMap<String,String> generateMap(String file, String templateToUse) throws IOException
		{
			 HashMap<String, String> map=new HashMap<String, String>();
			Properties MyPropertyFile= new Properties();
		    FileInputStream ip = new FileInputStream(file);
		    MyPropertyFile.load(ip);
		    Set<Object> keys = MyPropertyFile.keySet();
	
		            for(Object k:keys){
		                String key=(String) k;
		                String value=MyPropertyFile.getProperty(key);
	                    map.put(templateToUse+key, value);
		            }
					return map;
		}
		
		public boolean checkXSLT(HashMap<String,String> map){
			boolean result = false;
			for(String key : map.keySet()){
				
				if(key.equalsIgnoreCase("xslt")){
					return true;
				}
			}
			return false;
		}
		
		/**
		 * 
		 * @param file
		 * @param templateToUse
		 * @param smtpUserNumber
		 * @return
		 * @throws IOException
		 */
		public  HashMap<String,String> generateMapNew(String file, String templateToUse, int smtpUserNumber) throws IOException
		{
			CreateXslt obj = new CreateXslt();
			HashMap<String, String> map=new HashMap<String, String>();
			Properties MyPropertyFile= new Properties();
		    FileInputStream ip = new FileInputStream(file);
		    MyPropertyFile.load(ip);
		    Set<Object> keys = MyPropertyFile.keySet();
		    String getSmtpUser = getSmtpUser(smtpUserNumber);
	
		            for(Object k:keys){
		                String key=(String) k;
		                String value=MyPropertyFile.getProperty(key);
	                    map.put(templateToUse+key, value);
		            }
		            map.put(templateToUse+"toHeader", getSmtpUser);
		            map.put(templateToUse+"smtpRecipients", getSmtpUser);
		            map.put(templateToUse+"smtpSender", System.getProperty("smtpSender"));
		            map.put(templateToUse+"smtpPort", System.getProperty("smtpPort"));
		            map.put(templateToUse+"fromHeader", getSmtpUser);
		            map.put(templateToUse+"smtpServer", System.getProperty("smtpServer"));
		            map.put(templateToUse+"serverIDs", System.getProperty("serverIDs"));
		            map.put(templateToUse+"timezoneToFormatInters", "IST");
		            
		            System.out.println("ServerId from Common Class : " + map.get(templateToUse+"serverIDs"));
		            
		            if(map.containsKey(templateToUse+"filter.exporterType")){
		            	if(map.get(templateToUse+"filter.exporterType").equalsIgnoreCase("collaboration")){
		            		map.put(templateToUse+"filter.resourceURLs", "all");
		            	}
		            }
		            
//		            if xslt than call this block
		            String xsltName = null;
		            if(map.containsKey(templateToUse+"xslt")){
		            	
		            	String value = map.get(templateToUse+ "xslt");
//		            	********** creating xslt throw selenium *****************  
		            	xsltName = obj.createTemplate(value);
		            	if(xsltName.contains("Collaboration")){
		            		xsltName.replace("Collaboration", "collab");
		            	}
		            	System.setProperty("XSLTName", xsltName);
		            	 map.put("xslt", xsltName);
		            	
		            	
		            			
		            }
		           
					return map;
		}
		
		
		/**
		 * 
		 * @param vantageIP
		 * @param milliSeconds
		 * @throws IOException
		 */
		
		public void restartVantage(String vantageIP, String milliSeconds) throws IOException
		{
			String command = "powershell.exe " + System.getProperty("powerShellLocation")+" "+System.getProperty("vantageIP")+" "+milliSeconds;
			System.out.println(command);
			  Process powerShellProcess = Runtime.getRuntime().exec(command);
			  powerShellProcess.getOutputStream().close();
			  String line;
			  System.out.println("Output:");
			  BufferedReader stdout = new BufferedReader(new InputStreamReader(
			    powerShellProcess.getInputStream()));
			  while ((line = stdout.readLine()) != null) {
			   System.out.println(line);
			  }
			  stdout.close();
//			  System.out.println("Error:");
			  BufferedReader stderr = new BufferedReader(new InputStreamReader(
			    powerShellProcess.getErrorStream()));
			  while ((line = stderr.readLine()) != null) {
			   System.out.println(line);
			  }
			  stderr.close();
			  System.out.println("Done");
			 
		}
		/**
		 * 
		 * @param map
		 * @return
		 */
		
		public ArrayList<Integer> returnGroupIDs(HashMap<String,String> map)
		{
			ArrayList<Integer> groupIDs = new ArrayList<Integer>();
			System.out.println("Obtaining group filters");
			
			for (String name: map.keySet()){
				if(name.contains("filter.groupIDs"))
				{
					String gp = map.get(name);
					String[] gps = gp.split(" ");
					for(int i = 0; i<gps.length;i++)
					{
						groupIDs.add(Integer.parseInt(gps[i]));
					}
				}else{
					continue;
				}
								
			}
			if(groupIDs.size()==0)
				groupIDs.add(-5);
			return groupIDs;
		}
		
		/**
		 * 
		 * @param map
		 * @return
		 */
		public ArrayList<Integer> returnNetworkIDs(HashMap<String,String> map)
		{
			ArrayList<Integer> networkIDs = new ArrayList<Integer>();
			System.out.println("Obtaining network filters");
			
			for (String name: map.keySet()){
				if(name.contains("filter.networkIDs"))
				{
					String gp = map.get(name);
					String[] gps = gp.split(" ");
					for(int i = 0; i<gps.length;i++)
					{
						networkIDs.add(Integer.parseInt(gps[i]));
					}
				}
								
			}
			if(networkIDs.size()==0)
				networkIDs.add(-1);
			return networkIDs;
		}
		
//		get Network for Reuter
		public boolean returnNetworkIDForReuter(Statement stmt, HashMap<String,String> map)
		{
			String networkID = null  ;
			String networkName ;
//			System.out.println("Obtaining network filters");
			
			for (String name: map.keySet()){
				if(name.contains("filter.networkIDs"))
				{
					networkID = map.get(name);
				}
								
			}
			networkName = dbUtils.getNetworkName(networkID);
			if(networkName.equals("Reuters")){
				return true;
			}else{
				return false;
			}
		}
		
	/**
	 * 	
	 * @param map
	 * @return
	 */
		public String returnToDate(HashMap<String,String> map){
			String toDate = null;
			for (String name: map.keySet()){
				if(name.contains("filter.processToDate")){
					toDate = map.get(name);
				}
			}
			return toDate;
		}
		/**
		 * 	
		 * @param map
		 * @return
		 */
	public  String returnFromDate(HashMap<String,String> map){
		String fromDate = null;
		for (String name: map.keySet()){
			if(name.contains("filter.processFromDate")){
				fromDate = map.get(name);
			}
		}
				return fromDate;
	}
	
	public int returnConversationType(HashMap<String,String> map){
		String conversationType = null;
		int conType = -1;
		for (String name: map.keySet()){
			if(name.contains("filter.partsType")){
				conversationType = map.get(name);
			}
		}
		if(conversationType != null){
			if(conversationType.contains("internal")){
				conType = 1;
			}else{
				conType =  0;
			}
		}
		return conType;
	}	
	
//	filter.chatTranscripts 
	
	public String returnSkipEmpty(HashMap<String,String> map){
		String skipEmpty = null;
		for (String name: map.keySet()){
			if(name.contains("filter.chatTranscripts")){
				skipEmpty = map.get(name);
			}
		}
				return skipEmpty;
	}
	
	
		/**
		 * 
		 * @param map
		 * @return
		 */
		public static String returnSmtpUserName(HashMap<String,String> map)
		{
			String smtpuserName = null;
			ArrayList<Integer> networkIDs = new ArrayList<Integer>();
			System.out.println("Obtaining network filters");
			
			for (String name: map.keySet()){
				if(name.contains("smtpSender"))
				{
					smtpuserName = map.get(name);
					}
			}
								
			return  smtpuserName;
		
		}
		
		
		/**
		 * 
		 * @param filePath
		 * @return
		 */
		public static String getInterIdFromFile(String filePath){
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
		
		public static void changeFileNamesAndCopy(String srcFolderPath, String destFolderPath) throws MessagingException, IOException{
			ArrayList<File> files = listFiles(srcFolderPath);
	
			for(int i = 0; i<files.size(); i++){
				String newName = "Conversation #"+getInterIdFromFile(files.get(i).getAbsolutePath())+".eml";
				System.out.println(files.get(i).getName());
				String newPath = destFolderPath+"/"+newName;
				System.out.println("Destination : "+ newPath);
				copy(files.get(i), new File(newPath));
			}
		}
		public static ArrayList listFiles(String directoryName) {
	
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
		public static void copy(File src, File dst) throws IOException {
		    InputStream in = new FileInputStream(src);
		    try {
		        OutputStream out = new FileOutputStream(dst);
		        try {
		            // Transfer bytes from in to out
		            byte[] buf = new byte[1024];
		            int len;
		            while ((len = in.read(buf)) > 0) {
		                out.write(buf, 0, len);
		            }
		        } finally {
		            out.close();
		        }
		    } finally {
		        in.close();
		    }
		}
		
//		get Data from Eml's for Text Formatted Data
	public  static HashMap<Integer,String> getMessage(String filePath) throws MessagingException, NumberFormatException, IOException{
	    HashMap<Integer,String> emlMap = new HashMap();
	    ArrayList<File> listOfFiles = listFiles(filePath);
	    Iterator itr = listOfFiles.iterator();
	          while(itr.hasNext())
	           {
	        	   File file = (File) itr.next();
	        	   InputStream is = new FileInputStream(file.getAbsoluteFile());
	               MimeMessage msg = new MimeMessage(null, is);
	               Integer interId = Integer.valueOf(getInterIdFromFile(file.getAbsolutePath()));
	               String messages = (String)msg.getContent();
	               emlMap.put(interId, messages);
	           }
	           
	           return emlMap;
	    }
	
		
//		Get Data from Eml's for XML Formatted Data of IM
		public static HashMap<Integer, HashMap<String, ArrayList<String>>> getXMLContent(String directory) throws MessagingException, IOException, ParserConfigurationException, SAXException{
	        
	        HashMap<Integer, HashMap<String, ArrayList<String>>> result = new HashMap();
	        ArrayList<String> messageValues;
	        HashMap<String, ArrayList<String>> messageMap;
	        
	        Properties props = System.getProperties();
	        props.put("mail.host", "smtp.dummydomain.com");
	        props.put("mail.transport.protocol", "smtp");
	
	        Session session = Session.getDefaultInstance(props);
	        InputStream source;
	        Message message;
	        String xml;
	        int interID;
	
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        InputStream stream;
	        Document doc;
	        
	        File emlDirectory = new File(directory);
	        //String[] filesInCallFolder = emlDirectory.list();
	        File[] files = emlDirectory.listFiles();
	        
	        for(File file : files){
	               System.out.println("---------------------------------------------------------------------");
	               
	               int row = 0;
	               messageMap = new HashMap();
	               
	               source = new FileInputStream(file);
	               message = new MimeMessage(session, source);
	               xml = message.getContent().toString();
	               stream = new ByteArrayInputStream(xml.getBytes());
	               doc = builder.parse(stream);
	               //System.out.println("Root: "+doc.getDocumentElement().getNodeName());
	               
	               org.w3c.dom.Element root = doc.getDocumentElement();
	               
	               interID = Integer.parseInt(root.getElementsByTagName("interID").item(0).getTextContent());
	               System.out.println(interID);
	               
	               NodeList messages = root.getElementsByTagName("msgSent");
	               //System.out.println(transcript.getLength());
	               for(int k=0; k<messages.getLength(); k++){
	                     
	                     messageValues = new ArrayList();
	                     
	                     Node msg = messages.item(k);
	                     NodeList list = msg.getChildNodes();
	                     for(int i=0; i<list.getLength(); i++){
	                            Node node = list.item(i);
	                            if(node.getNodeType() == Node.ELEMENT_NODE){
	                                   messageValues.add(node.getTextContent());
	                                   System.out.println("Node Name: "+node.getNodeName()+", Node Value: "+node.getTextContent());
	                            }
	                     }
	                     System.out.println();
	                     
	                     messageMap.put("Row: "+row++, messageValues);
	               }
	               System.out.println("------------------------------------------------------------------------");
	               
	               result.put(interID, sortMap(messageMap));
	        }
	        
	        return result;
	 }
	
		public static HashMap<String, ArrayList<String>> sortMap(HashMap<String, ArrayList<String>> map){
	        
	        //int t = 1460540061074;
	        
	        for (int i = 0; i < map.size(); i++) {
	
	               for (int j = i + 1; j < map.size(); j++) {
	
	                     ArrayList<String> fist = map.get("Row: " + i);
	                     ArrayList<String> second = map.get("Row: " + j);
	                     
	                     if (Long.parseLong(fist.get(0)) > Long.parseLong(second.get(0))){
	                            map.put("Row " + i, second);
	                            map.put("Row " + j, fist);
	                     }
	               }
	        }
	        
	        return map;
	 }
	
		
		
	
	
	
	public static boolean verifyXMLData(HashMap<Integer, HashMap<String, ArrayList<String>>> resultDB, HashMap<Integer, HashMap<String, ArrayList<String>>> resultXML,String versionProperty, HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete){
	    boolean result = true;
	    boolean flag;
	    boolean extra = true;
	    boolean flag1 = false;
	    Headers header = new Headers();
	    ArrayList<String> columns = null;
	    failedInterIDs = new HashMap<Integer,ArrayList<String>>();
	    
	    String version = versionProperty.substring(versionProperty.indexOf("_")+1);
	    
	    //Get interIDs
	    Set<Integer> interIDsDB = resultDB.keySet();
	    Set<Integer> interIDsXML = resultXML.keySet();
	   
	    for(int intDB : resultDB.keySet()){
	           
	           flag = false;
	           extra = true;
	           
	           for(int intXML : resultXML.keySet()){
	                 
	                 if(intXML == intDB){
	                        
	                        extra = false;
	                        columns = new ArrayList();
	                        
	                        //Get the HashMap
	                        HashMap<String, ArrayList<String>> dbMap = resultDB.get(intDB);
	                        HashMap<String, ArrayList<String>> XMLMap = resultXML.get(intXML);
	                        
	                        //check if number of row data match
	                        System.out.println(dbMap.size() + "#########" + XMLMap.size());
	                        if(dbMap.isEmpty()&&XMLMap.isEmpty())
	                        {
	                        	result = result & true;
	                        	extra = false;
	                        	flag =true;
	                        	break;
	                        }
	                        else if(dbMap.size() != XMLMap.size()){
	                               System.out.println("*****Number of Message Data row didn't match with the Messag Data rows in the .eml file for InterID "+intDB);
	                               result = false;
	                               columns = new ArrayList();
	                               columns.add("*****Number of Message Data rows didn't match with the Messag Data rows in the .eml file");
	                               failedInterIDs.put(intDB, columns);
	                               break;
	                               
	                        }else{
	                               System.out.println("Number of Message Data rows matches for the InterID "+intDB);
	                        }
	                        
	                       
	                        
	                        //Verify Message data row by row
	                        for(int row=0; row<dbMap.size(); row++){
	                        	
	                        	
	                        	
	                               if(dbMap.get("Row: "+row).get(0).equals(XMLMap.get("Row: "+row).get(0))){
	                            	   if(row == 0){
	                            		   System.out.println("SentTime matches with the XML Data");
	                                       flag = true;
	                            	   }else{
	                            		   System.out.println("SentTime matches with the XML Data");
	                                       flag = flag & true;
	                            	   }
	                                      
	                               }else{
	                                      System.out.println("*****SentTime did not match with the XML Data");
	                                      columns.add("Sent Time");
	                                      flag = false;
	                               }
	                               
	                               if(dbMap.get("Row: "+row).get(1).equals(XMLMap.get("Row: "+row).get(1))){
	                                   System.out.println("BuddyName matches with the XML Data");
	                                   flag = flag & true;
	                                  }else{
		                                   System.out.println("BuddyName did not match with the XML Data");
		                                   columns.add("BuddyName");
		                                   flag = false;
	                                  } 
	                               
	                               if(version.equalsIgnoreCase("v1")){
	                                   if(dbMap.get("Row: "+row).get(2) != null){
	                                          if(dbMap.get("Row: "+row).get(2).trim().equals(XMLMap.get("Row: "+row).get(3).trim())){
	                                                 System.out.println("Text value matches with the XML Data");
	                                                 flag = flag & true;
	                                          }else{
	                                                 System.out.println("Text value did not match with the XML Data");
	                                                 columns.add("Text");
	                                                 flag = false;
	                                          }
	                                   }
	                                   
	                            }else if(version.equalsIgnoreCase("v2")){
	                                   if(dbMap.get("Row: "+row).get(2) != null){
	                                          if(dbMap.get("Row: "+row).get(2).trim().equals(XMLMap.get("Row: "+row).get(4).trim())){
	                                                 System.out.println("Text value matches with the XML Data");
	                                                 flag = flag & true;
	                                          }else{
	                                                 System.out.println("Text value did not match with the XML Data");
	                                                 columns.add("Text");
	                                                 flag = false;
	                                          }
	                                   }
	                                   
	                            }else if(version.equalsIgnoreCase("v3")){
	                                   if(dbMap.get("Row: "+row).get(2) != null){
	                                          if(dbMap.get("Row: "+row).get(2).trim().equals(XMLMap.get("Row: "+row).get(9).trim())){
	                                                 System.out.println("Text value matches with the XML Data");
	                                                 flag = flag & true;
	                                          }else{
	                                                 System.out.println("Text value did not match with the XML Data");
	                                                 columns.add("Text");
	                                                 flag = false;
	                                          }
	                                   }
	                                   
	                            }else{
	                                   System.out.println("Invalid XML Version");
	                                   columns.add("Text");
	                                   flag = false;
	                                   
	                            }
	                            
	                            System.out.println();
	                     }
	                        flag1 = dbUtils.verifyHeaders(headersFromTestFile, intXML, headersFromEmls.get(intXML), exporterTemplete, header);
	                        
	                        break;
	                 }
	           }
	           
	           if(extra){
	                 columns = new ArrayList();
	                 columns.add("Extra InterID from Database");
	                 failedInterIDs.put(intDB, columns);
	           }
	           System.out.println("#######################################");
	           System.out.println(result);
	           System.out.println(flag);
	           System.out.println(!extra);
	           System.out.println("#######################################");
	           
	           result = result & flag & (!extra);
	           System.out.println("Result value ="+result);
	           if(!result & !flag & !extra){
	        	    failedInterIDs.put(intDB, columns);
	        	   }
	    }
	    
	    for(int interID : interIDsXML){
	           if(!interIDsDB.contains(interID)){
	                 columns = new ArrayList();
	                 columns.add("Extra InterID from .eml files");
	                 failedInterIDs.put(interID, columns);
	                 result = false;
	           }
	    }
	    
	    System.out.println("Inside Commonutils, printing failed inter size " +failedInterIDs.size());
	    
	    return result & flag1;
	}
	
	
	
	public static boolean verifyTextFormatData(HashMap<Integer, HashMap<String, ArrayList<String>>> dataFromDB, HashMap<Integer, String> inputData,HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete){
	    boolean flag = true;
	    Set<Integer> interIdsFromDB = dataFromDB.keySet();
	    failedInterIDs = new HashMap();
	    Headers header = new Headers();
	    boolean flag1 = false;
	    ArrayList<String> failed;
	    for(int dbInterId : dataFromDB.keySet()){
	    	
	           for(int inputInterId : inputData.keySet()){
	                 if(dbInterId == inputInterId){
	                	 
	                	 failed = new ArrayList();
	                	 System.out.println("For interaction id :"+dbInterId);
	                        HashMap<String, ArrayList<String>> dbMap = dataFromDB.get(dbInterId);
	                        String inputText = inputData.get(dbInterId);
	                        
	                         for(int row = 0; row<dbMap.size(); row++){
	                        	 System.out.println("DB Time Stamp : "+dbMap.get("Row: "+ row).get(0));
	                        	 System.out.println("sent time :" + getTime(Long.valueOf(dbMap.get("Row: "+ row).get(0))));
	                               if(inputText.contains(getTime(Long.valueOf(dbMap.get("Row: "+ row).get(0))))){
	                                      System.out.println("Sent Time is matched");
	                                      flag = flag && true;
	                               }else{
	                                      System.out.println("Sent Time did not match");
	                                      failed.add("Sent Time");
	                                      flag = false;
	                               }
	                               if(inputText.contains(dbMap.get("Row: "+row).get(1))){
	                                      System.out.println("Buddyname is matched");
	                                      flag = flag && true;
	                               }else{
	                                      System.out.println("Buddyname did not match");
	                                      failed.add("Buddy Name");
	                                      flag = false;
	                               }
	                               if(dbMap.get("Row: "+row).get(2)!= null){
	                            	   System.out.println("Text From DB ----->");
	                            	   System.out.println(dbMap.get("Row: "+row).get(2));
	                                      if(inputText.contains(dbMap.get("Row: "+row).get(2))){
	                                             System.out.println("Text Message is matched");
	                                             flag = flag && true;
	                                      }else{
	                                             System.out.println("Text Message did not match ");
	                                             failed.add("Text Content");
	                                             flag = false;
	                                      }
	                               }else{
	                                      System.out.println("No Text");
	                                      flag = flag && true;
	                               }
	                        }
	                    
//	                     flag1 = dbUtils.verifyHeaders(headersFromTestFile, dbInterId, headersFromEmls.get(dbInterId), exporterTemplete, header);
	                         if(failed.size()>0)
	                         {
	                        	 failedInterIDs.put(dbInterId, failed);
	                        	 //failed.clear();
	                         }
	                 }
	           }
	    }            
	     return flag & flag1;
	}
	
//	Get HTML formatted Data From Eml for IM
	
	public static HashMap<Integer, HashMap<String, ArrayList<String>>> getHTMLContent(String directory) throws MessagingException, IOException{
        
        HashMap<Integer, HashMap<String, ArrayList<String>>> result = new HashMap();
        ArrayList<String> messageValues;
        HashMap<String, ArrayList<String>> messageMap;
        
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");

        Session session = Session.getDefaultInstance(props);
        InputStream source;
        String html;
        int interID;
        Message message;
        
        File emlDirectory = new File(directory);
        //String[] filesInCallFolder = emlDirectory.list();
        File[] files = emlDirectory.listFiles();
        
        for(File file : files){
               System.out.println("---------------------------------------------------------------------");
               
               int row = 0;
               boolean firstRow = true;
               messageMap = new HashMap();
               
               source = new FileInputStream(file);
               message = new MimeMessage(session, source);
               
               interID = Integer.parseInt(message.getSubject().split("#")[1]);
               
               html = message.getContent().toString();
               org.jsoup.nodes.Document doc = Jsoup.parse(html);
               Elements table = doc.getElementsByTag("table");
               //System.out.println(table.first());
               Elements rows = table.get(0).getElementsByTag("tr");
               System.out.println(rows.size());
               for(org.jsoup.nodes.Element ro : rows){
                     
                     //Skip first row 
                      if(firstRow){
                            firstRow = false;
                            
                     }else{
                            messageValues = new ArrayList();
                            
                            Elements columns = ro.getElementsByTag("td");
                            
                            //Check message column text
                            String messageText = columns.get(3).text();
                            if(messageText.equalsIgnoreCase("Conversation started.") || messageText.equalsIgnoreCase("Joined conversation.")
                                          || messageText.equalsIgnoreCase("Left conversation.") || messageText.equalsIgnoreCase("Conversation ended.")){
                                   //do nothing
                            }else{
                                   messageValues.add(columns.get(0).text());
                                   messageValues.add(columns.get(2).text());
                                   messageValues.add(columns.get(3).text());
                                   
                                   messageMap.put("Row: "+row++, messageValues);
                            }
                     }
               }
               
               System.out.println("---------------------------------------------------------------------");
               result.put(interID, messageMap);
        }
        
        return result;
 }

	
	
//	Verify HTML Formatted Data for IM
	
	public static boolean verifyHTMLContent(HashMap<Integer, HashMap<String, ArrayList<String>>> resultDB, HashMap<Integer, HashMap<String, ArrayList<String>>> resultHTML,HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete) throws ParseException{
        
        boolean result = true;
        boolean flag;
        boolean extra = true;
        ArrayList<String> columns = null;
        Headers header = new Headers();
        boolean flag1 = false;
        
        failedInterIDs = new HashMap();
        
        //Get interIDs
        Set<Integer> interIDsDB = resultDB.keySet();
        Set<Integer> interIDsHTML = resultHTML.keySet();
        
        for(int intDB : resultDB.keySet()){
               
               flag = false;
               extra = true;
               
               for(int intHTML : resultHTML.keySet()){
                     
                     if(intHTML == intDB){
                            
                            extra = false;
                            columns = new ArrayList();
                            
                            //Get the HashMap
                            HashMap<String, ArrayList<String>> dbMap = resultDB.get(intDB);
                            HashMap<String, ArrayList<String>> HTMLMap = resultHTML.get(intHTML);
                            
                            //check if number of row data match
                            if(dbMap.isEmpty() && HTMLMap.isEmpty()){
                                   flag = true;
                                   extra = false;
                                   result = result & true;
                                   break;
                                   
                            }else if(dbMap.size() != HTMLMap.size()){
                                   System.out.println("Number of Message Data row didn't match with the Messag Data rows in the .eml file for InterID "+intDB);
                                   result = false;
                                   columns = new ArrayList();
                                   columns.add("Number of Message Data rows didn't match with the Messag Data rows in the .eml file");
                                   failedInterIDs.put(intDB, columns);
                                   break;
                                   
                            }else{
                                   System.out.println("Number of Message Data rows matches for the InterID "+intDB);
                            }
                            
                            //verify Message Data
                            for(int row=0; row<dbMap.size(); row++){
                                   if(dbMap.get("Row: "+row).get(0).equals(getUnixTime(HTMLMap.get("Row: "+row).get(0)))){
                                          if(row == 0){
                                                 System.out.println("SentTime matches with the XML Data");
                                                 flag = true;
                                          }else{
                                                 System.out.println("SentTime matches with the XML Data");
                                                 flag = flag & true;
                                          }
                                   }else{
                                          System.out.println("SentTime did not match with the HTML Data");
                                          columns.add("Sent Time");
                                          flag = false;
                                   }
                                   
                                   if(HTMLMap.get("Row: "+row).get(1).contains(dbMap.get("Row: "+row).get(1))){
                                          System.out.println("BuddyName matches with the HTML Data");
                                          flag = flag & true;
                                   }else{
                                          System.out.println("BuddyName did not match with the HTML Data");
                                          columns.add("BuddyName");
                                          flag = false;
                                   }
                                   
                                   if(dbMap.get("Row: "+row).get(2).trim().equals(HTMLMap.get("Row: "+row).get(3).trim())){
                                          System.out.println("Text value matches with the HTML Data");
                                          flag = flag & true;
                                   }else{
                                          System.out.println("Text value did not match with the HTML Data");
                                          columns.add("Text");
                                          flag = false;
                                   }
                                   System.out.println();
                            }
                           flag1 = dbUtils.verifyHeaders(headersFromTestFile, intHTML, headersFromEmls.get(intHTML), exporterTemplete, header);
                            break;
                     }
               }
               
               if(extra){
                     columns = new ArrayList();
                     columns.add("Extra InterID from Database");
                     failedInterIDs.put(intDB, columns);
               }
               
               result = result & flag & (!extra);
               
               if(!result & !flag & !extra){
                     failedInterIDs.put(intDB, columns);
               }
               
               System.out.println();
        }
        
        for(int interID : interIDsHTML){
               if(!interIDsDB.contains(interID)){
                     columns = new ArrayList();
                     columns.add("Extra InterID from .eml files");
                     failedInterIDs.put(interID, columns);
                     result = false;
               }
        }
        
        return result;
 }

//	Get HTML Formatted Content Data for Collab from Eml Files
	public static HashMap<Integer, HashMap<String, String>> getHTMLContentForCollab(String directory) throws NumberFormatException, MessagingException, IOException{
        HashMap<Integer, HashMap<String, String>> result = new HashMap();
        HashMap<String, String> messageValues;
        
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");

        Session session = Session.getDefaultInstance(props);
        InputStream source;
        String html = null;
        int interID;
        Message message;
        
        File emlDirectory = new File(directory);
        File[] files = emlDirectory.listFiles();
        
        for(File file : files){
               System.out.println("---------------------------------------------------------------------");
               
               messageValues = new HashMap();
 
               boolean firstRow = true;
               
               source = new FileInputStream(file);
               message = new MimeMessage(session, source);
               
               Multipart multipart = (Multipart) message.getContent();
        //Get HTML Part
        for(int i=0; i<multipart.getCount(); i++){
               if(multipart.getBodyPart(i).getContent().toString().contains("<!DOCTYPE HTML SYSTEM>")){
                     html = multipart.getBodyPart(i).getContent().toString();
               }
        }
               
               interID = Integer.parseInt(message.getSubject().split("#")[1]);
               
               org.jsoup.nodes.Document doc = Jsoup.parse(html);
               Elements table = doc.getElementsByTag("table");
               Elements rows = table.get(0).getElementsByTag("tr");
               //Get Text Value
               for(org.jsoup.nodes.Element ro : rows){
                     //Skip first row 
                     if(firstRow){
                            firstRow = false;
                            
                     }else{
                            
                            Elements columns = ro.getElementsByTag("td");
                            
                            for(org.jsoup.nodes.Element col : columns){
                                   if(col.attr("class").equalsIgnoreCase("c1")){
                                          //System.out.println(col.text());
                                          messageValues.put("Text", col.text().trim());
                                   }
                            }
                     }
               }
               
               //Get Body Elements
               Elements body = doc.getElementsByTag("body");
               Elements paras = body.get(0).getElementsByTag("p");
               
               for(org.jsoup.nodes.Element p : paras){
                     if(p.text().trim() != null && !p.text().trim().equals("")){
                            
                            String[] attributes = p.text().split(":");
                            if(attributes.length == 2){
                                   messageValues.put(attributes[0].trim(), attributes[1].trim());
                            }
                     }
               }
               
               result.put(interID, messageValues);
        }
        
        System.out.println(result);
        
        return result;
 }

//	 verify Text Format Collabortion
	 
	 public static boolean verifyTextFormatForCollab(HashMap<Integer,HashMap<String,String>> dataFromDB, HashMap<Integer, String> inputData, String format,HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete) throws NumberFormatException, XPathExpressionException, SQLException, ParserConfigurationException, SAXException, IOException{
		 boolean flag = false;
		 DBUtils dbUtils = new DBUtils();
		 boolean headerFlag = false;
		 Headers header = new Headers();
//		 HashMap<Integer,HashMap<String,String>> dataFromDB = db.getMessageDataForCollabForText(interIds);
		 Set<Integer> interIdsFromDB = dataFromDB.keySet();
		 failedInterIDs = new HashMap();
		 HashMap<String, String> messagesMap ;
		 ArrayList<String> failed = new ArrayList();
		 if(!format.contains("V4")){
			 for(int dbInterId : dataFromDB.keySet()){
				 failed = new ArrayList();
				 for(int inputInterId : inputData.keySet()){
					 if(dbInterId == inputInterId){
						messagesMap = dataFromDB.get(dbInterId);
						 String inputMessage = inputData.get(dbInterId);
						 
							 Set<String> attrs = messagesMap.keySet();
							 for(String attr : attrs){
								 if(!attr.equals("Text Content: \n")){
									 if(inputMessage.contains(attr+ " "+ messagesMap.get(attr))){
										 System.out.println(attr + " is matched ");
										 System.out.println("Value of "+attr + " is \n" + messagesMap.get(attr));
									 }else{
										 failed.add(attr);
									 }
								 }else{
									 if(inputMessage.contains(attr + messagesMap.get(attr))){
										 System.out.println(attr + " is matched ");
										 System.out.println("Value of "+attr + " is \n" + messagesMap.get(attr));
									 }else{
										 failed.add(attr);
									 }
								 }
							 }
							 headerFlag = dbUtils.verifyHeaders(headersFromTestFile, dbInterId, headersFromEmls.get(dbInterId), exporterTemplete, header);
						 } 
					 }
				 failedInterIDs.put(dbInterId, failed);
				 }
		 }else{
			 for(int dbInterId : dataFromDB.keySet()){
				 failed = new ArrayList();
				 for(int inputInterId : inputData.keySet()){
					 if(dbInterId == inputInterId){
						messagesMap = dataFromDB.get(dbInterId);
						 String inputMessage = inputData.get(dbInterId);
						 for(String attr : messagesMap.keySet()){
							if (!attr.equalsIgnoreCase("contentType:")
									&& !attr.equalsIgnoreCase("contentSubType:")
									&& !attr.equalsIgnoreCase("action:")
									&& !attr.equalsIgnoreCase("resourceName:")
									&& !attr.equalsIgnoreCase("Resource URL:")) {
								if(!attr.equalsIgnoreCase("Text Content:\n")){
									if(inputMessage.contains(attr+" "+ messagesMap.get(attr))){
										System.out.println(attr.substring(0,attr.length()-1) + " is matched");
									}else{
										System.out.println(attr.substring(0,attr.length()-1) + " did not matched");
										failed.add(attr.substring(0,attr.length()-1));
									}
								}else{
									if(inputMessage.contains(attr + messagesMap.get(attr))){
										System.out.println(attr.substring(0,attr.length()-1) + " is matched");
									}else{
										System.out.println(attr.substring(0,attr.length()-1) + " did not matched");
										failed.add(attr.substring(0,attr.length()-1));
									}
								}
							}
						 }
						} 
					 }
				 headerFlag = dbUtils.verifyHeaders(headersFromTestFile, dbInterId, headersFromEmls.get(dbInterId), exporterTemplete, header);
				 
				 failedInterIDs.put(dbInterId, failed);
				 }
		 }
		 if(failedInterIDs.size() == 0)
			 return true;
		 else{
			 return false;
		 }
	 }
	 
	
	
//	Verify HTML Formatted Data for Collab
	
    public static boolean verifyHTMLContentForCollab(HashMap<Integer, HashMap<String, String>> resultDB, HashMap<Integer, HashMap<String, String>> resultHTML,HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete){
        boolean result = true;
        boolean flag;
        boolean flag1 = false;
        Headers header = new Headers();
               boolean extra = true;
               boolean firstAttribute;
               ArrayList<String> columns = null;
               
               failedInterIDs = new HashMap();
               
               //Get interIDs
               Set<Integer> interIDsDB = resultDB.keySet();
               Set<Integer> interIDsHTML = resultHTML.keySet();
               
               for(int intDB : resultDB.keySet()){
                      
                      flag = false;
                      extra = true;
                      firstAttribute = true;
                      
                      for(int intHTML : resultHTML.keySet()){
                            
                            if(intHTML == intDB){
                                   
                                   extra = false;
                                   columns = new ArrayList();
                                   
                                   //Get the HashMap
                                   HashMap<String, String> dbMap = resultDB.get(intDB);
                                   HashMap<String, String> HTMLMap = resultHTML.get(intHTML);
                                   
                                   //check if number of row data match
                                   if(dbMap.isEmpty() && HTMLMap.isEmpty()){
                                          flag = true;
                                          extra = false;
                                          result = result & true;
                                          break;
                                          
                                   }else if(dbMap.size() != HTMLMap.size()){
                                          System.out.println("Number of Message Data row didn't match with the Messag Data rows in the .eml file for InterID "+intDB);
                                          result = false;
                                          columns = new ArrayList();
                                          columns.add("Number of Message Data rows didn't match with the Messag Data rows in the .eml file");
                                          failedInterIDs.put(intDB, columns);
                                          break;
                                          
                                   }else{
                                          System.out.println("Number of Message Data rows matches for the InterID "+intDB);
                                   }
                                   
                                   //Verify Message Data
                                   for(String dbAttName : dbMap.keySet()){
                                          
                                          if(HTMLMap.get(dbAttName) != null){
                                                 
                                                 if(dbMap.get(dbAttName).equals(HTMLMap.get(dbAttName))){
                                                        System.out.println("Attribute: "+dbAttName+", value matches with the HTML Content");
                                                        if(firstAttribute){
                                                               flag = true;
                                                               firstAttribute = false;
                                                               
                                                        }else{
                                                               flag = flag & true;
                                                        }
                                                        
                                                 }else{
                                                        System.out.println("Attribute: "+dbAttName+", value did not match with the HTML Content");
                                                        columns.add(dbAttName);
                                                        flag = false;
                                                 }
                                                        
                                          }else{
                                                 columns.add("Attribute: "+dbAttName+", did not exist in HTML Content");
                                                 flag = false;
                                          }
                                   }
                                   //verify Headers
                                   flag1 = dbUtils.verifyHeaders(headersFromTestFile, intHTML, headersFromEmls.get(intHTML), exporterTemplete, header);
                                   
                                   break;
                            }
                      }
                      
                      if(extra){
                            columns = new ArrayList();
                            columns.add("Extra InterID from Database");
                            failedInterIDs.put(intDB, columns);
                      }
                      
                      result = result & flag & (!extra) & flag1;
                      
                      if(!result & !flag & !extra){
                            failedInterIDs.put(intDB, columns);
                      }
                      
                      System.out.println();     
               }
               
               for(int interID : interIDsHTML){
                      if(!interIDsDB.contains(interID)){
                            columns = new ArrayList();
                            columns.add("Extra InterID from .eml files");
                            failedInterIDs.put(interID, columns);
                            result = false;
                      }
               }             
               
        return result;
     }
    
//	Generating XML Data For Collab From EML Files
	public static HashMap<Integer,HashMap<String,String>> getXMLDataForCollabFromEML(String emlFolder) throws IOException, MessagingException, SAXException, ParserConfigurationException{
		HashMap<Integer,HashMap<String,String>> result = new HashMap();
		 
		  HashMap<String, String> messageMap;
		  
		  Properties props = System.getProperties();
		  props.put("mail.host", "smtp.dummydomain.com");
		  props.put("mail.transport.protocol", "smtp");

		  Session session = Session.getDefaultInstance(props);
		  InputStream source;
		  Message message;
		  String xml;
		  int interID;
		  

		  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		  DocumentBuilder builder = factory.newDocumentBuilder();
		  InputStream stream;
		  Document doc;
		  
		  File emlDirectory = new File(emlFolder);
		  File[] files = emlDirectory.listFiles();
		  
		  for(File file : files){
		   
		   messageMap = new HashMap();
		   
		   source = new FileInputStream(file);
		   message = new MimeMessage(session, source);
		   String subject = message.getSubject();
		   int index = subject.indexOf("#");
		   String interId = subject.substring(index+1);
		   
		   xml = message.getContent().toString();
		   stream = new ByteArrayInputStream(xml.getBytes());
		   doc = builder.parse(stream);
		
		   ArrayList<String> getData = new ArrayList();
		   

		   getData.add("networkName");
		   getData.add("contentType");
		   getData.add("intEmployeeID");
		   getData.add("employeeID");
		   getData.add("firstName");
		   getData.add("lastName");
		   getData.add("employeeEmail");
		   getData.add("buddyName");
		   getData.add("networkID");
		   getData.add("serverName");
		   getData.add("roomName");
		   getData.add("resourceName");
		   getData.add("containerID");
		   getData.add("eventID");
		   getData.add("parentEventID");
		   getData.add("action");
		   getData.add("roomName");
		   getData.add("contentSubType");
		   getData.add("text");

		   NodeList nodeList = doc.getDocumentElement().getChildNodes();
		   
		   org.w3c.dom.Element root = doc.getDocumentElement();
		   
		   for(String type : getData){
			   String typeValue = checkExistance(root, type);
			   if(typeValue != null){
				   String getValue = typeValue;
				   messageMap.put(type, getValue);
			   }
		   }
		   NodeList attribute = doc.getElementsByTagName("attribute");
		   for(int i = 0; i< attribute.getLength(); i++){
			   
			   Node node = attribute.item(i);
			   NodeList list = node.getChildNodes();
			   ArrayList<String> value = new ArrayList();
			   for(int h = 0 ; h< list.getLength() ; h++){
				   if(list.item(h).getNodeType() == Node.ELEMENT_NODE){
					   value.add(list.item(h).getTextContent());   
				   }
				   
			   }
			   messageMap.put(value.get(0), value.get(1));
			}
		   result.put(Integer.valueOf(interId), messageMap);
		}

		   
		   
		  
		return result;
	}
	
//	Check Tag Existence
	public static String checkExistance(Element root, String tagName){
		String value = null;
		boolean flag  = false;
		try{
			if(root.getElementsByTagName(tagName).item(0).getTextContent() != null){
				flag = true;
				if(flag){
					value = (String)root.getElementsByTagName(tagName).item(0).getTextContent();
				}
			}
		}catch(Exception e){
			return null;
		}
		return value;
	}
	
	/**
	 * 
	 * @param resultXML
	 * @param resultDB
	 * @param formatVersion
	 * @param header
	 * @return
	 */
//	Verify Collab For XML
		public static boolean verifyXMLForCollab(HashMap<Integer,HashMap<String,String>> resultXML, HashMap<Integer,HashMap<String,String>> resultDB, String formatVersion, HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete){
	         Headers header = new Headers();
//	         DBUtils dbUtils = new DBUtils();
	         boolean headerFlag = false;
	         ArrayList<Integer> extraInterIDs = new ArrayList();
	         boolean result = true;
	         boolean flag;
	         ArrayList<String> failed ;
	         
	         //Check whether the result Maps have Proper Data or not
	         if(resultXML.size() == 0 && resultDB.size() == 0){
	                System.out.println("Both the XML and DB Maps contain zero data");
	                return false;
	                
	         }else if(resultDB.size() == 0 && resultXML.size() != 0){
	                System.out.println("There is no Data in the DB Map");
	                return false;
	                
	         }else if(resultDB.size() != 0 && resultXML.size() == 0){
	                System.out.println("There is no Data in XML Map");
	                return false;
	                
	         }else if(resultDB.size() > resultXML.size()){
	                System.out.println("There are more Interactions exported than the eml files present in the Directory");
	                
	                Set<Integer> interIDsDB = resultDB.keySet();
	                Set<Integer> interIDsXML = resultXML.keySet();
	                
	                for(int inter : interIDsDB){
	                      if(!interIDsXML.contains(inter)){
	                             extraInterIDs.add(inter);
	                      }
	                }
	                
	                System.out.println("Extra Interactions: "+extraInterIDs);
	                return false;
	                
	         }else if(resultDB.size() < resultXML.size()){
	                System.out.println("There are more eml files have been generated than the Interactions in DB");
	                
	                Set<Integer> interIDsDB = resultDB.keySet();
	                Set<Integer> interIDsXML = resultXML.keySet();
	                
	                for(int inter : interIDsXML){
	                      if(!interIDsDB.contains(inter)){
	                             extraInterIDs.add(inter);
	                      }
	                }
	                
	                System.out.println("Extra eml files have been generated for the Interactions: "+extraInterIDs);
	                return false;
	                
	         }else{
	                //Loop through interIDs
	                for(int intDB : resultDB.keySet()){
	                	failed = new ArrayList();
	                      flag = false;
	                      for(int intXML : resultXML.keySet()){
	                             
	                             if(intXML == intDB){
	                                    
	                                    //Get the HashMap
	                                    HashMap<String, String> dbMap = resultDB.get(intDB);
	                                    HashMap<String, String> XMLMap = resultXML.get(intXML);
	                                    Set<String> dbMapKeys = dbMap.keySet();
	                                    Set<String> XMLMapKeys = XMLMap.keySet();
	                                    
	                                    for(String XMLMapKey : XMLMapKeys){
	                                    	for(String dbMapKey : dbMapKeys){
	                                    		if(XMLMapKey.equalsIgnoreCase(dbMapKey)){
	                                    			if(dbMap.get(XMLMapKey) != null || !dbMap.get(XMLMapKey).equals("")){
	                                    				if(XMLMap.get(XMLMapKey).equalsIgnoreCase(dbMap.get(dbMapKey))){
		                                    				System.out.println(XMLMapKey + " is Matched");
		                                    			}else{
		                                    				System.out.println(XMLMapKey + " did not Matched");
		                                    				failed.add(XMLMapKey);
		                                    			}
	                                    			}
	                                    		
	                                    		}
	                                    	}
	                                    }

	                                    //verifying headers
	                                    headerFlag = dbUtils.verifyHeaders(headersFromTestFile, intXML, headersFromEmls.get(intXML), exporterTemplete, header);
	                                    break;
	                             }
	                             failedInterIDs.put(intDB, failed);
	                      }
	                      
	                     
	                }
	                if(failedInterIDs.size() == 0){
	                	return true;
	                }else{
	                	return result && headerFlag;	
	                }
	                
	         }
	  }
	
		public HashMap<String, String> getMessageAttributes(String attributes) throws ParserConfigurationException, SAXException, IOException{
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			        DocumentBuilder builder = factory.newDocumentBuilder();
			        InputStream stream;
			        Document doc;
			        
			        HashMap<String, String> values = new HashMap();
			        
			        stream = new ByteArrayInputStream(attributes.getBytes());
			        doc = builder.parse(stream);
			        
			        org.w3c.dom.Element root = doc.getDocumentElement();
			        
			        NodeList attrs = root.getElementsByTagName("attr");
			        
			        for(int k=0; k<attrs.getLength(); k++){
			               
			               Node attr = attrs.item(k);
			               if(attr.getNodeType() == Node.ELEMENT_NODE){
			                     values.put(attr.getAttributes().item(0).getNodeValue(), attr.getTextContent());
			                     
			               }
			        }
			        return values;
			}
	
	public static String getTime(Long unixTime){
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
	
	public static HashMap<Integer, ArrayList<String>> getFailedInterIDs() {
		// TODO Auto-generated method stub
		return failedInterIDs;
	}
	
	public static int getExporterNum(String exporterName){
		int intex = exporterName.lastIndexOf(" ");
		return Integer.valueOf(exporterName.substring(intex+1)).intValue();
	}
	
	       
	       
	public  static String getSmtpUser(int number){
		String smtpUser = System.getProperty("smtpRecipients");
//		String user = "empTest@test.com";
		int index1 = smtpUser.indexOf("@");
		String str = smtpUser.substring(0,index1);
//		System.out.println(user.substring(0,index1));
		str = str + number;
		System.out.println(str);
		smtpUser = str + smtpUser.substring(index1);
		System.out.println(smtpUser);
		return smtpUser;
	}
	
	public static long getUnixTime(String timeStamp) throws ParseException{
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
	
//	get Modality 
	public String getModality(HashMap<String,String> map){
		String modality = "IM";
		for(String str : map.keySet()){
			if(str.equals("filter.exporterType")){
				if(map.get(str).contains("collab") || map.get(str).contains("Collab")){
					modality = "Collaboration";
					break;
				}else if(map.get(str).contains("email") || map.get(str).contains("Email")){
					modality = "email";
					break;
				}else{
					modality = "IM";
				}	
			}
		}
		return modality;
	} 

//	Get Scenario Name from scenario File Name...
	public static String getScenarioName(String fileSourceLocation){
	    	File file = new File(fileSourceLocation);
	    	String name = file.getName();
	    	int index = name.indexOf(".");
	    	return (name.substring(0,index));
	    }

//	 Creating Folder with same name of Scenario, if it is not exist
	public static void createDirectory(String folderPath) throws Exception {
        File dir = new File(folderPath);
        if(!dir.exists()){
        	dir.mkdir();
        }
    }
    
//	get smtp user name
	 public static String getUser(String userName){
		 int index = userName.indexOf("@");
		 System.out.println("User Folder Name : " +userName.substring(0, index));
		 return userName.substring(0,index);
	 }
	 
	 /**
	  * 
	  * @param emlsFolderPath
	  * @return
	  */
//	 get All the interaction ids from emls
	 public static ArrayList<Integer> getInterIdsFromEmls(String emlsFolderPath){
		 ArrayList<Integer> interIds = new ArrayList();
		 
		 File folder = new File(emlsFolderPath);
		 File[] files =  folder.listFiles();
		 for(File file : files){
			 String interId = getInterIdFromFile(file.getAbsolutePath());
			 interIds.add(Integer.valueOf(interId));
		 }
		 return interIds;
	 }
	 
	 /**
	  * 
	  * @param file
	  * @return
	  */
//	 get Header and its value(tokens) from properties file
	 public HashMap<String,String> getHeaderMapFromTestFile(File file){
		 HashMap<String,String> headerMap = new HashMap();
			try{
				FileInputStream fileInputStream=new FileInputStream(file);
				Properties properties=new Properties();
				properties.load(fileInputStream);
				fileInputStream.close();
				
//			ArrayList to store all keyset from property file which has customHeaderName as prefix
				ArrayList<Object> tempArrayList=new ArrayList<Object>();
				Set<Object> keyset=properties.keySet();
				for(Object o:keyset){
					if(((String) o).toLowerCase().contains("customHeaderName".toLowerCase())){
						tempArrayList.add((String) o);
					}
				}
//			Iterator for retrieving values of all the keys	
				
				Iterator<Object> itr=tempArrayList.iterator();
				String temp,temp1,temp2;
				while(itr.hasNext()){
					temp=(String) itr.next();	//Keys
					//get suffix and append it with customHeaderValue
					temp1=temp.replace("customHeaderName","");
					temp2="customHeaderValue".concat(temp1);
					if(properties.getProperty(temp2)!=null){
						headerMap.put(properties.getProperty(temp), properties.getProperty(temp2));
					}
					else{
						System.out.println("This  "+properties.getProperty(temp)+"      has no value");
					}
					
				}
				if(headerMap.isEmpty()){
					System.out.println("No Headers and tokens are define in test file");
				}
				
				for (Map.Entry me : headerMap.entrySet()) {
					System.out.println("Key:    "+me.getKey() + " &      Value: " + me.getValue());
				  }
			
			}
			catch(FileNotFoundException e){
				e.printStackTrace();
			}
			catch(IOException o){
				o.printStackTrace();
			};
			
			return headerMap;

		}
//	 check Scenario is  for Skip duplicate or not 
	 public static boolean skipDuplicate(HashMap<String,String> map){
		 
		 if(map.containsKey("skip_Duplicate")){
	    		return true;
	    	}else{
	    		return false;
	    	}
	 }
	 
//	 check Scenario is  for Skip Empty or not 
	 public static boolean skipEmpty(HashMap<String,String> map){
		 
		 if(map.containsKey("chatTranscripts")){
	    		return true;
	    	}else{
	    		return false;
	    	}
	 }
	 
	 
//	Check Exporter Type is Journal Mail or not
	 
	 public boolean checkJournal(HashMap<String,String> map){
		 if(map.containsKey("exportAsEnterpriseVaultJournal")){
			 return true;
		 }else{
			 return false;
		 }
	 }
	 
	 
//	 get Actual Email body From Journal Email
	public void getEmlFromJournalEmail(String smtpLocation) {
		try{

			File file = new File(smtpLocation);
			for (File temp : file.listFiles()) {
				if (temp.isDirectory()) {
					for (File emlFile : temp.listFiles()) {
	
						ArrayList<File> attachments = new ArrayList<File>();
						InputStream source;
	
						source = new FileInputStream(emlFile);
	
						Properties props = System.getProperties();
						props.put("mail.host", "smtp.dummydomain.com");
						props.put("mail.transport.protocol", "smtp");
	
						Session mailSession = Session.getDefaultInstance(props,
								null);
						MimeMessage message = new MimeMessage(mailSession, source);
						String subject = message.getSubject();
	
						Multipart multipart = (Multipart) message.getContent();
						
						for(int i=0; i<multipart.getCount(); i++){
							System.out.println(multipart.getContentType());
						}
						
						for (int x = 0; x < multipart.getCount(); x++) {
							BodyPart bodyPart = multipart.getBodyPart(x);
	
							if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())&& !StringUtils.isNotBlank(bodyPart.getFileName())) {
								continue; // dealing with attachments only
							}
							InputStream is = bodyPart.getInputStream();
	
							File f = new File(smtpLocation+"/" + subject + ".eml");
							FileOutputStream fos = new FileOutputStream(f);
							byte[] buf = new byte[10000];
							int bytesRead;
							while ((bytesRead = is.read(buf)) != -1) {
								fos.write(buf, 0, bytesRead);
							}
							fos.close();
							attachments.add(f);
	
						}
						System.out.println();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println();
		}
	}
	 
//	Check Interaction is duplicate or not
	 public static void checkInteractionDuplicate(ArrayList<Integer> fromDB, ArrayList<Integer> fromEml, HashMap<String,String> map, String exporterId){
		 
		 Integer networkId = 0;
		 String[] tempArray = exporterId.split(".");
		 Integer exporterNo = Integer.valueOf(tempArray[1]);
		 if(map.containsKey("networkIDs")){
			 networkId = Integer.valueOf(map.get("filter.networkIDs"));
		 }
		 HashMap<Integer,String> interStatusFromDB = dbUtils.getInterIDsStatus(fromDB, networkId.intValue(), exporterNo.intValue());
		 
		 for(Integer interIdFromEml : fromEml){
			 if(!interStatusFromDB.get(interIdFromEml).equals("Exported")){
				 System.out.println("Interaction Id : "+ interIdFromEml + " is Exported , but in DB it mark as " + interStatusFromDB.get(interIdFromEml));
			 }else{
				 System.out.println("Interaction Id : "+ interIdFromEml + " : is Exported, And in DB it is Exported");
			 }
		 }
		 
	 }
	
//	 check Interaction is Empty Or Not
	 
	 public static void checkEmptyInteractions(ArrayList<Integer> fromEml, ArrayList<Integer> fromDB){
		 for(Integer interId : fromDB){
			 for(Integer interID : fromEml){
				 if(interId.equals(interID)){
					 System.out.println("Interaction : "+interId+" : Empty but exported");
				 }
			 }
		 }
	 }
	 
	
//	
	
//	Delete Directory for Per Loop 
    public static void deleteDir(String folderPath){
    	try{
    		FileUtils.deleteDirectory(new File(folderPath));
    		System.out.println("Delete process is done");
    	}catch(Exception e){
    		System.out.println("Something went wrong");
    	}
    	
    }
	 
}
