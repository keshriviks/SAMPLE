package com.actiance.test.exporterNew.tests;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;

public class TestRunner {

	public static void main(String[] args) {
		// Create object of TestNG Class
		TestNG runner = new TestNG();

		System.setProperty("dbHost", args[0]);
		System.setProperty("dbName", args[1]);
		System.setProperty("dbUsername", args[2]);
		System.setProperty("dbPassword", args[3]);

		// Create a list of String
		List<String> suitefiles = new ArrayList<>();

		// Add xml file which you have to execute
		suitefiles.add(
				"C:/oneTouch/importer/testng_xmls/automapping/inbb/loginAsBudyName/instantBloomberg_Automapping_loginAsBuddyName.xml");

		// now set xml file for execution
		runner.setTestSuites(suitefiles);

		// finally execute the runner using run method
		runner.run();
	}

}

