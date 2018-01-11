package com.actiance.test.sharepointNew.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.xpath.XPathExpressionException;

import junit.framework.Assert;

import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.actiance.test.sharepointNew.utils.DatabaseUtils;
import com.actiance.test.sharepointNew.utils.DatabaseUtilsCContent;
import com.actiance.test.sharepointNew.utils.ListUtils;
import com.actiance.test.sharepointNew.utils.ServiceUtils;
import com.actiance.test.sharepointNew.utils.SiteUtils;
import com.actiance.test.sharepointNew.utils.TestDataObject;
import com.actiance.test.sharepointNew.utils.TestDataObjectCContent;
import com.actiance.test.sharepointNew.utils.TestUtils;
import com.actiance.test.sharepointNew.utils.TestUtilsCContent;
import com.independentsoft.share.FieldValue;
import com.independentsoft.share.File;
import com.independentsoft.share.List;
import com.independentsoft.share.ListItem;
import com.independentsoft.share.Service;
import com.independentsoft.share.ServiceException;

public class SPCustomContentTest {
	
	ServiceUtils serUtils = new ServiceUtils();
	SiteUtils sitUtils = new SiteUtils();
	TestUtilsCContent utils = new TestUtilsCContent();
	DatabaseUtilsCContent db = new DatabaseUtilsCContent();
	ListUtils liUtils = new ListUtils();
	
	final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";
	String uniqueTimeStamp;
	
	Connection con;
	Statement stmt;
	ResultSet rs;
	
	Service service;
	
