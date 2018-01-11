package Sample123;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

public class sample123 {
	public static void main(String[] args) throws InterruptedException {
		
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
        Thread.sleep(2000);
        
        //verify the title page of apps
        String actualTitle = driver.getTitle();
        System.out.println("Title page"+" : "+actualTitle);
        String expectedTitle = "Vantage - Dashboard";
        
        Assert.assertEquals(actualTitle, expectedTitle);
        


// click on policy          
WebElement policy=driver.findElement(By.linkText("Policies"));
policy.click();
Thread.sleep(2000);

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
Thread.sleep(2000);

//verifying the groups page
        String actualgroups = driver.getTitle();
       String expectedgroups = "Vantage - Groups";
        if (actualgroups.contains(expectedgroups))
        {
//                        System.out.println("Pass the flow for groups");
        }
        else{
                        System.out.println("Fail the test flow for groups");
        }
        
// click on default group
WebElement defaultgroup=driver.findElement(By.linkText("Default Group"));
defaultgroup.click(); 

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
Thread.sleep(2000);

//verifying the configuration page
        String actualEmployees = driver.getTitle();
        String expectedEmployees = "Vantage - Employees";

if (actualEmployees.contains(expectedEmployees))
        {
//                        System.out.println("Pass the flow for Employees");
        }
        else{
                        System.out.println("Fail the test flow for Employees");
        }

// click on Administrator link
WebElement Administrator=driver.findElement(By.linkText("Administrator"));
Administrator.click();
Thread.sleep(2000);

//verifying the configuration page
        String actualAdministrator= driver.getTitle();
        String expectedAdministrator = "Add/Edit Employee";

if (actualAdministrator.contains(expectedAdministrator))
        {
//                        System.out.println("Pass the flow for Administrator");
        }
        else{
                        System.out.println("Fail the test flow for Administrator");
        }

//click on Reports link
WebElement Reports=driver.findElement(By.linkText("Reports"));
Reports.click();
Thread.sleep(2000);

//verifying the Reports page
     String actualReports= driver.getTitle();
     System.out.println("cool"+actualReports);
     String expectedReports = "Vantage - Reports";

if (actualReports.contains(expectedReports))
     {
//                     System.out.println("Pass the flow for Reports");
     }
     else{
                     System.out.println("Fail the test flow for Reports");
     }

WebElement currentonline=driver.findElement(By.linkText("IM: Currently Online"));
currentonline.click();
Thread.sleep(1000);
WebElement Reports1=driver.findElement(By.linkText("Reports"));
Reports1.click();
WebElement currentUsage=driver.findElement(By.linkText("IM: Current Usage"));
currentUsage.click();
Thread.sleep(1000);
WebElement Reports2=driver.findElement(By.linkText("Reports"));
Reports2.click();
Thread.sleep(1000);
WebElement currentActiceChat=driver.findElement(By.linkText("Chat: Currently Active Chat Rooms"));
currentActiceChat.click();
Thread.sleep(1000);
WebElement Reports3=driver.findElement(By.linkText("Reports"));
Reports3.click();
Thread.sleep(1000);
WebElement InteractionHistory=driver.findElement(By.linkText("Interactions Export History"));
InteractionHistory.click();
Thread.sleep(1000);
WebElement Reports4=driver.findElement(By.linkText("Reports"));
Reports4.click();
Thread.sleep(1000);
WebElement AuditReport=driver.findElement(By.linkText("Audit Report"));
AuditReport.click();
Thread.sleep(1000);
WebElement Reports5=driver.findElement(By.linkText("Reports"));
Reports5.click();
Thread.sleep(1000);
WebElement email=driver.findElement(By.linkText("Email: Top 10 Employees (Past Week)"));
email.click();

//click on configuration link                      
WebElement Configuration=driver.findElement(By.linkText("Configuration"));
Configuration.click();
Thread.sleep(2000);
//verifying the configuration page
        String actualconfiguration = driver.getTitle();
//        System.out.println("Actual Configuration"+":"+actualconfiguration);

        String expectedconfiguration = "Vantage - Configuration";

if (actualconfiguration.contains(expectedconfiguration))
        {
//                        System.out.println("Pass the flow for configuration");
        }
        else{
                        System.out.println("Fail the test flow for configuration");
        }

WebElement deployment=driver.findElement(By.linkText("Deployment"));
deployment.click();
Thread.sleep(1000);
WebElement Configuration1=driver.findElement(By.linkText("Configuration"));
Configuration1.click();
Thread.sleep(1000);
WebElement clusterconfiguration=driver.findElement(By.linkText("Cluster Configuration"));
clusterconfiguration.click();
Thread.sleep(1000);
WebElement Configuration2=driver.findElement(By.linkText("Configuration"));
Configuration2.click();
Thread.sleep(1000);
WebElement Enterprise=driver.findElement(By.linkText("Enterprise IM Servers"));
Enterprise.click();
Thread.sleep(1000);
WebElement Configuration3=driver.findElement(By.linkText("Configuration"));
Configuration3.click();
Thread.sleep(1000);
WebElement CustomRules=driver.findElement(By.linkText("Custom Rules"));
CustomRules.click();
Thread.sleep(1000);
WebElement Configuration4=driver.findElement(By.linkText("Configuration"));
Configuration4.click();
Thread.sleep(1000);
WebElement SpIMBlocking=driver.findElement(By.linkText("SpIM Blocking"));
SpIMBlocking.click();
Thread.sleep(1000);
WebElement Configuration5=driver.findElement(By.linkText("Configuration"));
Configuration5.click();
Thread.sleep(1000);
WebElement content=driver.findElement(By.linkText("Content Challenge Settings"));
content.click();
Thread.sleep(1000);
WebElement Configuration6=driver.findElement(By.linkText("Configuration"));
Configuration6.click();
Thread.sleep(1000);
WebElement interactions=driver.findElement(By.linkText("Interaction Exporters"));
interactions.click();
Thread.sleep(1000);
WebElement Configuration7=driver.findElement(By.linkText("Configuration"));
Configuration7.click();
Thread.sleep(1000);
WebElement ScheduledGroup=driver.findElement(By.linkText("Scheduled Group Owner Checks"));
ScheduledGroup.click();
Thread.sleep(1000);
WebElement Configuration8=driver.findElement(By.linkText("Configuration"));
Configuration8.click();
Thread.sleep(1000);
WebElement FileTransfer=driver.findElement(By.linkText("Default File Transfer Settings"));
FileTransfer.click();
Thread.sleep(1000);
WebElement Configuration9=driver.findElement(By.linkText("Configuration"));
Configuration9.click();
Thread.sleep(1000);
WebElement Preferences=driver.findElement(By.linkText("Preferences"));
Preferences.click();
Thread.sleep(1000);
WebElement Configuration10=driver.findElement(By.linkText("Configuration"));
Configuration10.click();
Thread.sleep(1000);
WebElement Dashboard=driver.findElement(By.linkText("Dashboard"));
Dashboard.click();
Thread.sleep(1000);
WebElement Configuration11=driver.findElement(By.linkText("Configuration"));
Configuration11.click();
Thread.sleep(1000);





 
       


}
}



	