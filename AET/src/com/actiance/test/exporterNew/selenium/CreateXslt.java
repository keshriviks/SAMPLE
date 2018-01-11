package com.actiance.test.exporterNew.selenium;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CreateXslt {
	static WebDriver driver;
	 
	public static void main(String[] args) {
		
		CreateXslt ce=new CreateXslt();
		
		//,"IM,PDF","IM,TXT","IM,XML","Collaboration,HTML","Collaboration,CSV","Collaboration,PDF","Collaboration,XML","Collaboration,TXT"
		String[] str={"IM,HTML"};
		for(String temp:str){
			try {
				System.out.println(ce.createTemplate(temp));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public WebDriver setDriver() {
		try {
			driver = new FirefoxDriver();
			WebDriverWait wait = new WebDriverWait(driver, 120);
			Properties prop = new Properties();

			InputStream in;

		/*	in = new FileInputStream(System.getProperty("vantageFile"));
			prop.load(in);*/

			driver.get("https://" + System.getProperty("vantageIP") + ":8443/ima/login.do");
			driver.findElement(By.name("username")).clear();
			driver.findElement(By.name("username")).sendKeys(System.getProperty("vanUsername").toString());
			driver.findElement(By.name("password")).clear();
			driver.findElement(By.name("password")).sendKeys(System.getProperty("vanPassword").toString());
			driver.findElement(By.className("button")).click();
			WebElement conf = driver.findElement(By.linkText("Configuration"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Configuration")));
			conf.click();
			WebElement cet = driver.findElement(By.linkText("Configure Exporter Templates"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Configure Exporter Templates")));
			cet.click();

		} catch (Exception e) {
			System.out.println("Vantage is not able to response");
			e.printStackTrace();
		}
		return driver;
	}
	
	
	
	public String createTemplate(String value) {
		String modality = null;
		String outputType = null;
		
		try{

			String sourcePath=(String) System.getProperty("defaultXSLTPath");
			WebDriver driver=setDriver();
			WebDriverWait wait=new WebDriverWait(driver,120);
			
//			add button
			WebElement add=driver.findElement(By.xpath("//input[@value='Add']"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@value='Add']")));
			add.click();
//			Give a template name
			WebElement templateName=driver.findElement(By.xpath("//*[@id='templateName']"));
			
//			select modalities
			Select oSelect=new Select(driver.findElement(By.xpath(".//*[@id='modalities']")));
			//set value for modalities and outputType
			String [] modOut=value.split(",");
			modality=modOut[0];
			outputType=modOut[1].trim();
			templateName.sendKeys(modality+"_"+outputType);
			System.out.println("Test1 : " + modality);
			String selectModality = modality;
			if(modality.equals("collab") || modality.equalsIgnoreCase("collaboration") ){
				selectModality = "Collaboration";
			}
			if(modality.equalsIgnoreCase("im")){
				selectModality = "IM";
			}
			oSelect.selectByVisibleText(selectModality);
//			select output type		
			driver.findElement(By.xpath(".//*[@id='exportTypeScheduled']")).click();
			if(outputType.equalsIgnoreCase("TXT")){
				oSelect=new Select(driver.findElement(By.xpath(".//*[@id='outputType']")));
				oSelect.selectByVisibleText("Text");
			}
			else{
				oSelect=new Select(driver.findElement(By.xpath(".//*[@id='outputType']")));
				oSelect.selectByVisibleText(outputType);
			}
			
			String path=null;
//			create path for searching the file		
			if(modality.equalsIgnoreCase("IM")){
				path=modality+"_conversation_XML_TO_"+outputType+"_format.xsl";
			}
			if(modality.equalsIgnoreCase("Collaboration")){
				path="Collab"+"_event_XML_TO_"+outputType+"_format.xsl";
			}
			driver.findElement(By.xpath(".//*[@id='content']/div[3]/table/tbody/tr/td[1]/table/tbody/tr[8]/td[2]/input")).sendKeys(sourcePath+path);
//			save the value
			driver.findElement(By.xpath(".//*[@id='footerLeft']/div/span/input[1]")).click();
			driver.quit();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return modality+"_"+outputType;
		
	}

}
