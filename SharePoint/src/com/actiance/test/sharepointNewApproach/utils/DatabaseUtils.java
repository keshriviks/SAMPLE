package com.actiance.test.sharepointNewApproach.utils;

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

import com.facetime.ftcore.util.Base64;



import au.com.bytecode.opencsv.bean.CsvToBean;

public class DatabaseUtils {
	Connection con;
	ResultSet rs;
	Statement stmt;
	
	private ArrayList<String> failedFields;
	
	private String driverName= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	TestUtilsTopSiteLevelLists utils = new TestUtilsTopSiteLevelLists();
	
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
		System.out.println(query);
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
	
	// get Action Name from ActionTypes Table for Action Id..
	public String getActionName(String actionID) throws SQLException {
		String actionName = null;
		String query = "select name from ActionTypes where actionID = "+ actionID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		actionName = rs.getString("name");
		return actionName;
	}
	
	public String getContentType(String typeID) throws SQLException{
		String contentType;
		String query = "Select value from contentTypes where typeID = "+typeID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		contentType = rs.getString("value");
		//System.out.println(contentType);
		return contentType;
	}
	
	public String getContentSubType(String typeID) throws SQLException{
		String contentSubType;
		String query = "Select value from contentSubTypes where typeID = "+typeID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		contentSubType = rs.getString("value");
		return contentSubType;
	}
	
	public ArrayList<String> getResourceNameAndURL(String resourceId) throws SQLException {
		ArrayList<String> values = new ArrayList<>();
		String query = "select resName, resURL from Resources where resourceId = "+resourceId;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		rs.next();
		values.add(rs.getString("resName"));
		values.add(rs.getString("resURL"));
		return values;
	}
	
	public boolean verifyMessageTableForAnnouncements(int interID, String title, String body, String expires, boolean isFileType) throws SQLException{
		byte[] input = body.getBytes();
		byte[] output;
		String titleFromDB;
		String expiresFromDB;
		String textFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		
		textFromDB = rs.getNString(1);
		attributes = rs.getNString(2);
		
		System.out.println("Text From DB: "+textFromDB);
		
		titleFromDB = utils.extractString(attributes, "Title");
		if(Arrays.equals(title.replace("&", "&amp;").getBytes(), titleFromDB.getBytes())){
			System.out.println("Title Matches with the Database value");
		}else{
			System.out.println("Title did not match with the Database value");
			//return false;
			failedFields.add("Title");
		}
		
		if(isFileType){
			if(body != null && !body.trim().equals("")){
				System.out.println("Input: "+body+", Output: "+textFromDB);
				if(textFromDB.contains(body)){
					System.out.println("Body Matches with the Database value");
				}else{
					System.out.println("Body did not match with the Database value");
					//return false;
					failedFields.add("Body");
				}
			}
		}else{
			if(body != null && !body.trim().equals("")){
				System.out.println("Input: "+body+", Output: "+textFromDB);
				//if(Arrays.equals(input, textFromDB.getBytes())){
				if(isEqual(body,textFromDB)){	
					System.out.println("Body Matches with the Database value");
				}else{
					System.out.println("Body did not match with the Database value");
					//return false;
					failedFields.add("Body");
				}
			}
		}
		
		
		if(expires != null && !expires.equals("")){
			expiresFromDB = utils.extractString(attributes, "Expires");
			if(expires.equals(expiresFromDB)){
				System.out.println("Expires value Matches with the Database value");
			}else{
				System.out.println("Expires did not match with the Database value");
				//return false;
				failedFields.add("Expires");
			}
		}
		
		return true;
	}
	
	private boolean isEqual(String body, String textFromDB) {
		String str1 = new String(Base64.decode(body.getBytes()));
		String str2 = new String(Base64.decode(textFromDB.getBytes()));
		return str1.equals(str2);
	}

