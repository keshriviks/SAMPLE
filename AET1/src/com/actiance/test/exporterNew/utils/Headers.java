/**
 * Created By : Manish Garg
 * Date : 16-June-2016
 * Purpose : To fetch the Required Email headers from EML and Same from DB with Corresponding Interactions,
 *  Than compare both sides 
 *  If something is fails, it will print the failure header name with corresponding Interaction id.
 *  
 */

package com.actiance.test.exporterNew.utils;

/*
 *     public static final int DEFAULT_DEPLOYMENT = 0;
       public static final int MASTER_DEPLOYMENT = 1;
       public static final int REMOTE_DEPLOYMENT = 2;

/**
       * Replacement tokens for email headers.
       * 
       * $exportID                      Unique number assigned to each export. Each exporter has its own pool of numbers that do overlap.
       * $exportIDPad10                Same as $exportID, except number is formated to 10 places - ex. 0000012345
       * $interID                              Unique number assigned to each interaction.
       * $exporterNum                         Unique number assigned to each exporter. The values are 0 to 63.
       * $exporterName                  User assigned exporter name.
       * $dbID                                 Database ID. Contains the DB URL.
       * $deploymentID                  Deployment ID. Number assigned to each DB in a replicated DB environment. Default is 0.
       * $serverName                          Deprecated, same as $exporterName
       * $serverID                      Unique number assigned to each server.
       * $intCompanyID                  Internal company ID.
       * $companyID                     Company ID.
       * $startTime                     Interaction start time.
       * $endTime                              Interaction end time.
       * $outgoingFlag                  True if first IM was sent from the employee to a participant.
       * $internalFlag                  True if all participants are employees.
       * $chatFlag                      True if conversation is from a persistent chat room.
       * $roomName                      Chat room name.
       * $interType                     Interaction type.
       * $containerID                         Container ID.
       * $parentEventID                Parent event ID.
       * $eventID                              Event ID.
       * $resourceID                          Resource ID.
       * $resourceName                  Resource name.
       * $contentType                         Content type.
       * $tamperedFlag                  True if conversation was modified in DB.
       * $supervisedFlag               Deprecated, always true.
       * $buddyName                     Employee's buddy name.
       * $networkID                     Employee's network ID.
       * $networkName                         Employee's network name.
       * $firstName                     Employee's first name. Can be null.
       * $lastName                      Employee's last name.
       * $employeeAttr                  Employee attribute - ex. $employeeAttr[employee.from.directory]
       * $buddyNetworkName             Employee buddy name and network name formatted as: buddyname@networkName.im
       * $employeeID                          Employee ID if mapped, else buddyname@networkName.im is used.
       * $emailAddress                  Employee email address if it exists, else buddyname@networkName.im is used.
       * $firstLastName                Employee's first and last name if it exists, else buddyname@networkName.im is used.
       * $participants                  List of participant buddy names separated by commas. If no participants, the employee buddy name is used.
       * $partsBuddyNetworkNames List of participant buddy names and network names formatted as: buddyname@networkName.im separated by commas. 
       * $partsEmployeeIDs             List of participant employee IDs separated by commas. If participant is not an employee, buddyname@networkName.im is used.
       * $partsEmailAddresses          List of participant employee email addresses separated by commas. If participant is not an employee or does not have an email address, buddyname@networkName.im is used.
       * $partsFirstLastNames          List of participant first and last names separated by commas. If participant is not an employee or does not have a first and last name, buddyname@networkName.im is used.
       * $fromEmailAddress             Email address if it exists for the sender of the first message, else buddyname@networkName.im is used.
       * $toEmailAddresses             Email address if it exists for the receivers of the first message and everyone else who later joined the conversation. buddyname@networkName.im is used for those with out an email address.
       * $subject                              Subject of the EML file[only in the case of email exporter]
       * $chatOriginator         Buddyname of the chat room originator
       * $emailName               Email name formatted as: $firstLastName <$emailAddress>; if firstLastName doesn't exist : "$emailAddress" <$emailAddress>
       * $partsEmailNames         Similar to $emailName format but for all participants except conversation originator (i.e. $emailName itself).
       */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
//import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public class Headers {
	 String dbId = null; 
	 String exportId = null;
	 String serverId = null;
	 String modelity = null;
	 String exporterName = null;
	 String exporterNumber = null;
	 String exportIDPad10 = null;
	
	ResultSet rs = null;
	static ArrayList<File> files = new ArrayList();
	
	
	public  ArrayList listFiles(String directoryName) {

	    // .............list file
	    File directory = new File(directoryName);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();

	    for (File file : fList) {
	        if (file.isFile()) {
	        	files.add(file.getAbsoluteFile());
	        } else if (file.isDirectory()) {
	            listFiles(file.getAbsolutePath());
	        }
	    }
	    return files;
	} 
	
