package Sample123;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

class sampleFinal implements Runnable {
   private Thread t;
   private String threadName;
   WebDriver driver;
   
   int counter =0;
    int n=1000;
    sampleFinal( String name) {
      threadName = name;
      System.out.println("Creating " +  threadName );
      
   }
   
   
   public void run() {
	   
	   Date d = new Date();
	   SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	   
	   
	   
	   /*if (counter==0){*/
		  System.out.println("Browser open one time");
		  WebDriver driver= new FirefoxDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.get("https://192.168.119.129:8443/ima/login.do");
			
			WebElement username=driver.findElement(By.xpath(".//input[@name='username']"));
			username.sendKeys("sysadmin");
			WebElement password=driver.findElement(By.xpath(".//input[@name='password']"));
			password.sendKeys("facetime");
			WebElement login=driver.findElement(By.id("footerRight"));
			login.click();
			String actualTitle = driver.getTitle();
			System.out.println("Title page"+" : "+actualTitle);
			String expectedTitle = "Vantage - Dashboard";
			
			Assert.assertEquals(actualTitle, expectedTitle);
	   
		/*counter++;
		 
		}
	   else{
		    System.out.println("Browser open more than one time");
		    
		   }*/
	   
	   while(true){  
		   for(int i=1; i<n; i++){
	        System.out.println("count"+i); 
	        
	        WebElement group_click=driver.findElement(By.linkText("Groups"));
			group_click.click();
			String actualgroup = driver.getTitle();
		   
	     
	     String expectedgroup = "Vantage - Groups";
	     
	     if (actualgroup.contains(expectedgroup))
			{
				System.out.println("Continue performing click operation");
			}
			else{
				System.out.println("Fail the test flow for Groups");
			}
			
		//Assert.assertEquals(actualgroup, expectedgroup);
		//System.out.println("Title page"+" : "+actualgroup);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WebElement dashboard_click=driver.findElement(By.linkText("Dashboard"));
		dashboard_click.click();
		String actualdashboard = driver.getTitle();
		
		
		String expecteddashboard = "Vantage - Dashboard";
		if (actualdashboard.contains(expecteddashboard))
		{
			System.out.println("Continue performing click operation");
		}
		else{
			System.out.println("Fail the test flow for Dashboard");
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Assert.assertEquals(actualdashboard, expecteddashboard);
		
		   }
	     }
		
       }
   
   public void start () {
	      System.out.println("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }
   

   public static void main(String args[]) {
	   sampleFinal R1 = new sampleFinal( "Thread-1");
	      R1.start();
	           
	   }   
	}  

