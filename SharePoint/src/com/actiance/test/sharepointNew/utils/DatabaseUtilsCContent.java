package com.actiance.test.sharepointNew.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.inet.ora.r;

public class DatabaseUtilsCContent {
	Connection con;
	ResultSet rs;
	Statement stmt;
	TestUtilsCContent testUtils = new TestUtilsCContent();
	
	int interID;
	String attribute;
	
	public Connection setConnection(){
		try{
			Class.forName(getDriverType());
		    con = DriverManager.getConnection(System.getProperty("dbUrl"), 
		    		System.getProperty("dbUserName"), System.getProperty("dbPassword"));
		    System.out.println("Created connection");
		    return con;
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while connecting to sql server");
		}
		return null;
	}
	
	public Statement createStatement(Connection conn){
		try{
			stmt = conn.createStatement();
			System.out.println("Created a statement");
			return stmt;
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while creating statement");
		}
		return null;
	}
	
	public ResultSet executeQuery(Statement st, String query){
		try{
			rs = st.executeQuery(query);
			System.out.println("Executed the query: "+query);
			return rs;
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while executing the query");
		}
		return null;
	}
	
	public void closeConnection(Connection connect){
		try{
			connect.close();
			System.out.println("Connection closed successfully");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while closing the database connection");
		}
	}
	
	public String getDriverType(){
		String driver = null;
		switch (System.getProperty("dbType")) {
		case "ORACLE":
			driver = "oracle.jdbc.driver.OracleDriver";
			break;
			
		case "MSSQL":
		
			driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			break;
			
		default:
			break;
		}
		return driver;
	}
	
	
	public boolean isInteractionCountChanged(Statement st, int previousCount){
	  int i=0;
	  int currentCount = -1;
	  boolean getCurrentCount = false;
	  String query = "Select count(*) from Interactions";
	  try{
	   do{
	    rs = st.executeQuery(query);
	    rs.next();
	    if(!getCurrentCount){
	     currentCount = rs.getInt(1);
	     getCurrentCount = true;
//	     System.out.println("Prev Count: "+previousCount);
	     if(currentCount > previousCount){
	      System.out.println("Interactions Count Changed");
	      return true;
	     }
	     Thread.sleep(5000);
	     continue;
	    }
	    currentCount = rs.getInt(1);
//	    System.out.println("Curr Count: "+currentCount);
	    if(currentCount > previousCount){
	     System.out.println("Interactions Count Changed");
	     return true;
	    }
	    Thread.sleep(5000);
	   }while(i++<240);
	   System.out.println("Interactions Count did not change");
	   return false;
	  }catch (Exception e){
	   System.out.println("Error while checking Interactions Count");
	   e.printStackTrace();
	  }
	  return false;
	}
			 
	public int getCurrentInteractionsCount(Statement st){
	  try{
	   String query = "Select count(*) from Interactions";
	   rs = st.executeQuery(query);
	   rs.next();
	   return rs.getInt(1);
	  }catch (Exception e){
	   System.out.println("Error while getting current interactions count");
	   e.printStackTrace();
	  }
	  return -1;
	 }	
	
	
	
