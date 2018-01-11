package com.actiance.test.exporterNew.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ParsingXSLTs {

	public static void main(String[] args) throws MessagingException, IOException{
		String filePath = "E:\\D-Drive\\Exporter\\XSLT\\manual\\IM\\HTML\\Test";
		
		parseCSV(filePath);
		
		
	}
	
	///////// Testing Code ////////////////////////////////////
	public static void parseCSV(String filePath) throws MessagingException, IOException{
		
		HashMap<Integer,HashMap<String,ArrayList<String>>> map = getHTMLContentOfXSLTForIM(filePath);
		
		for(Integer inter : map.keySet()){
			System.out.println("**********"+inter+"**********");
			System.out.println(map.get(inter));
			System.out.println("*****************************");
		}
		
/*		HashMap<Integer,HashMap<String,String>> result = parseCSV_Xslt(filePath);
		for(Integer interID : result.keySet()){
			System.out.println("*********************************");
			System.out.println("Interaction Id : "+interID);
			System.out.println(result.get(interID));
			System.out.println("********************************");
		}*/
	}
	
	
	/////////////////////////////////////////////////////////////////
	
	//Return LinkedHashMap with interactionid as key and content of Eml as value
	public static HashMap<Integer, HashMap<String, String>> parseCSV_Xslt(String path){
		File file = new File(path);
		String[] fieldsName = {"ContentTypes","ContentSubTypes","NetworkName","ParentObjectID","ObjectId","ObejctURI","Action","UserId","EmployeeId","EmailAddress","ResourceName","ResourceURI","ResourceID","Title","ContentText","AssociatedFileInteractions","FileNameOriginal",""};
//		LinkedHashMap with interactionid as key and content of Eml as value
		HashMap<Integer, HashMap<String, String>> csvMap=new HashMap();
		try{
			
			for(File xfile:file.listFiles()){
				InputStream source;
		        source = new FileInputStream(xfile);
		        Properties props = System.getProperties();
		        props.put("mail.host", "smtp.dummydomain.com");
		        props.put("mail.transport.protocol", "smtp");
		        Session mailSession = Session.getDefaultInstance(props, null);
		        MimeMessage message = new MimeMessage(mailSession, source);
//		        System.out.println(message.getContent().toString().substring(1));
		        String str=message.getContent().toString().substring(1);
		        String []tempLineBreak=str.split("\n");
		        String [] fieldName=tempLineBreak[0].split(",");
		        String [] values=tempLineBreak[1].substring(1).split("\",\"");
		        values[values.length-1]="";
//		        LinkedHashMap with Fieldname as Key and respective value as HashValue
		       LinkedHashMap<String, String> dataMap=new LinkedHashMap<String, String>();
		       HashMap<String,String> fieldWithValue = new HashMap();
		       HashMap<String,String> messageAttr = new HashMap();
		       for(int i = 0;i<fieldName.length;i++){
		    	   if(fieldName[i].equalsIgnoreCase("ParentObjectID")){
		    		   dataMap.put("parentEventID", values[i]);
		    	   }
		    	   if(fieldName[i].equalsIgnoreCase("ObjectID")){
		    		   dataMap.put("eventId", values[i]);
		    	   }
		    	   if(fieldName[i].equalsIgnoreCase("ContainerID")){
		    		   dataMap.put("containerId", values[i]);
		    	   }
		    	   if(fieldName[i].equalsIgnoreCase("Title")){
		    		   dataMap.put("roomName", values[i]);
		    	   }
		    	   if(fieldName[i].equalsIgnoreCase("UserID")){
		    		   dataMap.put("buddyName", values[i]);
		    	   }
//		    	   if(fieldName[i].equalsIgnoreCase("ContentText")){
//		    		   dataMap.put("networkID", values[i]);
//		    	   }
		    	   if(fieldName[i].equalsIgnoreCase("NetworkName")){
		    		   dataMap.put("networkName", values[i]);
		    	   }
		     	   if(fieldName[i].equalsIgnoreCase("EmployeeID")){
		    		   dataMap.put("employeeId", values[i]);
		    	   }
		     	   if(fieldName[i].equalsIgnoreCase("EmailAddress")){
		    		   dataMap.put("employeeEmail", values[i]);
		    	   }

		     	   if(fieldName[i].equalsIgnoreCase("ContentType")){
		    		   dataMap.put("Content type", values[i]);
		    	   }
		     	   if(fieldName[i].equalsIgnoreCase("ContentSubtype")){
		    		   dataMap.put("Content sub-type", values[i]);
		    	   }
		     	   if(fieldName[i].equalsIgnoreCase("Action")){
		    		   dataMap.put("Action", values[i]);
		    	   }
		     	   if(fieldName[i].equalsIgnoreCase("ResourceName")){
		    		   dataMap.put("Resource name", values[i]);
		    	   }
		     	   if(fieldName[i].equalsIgnoreCase("ResourceURI")){
		    		   dataMap.put("Resource URL", values[i]);
		    	   }
		     	   if(fieldName[i].equalsIgnoreCase("ContentText")){
		    		   dataMap.put("Text", values[i]);
		    	   }

		    	   if(fieldName[i].equalsIgnoreCase("Attributes")){
//		    		   System.out.println(values[i]);
		    		   String []temp=values[i].split("\\$\\$");
//		    		   System.out.println(temp.length+"\n\n\n\n");
		    		   for(int j=0;j<temp.length;j++){
//		    			   System.out.println(temp[j]);
		    			   String []jtemp=temp[j].split(":");
//		    			   messageAttr.put(jtemp[0], jtemp[1]);
		    			   dataMap.put(jtemp[0],jtemp[1]);
		    		   }
		    		   continue;
		    	   }
//		    	   dataMap.put(fieldName[i].trim(),values[i]);
		       }
		       
		       String interId=values[0];
		       System.out.println(interId);
		       csvMap.put(Integer.valueOf(interId), dataMap);
			}
		}catch(MessagingException me){
			System.out.println("Error while reading EML File");
		}catch(IOException e){
			System.out.println("Can not Access File");
		}
		return csvMap;
	}
	
//	parse HTML XSLT (checked with Yammer and Jive)
	
	public  HashMap<Integer, HashMap<String, String>> parseHTML_XSLT_Collab(String directory) throws NumberFormatException, MessagingException, IOException{
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
               messageValues = new HashMap();
 
               boolean firstRow = true;
               
               source = new FileInputStream(file);
               message = new MimeMessage(session, source);
               
               ArrayList<String> fileNames = new ArrayList();
               
               Object obj = (Object) message.getContent();
               
               if(obj instanceof String){
            	   html = (String)message.getContent();
               }else{
                   
                   Multipart multipart = (Multipart) message.getContent();
                   
                   for(int i = 0 ; i< multipart.getCount(); i++){
                	   BodyPart bodyPart = multipart.getBodyPart(i);
              		   if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
    			               !StringUtils.isNotBlank(bodyPart.getFileName())){
              			 html = multipart.getBodyPart(i).getContent().toString();
					} else {
						try {
							InputStream is = bodyPart.getInputStream();
							String filePath = "C:\\temp";
							File f;
							FileOutputStream fos = null;
// creating the temp file and copy the attachment into it
							
							f = new File(filePath + "\\" + bodyPart.getFileName());
							fos = new FileOutputStream(f);
							byte[] buf = new byte[10000];
							int bytesRead;
							while ((bytesRead = is.read(buf)) != -1) {
								fos.write(buf, 0, bytesRead);
							}
							if(f.getName().endsWith(".zip") || f.getName().endsWith(".rar")){
								FileInputStream fis = null;
				                ZipInputStream zipIs = null;
				                ZipEntry zEntry = null;
				                fis = new FileInputStream(f);
				                zipIs = new ZipInputStream(new BufferedInputStream(fis));
				                while((zEntry = zipIs.getNextEntry()) != null){
//				                	System.out.println(zEntry.getName());
				                    fileNames.add(zEntry.getName());
				                    
				                }
				                fileNames = getFileNames(fileNames);
				                zipIs.close();
							}else{
								fileNames.add(f.getName());
								fileNames = getFileNames(fileNames);
							}
						} catch (IOException e) {

						}
						messageValues.put("File", getFileNameWithHash(fileNames));
					}
                   }            	   
               }

               
//               html = (String) message.getContent();

               interID = Integer.parseInt(message.getSubject().split("#")[1]);
               
               org.jsoup.nodes.Document doc = Jsoup.parse(html);
               Elements table = doc.getElementsByTag("table");
               Elements rows = table.get(1).getElementsByTag("tr");
               //Get Text Value
               for(org.jsoup.nodes.Element ro : rows){
                     //Skip first row 
                     if(firstRow){
                            firstRow = false;
                     }else{
                            Elements columns = ro.getElementsByTag("td");
                            for(org.jsoup.nodes.Element col : columns){
                            	String value = col.text();
                            	int index =value.indexOf(":");
                            	String mapKey = value.substring(0, index);
                            	String mapValue = value.substring(index+1);
                            	messageValues.put(mapKey, mapValue);
                            }
                     }
               }
//               get Text Content Part
               
               Elements table2 = doc.getElementsByTag("table");
               org.jsoup.nodes.Element row3 = table2.get(2).getElementsByTag("tr").get(3).getElementsByTag("td").get(0);
               org.jsoup.nodes.Element pre = row3.getElementsByTag("pre").get(0);
//               System.out.println(pre.text());
               
               messageValues.put("Text", pre.text());
              /* Elements row2 = table2.get(2).getElementsByTag("tr");
//               System.out.println("Size : "+ row2.size());
               
               //Get Text Value
               boolean second = true;
               firstRow = true;
               boolean third = true;
               for(org.jsoup.nodes.Element ro : row2){
                     //Skip first row 
                     if(firstRow){
//                    	 System.out.println(ro.text());
                            firstRow = false;
                            
                     }else if(second){
//                    	 System.out.println(ro.text());
                    	 second = false;
                     }else if(third){
                    	 third = false;
                     }
                     else{
                    	 System.out.println("--------------------------------");
                            Elements columns = ro.getElementsByTag("td");
                            for(org.jsoup.nodes.Element col : columns){
                            	Elements pre = col.getElementsByTag("pre");
//                            	System.out.println("Pre Size : "+pre.size());
                            	System.out.println(col.text());
                            	String value = col.text();
                            	if(!value.contains(":")){
                            		messageValues.put("Content", value);
                            	}else{
                            		int index =value.indexOf(":");
                                	String mapKey = value.substring(0, index);
                                	String mapValue = value.substring(index+1);
//                                	System.out.println(mapKey +"->>>"+mapValue);
                                	messageValues.put(mapKey, mapValue);
                            	}
                            }
                     }
               }*/
               
//               Adding File name into Message Map
               StringBuffer fileName = new StringBuffer();
               if(fileNames.size() > 0){
            	   for(int  i = 0 ; i <fileNames.size(); i++){
            		   fileName.append(fileNames.get(i));
            		   if(i +1 != fileNames.size()){
            			   fileName.append("#");
            		   }
            	   }
            	   messageValues.put("File", fileName.toString());
               }
            
/*               for(String msgKeys : messageValues.keySet()){
            	   if(msgKeys.equals("Resource URI")){
            		   messageValues.put("Resource URL", value)
            		   
            	   }
               }*/
               result.put(interID, messageValues);
        }
        
        return result;
  }	
	
	public ArrayList<String> getFileNames(ArrayList<String> fileNames){
		ArrayList<String> getFileName = new ArrayList();
		for(String fileName : fileNames){
			String temp = fileName.substring(fileName.indexOf("_")+1);
			temp = temp.substring(temp.indexOf("_")+1);
			getFileName.add(temp);
		}
		return getFileName;
	}
	
	public String getFileNameWithHash(ArrayList<String> fileNames){
		StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i < fileNames.size(); i++){
			sb.append(fileNames.get(i));
			if(i + 1 != fileNames.size()){
				sb.append("#");
			}
		}
		return sb.toString();
	}
	
