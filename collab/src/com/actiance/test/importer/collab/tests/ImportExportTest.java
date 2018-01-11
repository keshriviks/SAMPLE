package com.actiance.test.importer.collab.tests;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.actiance.test.importer.collab.utils.ContentEvent;
import com.actiance.test.importer.collab.utils.ContentEvents;

import com.actiance.test.importer.collab.utils.ParsingFile;

//import junit.framework.Assert;

import com.actiance.test.importer.collab.utils.DatabaseUtils;
import com.actiance.test.importer.collab.utils.ImporterUtils;

public class ImportExportTest {
	ImporterUtils utils = new ImporterUtils();
	ContentEvents content = new ContentEvents();
	DatabaseUtils db = new DatabaseUtils();
//	CollabTestVerification collabTest = new CollabTestVerification();
	
	
	Statement stmt = null;
	
	@BeforeTest(alwaysRun = true)
	@Parameters({ "networkName", "folderLocation", "dbType", "dbName", "dbUserName", "dbPassword", "dbHost"})
	public void setUp(String networkName, String folderLocation, String dbType, String dbName, String dbUserName, String dbPassword, String dbHost) throws InterruptedException {
/*		
//		Setting the Parameters from XML into System Liabrary  
		System.setProperty("networkType", networkName.trim());
		System.setProperty("folderLocation", folderLocation.trim());
		System.setProperty("dbType", dbType.trim());
		System.setProperty("dbName", dbName.trim());
		System.setProperty("dbUserName", dbUserName.trim());
		System.setProperty("dbPassword", dbPassword.trim());
		System.setProperty("dbHost", dbHost.trim());
*/		
		System.out.println("Importer is running for **"+networkName+"**");
		
//	getting the statement object for Database verification	
		stmt = db.VantageDBConnectivity(dbHost, dbName, dbUserName, dbPassword);
/*		
		//Open browser and load url
		brow = new BrowSetupAndLoadUrl();
		driver = brow.openBrowserAndLoadUrl(profile, vantageIP);
		wait = new WebDriverWait(driver, 60);
		
		//Log in and set the importer
		Login log = PageFactory.initElements(driver, Login.class);
		Dashboard dashboard = log.gotoDashboard(vanUser, vanPwd);
		Configuration config = dashboard.gotoConfiguration();
		//ApiRegistration api = config.gotoApiRegistration();
		//AddNetwork add = api.gotoAdd();
		//ImportNetwork imp = add.gotoImportNetwork();
		//add = imp.importNetworkFile(networkFile);
		//String apiNetworkName = add.getNetworkName();
		//api = add.saveImportedNetworkFile();
		//config = api.gotoConfiguration();
		ImportSettings importSet = config.gotoImportSettings();
		importSet.setImporter(networkName, folderLocation, "Indii", "Collaboration View");
		config = importSet.gotoConfiguration();*/
	}
	
//	Release the Statement Object resources
/*	@AfterTest(alwaysRun=true)
	public void releaseResources() throws SQLException{
		stmt.close();
		driver.close();
	}*/
	
//	Test Method
	@Test(dataProvider = "collabFile", groups={"Sanity"})
	public void TestCollab(String fileName, ContentEvents content) {
		boolean bRet = false;
		try {
			
			System.out.println(System.getProperty("dbType"));
			System.out.println();
			System.out.println("**************************************************************************************************************************************************");
			System.out.println("----------------------------------Started Test for File :"+fileName+ "----------------------------------------------");
			System.out.println("**************************************************************************************************************************************************");
			System.out.println();
			//Check for Interaction
			boolean processed = true;
			boolean presence = false;
			boolean flag = false;
			boolean result = true;
			
			
			ArrayList<ContentEvent> contentEvent = content.getContentEvent();
			if(contentEvent!=null){
//			check the presence of interactions in DB for single File and one by one interaction.....
			for(ContentEvent event : contentEvent){
				if(db.isInteractionCreated(stmt, event.getEventTime(), event.getUserID())){
					if(!result){
						flag = false;
						System.out.println("There is a Interaction exist for Event Time: "+event.getEventTime()+" and UserID: "+event.getUserID()+" which is Incorrect");
					}
					flag = true;
				}else{
					flag = false;
				}
				
				result = result & flag;
			}
			
			if(!result){
				processed = false;
			}
			
//			Verify File Presence each folder
			presence = utils.verifyFilePresence(System.getProperty("folderLocation"), fileName, processed);
			
//			Interaction is found in Db but the file is not in ProcessedFile folder
			if(processed & !presence){
				System.out.println("File : "+fileName + " : is Processed but not in processedFiles Folder");
				bRet = false;
			}
			
//			File is not Processed(No Interaction in DB) but file is not in skipped or failed Folder
			else if(!processed & !presence){
				System.out.println("File : "+ fileName + " : is not processed but it is not in failedFiles or skippedFiles Foler");
				bRet = false;
			}
//			File is not processed(No Interaction in DB) and it is in other failed or skipped folder
			else if(!processed & presence){
				System.out.println("File : "+fileName+" : is Not processed and it is in Failed or Skipped Folder");
				bRet = true;
				
			}
//			 File is Processed (Interactions is Found ) and it is in ProcessedFile Folder
			else if (processed & presence) 
				bRet = new CollabImportExportVerification().verifyConentEvent(content, stmt);
			}
			else{
				System.out.println("No conversation in File : "+fileName);
				bRet = false;
			}	
		} catch (Exception e) {
//			System.out.println("Error in Parsing File");
			e.printStackTrace();
		}
			
		Assert.assertEquals(true, bRet);
//	assertEquals(true, bRet);
	}


//	 Providing the data for Test Method
	  @DataProvider(name="collabFile") 
	  public Object[][] parseFile(){

		  Object[][] fileObjectMap = null;
		  
		  //Get all the file names from the Importer Folder
		  
		  String[] allFileNames = utils.getAllFileNames(System.getProperty("folderLocation"));
		  ArrayList<String> onlyFiles = new ArrayList<String>();
		  System.out.println("***********************");
		  System.out.println("Files For Importing...");
		  System.out.println("***********************");
		  for(String file : allFileNames){
			  if(new File(System.getProperty("folderLocation")+"/"+file).isFile()){
				  onlyFiles.add(file);
				  
//				  System.out.println(file);
			  }
		  }
		  
		  fileObjectMap = new Object[onlyFiles.size()][2];
		  
		  for(int i=0; i<onlyFiles.size(); i++){
			  ContentEvents content = ParsingFile.parseFile(onlyFiles.get(i));
			  fileObjectMap[i][0] = onlyFiles.get(i);
			  fileObjectMap[i][1] = content;
		  }
		  //Run Importer
//		  System.out.println();
		  try {
			  System.out.println("Waiting for Importer Status......");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return fileObjectMap;
	  }
}
