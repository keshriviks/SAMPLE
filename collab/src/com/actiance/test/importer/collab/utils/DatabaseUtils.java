package com.actiance.test.importer.collab.utils;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

//import org.apache.tools.ant.types.CommandlineJava.SysProperties;

//import com.adventnet.snmp.snmp2.agent.SysOREntry;


public class DatabaseUtils {
	ResultSet rSet = null;
	TestUtils testUtils = new TestUtils();
			
	
	// Establishing the Database Connection
	
	public Statement VantageDBConnectivity(String DBSERVERIP, String DBName,
			String sDBUser, String sDBPass) {

		Statement VantageDBstmt = null;
		Connection VantagedbConn;
		String VantagedbURL;
		String driverName;
		System.out.println("dbtype is " + System.getProperty("dbType"));

		try {
			if (System.getProperty("dbType").equalsIgnoreCase("Oracle")) {
				driverName = "oracle.jdbc.driver.OracleDriver"; // Start JDBC
				// VantagedbURL="jdbc:oracle:thin:@"+DBSERVERIP+":1521:"+DBName+";user="+sDBUser+";password="+sDBPass;
				VantagedbURL = "jdbc:oracle:thin:" + sDBUser + "//" + sDBPass
						+ "@" + DBSERVERIP + ":1521:" + DBName;
				// nidhi1_ci_2014r2/facetime@192.168.117.169:1521:autodb";
				//System.out.println("vantage db url now is " + VantagedbURL);

			} else {
				driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // Start
																				// JDBC
				VantagedbURL = "jdbc:sqlserver://" + DBSERVERIP+ ";databaseName=" + DBName + ";user=" + sDBUser	+ ";password=" + sDBPass;
				//System.out.println("vantage db url now is " + VantagedbURL);
				

			}

			Class.forName(driverName);
			VantagedbConn = DriverManager.getConnection(VantagedbURL);
			VantageDBstmt = VantagedbConn.createStatement();
			System.out.println("Connection have been Created ");
//			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println("Error in DB Connection");
			e.printStackTrace();
		} finally {
			return VantageDBstmt;
		}
	}
	     
	
//// Data Verification Methods with DB....
	
	public boolean isInteractionCreated(Statement stmt,
			Long eventTime, String userID) {

		int count = 0;
		String query = "select count(*) from interactions where startTime="
				+ eventTime + " and buddyName = '" + userID + "'";
		try {
			rSet = stmt.executeQuery(query);
			rSet.next();
			count = rSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();

		}
		if (count > 0)
			return true;
		else
			return false;
	}
	
	 public int getInteractionByUserIdandEventTimeAndObjectID(Statement stmt, String userID, Long eventTime, String contentSubType, String eventId){
		 int interId=0;
		 String query = ("select interId from interactions where startTime="+ eventTime + " and buddyName = '" + userID + "' and eventId ='"+ eventId + "'");
		 try{
			 System.out.println(query);
			 rSet=stmt.executeQuery(query);
			 rSet.next();
			 interId=rSet.getInt(1);
			 System.out.println("******************************************************");
			 System.out.println("Interaction is Found , interactions is : "+interId);
			 System.out.println("******************************************************");
		 }
		 catch(SQLException e){
			 System.out.println("Interaction is not ther for buddyname : "+userID +" on :"+eventTime+" time");
			 e.printStackTrace();
		 }
		 return interId;
	 }
	
		public boolean verifyObjectID(Statement stmt, int interId, String objectId) {
		//boolean flag = false;
		int count = 0;
		String query = "select count(*) from interactions where interId= "
				+ interId + " and eventID = '" + objectId + "'" ;
		//System.out.println("Object ID verification query : " + query);
		try {
			rSet = stmt.executeQuery(query);
			rSet.next();
			count = rSet.getInt(1);
		} catch (SQLException e) {
			System.out.println("Obeject Id does not with DB ");
			e.printStackTrace();

		}
		if (count > 0)
			return true;
		else
			return false;
	} 
	
