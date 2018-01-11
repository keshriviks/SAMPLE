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

public class DatabaseUtils {
	Connection con;
	ResultSet rs;
	Statement stmt;
	TestUtils testUtils = new TestUtils();
	
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
	
	
	public boolean verifyAnnouncement(TestDataObject testDataObject, Statement stmt)
	{
		
		String interID = null;
		
	//	String query = "select * from interactions"
		
		return true;
		
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
		     System.out.println("Prev Count: "+previousCount);
		     if(currentCount > previousCount){
		      System.out.println("Interactions Count Changed");
		      return true;
		     }
		     Thread.sleep(5000);
		     continue;
		    }
		    currentCount = rs.getInt(1);
//		    System.out.println("Curr Count: "+currentCount);
		    if(currentCount > previousCount){
		     System.out.println("Interactions Count Changed");
		     return true;
		    }
		    Thread.sleep(5000);
		   }while(i++<300);
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
		
	
	public boolean verifyInputParameters(Statement stmt, TestDataObject testDataObj, Date date, 
			boolean edit, boolean subType) throws ParseException, XPathExpressionException{
        ArrayList<String> failed = new ArrayList();
        Long startTime = date.getTime();
        int interId =0;
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, testDataObj.getUserName() );
        
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
        if(testDataObj.getItemDescription()!=null && !testDataObj.getItemDescription().equals("NA") && !testDataObj.getItemDescription().equals(""))
        	if(!verifyMsgBody(stmt, interId, testDataObj.getItemDescription()))
                failed.add("Description "+testDataObj.getItemDescription());	
        
