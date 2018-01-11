package Sample123;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

import com.thoughtworks.selenium.webdriven.commands.Click;

public class VerifyAllLinksVanatage {

	public static void main(String[] args) throws InterruptedException {
		WebDriver driver= new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        driver.get("https://192.168.116.151:8443/ima/login.do");
        // Giving the value on username
        WebElement username=driver.findElement(By.xpath(".//input[@name='username']"));
        username.sendKeys("sysadmin");
        //// Giving the value on password
        WebElement password=driver.findElement(By.xpath(".//input[@name='password']"));
        password.sendKeys("facetime");
        //click on login 
        WebElement login=driver.findElement(By.id("footerRight"));
        login.click();
        try {
                        Thread.sleep(3000);
        } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
        }
        //verify the title page of apps
        String actualTitle = driver.getTitle();
        System.out.println("Title page"+" : "+actualTitle);
        String expectedTitle = "Vantage - Dashboard";
        
        Assert.assertEquals(actualTitle, expectedTitle);
        
        System.out.println("*************"+"Dashboard opration"+"**************");
        
        WebElement Configuration=driver.findElement(By.linkText("Configuration"));
        Configuration.click();
        
        List<WebElement> allLinks = driver.findElements(By.tagName("a"));
        int total_ConfigLink = allLinks.size();
        System.out.println(total_ConfigLink);
        List<String> urlList = new ArrayList<String>();
        
        for (WebElement link : allLinks) {
        	 
        System.out.println(link.getAttribute("href"));
        urlList.add(link.getAttribute("href"));
        	 
        System.out.println(link.getText());
        
        }
        List<String> ignoreList = new ArrayList<String>();
        ignoreList.add("logout");
        ignoreList.add("actiance.com");
        
        Iterator itr = urlList.iterator();
        while(itr.hasNext())
        {
        String url = (String) itr.next();
        //System.out.println("Clicking url : "+url);
        //if the url doesnt contain any string from ignorelist then proceed
        Iterator<String> itr2 = ignoreList.iterator();
        boolean ignoreFlag = false;
        while(itr2.hasNext())
        {
        	String ignore = (String) itr2.next();
        	//System.out.println("Checking for :"+ignore);
        	if(url.contains(ignore))
        	{
        		ignoreFlag = true;
        	//	System.out.println("ignoring the url");
        	}
        }
        //System.out.println("Ignore Flag:"+ignoreFlag);
        if(!ignoreFlag){
        	//Code to click and verify
       // System.out.println("Clicking :"+url);
        	driver.get(url);
        	System.out.println(url+" ---------- "+driver.getTitle());
        	
        	if(driver.getPageSource().contains("error")){
        		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        }
        }
        }
      /*  WebElement Policies=driver.findElement(By.linkText("Policies"));
        Configuration.click();*/
        ///////////////groups///////////////////
        
        WebElement Groups=driver.findElement(By.linkText("Groups"));
        Groups.click();
        
        List<WebElement> allLinks_grp = driver.findElements(By.tagName("a"));
        int total_grp = allLinks_grp.size();
        System.out.println(total_grp );
        List<String> urlList_grp= new ArrayList<String>();
        
        for (WebElement link_grp  : allLinks_grp ) {
   
        System.out.println(link_grp .getAttribute("href"));
        urlList_grp.add(link_grp .getAttribute("href"));
        	 
        System.out.println(link_grp .getText());
        
        }
        
        List<String> ignoreList_grps = new ArrayList<String>();
        ignoreList_grps.add("logout");
        ignoreList_grps.add("actiance.com");
        
        Iterator itr_grp = urlList_grp.iterator();
        while(itr_grp.hasNext())
        {
        String url_grp = (String) itr_grp.next();
        //System.out.println("Clicking url : "+url);
        //if the url doesnt contain any string from ignorelist then proceed
        Iterator<String> itr_Igngrp = ignoreList_grps.iterator();
        boolean ignoreFlag_grp = false;
        while(itr_Igngrp.hasNext())
        {
        	String ignore_grp = (String) itr_Igngrp.next();
        	//System.out.println("Checking for :"+ignore);
        	if(url_grp.contains(ignore_grp))
        	{
        		ignoreFlag_grp = true;
        	//	System.out.println("ignoring the url");
        	}
        }
        //System.out.println("Ignore Flag:"+ignoreFlag);
        if(!ignoreFlag_grp){
        	//Code to click and verify
       // System.out.println("Clicking :"+url);
        	driver.get(url_grp);
        	
        	System.out.println(url_grp+" ---------- "+driver.getTitle());
        	
        	if(driver.getPageSource().contains("error"))
        		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        	
        }  
}
        	
        }
	}


        
	
       
 