	public boolean verifyEmployeeInfo(Statement stmt, int interId, EmployeeInfo emp) {
		// boolean flag=false;
		int count = 0;
		String query = "select count(*) from Employees where employeeID = '"
				+ emp.getEmployeeID() + "' and firstName = '"
				+ emp.getFirstName() + "' and lastName = '" + emp.getLastName()
				+ "'";
		try {
			rSet = stmt.executeQuery(query);
			rSet.next();
			count = rSet.getInt(1);
		} catch (SQLException e) {
			System.out.println("Employee Info for interaction : " + interId +" : Does not Matched With DB");
			e.printStackTrace();
		}
		if (count > 0)
			return true;
		else
			return false;
	}
	
	public boolean verifyParticipant(Statement stmt, int interId, Participant participant) {

		int count = 0;
		String query = "select count(*)  from Participants where interid = "
				+ interId + "and buddyName ='" + participant.getBuddyName()
				+ "'";
		try {
			rSet = stmt.executeQuery(query);
			rSet.next();
			count = rSet.getInt(1);
		} catch (SQLException e) {
			System.out.println("Buddyname with : " + participant.getBuddyName() +" for Interaction : "+ interId
					+ " : does not matched with Participant");
			e.printStackTrace();
		}
		if (count > 0)
			return true;
		else
			return false;
	}
	
	public boolean verifyContentText(Statement stmt, int interId, byte[] contentTextInput) throws UnsupportedEncodingException{
		boolean flag = false;
		ArrayList<byte[]> bytes = new ArrayList<byte[]>();
		bytes = getBytesForContentTextFromMessagesTable(stmt, interId);
		for(int i = 0; i<bytes.size(); i++){
			if(bytes.get(i).length == contentTextInput.length){
				for(int j = 0 ; j< bytes.get(i).length; j++){
					if(bytes.get(i)[j]==contentTextInput[j]){
//						System.out.println(bytes.get(i)[j]+"=="+ contentTextInput[j]);
						flag = true;
						break;
					}
					else{
						flag = false;
					}
				}
				
			}
		}
		return flag;
		
	}
	
	
	
	//// Incomplete Method...(Attrt value is not verified ) and the Query is MSSQL specific 
	public boolean verifyAttribute(Statement stmt, int interId, Attribute attr){
		ArrayList<String> attrs = getAttribute(stmt, interId);
		for(String attribute : attrs){
			if(testUtils.extractString(attribute, attr.getName()).equalsIgnoreCase(attr.getValue())){
				return true;
			}else{
				continue;
			}
		}
		return false;
	}
	
	// Query is MSSQL specific it will not work on Oracle
	
	public boolean verifyActionType(Statement stmt, int interId, String actionType) {
		int actionId=getActionIdByActionType(stmt, actionType);
		int count = 0;
		String query;
		if(System.getProperty("dbType").equalsIgnoreCase("MSSQL"))
			query = "select count(*) from Interactions where interId =" + interId +" and  attributes.exist('//*[text()=\""+actionId+"\"]')=1";
		else
			query=" ";
		try{
			//System.out.println(query);
			rSet=stmt.executeQuery(query);
			rSet.next();
			count=rSet.getInt(1);
		}catch(SQLException e){
			System.out.println("Action type : "+ actionType + " for interaction : "+ interId + " does not matched with DB");
		}
		if (count > 0)
			return true;
		else
			return false;
	}
	
	// Query is MSSQL specific it will not work on Oracle
	public boolean verifyObjectURI(Statement stmt, int interId, String objectURI){
		int count=0;
		String query;
		if(System.getProperty("dbType").equalsIgnoreCase("MSSQL"))
			query = "select count(*) from Interactions where interId =" + interId +" and  attributes.exist('//*[text()=\""+objectURI+"\"]')=1";
		else
			query="";
		try{
			rSet=stmt.executeQuery(query);
			rSet.next();
			count=rSet.getInt(1);
		}
		catch(SQLException e){
			System.out.println("ObjectUri : "+objectURI + " : for Interaction : " + interId + " : does not matched with DB");
		}
		if(count > 0)
			return true;
		else
			return false;
	}
	
/*	public boolean verifyFile(Statement stmt, int interId, File file ){
		int count = 0;
		String query = "select count(*) from FileXfers where interid = " + interId + " and fileName ='"+file.getFileName()+"' and networkFileID='"+file.getFileID() +"'" ;
		try{
			rSet = stmt.executeQuery(query);
			rSet.next();
			count = rSet.getInt(1);
		}
		catch(SQLException e){
			System.out.println("File is not in DB for Interaction : "+interId);
		}
		
		
		if(count > 0)
			return true;
		else
			return false;
	}*/
	
	
	
