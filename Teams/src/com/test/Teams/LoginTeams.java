package com.test.Teams;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LoginTeams {

	public static void main(String[] args) {
		 
		System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
		 WebDriver driver= new ChromeDriver();
	        driver.manage().window().maximize();
	        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	        driver.get("https://teams.microsoft.com");
	        
		    WebElement login=driver.findElement(By.xpath("//div[contains(text(),'someone@example.com')]"));
		    //login.clear();
		    //login.click();
		    login.sendKeys("achandra@actiance.co.in");

	}

}
