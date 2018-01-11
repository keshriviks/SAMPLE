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

public class Policies {

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
       System.out.println("size :"+urlList_policy.size());
       System.out.println("size :"+urlList_policy);
        
        
        Iterator itr_pol = urlList_policy.iterator();
        while(itr_pol.hasNext())
        {
        String url_policy = (String) itr_pol.next();
        //System.out.println("Clicking url : "+url);
        //if the url doesnt contain any string from ignorelist then proceed
        Iterator<String> itr2_pol = ignoreList_policy.iterator();
        boolean ignoreFlag_pol = false;
        while(itr2_pol.hasNext())
        {
        	String ignore1 = (String) itr2_pol.next();
        	//System.out.println("Checking for :"+ignore);
        	System.out.println(ignore1);
        	System.out.println(url_policy);
        	if(url_policy.contains(ignore1))
        	{
        		ignoreFlag_pol = true;
        	//	System.out.println("ignoring the url");
        	}
        }
        //System.out.println("Ignore Flag:"+ignoreFlag);
        if(!ignoreFlag_pol){
        	//Code to click and verify
       // System.out.println("Clicking :"+url);
        	driver.get(url_policy);
        	
        	System.out.println(url_policy+" ---------- "+driver.getTitle());
        	
        	if(driver.getPageSource().contains("error")){
        		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        	
        	}
        	 
        }
        }
     /*   WebElement db1=driver.findElement(By.linkText("Groups"));
        db1.click();*/
        
       
       
	}
        
	}
	


