package com.actiance.test.sharepointNew.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;



public class Dashboard {
	
	WebDriver driver;
	WebDriverWait wait;
	
	
	@FindBy(linkText="Dashboard")
	private WebElement Dashboard;
	
	@FindBy(linkText="Configuration")
	private WebElement Configuration;
	
	public Dashboard(WebDriver driver){
		this.driver = driver;
		wait = new WebDriverWait(driver, 120);
	}
	
	public void clickDashboard(){
		wait.until(ExpectedConditions.visibilityOf(Dashboard));
		Dashboard.click();
	}
	
	public void clickConfiguration(){
		wait.until(ExpectedConditions.visibilityOf(Configuration));
		Configuration.click();
	}
	
	public Configuration gotoConfiguration(){
		clickConfiguration();
		return PageFactory.initElements(driver, Configuration.class);
	}

}
