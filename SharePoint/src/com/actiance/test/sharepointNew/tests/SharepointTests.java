package com.actiance.test.sharepointNew.tests;

//import java.awt.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.xpath.XPathExpressionException;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.actiance.test.sharepointNew.utils.*;
import com.adventnet.snmp.snmp2.agent.SysOREntry;
import com.independentsoft.share.FieldValue;
import com.independentsoft.share.File;
import com.independentsoft.share.List;
import com.independentsoft.share.ListItem;
import com.independentsoft.share.Service;
import com.independentsoft.share.ServiceException;
//import com.independentsoft.share.queryoptions.IQueryOption;
//import com.independentsoft.share.Service;
//import com.independentsoft.share.ServiceException;
import com.independentsoft.share.Site;
import com.independentsoft.share.SiteCreationInfo;



public class SharepointTests {
	
	ServiceUtils serUtils = new ServiceUtils();
	SiteUtils sitUtils = new SiteUtils();
	TestUtils utils = new TestUtils();
	DatabaseUtils db = new DatabaseUtils();
	ListUtils liUtils = new ListUtils();
	
	final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";
	String uniqueTimeStamp;
	
	Connection con;
	Statement stmt;
	ResultSet rs;
	
	Service service;
	
	HashMap<String, ArrayList<TestDataObject>> featuresMap;
	
	
	@BeforeSuite(alwaysRun=true)
	@Parameters({"dbType", "dbUrl", "dbUserName", "dbPassword", "siteURL", "username", "userPassword", "csvFile"})
	public void setupEnvironment(String dbType, String dbUrl, String dbUserName, String dbPassword, String siteURL, String username, String userPassword, String csvFile){
		
		//Set patameters
		System.setProperty("dbType", dbType);
		System.setProperty("dbUrl", dbUrl);
		System.setProperty("dbUserName", dbUserName);
		System.setProperty("dbPassword", dbPassword);
		System.setProperty("siteURL", siteURL.trim());
		System.setProperty("username",username.trim());
		System.setProperty("userPassword", userPassword.trim());
		
		
		//Read CSV file
		featuresMap = utils.getTestObjects(csvFile);
		
		//Connect to database
//		con = db.setConnection();
//		stmt = db.createStatement(con);
		System.out.println("**********************************************");
		
	}
	
	@BeforeMethod(alwaysRun = true)
	public void getDBConnected(){
		
		System.out.println("Connecting with DB");
		con = db.setConnection();
		stmt = db.createStatement(con);
		
	}	
	
	
	@AfterMethod(alwaysRun = true)
	public void closeDBConnection() throws SQLException{
		System.out.println("DB Connection is going to close...");
		if(stmt != null){
			stmt.close();
			System.out.println("Statement Closed");
		}
		if(con != null){
			con.close();
			System.out.println("Connection Closed");
		}
		System.out.println("**********************************************");
	}
	
//	@BeforeMethod
	public String getCurrentTimeStamp(){
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
//		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		date = cal.getTime();
		uniqueTimeStamp = String.valueOf(date.getTime()); 
		return uniqueTimeStamp;
	}
	
	
	
