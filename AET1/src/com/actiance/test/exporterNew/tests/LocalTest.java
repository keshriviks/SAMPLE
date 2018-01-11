package com.actiance.test.exporterNew.tests;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

import org.apache.batik.ext.awt.image.codec.util.MemoryCacheSeekableStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.actiance.test.exporterNew.utils.CommonUtils;

public class LocalTest {
	public static void main(String... s) throws Exception{
		
		System.setProperty("smtpRecipients", "u@act.in");
		
		System.out.println(CommonUtils.getUser("u1@act.in"));
		
		
		
		System.out.println(CommonUtils.getSmtpUser(1));
		String emldump = "D:/D-Drive/Exporter/Automation/emls" + "/" + "u1";
		String smtp = "D:/installed/hMailServer/Data/act.in" + "/" + "u1";
		CommonUtils.createDirectory(emldump);
		CommonUtils.changeFileNamesAndCopy(smtp, emldump);
		
		
/*// for Collab CSV
		String dirPath = "E:/D-Drive/Exporter/XSLT/manual/Collab/HTML/exptest4";
		HashMap<Integer,HashMap<String,String>> getData = getHTML_XSLT_ContentForCollab(dirPath);
		
*/		
		// for IM HTML
//		String dirPath = "E:/D-Drive/Exporter/XSLT/manual/IM/HTML/Test";
//		HashMap<Integer,HashMap<String,ArrayList<String>>> getData = getHTMLContentOfXSLTForIM(dirPath);
		
		/*for(Integer interId : getData.keySet()){
			System.out.println("******************** Interaction Id : "+ interId + "***************************");
			HashMap<String,ArrayList<String>> msgMap = getData.get(interId);
			for(String attr : msgMap.keySet()){
				System.out.println(attr + " : "+ msgMap.get(attr));
			}
			System.out.println("******************** Finish Interaction Id : "+ interId + "***************************");
		}*/
		
//		for Collab XML
		/*
		String smtpLocation = "E:/D-Drive/Exporter/XSLT/manual/Collab/Test";
		getEmlFromJournalEmail(smtpLocation);
		*/
//		getFileName();
//		changeKey();
		
		
	}
	
	
	public static void changeKey(){
		HashMap<String,String> map = new HashMap();
		map.put("A", "Manish");
		map.put("B", "kanika");
		map.put("C", "Prabhakar");
		System.out.println(map);
		for(String key : map.keySet()){
			if(key.equals("B")){
				map.remove(key);
			}
		}
		System.out.println(map);
	}
	
	public static void getFileName(){
		String filePath = "E:\\VijayTest\\Zip Test\\2006_Attachments_1478856576975.zip";

//        System.out.println("inside1");
        File file = new File(filePath);
        try {
        	String temp = null;
        	if(file.getName().endsWith(".rar") || file.getName().endsWith(".zip")){
        		FileInputStream fis = null;
                ZipInputStream zipIs = null;
                ZipEntry zEntry = null;
                fis = new FileInputStream(file);
                zipIs = new ZipInputStream(new BufferedInputStream(fis));
                while((zEntry = zipIs.getNextEntry()) != null){
                    temp = zEntry.getName().substring(zEntry.getName().indexOf("_")+1);
                    temp=temp.substring(temp.indexOf("_")+1);
                    System.out.println(zEntry.getName());
                }
                zipIs.close();
        	}else{
        		temp = file.getName().substring(file.getName().indexOf("_")+1);
        		
        		
        		System.out.println(temp.substring(temp.indexOf("_")+1));
        	}

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        System.out.println("insside2");

	}
	
/*	public ArrayList<String> actualFileName(ArrayList<String> fileNames){
		ArrayList<>
	}*/
	public static HashMap<Integer, HashMap<String, String>> getHTML_XSLT_ContentForCollab(String directory) throws NumberFormatException, MessagingException, IOException{
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
               
               html = (String) message.getContent();

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
//                            	System.out.println(col.text());
                            	String value = col.text();
                            	int index =value.indexOf(":");
                            	String mapKey = value.substring(0, index);
                            	String mapValue = value.substring(index+1);
//                            	System.out.println(mapKey +"->>>"+mapValue);
                            	messageValues.put(mapKey, mapValue);
                                  /* if(col.attr("class").equalsIgnoreCase("c1")){
                                          //System.out.println(col.text());
                                          messageValues.put("Text", col.text().trim());
                                   }*/
                            }
                     }
               }
               
//               get Text Content Part
               
               Elements table2 = doc.getElementsByTag("table");
               Elements row2 = table2.get(2).getElementsByTag("tr");
