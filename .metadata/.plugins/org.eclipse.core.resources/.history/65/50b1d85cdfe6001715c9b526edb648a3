package com.actiance.test.sharepointNew.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;



public class SharepointAdditional {
	
	WebDriver driver;
	WebDriverWait wait;
	
	@FindBy(xpath="//a[@href='/ima/defineAdditionalContent.do?method=load']")
	private WebElement sharepointAdditional;
	
	@FindBy(xpath="//input[@id='certdb']")
	private WebElement addFile;
	
	@FindBy(linkText="Configuration")
	private WebElement Configuration;
	
	@FindBy(id="submit3")
	private WebElement submit3;
	
	@FindBy(xpath="//input[@id='attach']")
	private WebElement accept;
	
	@FindBy(xpath="//a[@class='buttonStyle']")
	private WebElement done;
	
	@FindBy(id="tableRow")
	private WebElement tableRow;
	
	@FindBy(xpath="//img[@src='./images/new/icons/refresh_label.gif']")
	private WebElement refresh;
	
	@FindBy(xpath="//a[@href='/ima/configSysAdmin.do']")
	private WebElement finalsave;
	
	
	
	public SharepointAdditional(WebDriver driver){
		this.driver = driver;
		wait = new WebDriverWait(driver, 120);
	}
	public void clickSharepointAdditional(){
		wait.until(ExpectedConditions.visibilityOf(sharepointAdditional));
		sharepointAdditional.click();
	}
	
	public void addFile(String fileLocation)
	{
		//addFile.click();
		addFile.sendKeys(fileLocation);
	}
	
	
	
	public void update()
	{
		submit3.click();
		wait = new WebDriverWait(driver, 120);
		
		}
	
	public void accept()
	{
		accept.click();
		wait = new WebDriverWait(driver, 120);
		}
	
	public void done()
	{
		done.click();
		}
	
	public void dropdown()
	{
		Select dropdown= new Select(tableRow);
		dropdown.selectByVisibleText("500");
	}
	
	public void refresh()
	{
		refresh.click();
		}
	
	public void finalsave()
	{
		finalsave.click();
		}
	
	
	public Configuration uploadAddionalContentTypeFile(String fileLocation){
		addFile(fileLocation);
		update();
		accept();
		done();
		dropdown();
		refresh();
		finalsave();
		return PageFactory.initElements(driver, Configuration.class);
		
		
		
		
	}
}