	//Execute Lists' Test Cases
	@Test(groups={"Lists", "All"}, dataProvider = "dpLists")
	public void lists(TestDataObject obj) throws ServiceException, InterruptedException, ParseException, XPathExpressionException, FileNotFoundException{
		boolean bRet = false;
		int interID = -1;
		//Skip the test if Execute equal No
		if(obj.getExecute().equals("No")){
			throw new SkipException("Execute Value has been set to 'No' so skipping the Test Case: "+obj.getTestCaseID());
					
		}else{
			try{
				interID = db.getTopMostInteractionID(stmt);
				System.out.println("------------------------------------ Test Case: "+obj.getTestCaseID()+" --------------------------------------");
				
				List list;
				ListItem listItem;
				ArrayList<ListItem> items;
				Date date = null;
                ArrayList<FieldValue> inputField = new ArrayList();
                ListUtils liUtils = new ListUtils(); 
                ListItem createdListItem = new ListItem();
				//Get Service object
				service = serUtils.getServiceObject(obj.getTenant(), obj.getUserName(), obj.getPassword());
				
				//Get Type
				switch (obj.getType()) {
				case "Announcement":
						
					// Perform the Action
					switch (obj.getAction()) {
					
					case "Create List":
						list = liUtils.getListObject(obj.getItemTitle(), obj.getItemDescription(), obj.getSiteTitle());
						List createdList = service.createList(list);
						//Verification
				        date = createdList.getCreatedTime();
				        System.out.println("List Is Successfully Created");
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;
					
					case "Create new Item":
						
						list = service.getListByTitle(obj.getSiteTitle());
						listItem = new ListItem();
                      
                        listItem.setTitle(obj.getItemTitle());

                        inputField = liUtils.getFieldValueForAnno(obj.getItemDescription(), obj.getExpiry());
                        createdListItem = service.createListItem(list.getId(), listItem);
                        if(inputField.size()>0){
                            service.setFieldValues(list.getId(), createdListItem.getId(), inputField);
                        }
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items, createdListItem.getId());
				        date = listItem.getModifiedTime();

				        System.out.println("Verifying the Data");
//                        Thread.sleep(120000);
                        bRet =  db.verifyInputParameters(stmt, obj, date, false,  true);
        
						break;
						
					case "Edit item":
						
						break;

					default:
						Assert.fail("Invalid Action");
						break;
					}
					
					break;
					
				case "Discussion Board":
					
					// Perform the Action
					switch (obj.getAction()) {
					
					case "Create List":
						list = liUtils.getListObject(obj.getItemTitle(), obj.getItemDescription(), obj.getSiteTitle());
						List createdList = service.createList(list);
						//Verification
				        date = createdList.getCreatedTime();
				       
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;
					
					case "Create New item":
						
						//Create List item
						listItem = new ListItem();
                        listItem.setTitle(obj.getItemTitle());
						
						//Get list
                        list = service.getListByTitle(obj.getSiteTitle());
                       
                        //Get Field values
                        ArrayList<FieldValue> fields; 
                        
                        switch (obj.getSubAction()) {
						case "Title Only":
							
							fields = liUtils.getFieldValuesForCreatingDiscussionBoardItem(obj.getItemTitle());
							//Create List Item
							ListItem newListItem = service.createListItem(list.getId(),listItem);
                            System.out.println(newListItem.getCreatedTime());
                            //Set the Field Values
                            service.setFieldValues(list.getId(), newListItem.getId(), fields);
                            //Verifying the Data
                            Thread.sleep(5000);
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = sitUtils.getListItemById(items, newListItem.getId());
					        date = listItem.getModifiedTime();

					        System.out.println("Verifying the Data");

				            bRet = db.verifyInputParameters(stmt, obj, date, false,  true);
							
				            break;
							
						case "Title And Body":
							fields = liUtils.getFieldVAluesForCreatingDiscussionBoardItem(obj.getItemTitle(), obj.getItemDescription());
							//Create List Item
							ListItem newListItem1 = service.createListItem(list.getId(),listItem);
                            System.out.println(newListItem1.getCreatedTime());
                            //Set the Field Values
                            service.setFieldValues(list.getId(), newListItem1.getId(), fields);
                            //Verifying the Data
                            Thread.sleep(5000);
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = sitUtils.getListItemById(items, newListItem1.getId());
					        date = listItem.getModifiedTime();
				            
					        System.out.println("Verifying the Data");
				            bRet =  db.verifyInputParameters(stmt, obj, date, false,  true);

				            break;
							
						case "Title Body and IsAQuestion":
							fields = liUtils.getFieldVAluesForCreatingDiscussionBoardItem(obj.getItemTitle(),obj.getItemDescription(),Boolean.parseBoolean(obj.getParam8()));
							//Create List Item 
							ListItem newListItem2 = service.createListItem(list.getId(),listItem);
                            System.out.println(newListItem2.getCreatedTime());
                            //Set the Field Values
                            service.setFieldValues(list.getId(), newListItem2.getId(), fields);
                            //Verifying the Data
                            Thread.sleep(5000);
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = sitUtils.getListItemById(items, newListItem2.getId());
					        date = listItem.getModifiedTime();

					        System.out.println("Verifying the Data");
				            bRet = db.verifyInputParameters(stmt, obj, date, false,  true);
							break;
							
						case "All Values":
							fields = liUtils.getFieldVAluesForCreatingDiscussionBoardItem(obj.getItemTitle(),obj.getItemDescription(),Boolean.parseBoolean(obj.getParam8()),Boolean.parseBoolean(obj.getParam9()));
							//Create List Item
							ListItem newListItem3 = service.createListItem(list.getId(),listItem);
                            System.out.println(newListItem3.getCreatedTime());
                            //Set the Field Values
                            service.setFieldValues(list.getId(), newListItem3.getId(), fields);
                            //Verifying the Data
                            Thread.sleep(5000);
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = sitUtils.getListItemById(items, newListItem3.getId());
					        date = listItem.getModifiedTime();

					        System.out.println("Verifying the Data");
				            bRet = db.verifyInputParameters(stmt, obj, date, false,  true);
							
				            break;
							
						default:
							Assert.fail("Invalid Sub Action");
	    					break;
						}
                        
                        break;
						
					case "Edit item":
						
						switch (obj.getSubAction()) {
						 case "Title":
							
							//Get List
							list = service.getListByTitle(obj.getSiteTitle());
							// Edit the ListItem
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = liUtils.getListItem(items, obj.getItemTitle());
							// Change the title
							FieldValue value = new FieldValue("Title", obj.getParam10());
							FieldValue value2 = new FieldValue("FileLeafRef", obj.getParam10());
							// Set Field Value to the ListItem
							ArrayList<FieldValue> newtitle = new ArrayList<>();
							newtitle.add(value);
							newtitle.add(value2);
							service.setFieldValues(list.getId(), listItem.getId(), newtitle);
							//Verification
							Thread.sleep(5000);
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = sitUtils.getListItemById(items, listItem.getId());
					        date = listItem.getModifiedTime();
					        
					        System.out.println("Verifying the Data");
					        bRet = db.verifyInputParameters(stmt, obj, date, true,  true);

					        break;
							      
						 case "Description":
							
							//Get List
							list = service.getListByTitle(obj.getSiteTitle());
							// Edit the ListItem
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = liUtils.getListItem(items, obj.getItemTitle());
							// Change the title
							FieldValue value1 = new FieldValue("Body", obj.getItemDescription());
							// Set Field Value to the ListItem
							service.setFieldValue(list.getId(), listItem.getId(), value1);
							//Verification
							Thread.sleep(5000);
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = sitUtils.getListItemById(items, listItem.getId());
					        date = listItem.getModifiedTime();

					        System.out.println("Verifying the Data");
					        bRet = db.verifyInputParameters(stmt, obj, date, true,  true);
							break;
						      
						 default:
								Assert.fail("Invalid Sub Action");
								break;
								
						     }
						
						break;

					case "Add reply":
						
						break;
						
					case "Edit reply":
						
						break;
						
					default:
						Assert.fail("Invalid Action");
						break;
					}
					
					break;
					
				case "Issue Tracking":
					
					switch (obj.getAction()) {
					
					case "Create List":
						list = liUtils.getListObject(obj.getItemTitle(), obj.getItemDescription(), obj.getSiteTitle());
						List createdList = service.createList(list);
						//Verification
				        date = createdList.getCreatedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);
						break;
					
					case "Create new item":
						
						//Get List
						list = service.getListByTitle(obj.getSiteTitle());
						//Create ListItem
						listItem = new ListItem(obj.getItemTitle());
						ListItem createdItem = service.createListItem(list.getId(), listItem);
						//Set Field Values to the ListItem
						ArrayList<FieldValue> fields;
						if(obj.getSubAction().equals("Mandatory")){
							fields = liUtils.getFieldValuesForCreatingIssueTrackingItem(obj.getItemTitle());
						}else{
							fields = liUtils.getFieldValuesForCreatingIssueTrackingItem(obj.getItemTitle(), obj.getParam7(), obj.getParam3(), obj.getParam4(), obj.getParam5(), "");
						}
						service.setFieldValues(list.getId(), createdItem.getId(), fields);
						Thread.sleep(5000);
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items, createdItem.getId());
				        date = listItem.getModifiedTime();
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        if(obj.getSubAction().equals("Mandatory")){
				        	bRet = db.verifyInputParameters(stmt, obj, date, false, false);
				        }else{
				        	bRet = db.verifyIssueTrackingInputParameters(stmt, obj, date, false, false);
				        }
						break;
						
					case "Edit item":
						
						//Get List
						list = service.getListByTitle(obj.getSiteTitle());
						//Edit the ListItem
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItem(items, obj.getItemTitle());
						//Change Description, Status and Comments
						FieldValue Des = new FieldValue("Comment", obj.getParam7());
						FieldValue stat = new FieldValue("Status", obj.getParam3());
						FieldValue comment = new FieldValue("V3Comments", obj.getParam5());
						ArrayList<FieldValue> fieldsToEdit = new ArrayList<FieldValue>();
						fieldsToEdit.add(Des);
						fieldsToEdit.add(stat);
						fieldsToEdit.add(comment);
						//Set Field Value to the ListItem
						service.setFieldValues(list.getId(), listItem.getId(), fieldsToEdit);
						Thread.sleep(5000);
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items, listItem.getId());
				        date = listItem.getModifiedTime();
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyIssueTrackingInputParameters(stmt, obj, date, true, false);
						break;
						
					default:
						Assert.fail("Invalid Action");
						break;
					}
					