/////////// Get HTML Data for IM modality of XSLT
		
	public static HashMap<Integer, HashMap<String, ArrayList<String>>> getHTMLContentOfXSLTForIM(String directory) throws MessagingException, IOException{
	  
	  HashMap<Integer, HashMap<String, ArrayList<String>>> result = new HashMap();
	  ArrayList<String> messageValues= null;
	  HashMap<String, ArrayList<String>> messageMap = null;
	  
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
	         int row = 0;
	         boolean firstRow = true;
	         messageMap = new HashMap();
	         messageValues = new ArrayList();
	         source = new FileInputStream(file);
	         message = new MimeMessage(session, source);
	         
	         interID = Integer.parseInt(message.getSubject().split("#")[1]);
	         html = message.getContent().toString();
	         org.jsoup.nodes.Document doc = Jsoup.parse(html);
	         Elements table = doc.getElementsByTag("table");
	        /* 
	         * Skipping Network, RoomName and Interaction Id from Head Part of EML (if Want than uncomment below code)
	         * 
	         * Elements rowsHead = table.get(0).getElementsByTag("tr");
	         for(org.jsoup.nodes.Element ro : rowsHead){
	      	   ArrayList<String> headValue = new ArrayList();
	      	   Elements columns = ro.getElementsByTag("td");
	      	   String[] str = columns.text().split(":");
	      	   headValue.add(str[1].trim());
	      	   messageMap.put(str[0].trim(),headValue);
	      	   
	         }*/
	         Elements rows = table.get(1).getElementsByTag("tr");
	         boolean second = true;
	         for(org.jsoup.nodes.Element ro : rows){
	               
	               //Skip first row -
	                if(firstRow){
	                      firstRow = false;
	//                      skip second row in body
	               }else if(second){
	              	 second = false;
	               }else{
	                      messageValues = new ArrayList();
	                      
	                      Elements columns = ro.getElementsByTag("td");
	                      
	                      //Check message column text
	                      String messageText = columns.get(3).text();
	//                      System.out.println("**********************************************************");
	//                      System.out.println(messageText);
	//                      System.out.println("**********************************************************");
	                      if(messageText.contains("Interaction Started") || messageText.equalsIgnoreCase("Joined Conversation")
	                                    || messageText.equalsIgnoreCase("Left Conversation") || messageText.equalsIgnoreCase("Interaction Ended")){
	                             //do nothing
	                      }else{
	                             messageValues.add(columns.get(0).text());
	                             messageValues.add(columns.get(2).text());
	                             messageValues.add(columns.get(3).text());
	                             
	                             messageMap.put("Row: "+row++, messageValues);
	                      }
	               }
	         }
	         
	         result.put(interID, messageMap);
	  }
	  
	  return result;
	}