//	Generating Header Map from Eml side
	public HashMap<Integer,HashMap<String, String>> getHeaderMapFromEml(String emlFilePath) throws MessagingException, NumberFormatException, IOException{
		HashMap<Integer,HashMap<String,String>> headerMapFromEMLs = new HashMap();
		HashMap<String,String> headerMapFromEML = new HashMap();
		ArrayList<File> emlFiles = new ArrayList();
		emlFiles = listFiles(emlFilePath);
		for(File emlFile : emlFiles){
			InputStream is = new FileInputStream(emlFile);
			MimeMessage msg = new MimeMessage(null, is);
			Enumeration headers = msg.getAllHeaders();
			Integer interId = Integer.valueOf(getIntractionId(emlFile.getAbsolutePath()));
			while(headers.hasMoreElements()){
				Header header = (Header) headers.nextElement();
				
				if(header.getName().contains("X-FaceTime-IMA")){
//					System.out.println("Header Name :"+ header.getName()+ "  --- value : "+ header.getValue());
					headerMapFromEML.put(header.getName(), header.getValue());	
				}
				headerMapFromEMLs.put(interId, headerMapFromEML);
			}
		}
		
		return headerMapFromEMLs;
	} 
/*//	base 1 Old 
	public HashMap<Integer,HashMap<String, String>> getHeadersFromDB(Statement stmt, HashMap<Set<Integer>,HashMap<String,String>> inputHeader){
		HashMap<Integer,HashMap<String, String>> getHeadersWithValue = new HashMap();;
		
		Set<Integer> interIdKey = inputHeader.keySet();
		HashMap<String,String> headerMap; 
		for(Integer interIdKey : inputHeader.keySet()){
			headerMap = inputHeader.get(interIdKey);
			HashMap<String, String> convertedHeadersValue = convertHeaderValues(stmt, headerMap, interIdKey.intValue());
			getHeadersWithValue.put(interIdKey, convertedHeadersValue);
		}
		return getHeadersWithValue;
	}
	
	*/
//	base 1
	public HashMap<String, String> getHeadersFromDB(Statement stmt, HashMap<String,String> inputHeaderFromProperties , int interId, String exporterTempleteName){
		HashMap<String,String> inputHeader = new HashMap();
		inputHeader = getHeadersFromDBAndPropertiesFile(stmt, inputHeaderFromProperties, exporterTempleteName);
		HashMap<String, String> getHeadersWithValue = new HashMap();
		HashMap<String,String> headerMap = inputHeader; 
			HashMap<String, String> convertedHeadersValue = convertHeaderTokensWithValues(stmt, headerMap, interId,exporterTempleteName);
//			getHeadersWithValue.put(Integer.valueOf(interId), convertedHeadersValue);
		return convertedHeadersValue;
	}
//	 base 2
	public HashMap<String, String> convertHeaderTokensWithValues(Statement stmt, HashMap<String, String> header, int interId, String exporterTempleteName){
		HashMap<String, String> resultMap = new HashMap();
		ArrayList<String> value;
		StringBuffer sb = null;
		for(String headerKey : header.keySet()){
			sb = new StringBuffer();
			String values = header.get(headerKey);
			value = new ArrayList();
			value = getValuesForToken(stmt, values, interId,exporterTempleteName);
			if(value .size() != 0){
				for(String tokenValue : value){
					
					sb.append(tokenValue + ":");
				}
				resultMap.put(headerKey, (sb.toString().substring(0,sb.length()-1)));
			}else{
				resultMap.put(headerKey, "");
			}
		
			
			value.clear();
			
		}
		return resultMap;
	}
	
//	break the tokens by ':' sign
	public ArrayList<String> splitToken(String tokens){
		ArrayList<String> token = new ArrayList();
		String[] tokenArray = tokens.split(":");
		for(String tokenStr : tokenArray ){
			token.add(tokenStr);
		}
		return token;
	}
//	Check value is token
	public boolean isToken(String value){
		if(value.charAt(0) == '$'){
			return true;
		}else{
			return false;
		}
	}

	