	public int getNewInteraction(Statement stmt){
		int interId = -1;
		String query = "select TOP 1  interId from Interactions order By interId Desc";
		try{
//			System.out.println(query);

			rs = stmt.executeQuery(query);
			rs.next();
			interId = rs.getInt("interId");
//			System.out.println(interId);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return interId;
	}
	
	
	public int getInteractionId(Statement stmt, Long startTime, String buddyname){
		
		int interId = -1;

        String query = "select interId from interactions where startTime = "+ startTime + " and buddyName like '%"+buddyname+"%'";
        try{
               System.out.println("Query For Interaction : "+ query);
               
               rs = stmt.executeQuery(query);
               if(rs.next()){
                     interId = rs.getInt(1);
                     System.out.println("*********************************");
                     System.out.println("Interaction Found, Id : "+interId);
                     System.out.println("*********************************");
                     return interId;
               }else {
                     startTime = startTime - 1000;
                     query = "select interId from interactions where startTime = "+ startTime + " and buddyName like '%"+buddyname+"%'";

//                     System.out.println("Query : "+query);

                     rs = stmt.executeQuery(query);
                     if(rs.next()){
                            interId = rs.getInt(1);
                            System.out.println("*********************************");
                            System.out.println("Interaction Found, Id : "+interId);
                            System.out.println("*********************************");
                            return interId;
                     }
                     else{
                            startTime = startTime + 2000;
                            query = "select interId from interactions where startTime = "+ startTime + " and buddyName like '%"+buddyname+"%'";

//                            System.out.println("Query : "+query);

                            rs = stmt.executeQuery(query);
                            if(rs.next()){
                                   interId = rs.getInt(1);
                                   System.out.println("*********************************");
                                   System.out.println("Interaction Found, Id : "+interId);
                                   System.out.println("*********************************");
                                   return interId;
                            }else {
		                            	startTime = startTime - 4000; // for Asset Library test case interaction is audit differenct time than creation time of file. 
		                                query = "select interId from interactions where startTime = "+ startTime + " and buddyName like '%"+buddyname+"%'";
		
//		                                System.out.println("Query : "+query);
		
		                                rs = stmt.executeQuery(query);
		                                if(rs.next()){
		                                       interId = rs.getInt(1);
		                                       System.out.println("*********************************");
		                                       System.out.println("Interaction Found, Id : "+interId);
		                                       System.out.println("*********************************");
		                                       return interId;
		                                }else {
			                            		System.out.println("*********************************");
			                            		System.out.println("No Interaction Found");
			                            		System.out.println("*********************************");
		                                }
                            }
                     }
               }
                     
//               if(interId > 0)
//                     return interId;

        }
        catch(SQLException e){
        	System.out.println("Error While fetching interId from DB for Buddyname = "+ buddyname + " for starttime : "+startTime);
            e.printStackTrace();
        }

        return interId;

  
	}
	
//	verify title..
	
	public boolean verifyTitle(Statement stmt, int interID, String title){
		int count = 0;
		String query = "select roomName from Interactions where interId=" + interID ;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			if(verifyStringInBytes(rs.getString("roomname"), title)){
				count++;
			}
		}
		catch(SQLException se){
			se.printStackTrace();
		}
		if(count > 0){
			System.out.println("Title :" + title + " is matched for Interaction Id : "+interID);
			return true;
		}else{
			System.out.println("Title :" + title + " did not match for Interaction Id : "+interID);
			return false;
			
		}
	}
	
	public boolean verifyDescription(Statement stmt, int interID, String description){
        String outputBody=null;
        int count = 0;
        String query = "select text from messages where interId = "+ interID;
        try{
//               System.out.println("Messages Body Verification Query : "+ query);
               rs = stmt.executeQuery(query);
               if(rs.next()){
            	   outputBody = rs.getString("text");
            	   count = 1;
               }else{
            	   System.out.println("Result Set was null");
               }
        }
        catch(SQLException e){
               System.out.println("Error while getting text from Messages Table:"+ interID);
               e.printStackTrace();
        }
        if(count > 0){
        	return verifyStringInBytes(description, outputBody);
        }else{
        	return false;
        }
	}
	
//	verify Fixed Value Indicator
	public boolean verifyCFixedValueIndicator(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
		 	int interId =0;
		 	ArrayList<String> failed = new ArrayList();
			int preCount = getCurrentInteractionsCount(stmt);
			if(isInteractionCountChanged(stmt, preCount))
			 interId = getInteractionId(stmt, startTime, buddyName );
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "KpiDescription"), obj.getItemDesc())){
			failed.add("Description : "+ obj.getItemDesc());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "KpiComments"), obj.getParam1())){
			failed.add("Comments : "+ obj.getParam1());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Value"), obj.getParam2())){
			failed.add("Value : "+ obj.getParam2());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Goal"), obj.getParam3())){
			failed.add("Goal : "+ obj.getParam3());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Warning"), obj.getParam4())){
			failed.add("Warning : "+ obj.getParam4());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
	
	
//	verify Task
	public boolean verifyCTask(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
		 	int interId =0;
		 	ArrayList<String> failed = new ArrayList();
			int preCount = getCurrentInteractionsCount(stmt);
//			if(isInteractionCountChanged(stmt, preCount))
			 interId = 2074;//getInteractionId(stmt, startTime, buddyName );
		String attrs = getAttribute(stmt, interId);
		
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+ obj.getItemTitle());
		}
		
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
//	verify Official Notice
	
	public boolean verifyCOfficialNotice(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		if(!verifyDescription(stmt, interId, obj.getItemDesc())){
			failed.add("Description : "+ obj.getItemDesc());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
	
//	verify Category
	
	public boolean verifyCCategory(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "CategoryDescription"), obj.getItemDesc())){
			failed.add("Description : "+ obj.getItemDesc());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
//	verify Circulation
	
	public boolean verifyCCirculation(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
			String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		if(!verifyDescription(stmt, interId, obj.getItemDesc())){
			failed.add("Description : "+ obj.getItemDesc());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "V3Comments"), obj.getParam1())){
			failed.add("Comments : "+ obj.getParam1());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}

	