//        Verify ExpiryDate for Announcement 
        if(testDataObj.getExpiry()!=null && !testDataObj.getExpiry().equals("NA") && !testDataObj.getExpiry().equals(""))
        	if(!verifyMsgExpiryDate(stmt, interId, testDataObj.getExpiry()))
                failed.add("Expiry "+testDataObj.getExpiry());	
        
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
        if(edit){
        	if(!verifyParentIteraction(stmt, interId, testDataObj.getItemTitle())){
        		failed.add("Parent Interaction "+testDataObj.getItemTitle());
        	}
        }
        
        if(subType){
        	if(!verifyContentSubType(stmt, interId, testDataObj.getContentSubType())){
        		failed.add("Content Sub Type "+testDataObj.getContentSubType());
        	}
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

                     System.out.println("Query : "+query);

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

                            System.out.println("Query : "+query);

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
		
		                                System.out.println("Query : "+query);
		
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
 
 	public boolean verifyStringInBytes(String inputString, String outputString){
 		byte[] input = inputString.getBytes();
 		byte[] output = outputString.getBytes();
 		
 		if(Arrays.equals(input, output)){
 			//System.out.println(input+ " == "+output);
 			return true;
 		}else{
 			return false;
 		}
 	}
 
 
 	public boolean verifyMsgBody(Statement stmt, int interId, String inputBody){
        String outputBody=null;
        int count = 0;
        String query = "select text from messages where interId = "+interId;
        try{
               System.out.println("Messages Body Verification Query : "+ query);
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
	
	public boolean verifyActionType(Statement stmt, int interId, String actionType) throws XPathExpressionException{
		SQLXML interAttributes;
		try{
			String query = "select contentType, attributes from Interactions where interID="+interId;
			rs = stmt.executeQuery(query);
			if(rs.next()){
//				int contentID = rs.getInt("contentType");
				interAttributes = rs.getSQLXML("attributes");
				XPath xpath = XPathFactory.newInstance().newXPath();
				DOMSource domSource = interAttributes.getSource(DOMSource.class);
		        Document document = (Document) domSource.getNode();
		        String expression = "//name[.='event.action']/following-sibling::*[1][name()='value']";
		        String actionID = xpath.evaluate(expression, document);
				
//		        query = "select value from contentTypes where typeID="+contentID;
//		        rs = stmt.executeQuery(query);
//		        rs.next();
//		        String contentValue = rs.getString("value");
		        
		        query = "select name from ActionTypes where actionID="+actionID;
		        rs = stmt.executeQuery(query);
		        rs.next();
		        String actionName = rs.getString("name");
		        
		        if(actionName.equals(actionType)){
		        	System.out.println("Action Type values is matching for Interaction Id : "+interId);
		        	return true;
		        }else{
		        	System.out.println("Action Type values did not matching for Interaction Id : "+interId);
		        	return false;
		        }
		        
			}else{
				System.out.println("Result Set was null");
				return false;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
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
	}
	
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
	
	public boolean verifyContentSubType(Statement stmt, int interId, String type){
		try{
			String query = "Select contentSubType from Interactions where interID = "+interId;
			rs = stmt.executeQuery(query);
		    rs.next();
		    int subTypeId = rs.getInt("contentSubType");
		    query = "select value from contentSubTypes where typeID = "+subTypeId;
//		    System.out.println(query);
//		    System.out.println();
		    rs = stmt.executeQuery(query);
		    rs.next();
		    String Value = rs.getString("value");
		    if(rs.getString("value").equalsIgnoreCase(type)){
		    	System.out.println("ContentSubType is matched for Interactions Id : "+interId);
		    	return true;
		    }else{
		    	System.out.println("ContentSubType did not matched for Interactions Id : "+interId);
		    	return false;
		    }
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean verifyFile(TestDataObject obj , Long lastModifiedTime, String fileName, Long fileSize, boolean checkFileName, boolean checkTitle, boolean checkKeyword){
		
		ArrayList<String> failedList = new ArrayList();
		boolean flag = false;
//		int interId = getNewlyCreatedInteractionID(stmt, interID);
//		int interId = getInteractionId(stmt, lastModifiedTime, obj.getUserName());
		
		 int interId =0;
			int preCount = getCurrentInteractionsCount(stmt);
			if(isInteractionCountChanged(stmt, preCount))
			 interId = getInteractionId(stmt, lastModifiedTime, obj.getUserName());
		
		if(checkFileName){
			if(!verifyFileName(stmt, interId, fileName))
				failedList.add("Filename : -> "+ fileName);
		}
		if(checkTitle){
			if(obj.getParam7()!=null || !obj.getParam7().equals("") || !obj.getParam7().equals("NA")){
				if(!verifyTitleForFile(stmt, interId, obj.getParam7()))
					failedList.add("Title : ->" + obj.getParam7());
			}
		}
		if(checkKeyword){
			if(obj.getParam7()!=null || !obj.getParam7().equals("") || !obj.getParam7().equals("NA")){
				if(!verifyKeywordForFile(stmt, interId, obj.getParam7()))
					failedList.add("Title : ->" + obj.getParam7());
			}
		}
		if(!verifyContentType(stmt, interId, obj.getContentType())){
			failedList.add("Content Type : "+ obj.getContentType());
		}
		if(!verifyResources(stmt, interId, obj.getSiteTitle())){
			failedList.add("Resource Name : "+ obj.getSiteTitle());
			
		}
		if(!verifyContentSubType(stmt, interId, obj.getContentSubType())){
			failedList.add("Content sub type : "+ obj.getContentSubType());
			
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
		
//		boolean flag = false;
//		int count = 0;
//		int interId = getInteractionId(stmt, lastModifiedTime, username);
//		String query = "select count(*) from FileXfers where interID = "+interId + " and fileName = '"+fileName+"'"; //and fileSize ="+fileSize;
//		try{
////			System.out.println(query);
//			rs = stmt.executeQuery(query);
//			rs.next();
//			count = rs.getInt(1);
//		}catch(SQLException se){
//			se.printStackTrace();
//		}
//		if(count > 0){
//			System.out.println("File Name : "+ fileName + " is matched for Interaction ID : "+ interId);
//			flag = true;
//		}else{
//			System.out.println("File Name : "+ fileName + " did not matched for Interaction ID : "+ interId);
//			flag = false;
//		}
//		if(flag && verifyResources(stmt, interId, siteTitle)){// && verifyContentType(stmt, interId, contentType) && verifyContentSubType(stmt, interId, contentSubType)){
//			return true;
//		}else{
//			return false;
//		}
	}
	
	
//	verify Interaction Type....
	
	public int getTopMostInteractionID(Statement stmt){
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
	
	
	public int getNewlyCreatedInteractionID(Statement stmt , int interId){
//		int interId1 = -1;
		int interId2 = -1;
		int i = 0 ;
		try{
//			interId1 = getInteractions(stmt);
			
			
			do{
				Thread.sleep(5000);
				interId2 = getTopMostInteractionID(stmt);
				i++;
				
			}while(interId == interId2 );//|| i < 30);
			if(i == 30){
				System.out.println("New Interaction is not audited");
			}
			if(interId2!=-1){
				System.out.println("Interaction Id :" + interId2);
			}
		}catch(Exception e) {
			System.out.println("Error in Fetching.....");
		}
		return interId2;
	}
	
//	verify Resource Id and Name ..
	public boolean verifyResources(Statement stmt, int interId, String containerName){
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
				if(rs.getString("resName").equals(containerName)){
					System.out.println("Rescouce Id :" + resId +" for Resouces Name :"+ containerName +" is Matched for Interaction Id : "+interId);
					flag = true;
				}else{
					System.out.println("Rescouce Id :" + resId +" for Resouces Name :"+ containerName +" did not Matched for Interaction Id : "+interId);
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
	
	//Verification for Issue Tracking
	public boolean verifyIssueTrackingInputParameters(Statement stmt, TestDataObject testDataObj, Date date, 
			boolean edit, boolean subType) throws ParseException, XPathExpressionException{
		ArrayList<String> failed = new ArrayList();
		if(verifyInputParameters(stmt, testDataObj, date, false, false)){
			
//	        Verify Description of Item
			if(verifyStringInBytes(testUtils.extractString(attribute, "Comment"), testDataObj.getParam7())){
				System.out.println("Description "+testDataObj.getParam7()+" is matched with DB value for Interaction : "+interID);   
	        }else{
	        	System.out.println("Description "+testDataObj.getParam7()+" did not match with DB value for Interaction : "+interID);
	        	failed.add("Description: "+testDataObj.getParam7());
	        }
	        	
//	        Verify Status of Item
			if(verifyStringInBytes(testUtils.extractString(attribute, "Status"), testDataObj.getParam3())){
				System.out.println("Status "+testDataObj.getParam3()+" is matched with DB value for Interaction : "+interID);   
	        }else{
	        	System.out.println("Status "+testDataObj.getParam3()+" did not match with DB value for Interaction : "+interID);
	        	failed.add("Status: "+testDataObj.getParam3());
	        }
			
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
	        if(edit){
	        	if(!verifyParentIteraction(stmt, interID, testDataObj.getItemTitle())){
	        		failed.add("Parent Title: "+testDataObj.getItemTitle());
	        	}
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
			
		}else{
			return false;
		}
	}

	public boolean verifyTitleForFile(Statement stmt , int interId, String title){
		int count = 0;
		String query = "select roomname from Interactions where interId=" + interId + " and roo"
				+ "mname = '"+title +"'";
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
			System.out.println("Title :" + title + " is matched for Interaction Id : "+interId);
			return true;
		}else{
			System.out.println("Title :" + title + " did not match for Interaction Id : "+interId);
			return false;
			
		}
	}
	
	public boolean verifyKeywordForFile(Statement stmt , int interId, String keyword){
		String sAttrs = null;
		boolean flag = false;
		String query = "select attributes from messages where interId = "+interId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			sAttrs = rs.getString("attributes");
			
			if(verifyStringInBytes(testUtils.extractString(sAttrs, "Keywords"), keyword)){
	               System.out.println("Keywords "+keyword+" is matched with DB for Interaction : "+interId);
	               flag = true;
	               
	        }else{
	               System.out.println("Keywords "+ keyword +" did not match with DB for Interaction : "+interId);
	               flag = false;
	        }
		}
		catch(SQLException se){
			se.printStackTrace();
		}
		return flag;
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

	public String getAttribute(Statement stmt, int interID){
		String attribute = null;
		String query = "select attributes from messages where interId ="+interID;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			attribute = rs.getString("attributes");
		}catch(SQLException e){
			System.out.println("Error while getting attributes from Messages ");
		}
		return attribute;
	}
	
	
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
	
	
//	Verify Event
	
	public boolean verifyCalendarEvent(Statement stmt, TestDataObject obj, Long startTime){
	 	int interId =0;
	 	ArrayList<String> failed = new ArrayList();
		int preCount = getCurrentInteractionsCount(stmt);
		if(isInteractionCountChanged(stmt, preCount))
		 interId = getInteractionId(stmt, startTime, obj.getUserName() );
		
		String attrs = getAttribute(stmt, interId);
		
		if(!verifyTitle(stmt, interId, obj.getItemTitle())){
			failed.add("Title : "+obj.getItemTitle());
		}
		
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Category"), obj.getItemDescription())){
			failed.add("Category : "+ obj.getItemDescription());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Comments"), obj.getParam3())){
			failed.add("Comments : "+ obj.getParam3());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "Location"), obj.getParam4())){
			failed.add("Location : "+ obj.getParam4());
		}
		if(!verifyStringInBytes(testUtils.extractString(attrs, "StartDate"), obj.getParam5())){
			failed.add("StartDate : "+ obj.getParam5());
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
	
	
	public String getYesOrNo(String value){
		if(Boolean.parseBoolean(value)){
			return "Yes";
		}else{
			return "No";
		}
	}
}
