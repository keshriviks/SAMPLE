package com.actiance.test.vantage;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

public class TestUtils {

public void Init() throws InterruptedException{
	WebDriver driver= new FirefoxDriver();
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
    driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
    driver.get("https://192.168.114.28:8443/ima/login.do");
    // Giving the value on username
    WebElement username=driver.findElement(By.xpath(".//input[@name='username']"));
    username.sendKeys("sysadmin");
    //// Giving the value on password
    WebElement password=driver.findElement(By.xpath(".//input[@name='password']"));
    password.sendKeys("facetime");
    //click on login 
    WebElement login=driver.findElement(By.id("footerRight"));
    login.click();
   Thread.sleep(2000);
    //verify the title page of apps
    String actualTitle = driver.getTitle();
    System.out.println("Title page"+" : "+actualTitle);
    String expectedTitle = "Vantage - Dashboard";
    
    Assert.assertEquals(actualTitle, expectedTitle);
}
}


