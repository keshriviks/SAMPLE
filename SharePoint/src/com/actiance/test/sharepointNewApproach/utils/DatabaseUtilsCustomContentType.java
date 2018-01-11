package com.actiance.test.sharepointNewApproach.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.actiance.test.nimbus.utils1.VerifyAttDefinition;

public class DatabaseUtilsCustomContentType {
	Connection con;
	ResultSet rs;
	Statement stmt;
	ArrayList<String> failed;
	
	private String driverName= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	TestUtilsSubSiteLevelLists utils = new TestUtilsSubSiteLevelLists();
	VerifyFields fields = new VerifyFields();
	
	public void setConnection(String dbUrl, String dbUserName, String dbPassword){
		try{
			Class.forName(driverName);
		    con = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
		    System.out.println("Created connection");
		    con.setAutoCommit(false);	
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while connecting to sql server");
		}
	}
	
	public void createStatement(){
		try{
			stmt = con.createStatement();
			System.out.println("Created a statement");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while creating statement");
		}
	}
	
	public void closeDBConnection(){
		if(stmt != null){
			try {
				stmt.close();
			} catch (SQLException e) {
				System.out.println("Unable to Close the Statement");
				e.printStackTrace();
			}
		}
		if(con != null){
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("Unable to Close the Connection");
				e.printStackTrace();
			}
		}
	}
	
	public HashMap<Integer, ArrayList<String>> getInterIDsMap(ArrayList<Integer> interIDs) throws SQLException, NumberFormatException, XPathExpressionException{
		int interID = -1;
		Long startTime;
		int contentTypeID;
		int contentSubTypeID;
		int resourceID;
		SQLXML attributes;
		int actionID;
		String roomName;
		
		DOMSource domSource;
		Document document;
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		String expression = "//name[.='event.action']/following-sibling::*[1][name()='value']";
		
		HashMap<Integer, ArrayList<String>> interIDsMap = new HashMap<>();
		ArrayList<String> dbResultSetValues = null;
		
		String query = "Select interID, startTime, contentType, contentSubType, resourceID, attributes, roomName from Interactions where networkID = 26 and buddyName = 'achandra@actianceengg.com'";
//		System.out.println(query);
		rs = stmt.executeQuery(query);
		
		if(interIDs.size() != 0){
			while(rs.next()){
				interID = rs.getInt(1);
				startTime = rs.getLong(2);
				contentTypeID = rs.getInt(3);
				contentSubTypeID = rs.getInt(4);
				resourceID = rs.getInt(5);
				roomName = rs.getString(7);
				attributes = rs.getSQLXML(6);
				
				
				domSource = attributes.getSource(DOMSource.class);
		        document = (Document) domSource.getNode();
		        actionID = Integer.parseInt(xpath.evaluate(expression, document));
		        
		        dbResultSetValues = new ArrayList<>();
		        dbResultSetValues.add(interID+"");
		        dbResultSetValues.add(startTime+"");
		        dbResultSetValues.add(contentTypeID+"");
		        dbResultSetValues.add(contentSubTypeID+"");
		        dbResultSetValues.add(resourceID+"");
		        dbResultSetValues.add(actionID+"");
		        dbResultSetValues.add(roomName);
		        
		        //System.out.println(dbResultSetValues);
		        interIDsMap.put(interID, dbResultSetValues);
		        
			}
		        
			//Remove the previous InterIDs
			for(int intID : interIDs){
				interIDsMap.remove(intID);
			}
			
		}else{
			while(rs.next()){
				interID = rs.getInt(1);
				startTime = rs.getLong(2);
				contentTypeID = rs.getInt(3);
				contentSubTypeID = rs.getInt(4);
				resourceID = rs.getInt(5);
				roomName = rs.getString(7);
				attributes = rs.getSQLXML(6);
				
				
				domSource = attributes.getSource(DOMSource.class);
		        document = (Document) domSource.getNode();
		        actionID = Integer.parseInt(xpath.evaluate(expression, document));
		        
		        dbResultSetValues = new ArrayList<>();
		        dbResultSetValues.add(interID+"");
		        dbResultSetValues.add(startTime+"");
		        dbResultSetValues.add(contentTypeID+"");
		        dbResultSetValues.add(contentSubTypeID+"");
		        dbResultSetValues.add(resourceID+"");
		        dbResultSetValues.add(actionID+"");
		        dbResultSetValues.add(roomName);
		        interIDsMap.put(interID, dbResultSetValues);
			}
		  
		}
		return interIDsMap;
	}
	
	public String getActionName(String actionID) throws SQLException {
		String actionName = null;
		String query = "select name from ActionTypes where actionID = "+ actionID;
//		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		actionName = rs.getString("name");
		return actionName;
	}
	
	public String getContentType(String typeID) throws SQLException{
		String contentType;
		String query = "Select value from contentTypes where typeID = "+typeID;
//		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		contentType = rs.getString("value");
		//System.out.println(contentType);
		return contentType;
	}
	
	/*public String getContentSubType(String typeID) throws SQLException{
		String contentSubType;
		String query = "Select value from contentSubTypes where typeID = "+typeID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		contentSubType = rs.getString("value");
		return contentSubType;
	}*/
	
	public ArrayList<String> getResourceNameAndURL(String resourceId) throws SQLException {
		ArrayList<String> values = new ArrayList<>();
		String query = "select resName, resURL from Resources where resourceId = "+resourceId;
//		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		values.add(rs.getString("resName"));
		values.add(rs.getString("resURL"));
		return values;
	}
	
	public boolean verifyDatabase(CSVObjectAddtionalAndCustomContentType object, ArrayList<String> DBValues) throws NumberFormatException, SQLException{
		//Verify Content Type
		System.out.println(object.getContentType());
		System.out.println(getContentType(DBValues.get(2)));
		failed = new ArrayList();
		try{
			
			if(object.getContentType().equals(getContentType(DBValues.get(2)))){
				System.out.println("Content Type Matches with the Database Value");
			}else{
				System.out.println("Content Type did not match with the Database Value");
				failed.add("Content Type");
	//			return false;
			}
			//Verify Content Sub Type
	/*		if(object.getContentSubType() != null && !object.getContentSubType().equals("")){
				if(object.getContentSubType().equals(getContentSubType(DBValues.get(3)))){
					System.out.println("Content Sub Type Matches with the Database Value");
				}else{
					System.out.println("Content Sub Type did not match with the Database Value");
					return false;
				}
			}*/
			//Verify Action Type
			if(object.getActionType().equals(getActionName(DBValues.get(5)))){
				System.out.println("Action Type Matches with the Database Value");
			}else{
				System.out.println("Action Type did not match with the Database Value");
				failed.add("Action Type");
	//			return false;
			}
			//Verify Resource Name and URL
			ArrayList<String> resNameAndURL = getResourceNameAndURL(DBValues.get(4));
	//		System.out.println("In : "+resNameAndURL.get(0));
	//		System.out.println("Out : "+ object.getResourceName());
			if(object.getResourceName().equals(resNameAndURL.get(0))){
				System.out.println("Resource Name Matches with the Database Value");
			}else{
				System.out.println("Resource Name did not match with the Database Value");
				failed.add("Resource Name");
	//			return false;
			}
	//		System.out.println("In : "+resNameAndURL.get(1));
	//		System.out.println("Out : "+ object.getResourceURL());
			if(object.getResourceURL().replace(" ", "%20").equals(resNameAndURL.get(1))){
				System.out.println("Resource URL Matches with the Database Value");
				
			}else{
				System.out.println("Resource URL did not match with the Database Value");
				failed.add("Resource URL");
	//			return false;
			}
			
			if(!verifyMessageTable(Integer.parseInt(DBValues.get(0)), object)){
	//			return false;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		 if(failed.size() == 0){
			
			   return true;
			  }else{
			   return false;
			  }
		
	}
	
	public boolean verifyMessageTable(int interID, CSVObjectAddtionalAndCustomContentType object) throws SQLException{
		String bodyFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID; 
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			failed.add("Result Set was null");
			return false;
		}
		bodyFromDB = rs.getNString(1);
		attributes = rs.getNString(2);
		
			try{
			//Verification for Announcement
			if(object.getType().equalsIgnoreCase("Announcement")){
				System.out.println(bodyFromDB);
				System.out.println(object.getBody());
				if(!fields.isEqual(bodyFromDB,object.getBody())){
					failed.add("Description");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Expires", object.getField1())){
					failed.add("Expire");
	//				return false;
				}
	//			Verify Category
			}else if(object.getType().equalsIgnoreCase("Category")){
				
				if(!fields.isEqual(bodyFromDB,object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Category Picture", object.getField1()+", "+object.getField2())){
					failed.add("URL");
	//				return false;
				}
//				if(!fields.verifyCategoryTypeOfDescription(attributes, object.getField1(),object.getField2())){
//					failed.add("Description");
//					return false;
//				}
				//Verify Comment	
			}else if(object.getType().equalsIgnoreCase("Comment")){
				System.out.println("DB : "+ bodyFromDB);
				String str = object.getBody();
				System.out.println("Input : "+object.getBody());
				if(!fields.isEqual(str, bodyFromDB)){
					System.out.println("Body did not match");
					failed.add("Body");
				}else{
					System.out.println("Body Matched");
				}
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
				}
			//Discussion Board
			}else if(object.getType().equalsIgnoreCase("Discussion Board")){
				if(!bodyFromDB.equals(object.getBody())){
					failed.add("Title");
				}
				if(!fields.verifyTitle(attributes, object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyIsQuestion(attributes, object.getField1())){
					failed.add("IsQuestion");
	//				return false;
				}
				
			//Verify Issue
			}else if(object.getType().equalsIgnoreCase("Issue")){
				
				if(!fields.verifyTitle(attributes, object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyDescriptionAsComments(attributes, object.getDescription())){
					failed.add("Description2");
	//				return false;
				}
				/*if(!fields.verifyDescription2ForIssue(attributes,"Description2")){
					failed.add("Description2");
	//				return false;
				}*/
				if(!fields.verifyAttributeValue(attributes, "Assigned To",  object.getField1())){
					failed.add("Assigned To");
	//				return false;
				}
				if(!fields.verifyIssueStatus(attributes, object.getField2())){
					failed.add("Description");
					return false;
				}
				if(!fields.verifyPriority(attributes, object.getField3())){
					failed.add("Priority");
	//				return false;
				}
				if(!fields.verifyCategory(attributes, object.getField4())){
					failed.add("Category");
	//				return false;
				}
				if(!fields.verifycategoryTitleForIssue(attributes, object.getField5())){
					failed.add("Category Title");
	//				return false;
				}
				if(!fields.verifyCommentAsV3Comments(attributes, object.getField6())){
					failed.add("Comment");
	//				return false;
				}
				if(!fields.verifyTaskDueDate(attributes, object.getField7())){
					failed.add("Dure Date");
	//			return false;
				}
			//Verify Links
			}else if(object.getType().equalsIgnoreCase("Links")){
				System.out.println("DB : "+ bodyFromDB);
				System.out.println("In : "+ object.getField2());
				if(!fields.isEqual(bodyFromDB,object.getField2())){
					System.out.println("Body did not match");
					failed.add("Link");
					
				}else{
					System.out.println("Body matched");
				}
				if(!fields.verifyAttributeValue(attributes, "URL", object.getField1())){
					failed.add("URL");
				}
				if(!fields.verifyAttributeValue(attributes, "Notes", object.getField3())){
					failed.add("Comments");
				}
			//Verify Post	
			}else if(object.getType().equalsIgnoreCase("Post")){
				System.out.println("DB : "+ bodyFromDB);
				String str = object.getBody();
				System.out.println("In : "+ object.getBody());
				if(!fields.isEqual(bodyFromDB,str)){
					System.out.println("Body did not match");
					failed.add("Body");
				}else{
					System.out.println("Body matched");
				}
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
				}
				if(!fields.verifyAttributeValue(attributes, "Published", object.getField1())){
					failed.add("Published Date");
				}
			//Verify Tasks
			}else if(object.getType().equalsIgnoreCase("Tasks")){
				System.out.println("DB : "+ bodyFromDB);
				System.out.println("In : "+ object.getBody());
				if(!fields.isEqual(bodyFromDB,object.getBody())){
					System.out.println("Body did not match");
					failed.add("Body");
				}else{
					System.out.println("Body Matched");
				}
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
				}
				if(!fields.verifyAttributeValue(attributes, "Due Date", object.getField1())){
					failed.add("Due Date");
				}
				if(!fields.verifyAttributeValue(attributes, "Priority", object.getField2())){
					failed.add("Priority");
				}
				if(!fields.verifyAttributeValue(attributes, "Assigned To", object.getField3())){
					failed.add("Assigned To");
				}
				if(!fields.verifyAttributeValue(attributes, "Percentage Completed", object.getField4())){
					failed.add("Complete Percentage");
				}
				if(!fields.verifyAttributeValue(attributes, "Start Date", object.getField15())){
					failed.add("Start Date");
				}
			//Verify Circulation	
			}else if(object.getType().equalsIgnoreCase("Circulation")){
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
				if(!bodyFromDB.contains(object.getBody())){
					failed.add("Body");
	//				return false;
				}
				if(!fields.verifyTitle(attributes, object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(object.getField1() != null){
					if(!fields.verifyAttributeValue(attributes, "Append-Only Comments", object.getField1())){
						failed.add("Append-Only Comments");
	//					return false;
					}
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
				
			//Verify Official Notice	
			}else if(object.getType().equalsIgnoreCase("Circulation")){
				if(!bodyFromDB.contains(object.getBody())){
					failed.add("Body");
	//				return false;
				}
				if(!fields.verifyTitle(attributes, object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
				
			//Verify Community Member, Site Membership and Users
			}else if(object.getType().equalsIgnoreCase("Community Member") || object.getType().equals("Users") || object.getType().equals("Site Membership")){
				if(object.getFileName() != null && !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
	//			Verify Contact
			}else if(object.getType().equalsIgnoreCase("Contact") || object.getType().equals("East Asia Contact")){
				if(!fields.verifyTitle(attributes, object.getField1())){
					failed.add("Last Name ");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "First Name", object.getField2())){
					failed.add("First Name");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Full Name", object.getField3())){
					failed.add("Full Name");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "E-Mail", object.getField4())){
					failed.add("E-Mail");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Company", object.getField5())){
					failed.add("Company");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Job Title", object.getField6())){
					failed.add("Job Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Business Phone", object.getField7())){
					failed.add("Business Phone");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Home Phone", object.getField8())){
					failed.add("Home Phone");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Mobile Number", object.getField9())){
					failed.add("Mobile Number");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Fax Number", object.getField10())){
					failed.add("Fax Number");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Address", object.getField11())){
					failed.add("Address");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "City", object.getField12())){
					failed.add("City");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "State/Province", object.getField13())){
					failed.add("State/Province");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "ZIP/Postal Code", object.getField14())){
					failed.add("ZIP/Postal Code");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Country/Region", object.getField15())){
					failed.add("Country/Region");
	//				return false;
				}
	//			Field 16 Verification is pending
				if(!fields.verifyAttributeValue(attributes, "Comments", object.getField17())){
					failed.add("Comments");
	//				return false;
				}
				if(object.getFileName() != null){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
	//	verify Event
			}else if(object.getType().equalsIgnoreCase("Event")){
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Comments", object.getDescription())){
					failed.add("Comments");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Start Time", object.getField1())){
					failed.add("Start Date");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "End Time", object.getField2())){
					failed.add("End Date");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Category", object.getField3())){
					failed.add("Category");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "All Day Event", object.getField4())){
					failed.add("full All Day Event");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Recurrence", object.getField5())){
					failed.add("Recurrence");
	//				return false;
				}
				if(object.getFileName() != null && object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
	//		Verify	Holiday
			
			}else if(object.getType().equalsIgnoreCase("Holiday")) {
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "V4HolidayDate", object.getField1())){
					failed.add("Holiday Date");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Category", object.getField2())){
					failed.add("Category");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "IsNonWorkingDay", object.getField3())){
					failed.add("IsNonWorkingDay");
	//				return false;
				}
				if(object.getFileName() != null){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
	//			verify Message
			}else if(object.getType().equalsIgnoreCase("Message")) {
				if(!bodyFromDB.equals(object.getBody())){
					failed.add("Description");
	//				return false;
				}
		/*		if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}*/
	//			verify New Word
			}else if(object.getType().equalsIgnoreCase("New Word")) {
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "IMEDisplay", object.getField1())){
					failed.add("Display");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "IMEComment1", object.getField2())){
					failed.add("IMEComment1");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "IMEComment2", object.getField3())){
					failed.add("IMEComment2");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "IMEComment3", object.getField4())){
					failed.add("IMEComment3");
	//				return false;
				}
	//			field 5 and 6  is pending
				if(!fields.verifyAttributeValue(attributes, "IMEPos", object.getField7())){
					failed.add("IMEPos");
	//				return false;
				}
				
				if(object.getFileName() != null){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
	//			verify Resource And Resource Group
			}else if(object.getType().equalsIgnoreCase("Resource") || object.getType().equalsIgnoreCase("Resource Group")) {
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(object.getFileName() != null && !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
	//			verify time Card
			}else if(object.getType().equalsIgnoreCase("Timecard")) {
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Created By", object.getField1())){
					failed.add("Created By");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Date2", object.getField2())){
					failed.add("Date2");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Day", object.getField3())){
					failed.add("Day");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Start", object.getField4())){
					failed.add("Start");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "End", object.getField5())){
					failed.add("End");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Return", object.getField6())){
					failed.add("Return");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Out", object.getField7())){
					failed.add("Out");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Break", object.getField8())){
					failed.add("Break");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Hours Worked", object.getField9())){
					failed.add("Hours Worked");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Late Night", object.getField10())){
					failed.add("Late Night");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Overtime", object.getField11())){
					failed.add("Overtime");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Overtime on Holiday", object.getField12())){
					failed.add("Overtime on Holiday");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Arrive Late", object.getField13())){
					failed.add("Arrive Late");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Leaving Early", object.getField14())){
					failed.add("Leaving Early");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Out of Office(Private)", object.getField15())){
					failed.add("Out of Office(Private)");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Vacation Type", object.getField16())){
					failed.add("Vacation Type");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Vacation Length", object.getField17())){
					failed.add("Vacation Length");
	//				return false;
				}
			
	//			verify Schedule ,(Schedule and Reservations) And Reservations
			}else if(object.getType().equalsIgnoreCase("Schedule") || object.getType().equalsIgnoreCase("Schedule and Reservations")) {
							
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				System.out.println(object.getDescription());
				if(!fields.verifyAttributeValue(attributes, "Comments", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Location", object.getField1())){
					failed.add("Location");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Start Time", object.getField2())){
					failed.add("Start Time");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "End Time", object.getField3())){
					failed.add("End Time");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Category", object.getField4())){
					failed.add("Category");
	//				return false;
				}
				
				if(object.getType().equalsIgnoreCase("Schedule and Reservations"))
				{
					if(!fields.verifyAttributeValue(attributes, "All Day Event", object.getField5())){
						failed.add("All Day Event");
		//				return false;
					}
					else
						if(!fields.verifyAttributeValue(attributes, "Is Current Version", object.getField5())){
							failed.add("Is Current Version");
			//				return false;
						
						}	
				}
	//			Verify Summary Task
			}else if(object.getType().equalsIgnoreCase("Summary Task")){
				
				if(!fields.verifyTitle(attributes, object.getName())){
					failed.add("Name");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Body", object.getBody())){
					failed.add("Body");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Start Time", object.getField1())){
					failed.add("Start Date");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Predecessors", object.getField2())){
					failed.add("Predecessors");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Task Status", object.getField3())){
					failed.add("Task Status");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Priority", object.getField4())){
					failed.add("Priority");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Due Date2", object.getField5())){
					failed.add("Due Date2");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "% Complete", object.getField6())){
					failed.add("% Complete");
	//				return false;
				}
				
	//			Verify Fixed Value based Status Indicator
			}else if(object.getType().equalsIgnoreCase("Fixed Value based Status Indicator")){
				
				if(!fields.verifyTitle(attributes, object.getName())){
					failed.add("Name");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Indicator Comments", object.getField1())){
					failed.add("Indicator Comments");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Indicator Goal Threshold", object.getField2())){
					failed.add("Indicator Goal Threshold");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Indicator Warning Threshold", object.getField3())){
					failed.add("Indicator Warning Threshold");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Indicator Value", object.getField4())){
					failed.add("Indicator Value");
	//				return false;
				}
			}else if(object.getType().equalsIgnoreCase("Document Set")){
				
				if(!fields.verifyTitle(attributes, object.getName())){
					failed.add("Name");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				
			}else if(object.getType().equalsIgnoreCase("Control Display Template") || object.getType().equalsIgnoreCase("Document") || object.getType().equalsIgnoreCase("Web Part Page") || object.getType().equalsIgnoreCase("Form") || object.getType().equalsIgnoreCase("Basic Page") || object.getType().equalsIgnoreCase("Group Display Template")){
				if(object.getName() != null || object.getName().equals("")){
					if(!fields.verifyAttributeValue(attributes, "Title", object.getName())){
						failed.add("Name");
	//					return false;
					}
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Report")){
				if(object.getTitle() != null || object.getTitle().equals("")){
					if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
						failed.add("Title");
	//					return false;
					}
				}
				if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Category", object.getField1())){
					failed.add("Category");
	//				return false;
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Rich Media Asset")){
	//			bodyFromDB = getTextFromDB(stmt, interID).get(0);
	//			attributes = getTextFromDB(stmt, interID).get(1);
				if(object.getTitle() != null || object.getTitle().equals("")){
					if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
						failed.add("Title");
	//					return false;
					}
				}
				if(!fields.verifyAttributeValue(attributes, "Keywords", object.getField1())){
					failed.add("Keywords");
	//				return false;
				}
				if(object.getFileName() != null && !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("file");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Picture")){
				
				if(!fields.verifyTitle(attributes, object.getTitle())){
					failed.add("Name");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Date Of Picture Taken", object.getField1())){
					failed.add("Date Of Picture Taken");
	//				return false;
				}
				
				
				if(!fields.verifyAttributeValue(attributes, "Copyright", object.getField2())){
					failed.add("Copyright");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Keywords", object.getField3())){
					failed.add("Keywords");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Description", object.getField4())){
					failed.add("Comments");
	//				return false;
				}
				if(object.getFileName() != null && !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("file");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Audio")){
				
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
						
				if(!fields.verifyTitle(attributes, object.getTitle())){
					failed.add("Name");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Copyright", object.getField1())){
					failed.add("Copyright");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Author", object.getField2())){
					failed.add("Author");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Keywords", object.getField3())){
					failed.add("Keywords");
	//				return false;
				}
				if(object.getFileName() != null && !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("file");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Image")){
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
				
				if(!fields.verifyTitle(attributes, object.getTitle())){
					failed.add("Name");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Keywords", object.getField1())){
					failed.add("Keywords");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Author", object.getField2())){
					failed.add("Author");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Copyright", object.getField3())){
					failed.add("Copyright");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Date Of Picture Taken", object.getField4())){
					failed.add("Date Of Picture Taken");
	//				return false;
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Link to a Document")){
	//			bodyFromDB = getTextFromDB(stmt, interID).get(0);
	//			attributes = getTextFromDB(stmt, interID).get(1);
				if(!bodyFromDB.contains(object.getField1())){
					failed.add("URL");
	//				return false;
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Enterprise Wiki Page") || object.getType().equalsIgnoreCase("Redirect Page") || object.getType().equalsIgnoreCase("Project Page")){
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Comments", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("file");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Master Page Preview")){
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("List View Style")){
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Required Fields", object.getField1())){
					failed.add("XSLStyleRequiredFields");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Category", object.getField2())){
					failed.add("XSLStyleCategory");
	//				return false;
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("file");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("JavaScript Display Template")){
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Target Scope", object.getField1())){
					failed.add("DisplayTemplateJSTargetScope");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Comments", object.getField2())){
					failed.add("Comments");
	//				return false;
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("file");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Master Page") || object.getType().equalsIgnoreCase("Catalog-Item Reuse") || object.getType().equalsIgnoreCase("Error Page")){
			
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
	//				return false;
				
				}
				if(!fields.verifyAttributeValue(attributes, "Comments", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				if(object.getField1() != null || object.getField1().equals("")){
					if(!fields.verifyAttributeValue(attributes, "Default CSS File", object.getField1())){
						failed.add("DefaultCssFile");
	//					return false;
					}
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("file");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Article Page") || object.getType().equalsIgnoreCase("Welcome Page") || object.getType().equalsIgnoreCase("Page")){
			
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!bodyFromDB.equals(object.getDescription())){
					failed.add("Description");
	//				return false;
				
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("file");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Filter Display Template")){
			
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Comments", object.getField1())){
					failed.add("Comments");
	//				return false;
				}
				if(object.getFileName() != null || !object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("file");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Excel based Status Indicator") || object.getType().equalsIgnoreCase("Custom Excel based Status Indicator")){
				
				if(!fields.verifyTitle(attributes, object.getName())){
					failed.add("Name");
	//				return false;
				}
				if(object.getType().equalsIgnoreCase("Custom Excel based Status Indicator")){
					if(!fields.verifyAttributeValue(attributes, "Description3", object.getDescription())){
						failed.add("Description");
					}
				}else{
					if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
						failed.add("Description");
					}
				}

				if(!fields.verifyAttributeValue(attributes, "Indicator Comments", object.getField1())){
					failed.add("Indicator Comments");
	//				return false;
				}
				
				if(!fields.verifyAttributeValueWithContainMethod(attributes, "Data Source", object.getField2())){
					failed.add("Data Source");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Goal Cell", object.getField3())){
					failed.add("Goal Cell");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Value Cell", object.getField4())){
					failed.add("Value Cell");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Warning Cell", object.getField5())){
					failed.add("Warning Cell");
	//				return false;
				}
			}else if(object.getType().equalsIgnoreCase("What's New Notification")){
			
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Assigned To", object.getField1())){
					failed.add("Assigned To");
	//				return false;
				}
			}else if(object.getType().equalsIgnoreCase("Item Display Templete")){
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
				if(!bodyFromDB.equals(object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Comments", object.getDescription())){
					failed.add("Comments");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Comments", object.getField1())){
					failed.add("Comments");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Target Control Type (Search)", object.getField2())){
					failed.add("Target Control Type (Search)");
	//				return false;
				}
				if(object.getFileName() != null && object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Work Flow Task")){
				
				if(!fields.verifyAttributeValue(attributes, "Title", object.getName())){
					failed.add("Name");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Start Date", object.getField1())){
					failed.add("Start Date");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Assigned To", object.getField2())){
					failed.add("Assigned To");
	//				return false;
				}
			}else if(object.getType().equalsIgnoreCase("Video Rendition")){
				
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
					failed.add("Description");
	//				return false;
				}
				
				if(!fields.verifyAttributeValue(attributes, "Keywords", object.getField1())){
					failed.add("Keywords");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Video Rendition Label", object.getField2())){
					failed.add("Video Rendition Label");
	//				return false;
				}
				if(!fields.verifyAttributeValue(attributes, "Copyright", object.getField3())){
					failed.add("Copyright");
	//				return false;
				}
				if(object.getFileName() != null && object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
				
			}else if(object.getType().equalsIgnoreCase("Wiki Page")){
				bodyFromDB = getTextFromDB(stmt, interID).get(0);
				attributes = getTextFromDB(stmt, interID).get(1);
				
				if(!bodyFromDB.equals(object.getDescription())){
					failed.add("Description");
				}
				if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
					failed.add("Title");
	//				return false;
				}
				if(object.getFileName() != null && object.getFileName().equals("")){
					if(!verifyFile(stmt, interID, object.getFileName())){
						failed.add("File");
	//					return false;
					}
				}
			}else if(object.getType().equalsIgnoreCase("Folder")){
				
				if(!fields.verifyAttributeValue(attributes, "URL Path", object.getField1())){
					failed.add("FileRef");
	//				return false;
				}
			}
			
			}
			
				catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean verifyTextAndAttributeForPages(int interID, String text, String title, String file, String category) throws SQLException{
		String bodyFromDB;
		String attributes;
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		while(rs.next()){
			bodyFromDB = rs.getString(1);
			attributes = rs.getString(2);
			if(bodyFromDB != null){
				if(!fields.verifyTitle(attributes, title)){
					return false;
				}
				if(!bodyFromDB.equals(text)){
					return false;
				}
				if(!fields.verifyFileName(attributes, file)){
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean verifyFile(String time, String file, String fileType) throws SQLException{
		int interID;
		int contentTypeID;
		String fileNameFromDB;
		String contentType;
		String query = "Select interID, contentType from Interactions where networkID = 26 and startTime = "+time;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		
		interID = rs.getInt(1);
		contentTypeID = rs.getInt(2);
		contentType = getContentType(contentTypeID+"");
		query = "Select attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		String attributes = rs.getString(1);
		fileNameFromDB = utils.extractString(attributes, "FileName");
		if(file.equals(fileNameFromDB)){
			System.out.println("File Name Mathes with the Database Value");
		}else{
			System.out.println("File Name did not match with the Database Value");
			return false;
		}
		if(fileType.equals(contentType)){
			System.out.println("File Type Matches with the Database Value");
		}else{
			System.out.println("File Type did not match with the Database Value");
			return false;
		}
		return true;
	}
	
	public boolean verifyParentPostForBlogComments(String time, String postTitle, String postPublished) throws SQLException{
		String attributes;
		String query = "Select attributes from Messages where interID = (Select interID from interactions where startTime = "+time+")";
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		attributes = rs.getString(1);
		if(!fields.verifyTitle(attributes, postTitle)){
			return false;
		}
		if(!fields.verifyPublishedDate(attributes, postPublished)){
			return false;
		}
		return true;
	}
	
	public boolean verifyParentDiscussion(String time, String subject, String body, String isQuestion) throws SQLException{
		String text;
		String attributes;
		String query = "Select text, attributes from Messages where interID = (Select interID from interactions where startTime = "+time+")";
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		text = rs.getString(1);
		attributes = rs.getString(2);
		
		if(!fields.verifyText(body, text)){
			return false;
		}
		if(!fields.verifySubjectAsTitle(attributes, subject)){
			return false;
		}
		if(!fields.verifyIsQuestion(attributes, isQuestion)){
			return false;
		}
		return true;
	}
	
	public boolean verifyParentPost(String time, String title) throws SQLException{
		String attributes;
		String query = "Select attributes from Messages where interID = (Select interID from interactions where startTime = "+time+")";
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		attributes = rs.getString(1);
		if(!fields.verifyTitleAsOriginalContent(attributes, title)){
			return false;
		}
		return true;
	}
	
	public boolean verifyFile(Statement stmt, int interId, String fileName){
		int count = 0;
		String query = "select count(*) from filexfers where interId = "+ interId + " and fileName ='"+ fileName + "'";
		try{
			System.out.println("File Query : "+query);
			rs = stmt.executeQuery(query);
			rs.next();
			count = rs.getInt(1);
		}catch(SQLException se){
			System.out.println("Error to fetch File Name for Interaction : "+interId);
		}
		if(count > 0){
			System.out.println("File Name is Matched");
			return true;
		}else{
			System.out.println("File Name did not Matched");
			return false;
		}
	}
	

	public ArrayList<String> getTextFromDB(Statement stmt, int interId){
		ArrayList<String> textAndAttr = new ArrayList();
		String query = "select text , attributes from messages where interId="+ interId + " and text IS NOT NULL";
		
		try{
			System.out.println(query);
			rs = stmt.executeQuery(query);
			if(rs != null){
				rs.next();
				textAndAttr.add(rs.getString("text"));
				textAndAttr.add(rs.getString("attributes"));
			}else{
				System.out.println("Result Set is null");
			}
		}catch(SQLException se){
			System.out.println("No Data Found");
		}
		return textAndAttr;
	}
	
	public ArrayList<String> getFailedColumns(){
		  return failed;
	}
	
}
