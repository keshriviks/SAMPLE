package com.actiance.test.sharepointNew.tests;


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

import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.actiance.test.sharepointNew.utils.DatabaseUtils;
import com.actiance.test.sharepointNew.utils.ListUtils;
import com.actiance.test.sharepointNew.utils.ServiceUtils;
import com.actiance.test.sharepointNew.utils.SiteUtils;
import com.actiance.test.sharepointNew.utils.TestDataObject;
import com.actiance.test.sharepointNew.utils.TestDataObjectCContent;
import com.actiance.test.sharepointNew.utils.TestUtils;
import com.actiance.test.sharepointNew.utils.TestUtilsCContent;
import com.independentsoft.share.FieldValue;
import com.independentsoft.share.List;
import com.independentsoft.share.ListItem;
import com.independentsoft.share.Service;
import com.independentsoft.share.ServiceException;

public class SPCContentTypeTest1 {

	ServiceUtils serUtils = new ServiceUtils();
	SiteUtils sitUtils = new SiteUtils();
	TestUtilsCContent utils = new TestUtilsCContent();
	DatabaseUtils db = new DatabaseUtils();
	ListUtils liUtils = new ListUtils();
	
	final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";
	String uniqueTimeStamp;
	
	Connection con;
	Statement stmt;
	ResultSet rs;
	
	Service service;
	
	HashMap<String, ArrayList<TestDataObjectCContent>> featuresMap;
	
	
	@BeforeSuite(alwaysRun=true)
	@Parameters({"dbType", "dbUrl", "dbUserName", "dbPassword", "siteURL", "username", "userPassword", "csvFile"})
	public void setupEnvironment(String dbType, String dbUrl, String dbUserName, String dbPassword, String siteURL, String username, String userPassword, String csvFile){
		
		//Set parameters
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
	
	
	
	
	
	@Test(groups={"Announcement","All"})
	public void testCAnnouncement(TestDataObjectCContent obj){
		
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
				service = serUtils.getServiceObject(System.getProperty("siteURL"), System.getProperty("username"), System.getProperty("userPassword"));
				
//				Get Type
				switch(obj.getType()){
				
				case "Annoucement" : 
					break;
					
				case "Link" : 
					break;
					
				case "Discussion": 
					break;
					
				case "Issue":
					break;
					
				case "Task":
					break;
					
				case "Users" :
					break;
					
				case "Comment" :
					break;
					
				case "Category" :
					break;
					
				case "CommunityMember" :
					break;
					
				case "Contact" :
					break;
					
				case "Curriculation" :
					break;
					
				case "EastAsiaContact" :
					break;
					
				case "Event" :
					break;
					
				case "Holiday" :
					break;
					
				case "Item" :
					break;
					
				case "Message" :
					break;
					
				case "NewWord" :
					break;
				
				case "OfficialNotice" :
					break;
					
				case "Post" :
					break;
					
				case "Resource" :
					break;
					
				case "ResourceGroup" :
					break;
					
				case "schedule and reservation" :
					break;
					
				case "schedule" :
					break;
					
				case "SiteMembership" :
					break;
				
				case "Timecard" :
					break;
					
				case "WhatsNewNotification" :
					break;
				
					default :
						System.out.println("Invalid Type : "+ obj.getType());
						
						break;
					
				}
			}catch(Exception e){
				}
		}
	}
	
//	Currently Site Related Custom Content Type can not automate through JShare...	
	@Test(groups={"Doucment", "All"}, dataProvider = "dpDoc")
	public void siteCContentType(TestDataObject obj) throws ServiceException, InterruptedException, ParseException, XPathExpressionException, FileNotFoundException{
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
			}catch(Exception e){
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
		ArrayList<TestDataObjectCContent> objects = featuresMap.get("Site");
		Object[][] result = new Object[objects.size()][1];
			
		for(int i=0; i<objects.size(); i++){
			result[i][0] = objects.get(i);
		}
		return result;
	}

}