					break;
					
				case "Links":
					
					// Perform the Action
					switch (obj.getAction()) {
					case "Create new item":
						
						//Create List item
						listItem = new ListItem();
                        listItem.setTitle(obj.getItemTitle());
						
						//Get list
                        list = service.getListByTitle(obj.getSiteTitle());
						
                        //Get Field values
                        ArrayList<FieldValue> fields; 
                        
                        switch (obj.getSubAction()) {
                        case "All Values":
                        	
                        	fields = liUtils.getFieldValuesForCreatingLinks(obj.getItemTitle(), obj.getItemDescription(), obj.getParam3());
                        	//Create List Item
							ListItem newListItem = service.createListItem(list.getId(),listItem);
                            System.out.println(newListItem.getCreatedTime());
                            //Set the Field Values
                            service.setFieldValues(list.getId(), newListItem.getId(), fields);
                        	break;
                        	
                        case "Mandatory":
                        	
                        	break;
                        	
                        default:
    						Assert.fail("Invalid Sub Action");
    						break;
                        }
                        
						break;
						
					case "Edit item":
						
						break;
						
					default:
						Assert.fail("Invalid Action");
						break;
					}
					break;
					
				case "Survey":
					
					break;
					
				case "Tasks":
					
					switch (obj.getAction()) {
					
					case "Create Tasks List":
						list = liUtils.getListObject(obj.getItemTitle(), obj.getItemDescription(), obj.getSiteTitle());
						List createdList = service.createList(list);
						//Verification
				        date = createdList.getCreatedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);
						break;
					
					case "Create new item":
						
						//Get List
						list = service.getListByTitle(obj.getSiteTitle());
						//Create ListItem
						listItem = new ListItem(obj.getItemTitle());
						ListItem createdItem = service.createListItem(list.getId(), listItem);
						//Set Field Values to the ListItem
						ArrayList<FieldValue> fields = sitUtils.getFieldValuesForCreatingTask(obj.getItemTitle());
						service.setFieldValues(list.getId(), createdItem.getId(), fields);
						Thread.sleep(5000);
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items, createdItem.getId());
				        date = listItem.getModifiedTime();

				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;
						
					case "Edit item":
						
						//Get List
						list = service.getListByTitle(obj.getSiteTitle());
						//Edit the ListItem
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItem(items, obj.getItemTitle());
						//Change DueDate
						FieldValue value = new FieldValue("DueDate", utils.addDaysInCurrentDate(2));
						//Set Field Value to the ListItem
						service.setFieldValue(list.getId(), listItem.getId(), value);
						//Verification
						Thread.sleep(5000);
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items, listItem.getId());
				        date = listItem.getModifiedTime();

				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, true, false);
						
				        break;
						
					default:
						Assert.fail("Invalid Action");
						break;
					}
					
				
				case "Wiki page":
					
					break;
				
					
				case "Contact" :
					switch(obj.getAction()){
					
					case "Create List":
						list = liUtils.getListObject(obj.getItemTitle(), obj.getItemDescription(), obj.getSiteTitle());
						List createdList = service.createList(list);
						//Verification
				        date = createdList.getCreatedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);
						break;
						
					case "Create Contact":

//						Getting List by it's name
						list = service.getListByTitle(obj.getSiteTitle());
						listItem = new ListItem();
						listItem.setTitle(obj.getItemTitle());
						inputField = liUtils.getFieldValueForContact(obj);
						
//						Creating ListItem
						ListItem createdItem = service.createListItem(list.getId(), listItem);
						
//						Setting Field Value for Newly Created ListItem
						for(FieldValue fv : inputField)
							service.setFieldValue(list.getId(), createdItem.getId(), fv);
						
//						Start Verification with DB
						System.out.println("Successfully Created, Into Verification...");
						bRet = db.verifyInputParameters(stmt,obj, createdItem.getModifiedTime(), false, false); // Verification Pending 
						break;
						
					case "Edit Contact" :
						break;
						
					default :
						System.out.println("Invalid Action Type Manish: "+obj.getAction());
							
					}
					break;
				
				case "Calendar":
					switch(obj.getActionType()){
					
					case "Create List":
						list = liUtils.getListObject(obj.getItemTitle(), obj.getItemDescription(), obj.getSiteTitle());
						List createdList = service.createList(list);
						//Verification
				        date = createdList.getCreatedTime();
				        System.out.println("List Is Successfully Created");
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = false;
//				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;
						
					case "Create Event" :
						list= service.getListByTitle(obj.getSiteTitle());
						
						listItem = new ListItem();
						listItem.setTitle(obj.getItemTitle());
						createdListItem = service.createListItem(list.getId(), listItem);
						
						inputField = liUtils.getFieldValueForEvent(obj);
						
						
						System.out.println("Setting Field Value");
						
						for(FieldValue fv: inputField){
							service.setFieldValue(list.getId(), createdListItem.getId(), fv);
						}
						date = createdListItem.getModifiedTime();
//						Verifying DB..
						System.out.println("Verifying DB...");
						bRet = db.verifyCalendarEvent(stmt, obj, date.getTime());
						
						break;
						
					case "Edit Event":
						break;
						
					default : 
						System.out.println("Invalid Action Type : "+obj.getActionType());
					}
					break;
					
				default:
					Assert.fail("Invalid Type");
					break;
				}
				
				
			}catch (ServiceException ex){
				
	        	System.out.println("Error: " + ex.getMessage());
	        	System.out.println("Error: " + ex.getErrorCode());
	        	System.out.println("Error: " + ex.getErrorString());
	        	System.out.println("Error: " + ex.getRequestUrl());
	        	ex.printStackTrace();
	        	
	        }finally{
	        	Assert.assertEquals(true, bRet, obj.getTestCaseID());
	        }
			
		}
		
		
		
	}
	
	
