package com.actiance.test.exporterNew.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VantageLogIn {
	WebDriver driver;
	WebDriverWait wait;
	
	@FindBy(xpath="//input[@name='username']")
	private WebElement userName;
	
	@FindBy(xpath="//input[@name='password']")
	private WebElement passWord;
	
	@FindBy(xpath="//input[@value='Login']")
	private WebElement logIn;
	
	public VantageLogIn(WebDriver driver){
		this.driver = driver;
		wait = new WebDriverWait(driver, 120);
	}
	
	public void enterUserName(String username){
		wait.until(ExpectedConditions.visibilityOf(userName));
		userName.clear();
		userName.sendKeys(username);
	}
	
	public void enterPassword(String password){
		passWord.clear();
		passWord.sendKeys(password);
	}
	
	public void clickLogIn(){
		wait.until(ExpectedConditions.visibilityOf(logIn));
		logIn.click();
	}
	
	public Dashboard logIn(String username, String password){
		enterUserName(username);
		enterPassword(password);
		clickLogIn();
		return PageFactory.initElements(driver, Dashboard.class);
	}
}