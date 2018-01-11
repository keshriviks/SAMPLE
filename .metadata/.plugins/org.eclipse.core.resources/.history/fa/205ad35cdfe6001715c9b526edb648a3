package com.actiance.test.sharepointNew.selenium;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;



public class SharePointConfiguration {
	
	private WebDriver driver;
//	private String vantageIP = "192.168.126.17";
	private String username = "sysadmin";
	private String password = "facetime";
	
	@Parameters({"vantageIP","additionalContentTypeFile","username","password", "startArchiveDate"})
	@BeforeSuite(alwaysRun=true)
	public void setup(String vantageIP, String additionalContentTypeFile, String username, String password, String startArchiveDate) {
		
		System.setProperty("vantageIP", vantageIP.trim());
		System.setProperty("additionalContentTypeFile", additionalContentTypeFile.trim());
		System.setProperty("username", username.trim());
	   	System.setProperty("password", password.trim());
		System.setProperty("startArchiveDate", startArchiveDate.trim());
		System.out.println("Start Date : "+ System.getProperty("startArchiveDate"));
		//driver = new FirefoxDriver();
		
		System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://"+ vantageIP +":8443/ima/login.do");
	}
	
	@Test
	public void configureSharePoint() {
		Login l=PageFactory.initElements(driver, Login.class);
		Dashboard m = l.gotoDashboard(username, password);
		Configuration c=m.gotoConfiguration();
		SharepointAdditional s=c.gotoSharepointAdditional();
		s.uploadAddionalContentTypeFile(System.getProperty("additionalContentTypeFile"));
		SharepointCompliance s1=c.gotoSharepointCompliance();
		s1.configureSHarePointOnlineSites(System.getProperty("username"),System.getProperty("password"));
	}
}