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

public class DatabaseUtilsForSubSitesLevelLists {
	Connection con;
	ResultSet rs;
	Statement stmt;
	
	private ArrayList<String> failedFields;
	
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
		System.out.println(query);
		rs = stmt.executeQuery(query);
		//Statement stmt2 = con.createStatement();
		int k=0;
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
		        dbResultSetValues.add(getContentType2(contentTypeID));
		        dbResultSetValues.add(contentSubTypeID+"");
		        dbResultSetValues.add(resourceID+"");
		        dbResultSetValues.add(actionID+"");
		        dbResultSetValues.add(roomName);
		        dbResultSetValues.add(getResourceURL(resourceID));
		        //dbResultSetValues.add(getContentType(typeID));
		        
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
				
//				System.out.println("row "+(k++)+" "+interID);
				
				domSource = attributes.getSource(DOMSource.class);
		        document = (Document) domSource.getNode();
		        actionID = Integer.parseInt(xpath.evaluate(expression, document));
		        
		        dbResultSetValues = new ArrayList<>();
		        dbResultSetValues.add(interID+"");
		        dbResultSetValues.add(startTime+"");
		        dbResultSetValues.add(getContentType2(contentTypeID));
		        dbResultSetValues.add(contentSubTypeID+"");
		        dbResultSetValues.add(resourceID+"");
		        dbResultSetValues.add(actionID+"");
		        dbResultSetValues.add(roomName);
		        dbResultSetValues.add(getResourceURL(resourceID));
		       // dbResultSetValues.add(contentType);
		        interIDsMap.put(interID, dbResultSetValues);
			}
		  
		}
		return interIDsMap;
	}
	
	public String getContentType2(int typeID) {
		String contentType=null;
		String query = "Select value from contentTypes where typeID = "+typeID;
		//System.out.println(query);
		try{
			Statement stmt2 = con.createStatement();
			ResultSet rs2 = stmt2.executeQuery(query);
			rs2.next();
			contentType = rs2.getString(1);
		}catch(SQLException se){
			System.out.println("Content Type value is 0, which is Null on Vantage");
		}
		return contentType;
	}
	
	public String getResourceURL(int resourceID){
		String resourceURL = null;
		String query = "Select resURL from Resources where resourceID = "+resourceID;
		try{
			Statement stmt2 = con.createStatement();
			ResultSet rs2 = stmt2.executeQuery(query);
			rs2.next();
			resourceURL = rs2.getString(1);
		}catch(SQLException se){
			System.out.println("No Resource URL Found");
		}
		return resourceURL;
	}
	
	public String getActionName(String actionID)  {
		String actionName = null;
		String query = "select name from ActionTypes where actionID = "+ actionID;
		try{
//			System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.next();
			actionName = rs.getString("name");
		}catch(SQLException se){
			System.out.println("No Action Type Found");
			se.printStackTrace();
		}
		return actionName;
	}
	
	public String getContentType(String typeID){
		String contentType = null;
		String query = "Select value from contentTypes where typeID = "+typeID;
		try{
//			System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.next();
			contentType = rs.getString("value");
		}catch(SQLException se){
			System.out.println("No Content type Found");
		}
		return contentType;
	}
	
	public String getContentSubType(String typeID){
		String contentSubType = null;
		String query = "Select value from contentSubTypes where typeID = "+typeID;
		try{
//			System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.next();
			contentSubType = rs.getString("value");
		}catch(SQLException se){
			System.out.println("No Content Sub Type Found");	
		}
		
		return contentSubType;
	}
	
	public ArrayList<String> getResourceNameAndURL(String resourceId) {
		ArrayList<String> values = new ArrayList<>();
		try{
			String query = "select resName, resURL from Resources where resourceId = "+resourceId;
//			System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.next();
			values.add(rs.getString("resName"));
			values.add(rs.getString("resURL"));
		}catch(SQLException se){
			System.out.println("Resource Name And Resource URL is null");
		}
		
		return values;
	}
	
	public boolean verifyDatabase(CSVSubSiteLevelLists object, ArrayList<String> DBValues) throws NumberFormatException, SQLException{
		
		failedFields = new ArrayList<>();
		try{
				
			//Verify Content Type
//			System.out.println("DB : "+DBValues.get(2));
//			System.out.println("IN : "+object.getContentType());
			if(object.getContentType().equals(DBValues.get(2))){
				System.out.println("Content Type Matches with the Database Value");
			}else{
				System.out.println("Content Type did not match with the Database Value");
				failedFields.add("Content Type");
			}
			//Verify Content Sub Type
			if(object.getContentSubType() != null && !object.getContentSubType().equals("")){
				if(object.getContentSubType().equals(getContentSubType(DBValues.get(3)))){
					System.out.println("Content Sub Type Matches with the Database Value");
				}else{
					System.out.println("Content Sub Type did not match with the Database Value");
					failedFields.add("Content Sub Type");
				}
			}
			//Verify Action Type
			if(object.getActionType().equals(getActionName(DBValues.get(5)))){
				System.out.println("Action Type Matches with the Database Value");
			}else{
				System.out.println("Action Type did not match with the Database Value");
				failedFields.add("Action Type");
			}
			//Verify Resource Name and URL
			ArrayList<String> resNameAndURL = getResourceNameAndURL(DBValues.get(4));
			if(object.getResourceName().equals(resNameAndURL.get(0))){
				System.out.println("Resource Name Matches with the Database Value");
			}else{
				System.out.println("Resource Name did not match with the Database Value");
				failedFields.add("Resource Name");
				//return false;
			}
			if(object.getResourceURL().equals(resNameAndURL.get(1))){
				System.out.println("Resource URL Matches with the Database Value");
			}else{
				System.out.println("Resource URL did not match with the Database Value");
				failedFields.add("Resource URL");
				//return false;
			}
			
			if(!verifyMessageTable(Integer.parseInt(DBValues.get(0)), object)){
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
	
	public boolean verifyMessageTable(int interID, CSVSubSiteLevelLists object) throws SQLException{
		String bodyFromDB;
		String attributes;
		
		String query = "Select text, attributes from Messages where interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			failedFields.add("Result Set was null");
			return false;
		}
		bodyFromDB = rs.getNString(1);
		attributes = rs.getNString(2);
		
		//Verification for Team Site
		if(object.getType().equalsIgnoreCase("Teamsite")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else if(object.getAction().equals("Create NewsFeed")){
				if(!bodyFromDB.equals(object.getDescription())){
					System.out.println("Description did not Match with the Database Value");
					//return false;
					failedFields.add("Description");
				}
				if(!fields.verifyTitleAsOriginalContent(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
			}else{
				if(!fields.verifyTitle(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
				if(!fields.verifyFileName(attributes, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}
			
		//Verification for Record Center	
		}else if(object.getType().equalsIgnoreCase("Record Center")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else{
				if(!fields.verifyTitle(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
				if(!fields.verifyFileName(attributes, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}
			
		//Verify Blog	
		}else if(object.getType().equalsIgnoreCase("Blog")){
			if(object.getAction().equals("Create Post") || object.getAction().equals("Edit Post")){
				if(!fields.verifyCategory(attributes, object.getCategory())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!fields.verifyPublishedDate(attributes, object.getPublish())){
					//return false;
					failedFields.add("Published Date");
				}
				if(!fields.verifyTitle(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
				if(!fields.verifyText(object.getBody(), bodyFromDB)){
					//return false;
					failedFields.add("Text");
				}
			}else if(object.getAction().equals("Upload File")){
				if(!verifyFile(object.getTime2(), object.getFile(), object.getFileType())){
					//return false;
				}
				if(!fields.verifyCategory(attributes, object.getCategory())){
					//return false;
					failedFields.add("Category");
				}
				if(!fields.verifyPublishedDate(attributes, object.getPublish())){
					//return false;
					failedFields.add("Published Date");
				}
				if(!fields.verifyTitle(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
				if(!bodyFromDB.contains(object.getBody())){
					//return false;
					failedFields.add("Body");
				}
			}else if(object.getAction().equals("Add Comment")){
				if(!verifyParentPostForBlogComments(object.getTime2(), object.getTitle(), object.getPublish(), object.getContentType())){
					//return false;
				}
				if(!fields.verifyText(object.getComments(), bodyFromDB)){
					//return false;
					failedFields.add("Text");
				}
			}else if(object.getAction().equals("Edit Comment")){
				if(!verifyParentPostForBlogComments(object.getTime2(), object.getTitle(), object.getPublish(), object.getContentType())){
					//return false;
				}
				if(!fields.verifyCommentAsTitle(attributes, object.getComments())){
					//return false;
					failedFields.add("Comments");
				}
				if(!fields.verifyText(object.getBody(), bodyFromDB)){
					//return false;
					failedFields.add("Text");
				}
			}else if(object.getAction().equals("Create Blog Comment") || object.getAction().equals("Edit Blog Comment")){
				if(!fields.verifyTitle(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
				if(!fields.verifyText(object.getBody(), bodyFromDB)){
					//return false;
					failedFields.add("Text");
				}
			}else if(object.getAction().equals("Create Blog Category") || object.getAction().equals("Edit Blog Category")){
				if(!fields.verifyTitle(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
			}else{
				System.out.println("Invalid Action");
				//return false;
				failedFields.add("Invalid Action");
			}
			
		//Project Site
		}else if(object.getType().equalsIgnoreCase("Project Site")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else if(object.getAction().equals("Create Post")){
				if(!fields.verifyText(object.getTitle(), bodyFromDB)){
					//return false;
					failedFields.add("Text");
				}
			}else if(object.getAction().equals("Create Task") || object.getAction().equals("Edit Task")){
				if(!fields.verifyName(attributes, object.getName())){
					//return false;
					failedFields.add("Name");
				}
				if(!fields.verifyAssignedTo(attributes, object.getAssignedTo())){
					//return false;
					failedFields.add("Assigned To");
				}
				if(!fields.verifyPriority(attributes, object.getPriority())){
					//return false;
					failedFields.add("Priority");
				}
				if(!fields.verifyDueDate(attributes, object.getDueDate())){
					//return false;
					failedFields.add("Due Date");
				}
				if(!fields.verifyTaskStatus(attributes, object.getIssueStatus())){
					//return false;
					failedFields.add("Task Status");
				}
				if(!fields.verifyPercentageCompleted(attributes, object.getPercentageCompletion())){
					//return false;
					failedFields.add("Percentage Completed");
				}
				if(!fields.verifyText(object.getDescription(), bodyFromDB)){
					//return false;
					failedFields.add("Text");
				}
				if(!fields.verifyPredecessors(attributes, object.getRelatedIssues())){
					//return false;
					failedFields.add("Predecessors");
				}
			}
			
		//Verify Community Site	
		}else if(object.getType().equalsIgnoreCase("Community Site")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else if(object.getAction().equals("Create Discussion") || object.getAction().equals("Edit Discussion")){
				if(!fields.verifyText(object.getBody(), bodyFromDB)){
					//return false;
					failedFields.add("Text");
				}
				if(!fields.verifySubjectAsTitle(attributes, object.getSubject())){
					//return false;
					failedFields.add("Subject");
				}
				if(!fields.verifyIsQuestion(attributes, object.getIsQuestion())){
					//return false;
					failedFields.add("IsQuestion");
				}
			}else if(object.getAction().equals("Add Reply") || object.getAction().equals("Edit Reply")){
				if(!verifyParentDiscussion(object.getTime2(), object.getSubject(), object.getBody(), object.getIsQuestion())){
					//return false;
				}
				if(!fields.verifyText(object.getReply(), bodyFromDB)){
					//return false;
					failedFields.add("Text");
				}
			}else{
				System.out.println("Invalid Action");
				failedFields.add("Invalid Action");
				//return false;
			}
			
		//Verify Enterprise Wiki	
		}else if(object.getType().equalsIgnoreCase("Enterprise Wiki")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else if(object.getAction().equals("Create Page") || object.getAction().equals("Add Category")){
				if(object.getSubAction().equals("Content")){
					if(!verifyTextAndAttributeForPages(interID, object.getBody(), object.getName(), object.getCategory())){
						//return false;
					}
					if(!verifyFileInFileXfersTable(interID, object.getFile())){
						//return false;
						failedFields.add("File Name");
					}
				}else if(object.getSubAction().equals("Upload File")){
					if(!verifyTextAndAttributeForPages(interID, object.getBody(), object.getName(), object.getCategory())){
						//return false;
					}
					if(object.getFile() != null && !object.getFile().equals("")){
						if(!verifyFile(object.getTime2(), object.getFile(), object.getFileType())){
							//return false;
						}
					}
				}else{
					if(!fields.verifyName(attributes, object.getName())){
						//return false;
						failedFields.add("File Name");
					}
					if(!verifyFileInFileXfersTable(interID, object.getFile())){
						//return false;
						failedFields.add("File Name");
					}
				}
			}

		//Verify Microfeed	
		}else if(object.getType().equalsIgnoreCase("Microfeed")){
			if(object.getAction().equals("Create Post")){
				if(!fields.verifyTitleAsOriginalContent(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
			}else if(object.getAction().equals("Reply Post")){
				if(!fields.verifyReplyAsOriginalContent(attributes, object.getReply())){
					//return false;
					failedFields.add("Reply");
				}
				if(!verifyParentPost(object.getTime2(), object.getTitle())){
					//return false;
					failedFields.add("Parent Post");
				}
			}else{
				System.out.println("Invalid Action");
				failedFields.add("Invalid Action");
				//return false;
			}
			
		//Verify Enterprise Search Center	
		}else if(object.getType().equalsIgnoreCase("Enterprise Search Center")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else{
				System.out.println("Invalid Action");
				//return false;
				failedFields.add("Invalid Action");
			}
		
		//Verify Basic Search Center	
		}else if(object.getType().equalsIgnoreCase("Basic Search Center")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else{
				System.out.println("Invalid Action");
				failedFields.add("Invalid Action");
				//return false;
			}
			
		//Verify Visio Process Repository	
		}else if(object.getType().equalsIgnoreCase("Visio Process Repository")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else{
				System.out.println("Invalid Action");
				failedFields.add("Invalid Action");
				//return false;
			}
			
		//verify Publishing Site	
		}else if(object.getType().equalsIgnoreCase("Publishing Site")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else if(object.getAction().equals("Publish Site")){
				if(!fields.verifyTitle(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
			}else{
				System.out.println("Invalid Action");
				failedFields.add("Invalid Action");
				//return false;
			}
			
		//verify Publishing Site With Wrokflow	
		}else if(object.getType().equalsIgnoreCase("Publishing Site With Wrokflow")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else if(object.getAction().equals("Publish Site")){
				if(!fields.verifyTitle(attributes, object.getTitle())){
					//return false;
					failedFields.add("Title");
				}
			}else{
				System.out.println("Invalid Action");
				failedFields.add("Invalid Action");
				//return false;
			}
			
		}else if(object.getType().equals("Document Center")){	
			if(!fields.verifyTitle(attributes, object.getTitle())){
				//return false;
				failedFields.add("Title");
			}
			if(!fields.verifyFileName(attributes, object.getFile())){
				//return false;
				failedFields.add("File Name");
			}
			if(!verifyFileInFileXfersTable(interID, object.getFile())){
				//return false;
				failedFields.add("File Name");
			}
		
		}else if(object.getType().equalsIgnoreCase("Document Work Space")){
			if(object.getAction().equals("Create Site")){
				if(!fields.verifyFileLeafRef(attributes, object.getFile())){
					//return false;
					failedFields.add("Master Page Name");
				}
				if(!verifyFileInFileXfersTable(interID, object.getFile())){
					//return false;
					failedFields.add("File Name");
				}
			}else{
				//do nothing
			}
			
		//Invalid Type	
		}else {
			System.out.println("Invalid Type");
			failedFields.add("Invalid Type");
			//return false;
		}
		
		return true;
	}
	
	public boolean verifyTextAndAttributeForPages(int interID, String text, String title, String category) throws SQLException{
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
					failedFields.add("Title");
					//return false;
				}
				System.out.println("In: "+text);
				System.out.println("Ou: "+bodyFromDB);
				if(!bodyFromDB.contains(text)){
					System.out.println("Body did not match with the Database Value");
					failedFields.add("Text");
					//return false;
				}
				if(!fields.verifyCategoryAsWikiCategory(attributes, category)){
					failedFields.add("Category");
					//return false;
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
			failedFields.add("File Name");
			//return false;
		}
		if(fileType.equals(contentType)){
			System.out.println("File Type Matches with the Database Value");
		}else{
			System.out.println("File Type did not match with the Database Value");
			failedFields.add("File Type");
			//return false;
		}
		if(!verifyFileInFileXfersTable(interID, file)){
			failedFields.add("File Name");
			//return false;
		}
		return true;
	}
	
	public boolean verifyFileInFileXfersTable(int interID, String fileName) throws SQLException{
		String fileNameFromDB;
		String query = "select fileName from filexfers where networkID = 26 and interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			return false;
		}
		fileNameFromDB = rs.getString(1);
		if(fileNameFromDB.equals(fileName)){
			System.out.println("File Name in FileXfers Table Matches with the Input value");
		}else{
			System.out.println("File Name in FileXfers Table did not Match with the Input value");
			return false;
		}
		return true;
	}
	
	public boolean verifyParentPostForBlogComments(String time, String postTitle, String postPublished, String contentType) throws SQLException{
		String attributes;
		String query = "Select attributes from Messages where interID = (Select interID from interactions where startTime = "+time+
				" and contentType = (select typeID from contentTypes where value = '"+contentType+"' and networkID = 26))";
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			failedFields.add("Result Set was null");
			return false;
		}
		attributes = rs.getString(1);
		if(!fields.verifyTitle(attributes, postTitle)){
			failedFields.add("Title");
			//return false;
		}
		if(!fields.verifyPublishedDate(attributes, postPublished)){
			failedFields.add("Published Date");
			//return false;
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
			failedFields.add("Result Set was null");
			return false;
		}
		text = rs.getString(1);
		attributes = rs.getString(2);
		
		if(!fields.verifyText(body, text)){
			failedFields.add("Parent Text");
			//return false;
		}
		if(!fields.verifySubjectAsTitle(attributes, subject)){
			failedFields.add("Parent Subject");
			//return false;
		}
		if(!fields.verifyIsQuestion(attributes, isQuestion)){
			failedFields.add("Parent isQuestion");
			//return false;
		}
		return true;
	}
	
	public boolean verifyParentPost(String time, String title) throws SQLException{
		String attributes;
		String query = "Select attributes from Messages where interID = (Select top 1 interID from interactions where startTime = "+time+")";
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null");
			//failedFields.add("Title");
			return false;
		}
		attributes = rs.getString(1);
		if(!fields.verifyTitleAsOriginalContent(attributes, title)){
			return false;
		}
		return true;
	}
	
	public ArrayList<String> getFailedColumns(){
		return failedFields;
	}
}
