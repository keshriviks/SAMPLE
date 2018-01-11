package com.actiance.test.importer.collab.tests;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;

public class TestRunner {
	
// Variable History
//	Runtime Argument list
//	Database_Host Database_Name Database_Username Database_Password Network_Name FolderLocation
	
	public static void main(String[] args) {
		// Create object of TestNG Class
		
		TestNG runner = new TestNG();

		String dbURL = "jdbc:sqlserver://" + args[0] + ":1433;database=" + args[1];

//		System.out.println("dbType " + args[0]);
//		System.out.println(dbURL);
		System.setProperty("dbType", args[0]);
		System.setProperty("dbHost", args[1]);
		System.setProperty("dbName", args[2]);
		System.setProperty("dbUserName", args[3]);
		System.setProperty("dbPassword", args[4]);
		System.setProperty("networkType", args[5]);
		System.setProperty("folderLocation", args[6]);
		
		

		// Create a list of String
		List<String> suitefiles = new ArrayList();

		
		// Add xml file which you have to execute
		suitefiles.add(
				"C:\\oneTouch\\importer\\testng_xmls\\importerCollabJive.xml");

		// now set xml file for execution
		runner.setTestSuites(suitefiles);

		// finally execute the runner using run method
		runner.run();
	}


}
