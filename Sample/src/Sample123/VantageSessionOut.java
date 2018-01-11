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

class VantageSessionOut implements Runnable  {
   private Thread t;
   private String threadName;
   WebDriver driver;
   
    int n=5000;
    VantageSessionOut( String name) {
      threadName = name;
      System.out.println("Creating " +  threadName );
      
   }
   
   
   public void run(){
                   
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
                        //click on configuration link
                        WebElement Configuration=driver.findElement(By.linkText("Configuration"));
                        Configuration.click();
                        try {
                                                                Thread.sleep(3000);
                                                } catch (InterruptedException e1) {
                                                                // TODO Auto-generated catch block
                                                                e1.printStackTrace();
                                                }
                     // click on policy          
                        WebElement policy=driver.findElement(By.linkText("Policies"));
                        policy.click();
                        try {
							Thread.sleep(2000);
						} catch (InterruptedException e14) {
							// TODO Auto-generated catch block
							e14.printStackTrace();
						}

                        String actualpolicy = driver.getTitle();
                        String expectedpolicy = "Vantage - Policies";
                        if (actualpolicy.contains(expectedpolicy))
                        {
                        //System.out.println("Pass the flow for Policy");
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
							Thread.sleep(2000);
						} catch (InterruptedException e13) {
							// TODO Auto-generated catch block
							e13.printStackTrace();
						}

                        //verifying the groups page
                                String actualgroups = driver.getTitle();
                               String expectedgroups = "Vantage - Groups";
                                if (actualgroups.contains(expectedgroups))
                                {
//                                                System.out.println("Pass the flow for groups");
                                }
                                else{
                                                System.out.println("Fail the test flow for groups");
                                }
                                
                        // click on default group
                        WebElement defaultgroup=driver.findElement(By.linkText("Default Group"));
                        defaultgroup.click(); 
                        try {
							Thread.sleep(2000);
						} catch (InterruptedException e13) {
							// TODO Auto-generated catch block
							e13.printStackTrace();
						}

                        //verifying the page 
                        String actualdefaultgroup= driver.getTitle();
                        String expecteddefaultgroup = "User and Group Defaults";
                        if (actualdefaultgroup.contains(expecteddefaultgroup))
                        {
                        // System.out.println("Pass the flow for defaultgroup=");
                        }
                        else{
                         System.out.println("Fail the test flow for defaultgroup");
                        }
                        //click on Employees Tab

                        WebElement Employees=driver.findElement(By.linkText("Employees"));
                        Employees.click();
                        try {
							Thread.sleep(2000);
						} catch (InterruptedException e12) {
							// TODO Auto-generated catch block
							e12.printStackTrace();
						}

                        //verifying the configuration page
                                String actualEmployees = driver.getTitle();
                                String expectedEmployees = "Vantage - Employees";

                        if (actualEmployees.contains(expectedEmployees))
                                {
//                                                System.out.println("Pass the flow for Employees");
                                }
                                else{
                                                System.out.println("Fail the test flow for Employees");
                                }

                        // click on Administrator link
                        WebElement Administrator=driver.findElement(By.linkText("Administrator"));
                        Administrator.click();
                        try {
							Thread.sleep(2000);
						} catch (InterruptedException e11) {
							// TODO Auto-generated catch block
							e11.printStackTrace();
						}

                        //verifying the configuration page
                                String actualAdministrator= driver.getTitle();
                                String expectedAdministrator = "Add/Edit Employee";

                        if (actualAdministrator.contains(expectedAdministrator))
                                {
//                                                System.out.println("Pass the flow for Administrator");
                                }
                                else{
                                                System.out.println("Fail the test flow for Administrator");
                                }

                        //click on Reports link
                        WebElement Reports=driver.findElement(By.linkText("Reports"));
                        Reports.click();
                        try {
							Thread.sleep(2000);
						} catch (InterruptedException e10) {
							// TODO Auto-generated catch block
							e10.printStackTrace();
						}

                        //verifying the Reports page
                             String actualReports= driver.getTitle();
                             String expectedReports = "Vantage - Reports";

                        if (actualReports.contains(expectedReports))
                             {
//                                             System.out.println("Pass the flow for Reports");
                             }
                             else{
                                             System.out.println("Fail the test flow for Reports");
                             }

                        WebElement currentonline=driver.findElement(By.linkText("IM: Currently Online"));
                        currentonline.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e9) {
							// TODO Auto-generated catch block
							e9.printStackTrace();
						}
                        WebElement Reports1=driver.findElement(By.linkText("Reports"));
                        Reports1.click();
                        WebElement currentUsage=driver.findElement(By.linkText("IM: Current Usage"));
                        currentUsage.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e8) {
							// TODO Auto-generated catch block
							e8.printStackTrace();
						}
                        WebElement Reports2=driver.findElement(By.linkText("Reports"));
                        Reports2.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e7) {
							// TODO Auto-generated catch block
							e7.printStackTrace();
						}
                        WebElement currentActiceChat=driver.findElement(By.linkText("Chat: Currently Active Chat Rooms"));
                        currentActiceChat.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e6) {
							// TODO Auto-generated catch block
							e6.printStackTrace();
						}
                        WebElement Reports3=driver.findElement(By.linkText("Reports"));
                        Reports3.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e5) {
							// TODO Auto-generated catch block
							e5.printStackTrace();
						}
                        WebElement InteractionHistory=driver.findElement(By.linkText("Interactions Export History"));
                        InteractionHistory.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e4) {
							// TODO Auto-generated catch block
							e4.printStackTrace();
						}
                        WebElement Reports4=driver.findElement(By.linkText("Reports"));
                        Reports4.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}
                        WebElement AuditReport=driver.findElement(By.linkText("Audit Report"));
                        AuditReport.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
                        WebElement Reports5=driver.findElement(By.linkText("Reports"));
                        Reports5.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                        WebElement email=driver.findElement(By.linkText("Email: Top 10 Employees (Past Week)"));
                        email.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}

                        //click on configuration link                      
                        WebElement Configuration_1=driver.findElement(By.linkText("Configuration"));
                        Configuration_1.click();
                        try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        
                              /*  String actualconfiguration = driver.getTitle();
//                                System.out.println("Actual Configuration"+":"+actualconfiguration);

                                String expectedconfiguration = "Vantage - Configuration";

                        if (actualconfiguration.contains(expectedconfiguration))
                                {
//                                                System.out.println("Pass the flow for configuration");
                                }
                                else{
                                                System.out.println("Fail the test flow for configuration");
                                }*/

                        WebElement deployment=driver.findElement(By.linkText("Deployment"));
                        deployment.click();
                        try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                       
                        WebElement Configuration1=driver.findElement(By.linkText("Configuration"));
                        Configuration1.click();
                        try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement clusterconfiguration=driver.findElement(By.linkText("Cluster Configuration"));
                        clusterconfiguration.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration2=driver.findElement(By.linkText("Configuration"));
                        Configuration2.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Enterprise=driver.findElement(By.linkText("Enterprise IM Servers"));
                        Enterprise.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration3=driver.findElement(By.linkText("Configuration"));
                        Configuration3.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement CustomRules=driver.findElement(By.linkText("Custom Rules"));
                        CustomRules.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration4=driver.findElement(By.linkText("Configuration"));
                        Configuration4.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement SpIMBlocking=driver.findElement(By.linkText("SpIM Blocking"));
                        SpIMBlocking.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration5=driver.findElement(By.linkText("Configuration"));
                        Configuration5.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement content=driver.findElement(By.linkText("Content Challenge Settings"));
                        content.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration6=driver.findElement(By.linkText("Configuration"));
                        Configuration6.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement interactions=driver.findElement(By.linkText("Interaction Exporters"));
                        interactions.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration7=driver.findElement(By.linkText("Configuration"));
                        Configuration7.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement ScheduledGroup=driver.findElement(By.linkText("Scheduled Group Owner Checks"));
                        ScheduledGroup.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration8=driver.findElement(By.linkText("Configuration"));
                        Configuration8.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement FileTransfer=driver.findElement(By.linkText("Default File Transfer Settings"));
                        FileTransfer.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration9=driver.findElement(By.linkText("Configuration"));
                        Configuration9.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Preferences=driver.findElement(By.linkText("Preferences"));
                        Preferences.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration10=driver.findElement(By.linkText("Configuration"));
                        Configuration10.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Dashboard=driver.findElement(By.linkText("Dashboard"));
                        Dashboard.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        WebElement Configuration11=driver.findElement(By.linkText("Configuration"));
                        Configuration11.click();
                        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        System.out.println("Flow : " + i + " is done on : "+ new Timestamp(System.currentTimeMillis()));
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
	   VantageSessionOut R1 = new VantageSessionOut( "Thread-1");
                      R1.start();
                           
                   }   
                }  


