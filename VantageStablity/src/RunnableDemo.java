import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

class RunnableDemo implements Runnable {
   private Thread t;
   private String threadName;
   WebDriver driver;
   int counter =0;
   RunnableDemo( String name) {
      threadName = name;
      System.out.println("Creating " +  threadName );
      
   }
   
   
   public void run() {
	   
	   if (counter!=1){
		  
		  System.out.println("hello");
	   
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
		//counter++;
		 
		}
	   
	   else{
		   
			   System.out.println("hello22");
		  
		  
		   }
	   }
//	   String actualgroup = driver.getTitle();
//	   String expectedgroup = "Vantage - Groups";
//	   boolean a =actualgroup.contains(expectedgroup);
//		 while(a){
//			 
//		WebElement group_click=driver.findElement(By.linkText("Groups"));
//		group_click.click();
//		
//		System.out.println("Title page"+" : "+actualgroup);
//		
//		if (actualgroup.contains(expectedgroup))
//		{
//			System.out.println("Continue performing click operation");
//		}
//		else{
//			System.out.println("Fail the test flow");
//		}
//		
//		Assert.assertEquals(actualgroup, expectedgroup);
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		WebElement dashboard_click=driver.findElement(By.linkText("Dashboard"));
//		dashboard_click.click();
//		String actualdashboard = driver.getTitle();
//		System.out.println("Title page"+" : "+actualdashboard);
//		String expecteddashboard = "Vantage - Dashboard";
//		
//		if (actualdashboard.contains(expecteddashboard))
//		{
//			System.out.println("Continue performing click operation");
//		}
//		else{
//			System.out.println("Fail the test flow");
//		}
//		
//		
//		Assert.assertEquals(actualdashboard, expecteddashboard);
		// }
 //  }
   public void start () {
	      System.out.println("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }


   public static void main(String args[]) {
	      RunnableDemo R1 = new RunnableDemo( "Thread-1");
	      R1.start();
	      
	      RunnableDemo R2 = new RunnableDemo( "Thread-2");
	      R2.start();
	   }   
	}  

