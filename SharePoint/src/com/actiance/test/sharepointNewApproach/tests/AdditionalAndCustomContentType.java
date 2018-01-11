package com.actiance.test.sharepointNewApproach.tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.xpath.XPathExpressionException;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.actiance.test.sharepointNewApproach.utils.CSVObjectAddtionalAndCustomContentType;
import com.actiance.test.sharepointNewApproach.utils.DatabaseUtilsCustomContentType;
import com.actiance.test.sharepointNewApproach.utils.TestUtilsAdditionalContentType;


public class AdditionalAndCustomContentType {

	DatabaseUtilsCustomContentType db = new DatabaseUtilsCustomContentType();
	TestUtilsAdditionalContentType utils = new TestUtilsAdditionalContentType();
	
	HashMap<String, CSVObjectAddtionalAndCustomContentType> ObjectsMap;
	HashMap<String, HashMap<String, String>> testResultMap;
	HashMap<Integer, ArrayList<String>> interIDsMap;
	
	@BeforeTest(alwaysRun=true)
	@Parameters({"dbUrl", "dbUser", "dbPassword", "CSVFile"})
	public void setEnvironment(String dbUrl, String dbUser, String dbPassword, String CSVFile) throws IOException{
		//Connect to Database
		db.setConnection(dbUrl, dbUser, dbPassword);
		db.createStatement();
		
		//Read CSV File
		ObjectsMap = utils.getCSVObjects(CSVFile);
	}
	
	@AfterTest(alwaysRun=true)
	public void closeConnections(){
		db.closeDBConnection();
	}
	
	@Test
	public void sharepointTest() throws NumberFormatException, SQLException, XPathExpressionException{

		boolean result = true;
		Long startTime = System.currentTimeMillis();
		Long endTime;
		endTime = startTime;
		int i = 0;
		
		ArrayList<Integer> interIDs = new ArrayList<>();
		HashMap<String, String> testResult = null;
		
		testResultMap = utils.getTestResultMap();
		
		if(testResultMap.size() == 0){
			result = false;
		}
		
		while(!utils.endTest(startTime, endTime)){
			System.out.println("--------------------- Loop: "+(i++)+" -------------------------");
			
			//Query Database and get Interactions
			interIDsMap = db.getInterIDsMap(interIDs);
			
			System.out.println("Number of InterIDs found: "+interIDsMap.size());
			
			for(int interID : interIDsMap.keySet()){
				interIDs.add(interID);
				
				for(String testCaseNo : ObjectsMap.keySet()){
					
					//Check whether the Test Case has already been verified
					if(!testResultMap.get(testCaseNo).get("Result").equals("Pass")){
						
						if(interIDsMap.get(interID).get(1).equals(ObjectsMap.get(testCaseNo).getTime1())){
							System.out.println();
							if(db.verifyDatabase(ObjectsMap.get(testCaseNo), interIDsMap.get(interID))){
								System.out.println("CSV Values match with the Database values for Test Case Number: "+ObjectsMap.get(testCaseNo).getTestCaseNo());
								testResult = testResultMap.get(testCaseNo);
								testResult.put("InterID", interID+"");
								testResult.put("Result", "Pass");
								testResultMap.put(testCaseNo, testResult);
								
							}else{
								testResult = testResultMap.get(testCaseNo);
								testResult.put("InterID", interID+"");
								testResult.put("Result", "Fail");
								testResult.put("Failed Columns", db.getFailedColumns().toString());
								testResultMap.put(testCaseNo, testResult);
								System.out.println("CSV Values did not match with the Database values for Test Case Number: "+ObjectsMap.get(testCaseNo).getTestCaseNo());
							}
						}
					}
				}
			}
			
			//Check if all the CSV Objects have been verified, if verified then close the test
			if(utils.endTest(testResultMap)){
				break;
			}
			//System.out.println("loop: "+i++);
			//Thread.sleep(1000);
			endTime = System.currentTimeMillis();
		}
		
		System.out.println("------------------------------------------------------------------------- Test Results ----------------------------------------------------------------");
		System.out.println();
		for(String testCase : testResultMap.keySet()){
			System.out.println("Test Case Number: "+testCase+", InterID: "+testResultMap.get(testCase).get("InterID")+", Result: "+testResultMap.get(testCase).get("Result"));
			if(!Boolean.parseBoolean(testResultMap.get(testCase).get("Result"))){
				result = false;
			}
		}
		System.out.println("----------------------------------------------------------------------------- End ----------------------------------------------------------------------");
//		if(!result){
//			Assert.fail();
//		}
		
		
		
	}// closed test method
	
	@DataProvider(name = "dp")
	 public Object[][] getResults(){
	  Object[][] result = new Object[testResultMap.size()][1];
	  int i = 0;
	  for(String testcase : testResultMap.keySet()){
	   result[i++][0] =  testcase+" "+testResultMap.get(testcase).get("Result")+" "+testResultMap.get(testcase).get("Failed Columns");
	  }
	  return result;
	 }
	
	@Test(dependsOnMethods = {"sharepointTest"}, dataProvider = "dp")
	public void result(String result){
		if(result.split(" ")[1].equals("Fail")){
			Assert.fail();
		}
	}
		
	 
}//closed Class