//	Converted Tokens into Values
	public ArrayList<String> getValuesForToken(Statement stmt, String values, int interId, String exporterTempleteName){
		ArrayList<String> resultValue = new ArrayList();
		ArrayList<String> value = splitToken(values);
		for(String tempValue : value){
			if(!isToken(tempValue)){
				resultValue.add(tempValue);
			}else{
				StringBuilder sb = new StringBuilder(tempValue);
				sb.deleteCharAt(0);
				tempValue = sb.toString();
				switch(tempValue.toLowerCase()){
				case "interid" :
					resultValue.add(String.valueOf(interId));
					break;
				case "endtime" :
					resultValue.add(getTime(Long.valueOf(getValueFromInteractionsTable(stmt, interId, tempValue))));
					break;
				case "parenturl" :
					resultValue.add("");
					break;
				case "tamperedflag" :
					resultValue.add("true");
				case "resourcename" : 
					resultValue.add(checkNull(getResourceName(stmt, getValueFromInteractionsTable(stmt, interId, "resourceId"))));
					break;
				case "parentobjectid" : 
					resultValue.add(checkNull(getValueFromInteractionsTable(stmt, interId, "parentEventId")));
					break;
				case "starttime" :
					resultValue.add(getTime(Long.valueOf(getValueFromInteractionsTable(stmt, interId, tempValue))));
					break;
				case "networkname":
					resultValue.add(checkNull(getNetworkName(stmt, getValueFromInteractionsTable(stmt, interId, "networkId"))));
					break;
				case "supervisedflag" :
					resultValue.add("true"); // Don't know in which table it is populated
					break;
				case "internalflag" :
					resultValue.add(getInternalFlag(getValueFromInteractionsTable(stmt, interId, tempValue)));
					break;
				case "exportid" :
					resultValue.add(getExportId(stmt, interId,exporterTempleteName));
					break;
				case "employeeid" : 
					resultValue.add(checkNull(getEmployeeColumnValue(stmt, (getValueFromInteractionsTable(stmt, interId, "intEmployeeId")),tempValue)));
					break;
				case "buddyname" : 
					resultValue.add(getBuddyName(stmt, interId));
					break;
				case "outgoingflag" :
					resultValue.add(String.valueOf(getOutgoing(stmt, interId)));
					break;
				case "objectid":
					resultValue.add(getObjectId(stmt, interId));
					break;
				case "contenttype":
					resultValue.add((getNetworkName(stmt, getValueFromInteractionsTable(stmt, interId, "networkId")) + "." +getModality(stmt, getValueFromInteractionsTable(stmt, interId, "interType"))));
					break;
				case "participants" :
					resultValue.add(checkNull(getParticipantId(stmt, interId)));//, getValueFromInteractionsTable(stmt, interId, "networkId"))));
					break;
				case "partsemployeeids" : 
					resultValue.add(getPartsEmployeeIds(stmt, interId));//, getValueFromInteractionsTable(stmt, interId, "networkId")));
					break;
				case "starttime,$endtime" :
					resultValue.add(getTime(Long.valueOf(getValueFromInteractionsTable(stmt, interId, "startTime")))+","+getTime(Long.valueOf(getValueFromInteractionsTable(stmt, interId, "endTime"))));
					break;
				case "firstname" : 
					resultValue.add(checkNull(getEmployeeColumnValue(stmt, getValueFromInteractionsTable(stmt, interId, "intEmployeeID"), tempValue)));
					break;
				case "lastname" :
					resultValue.add(checkNull(getEmployeeColumnValue(stmt, getValueFromInteractionsTable(stmt, interId, "intEmployeeID"), tempValue)));
					break;
				case "partsemailaddresses" :
					resultValue.add(checkNull(getPartsEmailAddresses(stmt, interId)));//, getValueFromInteractionsTable(stmt, interId, "networkId"))));
					break;
				case "networkid" :
					resultValue.add(checkNull(getValueFromInteractionsTable(stmt, interId, tempValue)));
					break;
				case "exportidpad10" :
					resultValue.add(getExportidpad10(stmt, interId,exporterTempleteName));
					break;
				case "exportername" :
					resultValue.add(getExporterName(stmt, exporterTempleteName));
					break;
				case "exporternum" :
					resultValue.add(getExporterNumber(exporterTempleteName));
					break;
				case "firstlastname" :
					resultValue.add(checkNull(getEmployeeColumnValue(stmt, getValueFromInteractionsTable(stmt, interId, "intEmployeeID"), "firstName")) + " " + checkNull(getEmployeeColumnValue(stmt, getValueFromInteractionsTable(stmt, interId, "intEmployeeID"), "lastName")));
					break;
				case "partsbuddynetworknames" :
					resultValue.add(getBuddyName(stmt, interId));
					break;
				case "chatoriginator" :
					resultValue.add(checkNull(getValueFromInteractionsTable(stmt, interId, "buddyName")));
					break;
				case "dbid" :
					resultValue.add(getDBId(stmt, System.getProperty("dbName")));
					break;
				case "deploymentid" : 
					resultValue.add("0"); // Has to update
					break;
				case "emailaddress" :
					resultValue.add(getPartsEmailAddresses(stmt, interId));//(stmt, interId));
					break;
				case "employeeattr[attribute]" :
					resultValue.add("");
					break;
				case "partsfirstlastnames" :
						resultValue.add(getPartsFirstNameLastName(stmt, interId));
					break;
				case "serverid" :
					resultValue.add(checkNull(getValueFromInteractionsTable(stmt, interId, tempValue)));
					break;
				case "title" :
					resultValue.add(checkNull(getTitle(stmt, interId)));
					break;
				case "fromemailaddress" :
					break;
				case "toemailaddresses" :
					break;
				case "containerid" :
					resultValue.add(checkNull(getValueFromInteractionsTable(stmt, interId, tempValue)));
					break;
				case "resourceid" :
					resultValue.add(checkNull(getResourceID(stmt, interId))); 
					break;
				case "intertype" :
					resultValue.add(checkNull(getModality(stmt, getValueFromInteractionsTable(stmt, interId, tempValue))));
					break;
				case "buddynetworkname" :
					resultValue.add(getNetworkName(stmt, getValueFromInteractionsTable(stmt, interId, "networkId")));
					break;
				case "companyid" :
					resultValue.add(getCompanyName(stmt, getValueFromInteractionsTable(stmt, interId, "intCompanyId")));
					break;
				default :
						resultValue.add("$"+tempValue);
						System.out.println( tempValue + " : is Not a Valid Token Please Update it in DB");
				}
			}
		}
		return resultValue;
	}
	
	