	HashMap<String, ArrayList<TestDataObjectCContent>> featuresMap;
	
	
	@BeforeSuite(alwaysRun=true)
	@Parameters({"dbType", "dbUrl", "dbUserName", "dbPassword", "siteURL", "username", "userPassword", "listName", "csvFile"})
	public void setupEnvironment(String dbType, String dbUrl, String dbUserName, String dbPassword, String siteURL, String username, String userPassword, String listName, String csvFile){
		
		//Set parameters
		System.setProperty("dbType", dbType);
		System.setProperty("dbUrl", dbUrl);
		System.setProperty("dbUserName", dbUserName);
		System.setProperty("dbPassword", dbPassword);
		System.setProperty("siteURL", siteURL.trim());
		System.setProperty("username",username.trim());
		System.setProperty("userPassword", userPassword.trim());
		System.setProperty("listName", listName.trim());
		
		
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
	
	
	
	
	@Test(groups={"List", "All"}, dataProvider = "dpList")
	public void listsCContentType(TestDataObjectCContent obj) throws ServiceException, InterruptedException, ParseException, XPathExpressionException, FileNotFoundException{
		boolean bRet = false;
		int interID = -1;
		//Skip the test if Execute equal No
		if(obj.getExecute().equals("No")){
			throw new SkipException("Execute Value has been set to 'No' so skipping the Test Case: "+obj.getTestCaseID());
					
		}else{
			try{
				System.out.println("------------------------------------ Test Case: "+obj.getTestCaseID()+" --------------------------------------");
				
				List list;
				ListItem listItem = new ListItem();
				ArrayList<ListItem> items;
				Date date = null;
                ArrayList<FieldValue> inputField = new ArrayList();
                ListUtils liUtils = new ListUtils(); 
                ListItem createdListItem = new ListItem();
                String buddyName =  System.getProperty("username");
                
				//Get Service object
				service = serUtils.getServiceObject(System.getProperty("siteURL"), System.getProperty("username"), System.getProperty("userPassword"));
				
//				Get Type
				switch(obj.getType()){
				
				case "Announcement" : 
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCAnnouncement(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					System.out.println("List Item Created Successfully, Setting up the fields");
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					System.out.println("Field values set successfully, Into verification");
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyInputParameters(stmt, obj, buddyName, date, false, false);
					
					break;
					
				case "Link" : 
					list = service.getListByTitle(obj.getFeatureName());
//					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCLink(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyInputParameters(stmt, obj, buddyName, date, false, false);
					break;
					
				case "Discussion": 
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCDiscussion(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyInputParameters(stmt, obj, buddyName, date, false, false);
					break;
					
				case "Issue":
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCIssue(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyIssueTrackingInputParameters(stmt, obj, buddyName, date, false, false);
					break;
					
				case "Task":
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCTask(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCTask(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Users" :
					list = service.getListByTitle(obj.getFeatureName());
//					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCUser(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCUser(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Comment" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCComment(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyInputParameters(stmt, obj, buddyName, date, false, false);
					break;
					
				case "Category" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCCategory(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCCategory(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Community Member" :
					list = service.getListByTitle(obj.getFeatureName());
//					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCCommunityMember(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCCommunityMember(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Contact" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCContact(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					System.out.println("Contact Getting created, Setting Field value ");
//					setting field values
					service.setFieldValues(list.getId(), createdListItem.getId(), inputField);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyContact(stmt, date.getTime(), buddyName, obj);
					break;
					
				case "Circulation" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCCirculation(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCCirculation(stmt, buddyName, obj, date.getTime());
					break;
					
				case "East Asia Contact" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCEastAsiaContact(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyContact(stmt, date.getTime(), buddyName, obj);
					break;
					
				case "Event" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCEvent(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCEvent(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Holiday" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCHoliday(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCHoliday(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Item" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCItem(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCMessages(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Message" :
					list = service.getListByTitle(obj.getFeatureName());
//					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCMessage(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCMessages(stmt, buddyName, obj, date.getTime());
					break;
					
				case "NewWord" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCNewWord(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCNewWord(stmt, buddyName, obj, date.getTime());
					break;
				
				case "Official Notice" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCOfficialNotice(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCOfficialNotice(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Post" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCPost(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCPost(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Resource" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCResource(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCResource(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Resource Group" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCResourceGroup(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCResource(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Schedule and Reservation" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCScheduleAndReservation(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCSchedule(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Schedule" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCSchedule(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCSchedule(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Site Membership" :
					list = service.getListByTitle(obj.getFeatureName());
//					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCSiteMembership(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCSiteMembership(stmt, buddyName, obj, date.getTime());
					break;
				
				case "Time card" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCTimeCard(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCTimecard(stmt, buddyName, obj, date.getTime());
					break;
					
				case "WhatsNewNotification" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCWhatsNewNotification(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCWhatsNewNotification(stmt, buddyName, obj, date.getTime());
					break;
					
				case "Summary Task" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCSummaryTask(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyInputParameters(stmt, obj, buddyName, date, false, false);
					break;
					
//				Not working...	
				case "WorkFlowTask" :
					list = service.getListByTitle(obj.getFeatureName());
					listItem.setTitle(obj.getItemTitle());
					inputField = liUtils.getFVForCWhatsNewNotification(obj);
					
					createdListItem = service.createListItem(list.getId(), listItem);
					
//					setting field values
					for(FieldValue fv : inputField)
						service.setFieldValue(list.getId(), createdListItem.getId(), fv);
					
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					createdListItem = sitUtils.getListItemById(items, createdListItem.getId());
					date = createdListItem.getModifiedTime();
//					Verifying DB..
					System.out.println("Verifying DB...");
					bRet = db.verifyCWhatsNewNotification(stmt, buddyName, obj, date.getTime());
					break;
					
				case "File":
					list = service.getListByTitle(obj.getFeatureName());
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					ListItem fileUploadLi = sitUtils.getListItemById(items, Integer.parseInt(obj.getParam20()));
					FileInputStream fileStream = null;
					fileStream = new FileInputStream(obj.getParam15());
					File file = service.createFile(obj.getParam16()+obj.getParam20()+obj.getParam18(), fileStream);
					System.out.println("File uploaded Successfully..");
					
//					Getting ListItem where file is uploaded...
					items = (ArrayList<ListItem>)service.getListItems(list.getId());
					fileUploadLi = sitUtils.getListItemById(items, Integer.parseInt(obj.getParam20()));
					date = fileUploadLi.getModifiedTime();
					
					System.out.println("Verifying DB....");
					
					bRet = db.verifyFile(obj, buddyName, date.getTime(), file.getName(), Long.parseLong("0"), true, false, false);
					break;

					default :
						System.out.println("Invalid Type : "+ obj.getType());
						
						break;
					
				}
			}catch(Exception e){
				e.printStackTrace();
				}
			finally{
				Assert.assertEquals(true, bRet);
			}
		}
	}
	
	@Test(groups={"Documents", "All"}, dataProvider = "dpDoc")
	public void siteCContentType(TestDataObjectCContent obj) throws ServiceException, InterruptedException, ParseException, XPathExpressionException, FileNotFoundException{
		boolean bRet = false;
		FileInputStream fileStream = null;
		//Skip the test if Execute equal No
		if(obj.getExecute().equals("No")){
			throw new SkipException("Execute Value has been set to 'No' so skipping the Test Case: "+obj.getTestCaseID());
					
		}else{
			try{
				
				System.out.println("------------------------------------ Test Case: "+obj.getTestCaseID()+" --------------------------------------");
				
				List list;
				ListItem listItem;
				ArrayList<ListItem> items;
				Date date = null;
                ArrayList<FieldValue> inputField = new ArrayList();
                
                ListUtils liUtils = new ListUtils(); 
                ListItem createdListItem = new ListItem();
                
                String buddyName =  System.getProperty("username");
                obj.setFeatureName("Documents");
                obj.setResourceName("Documents");
				//Get Service object
                service = serUtils.getServiceObject(System.getProperty("siteURL"), System.getProperty("username"), System.getProperty("userPassword"));
				fileStream = new FileInputStream(obj.getParam1());
//				Param2 = Shared Documents+ Param3 = "/"+ Param4 = "FileName For SP UI" , sParam7= extension (mp3,wav.jpg)
				File file = service.createFile(obj.getParam2()+obj.getParam3()+obj.getParam4()+obj.getParam7(), fileStream);
				System.out.println(obj.getFeatureName());
				
				list = service.getListByTitle(obj.getFeatureName());
				int getListCount = list.getItemCount();
				System.out.println(getListCount);
				
				FieldValue fv = new FieldValue("ContentTypeId" , obj.getParam6());
				service.setFieldValue(list.getId(), getListCount, fv);
				
				items = (ArrayList<ListItem>)service.getListItems(list.getId());
				createdListItem = sitUtils.getListItemById(items, getListCount);
				
				date = createdListItem.getModifiedTime();
				System.out.println("Verifying DB...");
				
				fileStream.close();
				
				bRet = db.verifyFile(obj, buddyName, date.getTime(), file.getName(), Long.parseLong("0"), true, false, false);
				
			}catch(Exception e){
				
				e.printStackTrace();
			}finally{
				Assert.assertEquals(bRet, true);
			}
			
		}
	}
	
	
	
	//Data Provider for Lists
	@DataProvider(name="dpList")
	public Object[][] getListsObject(){
		ArrayList<TestDataObjectCContent> objects = featuresMap.get("List");
		Object[][] result = new Object[objects.size()][1];
		
		for(int i=0; i<objects.size(); i++){
			result[i][0] = objects.get(i);
		}
		return result;
	}
		
	//Data Provider for Site
	@DataProvider(name="dpDoc")
	public Object[][] getSiteObject(){
		ArrayList<TestDataObjectCContent> objects = featuresMap.get("Documents");
		Object[][] result = new Object[objects.size()][1];
			
		for(int i=0; i<objects.size(); i++){
			result[i][0] = objects.get(i);
		}
		return result;
	}

}
