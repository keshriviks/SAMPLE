package com.actiance.test.vantage;

import java.io.IOException;
import java.util.Random;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
//import Sam.com.actiance.Teams.TestBase;

import Sam.com.actiance.Teams.TestBase;

public class ChatRoomPage extends TestBase {
	
	WebDriver driver;
	
	
	
	
	//One to one conversation
	@FindBy(xpath="//span[text()='Favorites']/ancestor::li[contains(@class,'ts-chats-header')]//div[@class='ts-tree-group']")
	//@FindBy(xpath="//a[@data-tid='chat-list-entry-with-Saurabh Vantage']")
	WebElement singlechat;
	
	//Nway Conversation
	//@FindBy(xpath="//a[@data-tid='chat-list-entry-with-Saurabh Vantage and varun K']")
	//@FindBy(xpath="//span[text()='Saurabh and varun'][@class='truncate truncate-name type-ahead-hint']")
	@FindBy(xpath="//span[text()='Saurabh and varun'][@class='truncate truncate-name type-ahead-hint']/ancestor::div[2]")
	WebElement nWaychat;
	
	//.//*[@id='wrapper']/div[1]/div/left-rail/div/div/chat-list/div/ul/li[2]/ul/li[3]/div/a
	@FindBy(xpath="//span[text()='Test Automation'][@class='truncate truncate-name type-ahead-hint']/ancestor::div[2]")
	WebElement Nwaychat_1stPlace;
	
	@FindBy(xpath=".//*[@id='wrapper']/div[1]/div/left-rail/div/div/chat-list/div/ul/li[1]/div[2]/div/ul/li/div/a")
	 WebElement chatTextAreaOne2One;
	
	//@FindBy(xpath=".//*[@id='wrapper']/div[1]/div/left-rail/div/div/chat-list/div/ul/li[2]/ul/li[4]/div/a")
	
	@FindBy(xpath=".//*[@id='cke_2_contents']/div")
	//@FindBy(xpath=".//*[@id='ts-bottom-compose-identifier']/div[2]/form/div[3]")
	 WebElement chatTextAreaNway;
	
	
	
	public ChatRoomPage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void clickOne2One() throws InterruptedException{
		singlechat.click();
		Thread.sleep(15000);
	}
	
	
	
	public long enterOnChatTextOne2One(String text) throws InterruptedException, IOException{
		loadData();

        Actions actions = new Actions(driver);
        actions.moveToElement(chatTextAreaOne2One);
        actions.click();
           
        actions.sendKeys(text);
        System.out.println("Messages sent :"+text);
        	 
        System.out.println("Messages"+" : "+(getObject("chatInput")+t));
        long sentTime = System.currentTimeMillis();
        actions.sendKeys(Keys.ENTER);
        actions.build().perform();
        
        Thread.sleep(3000);
        return sentTime;
        }
        //System.out.println("no of messages"+ i);
	
	
	public void enterOnChatTextOne2OneExample() throws InterruptedException, IOException{
		loadData();
		String input= (getObject("chatInput"));
		
		
       // actions.sendKeys(OR.getProperty("chatInput"+t));
        
        
        for(int i=1; i<=3; i++){
        	Long l=System.currentTimeMillis();
        	Actions actions = new Actions(driver);
            actions.moveToElement(chatTextAreaOne2One);
            actions.click();
            //Random randomGenerator = new Random();
           // int k = randomGenerator.nextInt(100);
        	actions.sendKeys(input+l);
        	System.out.println("random number"+ input+l);
        	
        
        
        System.out.println("Messages"+" : "+(getObject("chatInput")+l));
        actions.sendKeys(Keys.ENTER);
        actions.build().perform();
        Thread.sleep(10000);
        }
        //System.out.println("no of messages"+ i);
	}
	
	
	public void NwayConversation() throws InterruptedException{
		Nwaychat_1stPlace.click();
		//nWaychat.click();
		Thread.sleep(10000);
		System.out.println("Nway chat room");
		Thread.sleep(10000); 
		
	}
	
	public long enterOnChatTextNway(String text) throws InterruptedException, IOException{
		loadData();
		
		Actions actions = new Actions(driver);
        actions.moveToElement(chatTextAreaNway);
        actions.click();
        Thread.sleep(5000);
        actions.sendKeys(text);
        
        System.out.println("Messages"+" : "+(getObject("chatInput")+t));
        long sentTime = System.currentTimeMillis();
        actions.sendKeys(Keys.ENTER);
        actions.build().perform();
        
        System.out.println("message entered");
        
        Thread.sleep(3000);
        return sentTime;
        }
	
}