//---------------------------********-------------------------- Database Queries ------------------********-----------
	
//	Get Deployment Id
	
//	Get Company Name
	public String getCompanyName(Statement stmt, String intCompanyID){
		String companyName = null;
		String query = "select name from Companies where intCompanyId="+ intCompanyID;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			companyName = rs.getString("name");
		}catch(SQLException se){
			System.out.println("Error to find Company Name"); 
		}
		return companyName;
	}
	
//	 Get Exportidpad10 ($exportidpad10)
	
	public String getExportidpad10(Statement stmt, int interId,String exporterTempleteName){
		if(exportIDPad10 != null){
			return exportIDPad10;
		}else{
			String exportId = getExportId(stmt, interId,exporterTempleteName);
			StringBuffer sb = new StringBuffer();
			int length = exportId.length();
			for(int i = 0; i< 10-length; i++){
				sb.append("0");
			}
			sb.append(exportId);
			exportIDPad10 = sb.toString();
			return exportIDPad10;
		}
	}
//	 Get Exporter Number ($exporterNum)
	
	public String getExporterNumber(String exporterTempleteName){
		if(exporterNumber != null){
			return exporterNumber;
		}else{
			int index = exporterTempleteName.lastIndexOf(".");
			exporterNumber = exporterTempleteName.substring(index+1);
			return exporterNumber;
		}
	}
	
//	Get Exporter Name ($exportername)
	
	public String getExporterName(Statement stmt, String exporterTempleteName){
		if(exporterName != null){
			return exporterName;
		}else{
			String query = "select value from serverproperties where name ='"+exporterTempleteName +".exporterName'";
			try{
				rs = stmt.executeQuery(query);
				rs.next();
				exporterName = rs.getString("value");
			}catch(SQLException se){
				System.out.println("Error to find Exporter Name from Server Properties table");
			}
			return exporterName;
		}
	}
	
//	 Get ServerId ($serverId)
	
	public String getServerId(Statement stmt, int interId){
		if(serverId != null){
			return serverId;
		}else{
			String query = "select serverId from interactions where interId ="+ interId;
			try{
				rs = stmt.executeQuery(query);
				rs.next();
				serverId = rs.getString("serverId");
			}catch(SQLException se){
				System.out.println("No Server Id Found");
			}
			return serverId;
		}
	}
	
	
//	Get Resource Id ($resourceid)
	
	public String getResourceID(Statement stmt, int interId){
		String resID = null;
		String query = "select resourceId from interactions where interId ="+ interId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			resID = rs.getString("resourceId");
		}catch(SQLException se){
			System.out.println("No resource Id ");
		}
		return resID;
		
	}
	
//	Get Title ($title)
	public String getTitle(Statement stmt, int interId){
		String title = null;
		String query = "select roomname from interactions where interId ="+ interId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			title = rs.getString("roomname");
		}catch(SQLException se){
			System.out.println("No title Found");
		}
		return title;
	}
	
	
