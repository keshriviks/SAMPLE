package com.actiance.test.exporterNew.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Configuration {
	WebDriver driver;
	WebDriverWait wait;
	
	@FindBy(linkText="Interaction Exporters")
	private WebElement intExpo;
	
	public Configuration(WebDriver driver){
		this.driver = driver;
		wait = new WebDriverWait(driver, 120);
	}
	
	public void clickInteractionExporters(){
		wait.until(ExpectedConditions.visibilityOf(intExpo));
		intExpo.click();
	}
	
	public InteractionExporters gotoInteractionExporters(){
		clickInteractionExporters();
		return PageFactory.initElements(driver, InteractionExporters.class);
	}
}
