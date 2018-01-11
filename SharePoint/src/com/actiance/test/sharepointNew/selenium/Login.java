package com.actiance.test.sharepointNew.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Login {
	
	@FindBy(xpath="//input[@type='text']")
	private WebElement username;
	
	@FindBy(xpath="//input[@type='password']")
	private WebElement password;
	
	@FindBy(xpath="//input[@class='button']")
	private WebElement submitLogin;

	WebDriver driver;
	WebDriverWait wait;
	
	public Login(WebDriver driver)
	{
		this.driver=driver;
		wait=new WebDriverWait(driver,120);
	}
	public void enteruserName(String username) {
		this.username.clear();
		this.username.sendKeys(username);
	}
	
	public void enterPassWord(String password) {
		this.password.clear();
		this.password.sendKeys(password);
	}
	
	public void submit() {
		submitLogin.click();
	}
	
	public Dashboard gotoDashboard(String username, String password){
		enteruserName("sysadmin");
		enterPassWord("facetime");
		submit();
		return PageFactory.initElements(driver, Dashboard.class);
	
	}

}
