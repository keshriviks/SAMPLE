package Sample123;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Test1 {

	
	
  public static void main(String[] args) {
	

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
      
      for(int i=1;i<total_Policy; i++){
    	  System.out.println(allLinks_policy.get(i).getText());
      }
		
    
	}

}