	public boolean verifyCorrelationId(Statement stmt, int interId, String correlationId){
		int count = 0;
		String query = "select count(*) from Interactions where interId = "+interId + " and parentEventId ='"+correlationId + "'";
		try{
		rSet = stmt.executeQuery(query);
		rSet.next();
		count = rSet.getInt(1);
		}
		catch(SQLException e){
			System.out.println("Correlation Id : "+correlationId+" for Interaction Id : "+interId+" is not matched with DB");
			e.printStackTrace();
		}
		if(count > 0)
			return true;
		else
			return false;
	}
	
	
	public boolean verifyUserId(Statement stmt, int interId, String userId){
		int count = 0;
		String query = "select count(*) from Interactions from interId = " + interId + " and buddyName ='" + userId +"'";
		try{
			rSet = stmt.executeQuery(query);
			rSet.next();
			count = rSet.getInt(1);
		}
		catch(SQLException e){
			System.out.println("User Id :"+ userId + " for Interaction : "+interId + " is not matched with DB"); 
			e.printStackTrace();
		}
		if(count > 0)
			return true;
		else
			return false;
	}
	
	
	public boolean verifyResource(Statement stmt, int interId, Long eventTime, String resourceName, String resourcesID){
		int count = 0;
		String query = "select count(*) from Interactions where resourceID = (select resourceID from Resources where resName='" +resourceName + "' and networkResourceID='"+resourcesID+"') and startTime = "+ eventTime ;
		try{
			//System.out.println("Resource Query : "+query);
			rSet = stmt.executeQuery(query);
			rSet.next();
			count =rSet.getInt(1); 
		}
		catch(SQLException e){
			System.out.println("Resource name : "+ resourceName + " for Interaction : " + interId + " at EventTime : "+ eventTime + " is not matched with DB" );
			e.printStackTrace();
		}
		if(count > 0)
			return true;
		else
			return false;
	}
	
	
	public boolean verifyContentType(Statement stmt, int interId, String contentType ){
		int count = 0;
		int contentTypeId = getContentTypeId(stmt, contentType);
		String query = "select count(*) from Interactions where interId ="+ interId + " and contentType ="+contentTypeId;
		try{
			//System.out.println(query);
			rSet = stmt.executeQuery(query);
			rSet.next();
			count = rSet.getInt(1);
		}
		catch(SQLException e){
			System.out.println("Content Type : "+contentType+ "is not macthed for Interaction : "+ interId);
			e.printStackTrace();
		}
		if(count > 0)
			return true;
		else
			return false;
		}
	
	
	
	//// utils methods
	
	public int getActionIdByActionType(Statement stmt, String actionType) {
		int networkId=getNetworkId(stmt);
		int actionId=0;
		String query="select actionID from ActionTypes where networkID = "+ networkId + " and name ='" + actionType+"'";
		try{
			//System.out.println(query);
			
			rSet=stmt.executeQuery(query);
			rSet.next();
			actionId=rSet.getInt(1);
		}
		catch(SQLException e){
			System.out.println("Invalid Action Type "+ actionType);
			e.printStackTrace();
		}
		
		return actionId;

	}
	
	public int getNetworkId(Statement stmt) {
		int networkId = 0;
		String query = "select networkID from Networks where name like '%"+ System.getProperty("networkType") + "%'";
		try {
			rSet = stmt.executeQuery(query);
			rSet.next();
			networkId = rSet.getInt(1);
		} catch (SQLException e) {
			System.out.println("Network : " + System.getProperty("networkType") + " : is not in DB");
			e.printStackTrace();
		}
		return networkId;

	}

