package com.actiance.Teams.Functionality;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestteamsLogin {

	public static void main(String[] args) throws InterruptedException {
		
		
		System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
        
        WebDriver driver = new ChromeDriver();
		//WebDriver driver = new FirefoxDriver();
		
		driver.get("https://teams.microsoft.com");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
	    driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
	    
	    Thread.sleep(10000);
	    
	    WebElement login  = driver.findElement(By.id("i0116"));
	    
	    login.clear();
//	    login.sendKeys("manish");
	    
//	    WebElement login=driver.findElement(By.xpath("//div[contains(text(),'someone@example.com')]"));
	    //login.clear();
	    //login.click();
	    login.sendKeys("achandra@actiance.co.in");
	    driver.findElement(By.xpath(".//*[@id='idSIButton9']")).click();
	    
	    
	    driver.findElement(By.xpath(".//*[@id='i0118']")).sendKeys("FaceTime@123");
	    
	    driver.findElement(By.xpath(".//*[@id='idSIButton9']")).click();
	
	    
		
	}
	
}