//               System.out.println("Size : "+ row2.size());
               
               //Get Text Value
               boolean second = true;
               firstRow = true;
               int i = 0 ;
               for(org.jsoup.nodes.Element ro : row2){
                     //Skip first row 
                     if(firstRow){
                            firstRow = false;
                            
                     }else if(second){
                    	 second = false;
                     /*}else if(row2.size()-1 == i){
                    	 break;*/
                     }else{
                            
                            Elements columns = ro.getElementsByTag("td");
//                            System.out.println(" Column Size"+columns.size());
                            for(org.jsoup.nodes.Element col : columns){
//                            	Elements pre = col.getElementsByTag("pre");
//                            	System.out.println("Pre Size : "+pre.size());
//                            	System.out.println(col.text());
                            	String value = col.text();
                            	if(!value.contains(":")){
                            		messageValues.put(value, "");
                            	}else{
                            		int index =value.indexOf(":");
                                	String mapKey = value.substring(0, index);
                                	String mapValue = value.substring(index+1);
//                                	System.out.println(mapKey +"->>>"+mapValue);
                                	messageValues.put(mapKey, mapValue);
                            	}
                            
                                  /* if(col.attr("class").equalsIgnoreCase("c1")){
                                          //System.out.println(col.text());
                                          messageValues.put("Text", col.text().trim());
                                   }*/
                            }
                     }
                     i++;
               }
               
               result.put(interID, messageValues);
        }
        
        return result;
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
               Elements rowsHead = table.get(0).getElementsByTag("tr");
               for(org.jsoup.nodes.Element ro : rowsHead){
            	   ArrayList<String> headValue = new ArrayList();
            	   Elements columns = ro.getElementsByTag("td");
            	   String[] str = columns.text().split(":");
            	   headValue.add(str[1].trim());
            	   messageMap.put(str[0].trim(),headValue);
            	   
               }
               Elements rows = table.get(1).getElementsByTag("tr");
               boolean second = true;
               for(org.jsoup.nodes.Element ro : rows){
                     
                     //Skip first row -
                      if(firstRow){
                            firstRow = false;
//                            skip second row in body
                     }else if(second){
                    	 second = false;
                     }else{
                            messageValues = new ArrayList();
                            
                            Elements columns = ro.getElementsByTag("td");
                            
                            //Check message column text
                            String messageText = columns.get(3).text();
//                            System.out.println("**********************************************************");
//                            System.out.println(messageText);
//                            System.out.println("**********************************************************");
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
        
        System.out.println(result);
        return result;
 }
	
	
	public HashMap<Integer,String> getTextContentOfXSLTForIMAndCollab(String directory){
		HashMap<Integer,String> result = new HashMap();
		
		return result;
	}
	
	public HashMap<Integer, HashMap<String,String>> getXMLContentOfXSLTForCollab(String directory){
		HashMap<Integer,HashMap<String,String>> result = new HashMap();
		return result;
	}
	
	
//	 get Actual XML File From  Email
	public static void getEmlFromJournalEmail(String smtpLocation) {
		int index = smtpLocation.lastIndexOf("/");
		String tempXmlDump = smtpLocation.substring(0,index)+"/temp";
		
		try{
			createDirectory(tempXmlDump);
			System.out.println("XML Dump Path : "+tempXmlDump);
			
			File file = new File(smtpLocation);
			for (File emlFile : file.listFiles()) {
						ArrayList<InputStream> attachments = new ArrayList();
						InputStream source;
	
						source = new FileInputStream(emlFile);
						System.out.println(emlFile.getAbsolutePath());

						Properties props = System.getProperties();
						props.put("mail.host", "smtp.dummydomain.com");
						props.put("mail.transport.protocol", "smtp");
	
						Session mailSession = Session.getDefaultInstance(props,null);
						MimeMessage message = new MimeMessage(mailSession, source);
						String subject = message.getSubject();
						
						System.out.println(subject);
						String messages = (String) message.getContent();
	
						System.out.println();
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println();
		}
//		deleteDir(tempXmlDump);
	}	
	
//	 Creating Folder
	public static void createDirectory(String folderPath) throws Exception {
       File dir = new File(folderPath);
       if(!dir.exists()){
       	dir.mkdir();
       }
   }
	
	
//	Delete Directory 
    public static void deleteDir(String folderPath){
    	try{
    		FileUtils.deleteDirectory(new File(folderPath));
    		System.out.println("Delete process is done");
    	}catch(Exception e){
    		System.out.println("Something went wrong");
    	}
    	
    }
}