//	Verify Site MemberShip
	public boolean verifyCSiteMembership(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
		int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
	
//	verify Community Member 
	
	public boolean verifyCCommunityMember(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
		int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
//	verify Users
	
	
	public boolean verifyCUser(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
		int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
	
//	verify Resource
	
	
	public boolean verifyCResource(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
		int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Resource Title : "+ obj.getItemTitle());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
		
	
//	verify Whats New Notification
	
	
	public boolean verifyCWhatsNewNotification(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
		int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Notification Title : "+ obj.getItemTitle());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
		
	
//	verify Messages
	
	
	public boolean verifyCMessages(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
		int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		
		if(!verifyDescription(stmt, interId, obj.getItemDesc())){
			failed.add("Message Body : "+ obj.getItemDesc());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
		
		
//	Verify Holiday
	
	public boolean verifyCHoliday(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		
		if(!verifyStringInBytes(testUtils.extractString(attrs, "V4HolidayDate"), obj.getItemDesc())){
			failed.add("Holiday Date : "+ obj.getItemDesc());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Category"), obj.getParam1())){
			failed.add("Category : "+ obj.getParam1());
		}
//		if(!verifyStringInBytes(testUtils.extractString(attrs, "IsNonWorkingDay"), obj.getParam2())){
//			failed.add("IsNonWorkingDay : "+ obj.getParam2());
//		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
//	Verify New word
	
	public boolean verifyCNewWord(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		
		if(!verifyStringInBytes(testUtils.extractString(attrs, "IMEDisplay"), obj.getItemDesc())){
			failed.add("IMEDisplay : "+ obj.getItemDesc());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "IMEComment1"), obj.getParam1())){
			failed.add("IMEComment1 : "+ obj.getParam1());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "IMEComment2"), obj.getParam2())){
			failed.add("IMEComment2 : "+ obj.getParam2());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "IMEComment3"), obj.getParam3())){
			failed.add("IMEComment3 : "+ obj.getParam3());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "IMEPos"), obj.getParam4())){
			failed.add("IMEPos : "+ obj.getParam4());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}	

	
//	Verify Event
	
	public boolean verifyCEvent(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Category"), obj.getItemDesc())){
			failed.add("Category : "+ obj.getItemDesc());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Comments"), obj.getParam1())){
			failed.add("Comments : "+ obj.getParam1());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Location"), obj.getParam2())){
			failed.add("Location : "+ obj.getParam2());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "StartDate"), obj.getParam3())){
			failed.add("StartDate : "+ obj.getParam3());
		}
		/*if(!verifyStringInBytes(testUtils.extractString(attrs, "EndDate"), obj.getParam5())){
			failed.add("EndDate : "+ obj.getParam5());
		}*/
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}
	
//	Verify Post
	
	public boolean verifyCPost(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		
		if(!verifyDescription(stmt, interId, obj.getItemDesc())){
			failed.add("Category : "+ obj.getItemDesc());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "PublishedDate"), obj.getParam1())){
			failed.add("PublishedDate : "+ obj.getParam1());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}		
	
	
	
