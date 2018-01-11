package com.actiance.test.sharepointNew.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SharepointCompliance {
	
	
	
	WebDriver driver;
	WebDriverWait wait;
	
	@FindBy(xpath="//a[@href='/ima/editFarmList.do?method=list']")
	private WebElement sharepointCompliance ;
	
	@FindBy(id="addOnlineFarm")
	private WebElement addFarm ;
	
	@FindBy(id="instanceName")
	private WebElement instanceName ;
	
	@FindBy(id="urls")
	private WebElement urls ;
	
	@FindBy(id="userId")
	private WebElement userId ;
	
	@FindBy(id="password")
	private WebElement password ;
	
	@FindBy(id="enableCompliance")
	private WebElement enableCompliance ;
	
	@FindBy(id="archiveDate")
	private WebElement archiveDate ;
	
	@FindBy(id="continuousCheckForProcessData")
	private WebElement continuousCheckForProcessData ;
	
	@FindBy(xpath=".//*[@id='serverIDs']/option")
	private WebElement serverID ;
	
	@FindBy(id="submit3")
	private WebElement submit3 ;
	
	@FindBy(xpath=".//*[@id='footerLeft']/input[1]")
	private WebElement save ;
	
	@FindBy(id="adminUrl")
	private WebElement adminUrl;
	
	public void enterAdminUrl()
	{
		adminUrl.sendKeys("https://actianceengg-admin.sharepoint.com/");
	}
	
	public SharepointCompliance(WebDriver driver){
		this.driver = driver;
		wait = new WebDriverWait(driver, 120);
	}
	public void clickSharepointCompliance(){
		wait.until(ExpectedConditions.visibilityOf(sharepointCompliance));
		sharepointCompliance.click();
	}
	
	public void addFarm() {
		addFarm.click();
	}
	
	public void enterinstanceName(String instanceName) {
		this.instanceName.clear();
		this.instanceName.sendKeys(instanceName);
	}
	
	
	public void enterurls(String urls) {
		this.urls.clear();
		this.urls.sendKeys(urls);
	}
	
	public void enteruserId(String userId) {
		this.userId.clear();
		this.userId.sendKeys(userId);
	}
	
	public void enterpassword(String password) {
		this.password.clear();
		this.password.sendKeys(password);
	}
	
	public void enableCompliance() {
		enableCompliance.click();
	}
	
	
	public void enterarchiveDate(String archiveDate) {
		this.archiveDate.clear();
		this.archiveDate.sendKeys(archiveDate);
	}
	
	public void entercontinuousCheckForProcessData(String continuousCheckForProcessData) {
		this.continuousCheckForProcessData.clear();
		this.continuousCheckForProcessData.sendKeys(continuousCheckForProcessData);
	}
	
	public void selectServerID() {
		serverID.click();
	}
	
	public void testConnection() {
		submit3.click();
	}
	
	public void save() {
		save.click();
	}
	
	public Configuration configureSHarePointOnlineSites(String username, String password){
		
		String[] weblist ={"topLevelLists","Libraries","subSiteLevelLists","AdditionalAndCustomType","CustomFieldTesting"};
		String url = "https://actianceengg.sharepoint.com/";
		
		
		for(String instance : weblist){
	    addFarm();
		enterinstanceName(instance);
		enterurls(url + instance);
		enteruserId(username);
		enterpassword(password);
		enableCompliance();
		System.out.println("Start Date : "+ System.getProperty("startArchiveDate"));
		enterarchiveDate(System.getProperty("startArchiveDate"));
		entercontinuousCheckForProcessData("1");
		selectServerID();
		enterAdminUrl();
		testConnection();
		enterpassword(password);
		save();
		
	}
		return PageFactory.initElements(driver, Configuration.class);	
	
		
	}		
}
