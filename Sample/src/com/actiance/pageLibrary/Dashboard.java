package com.actiance.pageLibrary;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;



public class Dashboard extends TestBase{
	
	WebDriver driver;
	WebDriverWait wait;
	
	
	@FindBy(linkText="Dashboard")
	 WebElement Dashboard;
	
	@FindBy(linkText="Configuration")
	 WebElement Configuration;

	
	public Dashboard(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickDashboard(){
		wait.until(ExpectedConditions.visibilityOf(Dashboard));
		Dashboard.click();
	}
	
	public void clickConfiguration(){
		
		Configuration.click();
	}
	
	

}