	public boolean verifyFile(String time1, String file, String fileType) throws SQLException{
		int interID;
		int contentTypeID;
		String fileNameFromDB;
		String contentType;
		String query = "Select interID, contentType from Interactions where networkID = 26 and startTime = "+time1;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			failedFields.add("Result Set was null");
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
			failedFields.add("Result Set was null");
			return false;
		}
		String attributes = rs.getString(1);
		fileNameFromDB = utils.extractString(attributes, "FileName");
		if(file.equals(fileNameFromDB)){
			System.out.println("File Name Mathes with the Database Value");
		}else{
			System.out.println("File Name did not match with the Database Value");
			//return false;
			failedFields.add("File Name");
		}
		if(fileType.equals(contentType)){
			System.out.println("File Type Matches with the Database Value");
		}else{
			System.out.println("File Type did not match with the Database Value");
			//return false;
			failedFields.add("File Type");
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
			failedFields.add("Result Set was null");
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
				//return false;
				failedFields.add("Parent Interaction Body");
			}
		}
		//Verify Subject
		if(subject.equals(subjectFromDB)){
			System.out.println("Subject of the Parent Interaction Matches with the Database Value");
		}else{
			System.out.println("Subject of the Parent Interaction did not match with the Database Value");
			//return false;
			failedFields.add("Parent Interaction Subject");
		}
		return true;
	}
	
	public boolean verifyMessageTableForDiscussionBoard(int interID, String subject, String body, String question, String reply,
			String time1, String time2, String file, String fileType, boolean isReply, boolean isFileType) throws SQLException{
		byte[] input = body.getBytes();
		String subjectFromDB;
		String questionFromDB;
		String textFromDB;
		String attributes;
		Boolean isQuestion = false;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			failedFields.add("Result Set was null");
			return false;
		}
		
		textFromDB = rs.getNString(1);
		attributes = rs.getNString(2);
		
		
		if(isFileType & isReply){
			//System.out.println("Input: "+body+", Output: "+textFromDB);
			if(textFromDB.contains(reply)){
				System.out.println("Reply Matches with the Database value");
			}else{
				System.out.println("Reply did not match with the Database value");
				//return false;
				failedFields.add("Reply");
			}
			//Verify Parent Interaction
			if(verifyParentInteraction(time1, subject, body)){
				System.out.println("Parent Interaction Matches with the Database Value");
			}else{
				System.out.println("Parent Interaction did not match with the Database Value");
				//return false;
			}
			//Verify File
			if(!verifyFile(time2.split("#")[0], file, fileType)){
				//return false;
			}
			
		}else if(isFileType & !isReply){
			if(body != null && !body.trim().equals("")){
				//System.out.println("Input: "+body+", Output: "+textFromDB);
				if(textFromDB.contains(body)){
					System.out.println("Body Matches with the Database value");
				}else{
					System.out.println("Body did not match with the Database value");
					//return false;
					failedFields.add("Body");
				}
			}
			//Verify File
			if(!verifyFile(time1, file, fileType)){
				//return false;
			}
			
		}else if(!isFileType & isReply){
			//System.out.println("Input: "+body+", Output: "+textFromDB);
			if(reply.equals(textFromDB)){
				System.out.println("Reply Matches with the Database value");
			}else{
				System.out.println("Reply did not match with the Database value");
				//return false;
				failedFields.add("Reply");
			}
			//Verify Parent Interaction
			if(verifyParentInteraction(time1, subject, body)){
				System.out.println("Parent Interaction Matches with the Database Value");
			}else{
				System.out.println("Parent Interaction did not match with the Database Value");
				//return false;
			}
			
		}else{
			subjectFromDB = utils.extractString(attributes, "Title");
			subjectFromDB = subjectFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			//System.out.println("Input Subject: "+subject+" Output Subject: "+subjectFromDB);
			if(Arrays.equals(subject.getBytes(), subjectFromDB.getBytes())){
				System.out.println("Subject Matches with the Database value");
			}else{
				System.out.println("Subject did not match with the Database value");
				//return false;
				failedFields.add("Subject");
			}
			
			if(body != null && !body.trim().equals("")){
				System.out.println("Input: "+body+", Output: "+textFromDB);
				if(Arrays.equals(input, textFromDB.getBytes())){
					System.out.println("Body Matches with the Database value");
				}else{
					System.out.println("Body did not match with the Database value");
					//return false;
					failedFields.add("Body");
				}
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
				//return false;
				failedFields.add("Question");
			}
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
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			failedFields.add("Result Set was null");
			return false;
		}
		attributes = rs.getNString(1);
		
		//Verify Title
		titleFromDB = utils.extractString(attributes, "Title");
		titleFromDB = titleFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		if(title.equals(titleFromDB)){
			System.out.println("Title Matches with the Database Value");
		}else{
			System.out.println("Title did not match with the Database Value");
			//return false;
			failedFields.add("Title");
		}
		//Verify AssignedTo
		if(assignedTo != null && !assignedTo.equals("")){
			if(assignedTo.equals(utils.extractString(attributes, "AssignedTo"))){
				System.out.println("AssignedTo Matches with the Database Value");
			}else{
				System.out.println("AssignedTo did not match with the Database Value");
				//return false;
				failedFields.add("AssignedTo");
			}
		}
		//Verify Issue Status
		if(issueStatus.equals(utils.extractString(attributes, "Status"))){
			System.out.println("Issue Status Matches with the Database Value");
		}else{
			System.out.println("Issue Status did not match with the Database Value");
			//return false;
			failedFields.add("Issue Status");
		}
		//Verify Priority
		if(priority.equals(utils.extractString(attributes, "Priority"))){
			System.out.println("Priority Matches with the Database Value");
		}else{
			System.out.println("Priority did not match with the Database Value");
			//return false;
			failedFields.add("Priority");
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
				//return false;
				failedFields.add("Description");
			}
		}
		//Verify Category
		if (category != null && !category.equals("")) {
			if (category.equals(utils.extractString(attributes, "Category"))) {
				System.out.println("Category Matches with the Database Value");
			} else {
				System.out.println("Category did not match with the Database Value");
				//return false;
				failedFields.add("Category");
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
				//return false;
				failedFields.add("Comment");
			}
		}
		//Verify Due Date
		if (dueDate != null && !dueDate.equals("")) {
			if (dueDate.equals(utils.extractString(attributes, "DueDate"))) {
				System.out.println("Due Date Matches with the Database Value");
			} else {
				System.out.println("Due Date did not match with the Database Value");
				//return false;
				failedFields.add("Due Date");
			}
		}
		//Verify File
		
		return true;
	}
	
	public boolean verifyMessageTableForLinks(int interID, String description, String URL, String notes) throws SQLException{
		String descriptionFromDB;
		String notesFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			failedFields.add("Result Set was null");
			return false;
		}
		descriptionFromDB = rs.getNString(1);
		attributes = rs.getNString(2);
		
		//Verify Description
		//DB does not store less than(<) and greater than(>) value in the column
		//descriptionFromDB = descriptionFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		System.out.println("In Des: "+description+", out Des: "+descriptionFromDB);
		if((description.replace("<", "").replace(">", "")).equals(descriptionFromDB)){
			System.out.println("Description Matches with the Database Value");
		}else{
			System.out.println("Description did not match with the Database Value");
			//return false;
			failedFields.add("Description");
		}
		//Verify Notes
		if(notes != null && !notes.equals("")){
			notesFromDB = utils.extractString(attributes, "Notes");
			notesFromDB = notesFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			//System.out.println("In Des: "+description+", out Des: "+descriptionFromDB);
			if((notes).equals(notesFromDB)){
				System.out.println("Notes Matches with the Database Value");
			}else{
				System.out.println("Notes did not match with the Database Value");
				//return false;
				failedFields.add("Notes");
			}
		}
		//Verify URL		
		if(URL.equals(utils.extractString(attributes, "URL"))){
			System.out.println("URL Matches with the Database Value");
		}else{
			System.out.println("URL did not match with the Database Value");
			//return false;
			failedFields.add("URL");
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
			failedFields.add("Result Set was null");
			return false;
		}
		descriptionFromDB = rs.getNString(1);
		attributes = rs.getNString(2);
		
		//Verify Name
		nameFromDB = utils.extractString(attributes, "Title");
		nameFromDB = nameFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		if(name.equals(nameFromDB)){
			System.out.println("Name Matches with the Database Value");
		}else{
			System.out.println("Name did not match with the Database Value");
			//return false;
			failedFields.add("Name");
		}
			
		if(isQuestion){
			if(descriptionFromDB.contains(question)){
				System.out.println("Description Contains Question");
			}else{
				System.out.println("Description did not contain Question");
				//return false;
				failedFields.add("Description");
			}
			if(choice1 != null && !choice1.equals("")){
				if(descriptionFromDB.contains(choice1)){
					System.out.println("Description Contains Choice1");
				}else{
					System.out.println("Description did not contain Choice1");
					//return false;
					failedFields.add("Description");
				}
			}
			if(choice2 != null && !choice2.equals("")){
				if(descriptionFromDB.contains(choice2)){
					System.out.println("Description Contains Choice2");
				}else{
					System.out.println("Description did not contain Choice2");
					//return false;
					failedFields.add("Description");
				}
			}
			if(choice3 != null && !choice3.equals("")){
				if(descriptionFromDB.contains(choice3)){
					System.out.println("Description Contains Choice3");
				}else{
					System.out.println("Description did not contain Choice3");
					//return false;
					failedFields.add("Description");
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
					//return false;
					failedFields.add("Description");
				}
			}//TCO365SP403 Question : TCO365SP403 https://www.google.co.in
		}else if(isResponse){
			//Text value in the Database ignores symbols <, > and :
			System.out.println(descriptionFromDB +" "+question+" : "+(responce.replace("<", "").replace(">", "")));
			if(descriptionFromDB.equals(question+" : "+(responce.replace("<", "").replace(">", "")))){
				System.out.println("Description contains Question and Responce");
			}else{
				System.out.println("Description did not contain Question and Responce");
				//return false;
				failedFields.add("Description");
			}
		}else{
			if(description != null && !description.equals("")){
				if(description.equals(descriptionFromDB)){
					System.out.println("Description Matches with the Database Value");
				}else{
					System.out.println("Description did not match with the Database Value");
					//return false;
					failedFields.add("Description");
				}
			}
		}	
		return true;
	}
	
	public boolean verifyMessageTableForTasks(int interID, String description, String name, String startDate, String dueDate, String assignedTo,
			String percentageComplete, String predecessors, String priority, String taskStatus, String time2, String file, String fileType,
			boolean isFileType) throws SQLException{
		String descriptionFromDB;
		String nameFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			failedFields.add("Result Set was null");
			return false;
		}
		descriptionFromDB = rs.getNString(1);
		attributes = rs.getNString(2);
		
		//Verify Name
		nameFromDB = utils.extractString(attributes, "Title");
		nameFromDB = nameFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		if(name.equals(nameFromDB)){
			System.out.println("Name Matches with the Database Value");
		}else{
			System.out.println("Name did not match with the Database Value");
			//return false;
			failedFields.add("Name");
		}
		//Verify Description
		if(description != null && !description.equals("")){
			if(descriptionFromDB.contains(description)){
				System.out.println("Description Matches with the Database Value");
			}else{
				System.out.println("Description did not match with the Database Value");
				//return false;
				failedFields.add("Description");
			}
		}
		//Verify AssignedTo
		if(assignedTo != null && !assignedTo.equals("")){
			if(assignedTo.equals(utils.extractString(attributes, "Assigned To"))){
				System.out.println("AssignedTo Matches with the Database Value");
			}else{
				System.out.println("AssignedTo did not match with the Database Value");
				//return false;
				failedFields.add("AssignedTo");
			}
		}
		//Verify Task Status
		if(taskStatus != null && !taskStatus.equals("")){
			if(taskStatus.equals(utils.extractString(attributes, "Task Status"))){
				System.out.println("Task Status Matches with the Database Value");
			}else{
				System.out.println("Task Status did not match with the Database Value");
				//return false;
				failedFields.add("Task Status");
			}
		}
		//Verify Priority
		if(priority != null && !priority.equals("")){
			if(priority.equals(utils.extractString(attributes, "Priority"))){
				System.out.println("Priority Matches with the Database Value");
			}else{
				System.out.println("Priority did not match with the Database Value");
				//return false;
				failedFields.add("Priority");
			}
		}	
		//Verify Percentage Complete
		if(percentageComplete != null && !percentageComplete.equals("")){
			if(percentageComplete.equals(utils.extractString(attributes, "Percentage Completed"))){
				System.out.println("Percentage Completed Matches with the Database Value");
			}else{
				System.out.println("Percentage Completed did not match with the Database Value");
				//return false;
				failedFields.add("Percentage Completed");
			}
		}	
		//Verify Predecessor
		if(predecessors != null && !predecessors.equals("")){
			if(predecessors.equals(utils.extractString(attributes, "Predecessors"))){
				System.out.println("Predecessors Matches with the Database Value");
			}else{
				System.out.println("Predecessors did not match with the Database Value");
				//return false;
				failedFields.add("Predecessros");
			}
		}
		//Verify Start Date
		if(startDate != null && !startDate.equals("")){
			if(startDate.equals(utils.extractString(attributes, "Start Date"))){
				System.out.println("Start Date Matches with the Database Value");
			}else{
				System.out.println("Start Date did not match with the Database Value");
				//return false;
				failedFields.add("Start Date");
			}
		}	
		//Verify Due Date
		if(dueDate != null && !dueDate.equals("")){
			if(dueDate.equals(utils.extractString(attributes, "Due Date"))){
				System.out.println("Due Date Matches with the Database Value");
			}else{
				System.out.println("Due Date did not match with the Database Value");
				//return false;
				failedFields.add("Due Date");
			}
		}
		//Verify file and file type
		if(isFileType){
			if(!verifyFile(time2, file, fileType)){
				System.out.println("File name and type did not match");
				//return false;
			}
		}
		return true;
	}
	
	public boolean verifyDatabase(CSVObjectsTopSiteLevelLists object, ArrayList<String> DBValues, boolean isFileType) throws NumberFormatException, SQLException{
		
		failedFields = new ArrayList<>();
		try{
			//Verify Content Type
			//System.out.println(object.getContentType());
			if(object.getContentType().equals(getContentType(DBValues.get(2)))){
				System.out.println("Content Type Matches with the Database Value");
			}else{
				System.out.println("Content Type did not match with the Database Value");
				//return false;
				failedFields.add("Content Type");
			}
			//Verify Content Sub Type
			if(object.getContentSubType() != null && !object.getContentSubType().equals("")){
				if(object.getContentSubType().equals(getContentSubType(DBValues.get(3)))){
					System.out.println("Content Sub Type Matches with the Database Value");
				}else{
					System.out.println("Content Sub Type did not match with the Database Value");
					//return false;
					failedFields.add("Content Sub Type");
				}
			}
			//Verify Action Type
			if(object.getActionType().equals(getActionName(DBValues.get(5)))){
				System.out.println("Action Type Matches with the Database Value");
			}else{
				System.out.println("Action Type did not match with the Database Value");
				//return false;
				failedFields.add("Action Type");
			}
			//Verify Resource Name and URL
			ArrayList<String> resNameAndURL = getResourceNameAndURL(DBValues.get(4));
			if(object.getType().equals("Survey")){
				if(object.getName().equals(resNameAndURL.get(0))){
					System.out.println("Resource Name Matches with the Database Value");
				}else{
					System.out.println("Resource Name did not match with the Database Value");
					//return false;
					failedFields.add("Resource Name");
				}
				if(resNameAndURL.get(1).replace("%20", " ").contains(object.getFeature()+"/"+object.getName())){
					System.out.println("Resource URL Matches with the Database Value");
				}else{
					System.out.println("Resource URL did not match with the Database Value");
					//return false;
					failedFields.add("Resource URL");
				}
				
			}else{
				
				if(object.getType().equals(resNameAndURL.get(0))){
					System.out.println("Resource Name Matches with the Database Value");
				}else{
					System.out.println("Resource Name did not match with the Database Value");
					//return false;
					failedFields.add("Resource Name");
				}
				if(resNameAndURL.get(1).replace("%20", " ").contains(object.getFeature()+"/"+object.getType())){
					System.out.println("Resource URL Matches with the Database Value");
				}else{
					System.out.println("Resource URL did not match with the Database Value");
					//return false;
					failedFields.add("Resource URL");
				}
			}
			
			//Verify Title, Body and Expires
			if(object.getType().equals("Announcements")){
				if(verifyMessageTableForAnnouncements(Integer.parseInt(DBValues.get(0)), object.getTitle(), object.getBody(), object.getExpires(), isFileType)){
					System.out.println("Title, Body and Expires match with the Database values");
				}else{
					System.out.println("Message Table values did not match with the CSV values");
					//return false;
				}
				
			}else if(object.getType().equals("Discussion Board")){
				if(verifyMessageTableForDiscussionBoard(Integer.parseInt(DBValues.get(0)), object.getSubject(), object.getBody(), object.getIsQuestion(), 
						object.getReply(), object.getTime1(), object.getTime2(), object.getFile(), object.getFileType(), object.getAction().contains("Reply"), isFileType)){
					System.out.println("Title, Body and isQuestion match with the Database values");
				}else{
					System.out.println("Message Table values did not match with the CSV values");
					//return false;
				}
				
			}else if(object.getType().equals("Issue Tracking")){
				if(verifyMessageTableForIssueTracking(Integer.parseInt(DBValues.get(0)), object.getTitle(), object.getDescription(), object.getAssignedTo(), object.getIssueStatus(), 
						object.getPriority(), object.getCategory(), object.getRelatedIssue(), object.getComment(), object.getDueDate(), object.getFile())){
					System.out.println("Message Table values match with the CSV values");
				}else{
					System.out.println("Message Table values did not match with the CSV values");
					//return false;
				}
				
			}else if(object.getType().equals("Links")){
				if(verifyMessageTableForLinks(Integer.parseInt(DBValues.get(0)), object.getDescription(), object.getUrl(), object.getNotes())){
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
						object.getAssignedTo(), object.getPercentageCompletion(), object.getRelatedIssue(), object.getPriority(), object.getIssueStatus(), object.getTime1(), 
						object.getFile(), object.getFileType(), isFileType)){
					System.out.println("Message Table values match with the Database values");
				}else{
					System.out.println("Message Table values did not match with the Database values");
					return false;
				}
			}else{
				System.out.println("Invalid Type");
				failedFields.add("Invalid Type");
				//return false;
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		if(failedFields.size() == 0){
			return true;
		}else{
			return false;
		}
		
		//return true;
	}

	public ArrayList<String> getFailedColumns(){
		return failedFields;
	}
}
