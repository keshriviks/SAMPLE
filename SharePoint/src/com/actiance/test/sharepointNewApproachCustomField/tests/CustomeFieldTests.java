package com.actiance.test.sharepointNewApproachCustomField.tests;

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
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.actiance.test.sharepointNewApproachCustomField.utils.DatabaseUtils;
import com.actiance.test.sharepointNewApproachCustomField.utils.TestDataObject;
import com.actiance.test.sharepointNewApproachCustomField.utils.TestUtils;



public class CustomeFieldTests {
	
	DatabaseUtils db = new DatabaseUtils();
	TestUtils utils = new TestUtils();
	
	HashMap<String, TestDataObject> ObjectsMap;
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
	public void sharepointTest() throws InterruptedException, NumberFormatException, XPathExpressionException, SQLException{
		boolean result = true;
		Long startTime = System.currentTimeMillis();
		Long endTime;
		endTime = startTime;
		int i = 1;
		
		ArrayList<Integer> interIDs = new ArrayList<>();
		HashMap<String, String> testResult = null;
		
		testResultMap = utils.getTestResultMap(ObjectsMap);
		
		if(testResultMap.size() == 0){
			result = false;
		}
		
		System.out.println(testResultMap.size());
		
		while(!utils.endTest(startTime, endTime)){
			
			System.out.println("-------------------------------------------------------------------- Loop: "+(i++)+" ----------------------------------------------------------");
			//Query Database and get Interactions
			interIDsMap = db.getInterIDsMap(interIDs);
			
			System.out.println("Number of InterIDs found: "+interIDsMap.size());
			
			for(int interID : interIDsMap.keySet()){
				
				interIDs.add(interID);
				
				for(String testCaseNo : ObjectsMap.keySet()){
//					System.out.println();
//					
					if(interIDsMap.get(interID).get(1).equals(ObjectsMap.get(testCaseNo).getTime1())){
						System.out.println();
						System.out.println("*******************************************************************");
						System.out.println("Verification for InterId : "+ interID);
						if(db.verifyDatabase(ObjectsMap.get(testCaseNo), interIDsMap.get(interID), false)){
							System.out.println("CSV Values match with the Database values for Test Case Number: "+ObjectsMap.get(testCaseNo).getTestCaseNo());
							testResult = testResultMap.get(testCaseNo);
							testResult.put("InterID", interID+"");
							testResult.put("Result", "Pass");
							testResultMap.put(testCaseNo, testResult);
							
						}else{
							testResult = testResultMap.get(testCaseNo);
							testResult.put("InterID", interID+"");
							testResult.put("Result", "Fail");
							testResultMap.put(testCaseNo, testResult);
							System.out.println("CSV Values did not match with the Database values for Test Case Number: "+ObjectsMap.get(testCaseNo).getTestCaseNo());
						}
					}
				}
			}
			Thread.sleep(1000);
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
		if(!result){
			Assert.fail();
		}
	}
}