//	Execute List based Additional Content Type
	
	@Test(groups ={"Additional_Content", "All"})
	public void listBasedAdditionalContentType(TestDataObject obj){
		boolean bRet = false;
		int interID = -1;
		//Skip the test if Execute equal No
		if(obj.getExecute().equals("No")){
			throw new SkipException("Execute Value has been set to 'No' so skipping the Test Case: "+obj.getTestCaseID());
			
		}else{
			try{
				interID = db.getTopMostInteractionID(stmt);
				System.out.println("--------------------------------------- Test Case : "+ obj.getTestCaseID() + " --------------------------------------");
				List list ; 
				ListItem listItem = new ListItem();
				ListItem createdListItem ;
				ArrayList<FieldValue> inputFv = new ArrayList();
				ArrayList<ListItem> items = new ArrayList();
				Date date ;
				
				switch(obj.getAction()){
				
				case "Create": 
					break;
				case "Edit" : 
					break;
					
				}
				
			}catch(Exception e){
				
			}finally{
				Assert.assertEquals(true, bRet);
			}
		}
		
	}
	
	//Execute Sites Test Cases
	@Test(groups={"Site", "All"}, dataProvider = "dpSite")
	public void site(TestDataObject obj) throws ServiceException, InterruptedException, ParseException, XPathExpressionException, FileNotFoundException{
		boolean bRet = false;
		int interID = -1;
		//Skip the test if Execute equal No
		if(obj.getExecute().equals("No")){
			throw new SkipException("Execute Value has been set to 'No' so skipping the Test Case: "+obj.getTestCaseID());
			
		}else{
			try{
				
				System.out.println("------------------------------------ Test Case: "+obj.getTestCaseID()+" --------------------------------------");
				
				SiteCreationInfo siteInfo;
				List list;
				ListItem listItem;
				ArrayList<ListItem> items;
				Date date = null;
				//Get Service object
				
				service = serUtils.getServiceObject(obj.getTenant(), obj.getUserName(), obj.getPassword());
				interID= db.getTopMostInteractionID(stmt);
				//Identify the Type
				switch (obj.getType()) {
				case "Blog":
					service = serUtils.getServiceObject(obj.getTenant()+"/"+obj.getSiteTitle(), obj.getUserName(), obj.getPassword());
					
					switch(obj.getAction()){
				    case "Create Post":
				    	list = service.getListByTitle("Posts");
				       
				    	switch(obj.getSubAction()){
				        case "NA":
				        	listItem = new ListItem();
				        	listItem.setTitle(obj.getItemTitle());
				        	ArrayList<FieldValue> fields = liUtils.getFieldValuesForCreatingBlogPost(obj.getParam3(), obj.getItemDescription(), obj.getParam4());
				        	ListItem createdListItem = service.createListItem(list.getId(), listItem);
				        	service.setFieldValues(list.getId(), createdListItem.getId(), fields);
				        	//Verifying the Data
				        	Thread.sleep(5000);
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = sitUtils.getListItemById(items, createdListItem.getId());
					        date = listItem.getModifiedTime();
				            System.out.println("Verifying the Data");
//				            Thread.sleep(120000);
				            bRet = db.verifyInputParameters(stmt, obj, date, false, true);

				        break;
				        
				       case "Create Comment":
				        
				        break;
				        
				       case "Edit Comment":
				        
				        break;
				       }
				    	
				      break;
				      
				     case "Edit Post":
						list = service.getListByTitle("Posts");
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						ListItem listItem2 = sitUtils.getListItem(items, obj.getParam10());
						ArrayList<FieldValue> fields = liUtils.getFieldValuesForEditingBlogPost(obj.getItemTitle(), obj.getParam3(), obj.getItemDescription(), obj.getParam4());
						service.setFieldValues(list.getId(), listItem2.getId(), fields);
						Thread.sleep(5000);
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items, listItem2.getId());
				        date = listItem.getModifiedTime();

				        System.out.println("Verifying the Data");
//						Thread.sleep(120000);
						bRet = db.verifyInputParameters(stmt, obj, date, true, true);

						break;
				      
				     case "Comment":
						list = service.getListByTitle("COMMENTS");

						switch (obj.getSubAction()) {

						case "Create Comment":
							listItem = new ListItem();
							listItem.setTitle(obj.getItemTitle());
							ArrayList<FieldValue> fields2 = liUtils.getFieldValuesForCreatingBlogComment(obj.getItemDescription());
							ListItem createdListItem = service.createListItem(list.getId(), listItem);
							service.setFieldValues(list.getId(), createdListItem.getId(), fields2);
							Thread.sleep(5000);
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = sitUtils.getListItemById(items, createdListItem.getId());
					        date = listItem.getModifiedTime();

					        System.out.println("Verifying the Data");
//							Thread.sleep(120000);
							bRet = db.verifyInputParameters(stmt, obj, date, false, true);

							break;

						case "Edit Comment":
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							ListItem listItem3 = sitUtils.getListItem(items, obj.getParam10());
							ArrayList<FieldValue> fields3 = liUtils.getFieldValuesForEditingBlogComment(obj.getItemTitle(),obj.getItemDescription());
							service.setFieldValues(list.getId(), listItem3.getId(), fields3);
							Thread.sleep(5000);
							items = (ArrayList<ListItem>) service.getListItems(list.getId());
							listItem = sitUtils.getListItemById(items, listItem3.getId());
					        date = listItem.getModifiedTime();
						
					        System.out.println("Verifying the Data");
//							Thread.sleep(120000);
							bRet = db.verifyInputParameters(stmt, obj, date, true,  true);

							break;

						default:
							System.out.println("Invalid Sub Action");
							Assert.fail();
							break;
						}
				      break;
				      
				     case "Create Blog Category":
				         
				         list = service.getListByTitle("Categories");
				         listItem = new ListItem();
				         listItem.setTitle(obj.getItemTitle());
				         ListItem createdListItem = service.createListItem(list.getId(), listItem);
				         Thread.sleep(5000);
						 items = (ArrayList<ListItem>) service.getListItems(list.getId());
						 listItem = sitUtils.getListItemById(items, createdListItem.getId());
						 date = listItem.getModifiedTime();
						 
						 System.out.println("Verifying the Data");
//						 Thread.sleep(120000);
						 bRet = db.verifyInputParameters(stmt, obj, date, false,  true);
				         break;
				         
				     case "Edit Category" :
				         list = service.getListByTitle("Categories");
				         items = (ArrayList<ListItem>) service.getListItems(list.getId());
				         listItem = sitUtils.getListItem(items, obj.getParam10());
				         FieldValue fv = new FieldValue("Title", obj.getItemTitle());
				         service.setFieldValue(list.getId(), listItem.getId(), fv);
				         items = (ArrayList<ListItem>) service.getListItems(list.getId());
				         listItem = sitUtils.getListItemById(items, listItem.getId());
				         date = listItem.getModifiedTime();
				         
				         System.out.println("Verifying the Data");
//				         Thread.sleep(120000);
				         bRet = db.verifyInputParameters(stmt, obj, date, true,  true);

				         break;
				      
				     default:
				      System.out.println("Invalid Action Type : "+ obj.getAction());
				      Assert.fail();
				     }
					
					break;

				case "Enterprise Wiki":
					// Perform the Action
					switch (obj.getAction()) {
					case "Create Enterprise Wiki":
						
						siteInfo = sitUtils.getSiteCreationInfoObject(obj.getSiteTitle(), obj.getParam3(), obj.getParam4(), true);
						Site createdSite = service.createSite(siteInfo);
						System.out.println("MasterUrl: " + createdSite.getMasterUrl());
				        System.out.println("ServerRelativeUrl: " + createdSite.getServerRelativeUrl());          
				        //Verification
				        date = createdSite.getCreatedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;

					case "Add Wiki Page":
						// Check for sub action
						break;

					case "Edit Wiki Page":
						// Check for sub action
						break;

					default:
						Assert.fail("Invalid Action");
						break;
					}
					
					break;
					
				case "Project Site":
					switch (obj.getAction()) {
					case "Create Project Site":
						
						siteInfo = sitUtils.getSiteCreationInfoObject(obj.getSiteTitle(), obj.getParam3(), obj.getParam4(), true);
						Site createdSite = service.createSite(siteInfo);
						System.out.println("MasterUrl: " + createdSite.getMasterUrl());
				        System.out.println("ServerRelativeUrl: " + createdSite.getServerRelativeUrl());   
				        //Verification
				        date = createdSite.getCreatedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;
						
					case "Create Tasks List":
						list = liUtils.getListObject(obj.getItemTitle(), obj.getItemDescription(), obj.getSiteTitle());
						List createdList = service.createList(list);
						//Verification
				        date = createdList.getCreatedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;
					
					case "Create Task":
						
						//Get List
						list = service.getListByTitle(obj.getSiteTitle());
						//Create ListItem
						listItem = new ListItem(obj.getItemTitle());
						ListItem createdItem = service.createListItem(list.getId(), listItem);
						//Set Field Values to the ListItem
						ArrayList<FieldValue> fields = sitUtils.getFieldValuesForCreatingTask(obj.getItemTitle());
						service.setFieldValues(list.getId(), createdItem.getId(), fields);
						//Verification
						Thread.sleep(5000);
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items,createdItem.getId());
				        date = listItem.getModifiedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;
						
					case "Edit Task":
						
						//Get List
						list = service.getListByTitle(obj.getSiteTitle());
						//Edit the ListItem
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItem(items, obj.getItemTitle());
						//Change DueDate
						FieldValue value = new FieldValue("DueDate", utils.addDaysInCurrentDate(2));
						//Set Field Value to the ListItem
						service.setFieldValue(list.getId(), listItem.getId(), value);
						//Verification
						Thread.sleep(5000);
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items,listItem.getId());
				        date = listItem.getModifiedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, true, false);

				        break;
						
					default:
						Assert.fail("Invalid Action");
						break;
					}
		
					break;
		
				case "Community Site":
					
					switch (obj.getAction()) {
					case "Create Community Site":
						
						siteInfo = sitUtils.getSiteCreationInfoObject(obj.getSiteTitle(), obj.getParam3(), obj.getParam4(), true);
						Site createdSite = service.createSite(siteInfo);
						System.out.println("MasterUrl: " + createdSite.getMasterUrl());
				        System.out.println("ServerRelativeUrl: " + createdSite.getServerRelativeUrl());   
				        //Verification
				        date = createdSite.getCreatedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;
						
					case "Create Discussion":
						
						//Get List
						list = service.getListByTitle(obj.getSiteTitle());
						//Create ListItem
						listItem = new ListItem(obj.getItemTitle());
						ListItem createdItem = service.createListItem(list.getId(), listItem);
						//Set Field Values to the ListItem
						ArrayList<FieldValue> fields = sitUtils.getFieldValuesForCreatingDiscussionBoard(obj.getItemTitle(), obj.getItemDescription(), obj.getParam3());
						service.setFieldValues(list.getId(), createdItem.getId(), fields);
						
						//Verification
						Thread.sleep(5000);
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items,createdItem.getId());
				        date = listItem.getModifiedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;

					case "Edit Discussion":
						
						//Get List
						list = service.getListByTitle(obj.getSiteTitle());
						////Edit the ListItem
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItem(items, obj.getItemTitle());
						//Change Body
						FieldValue value = new FieldValue("Body", obj.getItemDescription());
						//Set Field Value to the ListItem
						service.setFieldValue(list.getId(), listItem.getId(), value);
						//Verification
						Thread.sleep(5000);
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItemById(items, listItem.getId());
				        date = listItem.getModifiedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, true, false);

				        break;
						
					case "Add reply":
						
						
						//Get List
						list = service.getListByTitle(obj.getSiteTitle());
						//Get the ListItem
						items = (ArrayList<ListItem>) service.getListItems(list.getId());
						listItem = sitUtils.getListItem(items, obj.getItemTitle());
						//Create ListItem
						ListItem listItem2 = new ListItem();
						ListItem createdItem2 = service.createListItem(list.getId(), listItem2);
						//Reply to the listItem
						ArrayList<FieldValue> replyFields = sitUtils.getFieldValuesForAddingReplyToDiscussion(obj.getItemDescription(), listItem.getId()+"");
						service.setFieldValues(list.getId(), createdItem2.getId(), replyFields);
						
						break;
						
					case "Edit reply":
						//Reply to the listItem
						/*FieldValue body = new FieldValue("Body", obj.getParam2());
						FieldValue parentId = new FieldValue("ParentItemID", listItem.getId()+"");
						ArrayList<FieldValue> replyFields = new ArrayList<FieldValue>();
						replyFields.add(body);
						replyFields.add(parentId);
						service.setFieldValues(list.getId(), listItem.getId(), replyFields);*/
						break;
						
					default:
						Assert.fail("Invalid Action");
						break;
					}
					
					break;
					
				case "Site":
					FileInputStream fileStream = null;
					
					switch (obj.getAction()) {
					case "Create Site":
						
						siteInfo = sitUtils.getSiteCreationInfoObject(obj.getSiteTitle(), obj.getParam3(), obj.getParam4(), true);
						Site createdSite = service.createSite(siteInfo);
						System.out.println("MasterUrl: " + createdSite.getMasterUrl());
				        System.out.println("ServerRelativeUrl: " + createdSite.getServerRelativeUrl());   
				        //Verification
				        date = createdSite.getCreatedTime();
				        
				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
				        bRet = db.verifyInputParameters(stmt, obj, date, false, false);

				        break;

//					case "Add File":
//						
//						fileStream = new FileInputStream(obj.getParam3());
//						com.independentsoft.share.File createdFile = service.createFile(obj.getParam4()+obj.getItemTitle(), fileStream);
//						 //Verification
//				        date = createdFile.getCreatedTime();
//				        System.out.println("Verifying the Data");
//				        Thread.sleep(120000);
//				        bRet = db.verifyInputParameters(stmt, obj, date, false, true);
//				        Assert.assertEquals(true, db.verifyInputParameters(stmt, obj, date, false, true), "Test Case: "+obj.getTestCaseID());
//						break;
//					
//					case "Update File":
//						
//						list = service.getListByTitle(obj.getSiteTitle());
//						items = (ArrayList<ListItem>) service.getListItems(list.getId());
//						listItem = items.get(0);
//						FieldValue value = new FieldValue("Title", obj.getItemTitle());
//						service.setFieldValue(list.getId(), listItem.getId(), value);
//						//Verification
//						Thread.sleep(5000);
//						items = (ArrayList<ListItem>) service.getListItems(list.getId());
//						listItem = sitUtils.getListItem(items, obj.getItemTitle());
//				        date = listItem.getModifiedTime();
//				        Thread.sleep(120000);
//				        bRet = db.verifyInputParameters(stmt, obj, date, true, true);
//				        Assert.assertEquals(true, db.verifyInputParameters(stmt, obj, date, true, true), "Test Case: "+obj.getTestCaseID());
//						break;

					default:
						Assert.fail("Invalid Action");
						break;
					}
					
				default:
					Assert.fail("Invalid Type");
					break;
				}
				
				
				
			}catch (ServiceException ex){
				
	        	System.out.println("Error: " + ex.getMessage());
	        	System.out.println("Error: " + ex.getErrorCode());
	        	System.out.println("Error: " + ex.getErrorString());
	        	System.out.println("Error: " + ex.getRequestUrl());
	        	ex.printStackTrace();
	        }finally{
	        	Assert.assertEquals(true, bRet, obj.getTestCaseID());
	        }
			
		}
		
	}
	
	
	
	//Execute Libraries Test Cases
	@Test(groups={"Libraries", "All"}, dataProvider = "dpLibraries")
	public void libraries(TestDataObject obj){
		
		System.out.println("------------------------------------ Test Case: "+obj.getTestCaseID()+" --------------------------------------");
		Assert.assertEquals(true, true);
		
	}
	
