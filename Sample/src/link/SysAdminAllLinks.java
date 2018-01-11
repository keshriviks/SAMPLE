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




public class SysAdminAllLinks {
WebDriver driver;





	//@Test
	//public void clickonallLinks() throws FileNotFoundException, IOException{
		public static void main(String args[]) throws FileNotFoundException, IOException, Exception
		{
			   File file = new File("D:/TestData/allLinks_Session.txt");
               try {
                            PrintStream pr = new PrintStream(file);
                            System.setOut(pr);
            } catch (FileNotFoundException e2) {
                            // TODO Auto-generated catch block
                            e2.printStackTrace();
            }
               
               System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
               
               WebDriver driver = new ChromeDriver();
		//WebDriver driver= new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        //driver.get("https://192.168.114.28:8443/ima/login.do");
        
  /*    String ip = null;
		//  String line; for giving ip in cmd
        //String ip = args[0];
        try (
            InputStream fis = new FileInputStream("config.txt");
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
           BufferedReader br = new BufferedReader(isr);
        ) {
           String line;
		while ((line = br.readLine()) != null) {
            ip = line;
           break;
           }
      }
        System.out.println("Machine details :"+ip);
        driver.get("https://"+ip+":8443/ima/login.do");*/
        driver.get("https://192.168.116.151:8443/ima/login.do");
        
//        driver.get("https://192.168.117.132:8443/ima/login.do");
        // setting the username 
        WebElement username=driver.findElement(By.xpath(".//input[@name='username']"));
        username.sendKeys("sysadmin");
        // setting the password 
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
        String actualTitle = driver.getTitle();
        System.out.println("Title page"+" : "+actualTitle);
        String expectedTitle = "Vantage - Dashboard";
        
        Assert.assertEquals(actualTitle, expectedTitle);
       
        WebElement Configuration=driver.findElement(By.linkText("Configuration"));
        Configuration.click();
        
        List<WebElement> allLinks_config = driver.findElements(By.tagName("a"));
        int total_ConfigLink = allLinks_config.size();
        System.out.println(total_ConfigLink);
        List<String> urlList = new ArrayList<String>();
        
        for (WebElement link : allLinks_config) {
        	 
        System.out.println(link.getAttribute("href"));
        urlList.add(link.getAttribute("href"));
        	 
        System.out.println(link.getText());
        
        }
        List<String> ignoreList_config = new ArrayList<String>();
        ignoreList_config.add("logout");
        ignoreList_config.add("actiance.com");
        
        Iterator itr = urlList.iterator();
        while(itr.hasNext())
        {
        String url_config = (String) itr.next();
        //System.out.println("Clicking url : "+url);
       
        Iterator<String> itr2 = ignoreList_config.iterator();  /// ignore the string
        boolean ignoreFlag_config = false;
        while(itr2.hasNext())
        {
        	String ignore_con = (String) itr2.next();
        	//System.out.println("Checking for :"+ignore);
        	if(url_config.contains(ignore_con))
        	{
        		ignoreFlag_config = true;
        	//	System.out.println("ignoring the url");
        	}
        }
        //System.out.println("Ignore Flag:"+ignoreFlag);
        if(!ignoreFlag_config){
        	//Code to click and verify
       // System.out.println("Clicking :"+url);
        	driver.get(url_config);
        	System.out.println(url_config+" ---------- "+driver.getTitle());
        	
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
        ignoreList_dsh.add("Policies");
        ignoreList_dsh.add("Groups");
        ignoreList_dsh.add("Configuration");
        ignoreList_dsh.add("Preferences");
        ignoreList_dsh.add("Reports");
        ignoreList_dsh.add("Employees");
        
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
        	

        System.out.println("*************"+"Groups"+"**************");
            ///////////////////////groups//////////////////
            WebElement Groups=driver.findElement(By.linkText("Groups"));
            Groups.click();
            
            List<WebElement> allLinks_grp = driver.findElements(By.tagName("a"));
            int total_grp = allLinks_grp.size();
            System.out.println(total_grp );
            List<String> urlList_grp= new ArrayList<String>();
            
            for (WebElement link_grp  : allLinks_grp ) {
       
            System.out.println(link_grp .getAttribute("href"));
            urlList_grp.add(link_grp .getAttribute("href"));
            	 
            System.out.println(link_grp .getText());
            
            }
            
            List<String> ignoreList_grps = new ArrayList<String>();
            ignoreList_grps.add("logout");
            ignoreList_grps.add("actiance.com");
            
            Iterator itr_grp = urlList_grp.iterator();
            while(itr_grp.hasNext())
            {
            String url_grp = (String) itr_grp.next();
          
            Iterator<String> itr_Igngrp = ignoreList_grps.iterator();
            boolean ignoreFlag_grp = false;
            while(itr_Igngrp.hasNext())
            {
            	String ignore_grp = (String) itr_Igngrp.next();
            	//System.out.println("Checking for :"+ignore);
            	if(url_grp.contains(ignore_grp))
            	{
            		ignoreFlag_grp = true;
            	}
            }
            if(!ignoreFlag_grp){
            
            	driver.get(url_grp);
            	
            	System.out.println(url_grp+" ---------- "+driver.getTitle());
            	
            	/*if(driver.getPageSource().contains("error")){
            		System.err.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            	}*/
            
	}
}
            System.out.println("*************"+"Employess"+"**************");
//            WebElement Employees=driver.findElement(By.linkText("Employees"));
            //*[@id='container']/tbody/tr[1]/td[1]/table[1]/tbody/tr/td[7]/a
             WebElement Employees=driver.findElement(By.xpath("//*[@id='container']/tbody/tr[1]/td[1]/table[1]/tbody/tr/td[7]/a"));
             
//            WebElement Employees=driver.findElement(By.partialLinkText("Employees"));
            //Thread.sleep(15000);
            Employees.click();
          // Thread.sleep(15000);
            
            
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
            List<String> ignoreList = new ArrayList<String>();
            ignoreList.add("logout");
            ignoreList.add("actiance.com");
            
            Iterator itr_emp = urlList_emp.iterator();
            while(itr_emp.hasNext())
            {
            String url_emp = (String) itr_emp.next();
          
            Iterator<String> itr2_emp = ignoreList.iterator();
            boolean ignoreFlag_emp = false;
            while(itr2_emp.hasNext())
            {
            	String ignore1 = (String) itr2_emp.next();
            	//System.out.println("Checking for :"+ignore);
            	if(url_emp.contains(ignore1))
            	{
            		ignoreFlag_emp = true;
                        	}
            }
            
            if(!ignoreFlag_emp){
            	//Code to click and verify
           // System.out.println("Clicking :"+url);
            	driver.get(url_emp);
            	//Thread.sleep(3000);
            	System.out.println(url_emp+" ---------- "+driver.getTitle());
     
            	
            	if(driver.getPageSource().contains("error")){
            		System.err.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            	}
	}
}
            ////////////////Preferences////////////////
            System.out.println("*************"+"Preferences"+"**************");
            WebElement Preferences=driver.findElement(By.linkText("Preferences"));
            Preferences.click();
            
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
            	
            	
            	/*if(driver.getPageSource().contains("error")){
            		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            	}*/
	}
}
            //////////reports/////////////
            System.out.println("*************"+"Reports"+"**************");
            		
            WebElement Reports=driver.findElement(By.linkText("Reports"));
            Reports.click();
            
            List<WebElement> allLinks_reports = driver.findElements(By.tagName("a"));
            int total_reports = allLinks_reports.size();
            System.out.println(total_reports );
            List<String> urlList_reports= new ArrayList<String>();
            
            for (WebElement link_reports  : allLinks_reports ) {
           // System.out.println(link_reports .getText());
           // System.out.println(link_reports .getAttribute("href"));
            urlList_reports.add(link_reports .getAttribute("href"));
            	 
            System.out.println(link_reports .getText());
            
            }            
            List<String> ignoreList_rep = new ArrayList<String>();
            ignoreList_rep.add("logout");
            ignoreList_rep.add("actiance.com");
            
            Iterator itr_rep = urlList_reports.iterator();
            while(itr_rep.hasNext())
            {
            String url_rep = (String) itr_rep.next();

            Iterator<String> itr2_rep = ignoreList_rep.iterator();
            boolean ignoreFlag_rep = false;
            while(itr2_rep.hasNext())
            {
            	String ignore_rep = (String) itr2_rep.next();
            	if(url_rep.contains(ignore_rep))
            	{
            		ignoreFlag_rep = true;
            	}
            }
          
            if(!ignoreFlag_rep){

            	driver.get(url_rep);
            	System.out.println(url_rep+" ---------- "+driver.getTitle());
            	            	
            	/*if(driver.getPageSource().contains("error")){
            		System.err.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            	}*/
            }
            }
            
            ////////////////Policies///////////////
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
                      
                        	
                        	/*if(driver.getPageSource().contains("error")){
                        		System.err.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                        	}*/
                    }
                        }
                        
                       
                        System.out.println("Sysadmin session completed");
		}
			
	}



        
	
       
 