//	Get Database Id ($dbId)
	
	public String getDBId(Statement stmt, String databaseName){
//		String dbID = null;
		if(dbId != null){
			return dbId;
		}else{
		String query = "select dbID from databases where dbName ='"+ databaseName + "'";
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			dbId = rs.getString("dbID");
		}catch(SQLException se){
			System.out.println("Error to find Database Id");
		}
		return dbId;
		}
	}
	
//	Get Parts Employee Ids Token($partsemployeeids)
	
	public String getPartsEmployeeIds(Statement stmt, int interId){//, String networkId){
		String partEmployeeIds = null;
		StringBuffer sb = new StringBuffer();
		ArrayList<Integer> particiapntId = getParticipantIds(stmt, interId);//, networkId);
		ArrayList<String> empIds = new ArrayList();
		for(Integer partId : particiapntId){
			empIds.add(getEmployeeColumnValue(stmt, String.valueOf(partId),"employeeId"));
		}
		for(String empId : empIds){
			sb.append(empId+",");
		}
		return (sb.toString().substring(0, sb.length()-1));
		
	}
	
	
//	Get Participants First Name Last Name
	
	public String getPartsFirstNameLastName(Statement stmt, int interId){
		ArrayList<Integer> partsId = getParticipantIds(stmt, interId);
		ArrayList<String> firstLastName = new ArrayList();
		for(Integer partId : partsId){
			firstLastName.add(getEmployeeColumnValue(stmt, String.valueOf(partId), "firstName")+ " "+ getEmployeeColumnValue(stmt, String.valueOf(partId), "lastName"));
		}
		StringBuffer sb = new StringBuffer();
		for(String partsFirstLastName : firstLastName){
			sb.append(partsFirstLastName+",");
		}
		return (sb.toString().substring(0, sb.length()-1));
	}
	
//	Get Participant Names ($participant)
	
	public String getParticipantId(Statement stmt, int interId){//, String networkId){
		ArrayList<Integer> participantIds = getParticipantIds(stmt, interId);
		ArrayList<String> participantNames = new ArrayList();
		StringBuffer sb = new StringBuffer();
		for(Integer partId : participantIds){
			participantNames.add(getBuddyNameFromEmployeeBuddyname(stmt, partId.intValue()));
		}
		for(String participantName : participantNames){
			sb.append(participantName+ ",");
		}
		return (sb.toString().substring(0, sb.length()-1));
		
	}
	
//	Get Participant Email Address ($partsEmailAddresses)
	
	public String getPartsEmailAddresses(Statement stmt, int interId){
		String partsEmailAddresses = null;
		ArrayList<Integer> partEmployeeId = getParticipantIds(stmt, interId);
		ArrayList<String> emailAddress = new ArrayList();
		for(Integer empId : partEmployeeId){
			String query = "select value from EmployeeAttributes where intEmployeeId ="+ empId + " and attrID=17";
			try{
				rs = stmt.executeQuery(query);
				if(rs != null){
					rs.next();
					emailAddress.add(rs.getString("value"));
				}else{
					emailAddress.add(getBuddyNameFromEmployeeBuddyname(stmt, empId.intValue()));
				}
			}catch(SQLException se){
				System.out.println("error to foind Email Address");
			}
		}
		StringBuffer sb = new StringBuffer();
		for(String email : emailAddress){
			sb.append(email+ ",");
		}
		return (sb.toString().substring(0, sb.length()-1));
		
	}
// Get Participant Id 
	public ArrayList<Integer> getParticipantIds(Statement stmt, int interId){//, String networkId){
		ArrayList<Integer> participantsIds = new ArrayList();
		String query = "select intEmployeeId from participants where interId ="+ interId ;//+ " and networkId =" + networkId;
		try{
			rs = stmt.executeQuery(query);
			while(rs.next()){
				participantsIds.add(rs.getInt("intEmployeeId"));
			}
		}catch(SQLException se){
			System.out.println("Error to find employee Id for Interaction : "+ interId);
		}
		return participantsIds;
	}
	
	
//	Get FirstName from Employee table
	
//	Get BuddyName For participant
	
	public String getBuddyNameFromEmployeeBuddyname(Statement stmt, int intParticipantId){
		String buddyName = null;
		String query = "select buddyName from EmployeeBuddyNames where intEmployeeId = "+ intParticipantId ;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			buddyName = rs.getString("buddyName");
		}catch(SQLException se){
			System.out.println("Error to find BuddyName for intEmployeeId : " + intParticipantId);
		}
		if(buddyName!= null){
			return buddyName;	
		}else{
			return "";
		}
		
	}
