/*
 * Test class to test the Collab xml for Chatter, Jive, Yammer (Basically Actiance format XMLs)
 * Author : Manish
 * 
 * */

package com.actiance.test.importer.collab.tests;


import java.io.UnsupportedEncodingException;
import java.sql.Statement;
import java.util.ArrayList;

//import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import com.actiance.test.importer.collab.utils.Attributes;
import com.actiance.test.importer.collab.utils.ContentEvent;
import com.actiance.test.importer.collab.utils.ContentEvents;
import com.actiance.test.importer.collab.utils.DatabaseUtils;
import com.actiance.test.importer.collab.utils.EmployeeInfo;
import com.actiance.test.importer.collab.utils.Files;
import com.actiance.test.importer.collab.utils.Participants;
import com.actiance.test.importer.collab.utils.TestUtils;

public class CollabImportExportVerification {


	DatabaseUtils dbutils= new DatabaseUtils();
	TestUtils testutil=new TestUtils();
	
	
	public boolean verifyConentEvent(ContentEvents contentEvents, Statement stmt) throws UnsupportedEncodingException{
		boolean resultFinal = false;
		for(int i=0; i<contentEvents.getContentEvent().size();i++){
			boolean[] bRet ={false,false,false,false,false,false,false,false,false,false,false};
//			Get the UserId or BuddyName
			String userId=contentEvents.getContentEvent().get(i).getUserID();
//			Get the Event Time
			long eventTime=contentEvents.getContentEvent().get(i).getEventTime();
//			Get the ContentSubType
			String contentSubType=contentEvents.getContentEvent().get(i).getContentSubType();
			
			String objectId = contentEvents.getContentEvent().get(i).getObjectID();
//			Get the Interaction ID.
			int interId=dbutils.getInteractionByUserIdandEventTimeAndObjectID(stmt, userId, eventTime, contentSubType,objectId);
			
//			Verifying the XML Field With DB.....
			
//			Verify ObjectID or EventId
			if(dbutils.verifyObjectID(stmt, interId, contentEvents.getContentEvent().get(i).getObjectID())){
				System.out.println("Object Id Matched for Interaction : "+interId);
				bRet[0] = true;
			}
//			Verify Employee info If XML contain It.
			if(verifyEmployee( stmt,interId , contentEvents.getContentEvent().get(i).getEmployeeInfo()))
				bRet[1] = true;
//			Verify Files If XMl contain It.
			if(verifyFiles(stmt, interId,contentEvents.getContentEvent().get(i).getFiles()))
				bRet[2]= true;
//			Verify Resource Name and ResourceId
			if(dbutils.verifyResource(stmt, interId, eventTime, contentEvents.getContentEvent().get(i).getResourceName(),contentEvents.getContentEvent().get(i).getResourceID())){
				bRet[3]= true;
				System.out.println("Resource Name and Id is matched for Interaction : "+interId);
			}
//			Verify Content Text or Actual Message(Supports Normal, Multibytes, Special Char)
			if(verifyContenText(stmt, interId, contentEvents.getContentEvent().get(i))){
				bRet[4]= true;
			}
//			verify Content Type
			if(dbutils.verifyContentType(stmt, interId, contentEvents.getContentEvent().get(i).getContentType())){
				System.out.println("Content type is Matched with DB for Interaction :"+interId);
				bRet[5] = true;
			}
//			verify CorrelationId if XML contain It.
			if(verifyCorrelation(stmt, interId, contentEvents.getContentEvent().get(i).getCorrelationID())){
				bRet[6] =true;
			}
//			Verify Action Type
			if(dbutils.verifyActionType(stmt, interId, contentEvents.getContentEvent().get(i).getAction())){
				System.out.println("Action type is matched with DB for Interaction : "+interId);
				bRet[7] = true;
			}
//			Verify Object URL
			if(dbutils.verifyObjectURI(stmt, interId, contentEvents.getContentEvent().get(i).getObjectURI())){
				System.out.println("Object URI is matched with DB for Interaction : "+ interId);
				bRet[8]= true;
			}
//			Verify Participant Info If XML contain It.
			if(verifyParticipant(stmt, interId, contentEvents.getContentEvent().get(i).getParticipants())){
				bRet[9]=true;
			}
//			Verify Attributes If XML contain It
			if(verifyAttributes(stmt, interId, contentEvents.getContentEvent().get(i).getAttributes())){
				bRet[10] = true;
			}
			
			for(int k = 0; k<bRet.length;k++){
				if(bRet[k])
					resultFinal = true;
				else{
					resultFinal = false;
					break;
				}
					
			}
//			resultFinal= false;
//			System.out.println(resultFinal);
				
		}
		
		 System.out.println("final Result : "+ resultFinal );
		  
		  return resultFinal;
	}

//	Checking the existence of Files in XML , if it is there than only verfying file one by one
	public boolean verifyFiles(Statement stmt, int interId, Files files ) {
		boolean flag = false;
		if (files == null) 
			flag = true;
		else if(files.getFile().size() == 0)
			flag = true;
		else {
			ArrayList<String> filesFromDB = dbutils.getFileNamesFromDB(stmt, interId);
			for (int i = 0; i < files.getFile().size(); i++) {
				flag = filesFromDB.contains(files.getFile().get(i).getFileName());
//				flag = dbutils.verifyFile(stmt, interId, files.getFile().get(i));
				if (flag)
					System.out.println("File : "+ files.getFile().get(i).getFileName()	+ " : Matched with DB for Interaction : " + interId);
				else {
					flag = false;
					break;
				}
			}

		}
		return flag;
	}
	
//	Checking the existence of employee in XML.. If it is there than only verifying...
	
