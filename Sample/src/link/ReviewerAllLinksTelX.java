package link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;




public class ReviewerAllLinksTelX {
WebDriver driver;
	//@Test
	//public void clickonallLinks() throws FileNotFoundException, IOException{
		public static void main(String args[]) throws FileNotFoundException, IOException, Exception
		{
			   /*File file = new File("D:/TestData/allLinks_reviewer.txt");
               try {
                            PrintStream pr = new PrintStream(file);
                            System.setOut(pr);
            } catch (FileNotFoundException e2) {
                            // TODO Auto-generated catch block
                            e2.printStackTrace();
            }
               System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
               
               WebDriver driver = new ChromeDriver();*/
		WebDriver driver= new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
/*        driver.get("https://apps.collabservsvtperf1.com");
        driver.findElement(By.xpath(".//*[@id='username']")).sendKeys("acti_cu1@isc4sb.com");
        driver.findElement(By.xpath(".//*[@id='continue']")).click();
        driver.findElement(By.xpath(".//*[@id='password']")).sendKeys("passw0rd");
        driver.findElement(By.xpath(".//*[@id='submit_form']")).click();*/
        
        //driver.navigate().to("https://comp1.actiance.net:8443/ima/sso.do?method=samlSSO&customerId=21474254");
        driver.get("https://tenant1.actiance.net:8443/ima/dashboardReviewer.do");
        WebElement username=driver.findElement(By.xpath(".//input[@name='username']"));
        username.sendKeys("reviewer");
        // setting the password 
        WebElement password=driver.findElement(By.xpath(".//input[@name='password']"));
        password.sendKeys("FaceTime@123");
        //click on login 
        WebElement login=driver.findElement(By.id("footerRight"));
        login.click();
        
       // driver.get("https://comp1.actiance.net:8443/ima/sso.do?method=samlSSO&customerId=21474254");
        
      //  String line; for giving ip in cmd
        //String ip = args[0];
    /*    try (
            InputStream fis = new FileInputStream("config.txt");
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
           BufferedReader br = new BufferedReader(isr);
        ) {
           while ((line = br.readLine()) != null) {
            ip = line;
           break;
           }
      }*/
       /* System.out.println("Machine details :"+ip);
        driver.get("https://"+ip+":8443/ima/login.do");*/
        //driver.get("https://192.168.116.151:8443/ima/login.do");
        //driver.get("https://192.168.117.132:8443/ima/login.do");
        // setting the username 
        
        
        
     /*   WebElement username=driver.findElement(By.xpath(".//input[@name='username']"));
        username.sendKeys("reviewer");
        // setting the password 
        WebElement password=driver.findElement(By.xpath(".//input[@name='password']"));
        password.sendKeys("facetime");
        //click on login 
        WebElement login=driver.findElement(By.id("footerRight"));
        login.click();*/
        try {
                        Thread.sleep(3000);
        } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
        }     
        String actualTitle = driver.getTitle();
        System.out.println("Title page"+" : "+actualTitle);
        String expectedTitle = "Vantage - Dashboard";
        Thread.sleep(3000);
        
        Assert.assertEquals(actualTitle, expectedTitle);

        System.out.println("*************"+"Restricted Phrases"+"**************"); 
        
        WebElement Interact=driver.findElement(By.linkText("Interactions"));
        Thread.sleep(2000);
        Interact.click();
        Thread.sleep(2000);
        
        List<WebElement> allLinks_Interact = driver.findElements(By.tagName("a"));
        int total_Interact = allLinks_Interact.size();
        System.out.println(total_Interact);
        List<String> urlList = new ArrayList<String>();
        
        for (WebElement link : allLinks_Interact) {
        	 
        System.out.println(link.getAttribute("href"));
        urlList.add(link.getAttribute("href"));
        	 
        System.out.println(link.getText());
        
        }
        List<String> ignoreList_Interact = new ArrayList<String>();
        ignoreList_Interact.add("logout");
        ignoreList_Interact.add("actiance.com");
        
        Iterator itr = urlList.iterator();
        while(itr.hasNext())
        {
        String url_Interact = (String) itr.next();
        //System.out.println("Clicking url : "+url);
       
        Iterator<String> itr2 = ignoreList_Interact.iterator();  /// ignore the string
        boolean ignoreFlag_Interact = false;
        while(itr2.hasNext())
        {
        	String ignore_Interact = (String) itr2.next();
        	//System.out.println("Checking for :"+ignore);
        	if(url_Interact.contains(ignore_Interact))
        	{
        		ignoreFlag_Interact = true;
        	//	System.out.println("ignoring the url");
        	}
        }
        //System.out.println("Ignore Flag:"+ignoreFlag);
        if(!ignoreFlag_Interact){
        	//Code to click and verify
       // System.out.println("Clicking :"+url);
        	driver.get(url_Interact);
        	System.out.println(url_Interact+" ---------- "+driver.getTitle());
        	
        /*	if(driver.getPageSource().contains("error")){
        		System.err.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        }*/
        	 }
         }
System.out.println("*************"+"Restricted Phrases"+"**************"); 
        WebElement Restrict=driver.findElement(By.linkText("ILP / Restricted Phrases"));
        Thread.sleep(2000);
        Restrict.click();
        Thread.sleep(2000);
        List<WebElement> allLinks_Restrict = driver.findElements(By.tagName("a"));
        int total_Restrict = allLinks_Restrict.size();
        System.out.println(total_Restrict );
        List<String> urlList_Restrict= new ArrayList<String>();
        
