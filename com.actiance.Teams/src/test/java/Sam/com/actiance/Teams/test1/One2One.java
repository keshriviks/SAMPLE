package Sam.com.actiance.Teams.test1;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.Test;

import com.independentsoft.exchange.FindFolderResponse;
import com.independentsoft.exchange.FindItemResponse;
import com.independentsoft.exchange.IsGreaterThanOrEqualTo;
import com.independentsoft.exchange.Message;
import com.independentsoft.exchange.MessagePropertyPath;
import com.independentsoft.exchange.Service;
import com.independentsoft.exchange.ServiceException;
import com.independentsoft.exchange.StandardFolder;

import Sam.com.actiance.Teams.DatabaseDetails;

public class One2One extends GetdataFromPropertiesfile {
	
	DatabaseDetails dbutils = new DatabaseDetails();
	Statement stmt = null;
	

	@Test
	
	public void OneChatRoom() throws InterruptedException, IOException, ClassNotFoundException, SQLException {
		Long t=System.currentTimeMillis(); 
		long actualReceiveTime = 0;
		long Timetaken = 0;
		long delayTime = 0;
		long foundTimeEWS = 0;
		String Stat = null;
		String Type="One2One";
	
System.setProperty("webdriver.chrome.driver", "D:\\Software\\chrome\\chromedriver.exe");
        
        WebDriver driver = new ChromeDriver();
		//WebDriver driver = new FirefoxDriver();
		
		driver.get("https://teams.microsoft.com");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
	    driver.manage().timeouts().pageLoadTimeout(80, TimeUnit.SECONDS);
	    
	    Thread.sleep(10000);
	    
	    WebElement login  = driver.findElement(By.id("i0116"));
	    login.sendKeys("achandra@actiance.co.in");
	    driver.findElement(By.xpath(".//*[@id='idSIButton9']")).click();
	    
	    
	    driver.findElement(By.xpath(".//*[@id='i0118']")).sendKeys("FaceTime@123");
	    Thread.sleep(6000);
	    driver.findElement(By.id("idSIButton9")).click();
	    //.//*[@id='idSIButton9']
	    Thread.sleep(30000);
	    
	    driver.findElement(By.xpath(".//*[@id='app-bar-2']")).click();
	    Thread.sleep(20000);
	    driver.findElement(By.xpath("//span[text()='Favorites']/ancestor::li[contains(@class,'ts-chats-header')]//div[@class='ts-tree-group']")).click();
	    Thread.sleep(5000);
	    WebElement chatTextAreaOne2One=driver.findElement(By.xpath(".//*[@id='wrapper']/div[1]/div/left-rail/div/div/chat-list/div/ul/li[1]/div[2]/div/ul/li/div/a"));
	    Actions actions = new Actions(driver);
        actions.moveToElement(chatTextAreaOne2One);
        actions.click();
        
        String text = "Hi"+t;
        actions.sendKeys(text);
        System.out.println("Messages sent :"+text);
        	 
        System.out.println("Messages"+" : "+text);
        long messageSentTime = System.currentTimeMillis();
        actions.sendKeys(Keys.ENTER);
        actions.build().perform();
        
        Thread.sleep(3000);

		System.out.println("===================================================================");
		System.out.println("====================EWS Validation for one2one ====================");
		System.out.println("===================================================================");

		try {
			Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
					"FaceTime@123");
			

			Calendar localCalendar = Calendar.getInstance();
			localCalendar.add(Calendar.HOUR, -1);

			Date time = localCalendar.getTime();

			String msgToValidate = text;

			System.out.println("Message sent" + msgToValidate);

			IsGreaterThanOrEqualTo restriction = new IsGreaterThanOrEqualTo(MessagePropertyPath.RECEIVED_TIME, time);
			FindFolderResponse findFolderResponse1 = service.findFolder(StandardFolder.CONVERSATION_HISTORY);
			FindItemResponse response = service.findItem(findFolderResponse1.getFolders().get(0).getFolderId(),
					MessagePropertyPath.getAllPropertyPaths(), restriction);

			System.out.println(response.getItems().size());
			boolean found = false;
			long searchEndTime = 0;

			// long actualReceiveTime = 0;
			// long Timetaken=0;
			System.out.println("Search started : " + messageSentTime);
			int k = 0;

			long startTime = System.currentTimeMillis(); // fetch starting time
			long elapsedTime = 0;
			// while(!found){
			while (elapsedTime < 60000 && !found) {

				System.out.println(k++);
				for (int i = 0; i < response.getItems().size(); i++) {
					if (response.getItems().get(i) instanceof Message && !found) {
						Message message = (Message) response.getItems().get(i);

						System.out.println("Subject = " + message.getSubject());
						System.out.println("ReceivedTime = " + message.getReceivedTime());

						if (message.getFrom() != null) {
							System.out.println("From = " + message.getFrom().getName());

						}
						String fromMB = message.getBodyPlainText();
						System.out.println("Body Preview = " + fromMB);
						if (fromMB.equals(msgToValidate)) {

							actualReceiveTime = message.getReceivedTime().getTime();
							foundTimeEWS = System.currentTimeMillis();
							System.out.println("Found Time" + foundTimeEWS);
							System.out.println("ReceivedTime = " + actualReceiveTime);
							found = true;

						}
						System.out.println("----------------------------------------------------------------");
					}
					
					if (found) {
						Stat = "Comp";
						System.out.println("Status" + ":" + Stat);
						break;

					} else {
						Stat = "Pending";
						Thread.sleep(5000);//wait if not found only
					}
				}

				elapsedTime = System.currentTimeMillis() - startTime;

			}

			// }
			System.out.println("Match Found at started : " + searchEndTime);

			System.out.println("Time taken :" + (actualReceiveTime - messageSentTime));
			delayTime = (actualReceiveTime - messageSentTime);
			// delayTime= (actualReceiveTime - messageSentTime);
			System.out.println("Delay time" + delayTime);

		}

		catch (ServiceException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getXmlMessage());

			e.printStackTrace();
		}

		

		System.out.println("==================================================================");
		System.out.println("==================DB Insertion of data============================");
		System.out.println("==================================================================");

		stmt = dbutils.VantageDBConnectivity(getObject("dbserver"), getObject("dbname"), getObject("username"),
				getObject("password"));
		
		

		System.out.println("column name" + text + " :" + messageSentTime + " :" + Type + " :" + messageSentTime + " :"
				+ actualReceiveTime + " :" + foundTimeEWS + " :" + delayTime + " :" + Stat);

		dbutils.teamsdataInsertIntable(stmt, Type, text, messageSentTime, actualReceiveTime, foundTimeEWS, delayTime,
				Stat);

	}
}