	public boolean verifyEmployee(Statement stmt, int interId, EmployeeInfo emp) {
		boolean flag = false;
		if (emp == null) 
			return flag = true;
		
		else{
			flag = dbutils.verifyEmployeeInfo(stmt, interId, emp);
			if(flag){
				System.out.println("Employee Information is Matched for Interaction : "+interId);
				System.out.println("Employee Name : "+emp.getEmployeeID()+" is matched with DB");
			}
		}
		return flag;
	}
	
//	Checking the existence of Correlation ID in XML, If it is there than only verifying ...
	
	public boolean verifyCorrelation(Statement stmt, int interId, String CorrelationId) {
		boolean flag = false;
		if (CorrelationId != null) {
			flag = dbutils.verifyCorrelationId(stmt, interId, CorrelationId);
			if(flag)
				System.out.println("Correlation is mathced with Db for Interaction : "+interId);
		} else
			flag = true;
		return flag;
	}
	
//	Checking the existence of Attribute in XML,If it is there than only verifying....
	public boolean verifyAttributes(Statement stmt, int interId, Attributes attributes) {
		boolean flag = false;
		if (attributes == null || attributes.getAttribute().size()==0)
			flag = true;
//		else if(attributes.getAttribute().size()== 0)
//			flag = true;
		else {
			for (int i = 0; i < attributes.getAttribute().size(); i++) {
				flag = dbutils.verifyAttribute(stmt, interId, attributes.getAttribute().get(i));
				if (flag){
					System.out.println("Attribute is matched with DB for Interaction : "+ interId);
					System.out.println("Attribute Name : "+attributes.getAttribute().get(i).getName()+" and Value : "+attributes.getAttribute().get(i).getValue()+" is matched");
				}
				else {
					flag = false;
					break;
				}
			}
		}
		return flag;

	}
	
//	Checking the existence of Participant in XML,If it is there than only verifying....
	public boolean verifyParticipant(Statement stmt, int interId,
			Participants participants) {
		boolean flag = false;
		if (participants == null)
			flag = true;
		else if(participants.getParticipant().size() == 0)
			flag = true;
		else {
			for (int i = 0; i < participants.getParticipant().size(); i++) {
				flag = dbutils.verifyParticipant(stmt, interId, participants.getParticipant().get(i));
				if (flag){
					System.out.println("Participant is macthed with DB for Interaction : "+ interId);
					System.out.println("Participant Name :"+ participants.getParticipant().get(i).getEmployeeInfo().getEmployeeID()+ " is matched with DB");
				}
				else {
					flag = false;
					break;
				}
			}
		}
		return flag;
	}

//	Converting the ContentText into bytes of Array...
	public byte[] getContentTextBytesFromInput(String content) throws UnsupportedEncodingException{
		byte[] b=content.getBytes("UTF-8");
//		for(byte ba : b)
//		System.out.println(ba);
		return b;
	}

//	Checking the existence of ContentText in XML,If it is there than only verifying....
	public boolean verifyContenText(Statement stmt, int interId, ContentEvent contents) throws UnsupportedEncodingException{
		boolean flag = false;
		byte[] bytesFromInput;
		if(contents.getContentText() == null || contents.getContentText().getValue().equals("")|| contents.getContentText().getValue() == null)
			flag = true;
		else if(contents.getContentText().getMimeType().equalsIgnoreCase("html")){
			String local = testutil.removeTags(contents.getContentText().getValue().replaceAll("\\s+", ""));
			bytesFromInput = getContentTextBytesFromInput(local.trim());
			flag = dbutils.verifyContentText(stmt, interId, bytesFromInput);
			if(flag)
				System.out.println("Content Text is Matched for InteractionId : "+interId);
			else
				System.out.println("Content text does not matched with Db");
		}
		else{
			String local = contents.getContentText().getValue().replaceAll("\\s+","");
			bytesFromInput=getContentTextBytesFromInput(local);
			flag = dbutils.verifyContentText(stmt, interId, bytesFromInput);
			if(flag)
				System.out.println("Content Text is Matched for InteractionId : "+interId);
			else
				System.out.println("Content text does not matched with Db");
				
		}
		return flag;
		
	}
}
