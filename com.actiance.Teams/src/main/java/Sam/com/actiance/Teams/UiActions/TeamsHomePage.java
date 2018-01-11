package Sam.com.actiance.Teams.UiActions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import Sam.com.actiance.Teams.TestBase;

//import Sam.com.actiance.Teams.TestBase;

public class TeamsHomePage extends TestBase{

WebDriver driver;

//Long t=System.currentTimeMillis();
	

    @FindBy(xpath=".//*[@id='app-bar-1']")
    WebElement activityRoom;

    @FindBy(xpath=".//*[@id='app-bar-2']")
    WebElement chatRoom;

	@FindBy(xpath=".//*[@id='app-bar-3']")
	WebElement teamsRoom;
	
	@FindBy(xpath=".//*[@id='app-bar-4']")
	WebElement meetingsRoom;
	
	@FindBy(xpath=".//*[@id='app-bar-7']")
	WebElement filesRoom;
	
	//click on Vantage Qa team
	//@FindBy(css=".truncate.header-text")
	//@FindBy(xpath="html/body/div[1]/div[2]/div[1]/div/left-rail/div/div/channel-list/div/div[1]/ul/li/ul/li[4]/div/h3/a")
	
	//Channel name: Vantage QATeam-channel-General
	@FindBy(xpath="//span[text()='General'][@class='truncate']/ancestor::a[contains(@data-tid,'team-Vantage QATeam-channel-General')]")
	WebElement groupName;
	
	//click on general
	//@FindBy(xpath="html/body/div[1]/div[2]/div[1]/div/left-rail/div/div/channel-list/div/div[1]/ul/li/ul/li[4]/div/div/ul/li[1]/a/span")
	
	//Vantage QA General channel
	@FindBy(xpath="//span[text()='General'][@class='truncate']/ancestor::a[contains(@data-tid,'team-Vantage QATeam-channel-General')]")
	
	WebElement genaralUnderGroup;
	
	@FindBy(xpath="html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[2]/form/div[4]/div[1]/div[1]/div[2]/div/div/div")
	WebElement chatTextAreaGroup;
	
	//Channel: VantageHackFest
	//@FindBy(xpath="//span[text()='General'][@class='truncate']/ancestor::a[contains(@data-tid,'team-VantageHackFest')]")
	//@FindBy(xpath="html/body/div[1]/div[2]/div[1]/div/left-rail/div/div/channel-list/div/div[1]/ul/li/ul/li[5]/div/h3/a/span")
	 @FindBy(partialLinkText="VantageHackFest")              
	WebElement hackfestgeneral;
	
	@FindBy(xpath="html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[2]/form/div[4]/div[1]/div[1]/div[2]/div/div/div")
	WebElement chatTexthackfest;
	
	//@FindBy(xpath="html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[2]/form/div[4]/div[1]/div[1]/div[2]/div/div/div")
	@FindBy(xpath="html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[3]/div[2]/div/button[1]")
	                 
	WebElement chatTexthackFest;
	
	
	

	public TeamsHomePage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickOnChatRoom() throws InterruptedException{
		chatRoom.click();
		Thread.sleep(20000);
	}
	
	public void clickOnTeamsRoom() throws InterruptedException{
		teamsRoom.click();
		Thread.sleep(20000);
	}
		
		//Click on group(vantage Qa team)
	public void clickOnGroupConversation() throws InterruptedException{
		groupName.click();
		Thread.sleep(10000);
		genaralUnderGroup.click();
		Thread.sleep(10000);
		
	}
	
	
	//VantageQaTeam
	public long enterOnChatTextGroup(String text) throws InterruptedException, IOException{
		loadData();
		Actions actions = new Actions(driver);
        actions.moveToElement(chatTextAreaGroup);
        actions.click();
            
        actions.sendKeys(text);
        System.out.println("Messages"+" : "+(getObject("chatInput")+t));
        long sentTime = System.currentTimeMillis();
        actions.sendKeys(Keys.ENTER);
        actions.build().perform();
        System.out.println("message entered");
        
        Thread.sleep(3000);
        return sentTime;
	}
	
	//Click on group(vantage Qa team)
public void clickOnGroupConversation1() throws InterruptedException{
	
	hackfestgeneral.click();
	Thread.sleep(10000);
	
	
}

public long enterSmileys(String text) throws InterruptedException, IOException{
	loadData();
	 driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	
    Actions actions = new Actions(driver);
    actions.moveToElement(chatTexthackFest);

    String baseUrl = "https://teams.microsoft.com/";
    for(int i=1;i<= 20;i++){
    	Thread.sleep(2000);
 //  driver.get(baseUrl + "/_#/conversations/19:5fd75181-7b91-4911-8b35-5fd26a45de72_b5879096-9b5d-4ddf-8bd6-65f2e00d791b@unq.gbl.spaces?ctx=chat");
    driver.findElement(By.xpath("html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[3]/div[2]/div/input-extension-emoji/button")).click();
    System.out.println("sadiq");
   Thread.sleep(2000);
    driver.findElement(By.xpath("html/body/div[4]/div/div/div/div[2]/div/div/div/ul/li["+i+"]")).click();
    Thread.sleep(2000);
    }
   driver.findElement(By.xpath("html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[3]/div[1]/button")).click();
   
  
   
    long sentTime = System.currentTimeMillis();
   
  
    Thread.sleep(2000);
    return sentTime;
    }

public long enterSmileys1() throws InterruptedException, IOException{
	loadData();
	 driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    Actions actions = new Actions(driver);
    actions.moveToElement(chatTexthackFest);

    String baseUrl = "https://teams.microsoft.com/";
    for(int i=2;i<= 2;i++){
    	Thread.sleep(2000);
 //  driver.get(baseUrl + "/_#/conversations/19:5fd75181-7b91-4911-8b35-5fd26a45de72_b5879096-9b5d-4ddf-8bd6-65f2e00d791b@unq.gbl.spaces?ctx=chat");
    driver.findElement(By.xpath("html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[3]/div[2]/div/input-extension-emoji/button")).click();
    System.out.println("sadiq");
   Thread.sleep(2000);
    driver.findElement(By.xpath("html/body/div[4]/div/div/div/div[2]/div/div/div/ul/li["+i+"]")).click();
    Thread.sleep(2000);
    }
   driver.findElement(By.xpath("html/body/div[1]/div[2]/div[2]/div/div/messages-header/div[2]/message-pane/div[2]/div[2]/new-message/div/div[3]/div[1]/button")).click();
  
    long sentTime = System.currentTimeMillis();
     
    Thread.sleep(2000);
    return sentTime;
    }


	//VantageHackFast
	
	public long enterOnChatTextGroup1(String text) throws InterruptedException, IOException{
		loadData();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		Actions actions = new Actions(driver);
        actions.moveToElement(chatTexthackFest);
        actions.click();
        
        
        actions.sendKeys(text);
       // System.out.println("Messages"+" : "+(getObject("chatInput")+t));
        long sentTime = System.currentTimeMillis();
        actions.sendKeys(Keys.ENTER);
        actions.build().perform();
        System.out.println("message entered");
        
        Thread.sleep(3000);
        return sentTime;
	}
}
