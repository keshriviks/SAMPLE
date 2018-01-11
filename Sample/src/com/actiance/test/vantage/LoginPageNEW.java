package com.actiance.test.vantage;

import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Sam.com.actiance.Teams.TestBase;



public class LoginPageNEW extends TestBase{

WebDriver driver;
	
/*	@FindBy(id="otherTileText")
	WebElement NewAccount;*/

	
	@FindBy(id="i0116")
	WebElement UserId;
	
	@FindBy(xpath=".//*[@id='idSIButton9']")
	WebElement NextButton;
	
	@FindBy(xpath=".//*[@id='i0118']")
	WebElement password;
	
	/*@FindBy(id="idSIButton9")
	WebElement SignIN;*/
	
	@FindBy(xpath=".//*[@id='idSIButton9']")
	WebElement SignIN;
	
	@FindBy(xpath=".//*[@id='personDropdown']")
	WebElement UserIcon;
	
	@FindBy(xpath=".//*[@id='logout-button']")
	WebElement logoutButton;
	
	public LoginPageNEW(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
		
	public void loginToApplication() throws InterruptedException, IOException{
		
		loadData();
		
		//log("cliked on sign in and object is:-"+signIn.toString());
		//NewAccount.click();
		UserId.sendKeys(getObject("userName"));
		//log("entered email address:-"+UserName+" and object is "+UserID.toString());
		Thread.sleep(5000);
		NextButton.click();
		password.sendKeys(getObject("passwrd"));
		//log("entered password:-"+password+" and object is "+Password.toString());
		Thread.sleep(5000);
		SignIN.click();
		Thread.sleep(40000);
		System.out.println("clicked on chat room helloooooo");
		//log("clicked on submit button and object is:-"+SignIN.toString());
	}
	
public void logoutApps() throws InterruptedException{
	UserIcon.click();
	logoutButton.click();
	Thread.sleep(5000);
		
	}
	
}
