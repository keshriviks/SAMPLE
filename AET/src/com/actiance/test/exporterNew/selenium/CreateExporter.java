package com.actiance.test.exporterNew.selenium;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;



public class CreateExporter {
	

	WebDriver driver;
	public String getExporterName = null;
	
	InputStream input = null;
	
	BrowSetupAndLoadUrl brow = null;
	VantageLogIn vanLogIn;
	Dashboard dashboard;
	Configuration configuration;
	InteractionExporters inExp;
	
	Properties prop = new Properties();
	
	public InteractionExporters createNewTemplate(int numberOfExporter){
		try{
//			 input = new FileInputStream(vantageDetailFile);
//			 prop.load(input);
			 
			 //Open browser and load url
			 brow = new BrowSetupAndLoadUrl();
			 String profileName = System.getProperty("profileName");
			 String url = getVantageURL(System.getProperty("vantageIP"));
			 
//			 driver = brow.openBrowserAndLoadURL(profileName, url);
			 driver = brow.openBrowserAndLoadUrl(profileName, url);
			 
			 //log in to the Vantage
			 vanLogIn = PageFactory.initElements(driver, VantageLogIn.class);
			 dashboard = vanLogIn.logIn(System.getProperty("vanUsername"), System.getProperty("vanPassword"));
			 assertEquals(driver.getTitle().trim(), "Vantage - Dashboard", "Log In issue");
			 System.out.println("Successfully Logged In to the Vantage");
			 System.out.println();
			 
			 configuration = dashboard.gotoConfiguration();
			 inExp = configuration.gotoInteractionExporters();
			 
			 getExporterName = inExp.getInitialExporterNumber();
			 
			 for(int i=0; i<numberOfExporter; i++){
			  inExp = PageFactory.initElements(driver, InteractionExporters.class);
			  inExp.clickCreateNew();
			  Thread.sleep(200);
			 }
			 //brow.closeBrowser();
		}catch(InterruptedException ie){
		 System.out.println("IOException");
		 ie.printStackTrace();
		}catch(Exception ite){
			System.out.println("Thread Exception");
			ite.printStackTrace();
		}
		return PageFactory.initElements(driver, InteractionExporters.class);
	}
	
	public String getVantageURL(String host){
	 return "https://"+host.trim()+":8443/ima";
	}
	
	
	public String getExporterName(){
		return this.getExporterName;
	}
}