	public int getContentTypeId(Statement stmt, String contentType){
		int contentTypeId = 0;
		int networkId = getNetworkId(stmt);
		String query = "select typeID from contentTypes where value='"+ contentType + "' and networkId ="+networkId;
		try{
			rSet = stmt.executeQuery(query);
			rSet.next();
			contentTypeId = rSet.getInt(1);
		}
		catch(SQLException e){
			System.out.println("Content type : "+contentType+ "is not found for Network Id : "+networkId+"please correct it in the DB");
			e.printStackTrace();
		}
		return contentTypeId;
	}
	
	public int getContentSubtypeId(Statement stmt, String contentSubtype){
		int networkId=getNetworkId(stmt);
		int contentSubTypeId = -1;
		if(contentSubtype == null)
			return contentSubTypeId;
		else{
			String query="select typeID from contentSubTypes where networkID="+networkId + " and value='"+contentSubtype+"'";
			try{
				rSet = stmt.executeQuery(query);
				rSet.next();
				contentSubTypeId = rSet.getInt(1);
				
			}
			catch(SQLException e){
				System.out.println("Error in Content Sub type....");
				e.printStackTrace();
			}
			return contentSubTypeId;
		}
	}

	public ArrayList<byte[]> getBytesForContentTextFromMessagesTable(Statement stmt, int interId) throws UnsupportedEncodingException{
		ArrayList<byte[]> text = new ArrayList();
		byte [] b= null;
		
		String query = "select text from messages where interId="+interId;
		try{
			rSet = stmt.executeQuery(query);
			while(rSet.next()){
				if(rSet.getString(1)!= null){
//					System.out.println("DB Input : "+rSet.getString(1));
					//text.add(rSet.getString(1));
					b = rSet.getString(1).replaceAll("\\s+", "").getBytes("UTF-8");
//					System.out.println(b.length);
					text.add(b);
				}
					
			}
				
		}
		catch(SQLException e){
			System.out.println("Error in getting the Text from Messages");
			e.printStackTrace();
		}
		
		return text;
	}
	
	
	public ArrayList<String> getAttribute(Statement stmt, int interID){
		ArrayList<String> allAttrs = new ArrayList();
		String query = "select attributes from Messages where interID = "+ interID + " and attributes IS NOT NULL";
		try{
			rSet = stmt.executeQuery(query);
			while(rSet.next()){
				allAttrs.add(rSet.getString(1));
			}
		}catch(SQLException e){
			System.out.println("No Attribute found for Interaction ID : " + interID);
		}
		
		return allAttrs;
		}
	
//	Incomplete....
	public boolean verifyAttributes(Attributes attrs, Statement stmt, int interID){
		boolean result = true;
		boolean flag = false;
		TestUtils testUtils = new TestUtils();
		ArrayList<String> dbAttrs = getAttribute(stmt, interID);
		String[] localAttr =new String[dbAttrs.size()];
		localAttr = dbAttrs.toArray(localAttr);
		for(Attribute attr : attrs.getAttribute()){
			for(int i = 0; i<localAttr.length; i++){
				if(attr.getValue().equals(testUtils.extractString(localAttr[i], attr.getName()))){
					System.out.println("Attribute Name: "+ attr.getName() + " and Value : "+ attr.getValue() + " is match for Interaction ID : "+interID);
					flag = true;
					break;
				}else{
					result = false;
				}
			}
		}
		return (result && flag);
	}
	
	
	public ArrayList<String> getFileNamesFromDB(Statement stmt, int interID){
		String query = "select filename from Filexfers where interId = " + interID;
		ResultSet rs;
		ArrayList<String> fileNames = new ArrayList<String>();
		
		try{
			rs = stmt.executeQuery(query);
			while(rs.next()){
				fileNames.add(rs.getString(1));
			}
		}catch(SQLException se){
			System.err.println("not able to fetch Filename from Filexfers Tables for Interaction Id : " + interID);
		}
		return fileNames;
	}
	
	
}