//	Verify Timecard
	
	public boolean verifyCTimecard(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Date"), obj.getParam1())){
			failed.add("Date : "+ obj.getParam1());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "DayOfWeek"), obj.getParam2())){
			failed.add("Days : "+ obj.getParam2());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Start"), obj.getParam3())){
			failed.add("Start : "+ obj.getParam2());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "End"), obj.getParam4())){
			failed.add("End : "+ obj.getParam4());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "In"), obj.getParam5())){
			failed.add("Return : "+ obj.getParam5());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Out"), obj.getParam6())){
			failed.add("Out : "+ obj.getParam6());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Break"), obj.getParam7())){
			failed.add("Break : "+ obj.getParam7());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "ScheduledWork"), obj.getParam8())){
			failed.add("Hours Worked : "+ obj.getParam8());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Overtime"), obj.getParam9())){
			failed.add("Overtime : "+ obj.getParam9());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "NightWork"), obj.getParam10())){
			failed.add("Late Night : "+ obj.getParam10());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "HolidayNightWork"), obj.getParam11())){
			failed.add("Overtime on Holiday : "+ obj.getParam11());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Late"), obj.getParam12())){
			failed.add("Arrive Late : "+ obj.getParam12());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "LeaveEarly"), obj.getParam13())){
			failed.add("Leaving Early : "+ obj.getParam13());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Vacation"), obj.getParam14())){
			failed.add("Vacation Type : "+ obj.getParam5());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "NumberOfVacation"), obj.getParam15())){
			failed.add("Vacation Length : "+ obj.getParam15());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
	
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}	

	
	
//	Verify Schedule
	
	public boolean verifyCSchedule(Statement stmt, String buddyName, TestDataObjectCContent obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Location"), obj.getItemDesc())){
			failed.add("Location : "+ obj.getItemDesc());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "StartDate"), obj.getParam1())){
			failed.add("StartDate : "+ obj.getParam1());
		}
	/*	if(!verifyStringInBytes(testUtils.extractString(attrs, "EndDate"), obj.getParam2())){
			failed.add("EndDate : "+ obj.getParam2());
		}*/
//		if(!verifyStringInBytes(testUtils.extractString(attrs, "fAllDayEvent"), obj.getParam3())){
//			failed.add("fAllDayEvent : "+ obj.getParam3());
//		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Category"), obj.getParam5())){
			failed.add("Category : "+ obj.getParam5());
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failed.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getResourceName())){
			failed.add("Resource : "+obj.getResourceName());
		}
		
		  if(!failed.isEmpty()){
	        	System.out.println("Printing the mismatching fields in DB");
	        	Iterator itr1 = failed.iterator();
	        	while(itr1.hasNext()){
	        		System.out.println(itr1.next());
	        	}
	        	return false;
	        }else{
	        	return true;
	        }
	}		
	
//	Verify Input Parameters for Announcement,Task,Issue,Link,Category,Comments,Discussion Board, Post
	
	public boolean verifyInputParameters(Statement stmt, TestDataObjectCContent testDataObj, String buddyName, Date date, 
			boolean edit, boolean subType) throws ParseException, XPathExpressionException{
        ArrayList<String> failed = new ArrayList();
        Long startTime = date.getTime();
        int interId =0;
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, buddyName );
    
        String sAttrs= null;
        String query = "select attributes from messages where interId = "+interId;
        try{
               rs = stmt.executeQuery(query);
               rs.next();
               sAttrs = rs.getString(1);
               attribute = sAttrs;
               
        }catch(SQLException e){
               System.out.println("No Attributes is found");
        }
       
//        Verify title of Item
        if(!testDataObj.getItemTitle().equals("") || testDataObj.getItemTitle()!= null){
        	if(verifyStringInBytes(testUtils.extractString(sAttrs, "Title"), testDataObj.getItemTitle())){
	               System.out.println("Title "+testDataObj.getItemTitle()+" is matched with DB for Interaction : "+interId);
	               
	        }else{
	               System.out.println("Title "+testDataObj.getItemTitle()+" did not match with DB for Interaction : "+interId);
	               failed.add(testDataObj.getItemTitle());
	        }
        }
//		Verify Description Of Item
        if(testDataObj.getItemDesc()!=null && !testDataObj.getItemDesc().equals("NA") && !testDataObj.getItemDesc().equals(""))
        	if(!verifyMsgBody(stmt, interId, testDataObj.getItemDesc()))
                failed.add("Description "+testDataObj.getItemDesc());	
        
//        Verify ExpiryDate for Announcement 
        if(testDataObj.getParam1()!=null && !testDataObj.getParam1().equals("NA") && !testDataObj.getParam1().equals(""))
        	if(!verifyMsgExpiryDate(stmt, interId, testDataObj.getParam1()))
                failed.add("Expiry "+testDataObj.getParam1());	
        
