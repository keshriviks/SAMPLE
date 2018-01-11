package com.actiance.test.exporterNew.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DBUtils {
	
	ResultSet rs;
	static Statement stmt;
	CommonUtils commonUtils;
	
	public DBUtils(){
		
	}
	
	public DBUtils(ArrayList<String> dbParams){
		try{
			stmt = getConnection(dbParams).createStatement();	
		}catch(SQLException se){
			System.err.println(se.getMessage());
		}
		
	}
	
	public static Connection getConnection(ArrayList<String> dbDetails) {
		String driverName = dbDetails.get(0);
		String dbName = dbDetails.get(1);
		String DB_URL = dbDetails.get(2);
		String USER = dbDetails.get(3);
		String PASS = dbDetails.get(4);
		Connection conn = null;
		try {
			Class.forName(driverName);
			if (dbName != null) {
				conn = DriverManager.getConnection(DB_URL + ";database=" + dbName, USER, PASS);
			} else {
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
			}
		} catch (SQLException | ClassNotFoundException se) {
			System.err.println("DB Connection Error : " + se.getMessage());
		}
		return conn;
	}
	

	public static boolean updateExporter(HashMap<String,String> map,ArrayList<String> dbParams) throws SQLException {
		DBUtils db = new DBUtils();

		System.out.println("###########INSIDE EXPORTER UPDATE############");


		Connection conn = null;
		Statement stmt = null;
		BufferedReader bufRdr = null;
		
			conn = getConnection(dbParams);
			stmt = conn.createStatement();
			
			PreparedStatement preparedStatement = null;
			String updateTableSQL = "UPDATE SERVERPROPERTIES SET VALUE = ?, MODTIME = " +System.currentTimeMillis()
	                  + " WHERE NAME = ?";
			preparedStatement = conn.prepareStatement(updateTableSQL);
			
			
			 for (String name: map.keySet()){
				 
				 String key = name.toString();
				 String value = map.get(key);
				 if(key.contains("lastConfigUpdateTime")){
					 value = String.valueOf(System.currentTimeMillis());
				 }
				 if(key.contains("templateID")){
						value = String.valueOf(db.getTemplateId(stmt, map.get("xslt")));
					}
				if(name.contains("xslt")){
						continue;
				}
				 
				 preparedStatement.setString(1, value);
				 preparedStatement.setString(2, key);
				 preparedStatement.executeUpdate();
				 conn.commit();
				/*if(name.contains("lastConfigUpdateTime"))
				{
//					String key =name.toString();
				    long value = System.currentTimeMillis();  
				    String currentTime = String.valueOf(value);
				    System.out.println(key + " " + value);  
				    preparedStatement.setString(1, currentTime);
					preparedStatement.setString(2, key);
					preparedStatement.executeUpdate();
					conn.commit();
				}
				else
				{
					                String key =name.toString();
					                String value = map.get(name).toString();  
					                System.out.println(key + " " + value);  
					                preparedStatement.setString(1, value);
									preparedStatement.setString(2, key);
									preparedStatement.executeUpdate();
									conn.commit();
				}*/
				
	                
			 }

			
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}
			
			return true;
		
	}
	
	
//	get template id from DB from XSLT
	
	public int getTemplateId(Statement stmt, String templateName){
		int templateId = 0;
		String query = "select templateId from ExporterTemplates where templateName ='"+templateName + "'";
		try{
			rs = stmt.executeQuery(query);
			rs.next();
			templateId = rs.getInt("templateId");
		}catch(SQLException se){
			System.out.println("Error to fetch Template ID");
			System.err.println(se.getMessage());
			
		}
		return templateId;
	}
	
	
	// Old Method
	