//	Get Value from Employee Table (First Name, Last Name, Employee Id)..
	
	public String getEmployeeColumnValue(Statement stmt, String empId, String columnName){
		String empName = null;
		String query = "select "+columnName+" from Employees where intEmployeeId ="+ empId;
		try{
//			System.out.println("Employee Name Query : "+query);
			rs = stmt.executeQuery(query);
			rs.next();
			empName = rs.getString(columnName);
		}catch(SQLException e){
			System.out.println("Error to Find Employee Id from DB for intEmployeeId = "+ empId);
		}
		if(empName != null){
			return empName;	
		}else{
			return "";
		}
		
	}
	
	
  public String checkNull(String str){
	  if(str != null){
		  return str;
	  }else{
		  return "";
	  }
  }
	

//	Get Modality type
	public String getModality(Statement stmt, String interType){
		if(modelity != null){
			return modelity;
		}else{
			String query = "select name from modalities where modalityId ="+ interType;
			try{
				rs = stmt.executeQuery(query);
				rs.next();
				modelity = rs.getString("name");
			}catch(SQLException se){
				System.out.println("Error to find the Modelity fro Interaction type Id: "+ interType);
			}
			return modelity;
		}
	}

	//	Get Internal Flag
	public String getInternalFlag(String internalFlagValue){
		if(internalFlagValue.equals("1")){
			return String.valueOf(true);
		}else{
			return String.valueOf(false);
		}
	}
	
	
//	 Get Export Id
	public String getExportId(Statement stmt, int interId, String exporterTempleteName){
		exporterNumber = getExporterNumber(exporterTempleteName);
		if(exportId != null){
			return exportId;
		}else{
	//		System.out.println(System.getProperty("exporterNumber"));
			String query = "select exportId from ExportHistory where interId ="+interId + " and exporterNum =" + exporterNumber;
			try{
//				System.out.println(query);
				rs = stmt.executeQuery(query);
				rs.next();
				exportId = rs.getString("exportId");
			}catch(SQLException se){
				System.out.println("Error to find ExportId for Interaction : " + interId);
			}
			return exportId;
		}
	}
	// Get Buddy Name
	
	public String getBuddyName(Statement stmt, int interId){
		String buddyName = null;
		String query = "select buddyname from Interactions where interId ="+ interId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			buddyName = rs.getString("buddyName");
		}catch(SQLException se){
			System.out.println("Error to find Buddyname from DB for Interaction ID:"+interId);
		}
		return buddyName;
	}
	
//	Get Outgoing Flag
	public boolean getOutgoing(Statement stmt, int interId){
		String query = "select outgoingFlag from Interactions where interId ="+ interId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			String outgoingFlagValue = rs.getString("outgoingFlag");
			if(outgoingFlagValue.equals("1")){
				return true;
			}else{
				return false;
			}
		}catch(SQLException se){
			System.out.println("Error to find Outgoing Flag from DB for Interaction ID:"+interId);
			return false;
		}
	}
	
//	Get Object Id
	
	public String getObjectId(Statement stmt, int interId){
		String objectId = null;
		String query = "select eventID from Interactions where interId ="+ interId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			objectId = rs.getString("eventID");
		}catch(SQLException se){
			System.out.println("Error to find Buddyname from DB for Interaction ID:"+interId);
		}
		return objectId;
	}
	
//	Get network Name
	public String getNetworkName(Statement stmt, String networkId){
		String networkName = null;
		String query = "select name from networks where networkId =" + networkId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			networkName = rs.getString("name");
		}catch(SQLException se){
			System.out.println("Network Name not Found");
		}
		return networkName;
	}
// get Resource Name 
	public String getResourceName(Statement stmt, String resId){
		String resourceName = null;
		String query = "select resName from Resources where resourceId ="+ resId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			resourceName = rs.getString("resName");
		}catch(SQLException se){
			System.out.println("No Resource Found for Resource Id : "+ resId);
		}
		return resourceName;
	}
	
//	Get Value from Interaction table columns (Dynamic Query)
	
	public String getValueFromInteractionsTable(Statement stmt, int interId, String column){
		String value = null;
		String query = "select "+ column + " from Interactions where interId = " + interId;
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			value = rs.getString(column);
		}catch(SQLException se){
			System.out.println("Error to find the "+ column + " value from DB");
		}
		if(value != null){
			return value;	
		}else{
			return "";
		}
		
	}
	
