package Sample123;




import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import com.thoughtworks.selenium.webdriven.commands.Click;

public class Employees {

	

	public static void main(String[] args) throws InterruptedException {
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
        
        WebElement Employees=driver.findElement(By.linkText("Employees"));
        Employees.click();
        
        List<WebElement> allLinks_emp = driver.findElements(By.tagName("a"));
        int total_emp = allLinks_emp.size();
        System.out.println(total_emp );
        List<String> urlList_emp= new ArrayList<String>();
        
        for (WebElement link_emp  : allLinks_emp ) {
        System.out.println(link_emp .getText());
        System.out.println(link_emp .getAttribute("href"));
        urlList_emp.add(link_emp .getAttribute("href"));
        	 
        System.out.println(link_emp .getText());
        
        }
/*        List<String> ignoreList_policy = new ArrayList<String>();
        ignoreList_policy.add("logout");
        ignoreList_policy.add("actiance.com");
        ignoreList_policy.add("SharePoint Default Policy");
        ignoreList_policy.add("Connections Default Policy");
        ignoreList_policy.add("About Vantage");
        ignoreList_policy.add("Preferences");*/
        
        List<String> ignoreList = new ArrayList<String>();
        ignoreList.add("logout");
        ignoreList.add("actiance.com");
        
        Iterator itr_emp = urlList_emp.iterator();
        while(itr_emp.hasNext())
        {
        String url_emp = (String) itr_emp.next();
        //System.out.println("Clicking url : "+url);
        //if the url doesnt contain any string from ignorelist then proceed
        Iterator<String> itr2_emp = ignoreList.iterator();
        boolean ignoreFlag1 = false;
        while(itr2_emp.hasNext())
        {
        	String ignore1 = (String) itr2_emp.next();
        	//System.out.println("Checking for :"+ignore);
        	if(url_emp.contains(ignore1))
        	{
        		ignoreFlag1 = true;
        	//	System.out.println("ignoring the url");
        	}
        }
        //System.out.println("Ignore Flag:"+ignoreFlag);
        if(!ignoreFlag1){
        	//Code to click and verify
       // System.out.println("Clicking :"+url);
        	driver.get(url_emp);
        	//Thread.sleep(3000);
        	System.out.println(url_emp+" ---------- "+driver.getTitle());
        	
        	
        	 /*Alert alert = driver.switchTo().alert();
        	 alert.dismiss();*/
        	
        	if(driver.getPageSource().contains("error")){
        		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        	}
        
        	
        	
        	                               
        	
        	  

        	
        	
        }
        }	

                

        }
        	
        	
 
	
}

