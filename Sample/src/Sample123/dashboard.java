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



public class dashboard {

	

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
        
       /* WebElement Groups=driver.findElement(By.linkText("Groups"));
        Groups.click();*/
        
        List<WebElement> allLinks_dsh = driver.findElements(By.tagName("a"));
        int total_dsh = allLinks_dsh.size();
        System.out.println(total_dsh );
        List<String> urlList_dsh= new ArrayList<String>();
        
        for (WebElement link_dsh  : allLinks_dsh ) {
        System.out.println(link_dsh .getText());
        System.out.println(link_dsh .getAttribute("href"));
        urlList_dsh.add(link_dsh .getAttribute("href"));
        	 
        System.out.println(link_dsh .getText());
        
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
        
        Iterator itr_dsh = urlList_dsh.iterator();
        while(itr_dsh.hasNext())
        {
        String url_dsh = (String) itr_dsh.next();
        //System.out.println("Clicking url : "+url);
        //if the url doesnt contain any string from ignorelist then proceed
        Iterator<String> itr2_dsh = ignoreList.iterator();
        boolean ignoreFlag1 = false;
        while(itr2_dsh.hasNext())
        {
        	String ignore1 = (String) itr2_dsh.next();
        	//System.out.println("Checking for :"+ignore);
        	if(url_dsh.contains(ignore1))
        	{
        		ignoreFlag1 = true;
        	//	System.out.println("ignoring the url");
        	}
        }
        //System.out.println("Ignore Flag:"+ignoreFlag);
        if(!ignoreFlag1){
        	//Code to click and verify
       // System.out.println("Clicking :"+url);
        	driver.get(url_dsh);
        	//Thread.sleep(3000);
        	System.out.println(url_dsh+" ---------- "+driver.getTitle());
        	
        	
        	 /*Alert alert = driver.switchTo().alert();
        	 alert.dismiss();*/
        	
        	if(driver.getPageSource().contains("error"))
        		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        	
        	/*ArrayList<String> winIds = new ArrayList<String>();
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