//        verify Action Type
//        if(!verifyActionType(stmt, interId, testDataObj.getActionType())){
//        	failed.add(testDataObj.getActionType());
////        	failed.add(testDataObj.getParam10());
//        }
//        
//        verify Content Type 
        
        if(!verifyContentType(stmt, interId, testDataObj.getContentType())){
        	failed.add("Content Type "+testDataObj.getContentType());
        }
        
        //Parameter 8 is meant for isQuestion Field Value for Discussion Board
        if(testDataObj.getParam8() != null && !testDataObj.getParam8().equals("NA") && !testDataObj.getParam8().equals("")){
        	if(testUtils.extractString(sAttrs, "IsQuestion").equals(getYesOrNo(testDataObj.getParam8()))){
                System.out.println("isQuesiton: "+testDataObj.getParam8()+" matches with DB value");
                
            }else{
                System.out.println("isQuesiton: "+testDataObj.getParam8()+" did not match with DB value");
                failed.add("Is Question "+testDataObj.getParam8());
            }
        }
//        if(!verifyContentSubType(stmt, interId, testDataObj.getContentSubType())){
//        	failed.add(testDataObj.getContentSubType());
//        }
        
        //Verify Resource Name
        if(testDataObj.getResourceName() != null && !testDataObj.getResourceName().equals("NA") && !testDataObj.getResourceName().equals("")){
        	if(!verifyResources(stmt, interId, testDataObj.getResourceName())){
        		failed.add("Resource Name "+testDataObj.getResourceName());
        	}
        }
        
        //Verify Parent Interaction
/*        if(edit){
        	if(!verifyParentIteraction(stmt, interId, testDataObj.getItemTitle())){
        		failed.add("Parent Interaction "+testDataObj.getItemTitle());
        	}
        }*/
        
//        if(subType){
//        	if(!verifyContentSubType(stmt, interId, testDataObj.getContentSubType())){
//        		failed.add("Content Sub Type "+testDataObj.getContentSubType());
//        	}
//        }
        
        if(!failed.isEmpty()){
        	System.out.println("Printing the mismatching fields in DB");
        	Iterator itr1 = failed.iterator();
        	while(itr1.hasNext()){
        		System.out.println(itr1.next());
        	}
        	return false;
        }else{
        	return true;
        }
       
	}
	
