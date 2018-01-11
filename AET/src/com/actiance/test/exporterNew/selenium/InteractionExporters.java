package com.actiance.test.exporterNew.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.actiance.coresever.export.exporter.InteractionExporter;

public class InteractionExporters {
	 WebDriver driver;
	 WebDriverWait wait;
	 
	 @FindBy(xpath="//input[@value='Create New']")
	 private WebElement createNew;
	 
	 @FindBy(id="exporterNum")
	 private WebElement exporterName;
	 
	 @FindBy(id ="go")
	 private WebElement go;
	 
	 @FindBy(id="submit3")
	 private WebElement ok;
	 
	 
	 public InteractionExporters(WebDriver driver){
	  this.driver = driver;
	  wait = new WebDriverWait(driver, 60);
	 }
	 
	 public void clickCreateNew(){
	  wait.until(ExpectedConditions.visibilityOf(createNew));
	  createNew.click();
	 }
	 
	 public String getInitialExporterNumber(){
	  wait.until(ExpectedConditions.visibilityOf(exporterName));
	  Select dropdown = new Select(exporterName);
	  List<WebElement> options = dropdown.getOptions();
	  return options.get(options.size()-1).getText();
	 }
	 
	 public InteractionExporters selectExporterAndClickGo(String exporter){
		 wait.until(ExpectedConditions.visibilityOf(exporterName));
		 Select dropdown = new Select(exporterName);
		 List<WebElement> option = dropdown.getOptions();
		 
		 for(WebElement element : option ){
//			 for(String exporter : exporters){
				 if(element.getText().equals(exporter)){
					 System.out.println("Exporter Name from Drop Down: " + element.getText());
					 element.click();
					 break;
				 }else{
					 continue;
				 }	 
//			 }
		 }
		 System.out.println("Clicking on Go button");
		 wait.until(ExpectedConditions.visibilityOf(go)).click();
		 System.out.println("Clicking on Ok Button");
		 wait.until(ExpectedConditions.visibilityOf(ok)).click();

		 
		 return  PageFactory.initElements(driver, InteractionExporters.class);
	 }
	 
	 public void closeBrowser(){
		 driver.close();
		 
	 }
	 
	 
	}