//	Get Custom Header Names from Server Properties
//	exporterTempleteName = imexport.12
	
	public HashMap<String,String> getCustomHeaderName(Statement stmt, String exporterTempleteName){
		HashMap<String,String> customHeaderNames = new HashMap();
		String preName = ".customHeaderName";
		for(int i = 0; i < 19; i++){
			String query = "select value from serverProperties where name = '" + exporterTempleteName + preName + i + "'";
			try{
				rs = stmt.executeQuery(query);
				rs.next();
				String headerName = rs.getString("value");
//				customHeaderNames.put(exporterTempleteName + preName + i, headerName);
				customHeaderNames.put(preName + i, headerName);
  			}catch(SQLException se){
  				System.out.println("Error to find Header Name");
  				se.printStackTrace();
  			}
		}
		return customHeaderNames;
	}
	
	
//	Get Custom Header Values from Server Properties
	
//	exporterTempleteName = imexport.12
	
	public HashMap<String,String> getCustomHeaderValue(Statement stmt, String exporterTempleteName){
		HashMap<String,String> customHeaderValues = new HashMap();
		String preName = ".customHeaderValue";
		for(int i = 0; i < 19; i++){
			String query = "select value from serverProperties where name = '" + exporterTempleteName + preName + i + "'";
			try{
				rs = stmt.executeQuery(query);
				rs.next();
				String headerValue = rs.getString("value");
//				customHeaderValues.put(exporterTempleteName + preName + i, headerValue);
				customHeaderValues.put(preName + i, headerValue);
  			}catch(SQLException se){
  				System.out.println("Error to find Header Name");
  				se.printStackTrace();
  			}
		}
		return customHeaderValues;
	}
	
//	Get HeaderName and Its Value From DB
//	exporterTempleteName = imexport.12
	public HashMap<String,String> getHeaderAndValueFromDB(Statement stmt , String exporterTempleteName){
		HashMap<String,String> headerNames = new HashMap();
		HashMap<String,String> values = new HashMap();
		headerNames = getCustomHeaderName(stmt, exporterTempleteName);
		values = getCustomHeaderValue(stmt, exporterTempleteName);
		HashMap<String,String> headersFromDB = new HashMap();
		for(int i = 0; i< 19; i++){
			headersFromDB.put(headerNames.get(".customHeaderName"+i), values.get(".customHeaderValue"+i));
		}
		return headersFromDB;
	}
	
//	Get Final Headers and Value From DB
	
	public HashMap<String,String> getHeadersFromDBAndPropertiesFile(Statement stmt, HashMap<String,String> fromProperties, String exporterTempleteName){
		HashMap<String , String> headers = new HashMap();
		HashMap<String,String> fromDB = new HashMap();
		fromDB = getHeaderAndValueFromDB(stmt, exporterTempleteName);
		if(fromProperties.size() != 0){
			for(String headerFromDB : fromDB.keySet()){
  				for(String headerFromProperties : fromProperties.keySet()){
  					if(headerFromDB.equals(headerFromProperties)){
//  						System.out.println("In DB : " + fromDB.get(headerFromDB) );
//  						System.out.println("In Properties File : "+ fromProperties.get(headerFromDB));
  						headers.put(headerFromDB, fromProperties.get(headerFromDB));
  						break;
  					}
  					else{
  						headers.put(headerFromDB, fromDB.get(headerFromDB));
  					}
  				}
  			}
			return headers;
		}else{
			return fromDB;
		}
	}
// Converting Unix to dd MMM, YYYY HH:MM:SS AM/PM
  	public String getTime(Long unixTime){
		Date date = new Date(unixTime); 
		SimpleDateFormat sdf = new SimpleDateFormat("d MMM, yyyy HH:mm:ss a"); // the format of your date
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+530")); // give a timezone reference for formating (see comment at the bottom
		String formattedDate = sdf.format(date);
		return formattedDate.toString();
	}