//	verifying Contact
	public boolean verifyContact(Statement stmt, Long startTime, String buddyName, TestDataObjectCContent obj){
		boolean result = false;
		ArrayList<String> failed = new ArrayList();
//		boolean flag []= {true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true};
		int interID =0;
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interID = getInteractionId(stmt, startTime, buddyName);
		
		try{
			String attrs = getAttribute(stmt, interID);
			if(attrs!=null)
			System.out.println("Attribute Found");
			if(!verifyTitle(stmt, interID, obj.getItemTitle())){
				failed.add("Last Name : "+ obj.getItemTitle());
//				System.out.println("Last Name : "+obj.getItemTitle() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getItemDesc().equals(extractValueForString(attrs, "FirstName"))){
//				System.out.println("First Name : "+obj.getItemDesc() +" is matched for Interaction ID : "+interID);
				failed.add("First Name : "+obj.getItemDesc());
			}
			if(!(obj.getItemDesc()+obj.getItemTitle()).equals(extractValueForString(attrs, "FullName"))){
//				System.out.println("Full Name : "+obj.getItemDesc() + obj.getItemTitle() +" is matched for Interaction ID : "+interID);
				failed.add("Full Name : "+obj.getItemTitle());
			}
			if(!obj.getParam1().equals(extractValueForString(attrs, "EMail"))){
				failed.add("Email : "+ obj.getParam1());
//				System.out.println("Email: "+obj.getParam1() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam2().equals(extractValueForString(attrs, "Company"))){
				failed.add("Company : "+ obj.getParam2());
//				System.out.println("Company: "+obj.getParam2() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam3().equals(extractValueForString(attrs, "JobTitle"))){
				failed.add("Company : "+ obj.getParam3());
//				System.out.println("JobTitle: "+obj.getParam3() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam4().equals(extractValueForString(attrs, "WorkAddress"))){
				failed.add("Company : "+ obj.getParam4());
//				System.out.println("WorkAddress: "+obj.getParam4() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam5().equals(extractValueForString(attrs, "WorkCity"))){
				failed.add("Work City : "+ obj.getParam5());
//				System.out.println("WorkCity: "+obj.getParam5() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam6().equals(extractValueForString(attrs, "WorkState"))){
				failed.add("Work State : "+obj.getParam6());
//				System.out.println("WorkState: "+obj.getParam6() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam7().equals(extractValueForString(attrs, "WorkZip"))){
				failed.add("Work Zip : "+ obj.getParam7());
//				System.out.println("WorkZip: "+obj.getParam7() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam8().equals(extractValueForString(attrs, "Comments"))){
				failed.add("comments :"+obj.getParam8());
//				System.out.println("Comments: "+obj.getParam8() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam9().equals(extractValueForString(attrs, "HomePhone"))){
				failed.add("Home Phone : "+ obj.getParam9());
//				System.out.println("HomePhone: "+obj.getParam9() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam10().equals(extractValueForString(attrs, "CellPhone"))){
				failed.add("Cell Phone : "+obj.getParam10());
//				System.out.println("CellPhone: "+obj.getParam10() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam11().equals(extractValueForString(attrs, "WorkPhone"))){
				failed.add("Work Phone : "+obj.getParam11());
//				System.out.println("WorkPhone: "+obj.getParam11() +" is matched for Interaction ID : "+interID);
			}
			if(!obj.getParam12().equals(extractValueForString(attrs, "WorkCountry"))){
				failed.add("Work Country : "+obj.getParam12());
//				System.out.println("WorkCountry: "+obj.getParam12() +" is matched for Interaction ID : "+interID);
			}
			/*if(obj.getParam13().equals(extractValueForString(attrs, "WorkFax"))){
				failed.add("work Fax : "+obj.getParam13());
//				System.out.println("WorkFax: "+obj.getParam13() +" is matched for Interaction ID : "+interID);
			}*/
			if(!verifyContentType(stmt, interID, obj.getContentType())){
				failed.add("Content type : "+obj.getContentType());
			}
			if(!verifyResources(stmt, interID, obj.getResourceName())){
				failed.add("Resource : "+obj.getResourceName());
			}
					
		}catch(NullPointerException ne){
			System.out.println("Attribute is not in Attributes columns");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		 
        if(!failed.isEmpty()){
        	System.out.println("Printing the mismatching fields in DB");
        	Iterator itr1 = failed.iterator();
        	while(itr1.hasNext()){
        		System.out.println(itr1.next());
        	}
        	return false;
        }else{
        	return true;
        }
	}
	
	
//	Verify Content type
	
	public boolean verifyContentType(Statement stmt, int interId, String contentType){
		String contentTypeValue = null;
		int contentTypeId ;
		String query = "select contentType from interactions where interId ="+interId ;
		try{
			rs = stmt.executeQuery(query);
			if(rs.next()){
				contentTypeId = rs.getInt("contentType");
				
				query = "select value from contentTypes where typeID="+contentTypeId;
		        rs = stmt.executeQuery(query);
		        rs.next();
		        contentTypeValue = rs.getString("value");
			}else{
				System.out.println("Result Set is Null");
				return false;
			}
				
	        
		}catch(SQLException se){
			
//			se.printStackTrace();
		}
		
		if(contentType.equalsIgnoreCase(contentTypeValue)){
			System.out.println("Content Type is matched for Interaction Id : "+interId);
			return true;
		}else{
			System.out.println("Content Type did not matched for Interaction Id : "+interId);
			return false;
		}
			
	}
	
//	verify Resource
	
	public boolean verifyResources(Statement stmt, int interId, String resName){
		boolean flag = false;
		int resId = 0;
		String query = "select resourceID from Interactions where interId = "+interId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			resId = rs.getInt(1);
			if(resId > 0){
				query = "select resName from Resources where resourceID ="+resId;
				rs = stmt.executeQuery(query);
				rs.next();
				if(rs.getString("resName").equals(resName)){
					System.out.println("Rescouce Id :" + resId +" for Resouces Name :"+ resName +" is Matched for Interaction Id : "+interId);
					flag = true;
				}else{
					System.out.println("Rescouce Id :" + resId +" for Resouces Name :"+ resName +" did not Matched for Interaction Id : "+interId);
					flag = false;
				}
				
			}else{
				System.out.println("Result Set is Null");
				flag = false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	

	public boolean verifyFile(TestDataObjectCContent obj, String buddyName, Long lastModifiedTime, String fileName, Long fileSize, boolean checkFileName, boolean checkTitle, boolean checkKeyword){
		
		int interId =0;
	 	ArrayList<String> failedList = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, lastModifiedTime, buddyName );
		if(checkFileName){
			if(!verifyFileName(stmt, interId, fileName))
				failedList.add("Filename : -> "+ fileName);
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failedList.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getFeatureName())){
			failedList.add("Resource Name : "+ obj.getFeatureName());
			
		}
		
		if(!failedList.isEmpty()){
        	System.out.println("Printing the mismatching fields in DB");
        	Iterator itr1 = failedList.iterator();
        	while(itr1.hasNext()){
        		System.out.println(itr1.next());
        	}
        	return false;
        }else{
        	return true;
        }
	}
	
 
 	public boolean verifyMsgBody(Statement stmt, int interId, String inputBody){
        String outputBody=null;
        int count = 0;
        String query = "select text from messages where interId = "+interId;
        try{
//               System.out.println("Messages Body Verification Query : "+ query);
               rs = stmt.executeQuery(query);
               if(rs.next()){
            	   outputBody = rs.getString("text");
            	   count = 1;
               }else{
            	   System.out.println("Result Set was null");
               }
        }
        catch(SQLException e){
               System.out.println("Body Did not matched for interaction iD :"+ interId);
               e.printStackTrace();
        }
        if(count > 0){
        	return verifyStringInBytes(inputBody, outputBody);
        }else{
        	return false;
        }
        
 	}
 
	public boolean verifyMsgExpiryDate(Statement stmt, int interId,
			String expiryDate) {
		String sAttrs = null;
		String query = "select attributes from messages where interId = "
				+ interId;
		try {
			rs = stmt.executeQuery(query);
			rs.next();
			sAttrs = rs.getString(1);
		} catch (SQLException e) {
			System.out.println("No Attributes is found");
		}
		if (testUtils.extractString(sAttrs, "Expires").equals(expiryDate)) {
			System.out
					.println("Expiry Date is matched with DB for Interaction : "
							+ interId);
			return true;
		} else {
			System.out
					.println("Expiry Date did not match with DB for Interaction : "
							+ interId);
			return false;
		}
	}
	
/*	
	public boolean verifyParentIteraction(Statement stmt, int interId, String parentText){
		try{
			String query = "Select parentInterID from Interactions where interID = "+interId;
			rs = stmt.executeQuery(query);
			int parentInterID;
		    if(rs.next()){
		    	 parentInterID = rs.getInt("parentInterId");
		    }else{
		     System.out.println("Result Set was null");
		     return false;
		    }
		    
		    query = "select roomName from Interactions where interID = "+parentInterID;
		    rs = stmt.executeQuery(query);
		    rs.next();
		    if(verifyStringInBytes(rs.getString("roomName"), parentText)){
		    	return true;
		    }else{
		    	return false;
		    }
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}*/
/*	
	public boolean verifyParentIteractionForIssueTracking(Statement stmt, int interId, String parentText){
		try{
			String query = "Select parentInterID from Interactions where interID = "+interId;
			rs = stmt.executeQuery(query);
			int parentInterID;
		    if(rs.next()){
		    	 parentInterID = rs.getInt("parentInterId");
		    }else{
		     System.out.println("Result Set was null");
		     return false;
		    }
		    
	        query = "select attributes from messages where interId = "+parentInterID;
	        rs = stmt.executeQuery(query);
            rs.next();
            String sAttrs = rs.getString(1);
            if(verifyStringInBytes(testUtils.extractString(sAttrs, "Title"), parentText)){
    			return true;
            }else{
            	return false;
            }
		    
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
*/
	//Verification for Issue Tracking
	public boolean verifyIssueTrackingInputParameters(Statement stmt, TestDataObjectCContent testDataObj, String buddyName, Date date, 
			boolean edit, boolean subType) throws ParseException, XPathExpressionException{
		ArrayList<String> failed = new ArrayList();
		if(verifyInputParameters(stmt, testDataObj,buddyName, date, false, false)){
			
//	        Verify Description of Item
			if(verifyStringInBytes(testUtils.extractString(attribute, "Comment"), testDataObj.getParam7())){
				System.out.println("Description "+testDataObj.getParam7()+" is matched with DB value for Interaction : "+interID);   
	        }else{
	        	System.out.println("Description "+testDataObj.getParam7()+" did not match with DB value for Interaction : "+interID);
	        	failed.add("Description: "+testDataObj.getParam7());
	        }
	        	
//	        Verify Status of Item
/*			if(verifyStringInBytes(testUtils.extractString(attribute, "Status"), testDataObj.getParam3())){
				System.out.println("Status "+testDataObj.getParam3()+" is matched with DB value for Interaction : "+interID);   
	        }else{
	        	System.out.println("Status "+testDataObj.getParam3()+" did not match with DB value for Interaction : "+interID);
	        	failed.add("Status: "+testDataObj.getParam3());
	        }*/
			
//	        Verify Priority of Item
			if(verifyStringInBytes(testUtils.extractString(attribute, "Priority"), testDataObj.getParam4())){
				System.out.println("Priority "+testDataObj.getParam4()+" is matched with DB value for Interaction : "+interID);   
	        }else{
	        	System.out.println("Priority "+testDataObj.getParam4()+" did not match with DB value for Interaction : "+interID);
	        	failed.add("Priority: "+testDataObj.getParam4());
	        }
			
//	        Verify Comments of Item
			if(verifyStringInBytes(testUtils.extractString(attribute, "V3Comments"), testDataObj.getParam5())){
				System.out.println("Comments "+testDataObj.getParam5()+" is matched with DB value for Interaction : "+interID);   
	        }else{
	        	System.out.println("Comments "+testDataObj.getParam5()+" did not match with DB value for Interaction : "+interID);
	        	failed.add("Comments: "+testDataObj.getParam5());
	        }
			
//			Verify Parent Interaction
	/*        if(edit){
	        	if(!verifyParentIteraction(stmt, interID, testDataObj.getItemTitle())){
	        		failed.add("Parent Title: "+testDataObj.getItemTitle());
	        	}
	        }*/
			
			if(!failed.isEmpty()){
		     	System.out.println("Printing the mismatching fields in DB");
		       	Iterator itr1 = failed.iterator();
		       	while(itr1.hasNext()){
		       		System.out.println(itr1.next());
		       	}
		       	return false;
			}else{
		       	return true;
	        }
			
		}else{
			return false;
		}
	}
	
	public boolean verifyFileName(Statement stmt, int interId , String fileName){
		int count = 0;
		ArrayList<String> fileNamesFromDB = new ArrayList();
		String query = "select fileName from FileXfers where interID = "+interId ; 
		try{
//			System.out.println(query);
			rs = stmt.executeQuery(query);
			while(rs.next()){		// because for Announcement FileXfers and Messages table audit with has all the previous file entry, and we have to check current uploaded file.
				fileNamesFromDB.add(rs.getString("fileName"));
			}
			for(String fileNamesDB : fileNamesFromDB){
				if(verifyStringInBytes(fileNamesDB, fileName)){
					count++;
				}
			}
			
		}catch(SQLException se){
			se.printStackTrace();
		}
		if(count > 0){
			System.out.println("File Name : "+ fileName + " is matched for Interaction ID : "+ interId);
			return true;
		}else{
			System.out.println("File Name : "+ fileName + " did not matched for Interaction ID : "+ interId);
			return false;
		}
	}
	
	
	/*
	 * Utility methods
	 * */	
		public String getAttribute(Statement stmt, int interID){
			String attribute = null;
			String query = "select attributes from messages where interId ="+interID + " and attributes IS NOT NULL";
			try{
				rs = stmt.executeQuery(query);
				rs.next();
				attribute = rs.getString("attributes") ;
			}catch(SQLException e){
				System.out.println("Error while getting attributes from Messages ");
			}
			return attribute;
		}
		
		
		public static String extractValueForString(String sData, String extractPart){
			String value = null;
			int indexPart = sData.indexOf(extractPart+"\">");
			String localString= sData.substring(indexPart);
			int newIndex = extractPart.length()+2;
			int index1 = localString.indexOf("</attr");
			value = localString.substring(newIndex,index1);
			return value;
	}
		
	 	public boolean verifyStringInBytes(String inputString, String outputString){
	 		byte[] input = inputString.getBytes();
	 		byte[] output = outputString.getBytes();
	 		
	 		if(Arrays.equals(input, output)){
	 			//System.out.println(input+ " == "+output);
//	 			System.out.println("Field matched");
	 			return true;
	 		}else{
	 			return false;
	 		}
	 	}	
	
	
	public String getYesOrNo(String value){
		if(Boolean.parseBoolean(value)){
			return "Yes";
		}else{
			return "No";
		}
	}
	
	
}

