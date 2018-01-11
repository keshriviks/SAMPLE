package Sam.com.actiance.Teams.UiActions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Sam.com.actiance.Teams.TestBase;

//import Sam.com.actiance.Teams.TestBase;

public class LoginPage extends TestBase{

WebDriver driver;
	
	@FindBy(id="cred_userid_inputtext")
	WebElement UserID;
	
	
	@FindBy(id="cred_password_inputtext")
	WebElement Password;
	
	@FindBy(xpath="//button[@id='cred_sign_in_button']")
	WebElement SignIN;
	
	@FindBy(xpath=".//*[@id='personDropdown']")
	WebElement UserIcon;
	
	@FindBy(xpath=".//*[@id='logout-button']")
	WebElement logoutButton;
	
	
	public LoginPage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
		
	public void loginToApplication() throws InterruptedException, IOException{
		
		loadData();
		
		//log("cliked on sign in and object is:-"+signIn.toString());
		
		UserID.sendKeys(getObject("userName"));
		//log("entered email address:-"+UserName+" and object is "+UserID.toString());
		
		Password.sendKeys(getObject("passwrd"));
		//log("entered password:-"+password+" and object is "+Password.toString());
		Thread.sleep(20000);
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