//	verify Headers
	// dbMap = Generating based on Properties file parameters 
	// inputMap = Generating based on Eml file
  	// exporterTempleteName = imexport.12
	
	public boolean verifyHeaders(HashMap<String,String> dbMap, HashMap<String,String> inputMap , int interId, Statement stmt, String exporterTempleteName){
		boolean result = true;
		exporterTempleteName = exporterTempleteName.substring(0,exporterTempleteName.length()-1);
//		System.setProperty("exporterNumber", "17");
		HashMap<String,ArrayList<String>> failedHeaders = new HashMap();
		ArrayList<String> failedField = null;
//		boolean flag = true;;
		HashMap<String , String> dbHeaderValues = getHeadersFromDB(stmt, dbMap, interId,exporterTempleteName);
		Set<String> dbHeaderNames = dbMap.keySet();
		Set<String> inputHeaderNames = inputMap.keySet();
		for(String dbHeader : dbHeaderValues.keySet()){
			for(String inputHeader : inputHeaderNames){
				failedField = new ArrayList();
				if(dbHeader.equalsIgnoreCase(inputHeader)){
					if(dbHeaderValues.get(dbHeader).equals(inputMap.get(dbHeader))){
//						System.out.println("Header Name : "+ dbHeader + " and Value : "+ dbHeaderValues.get(dbHeader) + " is matched");
					}else{
						failedField.add(dbHeaderValues.get(dbHeader));
						failedField.add(inputMap.get(dbHeader));
						failedHeaders.put(dbHeader, failedField);
//						System.out.println("Expected Value  : "+ dbHeaderValues.get(dbHeader) + " and Actual Value : "+ inputMap.get(dbHeader) + " didn't match for Header : "+ dbHeader);
						result = false;
					}
				}
			}
		}
//		result = result & flag;
		if(failedHeaders.size() == 0 ){
			System.out.println("All Headers Are Matched");
			result = true;
		}else{
			System.out.println();
			System.out.println("--------Mismatched Headers for Interaction Id : "+ interId+"---- ");
			Set<String> filedHeaderName = failedHeaders.keySet();
			for(String headerName : filedHeaderName){
				System.out.println();
				System.out.println("Header Name :"+ headerName);
				System.out.println("Expected : "+failedHeaders.get(headerName).get(0));
				System.out.println("Actual : "+failedHeaders.get(headerName).get(1));
			}	System.out.println("------------------------------------------------------------");
			
		}
//		System.out.println(failedHeaders);
		return result;
	}
	
	public static void main(String[] args) throws NumberFormatException, MessagingException, IOException, SQLException {
//		Testing the code
		int interId = 10027;
		System.setProperty("dbName", "PRA13637");
//		System.setProperty("exporterT", value)
		Headers gh = new Headers();
		String filePath = "D:/Exporter/HeaderTest/New Token Files";
		HashMap<Integer,HashMap<String,String>> inputMap = new HashMap();
		HashMap<String,String> dbMap = new HashMap();
		inputMap = gh.getHeaderMapFromEml(filePath);
		dbMap = gh.dummyMap();
		Statement stmt = gh.getStatement();
//		System.out.println("Statement Created");
		boolean result = gh.verifyHeaders(dbMap, inputMap.get(interId), interId, stmt,"imexport.17");
		System.out.println("Result : "+ result);
		
		
	}
	
	public   String getIntractionId(String filePath) throws MessagingException, IOException{
//		System.out.println("****************************");
//		System.out.println("Source EML : "+ filePath);
		InputStream is = new FileInputStream(filePath);
		MimeMessage msg = new MimeMessage(null, is);
		String subject = (String)msg.getSubject();
		int index = subject.indexOf("#");
		String tempStr = subject.substring(index+1);
//		System.out.println(tempStr);
		return tempStr.trim();	
	}
	
	public HashMap<String,String> dummyMap(){
		HashMap<String,String> headerMap = new HashMap();
		headerMap.put("X-FaceTime-IMA-interID", "$interId");
		headerMap.put("X-FaceTime-IMA-employeeID", "$partsEmailAddresses");
//		headerMap.put("X-FaceTime-IMA-exportID-interID", "$exportId:$interId");
//		headerMap.put("X-FaceTime-IMA-contentType", "$contentType");
//		headerMap.put("X-FaceTime-IMA-partsEmployeeIDs", "$partsEmployeeIDs:Lync");
//		headerMap.put("X-FaceTime-IMA-participants", "$participants");
//		headerMap.put("X-FaceTime-IMA-internalFlag", "$internalFlag");
//		headerMap.put("X-FaceTime-IMA-buddyName", "$buddyName:$networkName");
//		headerMap.put("X-FaceTime-IMA-networkName", "$networkName");
//		headerMap.put("X-FaceTime-IMA-outgoingFlag", "$outgoingFlag");
//		headerMap.put("X-FaceTime-IMA-startTime", "$startTime");
//		headerMap.put("X-FaceTime-IMA-endTime", "$endTime");
//		headerMap.put("X-FaceTime-IMA-parentObjectID", "$parentObjectID");
		
		
		
		
		return headerMap;
	}
	
	public Statement getStatement() throws SQLException{
		ArrayList<String> dbParam = new ArrayList();
		dbParam.add("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dbParam.add("PRA13637");
		dbParam.add("jdbc:sqlserver://192.168.114.214:1433");
		dbParam.add("sa");
		dbParam.add("FaceTime@123");
		Connection conn = DBUtils.getConnection(dbParam);
		Statement stmt = conn.createStatement();
		if(stmt != null){
//			System.out.println("Statement got Created...");
		}
		return stmt;
	}
	
}
