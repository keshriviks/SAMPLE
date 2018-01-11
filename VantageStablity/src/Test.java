import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.asserts.Assertion;


	
	public class Test extends Thread{

	public static void main(String[] args) throws InterruptedException {
		
		
		WebDriver driver= new FirefoxDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://192.168.119.129:8443/ima/login.do");
		
		WebElement username=driver.findElement(By.xpath(".//input[@name='username']"));
		username.sendKeys("sysadmin");
		WebElement password=driver.findElement(By.xpath(".//input[@name='password']"));
		password.sendKeys("facetime");
		WebElement login=driver.findElement(By.id("footerRight"));
		login.click();
		String actualTitle = driver.getTitle();
		System.out.println("Title page"+" : "+actualTitle);
		String expectedTitle = "Vantage - Dashboard";
		
		Assert.assertEquals(actualTitle, expectedTitle);
		
		WebElement group_click=driver.findElement(By.linkText("Groups"));
		group_click.click();
		String actualgroup = driver.getTitle();
		System.out.println("Title page"+" : "+actualgroup);
		String expectedgroup = "Vantage - Groups";
		if (actualgroup.contains(expectedgroup))
		{
			System.out.println("Continue performing click operation");
		}
		else{
			System.out.println("Fail the test flow");
		}
		
		Assert.assertEquals(actualgroup, expectedgroup);
		Thread.sleep(2000);
		WebElement dashboard_click=driver.findElement(By.linkText("Dashboard"));
		dashboard_click.click();
		String actualdashboard = driver.getTitle();
		System.out.println("Title page"+" : "+actualTitle);
		String expecteddashboard = "Vantage - Dashboard";
		
		if (actualdashboard.contains(expecteddashboard))
		{
			System.out.println("Continue performing click operation");
		}
		else{
			System.out.println("Fail the test flow");
		}
		
		Assert.assertEquals(actualdashboard, expecteddashboard);
		

		
	    }
	
}
		



