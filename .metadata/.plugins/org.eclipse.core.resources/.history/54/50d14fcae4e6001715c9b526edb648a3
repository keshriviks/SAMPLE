package com.actiance.test.sharepointNewApproachCustomField.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class DatabaseUtils {
	Connection con;
	ResultSet rs;
	Statement stmt;
	
	private String driverName= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	TestUtils utils = new TestUtils();
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
//		System.out.println("DB "+contentType);
		return contentType;
	}
	
	public String getContentSubType(String typeID) throws SQLException{
		String contentSubType;
		String query = "Select value from contentSubTypes where typeID = "+typeID;
//		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		contentSubType = rs.getString("value");
//		System.out.println(contentSubType);
		return contentSubType;
	}
	
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
	
	public boolean verifyCustomFields(String customField1, String customField2, String attributes){ 
		boolean flag1 = false;
		boolean flag2 = false;
//		System.out.println(customField1);
//		System.out.println(attributes);
		System.out.println("Field One : ");
		System.out.println(utils.extractString(attributes, customField1.split("#")[0]));
		System.out.println("In: "+customField1.split("#")[1]+", Out: "+utils.extractString(attributes, customField1.split("#")[0]));
		if(customField1.split("#")[1].equalsIgnoreCase((utils.extractString(attributes, customField1.split("#")[0]).replace(",", "")))){
			System.out.println("Custom Field1 value matches with the Database value");
			flag1 = true;
		}else{
			System.out.println("Custom Field1 value did not match with the Database value");
			
		}
		if(customField2 != null && !customField2.equals("")){
			System.out.println("Field Two : ");
			System.out.println("In: "+ (customField2.split("#")[1]).replace("\n", " "));
			System.out.println("Out: "+(utils.extractString(attributes, customField2.split("#")[0]).replace("\n", " ")));
			if(customField2.split("#")[1].equalsIgnoreCase((utils.extractString(attributes, customField2.split("#")[0]).replace("\n", " ").replace(",", "")))){
				System.out.println("Custom Field2 value matches with the Database value");
				flag2 = true;
			}else{
				System.out.println("Custom Field2 value did not match with the Database value");
				
			}
		}else{
			flag2 = true;
		}
		
		return (flag1 && flag2);
	}
	
	public boolean verifyMessageTableForAnnouncements(int interID, String title, String body, String expires, 
			String customField1, String customField2, boolean isFileType) throws SQLException{
		byte[] input = body.getBytes();
		byte[] output;
		String titleFromDB;
		String expiresFromDB;
		String textFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID ;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		
		textFromDB = rs.getString(1);
		attributes = rs.getString(2);
		
		titleFromDB = utils.extractString(attributes, "Title");
		//System.out.println("Inpurt: "+title);
		//System.out.println("out: "+titleFromDB);
		
		if(Arrays.equals(title.replace("&", "&amp;").getBytes(), titleFromDB.getBytes())){
			System.out.println("Title Matches with the Database value");
		}else{
			System.out.println("Title did not match with the Database value");
			return false;
		}
		
		if(expires != null && !expires.equals("")){
			expiresFromDB = utils.extractString(attributes, "Expires");
			if(expires.equals(expiresFromDB)){
				System.out.println("Expires value Matches with the Database value");
			}else{
				System.out.println("Expires did not match with the Database value");
				return false;
			}
		}
		
		if(!verifyCustomFields(customField1, customField2, attributes)){
			return false;
		}
		
		return true;
	}
	
	public boolean verifyFile(String time1, String file, String fileType) throws SQLException{
		int interID;
		int contentTypeID;
		String fileNameFromDB;
		String contentType;
		String query = "Select interID, contentType from Interactions where networkID = 26 and startTime = "+time1;
//		System.out.println(query);
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
	
	public boolean verifyParentInteraction(String time, String subject, String body) throws SQLException{
		String textFromDB;
		String subjectFromDB;
		String attributes;
		String query = "Select text, attributes from Messages where interID = (Select interID from interactions where startTime = "+time+")";
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		textFromDB = rs.getString(1);
		attributes = rs.getString(2);
		subjectFromDB = utils.extractString(attributes, "Title");
		//Verify Body
		if(body != null && !body.equals("")){
			if(textFromDB.equals(body)){
				System.out.println("Body of Parent Interaction matches with the Database Value");
			}else{
				System.out.println("Body of Parent Interaction did not match with the Database Value");
				return false;
			}
		}
		//Verify Subject
		if(subject.equals(subjectFromDB)){
			System.out.println("Subject of the Parent Interaction Matches with the Database Value");
		}else{
			System.out.println("Subject of the Parent Interaction did not match with the Database Value");
			return false;
		}
		return true;
	}
	
	public boolean verifyMessageTableForDiscussionBoard(int interID, String subject, String body, String question, String reply,
			String time1, String time2, String file, String fileType, boolean isReply, boolean isFileType,
			String customField1, String customField2) throws SQLException{
		byte[] input = body.getBytes();
		String subjectFromDB;
		String questionFromDB;
		String textFromDB;
		String attributes;
		Boolean isQuestion = false;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
//		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		
		textFromDB = rs.getString(1);
		attributes = rs.getString(2);
		
		subjectFromDB = utils.extractString(attributes, "Title");
		subjectFromDB = subjectFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		//System.out.println("Input Subject: "+subject+" Output Subject: "+subjectFromDB);
		if(Arrays.equals(subject.getBytes(), subjectFromDB.getBytes())){
			System.out.println("Subject Matches with the Database value");
		}else{
			System.out.println("Subject did not match with the Database value");
			return false;
		}
		
		if(body != null && !body.trim().equals("")){
			System.out.println("Input: "+body+", Output: "+textFromDB);
			if(Arrays.equals(input, textFromDB.getBytes())){
				System.out.println("Body Matches with the Database value");
			}else{
				System.out.println("Body did not match with the Database value");
				return false;
			}
		}
		
		if(question != null && !question.equals("")){
			questionFromDB = utils.extractString(attributes, "Question");
			if(question.equalsIgnoreCase("Yes")){
				isQuestion = true;
			}
			//System.out.println("Input question: "+question+", Output question: "+questionFromDB);
			//System.out.println("Parsing: "+Boolean.parseBoolean(question)+" "+Boolean.parseBoolean(questionFromDB));
			if(isQuestion == Boolean.parseBoolean(questionFromDB)){
				System.out.println("Question value Matches with the Database value");
			}else{
				System.out.println("Question did not match with the Database value");
				return false;
			}
		}
		
		if(!verifyCustomFields(customField1, customField2, attributes)){
			return false;
		}
		
		return true;
	}
	
	public boolean verifyMessageTableForIssueTracking(int interID, String title, String description, String assignedTo, String issueStatus, String priority,
			 String category, String relatedIssue, String comment, String dueDate, String file) throws SQLException{
		String titleFromDB;
		String descriptionFromDB;
		String commentFromDB;
		String attributes;
		
		String query = "Select attributes from Messages where interID = "+interID;
//		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		attributes = rs.getString(1);
		
		//Verify Title
		titleFromDB = utils.extractString(attributes, "Title");
		titleFromDB = titleFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		if(title.equals(titleFromDB)){
			System.out.println("Title Matches with the Database Value");
		}else{
			System.out.println("Title did not match with the Database Value");
			return false;
		}
		//Verify AssignedTo
		if(assignedTo != null && !assignedTo.equals("")){
			if(assignedTo.equals(utils.extractString(attributes, "AssignedTo"))){
				System.out.println("AssignedTo Matches with the Database Value");
			}else{
				System.out.println("AssignedTo did not match with the Database Value");
				return false;
			}
		}
		//Verify Issue Status
		if(issueStatus.equals(utils.extractString(attributes, "Status"))){
			System.out.println("Issue Status Matches with the Database Value");
		}else{
			System.out.println("Issue Status did not match with the Database Value");
			return false;
		}
		//Verify Priority
		if(priority.equals(utils.extractString(attributes, "Priority"))){
			System.out.println("Priority Matches with the Database Value");
		}else{
			System.out.println("Priority did not match with the Database Value");
			return false;
		}
		//Verify Description
		//DB does not store colon value in the attributes
		if(description != null && !description.equals("")){
			descriptionFromDB = utils.extractString(attributes, "Comment");
			descriptionFromDB = descriptionFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			System.out.println("In Des: "+description+", out Des: "+descriptionFromDB);
			if((description.replace(":", "")).equals(descriptionFromDB)){
				System.out.println("Description Matches with the Database Value");
			}else{
				System.out.println("Description did not match with the Database Value");
				return false;
			}
		}
		//Verify Category
		if (category != null && !category.equals("")) {
			if (category.equals(utils.extractString(attributes, "Category"))) {
				System.out.println("Category Matches with the Database Value");
			} else {
				System.out.println("Category did not match with the Database Value");
				return false;
			}
		}
		//Verify Related Issues
	//	if (relatedIssue != null && !relatedIssue.equals("")) {
	//		if (relatedIssue.equals(utils.extractString(attributes, "Category"))) {
	//			System.out.println("Category Matches with the Database Value");
	//		} else {
	//			System.out
	//					.println("Category did not match with the Database Value");
	//			return false;
	//		}
	//	}
		//Verify Comments
		//Database ignores Colon in the comment attribute
		if (comment != null && !comment.equals("")) {
			commentFromDB = utils.extractString(attributes, "V3Comments");
			commentFromDB = commentFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			if ((comment.replace(":", "")).equals(commentFromDB)) {
				System.out.println("Comment Matches with the Database Value");
			} else {
				System.out.println("Comment did not match with the Database Value");
				return false;
			}
		}
		//Verify Due Date
		if (dueDate != null && !dueDate.equals("")) {
			if (dueDate.equals(utils.extractString(attributes, "DueDate"))) {
				System.out.println("Due Date Matches with the Database Value");
			} else {
				System.out.println("Due Date did not match with the Database Value");
				return false;
			}
		}
		//Verify File
		
		return true;
	}
	
	public boolean verifyMessageTableForLinks(int interID, String description, String URL, String notes, 
			String customField1, String customField2) throws SQLException{
		String descriptionFromDB;
		String notesFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		descriptionFromDB = rs.getString(1);
		attributes = rs.getString(2);
		
		//Verify Description
		//DB does not store less than(<) and greater than(>) value in the column
		//descriptionFromDB = descriptionFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		System.out.println("In Des: "+description+", out Des: "+descriptionFromDB);
		if((description.replace("<", "").replace(">", "")).equals(descriptionFromDB)){
			System.out.println("Description Matches with the Database Value");
		}else{
			System.out.println("Description did not match with the Database Value");
			return false;
		}
		//Verify Notes
		if(notes != null && !notes.equals("")){
			notesFromDB = utils.extractString(attributes, "Notes");
			notesFromDB = notesFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			
			System.out.println("In Des: "+description+", out Des: "+descriptionFromDB);
			if((notes).equals(notesFromDB)){
				System.out.println("Notes Matches with the Database Value");
			}else{
				System.out.println("Notes did not match with the Database Value");
				return false;
			}
		}
		//Verify URL		
		if(URL.equals(utils.extractString(attributes, "URL"))){
//			System.out.println(utils.extractString(attributes, "URL"));
			System.out.println("URL Matches with the Database Value");
		}else{
			System.out.println("URL did not match with the Database Value");
			return false;
		}
		if(!verifyCustomFields(customField1, customField2, attributes)){
//			System.out.println("*******************");
//			System.out.println(customField1);
//			System.out.println(customField2);
//			System.out.println("******************");
			return false;
		}
		return true;
	}
	
	public boolean verifyMessageTableForSurvey(int interID, String description, String name, String question, String choice1, String choice2,
			String choice3, String defaultValue, String responce, String file, String fileType, boolean isQuestion, boolean isResponse) throws SQLException{
		String descriptionFromDB;
		String nameFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		descriptionFromDB = rs.getString(1);
		attributes = rs.getString(2);
		
		//Verify Name
		nameFromDB = utils.extractString(attributes, "Title");
		nameFromDB = nameFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		if(name.equals(nameFromDB)){
			System.out.println("Name Matches with the Database Value");
		}else{
			System.out.println("Name did not match with the Database Value");
			return false;
		}
			
		if(isQuestion){
			if(descriptionFromDB.contains(question)){
				System.out.println("Description Contains Question");
			}else{
				System.out.println("Description did not contain Question");
				return false;
			}
			if(choice1 != null && !choice1.equals("")){
				if(descriptionFromDB.contains(choice1)){
					System.out.println("Description Contains Choice1");
				}else{
					System.out.println("Description did not contain Choice1");
					return false;
				}
			}
			if(choice2 != null && !choice2.equals("")){
				if(descriptionFromDB.contains(choice2)){
					System.out.println("Description Contains Choice2");
				}else{
					System.out.println("Description did not contain Choice2");
					return false;
				}
			}
			if(choice3 != null && !choice3.equals("")){
				if(descriptionFromDB.contains(choice3)){
					System.out.println("Description Contains Choice3");
				}else{
					System.out.println("Description did not contain Choice3");
					return false;
				}
			}
			if(defaultValue != null && !defaultValue.equals("")){
				//For Yes or No Question Type
				if(defaultValue.equalsIgnoreCase("Yes")){
					defaultValue = "1";
				}else if(defaultValue.equalsIgnoreCase("No")){
					defaultValue = "0";
				}
				if(descriptionFromDB.contains(defaultValue)){
					System.out.println("Description Contains Default Value");
				}else{
					System.out.println("Description did not contain Default Value");
					return false;
				}
			}
		}else if(isResponse){
			//Text value in the Database ignores symbols <, > and :
			System.out.println(descriptionFromDB +" "+question+" : "+(responce.replace("<", "").replace(">", "")));
			if(descriptionFromDB.equals(question+" : "+(responce.replace("<", "").replace(">", "")))){
				System.out.println("Description contains Question and Responce");
			}else{
				System.out.println("Description did not contain Question and Responce");
				return false;
			}
		}else{
			if(description != null && !description.equals("")){
				if(description.equals(descriptionFromDB)){
					System.out.println("Description Matches with the Database Value");
				}else{
					System.out.println("Description did not match with the Database Value");
					return false;
				}
			}
		}	
		return true;
	}
	
	public boolean verifyMessageTableForTasks(int interID, String description, String name, String startDate, String dueDate, String assignedTo,
			String percentageComplete, String predecessors, String priority, String taskStatus, String time2, String file, String fileType,
			boolean isFileType, String customField1, String customField2) throws SQLException{
		String descriptionFromDB;
		String nameFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		descriptionFromDB = rs.getString(1);
		attributes = rs.getString(2);
		
		//Verify Name
		nameFromDB = utils.extractString(attributes, "Title");
		nameFromDB = nameFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		if(name.equals(nameFromDB)){
			System.out.println("Name Matches with the Database Value");
		}else{
			System.out.println("Name did not match with the Database Value");
			return false;
		}
		//Verify Description
		if(description != null && !description.equals("")){
			if(descriptionFromDB.contains(description)){
				System.out.println("Description Matches with the Database Value");
			}else{
				System.out.println("Description did not match with the Database Value");
				return false;
			}
		}
		//Verify AssignedTo
		if(assignedTo != null && !assignedTo.equals("")){
			if(assignedTo.equals(utils.extractString(attributes, "Assigned To"))){
				System.out.println("AssignedTo Matches with the Database Value");
			}else{
				System.out.println("AssignedTo did not match with the Database Value");
				return false;
			}
		}
		//Verify Task Status
		if(taskStatus != null && !taskStatus.equals("")){
			if(taskStatus.equals(utils.extractString(attributes, "Task Status"))){
				System.out.println("Task Status Matches with the Database Value");
			}else{
				System.out.println("Task Status did not match with the Database Value");
				return false;
			}
		}
		//Verify Priority
		if(priority != null && !priority.equals("")){
			if(priority.equals(utils.extractString(attributes, "Priority"))){
				System.out.println("Priority Matches with the Database Value");
			}else{
				System.out.println("Priority did not match with the Database Value");
				return false;
			}
		}	
		//Verify Percentage Complete
		if(percentageComplete != null && !percentageComplete.equals("")){
			if(percentageComplete.equals(utils.extractString(attributes, "Percentage Completed"))){
				System.out.println("Percentage Completed Matches with the Database Value");
			}else{
				System.out.println("Percentage Completed did not match with the Database Value");
				return false;
			}
		}	
		//Verify Predecessor
		if(predecessors != null && !predecessors.equals("")){
			if(predecessors.equals(utils.extractString(attributes, "Predecessors"))){
				System.out.println("Predecessors Matches with the Database Value");
			}else{
				System.out.println("Predecessors did not match with the Database Value");
				return false;
			}
		}
		//Verify Start Date
		if(startDate != null && !startDate.equals("")){
			if(startDate.equals(utils.extractString(attributes, "Start Date"))){
				System.out.println("Start Date Matches with the Database Value");
			}else{
				System.out.println("Start Date did not match with the Database Value");
				return false;
			}
		}	
		//Verify Due Date
		if(dueDate != null && !dueDate.equals("")){
			if(dueDate.equals(utils.extractString(attributes, "Due Date"))){
				System.out.println("Due Date Matches with the Database Value");
			}else{
				System.out.println("Due Date did not match with the Database Value");
				return false;
			}
		}
		if(!verifyCustomFields(customField1, customField2, attributes)){
			return false;
		}
		return true;
	}
	
	public boolean verifyBlogPostsComentCantegory(int interID, String title, String body, String published, String category, 
			String file, String fileType, boolean isFileType, String customField1, String customField2) throws SQLException{
		String bodyFromDB;
		String titleFromDB;
		String publishedFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		bodyFromDB = rs.getString(1);
		attributes = rs.getString(2);
		
		//Verify Title
		titleFromDB = utils.extractString(attributes, "Title");
		titleFromDB = titleFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		if(title.equals(titleFromDB)){
			System.out.println("Title Matches with the Database Value");
		}else{
			System.out.println("Title did not match with the Database Value");
			return false;
		}
		//Verify Description
		if(body != null && !body.equals("")){
			if(bodyFromDB.contains(body)){
				System.out.println("Description Matches with the Database Value");
			}else{
				System.out.println("Description did not match with the Database Value");
				return false;
			}
		}
		//Verify Category
		if (category != null && !category.equals("")) {
			if (category.equals(utils.extractString(attributes, "Category"))) {
				System.out.println("Category Matches with the Database Value");
			} else {
				System.out.println("Category did not match with the Database Value");
				return false;
			}
		}		
		
		if(published != null && !published.equals("")){
			publishedFromDB = utils.extractString(attributes, "Published");
			if(publishedFromDB.contains(published)){
				System.out.println("Published value Matches with the Database value");
			}else{
				System.out.println("Published did not match with the Database value");
				return false;
			}
		}
		
		if(!verifyCustomFields(customField1, customField2, attributes)){
			return false;
		}
		
		return true;
	}
	
	
	public boolean verifySites(int interID, TestDataObject object) throws SQLException{
		String bodyFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID + " AND text IS NOT NULL";
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		bodyFromDB = rs.getString(1);
		attributes = rs.getString(2);
		
		if(object.getType().equals("Shared Documents")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			if(!fields.verifyFileName(attributes, object.getFile())){
				return false;
			}
			
		}else if(object.getType().equals("FormLib1")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			if(!fields.verifyFileName(attributes, object.getFile())){
				return false;
			}
			
		}else if(object.getType().equals("PictureLib1")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			if(!fields.verifyDescription(attributes, object.getDescription())){
				return false;
			}
			if(!fields.verifyKeyword(attributes, object.getKeywords())){
				return false;
			}
			if(!fields.verifyDatePictureTaken(attributes, object.getDatePictureTaken())){
				return false;
			}
			if(!fields.verifyFileName(attributes, object.getFile())){
				return false;
			}
			
		}else if(object.getType().equals("Report lib1")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			if(!fields.verifyDescription(attributes, object.getDescription())){
				return false;
			}
			if(!fields.verifyFileName(attributes, object.getFile())){
				return false;
			}
			
		}else if(object.getFeature().equals("eWiki")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			if(!fields.verifyDescriptionAsComments(attributes, object.getDescription())){
				return false;
			}
			if(!fields.verifyFileNameForPages(attributes, object.getFile())){
				return false;
			}
			
		}else if(object.getFeature().equals("CustomFieldTesting/eWiki")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			if(!fields.verifyFileName(attributes, object.getFile())){
				return false;
			}
			
		}else if(object.getFeature().equals("/eWiki")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			if(!fields.verifyDescriptionAsComments(attributes, object.getDescription())){
				return false;
			}
			if(!fields.verifyFileNameForPages(attributes, object.getFile())){
				return false;
			}
			
		}else if(object.getFeature().equals("FieldTesting/eWiki")){	
			query = "Select text, attributes from Messages where interID = "+interID ;
			System.out.println(query);
			rs = stmt.executeQuery(query);
			while(rs.next()){
				bodyFromDB = rs.getString(1);
				attributes = rs.getString(2);
				if(bodyFromDB != null){
					if(!fields.verifyTitle(attributes, object.getTitle())){
						return false;
					}
					if(!bodyFromDB.equals(object.getDescription())){
						return false;
					}
					if(!fields.verifyFileName(attributes, object.getFile())){
						return false;
					}
					if(verifyCustomFields(object.getCustomField1(), object.getCustomField2(), attributes)){
						return true;
					}
				}
			}
			
		}else if(object.getType().equals("Wiki Page Liabrary")){
			if(!fields.verifyName(attributes, object.getTitle())){
				return false;
			}
			if(!fields.verifyFileName(attributes, object.getFile())){
				return false;
			}
			
		}else if(object.getFeature().equals("Testing")){
			query = "Select text, attributes from Messages where interID = "+interID;
			System.out.println(query);
			rs = stmt.executeQuery(query);
			while(rs.next()){
				bodyFromDB = rs.getString(1);
				attributes = rs.getString(2);
				if(bodyFromDB != null){
					if(!fields.verifyTitle(attributes, object.getTitle())){
						return false;
					}
					if(!fields.verifyCommens(attributes, object.getComments())){
						return false;
					}
					if(!fields.verifyKeyword(attributes, object.getKeywords())){
						return false;
					}
					if(!fields.verifyDatePictureTaken(attributes, object.getDatePictureTaken())){
						return false;
					}
					if(!fields.verifyAuthor(attributes, object.getAuthor())){
						return false;
					}
					if(!fields.verifyPreviewImageURL(attributes, object.getPreviewImageURL())){
						return false;
					}
					if(!fields.verifyCopyright(attributes, object.getCopyRight())){
						return false;
					}
					if(!fields.verifyFileName(attributes, object.getFile())){
						return false;
					}
					break;
				}
			}
		}else if(object.getType().equals("Asset Lib1")){
			ArrayList<String> data = getDataFromMessages(stmt, interID);
			bodyFromDB = data.get(0);
			attributes = data.get(1);
			if(!fields.verifyTitle(attributes, object.getTitle())){
				System.out.println("In : "+object.getTitle());
				return false;
			}
			if(!fields.verifyCommens(attributes, object.getComments())){
				return false;
			}
			if(!fields.verifyKeyword(attributes, object.getKeywords())){
				return false;
			}
			if(!fields.verifyDatePictureTaken(attributes, object.getDatePictureTaken())){
				return false;
			}
			if(!fields.verifyAuthor(attributes, object.getAuthor())){
				return false;
			}
			if(!fields.verifyPreviewImageURL(attributes, object.getPreviewImageURL())){
				return false;
			}
			if(!fields.verifyCopyright(attributes, object.getCopyRight())){
				return false;
			}
			if(!fields.verifyFileName(attributes, object.getFile())){
				return false;
			}
			
		}else if(object.getType().equals("Pages")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			if(!fields.verifyDescription(attributes, object.getDescription())){
				return false;
			}
			if(!fields.verifyFileName(attributes, object.getFile())){
				return false;
			}
			
		}else{
			System.out.println("Invalid Feature");
			return false;
		}
		
		if(!verifyCustomFields(object.getCustomField1(), object.getCustomField2(), attributes)){
			return false;
		}
		return true;
	}
	
	public boolean verifyCustomLists(int interID, TestDataObject object) throws SQLException{
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		String bodyFromDB = rs.getString(1);
		String attributes = rs.getString(2);
		
		if(object.getContentType().equals("Discussion Board")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			System.out.println(object.getBody()+" "+bodyFromDB);
			if(object.getBody().equals(bodyFromDB)){
				System.out.println("Body Matches with the Database value");
			}else{
				System.out.println("Body did not match with the Database value");
				return false;
			}
			
		}else if(object.getContentType().equals("Blog")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			System.out.println(object.getBody()+" "+bodyFromDB);
			if(object.getBody().equals(bodyFromDB)){
				System.out.println("Body Matches with the Database value");
			}else{
				System.out.println("Body did not match with the Database value");
				return false;
			}
			if(!fields.verifyPublishedDate(attributes, object.getExpires())){
				return false;
			}
			
		}else if(object.getContentType().equals("Announcements")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			System.out.println(object.getBody()+" "+bodyFromDB);
			if(object.getDescription().equals(bodyFromDB)){
				System.out.println("Description Matches with the Database value");
			}else{
				System.out.println("Description did not match with the Database value");
				return false;
			}
			if(!fields.verifyExpires(attributes, object.getExpires())){
				return false;
			}
			
		}else if(object.getContentType().equals("Tasks")){
			if(!fields.verifyTitle(attributes, object.getTitle())){
				return false;
			}
			if(object.getBody().equals(bodyFromDB)){
				System.out.println("Body Matches with the Database value");
			}else{
				System.out.println("Body did not match with the Database value");
				return false;
			}
			if(!fields.verifyDueDate(attributes, object.getDueDate())){
				return false;
			}
			
		}else if(object.getContentType().equals("Links")){
			if(!fields.verifyNotes(attributes, object.getNotes())){
				return false;
			}
			if(!fields.verifyURL(attributes, object.getuRL())){
				return false;
			}
			
		}else{
			System.out.println("Invalid Custom list");
			return false;
		}
		if(!verifyCustomFields(object.getCustomField1(), object.getCustomField2(), attributes)){
			return false;
		}
		return true;
	}
	
	public boolean verifyDatabase(TestDataObject object, ArrayList<String> DBValues, boolean isFileType) throws NumberFormatException, SQLException{
		//Verify Content Type
//		System.out.println("Input"+object.getContentType());
//		System.out.println("Output : "+ getContentType(DBValues.get(2)));
		if(object.getContentType().equals(getContentType(DBValues.get(2)))){
			System.out.println("Content Type Matches with the Database Value");
		}else{
			System.out.println("Content Type did not match with the Database Value");
			return false;
		}
		//Verify Content Sub Type
//		System.out.println(object.getContentSubType());
		if(object.getContentSubType() != null && !object.getContentSubType().equals("")){
			if(object.getContentSubType().equalsIgnoreCase(getContentSubType(DBValues.get(3)))){
				System.out.println("Content Sub Type Matches with the Database Value");
			}else{
				System.out.println("Content Sub Type did not match with the Database Value");
				return false;
			}
		}
		//Verify Action Type
		if(object.getActionType().equals(getActionName(DBValues.get(5)))){
			System.out.println("Action Type Matches with the Database Value");
		}else{
			System.out.println("Action Type did not match with the Database Value");
			return false;
		}
		//Verify Resource Name and URL
		ArrayList<String> resNameAndURL = getResourceNameAndURL(DBValues.get(4));
//		if(object.getType().equals("Survey")){
			if(object.getResourceName().contains(resNameAndURL.get(0))){
				System.out.println("Resource Name Matches with the Database Value");
			}else{
				System.out.println("Resource Name did not match with the Database Value");
				return false;
			}
			if(resNameAndURL.get(1).contains(object.getResourceURL().replaceAll(" ", "%20"))){
				System.out.println("Resource URL Matches with the Database Value");
			}else{
				System.out.println("Resource URL did not match with the Database Value");
				return false;
			}
//		}	
		
		//Verify Title, Body and Expires
		if(object.getType().equals("Announcement")){
			if(verifyMessageTableForAnnouncements(Integer.parseInt(DBValues.get(0)), object.getTitle(), object.getBody(), object.getExpires(), 
					object.getCustomField1(), object.getCustomField2(), isFileType)){
				System.out.println("Title, Body and Expires match with the Database values");
			}else{
				System.out.println("Message Table values did not match with the CSV values");
				return false;
			}
			
		}else if(object.getType().equals("Discussion Board")){
			if(verifyMessageTableForDiscussionBoard(Integer.parseInt(DBValues.get(0)), object.getSubject(), object.getBody(), object.getIsQuestion(), 
					object.getReply(), object.getTime1(), object.getTime2(), object.getFile(), object.getFileType(), object.getAction().contains("Reply"), 
					isFileType, object.getCustomField1(), object.getCustomField2())){
				System.out.println("Title, Body and isQuestion match with the Database values");
			}else{
				System.out.println("Message Table values did not match with the CSV values");
				return false;
			}
			
		}else if(object.getType().equals("Issue Tracking")){
			if(verifyMessageTableForIssueTracking(Integer.parseInt(DBValues.get(0)), object.getTitle(), object.getDescription(), object.getAssignedTo(), object.getIssueStatus(), 
					object.getPriority(), object.getCategory(), object.getRelatedIssues(), object.getComments(), object.getDueDate(), object.getFile())){
				System.out.println("Message Table values match with the CSV values");
			}else{
				System.out.println("Message Table values did not match with the CSV values");
				return false;
			}
			
		}else if(object.getType().equals("Links")){
			if(verifyMessageTableForLinks(Integer.parseInt(DBValues.get(0)), object.getDescription(), object.getuRL(), object.getNotes(),
					object.getCustomField1(), object.getCustomField2())){
				System.out.println("Message Table values match with the Database values");
			}else{
				System.out.println("Message Table values did not match with the Database values");
				return false;
			}
		}else if(object.getType().equals("Survey")){
			if(verifyMessageTableForSurvey(Integer.parseInt(DBValues.get(0)), object.getDescription(), object.getName(), object.getQuestion(), object.getChoice1(), 
					object.getChoice2(), object.getChoice3(), object.getDefaultValue(), object.getResponce(), object.getFile(), object.getFileType(), 
					object.getSubAction().contains("Question"), object.getSubAction().contains("Respond"))){
				System.out.println("Message Table values match with the Database values");
			}else{
				System.out.println("Message Table values did not match with the Database values");
				return false;
			}	
		}else if(object.getType().equals("Tasks")){
			if(verifyMessageTableForTasks(Integer.parseInt(DBValues.get(0)), object.getDescription(), object.getName(), object.getStartDate(), object.getDueDate(), 
					object.getAssignedTo(), object.getPercentageCompletion(), object.getRelatedIssues(), object.getPriority(), object.getIssueStatus(), object.getTime1(), 
					object.getFile(), object.getFileType(), isFileType, object.getCustomField1(), object.getCustomField2())){
				System.out.println("Message Table values match with the Database values");
			}else{
				System.out.println("Message Table values did not match with the Database values");
				return false;
			}
		}else if(object.getFeature().equals("Aut_Blog1/Lists")){	
			if(verifyBlogPostsComentCantegory(Integer.parseInt(DBValues.get(0)), object.getTitle(), object.getBody(), object.getExpires(), object.getCategory(), 
					object.getFile(), object.getFileType(), isFileType, object.getCustomField1(), object.getCustomField2())){
				System.out.println("Message Table values match with the Database values");
			}else{
				System.out.println("Message Table values did not match with the Database values");
				return false;
			}
		}else if(object.getType().equals("CustomList")){
			if(!verifyCustomLists(Integer.parseInt(DBValues.get(0)), object)){
				return false;
			}
		}else if(object.getFeature().equals("Documents")){
			if(!verifyCustomFields(object.getCustomField1(), object.getCustomField2(), getAttributes(stmt, Integer.parseInt(DBValues.get(0))))){
				System.out.println("Custom Fields did not matched");
			}
			if(!verifyFile(stmt, Integer.parseInt(DBValues.get(0)), object.getFile())){
				return false;
			}
		
			
		}else{
			if(!verifySites(Integer.parseInt(DBValues.get(0)), object)){
				return false;
			}
		}
		
		return true;
	}
	
	public String getAttributes(Statement stmt, int interId){
		String attribute = null;
		String query = "select attributes from messages where interId = "+ interId ;
		try{
			System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.next();
			attribute = rs.getString("attributes");
		}catch(Exception e){
			System.out.println("Unable to fetch attributes");
			e.printStackTrace();
		}
		return attribute;
	}
	
	public boolean verifyFile(Statement stmt, int interId, String filename){
		String query = "select count(*) from filexfers where interId ="+ interId+ " and fileName like '%"+filename+"%'";
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			if(rs.getInt(1) > 0){
				System.out.println("File Name Matched");
				return true;
			}else{
				System.out.println("File Name did not Matched");
				return false;
			}
		}catch(SQLException se){
			System.out.println("Unable to fecth file name");
		}
		return false;
	}
	
	public ArrayList<String> getDataFromMessages(Statement stmt, int interId){
		ArrayList<String> getMsgData = new ArrayList();
		String query = "select text, attributes from Messages where interid ="+ interId + " and text IS NOT NULL"; 
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			getMsgData.add(rs.getString(1));
			getMsgData.add(rs.getString(2));
		}catch(SQLException se){
			se.printStackTrace();
		}
		return getMsgData;
	}
	
}
