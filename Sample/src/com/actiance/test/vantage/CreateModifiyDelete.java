package com.actiance.test.vantage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CreateModifiyDelete {
	
	WebDriver driver;
	Long t=System.currentTimeMillis();
	

	@Test(enabled =true, priority=1)

	public void CreateEmp() throws InterruptedException{
/*		File file = new File("D:/TestData/allLinks12.txt");
		
	    try {
	                 PrintStream pr = new PrintStream(file);
	                 System.setOut(pr);
	 } catch (FileNotFoundException e2) {
	                 // TODO Auto-generated catch block
	                 e2.printStackTrace();
	 }*/
		
        System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
        
        WebDriver driver = new ChromeDriver();
		
		   //WebDriver driver= new FirefoxDriver();
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
	      Thread.sleep(2000);
	       //verify the title page of apps
	       String actualTitle = driver.getTitle();
	       printConsole("Title page"+" : "+actualTitle);
	       String expectedTitle = "Vantage - Dashboard";
	       
	       Assert.assertEquals(actualTitle, expectedTitle);
        
        driver.findElement(By.linkText("Employees")).click();
       
		driver.findElement(By.id("addUser")).click();
		driver.findElement(By.name("lastName")).sendKeys("Aaaa1"+t);
        driver.findElement(By.name("firstName")).sendKeys("aaaa1"+t);
		WebElement empid = driver.findElement(By.name("employeeID"));
		empid.sendKeys("empid1"+t);
	        
		driver.findElement(By.xpath(".//*[@id='footerLeft']/input[1]")).click();
		String expempcreated=driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[1]/td[3]")).getText();
		
		printConsole("Employee created"+" : "+expempcreated);
		String actualEmp_value= "empid1"+t;
		printConsole("expected"+" : "+actualEmp_value);
		
		if (expempcreated.contains(actualEmp_value))
		{
			printConsole("Employee created successfully");
		}
		else{
			printConsole("*********Employee not created*******");
		}
		
	}
		
		
@Test(enabled=true,priority=4)	
public void modifiedEmployee() throws InterruptedException{
	
	
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
   Thread.sleep(2000);
    //verify the title page of apps
    String actualTitle = driver.getTitle();
    printConsole("Title page"+" : "+actualTitle);
    String expectedTitle = "Vantage - Dashboard";
    
    Assert.assertEquals(actualTitle, expectedTitle);
 
 driver.findElement(By.linkText("Employees")).click();

	driver.findElement(By.id("addUser")).click();
	driver.findElement(By.name("lastName")).sendKeys("Aaaa2"+t);
 driver.findElement(By.name("firstName")).sendKeys("aaaa2"+t);
	WebElement empid = driver.findElement(By.name("employeeID"));
	empid.sendKeys("empid2"+t);
     
	driver.findElement(By.xpath(".//*[@id='footerLeft']/input[1]")).click();
	String expempcreated=driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[1]/td[3]")).getText();

	printConsole("Employee created"+" : "+expempcreated);
	String actualEmp_value= "empid2"+t;
	printConsole("expected"+" : "+actualEmp_value);
	
	if (actualEmp_value.contains(expempcreated))
	{
		printConsole("Employee created successfully");
	}
	else{
		System.err.println("*********Employee not created*******");
	}
	
}	

@Test(enabled = true, priority=7)

public void deleteemployee() throws InterruptedException{
	
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
    printConsole("Title page"+" : "+actualTitle);
    String expectedTitle = "Vantage - Dashboard";
    
    Assert.assertEquals(actualTitle, expectedTitle);
 driver.findElement(By.linkText("Employees")).click();

	driver.findElement(By.id("addUser")).click();
	//Thread.sleep(50000);
	driver.findElement(By.name("lastName")).sendKeys("Aaaa3"+t);
 driver.findElement(By.name("firstName")).sendKeys("aaaa3"+t);
	WebElement empid = driver.findElement(By.name("employeeID"));
	empid.sendKeys("empid3"+t);
 	
 
	driver.findElement(By.xpath(".//*[@id='footerLeft']/input[1]")).click();
	String expempcreated=driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[1]/td[3]")).getText();

	printConsole("Employee created"+" : "+expempcreated);
	String actualEmp_value= "empid3"+t;
	printConsole("empcreated"+" : "+actualEmp_value);
	
	if (actualEmp_value.contains(expempcreated))
	{
		printConsole("Employee created successfully");
	}
	else{
		System.err.println("*********Employee not created*******");
	}
		
	driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[1]/td[1]/input")).click();
	driver.findElement(By.xpath(".//*[@id='deactivate2']")).click();

	Set<String> handles=driver.getWindowHandles();
	
	    Iterator itr=handles.iterator();
	 while(itr.hasNext()){
		//printConsole("here2");
		String wind=(String) itr.next();
		driver.switchTo().window(wind);
		//printConsole(driver.getTitle());
		if(driver.getTitle().contains("Confirmation"))
		{
			printConsole("About to Delete");
			driver.findElement(By.xpath(".//*[@id='footerLeft']/input[1]")).click();
			printConsole("Deleted");
		}
		}
}
	
	 @Test(enabled =true,priority=2 )
	 public void CreateGroup() throws InterruptedException{
			
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
	       printConsole("Title page"+" : "+actualTitle);
	       String expectedTitle = "Vantage - Dashboard";
	       
	       Assert.assertEquals(actualTitle, expectedTitle);
		
        driver.findElement(By.linkText("Groups")).click();
     
		driver.findElement(By.id("addDynamic")).click();
		driver.findElement(By.name("groupName")).sendKeys("Aaaa4"+t);
      
		driver.findElement(By.xpath(".//*[@id='footerLeft']/input[1]")).click();
		String expgrpcreated=driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[1]/td[2]/a")).getText();

		printConsole("Group created"+" : "+expgrpcreated);
		String actualgrp= "Aaaa4"+t;
		printConsole("expected"+" : "+actualgrp);
		
		if (actualgrp.contains(expgrpcreated))
		{
			printConsole("Group created successfully");
		}
		else{
			System.err.println("*********Group not created*******");
		}	
		
	 }

	 @Test(enabled=true, priority=5)
		public void modifiedgroup() throws InterruptedException{
			
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
	       printConsole("Title page"+" : "+actualTitle);
	       String expectedTitle = "Vantage - Dashboard";
	       
	       Assert.assertEquals(actualTitle, expectedTitle);
		
        driver.findElement(By.linkText("Groups")).click();
   
		driver.findElement(By.id("addDynamic")).click();
		driver.findElement(By.name("groupName")).sendKeys("Aaaa5"+t);
    
		driver.findElement(By.xpath(".//*[@id='footerLeft']/input[1]")).click();
		String expgrpcreated=driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[1]/td[2]/a")).getText();

		printConsole("Group created"+" : "+expgrpcreated);
		String actualgrp= "Aaaa5"+t;
		printConsole("actualgrp"+" : "+actualgrp);
		
		if (actualgrp.contains(expgrpcreated))
		{
			printConsole("Group created successfully");
		}
		else{
			System.err.println("*********Group not created*******");
		}
		///////////////////////modifications///////////////////////
		driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[4]/td[2]/a")).click();
		
		WebElement mod_grp_name=driver.findElement(By.xpath(".//*[@id='content']/div[3]/table/tbody/tr/td[1]/table/tbody/tr[1]/td[2]/input"));
		mod_grp_name.sendKeys("Test"+t);
		driver.findElement(By.xpath(".//*[@id='footerLeft']/input[1]")).click();
		
		String aftermodifiedgrp =driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[4]/td[2]/a")).getText();
		  printConsole("After modification group name"+" : "+aftermodifiedgrp);
		
	 }
	 
	 
	
	@Test(enabled=true, priority=8)
	public void deletegroup(){
		
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
		       printConsole("Title page"+" : "+actualTitle);
		       String expectedTitle = "Vantage - Dashboard";
		       
		       Assert.assertEquals(actualTitle, expectedTitle);
			
	        driver.findElement(By.linkText("Groups")).click();
	     
			driver.findElement(By.id("addDynamic")).click();
			driver.findElement(By.name("groupName")).sendKeys("Aaab"+t);
	      
			driver.findElement(By.xpath(".//*[@id='footerLeft']/input[1]")).click();
			String expgrpcreate=driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[1]/td[2]/a")).getText();
			printConsole("Group created"+" : "+expgrpcreate);
			String actualgrp= "Aaab"+t;
			printConsole("actualgrp"+" : "+expgrpcreate);
			
			if (actualgrp.contains(expgrpcreate))
			{
				printConsole("Group created successfully");
			}
			else{
				System.err.println("*********Group not created*******");
			}
			
			driver.findElement(By.xpath(".//*[@id='employees']/tbody/tr[4]/td[1]/input")).click();
			driver.findElement(By.xpath(".//*[@id='deactivate2']")).click();

			Set<String> handles1=driver.getWindowHandles();
			
			
			    	//String parent_window = driver.getWindowHandle();
			     //printConsole("here1");
			    Iterator itr1=handles1.iterator();
			   while(itr1.hasNext()){
				//printConsole("here2");
				String wind1=(String) itr1.next();
				driver.switchTo().window(wind1);
				//printConsole(driver.getTitle());
				if(driver.getTitle().contains("Confirmation"))
				{
					printConsole("About to Delete");
					driver.findElement(By.xpath(".//*[@id='footerLeft']/input[1]")).click();
					printConsole("Deleted");
					
				}
			 }
			
	}
@Test(enabled=true,priority=6)

public void CreateImporter() throws InterruptedException{
	
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
   Thread.sleep(2000);
    //verify the title page of apps
    String actualTitle = driver.getTitle();
    printConsole("Title page"+" : "+actualTitle);
    String expectedTitle = "Vantage - Dashboard";
    
    Assert.assertEquals(actualTitle, expectedTitle);
    
    driver.findElement(By.linkText("Configuration")).click();
    Thread.sleep(2000);
    driver.findElement(By.linkText("Import Settings")).click();
    Thread.sleep(3000);
    String expectedtitleImport=driver.getTitle();
    printConsole(expectedtitleImport);
    String actualimporter ="Vantage - Import Settings";
    Assert.assertEquals(actualimporter, expectedtitleImport);
   //actibank.com
    //cn=actibank.com
    WebElement emailDomain=driver.findElement(By.name("employeeEmailDomains"));
    emailDomain.clear();
    emailDomain.sendKeys("cn=actibank.com");
    WebElement createnew=driver.findElement(By.xpath(".//*[@id='content']/div[7]/table/tbody/tr/td[3]/input[2]"));
    createnew.click();
    WebElement Importername=driver.findElement(By.name("importerName"));
    Importername.clear();
    Importername.sendKeys("Importer"+t);
    driver.findElement(By.xpath(".//*[@id='content']/h2[3]/a")).click();

    WebElement ImporterStatus=driver.findElement(By.name("importerEnabled"));
    if ( !ImporterStatus.isSelected() )
    {
    	ImporterStatus.click();
    }
    WebElement filelocation=driver.findElement(By.name("fileLocation"));
    filelocation.clear();
    filelocation.sendKeys("C:\\users");
    
    WebElement serverid=driver.findElement(By.name("serverIDs"));
    Select sel = new Select(serverid);
    //sel.selectByVisibleText("3");
    sel.selectByIndex(0);
    WebElement network=driver.findElement(By.name("network"));
    Select sel1 = new Select(network);
    sel1.selectByVisibleText("Jabber");
    WebElement type=driver.findElement(By.id("xmlTypeList"));
    Select sel2 = new Select(type);
    sel2.selectByVisibleText("Employee View");
    driver.findElement(By.id("btnApply")).click();
    printConsole("Importer is created sucessfully");
  
    String expectedimport= driver.findElement(By.name("importerName")).getText();
    String actualimport ="Importer"+t;
    if (actualimport.contains(expectedimport))
	{
		printConsole("Import created successfully");
	}
	else{
		System.err.println("*********Import not created*******");
	}
}
     
@Test(enabled=true,priority=3)
public void createExporter() throws InterruptedException{
	
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
   Thread.sleep(2000);
    //verify the title page of apps
    String actualTitle = driver.getTitle();
    printConsole("Title page"+" : "+actualTitle);
    String expectedTitle = "Vantage - Dashboard";
    
    Assert.assertEquals(actualTitle, expectedTitle);
    
    driver.findElement(By.linkText("Configuration")).click();
    Thread.sleep(2000);
    driver.findElement(By.linkText("Interaction Exporters")).click();
    Thread.sleep(3000);
    String expectedtitleExport=driver.getTitle();
    printConsole(expectedtitleExport);
    WebElement exportername=driver.findElement(By.name("exporterName"));
    exportername.clear();
    exportername.sendKeys("Exporter"+t);
    
    WebElement exporterType=driver.findElement(By.xpath(".//*[@id='filter_exporterType']"));
    Select sel_exp = new Select(exporterType);
    sel_exp.selectByVisibleText("Exporter for IM interactions");
    
    driver.findElement(By.xpath(".//*[@id='content']/h2[2]/a")).click();
    
    WebElement EmporterStatus=driver.findElement(By.name("enabledFlag"));
    if ( !EmporterStatus.isSelected() )
    {
    	EmporterStatus.click();
    }
    WebElement logging=driver.findElement(By.name("logStatusFlag"));
    if ( !logging.isSelected() )
    {
    	logging.click();
    }
    
    WebElement serverid1=driver.findElement(By.name("serverIDs"));
    Select sel1_exp = new Select(serverid1);
    sel1_exp.selectByIndex(0);
    //sel1_exp.selectByVisibleText("3");
    
    driver.findElement(By.xpath(".//*[@id='content']/h2[7]/a")).click();
    WebElement OutputFormat=driver.findElement(By.name("converterClass"));
    Select sel2_exp = new Select(OutputFormat);
    sel2_exp.selectByIndex(1);
    driver.findElement(By.id("submit3")).click();
    
    String expectedexport= driver.findElement(By.name("exporterName")).getText();
    String actualexport  ="Exporter"+t;
    if (actualexport.contains(expectedexport))
	{
		printConsole("Export created successfully");
	}
	else{
		System.err.println("*********Export not created*******");
	}
    printConsole("Sucessfully verified");
}

public void printConsole(String str)
{
	System.out.println(str);
	//File f = new File("");
	try(FileWriter fw = new FileWriter("d:\\logs.txt", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw))
		{
		    out.println(str);
		    //more code
		   // out.println("more text");
		    //more code
		} catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
}


    
}

		
	

		
		
	
		
	

