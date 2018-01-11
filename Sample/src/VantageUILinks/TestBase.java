package VantageUILinks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

public class TestBase {

	  WebDriver driver;
	 static Properties OR;
	 Long t=System.currentTimeMillis(); 
	 
	 public  void loadData() throws IOException  { 
		    OR = new Properties();
			File file = new File("C:\\Users\\VKeshri\\workspace\\Sample\\src\\VantageUILinks\\config1.properties");
			FileInputStream f = new FileInputStream(file);
			OR.load(f);
			
			System.out.println("username ?11"+" : "+ getObject("username"));
			System.out.println("password ?11"+" : "+getObject("password"));
			
	}
	 
	 public static String getObject(String Data){
		 String data = OR.getProperty(Data);
		 return data;
		 
	 }
	 
	 public void selectBrowser(String browser) {
			
			if (browser.equals("chrome")) {
				System.out.println(System.getProperty("user.dir"));
				System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
				driver = new ChromeDriver();
				
			} else if (browser.equals("firefox")) {
				
				System.setProperty("webdriver.gecko.driver", "D:\\Software\\firefox\\geckodriver.exe");
				driver = new FirefoxDriver();
				}
   }
	 
	 public void init() throws IOException {
			loadData();
			//String log4jConfPath = "log4j.properties";
			//PropertyConfigurator.configure(log4jConfPath);
			System.out.println("browser name ?"+" : "+getObject("browser"));
			selectBrowser(getObject("browser"));
			System.out.println("url for apps ?"+" : "+getObject("url"));
			getUrl(getObject("url"));
			
		}
	 
	 public void getUrl(String url) {
			//log.info("navigating to :-" + url);
			driver.get(url);
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
		    driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
		}
	 
	 public void loginToapplication(){
	        driver.findElement(By.xpath(".//input[@name='username']")).sendKeys(getObject("username"));
	        driver.findElement(By.xpath(".//input[@name='password']")).sendKeys(getObject("password"));
	        driver.findElement(By.id("footerRight")).click();
	        
	 }
	 
	 
	 
	 public void closeBrowser(){
		// driver.findElement(By.xpath(".//*[@id='mockupNotesLink']/a")).click();
			driver.close();
	 }
	 
	 public void policyLinks() throws InterruptedException{
		 
		 Thread.sleep(5000);
		 String actualTitle = driver.getTitle();
		 System.out.println("Title page"+" : "+actualTitle);
		 String expectedTitle = "Vantage - Dashboard";

		 Assert.assertEquals(actualTitle, expectedTitle);

		 System.out.println("*************"+"Polices"+"**************");

		           
		                        WebElement Policies=driver.findElement(By.linkText("Policies"));
		                        Policies.click();
		                        
		                        List<WebElement> allLinks_policy = driver.findElements(By.tagName("a"));
		                        int total_Policy= allLinks_policy.size();
		                        System.out.println(total_Policy);
		                        List<String> urlList_policy = new ArrayList<String>();
		                        
		                        for (WebElement link_policy : allLinks_policy) {
		                        	
		                        	if(link_policy.getAttribute("href")!= null)
		                        	{
		                        System.out.println(link_policy.getAttribute("href"));
		                        urlList_policy.add(link_policy.getAttribute("href"));
		                        	 
		                        System.out.println(link_policy.getText());
		                        	}
		                        }
		                        List<String> ignoreList_policy = new ArrayList<String>();
		                        ignoreList_policy.add("logout");
		                        ignoreList_policy.add("actiance.com");
		                        ignoreList_policy.add("collaborationpolicies.do");
		                        
		                        
		                        Iterator itr_pol = urlList_policy.iterator();
		                        while(itr_pol.hasNext())
		                        {
		                        String url_policy = (String) itr_pol.next();
		                       
		                        Iterator<String> itr2_pol = ignoreList_policy.iterator();
		                        boolean ignoreFlag_pol = false;
		                        while(itr2_pol.hasNext())
		                        {
		                        	String ignore_pol = (String) itr2_pol.next();
		                        	if(url_policy.contains(ignore_pol))
		                        	{
		                        		ignoreFlag_pol = true;
		                        	}
		                        }
		                 
		                        if(!ignoreFlag_pol){
		                     
		                        	driver.get(url_policy);
		                        	
		                        	System.out.println(url_policy+" ---------- "+driver.getTitle());
		                        	
		                        	if(driver.getPageSource().contains("error")){
		                        		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		                        	}
		                    }
		                        }
		 	
		 	
		 

	 }
	 
	 
}