//	Execute File Related Test case
	@Test(groups={"Files"}, dataProvider = "dpFiles")
	public void files(TestDataObject obj) throws IOException, InterruptedException{
		boolean bRet = false;
		List list = new List();
		ListItem listItem = new ListItem();
		Date date = null;
		Service service = null;
		File file = null;
		FileInputStream fileStream = null;
		int interID =-1;

		//Skip the test if Execute equal No
				if(obj.getExecute().equals("No")){
					throw new SkipException("Execute Value has been set to 'No' so skipping the Test Case: "+obj.getTestCaseID());
							
				}else{
					try{
						System.out.println("------------------------------------ Test Case: "+obj.getTestCaseID()+" --------------------------------------");
						service = serUtils.getServiceObject(obj.getTenant(), obj.getUserName(), obj.getPassword());
						fileStream = new FileInputStream(obj.getParam6());
						interID = db.getTopMostInteractionID(stmt);
//						System.out.println("file is Successfully Uploaded....");

				switch(obj.getType()){
							
					case "Announcement":
								
						switch(obj.getAction()){
								
						case "Title":
							file = service.createFile(obj.getParam3()+obj.getParam4()+obj.getParam5(), fileStream);
									
//					        Get the updated listItem 
							System.out.println("getting the Id of ListItem which is updated");
							list = service.getListByTitle(obj.getSiteTitle());
					        listItem =service.getListItem(list.getId(), Integer.parseInt(obj.getParam4())); 
					        date = listItem.getModifiedTime();
//							        into Verification
							        System.out.println("Into Verification...");
//							        Thread.sleep(120000);
							        bRet = db.verifyFile(obj, date.getTime(), file.getName(), file.getLength(), true, false, false);
							        
//							        bRet = db.verifyFile(obj.getUserName(), date.getTime(), file.getName(), file.getLength(), obj.getSiteTitle(), obj.getContentType(),obj.getContentSubType());
							        break;
	
						case "Body":
							break;
									
						}
						break;
								

					case "Discussion Board" : 
						file = service.createFile(obj.getParam3()+obj.getParam4()+obj.getParam5(), fileStream);
								
//						Get the updated listItem 
						System.out.println("getting the Id of ListItem which is updated");
						list = service.getListByTitle(obj.getSiteTitle());
					    listItem =service.getListItem(list.getId(), Integer.parseInt(obj.getParam4())); 
					    date = listItem.getModifiedTime();
//				        into Verification
//			        	Thread.sleep(120000);
					    System.out.println("Into Verification...");
				        bRet = db.verifyFile(obj, date.getTime(), file.getName(), file.getLength(), true, false, false);
						        
					        break;
								
					case "Issue Tracking":
						break;
								
					case "Links":
						break;
								
					case "Survey":
						break;
								
					case "Tasks":
						break;
								
					case "Blog":
								
						switch(obj.getAction()){
								
						case "Post":
							break;
								
						case "Comments":
							break;
									
							default : 
								System.out.println("Invalid Action : " + obj.getAction());
						}
						break;
								
							case "Enterprise Wiki page":
									break;
									
							case "Asset Library":
								list = service.getListByTitle(obj.getSiteTitle());
								System.out.println("List Id : "+list.getId());
								switch(obj.getAction()){
								case "Upload Document":
									switch(obj.getSubAction()){
									case "Name Field":
										
										file = service.createFile(obj.getParam3()+obj.getParam4()+obj.getParam5(), fileStream);
										file.getCreatedTime();
										
//										Into verification
										System.out.println("Verifying Data...");
//										Thread.sleep(150000);
										bRet = db.verifyFile(obj, file.getCreatedTime().getTime(), file.getName(), file.getLength(), true, false, false);
										
//										bRet = db.verifyFile(obj.getUserName(), file.getCreatedTime().getTime(), file.getName(), file.getLength(), obj.getSiteTitle(),obj.getContentType(),obj.getContentSubType());
										
										break;
										
									case "Title Field":
										FieldValue fv = new FieldValue("Title",obj.getParam7());
										System.out.println("Setting the field value..");
										service.setFieldValue(list.getId(), Integer.parseInt(obj.getParam8()), fv);
										listItem = service.getListItem(list.getId(), Integer.parseInt(obj.getParam8()));
										date = listItem.getModifiedTime();
										
//										Into verification
										System.out.println("Into Verification");
//										Thread.sleep(150000);
//										bRet = db.verifyFile(obj, date.getTime(), obj.getParam5(), Long.parseLong("0"), false, true, false);
										
//										bRet = db.verifyFile(obj.getUserName(), date.getTime(), obj.getParam5().replaceAll("/", ""), file.getLength(), obj.getSiteTitle(), obj.getContentType(), obj.getContentSubType());
										break;
										
									case "Keyword Field":
										FieldValue fv1 = new FieldValue("Keywords",obj.getParam7());
										service.setFieldValue(list.getId(), Integer.parseInt(obj.getParam8()), fv1);
										listItem = service.getListItem(list.getId(), Integer.parseInt(obj.getParam8()));
										date = listItem.getModifiedTime();
										
//										Into Verification
										System.out.println("Into Verification..");
//										Thread.sleep(150000);
										bRet = db.verifyFile(obj, date.getTime(), obj.getParam5(), Long.parseLong("0"), false, false, true);
										
//										bRet = db.verifyFile(obj.getUserName(), date.getTime(), obj.getParam5().replaceAll("/", ""), file.getLength(), obj.getSiteTitle(), obj.getContentType(),obj.getContentSubType());
										break;
										
										default :
											System.out.println("Invalid Sub Action : "+obj.getSubAction());
											break;
									}
									break;
									
								case "New Document":
//									interID = db.getTopMostInteractionID(stmt);
									System.out.println("Previous Interaction Id : "+interID);
									file = service.createFile(obj.getParam3()+obj.getParam4()+obj.getParam5(), fileStream);
//									file.getCreatedTime();
									
//									Into verification
									System.out.println("Into Verification...");
//									Thread.sleep(120000);
									bRet = db.verifyFile(obj, file.getCreatedTime().getTime(), file.getName(), file.getLength(), true, false, false);
//									bRet = db.verifyFile(obj.getUserName(), file.getCreatedTime().getTime(), file.getName(), file.getLength(), obj.getSiteTitle(), obj.getContentType(), obj.getContentSubType());
								default : 
									System.out.println("Invalid Sub Action type : "+obj.getAction());
									break;
						}
					break;
								
							case "Document Library":
								switch (obj.getAction()) {
								case "Create":
									
									file = service.createFile(obj.getParam3()+obj.getParam4()+obj.getParam5(), fileStream);
									file.getCreatedTime();
									
//									Into verification
									System.out.println("Verifying Data...");
//									Thread.sleep(120000);
									bRet = db.verifyFile(obj, file.getCreatedTime().getTime(), file.getName(), file.getLength(), true, false, false);
//									bRet = db.verifyFile(obj.getUserName(), file.getCreatedTime().getTime(), file.getName(), file.getLength(), obj.getSiteTitle(), obj.getContentType(),obj.getContentSubType());
									break;
								case "Update":
									
									break;

								default:
									System.out.println("Invalid Action: "+obj.getAction());
									break;
								}
								
								break;
								
							case "Picture Library":
								list = service.getListByTitle(obj.getSiteTitle());
								System.out.println("List Id : "+list.getId());
//								interID = db.getTopMostInteractionID(stmt);
								System.out.println("Previous Interaction Id : "+interID);
									switch(obj.getAction()){
									case "Name Field":
										file = service.createFile(obj.getParam3()+obj.getParam4()+obj.getParam5(), fileStream);
										file.getCreatedTime();
										
//										Into verification
										System.out.println("Verifying Data...");
//										Thread.sleep(120000);
										bRet = db.verifyFile(obj, file.getCreatedTime().getTime(), file.getName(), file.getLength(), true, false, false);
//										bRet = db.verifyFile(obj.getUserName(), file.getCreatedTime().getTime(), file.getName(), file.getLength(), obj.getSiteTitle(),obj.getContentType(),obj.getContentSubType());
										break;
										
									case "Title Field":
										FieldValue fv = new FieldValue("Title",obj.getParam7());
										service.setFieldValue(list.getId(), Integer.parseInt(obj.getParam8()), fv);
										listItem = service.getListItem(list.getId(), Integer.parseInt(obj.getParam8()));
										date = listItem.getModifiedTime();
													
			//							Into verification
										bRet = db.verifyFile(obj, file.getCreatedTime().getTime(), file.getName(), file.getLength(), false, true, false);
										break;
													
									case "Keywords Field":
										FieldValue fv1 = new FieldValue("Keyword",obj.getParam7());
										service.setFieldValue(list.getId(), Integer.parseInt(obj.getParam8()), fv1);
										listItem = service.getListItem(list.getId(), Integer.parseInt(obj.getParam8()));
										date = listItem.getModifiedTime();
													
			//							Into Verification
										bRet = db.verifyFile(obj, file.getCreatedTime().getTime(), file.getName(), file.getLength(), false, false, true);
										break;
													
									default :
										System.out.println("Invalid  Action : "+obj.getAction());
										break;
									}
									break;
									
						case "Form Library":
									file = service.createFile(obj.getParam3()+obj.getParam4()+obj.getParam5(), fileStream);
									file.getCreatedTime();
									
//									Into verification
									System.out.println("Verifying Data...");
//									Thread.sleep(120000);
									bRet = db.verifyFile(obj, file.getCreatedTime().getTime(), file.getName(), file.getLength(), true, false, false);
//									
//									bRet = db.verifyFile(obj.getUserName(), file.getCreatedTime().getTime(), file.getName(), file.getLength(), obj.getSiteTitle(),obj.getContentType(),obj.getContentSubType());
									break;
							
								
						case "Data connection library":
								list = service.getListByTitle(obj.getSiteTitle());
//								interID = db.getTopMostInteractionID(stmt);
								System.out.println("Previous Interaction Id : "+interID);
								switch(obj.getAction()){
								case "Name Field":
									file = service.createFile(obj.getParam3()+obj.getParam4()+obj.getParam5(), fileStream);
									file.getCreatedTime();
									
//									Into verification
									System.out.println("Verifying Data...");
//									Thread.sleep(120000);
									bRet = db.verifyFile(obj, file.getCreatedTime().getTime(), file.getName(), file.getLength(), true, false, false);
//									bRet = db.verifyFile(obj.getUserName(), file.getCreatedTime().getTime(), file.getName(), file.getLength(), obj.getSiteTitle(),obj.getContentType(),obj.getContentSubType());
									break;
								case "Title Field":
									FieldValue fv = new FieldValue("Title",obj.getParam7());
									service.setFieldValue(list.getId(), Integer.parseInt(obj.getParam8()), fv);
									System.out.println("Title is Created");
									listItem = service.getListItem(list.getId(), Integer.parseInt(obj.getParam8()));
									date = listItem.getModifiedTime();
									
//									Into verification
//									Thread.sleep(120000);
									bRet = db.verifyFile(obj, date.getTime(), obj.getParam5(), Long.parseLong("0"), false, true, false);
//									bRet = db.verifyFile(obj.getUserName(), date.getTime(), obj.getParam5().replaceAll("/", ""), file.getLength(), obj.getSiteTitle(), obj.getContentType(), obj.getContentSubType());
									break;
									
								case "Keyword Field":
									FieldValue fv1 = new FieldValue("Keywords",obj.getParam7());
									service.setFieldValue(list.getId(), Integer.parseInt(obj.getParam8()), fv1);
									System.out.println("Keywords Updated Successfully");
									listItem = service.getListItem(list.getId(), Integer.parseInt(obj.getParam8()));
									date = listItem.getModifiedTime();
									
//									Into Verification
									System.out.println("Verifying Data....");
//									Thread.sleep(120000);
									bRet = db.verifyFile(obj, date.getTime(), obj.getParam5(), Long.parseLong("0"), false, false, true);
//									bRet = db.verifyFile(obj.getUserName(), date.getTime(), obj.getParam5().replaceAll("/", ""), file.getLength(), obj.getSiteTitle(), obj.getContentType(),obj.getContentSubType());
									break;
									default : 
										System.out.println("Invalid Action : "+ obj.getAction());
								}
								
									
							case "Report library":
									list = service.getListByTitle(obj.getSiteTitle());
//									interID = db.getTopMostInteractionID(stmt);
									System.out.println("Previous Interaction Id : "+interID);
									switch(obj.getAction()){
									case "Name Field":
										file = service.createFile(obj.getParam3()+obj.getParam4()+obj.getParam5(), fileStream);
										file.getCreatedTime();
										
//										Into verification
										System.out.println("Verifying Data...");
//										Thread.sleep(120000);
										bRet = db.verifyFile(obj, file.getCreatedTime().getTime(), file.getName(), file.getLength(), true, false, false);
//										bRet = db.verifyFile(obj.getUserName(), file.getCreatedTime().getTime(), file.getName(), file.getLength(), obj.getSiteTitle(),obj.getContentType(),obj.getContentSubType());
										break;
									case "Title Field":
										FieldValue fv11 = new FieldValue("Title",obj.getParam7());
										service.setFieldValue(list.getId(), Integer.parseInt(obj.getParam8()), fv11);
										System.out.println("Title is Created");
										listItem = service.getListItem(list.getId(), Integer.parseInt(obj.getParam8()));
										date = listItem.getModifiedTime();
										
//										Into verification
//										Thread.sleep(120000);
										bRet = db.verifyFile(obj, date.getTime(), obj.getParam5(), Long.parseLong("0"), false, true, false);
//										bRet = db.verifyFile(obj.getUserName(), date.getTime(), obj.getParam5().replaceAll("/", ""), file.getLength(), obj.getSiteTitle(), obj.getContentType(), obj.getContentSubType());
										break;
								
//										Verification is pending for Category Field
									case "Category Field":
										FieldValue fv10 = new FieldValue("ReportCategory",obj.getParam7());
										service.setFieldValue(list.getId(), Integer.parseInt(obj.getParam8()), fv10);
										System.out.println("Keywords Updated Successfully");
										listItem = service.getListItem(list.getId(), Integer.parseInt(obj.getParam8()));
										date = listItem.getModifiedTime();
										
//										Into Verification
										System.out.println("Verifying Data....");
//										Thread.sleep(120000);
										bRet = db.verifyFile(obj, date.getTime(), obj.getParam5(), Long.parseLong("0"), false, false, true);
//										bRet = db.verifyFile(obj.getUserName(), date.getTime(), obj.getParam5().replaceAll("/", ""), file.getLength(), obj.getSiteTitle(), obj.getContentType(),obj.getContentSubType());
										break;
										
							/*		case "Status Field":
										FieldValue fv13 = new FieldValue("ReportCategory",obj.getParam7());
										service.setFieldValue(list.getId(), Integer.parseInt(obj.getParam8()), fv13);
										System.out.println("Keywords Updated Successfully");
										listItem = service.getListItem(list.getId(), Integer.parseInt(obj.getParam8()));
										date = listItem.getModifiedTime();
										
//										Into Verification
										System.out.println("Verifying Data....");
//										Thread.sleep(120000);
										bRet = db.verifyFile(obj, date.getTime(), obj.getParam5(), Long.parseLong("0"), false, false, true);
//										bRet = db.verifyFile(obj.getUserName(), date.getTime(), obj.getParam5().replaceAll("/", ""), file.getLength(), obj.getSiteTitle(), obj.getContentType(),obj.getContentSubType());
										break;*/
		
									default :
										System.out.println("Invalid  Action : "+obj.getAction());
										break;
								}
								break;
								
								default :
									System.out.println("Invalid type : "+obj.getType());
									break;
							}	
						}catch(ServiceException ex){
							
							System.out.println("Error: " + ex.getMessage());
//				        	System.out.println("Error: " + ex.getErrorCode());
//				        	System.out.println("Error: " + ex.getErrorString());
//				        	System.out.println("Error: " + ex.getRequestUrl());
				        	ex.printStackTrace();
							
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							 if(fileStream != null){
						         fileStream.close();
						      }
							 if(bRet){
								 System.out.println("Test Case : "+ obj.getTestCaseID()+  " is Successfully Passed");
							 }else{
								 System.out.println("Test Case : "+ obj.getTestCaseID() + " is Failed");
							 }
							Assert.assertEquals(true, bRet);
					}
				}
			}
		
	//Data Provider for Lists
	@DataProvider(name="dpLists")
	public Object[][] getListsObject(){
		
		ArrayList<TestDataObject> objects = featuresMap.get("Lists");
		Object[][] result = new Object[objects.size()][1];
		
		for(int i=0; i<objects.size(); i++){
			result[i][0] = objects.get(i);
		}
		
		
		return result;
	}
	
	//Data Provider for Site
	@DataProvider(name="dpSite")
	public Object[][] getSiteObject(){
		
		ArrayList<TestDataObject> objects = featuresMap.get("Site");
		Object[][] result = new Object[objects.size()][1];
		
		for(int i=0; i<objects.size(); i++){
			result[i][0] = objects.get(i);
		}
		
		return result;
	}
	
	
	//Data Provider for Libraries
	@DataProvider(name="dpLibraries")
	public Object[][] getLibrariesObject(){
		
		ArrayList<TestDataObject> objects = featuresMap.get("Libraries");
		Object[][] result = new Object[objects.size()][1];
		
		for(int i=0; i<objects.size(); i++){
			result[i][0] = objects.get(i);
		}
		
		return result;
	}
	
	
	@DataProvider(name="dpFiles")
	public Object[][] getFileObject(){
		
		ArrayList<TestDataObject> objects = featuresMap.get("Files");
		Object[][] result = new Object[objects.size()][1];
		
		for(int i=0; i<objects.size(); i++){
			result[i][0] = objects.get(i);
		}
		
		return result;
	}
}