        for (WebElement link_Restrict  : allLinks_Restrict ) {
        urlList_Restrict.add(link_Restrict .getAttribute("href"));
        	 
        System.out.println(link_Restrict .getText());
        }
        
        List<String> ignoreList_Restrict = new ArrayList<String>();
        ignoreList_Restrict.add("logout");
        ignoreList_Restrict.add("actiance.com");

        
        Iterator itr_Restrict = urlList_Restrict.iterator();
        while(itr_Restrict.hasNext())
        {
        String url_dsh = (String) itr_Restrict.next();
        
        Iterator<String> itr_ignoreRestrict = ignoreList_Restrict.iterator();
        boolean ignoreFlag_Restrict = false;
        while(itr_ignoreRestrict.hasNext())
        {
        	String ignore_Restrict = (String) itr_ignoreRestrict.next();
    
        	if(url_dsh.contains(ignore_Restrict))
        	{
        		ignoreFlag_Restrict = true;
        	
        	}
        }
        System.out.println("Ignore Flag:"+ignoreFlag_Restrict);
        if(!ignoreFlag_Restrict){
        	//Code to click and verify
       System.out.println("Clicking :"+url_dsh);
        	driver.get(url_dsh);
        	//System.out.println(url_dsh+" ---------- "+driver.getTitle());
        	
        /*	if(driver.getPageSource().contains("error")){
        		System.err.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        	}*/
        }
        }
        	

       
            System.out.println("*************"+"Dashboard opration"+"**************"); 
            
            List<WebElement> allLinks_dsh = driver.findElements(By.tagName("a"));
            int total_dsh = allLinks_dsh.size();
            System.out.println(total_dsh );
            List<String> urlList_dsh= new ArrayList<String>();
            
            for (WebElement link_dsh  : allLinks_dsh ) {
            urlList_dsh.add(link_dsh .getAttribute("href"));
            	 
            System.out.println(link_dsh .getText());
            }
            
            List<String> ignoreList_dsh = new ArrayList<String>();
            ignoreList_dsh.add("logout");
            ignoreList_dsh.add("actiance.com");
     
            
            Iterator itr_dsh = urlList_dsh.iterator();
            while(itr_dsh.hasNext())
            {
            String url_dsh = (String) itr_dsh.next();
            
            Iterator<String> itr_igndsh = ignoreList_dsh.iterator();
            boolean ignoreFlag_dsh = false;
            while(itr_igndsh.hasNext())
            {
            	String ignore_dsh = (String) itr_igndsh.next();
        
            	if(url_dsh.contains(ignore_dsh))
            	{
            		ignoreFlag_dsh = true;
            	
            	}
            }
            System.out.println("Ignore Flag:"+ignoreFlag_dsh);
            if(!ignoreFlag_dsh){
            	//Code to click and verify
           System.out.println("Clicking :"+url_dsh);
            	driver.get(url_dsh);
            	System.out.println(url_dsh+" ---------- "+driver.getTitle());
            	
            /*	if(driver.getPageSource().contains("error")){
            		System.err.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            	}*/
            }
            }
            
            System.out.println("*************"+"Preferences"+"**************");
            
            WebElement Preferences=driver.findElement(By.linkText("Preferences"));
            Thread.sleep(2000);
            
            Preferences.click();
            Thread.sleep(2000);
            
            List<WebElement> allLinks_pf = driver.findElements(By.tagName("a"));
            int total_pf = allLinks_pf.size();
            System.out.println(total_pf );
            List<String> urlList_pf= new ArrayList<String>();
            
            for (WebElement link_pf  : allLinks_pf ) {
      
            
            urlList_pf.add(link_pf .getAttribute("href"));
            	 
            
            
            }

            List<String> ignoreList_pf = new ArrayList<String>();
            ignoreList_pf.add("logout");
            ignoreList_pf.add("actiance.com");
            
            Iterator itr_pf = urlList_pf.iterator();
            while(itr_pf.hasNext())
            {
            String url_pf = (String) itr_pf.next();
         
            Iterator<String> itr2_pf = ignoreList_pf.iterator();
            boolean ignoreFlag_pf = false;
            while(itr2_pf.hasNext())
            {
            	String ignore_pf = (String) itr2_pf.next();
           
            	if(url_pf.contains(ignore_pf))
            	{
            		ignoreFlag_pf = true;
    
            	}
            }
            
            //System.out.println("Ignore Flag:"+ignoreFlag);
            if(!ignoreFlag_pf){
            	//Code to click and verify
           // System.out.println("Clicking :"+url);
            	driver.get(url_pf);
            	
            	System.out.println(url_pf+" ---------- "+driver.getTitle());
            	
           
}
            
	}
            System.out.println("Review session completed");
	}
		
}

        
	
       
 
