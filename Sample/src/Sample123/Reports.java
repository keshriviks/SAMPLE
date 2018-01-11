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



public class Reports {

	

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
        
        WebElement Reports=driver.findElement(By.linkText("Reports"));
        Reports.click();
        
        List<WebElement> allLinks_reports = driver.findElements(By.tagName("a"));
        int total_reports = allLinks_reports.size();
        System.out.println(total_reports );
        List<String> urlList_reports= new ArrayList<String>();
        
        for (WebElement link_reports  : allLinks_reports ) {
        System.out.println(link_reports .getText());
        System.out.println(link_reports .getAttribute("href"));
        urlList_reports.add(link_reports .getAttribute("href"));
        	 
        System.out.println(link_reports .getText());
        
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
        
        Iterator itr_rep = urlList_reports.iterator();
        while(itr_rep.hasNext())
        {
        String url_rep = (String) itr_rep.next();
        //System.out.println("Clicking url : "+url);
        //if the url doesnt contain any string from ignorelist then proceed
        Iterator<String> itr2_rep = ignoreList.iterator();
        boolean ignoreFlag1 = false;
        while(itr2_rep.hasNext())
        {
        	String ignore1 = (String) itr2_rep.next();
        	//System.out.println("Checking for :"+ignore);
        	if(url_rep.contains(ignore1))
        	{
        		ignoreFlag1 = true;
        	//	System.out.println("ignoring the url");
        	}
        }
        //System.out.println("Ignore Flag:"+ignoreFlag);
        if(!ignoreFlag1){
        	//Code to click and verify
       // System.out.println("Clicking :"+url);
        	driver.get(url_rep);
        	//Thread.sleep(3000);
        	System.out.println(url_rep+" ---------- "+driver.getTitle());
        	
        	////window handle
        	Set<String> handles=driver.getWindowHandles();
	      	String parent_window = driver.getWindowHandle();
	      	for (String childwins : handles){
	      	if(!(parent_window).equals(handles)){	
                parent_window =	childwins;
                driver.switchTo().window(childwins);
                //driver.switchTo().window(parent_window);
        	
	      	}
        	
        	if(driver.getPageSource().contains("error")){
        		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        	}
      /*  	ArrayList<String> winIds = new ArrayList<String>();
        	Set<String> handles=driver.getWindowHandles();
        	      	String parent_window = driver.getWindowHandle();
        	Iterator tr_han=handles.iterator();
        	while(tr_han.hasNext()){
        		String child=(String) tr_han.next();
        		winIds.add(child);
        		//String child_window=(String) tr_han.next();
        	}
        	driver.switchTo().window(winIds.get(1)).close();
        	driver.switchTo().window(winIds.get(2)).close();
        	driver.switchTo().window(winIds.get(3)).close();*/
        	
	      	}
        }                              
        }
	}
        	  

        	
        	
        }
        

                

     
        	
        	
 
	


