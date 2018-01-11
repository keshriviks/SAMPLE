package com.actiance.pageLibrary;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;



public class Configuration extends TestBase {
	
	
		WebDriver driver;
		WebDriverWait wait;
		
		@FindBy(linkText="Configuration")
		 WebElement Configuration;
		
		@FindBy(linkText="Import Settings")
		 WebElement ImporterSettings ;
		
		@FindBy(linkText="API Network Registration")
		 WebElement NetworkRegistration ;
		
	
		public Configuration(WebDriver driver){
			this.driver = driver;
			PageFactory.initElements(driver, this);
		}
		
		
		public void clickConfiguration(){
			wait.until(ExpectedConditions.visibilityOf(Configuration));
			Configuration.click();
		}
		public void clickImporter(){
			ImporterSettings.click();
		}
		
        public void clickAPiNetworkRegistration(){	
        	NetworkRegistration.click();
		}
	
		
		

}
