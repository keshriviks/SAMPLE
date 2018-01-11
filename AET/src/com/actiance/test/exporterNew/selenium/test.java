package com.actiance.test.exporterNew.selenium;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class test {
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Hello World");
		
		WebDriver driver = new FirefoxDriver();
		
		driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
		
		driver.get("https://192.168.114.28:8443/ima");
		
		driver.findElement(By.xpath("//input[@name='username']")).sendKeys("sysadmin");
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys("facetime");
		driver.findElement(By.xpath("//input[@value='Login']")).click();
		
		driver.findElement(By.linkText("Configuration")).click();
		
		driver.findElement(By.linkText("Interaction Exporters")).click();
		
		WebElement select = driver.findElement(By.id("exporterNum"));
		
		Select dropdown =  new Select(select);
		
		List<WebElement> list = dropdown.getOptions();
		
		for(WebElement ele : list){
			System.out.println(ele.getText());
			if(ele.getText().equals("Exporter 4")){
				ele.click();
			}
		}
		Thread.sleep(5000);
		driver.findElement(By.id("go")).click();
		Thread.sleep(5000);
		driver.findElement(By.id("submit3")).click();
		
//		driver.close();
	}

}
