package com.actiance.test.exporterNew.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.actiance.tests.office365.utils.FileName;
//import com.facetime.imcoreserver.export.db.DBParticipant_Vantage_11_0;

public class CollabProcessing {
	
	HashMap<Integer,ArrayList<String>> failedInterIDs= null;
	DBUtils dbUtils = new DBUtils();
	/**
	 * 
	 * @param directory
	 * @return
	 * @throws NumberFormatException
	 * @throws MessagingException
	 * @throws IOException
	 */
//	Get HTML Formatted Content Data for Collab from Eml Files
	public  HashMap<Integer, HashMap<String, String>> getHTMLContentForCollab(String directory){
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
        
        try{
            for(File file : files){
//                System.out.println("---------------------------------------------------------------------");
                
                messageValues = new HashMap();
  
                boolean firstRow = true;
                
                source = new FileInputStream(file);
                message = new MimeMessage(session, source);
                
                ArrayList<String> fileNames = new ArrayList();
                StringBuffer fileName = new StringBuffer();
                
                Multipart multipart = (Multipart) message.getContent();
         //Get HTML Part
         for(int i=0; i<multipart.getCount(); i++){
                if(multipart.getBodyPart(i).getContent().toString().contains("<!DOCTYPE HTML SYSTEM>")){
                      html = multipart.getBodyPart(i).getContent().toString();
//                      System.out.println(html);
                }
                BodyPart bodyPart = multipart.getBodyPart(i);
                if(Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
			               StringUtils.isNotBlank(bodyPart.getFileName())){
                	if(!multipart.getBodyPart(i).getFileName().contains(".htm")){
                		fileNames.add(multipart.getBodyPart(i).getFileName().toString().toLowerCase());
                	}
//                	System.out.println("FileName : "+multipart.getBodyPart(i).getFileName());
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
                                    if(col.attr("class").equalsIgnoreCase("c2")){
//                                           System.out.println(col.text());
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
                
//               Add fileNames into Message Map
                
                if(fileNames.size() > 0){
                	for(int i = 0 ; i < fileNames.size() ; i++){
                		fileName.append(fileNames.get(i));
                		if(i + 1 != fileNames.size()){
                			fileName.append("#");
                		}
                	}
                	messageValues.put("File", fileName.toString());
                }
                result.put(interID, messageValues);
//                System.out.println(result);
         }
        }catch(NumberFormatException | MessagingException | IOException e){
        	System.err.println(e.getMessage());
        }

        
//        System.out.println(result);
        
        return result;
 }
	
//	Generating XML Data For Collab From EML Files
	public  HashMap<Integer,HashMap<String,String>> getXMLDataForCollabFromEML(String emlFolder) {
		HashMap<Integer,HashMap<String,String>> result = new HashMap();
		 
		  HashMap<String, String> messageMap;
		  
		  Properties props = System.getProperties();
		  props.put("mail.host", "smtp.dummydomain.com");
		  props.put("mail.transport.protocol", "smtp");

		  Session session = Session.getDefaultInstance(props);
		  InputStream source;
		  Message message;
		  String xml = null;
		  int interID;
		  
		  try{
			  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			  DocumentBuilder builder = factory.newDocumentBuilder();
			  InputStream stream;
			  Document doc;
			  
			  File emlDirectory = new File(emlFolder);
			  File[] files = emlDirectory.listFiles();
			  
			  for(File file : files){
			   
			   messageMap = new HashMap();
			   
			   source = new FileInputStream(file);
			   System.out.println(file.getAbsolutePath());
			   message = new MimeMessage(session, source);
			   Object obj = (Object) message.getContent();
			   String subject = message.getSubject();
			   int index = subject.indexOf("#");
			   String interId = subject.substring(index+1);
			   StringBuffer fileName = new StringBuffer();
			   if(obj instanceof Multipart){
				   Multipart mul = (Multipart) obj;
				   
				   ArrayList<String> fileNames = new ArrayList();
//	              Get Files from EML and Add into ArrayList
				   for(int i = 0; i<mul.getCount();i++){
	              	 BodyPart bodyPart = mul.getBodyPart(i);
	        		   if(Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
				               StringUtils.isNotBlank(bodyPart.getFileName())) {
	        			   if(!bodyPart.getFileName().contains(".htm")){
//					        	System.out.println("File Name : "+ bodyPart.getFileName().toLowerCase());
	        				   fileNames.add(mul.getBodyPart(i).getFileName().toString());
					        
				        	}
				        }else{
				        	xml = mul.getBodyPart(i).getContent().toString();
				        }
	               }
	               
//				   Add File Names into Message map Separated by '#'
				   if(fileNames.size() > 0 ){
					   for(int  i = 0 ; i < fileNames.size(); i++){
						   fileName.append(fileNames.get(i));
						   if( i + 1 != fileNames.size()){
							   fileName.append("#");
							   
						   }
					   }
				   }	
				   messageMap.put("File",fileName.toString());
			   }else{
				   xml = message.getContent().toString();
			   }
			   
//			   xml = message.getContent().toString();
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
//			   getData.add("serverName");
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
		  }catch(IOException | MessagingException | SAXException | ParserConfigurationException e){
			  System.out.println(e.getMessage() + "ge mesg");
			  e.printStackTrace();
		  }

		 return result;
	} 

//	get Data from Eml's for Text Formatted Data
	   public HashMap<Integer,String> getMessage(String filePath){
        HashMap<Integer,String> emlMap = new HashMap();
        ArrayList<File> listOfFiles = new TestUtils().listFiles(filePath);
        Iterator itr = listOfFiles.iterator();
        try{
            while(itr.hasNext()){
          	   File file = (File) itr.next();
          	   InputStream is = new FileInputStream(file.getAbsoluteFile());
                 MimeMessage msg = new MimeMessage(null, is);
                 Integer interId = Integer.valueOf(msg.getSubject().split("#")[1]);
                 Object obj = (Object) msg.getContent();
                 String message = null;
                 StringBuffer msg1 = new StringBuffer();
//                 if(obj instanceof String){
//                	 
//                	message = obj.toString();
//                	System.out.println(message);
//                 }
                 Multipart mul = (Multipart) obj;
                 for(int i = 0; i<mul.getCount();i++){
                	 BodyPart bodyPart = mul.getBodyPart(i);
          		   if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
			               !StringUtils.isNotBlank(bodyPart.getFileName())) {
			            msg1.append((String)mul.getBodyPart(i).getContent().toString().replaceAll("\n", " ").replaceAll("\r", "").toLowerCase());
			        }else{
			        	if(!bodyPart.getFileName().contains(".htm")){
//				        	System.out.println("File Name : "+ bodyPart.getFileName().toLowerCase());
				        	msg1.append(", File: "+bodyPart.getFileName().toLowerCase());
			        	}
			        }
                	 
                 }
                 message = msg1.toString();
//                 if(obj instanceof String){
//                	 message = (String)obj;
//                 }
                 emlMap.put(interId, message);
             }
        }catch( MessagingException | NumberFormatException | IOException  |ClassCastException e){
        	System.out.println(e.getMessage());
        }
        return emlMap;
 }
		  
//	Check Tag Existence
	public String checkExistance(Element root, String tagName){
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
	
	//////////////////////////// Verification Methods ///////////////////////////////////
	
//	 verify Text Format Collabortion
	 
	 public boolean verifyTextFormatForCollab(HashMap<Integer,HashMap<String,String>> dataFromDB, HashMap<Integer, String> inputData, String format,HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete) throws NumberFormatException, XPathExpressionException, SQLException, ParserConfigurationException, SAXException, IOException{
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
						 System.out.println("--------------------------------------------------------");
						 System.out.println("Start: Verfication for Interaction Id : "+ dbInterId);
						messagesMap = dataFromDB.get(dbInterId);
						 String inputMessage = inputData.get(dbInterId);
						 
							 Set<String> attrs = messagesMap.keySet();
							 for(String attr : attrs){
								 if(!attr.equals("Text Content:") && !attr.equals("Resource URL:")){
//									 System.out.println("Value of "+attr + " " + messagesMap.get(attr));
									 if(inputMessage.contains(messagesMap.get(attr).toLowerCase())){
										 System.out.println(attr + " is matched ");
//										 System.out.println("Value of "+attr + " is \n" + messagesMap.get(attr));
									 }else{
										 System.out.println(attr + " Not matched");
										 
										 failed.add(attr);
									 }
								 }else if(attr.equals("Text Content:")){
									 if(inputMessage.replaceAll("\n", "").replaceAll("\r", "").contains(messagesMap.get(attr).replaceAll("\n", "").replaceAll("\r", ""))){
										 System.out.println(attr + " is matched ");
//										 System.out.println("Value of "+attr + " is \n" + messagesMap.get(attr));
									 }else{
										 System.out.println(attr + " : Not matched");
										 failed.add(attr);
									 }
								 }
							 }
//							 headerFlag = dbUtils.verifyHeaders(headersFromTestFile, dbInterId, headersFromEmls.get(dbInterId), exporterTemplete, header);
							 System.out.println("Stopped : Verfication is Done for Interaction Id : "+ dbInterId);
							 System.out.println("--------------------------------------------------------");
					 } 
					
					 }
				 failedInterIDs.put(dbInterId, failed);
				 }
		 }else{
			 for(int dbInterId : dataFromDB.keySet()){
				 failed = new ArrayList();
				 for(int inputInterId : inputData.keySet()){
					 if(dbInterId == inputInterId){
						 System.out.println("--------------------------------------------------------");
						 System.out.println("Start: Verfication for Interaction Id : "+ dbInterId);
						messagesMap = dataFromDB.get(dbInterId);
						 String inputMessage = inputData.get(dbInterId);
						 for(String attr : messagesMap.keySet()){
							if (!attr.equalsIgnoreCase("contentType:")
									&& !attr.equalsIgnoreCase("contentSubType:")
									&& !attr.equalsIgnoreCase("action:")
									&& !attr.equalsIgnoreCase("resourceName:")
									&& !attr.equalsIgnoreCase("Resource URL:")) {
								if(!attr.equalsIgnoreCase("Text Content:")){
									if(inputMessage.contains(messagesMap.get(attr))){
										System.out.println(attr.substring(0,attr.length()-1) + " is matched");
									}else{
										System.out.println(attr.substring(0,attr.length()-1) + " did not matched");
										failed.add(attr.substring(0,attr.length()-1));
									}
								}else{
									if(inputMessage.contains(messagesMap.get(attr))){
										System.out.println(attr.substring(0,attr.length()-1) + " is matched");
									}else{
										System.out.println(attr.substring(0,attr.length()-1) + " did not matched");
										failed.add(attr.substring(0,attr.length()-1));
									}
								}
							}
						 }
						 System.out.println("Stopped : Verfication is Done for Interaction Id : "+ dbInterId);
						 System.out.println("-----------------------------------------------------------------");
						} 
					 }
//				 headerFlag = dbUtils.verifyHeaders(headersFromTestFile, dbInterId, headersFromEmls.get(dbInterId), exporterTemplete, header);
				 
				 failedInterIDs.put(dbInterId, failed);
				 }
		 }
		 if(failedInterIDs.size() == 0)
			 return true; //&& headerFlag;
		 else{
			 return false;
		 }
	 }
	 
	
	
//	Verify HTML Formatted Data for Collab
	