/*
	public  ArrayList<Integer> getInterIDsBasedOnFilters(String conversationType, ArrayList<Integer> groupIDs, ArrayList<Integer> networkIDs,
			   boolean includeAll, boolean skippEmpty, String fromDate, String toDate, ArrayList<String> dbParams) throws SQLException{
			  
			  ArrayList<Integer> result = new ArrayList<Integer>();
			  HashSet<Integer> employees = new HashSet<Integer>();
			  String query = null;
			  int modalityID = 0;
			  
			  stmt = getConnection(dbParams).createStatement();
			  
			  //Get modalityType ID
			  if(conversationType != null && !conversationType.equals("")){
			   query = "Select modalityID from Modalities where name = '"+conversationType+"'";
			   System.out.println(query);
			   rs = stmt.executeQuery(query);
			   rs.next();
			   modalityID = rs.getInt(1);
			  }
			  
			  //Check if All Groups
			  if(groupIDs.size() == 1 && groupIDs.get(0) == -5){
			   
			   //Check if All Networks
			   if(networkIDs.size() == 1 && networkIDs.get(0) == -1){
				   
				   
			    query = "Select interID from Interactions where interType = "+modalityID;
			    System.out.println(query);
			    rs = stmt.executeQuery(query);
			    while(rs.next()){
			     result.add(rs.getInt(1));
			    }
			    
			    //include all or skip
			    
			    
			    
			    
			   }else{
			    for(int networkID : networkIDs){
			     query = "Select interID from Interactions where networkID = "+networkID+" and interType = "+modalityID;
			     System.out.println(query);
			     rs = stmt.executeQuery(query);
			     while(rs.next()){
			      result.add(rs.getInt(1));
			     }
			    }
			   }
			   
			  }else{
			   
			   //Get intEmplooyeID for each group
			   for(int groupID : groupIDs){
			    query = "Select intEmployeeID from GroupMembers where groupID = "+groupID;
			    rs = stmt.executeQuery(query);
			    while(rs.next()){
			     employees.add(rs.getInt(1));
			    }
			   }
			   
			   //Check if All Networks
			   if(networkIDs.size() == 1 && networkIDs.get(0) == -1){
			    
			    //Loop through each employee and get InterID
			    for(int intEmployeeID : employees){
			     query = "Select interID from Interactions where intEmployeeID = "+intEmployeeID+" and interType = "+modalityID;
			     System.out.println(query);
			     rs = stmt.executeQuery(query);
			     while(rs.next()){
			      result.add(rs.getInt(1));
			     }
			    }
			    
			   }else{
			    for(int intEmployeeID : employees){
			     //Loop through each Network
			     for(int networkID : networkIDs){
			      query = "Select interID from Interactions where intEmployeeID = "+intEmployeeID+" and networkID = "+networkID+" and interType = "+modalityID;
			      System.out.println(query);
			      rs = stmt.executeQuery(query);
			      while(rs.next()){
			       result.add(rs.getInt(1));
			      }
			     }
			    }
			   }
			  }
			  return result;
			 }
	*/

	public ArrayList<Integer> getInterIDsBasedOnFilters(HashMap<String, String> map, ArrayList<String> dbParams)
			throws SQLException {
		ArrayList<Integer> result = new ArrayList<Integer>();
		try {
			Statement stmt = getConnection(dbParams).createStatement();
			commonUtils = new CommonUtils();
//			Retrieve Filters of Properties file.
			ArrayList<Integer> groupIDs = commonUtils.returnGroupIDs(map);
			ArrayList<Integer> networkIDs = commonUtils.returnNetworkIDs(map);
			boolean dateRange = false;
			String toDate =  null;
			toDate = getTime(commonUtils.returnToDate(map));
			String fromDate = null;
			fromDate = null;
			fromDate = getTime(commonUtils.returnFromDate(map));
			String skipEmpty = commonUtils.returnSkipEmpty(map);
			String exporterType = commonUtils.getModality(map);
			int conversationType = commonUtils.returnConversationType(map);
			boolean conversationFlag = false;
			if(conversationType != -1){
				conversationFlag = true;
			}
			

			if (toDate != null && fromDate != null) {
				dateRange = true;
			}

			HashSet<Integer> employees = new HashSet<Integer>();
			String query = null;
			int modalityID = 0;

			// Get modalityType ID
			if (exporterType != null && !exporterType.equals("")) {
				query = "Select modalityID from Modalities where name = '" + exporterType + "'";
				System.out.println(query);
				rs = stmt.executeQuery(query);
				rs.next();
				modalityID = rs.getInt(1);
			}

			// Check if All Groups
			if (groupIDs.size() == 1 && groupIDs.get(0) == -5) {

				// Check if All Networks
				if (networkIDs.size() == 1 && networkIDs.get(0) == -1) {

					if (dateRange){
						if(conversationFlag){
							query = "Select interID from Interactions where interType = " + modalityID
									+ " and endTime between '" + fromDate + "' and '" + toDate + "' and internalFlag ='"+conversationType+"'" ;
						}else{
							query = "Select interID from Interactions where interType = " + modalityID
									+ " and endTime between '" + fromDate + "' and '" + toDate + "'";
						}
						System.out.println(query);
						rs = stmt.executeQuery(query);
						while (rs.next()) {
							result.add(rs.getInt(1));
						}

					} else {
						if(conversationFlag){
							query = "Select interID from Interactions where interType = " + modalityID + " and internalFlag ='"+conversationType+"'";
						}else{
							query = "Select interID from Interactions where interType = " + modalityID;
						}
						System.out.println(query);
						rs = stmt.executeQuery(query);
						while (rs.next()) {
							result.add(rs.getInt(1));
						}
					}

				} else {
					for (int networkID : networkIDs) {
						if (dateRange) {
							if(conversationFlag){
								query = "Select interID from Interactions where networkID = " + networkID
										+ " and interType = " + modalityID + " and endTime between '" + fromDate + "' and '"
										+ toDate + "' and internalFlag ='"+conversationType+"'";
							}else{
								query = "Select interID from Interactions where networkID = " + networkID
										+ " and interType = " + modalityID + " and endTime between '" + fromDate + "' and '"
										+ toDate + "'";
							}

							System.out.println(query);
							rs = stmt.executeQuery(query);
							while (rs.next()) {
								result.add(rs.getInt(1));
							}

						} else {
							if(conversationFlag){
								query = "Select interID from Interactions where networkID = " + networkID
										+ " and interType = " + modalityID + " and internalFlag ='"+conversationType+"'" ;
							}else{
								query = "Select interID from Interactions where networkID = " + networkID
										+ " and interType = " + modalityID;	
							}
							
							System.out.println(query);
							rs = stmt.executeQuery(query);
							while (rs.next()) {
								result.add(rs.getInt(1));
							}

						}

					}
				}

			} else {

				// Get intEmplooyeID for each group
				for (int groupID : groupIDs) {
					query = "Select intEmployeeID from GroupMembers where groupID = " + groupID;
					System.out.println(query);
					rs = stmt.executeQuery(query);
					while (rs.next()) {
						employees.add(rs.getInt(1));
					}
				}

				// Check if All Networks
				if (networkIDs.size() == 1 && networkIDs.get(0) == -1) {
					if (dateRange) {
						for (int intEmployeeID : employees) {
							query = "Select interID from Interactions where intEmployeeID = " + intEmployeeID
									+ " and interType = " + modalityID + " and endTime between '" + fromDate + "' and '"
									+ toDate + "' and internalFlag=1";
							System.out.println(query);
							rs = stmt.executeQuery(query);
							while (rs.next()) {
								result.add(rs.getInt(1));
							}
						}

					} else {
						// Loop through each employee and get InterID
						for (int intEmployeeID : employees) {
							query = "Select interID from Interactions where intEmployeeID = " + intEmployeeID
									+ " and interType = " + modalityID + " and internalFlag =1";
							System.out.println(query);
							rs = stmt.executeQuery(query);
							while (rs.next()) {
								result.add(rs.getInt(1));
							}
						}
					}

				} else {
					for (int intEmployeeID : employees) {
						if (dateRange) {
							// Loop through each Network
							for (int networkID : networkIDs) {
								query = "Select interID from Interactions where intEmployeeID = " + intEmployeeID
										+ " and networkID = " + networkID + " and interType = " + modalityID
										+ " and endTime between '" + fromDate + "' and '" + toDate + "' and internalFlag =1";
								System.out.println(query);
								rs = stmt.executeQuery(query);
								while (rs.next()) {
									result.add(rs.getInt(1));
								}
							}

						} else {
							// Loop through each Network
							for (int networkID : networkIDs) {
								query = "Select interID from Interactions where intEmployeeID = " + intEmployeeID
										+ " and networkID = " + networkID + " and interType = " + modalityID+" and internalFlag =1";
								System.out.println(query);
								rs = stmt.executeQuery(query);
								while (rs.next()) {
									result.add(rs.getInt(1));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return result;
	}
	
	public HashMap<Integer, HashMap<String, ArrayList<String>>> getMessageData(ArrayList<Integer> interIDs,ArrayList<String> dbParams){
		
        
        HashMap<Integer, HashMap<String, ArrayList<String>>> result = new HashMap();
        String query = null;
        ArrayList<String> message;
        HashMap<String, ArrayList<String>> messageMap;

//        stmt = getConnection(dbParams).createStatement(); 
        rs = null;
        try{
        	 //loop through each interID
            for(int interID : interIDs){
                   
                   int row = 0;
                   messageMap = new HashMap();
                   
                   query = "Select sentTime, buddyName, text from Messages where interID = "+interID+" order by sentTime";
                   System.out.println(query);
                   rs = stmt.executeQuery(query);
                   while(rs.next()){
                         
                         message = new ArrayList();
                         message.add(rs.getString(1));
                         message.add(rs.getString(2));
                         message.add(rs.getString(3).replaceAll("\n", "").replaceAll("\r", ""));
                         messageMap.put("Row: "+row++, message);
                   }
                   result.put(interID, messageMap);
            }
        }catch(SQLException se){
        	System.err.println(se.getMessage());
        }
        return result;
 }
	
	
	////////////////////////////////////// Testing Code /////////////////////////////////////////////////////////
	
	public HashMap<Integer, HashMap<String, ArrayList<String>>> getMessageDataIMTextXSLT(ArrayList<Integer> interIDs,ArrayList<String> dbParams, boolean fileAttachment) throws SQLException{
        
        HashMap<Integer, HashMap<String, ArrayList<String>>> result = new HashMap();
        String query = null;
        ArrayList<String> message;
        HashMap<String, ArrayList<String>> messageMap;

//        stmt = getConnection(dbParams).createStatement(); 
        rs = null;
        String systemText = null;
        
        //loop through each interID
        for(int interID : interIDs){
               
               int row = 0;
               messageMap = new HashMap();
               message = new ArrayList();
               query = "Select sentTime, buddyName, text, systemtext from Messages where interID = "+interID+" order by sentTime";
               System.out.println(query);
               rs = stmt.executeQuery(query);
               while(rs.next()){
            	   
            	   	 message = new ArrayList();
                     message.add(rs.getString(1));
                     message.add(rs.getString(2));
                     if(rs.getString(3) == null && !rs.getString(4).replaceAll("\n", "").replaceAll("\r", "").contains("<file>"))
                    	 message.add(rs.getString(4).replaceAll("\n", "").replaceAll("\r", ""));
                     else
                    	 message.add(rs.getString(3));
                     messageMap.put("Row: "+row++, message);
               }
               if(fileAttachment){
            	   ArrayList<String> fileMap = getFilesName(stmt, interID);
            	   if(fileMap.size() != 0){
            		   messageMap.put("Files ", fileMap);
            	   }
            	  
               }
               result.put(interID, messageMap);
        }
        return result;
 }
	
	///////////////////////////////////Testing code Finish /////////////////////////////////////////////////////
	
	
	
//	Get Attachments from FileXFers
	
	public ArrayList<String> getFilesName(Statement stmt, int interID){
		ArrayList<String> getFileNames = new ArrayList();
		String query = "select fileName from filexfers where interId ="+ interID;
		try{
			rs = stmt.executeQuery(query);
			while(rs.next()){
				getFileNames.add(rs.getString("fileName"));
			}				

		}catch(SQLException se){
			System.out.println("Error to fetch file name from Filexfer for Interaction id : "+ interID);
		}
		return getFileNames;
	}

	

	public static void resetExporterRecords(ArrayList<String> dbParams){
     String query = null;
     Connection conn = null;
     Statement stmt = null;
     try{
    	 conn = getConnection(dbParams);
         stmt = conn.createStatement();
         ResultSet rs = null;
         query = "UPDATE interactions set exportState = 0";
         System.out.println(query);				
         stmt.executeUpdate(query);
         query = "DELETE exportHistory";
         System.out.println(query);				
         stmt.executeUpdate(query);
     }catch(SQLException se){
    	 System.out.println("SQL Exception Occur");
     }
	}	
	
	
//		Get HTML formatted content Data for Collab from DB
	  
	  public HashMap<Integer, HashMap<String, String>> getMessageDataOfCollabForHTML(ArrayList<Integer> interIDs) throws SQLException, NumberFormatException, XPathExpressionException, ParserConfigurationException, SAXException, IOException{
          HashMap<Integer, HashMap<String, String>> result = new HashMap();
          String query = null;
          HashMap<String, String> message;
          HashMap<String, ArrayList<String>> messageMap;
          
          int contentTypeID;
          String contentType;
          int contentSubTypeID;
          String contentSubType;
          String containerId;
          int resourceID;
          String topic;
          SQLXML attrs ;
          int actionID;
          String actionType;
          String eventId;
          ArrayList<String> resources;
          String resourceName;
          String resourceURL;
          String text;
          String messageAttributes = null;
          HashMap<String, String> msgAttributes;
          
          DOMSource domSource;
          Document document;
          XPath xpath = XPathFactory.newInstance().newXPath();
          
          String expression = "//name[.='event.action']/following-sibling::*[1][name()='value']";
          
          //loop through each interID
          for(int interID : interIDs){
                 
                 message = new HashMap();
                 
                 query = "Select contentType, contentSubType, resourceID, attributes, roomName, eventId, containerID from Interactions where interID = "+interID;
                 System.out.println(query);
                 rs = stmt.executeQuery(query);
                 rs.next();
                 
                 contentTypeID = rs.getInt(1);
                 contentSubTypeID = rs.getInt(2);
                 resourceID = rs.getInt(3);
//                 System.out.println(rs.getSQLXML(4));

                 topic = rs.getString(5);
                 eventId = rs.getString(6);
                 containerId = rs.getString(7);
                 attrs = rs.getSQLXML(4);

           domSource = attrs.getSource(DOMSource.class);
           document = (Document) domSource.getNode();
           actionID = Integer.parseInt(xpath.evaluate(expression, document));
           
           //Get contentType
           contentType = getContentType(stmt, contentTypeID);
           //Get ContentSubType
           contentSubType = getContentSubType(stmt, contentSubTypeID);
           //Get Action Type
           actionType = getActionType(actionID);
           //Get Resource Name and URL
           resources = getResourceNameAndURL(resourceID);
           resourceName = resources.get(0);
           resourceURL = resources.get(1);
           
           //Get Message Table data
           
           query = "select text attributes from Messages where interID ="+interID + " and text IS NOT NULL";
//           query = "Select attributes from Messages where interID = "+interID;
           System.out.println(query);
                 rs = stmt.executeQuery(query);
                 rs.next();
                 text = rs.getString(1);
                 if(rs.getString(2) != null){
                	 messageAttributes = rs.getString(2);	 
                 }
                 
                 
                 //Get Attribute Names and Values
                 if(messageAttributes != null){
                	 msgAttributes = getMessageAttributes(messageAttributes);	 
                 }
                 msgAttributes = new HashMap();
                 
//                 Get File Names from Filexfers
                 ArrayList<String> fileNames = getFileListsForCollab(interID);
                 //Add all the values
                 if(topic !=null){
                	 message.put("Topic", topic);
                 }
                 if(eventId != null){
                	 message.put("Event ID", eventId);
                 }
                 if(containerId !=null){
                	 message.put("Container ID", containerId);
                 }
                 message.put("Content type", contentType);
                 message.put("Content sub-type", contentSubType);
                 message.put("Action", actionType);
                 message.put("Resource name", resourceName);
                 message.put("Resource URL", resourceURL);
                 message.put("Text", text);
                 if(msgAttributes.size() > 0){
                	 for(String attName : msgAttributes.keySet()){
                         message.put(attName, msgAttributes.get(attName));
                   }	 
                 }
                
       		  StringBuffer fileName1 = new StringBuffer();
    		  if(fileNames.size() > 0){
        		  for(int i = 0 ; i < fileNames.size() ; i++){
        			  fileName1.append(fileNames.get(i));
        			  if(i+1 != fileNames.size()){
        				  fileName1.append("#");
        			  }
        		  }    			  
    		  }
    		  message.put("File", fileName1.toString());
              result.put(interID, message);
          }
          
          return result;
   }
	  
	  
	  
//		Get HTML formatted content Data for Collab from DB
	  
	public HashMap<Integer, HashMap<String, String>> getMessageDataOfCollabForHTMLXSLT(ArrayList<Integer> interIDs)
			throws SQLException, NumberFormatException, XPathExpressionException, ParserConfigurationException,
			SAXException, IOException {
		HashMap<Integer, HashMap<String, String>> result = new HashMap();
		String query = null;
		HashMap<String, String> message;
		HashMap<String, ArrayList<String>> messageMap;

		int contentTypeID;
		String contentType;
		
		String objectID;
		String parentObjectID;
		String objectURI;
		String parentURI;
		String buddyName;
		int intEmployeeID;
		String employeeID;
		String employeeName;
		
		
		int contentSubTypeID;
		String contentSubType;
		String containerId;
		int resourceID;
		String topic;
		SQLXML attrs;
		int actionID;
		String actionType;
		String eventId;
		ArrayList<String> resources;
		String resourceName;
		String resourceURL;
		String text;
		String messageAttributes = null;
		HashMap<String, String> msgAttributes;

		DOMSource domSource;
		Document document;
		XPath xpath = XPathFactory.newInstance().newXPath();

		String expressionForAction = "//name[.='event.action']/following-sibling::*[1][name()='value']";
		String expressionForEventURI = "//name[.='event.eventItemURL']/following-sibling::*[1][name()='value']";
//		String expressionForParentEventURI = "//name[.='event.parentEventItemURL']/following-sibling::*[1][name()='value']";

		// loop through each interID
		for (int interID : interIDs) {

			message = new HashMap();

			query = "Select contentType, contentSubType, resourceID, attributes, roomName, eventId, containerID, buddyName, intEmployeeID from Interactions where interID = "
					+ interID;
			System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.next();

			contentTypeID = rs.getInt(1);
			contentSubTypeID = rs.getInt(2);
			resourceID = rs.getInt(3);
			// System.out.println(rs.getSQLXML(4));

			topic = rs.getString(5);
			eventId = rs.getString(6);
			containerId = rs.getString(7);
			attrs = rs.getSQLXML(4);

			domSource = attrs.getSource(DOMSource.class);
			document = (Document) domSource.getNode();
			actionID = Integer.parseInt(xpath.evaluate(expressionForAction, document));

			// Get contentType
			contentType = getContentType(stmt, contentTypeID);
			// Get ContentSubType
			contentSubType = getContentSubType(stmt, contentSubTypeID);
			// Get Action Type
			actionType = getActionType(actionID);
			// Get Resource Name and URL
			resources = getResourceNameAndURL(resourceID);
			resourceName = resources.get(0);
			resourceURL = resources.get(1);

			// Get Message Table data
			query = "Select text, attributes from Messages where interID = " + interID + " where text IS NOT NULL";
			System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.next();
			text = rs.getString(1);
			if (rs.getString(2) != null) {
				messageAttributes = rs.getString(2);
			}

			// Get Attribute Names and Values
			if (messageAttributes != null) {
				msgAttributes = getMessageAttributes(messageAttributes);
			}
			msgAttributes = new HashMap();

			// Get File Names from Filexfers
			ArrayList<String> fileNames = getFileListsForCollab(interID);
			// Add all the values
			if (topic != null) {
				message.put("Topic", topic);
			}
			if (eventId != null) {
				message.put("Event ID", eventId);
			}
			if (containerId != null) {
				message.put("Container ID", containerId);
			}
			message.put("Content type", contentType);
			message.put("Content sub-type", contentSubType);
			message.put("Action", actionType);
			message.put("Resource name", resourceName);
			message.put("Resource URL", resourceURL);
			message.put("Text", text);
			if (msgAttributes.size() > 0) {
				for (String attName : msgAttributes.keySet()) {
					message.put(attName, msgAttributes.get(attName));
				}
			}

			StringBuffer fileName1 = new StringBuffer();
			if (fileNames.size() > 0) {
				for (int i = 0; i < fileNames.size(); i++) {
					fileName1.append(fileNames.get(i));
					if (i + 1 != fileNames.size()) {
						fileName1.append("#");
					}
				}
			}
			message.put("File", fileName1.toString());
			result.put(interID, message);
		}

		return result;
	} 
	  
//	  Get File Names from FilexFers.
	  public ArrayList<String> getFileListsForCollab(Integer interId){
//		  HashMap<String,String> fileList = new HashMap();
		  ArrayList<String> fileNames = new ArrayList();
		  String query = "select filename from filexfers where interid ="+interId;
		  try{
			  rs = stmt.executeQuery(query);
			  while(rs.next()){
				  fileNames.add(rs.getString("filename"));
			  }
		  }catch(SQLException se){
			  System.err.println("No File : "+ se.getMessage());
		  }

//		  fileList.put("File", fileName1.toString());
		  return fileNames;
	  }
	  
//		 Get Data for Text Version Collab
		 
		 public  HashMap<Integer, HashMap<String, String>> getMessageDataForCollabForText(ArrayList<Integer> interIDs){
	         HashMap<Integer, HashMap<String, String>> result = new HashMap();
	         String query = null;
//	         stmt = getConnection(dbParams).createStatement();
	         HashMap<String, String> message;
	         HashMap<String, ArrayList<String>> messageMap;
	         
//	         stmt = getConnection(dbParams).createStatement();
	         int contentTypeID;
	         String contentType;
	         int contentSubTypeID;
	         String contentSubType = null;
	         int resourceID;
	         SQLXML attributes;
	         int actionID;
	         String actionType;
	         ArrayList<String> resources;
	         String resourceName;
	         String resourceURL;
	         String text = null;
	         String messageAttributes = null;
	         HashMap<String, String> msgAttributes = null;
	        try{
		         DOMSource domSource;
		         Document document;
		         XPath xpath = XPathFactory.newInstance().newXPath();
		         
		         String expression = "//name[.='event.action']/following-sibling::*[1][name()='value']";
	        	//loop through each interID
	        	for(int interID : interIDs){

	                
	                message = new HashMap();
	                
	                query = "Select contentType, contentSubType, resourceID, attributes from Interactions where interID = "+interID;
	                System.out.println(query);
	                rs = stmt.executeQuery(query);
	                rs.next();
	                
	                contentTypeID = rs.getInt(1);
	                contentSubTypeID = rs.getInt(2);
	                resourceID = rs.getInt(3);
	                attributes = rs.getSQLXML(4);
	                
	          domSource = attributes.getSource(DOMSource.class);
	          document = (Document) domSource.getNode();
	          actionID = Integer.parseInt(xpath.evaluate(expression, document));
	          
	          //Get contentType
	          contentType = getContentType(stmt, contentTypeID);
	          //Get ContentSubType
	          if(contentSubTypeID != -1){
	        	  contentSubType = getContentSubType(stmt, contentSubTypeID);
	          }
	          
	          //Get Action Type
	          actionType = getActionType(actionID);
	          //Get Resource Name and URL
	          resources = getResourceNameAndURL(resourceID);
	          resourceName = resources.get(0);
	          resourceURL = resources.get(1);
	          
	          //Get Message Table data
	          query = "Select text from Messages where interID = "+interID + " and text IS NOT NULL";
	          System.out.println(query);
	                rs = stmt.executeQuery(query);
	                rs.next();
	                if(rs.getNString(1) != null){
	                	text = rs.getString(1).replaceAll("/n", " ").replaceAll("/r", "");
	                }
	                
	           query = "select attributes from messages where interId ="+ interID + " and attributes IS NOT NULL";
	           System.out.println(query);
	            	rs = stmt.executeQuery(query);
	            	if(rs.next()){
	            		messageAttributes = rs.getString(1);	
	            	}
	                
	                
	                //Get Attribute Names and Values
	            	msgAttributes = new HashMap();
	            	if(messageAttributes != null){
	            		msgAttributes = getMessageAttributes(messageAttributes);
	            	}
	            		
	                ArrayList<String> fileNames = getFileListsForCollab(interID);
	                //Add all the values
	                message.put("contentType:", contentType);
	  	          if(contentSubTypeID != -1){
	  	        	message.put("contentSubType:", contentSubType);  
		          }
	                
	                message.put("action:", actionType.toLowerCase());
	                message.put("resourceName:", resourceName.toLowerCase());
	                message.put("Resource URL:", resourceURL.toLowerCase());
	                if(text != null){
	                	message.put("Text Content:", text.replaceAll("\n", "").replaceAll("\r", "").toLowerCase());
	                }
	                
	                if(msgAttributes.size() > 0){
		                for(String attName : msgAttributes.keySet()){
		                      message.put(attName+":", msgAttributes.get(attName).toLowerCase());
		                }	                	
	                }

	                StringBuffer fileName = new StringBuffer();
	                if(fileNames.size() > 0){
	                	  for(int i = 0 ; i < fileNames.size(); i++){
	  	                	fileName.append(fileNames.get(i));
	  	                	if(i + 1 != fileNames.size()){
	  	                		fileName.append("#");
	  	                	}
	  	                }
	  	                message.put("File:", fileName.toString().toLowerCase());
	                }
	
	                result.put(interID, message);
	         
	        	}
	        } catch(Exception e){
	        	System.err.println(e.getMessage());
	        }
	         return result;
	  }
	  
//		Get XML & CSV Content For Collab From DB
		public HashMap<Integer, HashMap<String, String>> getXMLMessageDataForCollabFromDB(ArrayList<Integer> interIDs) throws SQLException, NumberFormatException, XPathExpressionException, ParserConfigurationException, SAXException, IOException{
	        HashMap<Integer, HashMap<String, String>> result = new HashMap();
	        String query = null;
	        HashMap<String, String> message;
	        HashMap<String, ArrayList<String>> messageMap;
	        
//	        Select parentEventID,eventID,resourceName,roomName,networkID, buddyName, intemployeeID, containerID, contentType, contentSubType, resourceID, attributes
	        
	        String parentEventId;
	        String eventId;
	        String roomName;
	        String networkId;
	        String buddyName;
	        String intEmployeeId;
	        String employeeId = null;
	        String containerId;
	        String networkName;
	        String employeeEmail = null;
	        
	        int contentTypeID;
	        String contentType;
	        int contentSubTypeID;
	        String contentSubType = null;
	        int resourceID;
	        SQLXML attributes;
	        int actionID;
	        String actionType;
	        ArrayList<String> resources;
	        String resourceName;
	        String resourceURL;
	        String text;
	        String messageAttributes = null;
	        HashMap<String, String> msgAttributes = null;
	        
//	        stmt = getConnection(dbParams).createStatement();
	        
	        DOMSource domSource;
	        Document document;
	        XPath xpath = XPathFactory.newInstance().newXPath();
	        
	        String expression = "//name[.='event.action']/following-sibling::*[1][name()='value']";
	        
	        //loop through each interID
	        for(int interID : interIDs){
	               
	               message = new HashMap();
	               
	               query = "Select parentEventID,eventID,roomName,networkID, buddyName, intemployeeID, containerID, contentType, contentSubType, resourceID, attributes from Interactions where interID = "+interID;
//	               System.out.println(query);
	               rs = stmt.executeQuery(query);
	               rs.next();
	               parentEventId = rs.getString(1);
	               eventId = rs.getString(2);
	               roomName = rs.getString(3);
	               networkId = rs.getString(4);
	               buddyName = rs.getString(5);
	               intEmployeeId = rs.getString(6);
	               containerId = rs.getString(7);
	               contentTypeID = rs.getInt(8);
	               contentSubTypeID = rs.getInt(9);
	               resourceID = rs.getInt(10);
	               attributes = rs.getSQLXML(11);
	               
		         domSource = attributes.getSource(DOMSource.class);
		         document = (Document) domSource.getNode();
		         actionID = Integer.parseInt(xpath.evaluate(expression, document));
		         
		         //Get contentType
		         contentType = getContentType(stmt, contentTypeID);
		         //Get ContentSubType
		         if(contentSubTypeID != -1){
		        	 contentSubType = getContentSubType(stmt, contentSubTypeID);	 
		         }
		         
		         //Get Action Type
		         actionType = getActionType(actionID);
		         //Get Resource Name and URL
		         resources = getResourceNameAndURL(resourceID);
		         resourceName = resources.get(0);
		         resourceURL = resources.get(1);
//		         Get Network Name
		         networkName = getNetworkName(networkId);
//		         Get EmployeeId
		         if(intEmployeeId != null){
		        	 employeeId = getEmployeeId(stmt, intEmployeeId);
//			         Get Employee Email
			         
			         employeeEmail = getEmployeeEmail(stmt, intEmployeeId);
			         if(employeeEmail == null){
			        	 employeeEmail = buddyName;
			         }
		         }
		         

		         
		         //Get Message Table data
		         query = "Select text from Messages where interID = "+interID;
//		         System.out.println(query);
	               rs = stmt.executeQuery(query);
	               rs.next();
	               text = rs.getString(1).trim().replaceAll("\n", "").replaceAll("\r", "");
			      query = "Select attributes from Messages where interID = "+interID + " and attributes is not null";
//			      System.out.println(query);
		               rs = stmt.executeQuery(query);
		               if(rs.next()){
		            	   messageAttributes = rs.getString(1);
		               }
		            	   
	               msgAttributes = new HashMap();
	               //Get Attribute Names and Values
		               if(messageAttributes != null){
		            	   
		            	   msgAttributes = getMessageAttributes(messageAttributes); 	   
		               }
	               
	               
	               //Add all the values
	               message.put("parentEventID", parentEventId);
	               message.put("eventId", eventId);
	               message.put("containerId", containerId);
	               message.put("roomName", roomName);
	               message.put("buddyName", buddyName);
	               message.put("networkID", networkId);
	               message.put("networkName", networkName);
	               message.put("employeeId", employeeId);
	               message.put("employeeEmail", employeeEmail);
	               message.put("intEmployeeID", intEmployeeId);
	               message.put("Content type", contentType);
	               if(contentSubTypeID != 0){
	            	   message.put("Content sub-type", contentSubType);   
	               }
	               
	               message.put("Action", actionType);
	               message.put("Resource name", resourceName);
	               message.put("Resource URL", resourceURL);
	               message.put("Text", text);
	               if(msgAttributes.size() > 0){
	            	   message.putAll(msgAttributes);   
	               }
	               
//	               for(String attName : msgAttributes.keySet()){
//	                     message.put(attName, msgAttributes.get(attName));
//	               }

	               ArrayList<String> fileNames = getFileListsForCollab(interID);
	               StringBuffer fileName = new StringBuffer();
	               if(fileNames.size() > 0){
	            	   for(int  i = 0 ; i < fileNames.size(); i++){
	            		   fileName.append(fileNames.get(i));
	            		   if(i + 1 != fileNames.size()){
	            			   fileName.append("#");
	            		   }
	            	   }
	            	   message.put("File", fileName.toString());
	               }
	               
	               result.put(interID, message);
	        }
	        
	        return result;
	 }
	  
		public String getNetworkName(String networkId){
			String networkName = null;
			String query = "select name from networks where networkId ="+ networkId;
			try{
//				System.out.println(query);
				rs = stmt.executeQuery(query);
				rs.next();
				networkName = rs.getString(1);
			}catch(SQLException se){
				System.out.println("Error to find network Name");
			}
			return networkName;
		}
	    public String getContentType(Statement stmt, int contentTypeId){
	           String query = "select value from ContentTypes where typeId = "+ contentTypeId;
	           String contentType = null;
	           try{
	                  rs = stmt.executeQuery(query);
	                  rs.next();
	                  contentType = rs.getString("value");
	           }catch(SQLException se){
	                  System.out.println("Error to find Content Type Id");
	           }
	           return contentType;
	    }
	    
	    public String getContentSubType(Statement stmt, int contentSubTypeId){
	        String query = "select value from ContentSubTypes where typeId = "+ contentSubTypeId;
	        String contentSubType = null;
	        try{
	               rs = stmt.executeQuery(query);
	               rs.next();
	               contentSubType = rs.getString("value");
	        }catch(SQLException se){
	               System.out.println("Error to find Content Sub Type Id");
	        }
	        return contentSubType;
	    }
	    
	    public String getActionType(int actionID) throws SQLException {
	              String actionName = null;
	              String query = "select name from ActionTypes where actionID = "+ actionID;
//	              System.out.println(query);
	              rs = stmt.executeQuery(query);
	              rs.next();
	              actionName = rs.getString("name");
	              return actionName;
	       }

	    public ArrayList<String> getResourceNameAndURL(int resourceId) throws SQLException {
	              ArrayList<String> values = new ArrayList();
	              String query = "select resName, resURL from Resources where resourceId = "+resourceId;
//	              System.out.println(query);
	              rs = stmt.executeQuery(query);
	              rs.next();
	              values.add(rs.getString("resName"));
	              values.add(rs.getString("resURL"));
	              return values;
	       }
	    
	    public HashMap<String, String> getMessageAttributes(String attributes) throws ParserConfigurationException, SAXException, IOException{
	       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	              DocumentBuilder builder = factory.newDocumentBuilder();
	              InputStream stream;
	              Document doc;
	              
	              HashMap<String, String> values = new HashMap();
	              
	              stream = new ByteArrayInputStream(attributes.getBytes());
	              doc = builder.parse(stream);
	              
	              org.w3c.dom.Element root = doc.getDocumentElement();
	              
	              NodeList attrs = root.getElementsByTagName("attr");
	              
	              for(int k=0; k<attrs.getLength(); k++){
	                     
	                     Node attr = attrs.item(k);
	                     if(attr.getNodeType() == Node.ELEMENT_NODE){
	                           values.put(attr.getAttributes().item(0).getNodeValue(), attr.getTextContent());
	                           
	                     }
	              }
	              return values;
	    }

//	    get Employee Email id
		public String getEmployeeEmail(Statement stmt, String intEmployeeId){
			String empEmail = null;
			String query = "select value from employeeAttributes where intEmployeeId ="+ intEmployeeId + " and attrId =17"; 
			try{
//				System.out.println("Emp Email : "+ query);
				rs = stmt.executeQuery(query);
				rs.next();
				empEmail = rs.getString(1);
			}catch(SQLException se){
				System.err.println("Error to find Employee Email");
			}
			return empEmail;
		}
		
//		Get Employee Id
		public String getEmployeeId(Statement stmt, String intEmployeeId){
			String employeeId = null;
			String query = "Select employeeId from employees where intEmployeeId ="+intEmployeeId;
			try{
//				System.out.println("EMP Id : " + query);
				rs = stmt.executeQuery(query);
				rs.next();
				employeeId = rs.getString(1);
			}
			catch(SQLException se){
				System.err.println("Error to find Employee Id");
			}
			return employeeId;
			
		}
	  
	  public LinkedHashMap<String,String> getLastActivityOnTime(ArrayList<String> exporterNames, ArrayList<String> dbDetails) throws SQLException{
		  LinkedHashMap<String,String> lastActivityOn = new LinkedHashMap();
		  String query  = "select value from serverProperties where name = ?";
		  PreparedStatement pr = getConnection(dbDetails).prepareStatement(query);
		  try{
			  for(String exporterName : exporterNames){
				  String fieldName = exporterName+"lastExport.exportTime";
				  pr.setString(1, fieldName);
				  rs = pr.executeQuery();
				  rs.next();
				  lastActivityOn.put(exporterName, rs.getString(1));
				    
			  }
			   
		  }catch(SQLException se){
			  System.out.println("Error to find last Activity Time");
		  }
		  return lastActivityOn;
	  }
	  

//	  public ArrayList<String> getCompletedExporterList(LinkedHashMap<String,String> exporterLastActivityOnTime, ArrayList<String> dbParams){ 
//
//		  ArrayList<String> completedExporterName = new ArrayList<String>();
//		  Set<String> exporterNames = exporterLastActivityOnTime.keySet();
//		  HashMap<String,String> newExporterMap = new HashMap();
//		  try{
////			  for(int i = 0; i < 5; i++){
//				  for(String exporterName : exporterNames){
//					  if(exporterName.equalsIgnoreCase(exporterLastActivityOnTime.get(exporterName))){
//						  String oldActivityOnTime = exporterLastActivityOnTime.get(exporterName);
//						  String newActivityTime = getLastActivityTime(exporterName, dbParams);
//						  if(!oldActivityOnTime.equals(newActivityTime)){
//							  newExporterMap.put(exporterName, newActivityTime);
//						  }
//					  }
//					 
//				  }
//				  Thread.sleep(60000);
//			 // }
//			  
//			  for(String exporterName : newExporterMap.keySet()){
//				  completedExporterName.add(exporterName);
//			  }
//		
//		  }catch(Exception e){
//			  System.out.println("Exception Occur While Fetching Last Activity on Time from ServerProperties");
//		  }
//		  
//		  return completedExporterName;
//	  }
//
// 	public String getLastActivityTime(String exporterName, ArrayList<String> dbDetails){
// 		String query  = "select value from serverProperties where name = '"+exporterName+ "lastExport.exportTime'";
// 		String lastActivityOnTime = null;
// 		
//		  
//		  try{
//			  Statement stmt = getConnection(dbDetails).createStatement();
//				  rs = stmt.executeQuery(query);
//				  rs.next();
//				  lastActivityOnTime = rs.getString(1);
//		  }catch(SQLException se){
//			  System.out.println("Error to find last Activity Time");
//		  }
//		  return lastActivityOnTime;
// 	} 
 	
 	
// 	Get Exporter List which finished It's Job
 	
    public  ArrayList<String>getCompletedExporterList(HashMap<String,String> ExporterTemplete,ArrayList<String> dbParams, Long waitTime) throws SQLException, InterruptedException{
 		 String postFieldName = "lastExport.exportTime";
 		 ArrayList<String> arrayList=new ArrayList<String>(); 
 		 Boolean flag=true;
 		 String current=null;
 		 String query = "select name,value from ServerProperties where name like '%lastExport.exportTime'";
 		 HashMap<String,String> newExporter=new HashMap<String, String>();
 		 PreparedStatement stmt = getConnection(dbParams).prepareStatement(query);
 		 for (Map.Entry<String,String> me : ExporterTemplete.entrySet()) {
 //Appending field name into another hash map				
 			 newExporter.put(me.getKey().concat(postFieldName),me.getValue()); 
 	     }
 	
 //for recording for how many cycles the value is fixed
 		 HashMap<String, Integer> checkCount=new HashMap<String, Integer>(); 
 //for storing the previous value
 		 HashMap<String, String>  checkPrev=new HashMap<String, String>();
 //storing current value of database
 		 HashMap<String,String>  checkFix=new HashMap<String, String>();	
 		
 		 int count=0;
 		 while(count<7){
 			 rs=stmt.executeQuery();
 		
 			 while(rs.next()){
 //if the record is present in hash map or not
 			 if(newExporter.containsKey(rs.getString("name"))){ 
 				 
 				 current=rs.getString("value");
 //for the first time for every record	use flag				 
 				 if(flag==true){ 
 //setting default value from dump	 
 				checkPrev.put(rs.getString("name"), newExporter.get(rs.getString("name")));
 				 }
 				 checkFix.put(rs.getString("name"), current);
 //newhashmap for checking if the value are same to the previous value
 				 if(current.equalsIgnoreCase(checkPrev.get(rs.getString("name")))){

 					 if(checkCount.containsKey(rs.getString("name"))){
 //if same increase count
 					 checkCount.put(rs.getString("name"), checkCount.get(rs.getString("name"))+1); 
 					 }
 					 else{
 //for the first time initialize count
 						 checkCount.put(rs.getString("name"), 1);			
 					 }
 				 }
 				 else{
 					 checkCount.put(rs.getString("name"), 0);				//if the value is moving then set the count 0
 					 checkPrev.put(rs.getString("name"), current);			//change previous value with current value
 				 }
 			 }
 			
 		 }
 		flag=false;
 		 Thread.sleep(waitTime);
 		 count++;
 	} 
 		 
 	for(Map.Entry<String, Integer> me: checkCount.entrySet() ){
 //if the value is fixed for more then 2 minutes
 		if(me.getValue()>=0){		
 //check if latest value is same as Initial  value then it is not fixed
 			if(checkFix.get(me.getKey()).equalsIgnoreCase(newExporter.get(me.getKey()))){  
 				arrayList.add(me.getKey().replace("lastExport.exportTime", ""));
 			}
 //otherwise it is fixed add those value in array list
 			else if(me.getValue()>=2){								
 			arrayList.add(me.getKey().replace("lastExport.exportTime", ""));
 		}
 			else{
 				continue;
 			}
 		}
 	}
 		if(arrayList.size()==0){
 			System.out.println("No exporter is updated");
// 			return null;
 		}
 		 return arrayList;
 	 }
    
    
    
    
//    public ArrayList<String> removeExtraFromString(ArrayList<String> eMap){
//    	ArrayList<String> extra = new ArrayList();
//    	for(String str : eMap){
//    		int index = str.indexOf(".last");
//    		extra.add(str.substring(0,index));
//    	}
//    }
    
    public boolean verifyHeaders(HashMap<String,String> dbMap,int interId, HashMap<String,String> inputMap, String expotertemplete, Headers header){
    	boolean flag = false;
    	if(stmt!=null){
    		flag = header.verifyHeaders(dbMap, inputMap, interId, stmt, expotertemplete);
    	}else{
    		ArrayList<String> dbParams = new ArrayList();
    		dbParams.add(System.getProperty("driverName"));
    		dbParams.add(System.getProperty("dbName"));
    		dbParams.add(System.getProperty("db_url"));
    		dbParams.add(System.getProperty("user"));
    		dbParams.add(System.getProperty("password"));
    		try{
    			stmt = getConnection(dbParams).createStatement();
    			flag = header.verifyHeaders(dbMap, inputMap, interId, stmt, expotertemplete);
    		}catch(SQLException se){
    			System.out.println("Error to connect with DB");
    		}
    		
    		
    	}
    	
    	return flag;
    }
    
//  Checking interaction id status , weather it is duplicate or Exported and put it into Map
    public HashMap<Integer, String> getInterIDsStatus(ArrayList<Integer> interIDs, int networkID, int exporterNumber){
        
        HashMap<Integer, String> result = new HashMap();
        ArrayList<Integer> checkedInterIDs = new ArrayList();
        ArrayList<Integer> interIDsWithSameEventID;
        
        String query;
        String eventID;
        try{
            for(int interID : interIDs){
                
                if(!checkedInterIDs.contains(interID)){
                      
                      interIDsWithSameEventID = new ArrayList();
                      
                      query = "Select eventID from Interactions where interID = "+interID+" and networkID = "+networkID;
                      System.out.println(query);
                      rs = stmt.executeQuery(query);
                      rs.next();
                      eventID = rs.getString(1);
                      
                      query = "Select interID from Interactions where eventID = '"+eventID+"' and networkID = "+networkID;
                      System.out.println(query);
                      rs = stmt.executeQuery(query);
                      while(rs.next()){
                             interIDsWithSameEventID.add(rs.getInt(1));
                      }
                      
                      for(int intID : interIDsWithSameEventID){
                             query = "Select name from ExportStates where exportStateID = (Select exportStateID from ExportHistory where interID = "+
                                           intID+" and exporterNum = "+exporterNumber+")";
                             System.out.println(query);
                             rs = stmt.executeQuery(query);
                             rs.next();
                             
                             result.put(intID, rs.getString(1));
                      }      
                }
         }	
        }catch(Exception e){
        	System.out.println("Error to find Skip Duplicate Interaction");
        	e.printStackTrace();
        }
        return result;
     }
    
/*//    Get Empty Interaction
    public HashMap<Integer, String> checkEmptyInteractions(ArrayList<Integer> interIDs) throws SQLException{
        HashMap<Integer, String> result = new HashMap<>();
       
        String query;
        String text;
        String systemText;
        
        for(int interID : interIDs){
         query = "Select text, systemText from Messages where interID = "+interID;
         System.out.println(query);
         rs = stmt.executeQuery(query);
         rs.next();
         text = rs.getString(1);
         systemText = rs.getString(2);
         if(text == null && systemText == null){
          result.put(interID, "Empty Interaction");
          
         }else{
          result.put(interID, "Non-Empty Interaction");
         }
        }
        
        return result;
       }
    */
    
//  Get Empty Interaction
  public ArrayList<Integer> getEmptyInteractions(ArrayList<Integer> interIDs) throws SQLException{
      ArrayList<Integer> getEmptyInteractions = new ArrayList();
      String query;
      String text;
      String systemText;
      
      for(int interID : interIDs){
       query = "Select text, systemText from Messages where interID = "+interID;
       System.out.println(query);
       rs = stmt.executeQuery(query);
       rs.next();
       text = rs.getString(1);
       systemText = rs.getString(2);
       if(text == null && systemText == null){
    	   getEmptyInteractions.add(interID);
       }
      }
      return getEmptyInteractions;
     }
  
    /////////////////////////////////// Testing Code ////////////////////////////////////////////////////////
   public static void main(String... s) throws SQLException{
	   DBUtils dbu = new DBUtils(); 
	   ArrayList<Integer> grpId = new ArrayList();
	   grpId.add(-5);
	   
	   String fromDate = "16-02-2016";
	   String toDate = "19-02-2016";
	   
	   toDate = getTime(toDate);
	   fromDate = getTime(fromDate);
//	   System.out.println(getTime(toDate));
	   ArrayList<Integer> netId = new ArrayList();
	   netId.add(-1);
	   Statement stmt2 = null;
	   ArrayList<String> db = new ArrayList();
	   db.add("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	   db.add("Aut_2015R2SP2_14132");
	   db.add("jdbc:sqlserver://192.168.116.215:1433");
	   db.add("sa");
	   db.add("FaceTime@123");
	   
	   Connection con = getConnection(db);
	   stmt2 = con.createStatement();
	   HashMap<String,String> map= new HashMap();
	   map.put("imexport.2.filter.groupIDs", "-5");
	   map.put("imexport.2.filter.networkIDs", "-1");
	   map.put("imexport.2.filter.processFromDate", "16-02-2016");
	   map.put("imexport.2.filter.processToDate", "19-02-2016");
	   map.put("imexport.2.filter.chatTranscripts", "skip_empty");
	   map.put("imexport.2.filter.exporterType", "collab");
	   
	   ArrayList<Integer> interIds = dbu.getInterIDsBasedOnFilters(map,db);
	   
	   System.out.println(interIds);
	   
	   
	   
	   
   }
   
   public static String getTime(String date){
	   String convertor = null;
	   SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	   try {

			Date date1 = formatter.parse(date);
			convertor = String.valueOf(date1.getTime());

		} catch (ParseException e) {
			e.printStackTrace();
		}
	   return convertor;
   }
   
   public int getServerId(Statement stmt){
	   int serverId = 0;
	   String query = "select serverId from serverTable where adminIPAddr ='"+ System.getProperty("vantageIP") + "'";
	   try{
		   rs = stmt.executeQuery(query);
		   rs.next();
		   serverId = rs.getInt(1);
	   }catch(SQLException se){
		   System.out.println(se.getMessage());
	   }
	   return serverId;
   }
}
