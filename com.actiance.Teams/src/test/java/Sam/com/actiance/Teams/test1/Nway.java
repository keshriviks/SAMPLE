package Sam.com.actiance.Teams.test1;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class Nway {
    public static void main(String[] args) throws InterruptedException {
    	
    	Long t=System.currentTimeMillis();
        WebDriver driver;
        System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
        
        driver = new ChromeDriver();
       // WebDriver driver= new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
        driver.get("https://teams.microsoft.com");
       // driver.findElement(By.xpath(".//*[@id='use_another_account']/tbody/tr/td/table/tbody/tr[2]/td[2]/div[1]")).click();
       // driver.findElement(By.xpath(".//*[@id='cred_userid_inputtext']")).sendKeys("achandra@actiance.co.in");
        driver.findElement(By.id("cred_userid_inputtext")).sendKeys("achandra@actiance.co.in");
        //driver.findElement(By.xpath(".//*[@id='cred_password_inputtext']")).sendKeys("FaceTime@123");
        driver.findElement(By.id("cred_password_inputtext")).sendKeys("FaceTime@123");
        Thread.sleep(20000);
        ////button[@id='cred_sign_in_button']
       // driver.findElement(By.id("cred_sign_in_button")).click();
        WebElement SignIn=driver.findElement(By.xpath("//button[@id='cred_sign_in_button']"));
        SignIn.isDisplayed();
        System.out.println("Check the signIn button is visible");
        SignIn.click();
        //driver.findElement(By.name("submit")).sendKeys(Keys.ENTER);
        
        
        //driver.findElement(By.xpath("//button[@id='cred_sign_in_button']")).click();
        Thread.sleep(40000);
        System.out.println("clicled on chat room helloooooo");
        
        
        ///teams
       // driver.findElement(By.xpath(".//*[@id='app-bar-3']"));
        
        //chat
        //driver.findElement(By.xpath(".//*[@id='app-bar-2']")).click();
        //html/body/div[1]/div[2]/div[1]/app-bar/nav/ul/li[2]/button
        driver.findElement(By.xpath("//button[@id='app-bar-2']")).click();
        //driver.findElement(By.xpath("//html/body/div[1]/div[2]/div[1]/app-bar/nav/ul/li[2]/button")).click();
        //driver.findElement(By.id("app-bar-2")).click();
        System.out.println("clicked on chat room");
        
        //chat to saurabh
        ////button[@id='app-bar-2']
       // driver.findElement(By.xpath("html/body/div[1]/div[2]/div[1]/div/left-rail/div/div/div[2]/chat-list/div/ul/li[1]/div[2]/div/ul/li/div/a/div[2]/div/span[1]")).click();
        driver.findElement(By.xpath("html/body/div[1]/div[2]/div[1]/div/left-rail/div/div/div[2]/chat-list/div/ul/li[2]/ul/li[3]/div/a")).click();
        System.out.println("Chat with varun and saurabh");
        //driver.findElement(By.xpath(".//*[@id='cke_2_contents']/div/div")).sendKeys("Hello");
        //driver.findElement(By.xpath(".//*[@id='ts-bottom-compose-identifier']/div[2]/form/div[3]")).sendKeys("Hello");
        WebElement ChatTextArea1=driver.findElement(By.xpath("html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[2]/form/div[3]/div[1]/div[1]/div[2]/div/div/div"));
        ChatTextArea1.isDisplayed();
        //.//*[@id='cke_2_contents']/div
        System.out.println(ChatTextArea1.getTagName());
        System.out.println(ChatTextArea1.getLocation());
        System.out.println("Chat conversation is visible");
        
        Actions actions = new Actions(driver);
        actions.moveToElement(ChatTextArea1);
        actions.click();
        actions.sendKeys("hii"+t);
        actions.sendKeys(Keys.ENTER);
        actions.build().perform();
        
      
       
        
        
        
    }
}