//	Get Data from XML for IM
	
	public static HashMap<Integer, HashMap<String, ArrayList<String>>> getXMLContentOfXSLTForIM(String path){
		FileOutputStream fos=null;
		File f=null;
		InputStream source=null;
		File file=new File(path);
		HashMap<Integer, HashMap<String, ArrayList<String>>> tempMap=new HashMap<>();
		try{
//			list all the Eml files
			for(File tempFile:file.listFiles()){
				   source = new FileInputStream(tempFile);
		           Properties props = System.getProperties();
		           props.put("mail.host", "smtp.dummydomain.com");
		           props.put("mail.transport.protocol", "smtp");
		    	   Session mailSession = Session.getDefaultInstance(props, null);
		           MimeMessage message = new MimeMessage(mailSession, source);
		           String mpart=(String) message.getContent();
		           int index=mpart.indexOf("<interactions");
		           InputStream is = new ByteArrayInputStream(mpart.substring(index).getBytes());
		           	f = new File(path+"\\temp.xml");
		   	        fos = new FileOutputStream(f);
		   	        byte[] buf = new byte[10000];
		   	        int bytesRead;
		   	        while((bytesRead = is.read(buf))!=-1) {
		   	            fos.write(buf, 0, bytesRead);
		   	        }
//		   	        parse files one by one
		   	     tempMap.putAll(parseXml(f));
		   	     source.close();
		   	        fos.close();
		   	        f.delete();
				
			}
		}
		catch(MessagingException | IOException e){
			
		}
		return tempMap;
	}
	
