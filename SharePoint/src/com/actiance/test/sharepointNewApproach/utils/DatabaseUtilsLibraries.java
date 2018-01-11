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
import com.adventnet.snmp.snmp2.agent.SysOREntry;

public class DatabaseUtilsLibraries {
	Connection con;
	ResultSet rs;
	Statement stmt;
	private ArrayList<String> failed ;
	
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
	
	public String getContentSubType(String typeID) throws SQLException{
		String contentSubType;
		if(typeID.equals("-1")){
			return null;
		}else{
			String query = "Select value from contentSubTypes where typeID = "+typeID;
//			System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.next();
			contentSubType = rs.getString("value");
			return contentSubType;
		}
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
	
	public boolean verifyDatabase(CSVObjectsLibraries object, ArrayList<String> DBValues) throws NumberFormatException, SQLException{
		//Verify Content Type
		failed = new ArrayList();
		//System.out.println(object.getContentType());
//		System.out.println("DB : "+ getContentType(DBValues.get(2)));
//		System.out.println("Input : "+object.getContentType());
		if(object.getContentType().equals(getContentType(DBValues.get(2)))){
			System.out.println("Content Type Matches with the Database Value");
		}else{
			System.out.println("Content Type did not match with the Database Value");
			failed.add("Content Type");
//			return false;
		}
		//Verify Content Sub Type
//		System.out.println("DB : "+ getContentSubType(DBValues.get(3)));
//		System.out.println("Input : "+object.getContentSubType());
		if(object.getContentSubType() != null && !object.getContentSubType().equals("")){
			if(object.getContentSubType().equals(getContentSubType(DBValues.get(3)))){
				System.out.println("Content Sub Type Matches with the Database Value");
			}else{
				System.out.println("Content Sub Type did not match with the Database Value");
				failed.add("Content Sub Type");
//				return false;
			}
		}
		//Verify Action Type
//		System.out.println("DB : "+ getActionName(DBValues.get(5)));
//		System.out.println("Input : "+object.getActionType());
		if(object.getActionType().equals(getActionName(DBValues.get(5)))){
			System.out.println("Action Type Matches with the Database Value");
		}else{
			System.out.println("Action Type did not match with the Database Value");
			failed.add("Action Type");
			
		}
		//Verify Resource Name and URL
	
		ArrayList<String> resNameAndURL = getResourceNameAndURL(DBValues.get(4));
//		System.out.println("DB : "+ resNameAndURL.get(0));
//		System.out.println("Input : "+object.getResourceName());
		if(object.getResourceName().equals(resNameAndURL.get(0))){
			System.out.println("Resource Name Matches with the Database Value");
		}else{
			System.out.println("Resource Name did not match with the Database Value");
			failed.add("Resource Name");
		}
		
//		System.out.println("DB : "+ resNameAndURL.get(1));
//		System.out.println("Input : "+object.getResourceURL());
		if(("/"+object.getResourceURL().replaceAll(" ", "%20")).equals(resNameAndURL.get(1))){
//			System.out.println(object.getResourceURL());
			System.out.println("Resource URL Matches with the Database Value");
		}else{
			System.out.println("Resource URL did not match with the Database Value");
			failed.add("resource URL");
		}
		
		if(!verifyMessageTable(Integer.parseInt(DBValues.get(0)), object)){}
			
		if(failed.size() == 0){
			   return true;
		}else{
		  return false;
		 }
	}
//		return true;		
	
	
	public boolean verifyMessageTable(int interID, CSVObjectsLibraries object) throws SQLException{
		String bodyFromDB;
		String attributes;
		
			try{
				String query = "Select text, attributes from Messages where interID = "+ interID  ;
				System.out.println(query);
				rs = stmt.executeQuery(query);
				if(!rs.next()){
					System.out.println("Result Set was null");
					failed.add("Result Set was null");
		//			return false;
				}
				bodyFromDB = rs.getNString(1);
				attributes = rs.getString(2);
				
				//Verification for Form Library
				if(object.getType().equalsIgnoreCase("Form library")){
					
					if(object.getName() != null && !object.getName().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Name", object.getName())){
							failed.add("Name");
						}
					}
					if(!verifyFileInFileXfersTable(interID, object.getFile())){
						failed.add("File");
					}
					
				//Verify Document Library	
				}else if(object.getType().equalsIgnoreCase("Document library")){
//					System.out.println("In Doc");
					/*if(!getRoomName(stmt, interID).equals(object.getName())){
						System.out.println("Name did not Matched");
						failed.add("Title");
					}else{
						System.out.println("Name matched");
					}*/
					if(object.getFile() != null && !object.getFile().equals("")){
						if(!verifyFileInFileXfersTable(interID, object.getFile())){
							failed.add("Filename");
						}
					}
					if(!object.getTitle().equals("") && object.getTitle()!= null){
						if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
							failed.add("Name");
						}
					}
		//			if(object.getName() != null && !object.getName().equals("")){
		//				if(!fields.verifyAttributeValue(attributes, "FileName", object.getName())){
		//					failed.add("Name");
		//				}
		//			}
					
				//Verify Picture Library	
				}else if(object.getType().equalsIgnoreCase("Picture Library")){
					if(object.getName() != null && !object.getName().equals("")){
						if(!fields.verifyAttributeValue(attributes, "FileName", object.getName())){
							failed.add("Name");
						}
					}
					if(object.getTitle() != null && !object.getTitle().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
							failed.add("Title");
						}
					}
					if(object.getKeyword() != null && !object.getKeyword().equals("")){
						System.out.println(attributes);
						if(!fields.verifyAttributeValue(attributes, "Keywords", object.getKeyword())){
							failed.add("Keyword");
						}
					}
					if(object.getDatePictureTaken() != null && !object.getDatePictureTaken().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Date Of Picture Taken", object.getDatePictureTaken())){
							failed.add("Date Of Picture Taken");
						}
					}
					if(object.getComments() != null && !object.getComments().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Description", object.getComments())){
							failed.add("Description");
						}
					}
					if(!verifyFileInFileXfersTable(interID, object.getFile())){
						failed.add("Filename");
					}
		//			Verify Data Connection Library
				}else if(object.getType().equalsIgnoreCase("Data connection library")){
					if(object.getName() != null || !object.getName().equals("")){
						
						if(!fields.verifyAttributeValue(attributes, "FileName", object.getName())){
							failed.add("Name");
						}
					}
					if(object.getTitle() != null || !object.getTitle().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
							failed.add("Title");
						}
					}
					if(object.getKeyword() != null || !object.getKeyword().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Keywords", object.getKeyword())){
							failed.add("Keyword");
						}
					}
					if(object.getDescription() != null || !object.getDescription().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
							failed.add("Description");
						}
					}
					if(object.getComments() != null || !object.getComments().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Description", object.getComments())){
							failed.add("Comment");
						}
					}
					if(object.getConnectionType() != null || !object.getConnectionType().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Connection Type", object.getConnectionType())){
							failed.add("Connection Type");
						}
					}
					if(object.getUDCPurpose() != null || !object.getUDCPurpose().equals("")){
						if(!fields.verifyAttributeValue(attributes, "UDC Purpose", object.getUDCPurpose())){
							failed.add("UDC Purpose");
						}
					}
					System.out.println(object.getFile());
					if(!verifyFileInFileXfersTable(interID, object.getFile())){
						failed.add("Filename");
					}
		//			Verify Report Library
				}else if(object.getType().equalsIgnoreCase("Report Library")){
					if(object.getName() != null && !object.getName().equals("")){
						if(!fields.verifyAttributeValue(attributes, "FileName", object.getName())){
							failed.add("Name");
						}
					}
					if(object.getTitle() != null && !object.getTitle().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle())){
							failed.add("Title");
						}
					}
					if(object.getDescription() != null && !object.getDescription().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Description", object.getDescription())){
							failed.add("Description");
						}
					}
					if(object.getReportCategory() != null && !object.getReportCategory().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Category", object.getReportCategory())){
							failed.add("Category");
						}
					}
					if(object.getReportStatus() != null && !object.getReportStatus().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Status", object.getReportStatus())){
							failed.add("ReportStatus");
						}
					}
					if(object.getAuthor() != null && !object.getAuthor().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Owner", object.getAuthor())){
							failed.add("Owner");
						}
					}
					if(!object.getFile().equals("") && object.getFile()!= null){
						if(!verifyFileInFileXfersTable(interID, object.getFile())){
							failed.add("Filename");
						}
					}
					
		//			Wiki Page Library
				}else if(object.getType().equalsIgnoreCase("Wiki page library") || object.getType().equalsIgnoreCase("Wiki page")){
					if(object.getName() != null && !object.getName().equals("")){
						if(!fields.verifyAttributeValue(attributes, "FileName", object.getName())){
							failed.add("Name");
						}
					}
					if(object.getDescription() != null && !object.getDescription().equals("")){
						if(!bodyFromDB.equals(object.getTitle())){
							failed.add("Title");
						}
					}
					if(object.getBody() != null && !object.getBody().equals("")){
						if(!fields.isEqual(bodyFromDB, object.getBody())){
							System.out.println("body did not match");
							failed.add("Body");
						}else{
							System.out.println("Body Matched");
						}
					}
					if(!object.getFile().equals("") && object.getFile() !=null){
						if(!verifyFileInFileXfersTable(interID, object.getFile())){
							failed.add("Filename");
						}
					}
					
					
		//			Asset Library
				}else if(object.getType().equalsIgnoreCase("Asset library")){
				
					if(object.getBody() != null && !object.getBody().equals("")){
						ArrayList<String> messages = getTextFromDB(stmt, interID);
						bodyFromDB = messages.get(0);
						attributes = messages.get(1);
						}
					
					if(object.getTitle() != null && !object.getTitle().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Title", object.getTitle().trim())){
							failed.add("Title");
						}
					}
					if(object.getName() != null && !object.getName().equals("")){
						if(!fields.verifyAttributeValue(attributes, "FileName", object.getName().trim())){
							failed.add("Name");
						}
					}
				
					if(object.getComments() != null && !object.getComments().equals("")){
						if(!bodyFromDB.equals(object.getComments().trim())){
							failed.add("Comments");
						}
					}
					if(object.getKeyword() != null && !object.getKeyword().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Keywords", object.getKeyword().trim())){
							failed.add("Name");
						}
					}
					if(object.getDatePictureTaken() != null && !object.getDatePictureTaken().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Date Of Picture Taken", object.getDatePictureTaken().trim())){
							failed.add("Date Of Picture Taken");
						}
					}
					if(object.getAuthor() != null && !object.getAuthor().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Author", object.getAuthor().trim())){
							failed.add("Owner");
						}
					}
					if(object.getCopyright() != null && !object.getCopyright().equals("")){
						if(!fields.verifyAttributeValue(attributes, "Copyright", object.getCopyright().trim())){
							failed.add("Copyright");
						}
					}
					if(!object.getFile().equals("") && object.getFile() !=null){
						if(!verifyFileInFileXfersTable(interID, object.getFile().trim())){
							failed.add("Filename");
						}
					}
					
					
				}
				else{
					System.out.println("Invalid Type");
					return false;
				}
			}catch(Exception e){
//				e.printStackTrace();
//				System.out.println();
			}
		return true;
		
	}
	
	public boolean verifyFileInFileXfersTable(int interID, String fileName) throws SQLException{
		String fileNameFromDB;
		String query = "select fileName from filexfers where networkID = 26 and interID = "+interID;
		System.out.println(query);
		rs = stmt.executeQuery(query);
		if(!rs.next()){
			System.out.println("Result Set was null For File");
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
	
	
	public String getRoomName(Statement stmt, int interId){
		try{
			rs= stmt.executeQuery("select roomName from Interactions where interId ="+ interId);
			rs.next();
			System.out.println();
			String name = rs.getString("roomName");
			return name;
		}catch(SQLException se){
			System.out.println("No room Name Found");
		}
		return null;
	}
	
	public ArrayList<String> getFailedColumns(){
		  return failed;
		 }
	
	public ArrayList<String> getTextFromDB(Statement stmt, int interId){
		ArrayList<String> textAndAttr = new ArrayList();
		String query = "select text , attributes from messages where interId="+ interId + " and text IS NOT NULL";
		try{
			rs = stmt.executeQuery(query);
			if(rs != null){
				System.out.println(query);
				rs.next();
				textAndAttr.add(rs.getNString("text"));	
				textAndAttr.add(rs.getNString("attributes"));
			}else{
				query = "select text , attributes from messages where interId="+ interId;
				System.out.println(query);
				rs = stmt.executeQuery(query);
				rs.next();
				textAndAttr.add(rs.getNString("text"));	
				textAndAttr.add(rs.getNString("attributes"));
//				System.out.println("Result Set is null");
			}
		}catch(Exception se){
			System.out.println("No Data Found123");
		}
		return textAndAttr;
	}
}
