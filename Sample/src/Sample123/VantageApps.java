package Sample123;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

class VantageApps implements Runnable {
   private Thread t;
   private String threadName;
   WebDriver driver;
   
   int counter =0;
    int n=1000;
    VantageApps( String name) {
      threadName = name;
      System.out.println("Creating " +  threadName );
      
   }
   
   
   public void run() {
	   //Date d = new Date();
	   //SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	   Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		    WebDriver driver= new FirefoxDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.get("https://192.168.125.82:8443/ima/login.do");
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
			
	   
	   while(true){  
		   for(int i=1; i<n; i++){
	        System.out.println("count no"+":"+i); 
	        
	        //click on configuration link
	        WebElement Configuration=driver.findElement(By.linkText("Configuration"));
	        Configuration.click();
	        try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        //verifying the configuration page
			String actualconfiguration = driver.getTitle();
			System.out.println("Actual Configuration"+":"+actualconfiguration);
		   
			String expectedconfiguration = "Vantage - Configuration";
	     
	     if (actualconfiguration.contains(expectedconfiguration))
			{
				System.out.println("Pass the flow for configuration");
			}
			else{
				System.out.println("Fail the test flow for configuration");
			}
	     
	     
	     //Assert.assertEquals(actualconfiguration, expectedconfiguration);
	     
	     //click on deployment link
	     WebElement deployment=driver.findElement(By.linkText("Deployment"));
	        deployment.click();
	        try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        //verifying the deployment-link page
			String actualdeployment = driver.getTitle();
			System.out.println("Actual deployment"+":"+actualdeployment);
			String expecteddeployment = "Vantage - Deployment";
	     
	     if (actualdeployment.equals(expecteddeployment))
			{
				System.out.println("Pass the flow for deployment");
			}
			else{
				System.out.println("Fail the test flow for deployment");
			}
		 
		 //Assert.assertEquals(actualdeployment, expecteddeployment);
		 
		 // Again click on configuration link
		 WebElement Configuration1=driver.findElement(By.linkText("Configuration"));
	        Configuration1.click();
	        try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String actualconfiguration1 = driver.getTitle();
			System.out.println("Actual Configuration1"+":"+actualconfiguration1);
		   
			String expectedconfiguration1 = "Vantage - Configuration";
	     
	     if (actualconfiguration1.contains(expectedconfiguration1))
			{
				System.out.println("Pass the flow for configuration1");
			}
			else{
				System.out.println("Fail the test flow for configuration1");
			}
	    
	     //Assert.assertEquals(actualconfiguration1, expectedconfiguration1);
	     //click on cluster configuration link
	     WebElement clusterconfiguration=driver.findElement(By.linkText("Cluster Configuration"));
	     clusterconfiguration.click();
	     try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String actualcluster = driver.getTitle();
		    System.out.println("Actual clusterconfiguration"+":"+actualcluster+" - "+new Timestamp(System.currentTimeMillis()));
			String expectedcluster = "Vantage - Cluster Configuration";
			
	     
	     if (actualcluster.contains(expectedcluster))
			{
				System.out.println("Pass the flow for Cluster Configuration");
			}
			else{
				System.out.println("Fail the test flow for Cluster Configuration");
			}
		 
		 
		 //Assert.assertEquals(actualcluster, expectedcluster);
	     
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
	   VantageApps R1 = new VantageApps( "Thread-1");
	      R1.start();
	           
	   }   
	}  

