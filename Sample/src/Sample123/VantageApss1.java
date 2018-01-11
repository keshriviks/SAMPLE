package Sample123;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

class VantageApss1 implements Runnable {
   private Thread t;
   private String threadName;
   WebDriver driver;
   
    int n=5000;
    VantageApss1( String name) {
      threadName = name;
      System.out.println("Creating " +  threadName );
      
   }
   
   
   public void run() {
                   
                   File file = new File("D:/TestData/sessionTimeOut.txt");
                   try {
                                PrintStream pr = new PrintStream(file);
                                System.setOut(pr);
                } catch (FileNotFoundException e2) {
                                // TODO Auto-generated catch block
                                e2.printStackTrace();
                }
                   //Date d = new Date();
                   System.out.println("Cusror will Move from Configuration -> Deployment -> Configuration -> Cluster Configuration - >Configuration -> Deployment and so on......");
                   //SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
                   Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                    WebDriver driver= new FirefoxDriver();
                                                driver.manage().window().maximize();
                                                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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
                                                
                   
                   while(true){  
                                 
                                   for(int i=1; i<n; i++){
                                  System.out.println("**************************************************");
                        // click on policy          
                        WebElement policy=driver.findElement(By.linkText("Policies"));
                        policy.click();
                        try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        String actualpolicy = driver.getTitle();
                        String expectedpolicy = "Vantage - Policies";
                        if (actualpolicy.contains(expectedpolicy))
                        {
//                                        System.out.println("Pass the flow for Policy");
                        }
                        else{
                                        System.out.println("Fail the test flow for Policy");
                        }     
                        WebElement findclick=driver.findElement(By.xpath(".//input[@type='button'][@onclick='submitFind()']"));
                        findclick.click();
                
                         // click on Groups
                        WebElement groups=driver.findElement(By.linkText("Groups"));
                        groups.click();
                        try {
                                                                Thread.sleep(3000);
                                                } catch (InterruptedException e1) {
                                                                // TODO Auto-generated catch block
                                                                e1.printStackTrace();
                                                }
                        //verifying the configuration page
                                                String actualgroups = driver.getTitle();
                                               String expectedgroups = "Vantage - Groups";
                                                if (actualgroups.contains(expectedgroups))
                                                {
//                                                                System.out.println("Pass the flow for groups");
                                                }
                                                else{
                                                                System.out.println("Fail the test flow for groups");
                                                }
                                                
                         // click on default group
                         WebElement defaultgroup=driver.findElement(By.linkText("Default Group"));
                         defaultgroup.click(); 
                         
                         //verifying the page 
                         String actualdefaultgroup= driver.getTitle();
                         System.out.println(actualdefaultgroup);
                         String expecteddefaultgroup = "Vantage - Groups";
                         if (actualgroups.contains(expecteddefaultgroup))
                         {
//                                         System.out.println("Pass the flow for groups");
                         }
                         else{
                                         System.out.println("Fail the test flow for groups");
                         }
                         
                         
                         
                        
                       
                        
                        
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
//                                                System.out.println("Actual Configuration"+":"+actualconfiguration);
                                   
                                                String expectedconfiguration = "Vantage - Configuration";
                     
                     if (actualconfiguration.contains(expectedconfiguration))
                                                {
//                                                                System.out.println("Pass the flow for configuration");
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
//                                                System.out.println("Actual deployment"+":"+actualdeployment);
                                                String expecteddeployment = "Vantage - Deployment";
                     
                     if (actualdeployment.equals(expecteddeployment))
                                                {
//                                                                System.out.println("Pass the flow for deployment");
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
//                                                System.out.println("Actual Configuration1"+":"+actualconfiguration1);
                                   
                                                String expectedconfiguration1 = "Vantage - Configuration";
                     
                     if (actualconfiguration1.contains(expectedconfiguration1))
                                                {
//                                                                System.out.println("Pass the flow for configuration1");
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
//                                    System.out.println("Actual clusterconfiguration"+":"+actualcluster+" - "+new Timestamp(System.currentTimeMillis()));
                                                String expectedcluster = "Vantage - Cluster Configuration";
                                                
                     
                     if (actualcluster.contains(expectedcluster))
                                                {
//                                                                System.out.println("Pass the flow for Cluster Configuration");
                                                }
                                                else{
                                                                System.out.println("Fail the test flow for Cluster Configuration");
                                                }
                     System.out.println("Flow : " + i + " is done on : "+ new Timestamp(System.currentTimeMillis()));
                                
                                 
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
                   VantageApss1 R1 = new VantageApss1( "Thread-1");
                      R1.start();
                           
                   }   
                }  


