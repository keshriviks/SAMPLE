package Sam.com.actiance.Teams.test1;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
//import org.testng.annotations.Test;
import org.testng.annotations.Test;

public class GroupConversation {
	
   @Test
	public void oneToone() throws InterruptedException{
		
    	Long t=System.currentTimeMillis();
        WebDriver driver;
        System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
        
        driver = new ChromeDriver();
       
        //System.setProperty("webdriver.gecko.driver", "D:\\Software\\firefox\\geckodriver.exe");
		//driver = new FirefoxDriver();
       
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
        driver.get("https://teams.microsoft.com");
      
        driver.findElement(By.id("cred_userid_inputtext")).sendKeys("achandra@actiance.co.in");
        
        driver.findElement(By.id("cred_password_inputtext")).sendKeys("FaceTime@123");
        Thread.sleep(20000);
       
       
        WebElement SignIn=driver.findElement(By.xpath("//button[@id='cred_sign_in_button']"));
        SignIn.isDisplayed();
        System.out.println("Check the signIn button is visible");
        SignIn.click();
     
        Thread.sleep(40000);
        System.out.println("clicked on chat room helloooooo");
        
   
     
       // driver.findElement(By.xpath(".//*[@id='app-bar-3']")).click();
        //driver.findElement(By.xpath("//a[@title='Vantage QATeam']")).click();
        driver.findElement(By.cssSelector(".truncate.header-text")).click();
       // driver.findElement(By.xpath("html/body/div[1]/div[2]/div[1]/div/left-rail/div/div/channel-list/div/div[1]/ul/li/ul/li[4]/div/h3/a")).click();
        //driver.findElement(By.xpath("//a[@title='Vantage QATeam' and @data-tid='team-Vantage QATeam']")).click();
        Thread.sleep(20000);
        
        //click on general
        driver.findElement(By.xpath("html/body/div[1]/div[2]/div[1]/div/left-rail/div/div/channel-list/div/div[1]/ul/li/ul/li[4]/div/div/ul/li[1]/a/span")).click();
        
        //driver.findElement(By.xpath("//button[@id='app-bar-2']")).click();
        //driver.findElement(By.xpath("//html/body/div[1]/div[2]/div[1]/app-bar/nav/ul/li[2]/button")).click();
        
        System.out.println("type in chat editor box");
               
        
        Thread.sleep(30000);
     
        
        //driver.findElement(By.xpath(".//*[@id='ts-bottom-compose-identifier']/div[2]/form/div[3]")).sendKeys("Hello");
      // WebElement ChatTextArea=driver.findElement(By.xpath("html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[2]/form/div[3]/div[1]/div[1]/div[2]/div/div/div/div"));
        WebElement ChatTextArea=driver.findElement(By.xpath("html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[2]/form/div[4]/div[1]/div[1]/div[2]/div/div/div"));
        
       //WebElement ChatTextArea=driver.findElement(By.xpath(".//*[@id='wrapper']/div[1]/div/left-rail/div/div/chat-list/div/ul/li[1]/div[2]/div/ul/li/div/a"));
       
       //.//*[@id='wrapper']/div[1]/div/left-rail/div/div/chat-list/div/ul/li[1]/div[2]/div/ul/li/div/a
        ChatTextArea.isDisplayed();
        System.out.println(ChatTextArea.getTagName());
        System.out.println(ChatTextArea.getLocation());
        System.out.println("Chat conversation is visible");
        
        Actions actions = new Actions(driver);
        actions.moveToElement(ChatTextArea);
        actions.click();
        actions.sendKeys("hii"+t);
        actions.sendKeys(Keys.ENTER);
        actions.build().perform();
        Thread.sleep(10000);
       
        //driver.findElement(By.xpath(".//*[@id='personDropdown']")).click();
        //driver.findElement(By.xpath(".//*[@id='logout-button']")).click();
        
        //driver.close();
        System.out.println("logout the application and close the browser");
        
        //////////////////////vantage db validation////////////////////
        
        
       
        
    }
	
}
