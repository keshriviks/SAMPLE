package com.actiance.test.exporterNew.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class IMProcessing {
	TestUtils testUtils ;
	DBUtils dbUtils = new DBUtils();
	HashMap<Integer, ArrayList<String>> failedInterIDs;
	
	
//	get Data from Eml's for Text Formatted Data
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws MessagingException
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	   public HashMap<Integer,String> getMessage(String filePath){
        HashMap<Integer,String> emlMap = new HashMap();
        testUtils = new TestUtils();
        ArrayList<File> listOfFiles = testUtils.listFiles(filePath);
        Iterator itr = listOfFiles.iterator();
        try{
            while(itr.hasNext())
            {
         	   File file = (File) itr.next();
         	   InputStream is = new FileInputStream(file.getAbsoluteFile());
                MimeMessage msg = new MimeMessage(null, is);
              
                Integer interId = Integer.valueOf(testUtils.getInterIdFromFile(file.getAbsolutePath()));
                String messages = (String)msg.getContent();
                emlMap.put(interId, messages.replaceAll("\n", "").replaceAll("\r", "").trim());
            }
        }catch(MessagingException | NumberFormatException | IOException e){
        	System.err.println(e.getMessage());
        }

        
        return emlMap;
 }


//		Get Data from Eml's for XML Formatted Data of IM
		public  HashMap<Integer, HashMap<String, ArrayList<String>>> getXMLContent(String directory){
	        
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

	        try{
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
		               root.getChildNodes();
		               
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
	        }catch(MessagingException | IOException | ParserConfigurationException | SAXException e){
	        	System.err.println(e.getMessage());
	        }
	     
	        
	        return result;
	 }
		
	
//	Get HTML formatted Data From Eml for IM
	
	public  HashMap<Integer, HashMap<String, ArrayList<String>>> getHTMLContent(String directory){
        
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
        try{
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
        }catch( MessagingException | IOException e){
        	System.err.println(e.getMessage());
        }

        
        return result;
 }
	
	//////////////// Utility Method //////////////////////////////
	
	
	public  HashMap<String, ArrayList<String>> sortMap(HashMap<String, ArrayList<String>> map){
        
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
	
	
	///////////////////////// Verification Methods //////////////////////////////////////

	public boolean verifyXMLData(HashMap<Integer, HashMap<String, ArrayList<String>>> resultDB, HashMap<Integer, HashMap<String, ArrayList<String>>> resultXML,String versionProperty, HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete){
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
//	                        flag1 = dbUtils.verifyHeaders(headersFromTestFile, intXML, headersFromEmls.get(intXML), exporterTemplete, header);
	                        
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
	           System.out.println("Interaction Pass : "+ flag);
	           System.out.println("No Extra Emls : "+ !extra);
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
	
	
	
	public boolean verifyTextFormatData(HashMap<Integer, HashMap<String, ArrayList<String>>> dataFromDB, HashMap<Integer, String> inputData,HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete){
	    boolean flag = true;
	    Set<Integer> interIdsFromDB = dataFromDB.keySet();
	    failedInterIDs = new HashMap();
	    Headers header = new Headers();
	    boolean flag1 = false;
	    ArrayList<String> failed;
	    for(int dbInterId : dataFromDB.keySet()){
	    	
	           for(int inputInterId : inputData.keySet()){
	                 if(dbInterId == inputInterId){
	                	 System.out.println();
	                	 System.out.println("*******************************************");
	                	 failed = new ArrayList();
	                	 System.out.println("For interaction id :"+dbInterId);
	                	 System.out.println();
	                        HashMap<String, ArrayList<String>> dbMap = dataFromDB.get(dbInterId);
	                        String inputText = inputData.get(dbInterId);
	                        
	                         for(int row = 0; row<dbMap.size(); row++){
	                        	 System.out.println("DB Time Stamp : "+dbMap.get("Row: "+ row).get(0));
	                        	 System.out.println("sent time :" + testUtils.getTime(Long.valueOf(dbMap.get("Row: "+ row).get(0))));
	                               if(inputText.contains(testUtils.getTime(Long.valueOf(dbMap.get("Row: "+ row).get(0))))){
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
	
//	Verify HTML Formatted Data for IM
	
	public boolean verifyHTMLContent(HashMap<Integer, HashMap<String, ArrayList<String>>> resultDB, HashMap<Integer, HashMap<String, ArrayList<String>>> resultHTML,HashMap<String,String> headersFromTestFile, HashMap<Integer,HashMap<String,String>> headersFromEmls, String exporterTemplete) throws ParseException{
        
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
                                   if(dbMap.get("Row: "+row).get(0).equals(testUtils.getUnixTime(HTMLMap.get("Row: "+row).get(0)))){
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
	
	public HashMap<Integer,ArrayList<String>> getFailedId(){
		return failedInterIDs;
	}
}