	public boolean verifyHTMLContentForCollab(HashMap<Integer, HashMap<String, String>> resultDB,
			HashMap<Integer, HashMap<String, String>> resultHTML, HashMap<String, String> headersFromTestFile,
			HashMap<Integer, HashMap<String, String>> headersFromEmls, String exporterTemplete) {
		
		boolean result = true;
		boolean flag;
		boolean flag1 = false;
		Headers header = new Headers();
		boolean extra = true;
		boolean firstAttribute;
		ArrayList<String> columns = null;

		failedInterIDs = new HashMap();

		// Get interIDs
		Set<Integer> interIDsDB = resultDB.keySet();
		Set<Integer> interIDsHTML = resultHTML.keySet();
		System.out.println("----------Start : Verification for All Interactions--------------------");
		for (int intDB : resultDB.keySet()) {
			System.out.println("***Start : Verification is Start for Interaction : " + intDB + ": ***");
			flag = false;
			extra = true;
			firstAttribute = true;
			System.out.println();
			for (int intHTML : resultHTML.keySet()) {

				if (intHTML == intDB) {

					extra = false;
					columns = new ArrayList();

					// Get the HashMap
					HashMap<String, String> dbMap = resultDB.get(intDB);
					HashMap<String, String> HTMLMap = resultHTML.get(intHTML);

					// check if number of row data match
					if (dbMap.isEmpty() && HTMLMap.isEmpty()) {
						flag = true;
						extra = false;
						result = result & true;
						break;

					} else if (dbMap.size() != HTMLMap.size()) {
						System.out.println("DB Map : "+ dbMap.size() + " Eml Map : "+ HTMLMap.size());
						System.out.println(
								"Number of Message Data row didn't match with the Messag Data rows in the .eml file for InterID "
										+ intDB);
						result = false;
						columns = new ArrayList();
						columns.add(
								"Number of Message Data rows didn't match with the Messag Data rows in the .eml file");
						failedInterIDs.put(intDB, columns);
						break;

					} else {
						System.out.println("Number of Message Data rows matches for the InterID " + intDB);
					}



					// Verify Message Data
					for (String dbAttName : dbMap.keySet()) {
						if (dbAttName.equals("File")) {
							columns.addAll(verifyFile(HTMLMap.get("File").split("#"), dbMap.get("File").split("#")));
						}else if (HTMLMap.get(dbAttName) != null) {

							if (dbMap.get(dbAttName).equals(HTMLMap.get(dbAttName))) {
								System.out.println("Attribute: " + dbAttName + ", value matches with the HTML Content");
								if (firstAttribute) {
									flag = true;
									firstAttribute = false;
								} else {
									flag = flag & true;
								}
							} else {
								System.out.println(
										"Attribute: " + dbAttName + ", value did not match with the HTML Content");
								columns.add(dbAttName);
								flag = false;
							}
						} else {
							columns.add("Attribute: " + dbAttName + ", did not exist in HTML Content");
							flag = false;
						}
					}
					// verify Headers
					// flag1 = dbUtils.verifyHeaders(headersFromTestFile,
					// intHTML, headersFromEmls.get(intHTML), exporterTemplete,
					// header);

					break;
				}
			}

			if (extra) {
				columns = new ArrayList();
				columns.add("Extra InterID from Database");
				failedInterIDs.put(intDB, columns);
			}

			result = result & flag & (!extra) & flag1;

			if (!result & !flag & !extra) {
				failedInterIDs.put(intDB, columns);
			}

			System.out.println("***Stop : Verification is Done for Interaction : " + intDB);
		}
		System.out.println("-------Stop : Verification is Over For All Interaction for Current Senario--------");
		for (int interID : interIDsHTML) {
			if (!interIDsDB.contains(interID)) {
				columns = new ArrayList();
				columns.add("Extra InterID from .eml files");
				failedInterIDs.put(interID, columns);
				result = false;
			}
		}

		return result;
	}
   
//	Verify Collab For XML
		public boolean verifyXMLForCollab(HashMap<Integer,HashMap<String,String>> resultXML, HashMap<Integer,HashMap<String,String>> resultDB, String formatVersion, HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete){
	         Headers header = new Headers();
//	         DBUtils dbUtils = new DBUtils();
	         boolean headerFlag = false;
	         ArrayList<Integer> extraInterIDs = new ArrayList();
	         boolean result = true;
	         boolean flag;
	         ArrayList<String> failed ;
	         failedInterIDs = new HashMap();
	         
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
	        	 System.out.println("----------------------------------------------------");
	        	 System.out.println("Start Verification for All Interactions : ");
	        	 System.out.println("----------------------------------------------------");
	        	 System.out.println();
	                //Loop through interIDs
	                for(int intDB : resultDB.keySet()){
	                	System.out.println();
	                	System.out.println("*******************************************");
	                	System.out.println("Verfication for Interaction : " + intDB);
	                	failed = new ArrayList();
	                      flag = false;
	                      for(int intXML : resultXML.keySet()){
	                             
	                             if(intXML == intDB){
	                                    
	                                    //Get the HashMap
	                                    HashMap<String, String> dbMap = resultDB.get(intDB);
	                                    HashMap<String, String> XMLMap = resultXML.get(intXML);
	                                    Set<String> dbMapKeys = dbMap.keySet();
	                                    Set<String> XMLMapKeys = XMLMap.keySet();
	                                    
	                               		if(formatVersion.contains("X1")){
	                               			dbMap.put("networkID", new DBUtils().getNetworkName(dbMap.get("networkID")));
	                               		}
		                                    for(String XMLMapKey : XMLMapKeys){
		                                    	if(!XMLMapKey.equals("serverName")){
		                                    		if(XMLMapKey.equals("File")){
		                                    			failed.addAll(verifyFile(XMLMap.get(XMLMapKey).split("#"), dbMap.get(XMLMapKey).split("#")));
		                                    		}
			                                    	for(String dbMapKey : dbMapKeys){

			                                    		if(XMLMapKey.equalsIgnoreCase(dbMapKey)){
			                                    			if(dbMap.get(dbMapKey) != null && !dbMap.get(dbMapKey).equals("")){
			                                    				if(XMLMap.get(XMLMapKey).equalsIgnoreCase(dbMap.get(dbMapKey))){
				                                    				System.out.println(XMLMapKey + " is Matched");
				                                    				break;
				                                    			}else{
				                                    				System.out.println(XMLMapKey + " did not Matched");
				                                    				failed.add(XMLMapKey);
				                                    			}
			                                    			}
			                                    		}
			                                    	}
		                                    	}
		                                    }

	                                    //verifying headers
//	                                    headerFlag = dbUtils.verifyHeaders(headersFromTestFile, intXML, headersFromEmls.get(intXML), exporterTemplete, header);
	                                    break;
	                             }
	                             failedInterIDs.put(intDB, failed);
	                              
	                      }
	                      System.out.println("Verifcation is Over for Interaction : "+ intDB);
	                    
	                }
	                if(failedInterIDs.size() == 0){
	                	return true;
	                }else{
	                	return false; //&& headerFlag;	
	                }
	                
	         }

	}
		
		public HashMap<Integer, ArrayList<String>> getFailedId(){
			return failedInterIDs;
		}
		
		public ArrayList<String> verifyFile(String[] fileFromEML, String[] fileFromDB){
			boolean flag = false;
			ArrayList<String> result = new ArrayList();
//			boolean result = true;
			if(fileFromEML.length == fileFromDB.length){
				System.out.println("No of Files are Same ");
				for(int i = 0 ; i < fileFromEML.length; i++){
					for(int j = 0 ; j < fileFromDB.length; j++){
						if(fileFromEML[i].toLowerCase().equals(fileFromDB[j].toLowerCase())){
							System.out.println("File Name Matched");
							flag = true;
							break;
						}else{
							continue;
						}
					}
					if(!flag){
						result.add(fileFromEML[i]);
					}
				}
			}
			return result;
		}
}
