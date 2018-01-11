package com.actiance.test.exporterNew.selenium;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.testng.Assert.assertEquals;

public class BrowSetupAndLoadUrl {
	WebDriver driver;
	WebDriverWait wait;
	
	public BrowSetupAndLoadUrl(WebDriver driver){
		this.driver = driver;
		this.wait = new WebDriverWait(driver, 120);
	}
	
	public BrowSetupAndLoadUrl(){
		
	}
	public static void main(String[] args) {
		WebDriver driver = new FirefoxDriver();
		BrowSetupAndLoadUrl br = new BrowSetupAndLoadUrl(driver);
		br.driver.get("https://www.google.co.in");
		
	}
	
	public WebDriver openBrowser(String ffProfile, String chatterURL){
		try{
			/*File profileDir = new File(ffProfile);
			FirefoxProfile profile = new FirefoxProfile(profileDir);
			*/
			ProfilesIni allProfiles = new ProfilesIni();
			FirefoxProfile profile = allProfiles.getProfile(ffProfile);
//			profile.setPreference("javascript.enabled", false);
			driver = new FirefoxDriver(profile);
			Thread.sleep(5000);
			driver.get(chatterURL);
			Thread.sleep(2000);
			assertEquals(driver.getTitle(), "Vantage - Log In", "It has been redirected to '"+driver.getTitle()+"' Page.");
			System.out.println("Successfully opened the browser and loaded the URL");
			return driver;
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("There was an error while opening browser and loading the URL");
		}
		return null;		
	}
	
	public WebDriver openBrowserAndLoadUrl(String ffProfile, String Url){
		try{
			ProfilesIni allProfiles = new ProfilesIni();
			FirefoxProfile profile = allProfiles.getProfile(ffProfile);
			
			driver = new FirefoxDriver(profile);
			Thread.sleep(5000);
			System.out.println("Browser Loaded With Profile");
			driver.get(Url);
			System.out.println("Loaded URL");
			
			assertEquals(driver.getTitle(), "Vantage - Log In", "It has been redirected to '"+driver.getTitle()+"' Page.");
			System.out.println("Successfully opened the browser and loaded the URL");
			return driver;
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("There was an error while opening browser and loading the URL");
		}
		return driver;	
	}
	
	public WebDriver openBrowserAndLoadURL(String profileName, String url) {
		try {
			System.out.println("Launching the Browser with Firefox Profile : "+ profileName);
			
			System.out.println(url);
			ProfilesIni allProfiles = new ProfilesIni();
			FirefoxProfile profile = allProfiles.getProfile("TestExporter");
			driver = new InternetExplorerDriver();
			Thread.sleep(5000);
			System.out.println("Opening Browser");
			System.out.println("");
			System.out.println("Browser Profile : "+ profileName);
			url = "www.google.co.in";
			System.out.println("Browser Opened Successfully, Loading URL...");
			driver.get(url);
			System.out.println("Driver Title : "+driver.getTitle());
//			driver.close();
		} catch (InterruptedException e) {
			System.err.println("Not able to Open Browser or URL");
			driver.close();
			driver.quit();
			return null;
		}
		return driver;
	}
	
	public void closeBrowser2(){
		driver.get("");
		//driver.quit();
	}
	
	public void closeBrowser(){
		driver.close();
		driver.quit();
	}
	
	public WebDriver openBrowserAndLoadURLInIE(String url){
		String driverPath = "E:/sw/Selenium_Drivers/";
//		WebDriver driver;
		System.setProperty("webdriver.ie.driver", driverPath+"IEDriverServer.exe");
		driver = new InternetExplorerDriver();
		driver.manage().window().maximize();
		driver.navigate().to(url);
		String strPageTitle = driver.getTitle();
		return driver;
	}
}