//	return HashMap with interID as Key value.
	public static HashMap<Integer, HashMap<String, ArrayList<String>>> parseXml(File file) {
		HashMap<Integer, HashMap<String, ArrayList<String>>> finalMap=new HashMap<>();
		HashMap<String, ArrayList<String>> tempMap=new HashMap<>();
		int count=0;
		try{
			DocumentBuilderFactory dbFactory 
		    = DocumentBuilderFactory.newInstance();
		 DocumentBuilder dBuilder;

		 dBuilder = dbFactory.newDocumentBuilder();
		 Document doc = dBuilder.parse(file);
		 doc.getDocumentElement().normalize();
		 XPath xPath =  XPathFactory.newInstance().newXPath();
//		Getting msgSent Values through Xpath.		 
		 String expression = "/interactions/interaction/transcript/event";
		 NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
		 
		 for(int i=0;i<nodeList.getLength();i++){
			 ArrayList< String> list=new ArrayList<>();
			 	Node nNode = nodeList.item(i);
//			 	create rowValue on the basis of no. of msgSent.
			 	String rowValue="Row: "+String.valueOf(count++);
			 	if(nNode.getNodeType()==Node.ELEMENT_NODE){
			 		Element eElement=(Element) nNode;
			 		//nodevalue.add(eElement.getAttribute("name") );
			 		list.add(eElement.getElementsByTagName("DateTimeUTC").item(0).getTextContent());
			 		list.add(eElement.getElementsByTagName("LoginName").item(0).getTextContent());
			 		list.add(eElement.getElementsByTagName("Content").item(0).getTextContent());
			 	}
			 	tempMap.put(rowValue, list);
			 }
//		 Getting interId value through Xpath.
		 expression = "/interactions/interaction";
		  nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

		     Node nNode = nodeList.item(0);
		     if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		        Element eElement = (Element) nNode;
		        NodeList node1=eElement.getElementsByTagName("interID");
		        String interId=node1.item(0).getTextContent();
		        finalMap.put(Integer.valueOf(interId), tempMap);
		     }
		}
		catch( SAXException | IOException | ParserConfigurationException | XPathExpressionException e){
			System.out.println("error while parsing");
			e.printStackTrace();
		}
		return finalMap;
	}

}
