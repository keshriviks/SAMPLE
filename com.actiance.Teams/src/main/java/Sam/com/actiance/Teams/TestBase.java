package Sam.com.actiance.Teams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestBase {
	
	 private static final Statement VantageDBstmt = null;
	public WebDriver driver;
	 static Properties OR;
	 protected Long t=System.currentTimeMillis(); 
	 
	 ///span[text()='Favorites']/ancestor::li[contains(@class,'ts-chats-header')]//div[@class='ts-tree-group']
	 
	 public  static  void loadData() throws IOException  { 
		    OR = new Properties();
			File file = new File("C:\\Users\\VKeshri\\workspace\\com.actiance.Teams\\src\\main\\java\\config.properties");
			
			
			FileInputStream f = new FileInputStream(file);
			OR.load(f);
			//System.out.println("username ?"+" : "+OR.getProperty("userName"));
			//System.out.println("username ?11"+" : "+ getObject("userName"));
			//System.out.println("password ?"+" : "+OR.getProperty("passwrd"));
			//System.out.println("password ?11"+" : "+getObject("passwrd"));
			
	 }
	 
	 public  static String getObject(String Data){
		 String data = OR.getProperty(Data);
		 return data;
		 
	 }
	
	public void selectBrowser(String browser) {
		
			if (browser.equals("chrome")) {
				System.out.println(System.getProperty("user.dir"));
				System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
				driver = new ChromeDriver();
				
			} else if (browser.equals("firefox")) {
				
				System.setProperty("webdriver.gecko.driver", "D:\\Software\\firefox\\geckodriver.exe");
				driver = new FirefoxDriver();
				}
}
	public void init() throws IOException {
		loadData();
		//String log4jConfPath = "log4j.properties";
		//PropertyConfigurator.configure(log4jConfPath);
		System.out.println("browser name ?"+" : "+getObject("browser"));
		selectBrowser(getObject("browser"));
		System.out.println("url for apps ?"+" : "+getObject("url"));
		getUrl(getObject("url"));
		
	}
	
	public void getUrl(String url) {
		//log.info("navigating to :-" + url);
		driver.get(url);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(200, TimeUnit.SECONDS);
	    driver.manage().timeouts().pageLoadTimeout(150, TimeUnit.SECONDS);
	}
	
	       
       
       
       
       
       
       
	public void waitForElement(WebDriver driver, int timeOutInSeconds, WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(element));
	}
	
	
     
	
	
	public void closeBrowser(){
		driver.close();
	}
	
}