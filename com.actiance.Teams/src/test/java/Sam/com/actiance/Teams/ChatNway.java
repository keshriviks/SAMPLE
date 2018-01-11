package Sam.com.actiance.Teams;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.client.config.RequestConfig;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import Sam.com.actiance.Teams.TestBase;
import Sam.com.actiance.Teams.UiActions.ChatRoomPage;
import Sam.com.actiance.Teams.UiActions.LoginPage;
import Sam.com.actiance.Teams.UiActions.LoginPageNEW;
import Sam.com.actiance.Teams.UiActions.TeamsHomePage;

//import com.actiance.APIs.TeamsConstants;
import com.independentsoft.exchange.FindFolderResponse;
import com.independentsoft.exchange.FindItemResponse;
import com.independentsoft.exchange.Identity;
import com.independentsoft.exchange.IsGreaterThanOrEqualTo;
import com.independentsoft.exchange.Mailbox;
import com.independentsoft.exchange.Message;
import com.independentsoft.exchange.MessagePropertyPath;
import com.independentsoft.exchange.RequestServerVersion;
import com.independentsoft.exchange.Service;
import com.independentsoft.exchange.ServiceException;
import com.independentsoft.exchange.StandardFolder;
import com.independentsoft.exchange.StandardFolderId;

/*import Sam.com.actiance.Teams.UiActions.ChatRoomPage;
import Sam.com.actiance.Teams.UiActions.LoginPage;
import Sam.com.actiance.Teams.UiActions.TeamsHomePage;*/

public class ChatNway extends TestBase {

	private static final long Timetaken = 0;
	private static final long actualReceiveTime = 0;
	LoginPage loginpage;
	LoginPageNEW loginNew;
	TeamsHomePage teamshomepage;
	ChatRoomPage chatroompage;
	stringSet File_input;

	Statement stmt = null;

	DatabaseDetails dbutils = new DatabaseDetails();

	@BeforeMethod

	public void setUp() throws IOException, InterruptedException {
		init();
	}

	
	@Test(invocationCount = 1,enabled=true)
    public void NwayChat_hyperlink() throws InterruptedException, IOException, Throwable{
    	//init();
   	 long actualReceiveTime = 0;
   	 long Timetaken=0;
   	 long delayTime=0;
   	 long foundTimeEWS=0;
   	 String Stat=null;
   	 
   	File_input = new stringSet();
	String text = File_input.getHyperlink()+t;
	System.out.println("Hyperlink"+text);
   	 String Type="Nway"; 
   	 //loginpage= new LoginPage(driver);
   	 loginNew = new LoginPageNEW(driver);
   	 teamshomepage =new TeamsHomePage(driver);
   	 chatroompage=new ChatRoomPage(driver);
   	 
     	 //loginpage.loginToApplication();
   	 loginNew.loginToApplication();
     teamshomepage.clickOnChatRoom();
   	 chatroompage.NwayConversation();
   	// long messageSentTime =chatroompage.enterOnChatTextNway(text);
   	long messageSentTime = chatroompage.enterOnChatTextNway(File_input.getHyperlink()+t);
   	 System.out.println("Sent Time "+messageSentTime);

   	 Thread.sleep(3000);
    	
   	 System.out.println("===============================================================");
   	 System.out.println("====================EWS Validation for Nway====================");
   	 System.out.println("===============================================================");
   	 
   
		try {
			Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
					"FaceTime@123");

			Calendar localCalendar = Calendar.getInstance();
			localCalendar.add(Calendar.MINUTE, -2);

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
						
					}
				}
				if (!found)
				Thread.sleep(5000);//wait if not found only
				
					Stat = "Pending";
				System.out.println("Status" + ":" + Stat);
				elapsedTime = System.currentTimeMillis() - startTime;

			}
				
						
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
    
	@Test(invocationCount = 1,enabled=false)
    public void NwayChat_utf() throws InterruptedException, IOException, Throwable{
    	//init();
   	 long actualReceiveTime = 0;
   	 long Timetaken=0;
   	 long delayTime=0;
   	 long foundTimeEWS=0;
   	 String Stat=null;
   	 
 	File_input = new stringSet();
	String text = File_input.getUTF8()+t;
	System.out.println("utf8"+" : "+text);
   	 String Type="Nway"; 
   	 //loginpage= new LoginPage(driver);
   	 loginNew = new LoginPageNEW(driver);
   	 teamshomepage =new TeamsHomePage(driver);
   	 chatroompage=new ChatRoomPage(driver);
   	 
     	 //loginpage.loginToApplication();
   	 loginNew.loginToApplication();
     teamshomepage.clickOnChatRoom();
   	 chatroompage.NwayConversation();
   	// long messageSentTime =chatroompage.enterOnChatTextNway(text);
   	long messageSentTime = chatroompage.enterOnChatTextNway(File_input.getUTF8()+t);
   	 System.out.println("Sent Time "+messageSentTime);

   	 Thread.sleep(3000);
    	
   	 System.out.println("===============================================================");
   	 System.out.println("====================EWS Validation for Nway====================");
   	 System.out.println("===============================================================");
   	 
   
		try {
			Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
					"FaceTime@123");

			Calendar localCalendar = Calendar.getInstance();
			localCalendar.add(Calendar.MINUTE, -2);

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
						
					}
				}
				if (!found)
				Thread.sleep(5000);//wait if not found only
				
					Stat = "Pending";
				System.out.println("Status" + ":" + Stat);
				elapsedTime = System.currentTimeMillis() - startTime;

			}
				
						
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
   
	@Test(invocationCount = 1,enabled=false)
    public void NwayChat_utf_numbers() throws InterruptedException, IOException, Throwable{
    	//init();
   	 long actualReceiveTime = 0;
   	 long Timetaken=0;
   	 long delayTime=0;
   	 long foundTimeEWS=0;
   	 String Stat=null;
   	 
   	File_input = new stringSet();
	String text = File_input.getNumber()+t;
	System.out.println("numbers"+" : "+text);
   	 String Type="Nway"; 
   	 //loginpage= new LoginPage(driver);
   	 loginNew = new LoginPageNEW(driver);
   	 teamshomepage =new TeamsHomePage(driver);
   	 chatroompage=new ChatRoomPage(driver);
   	 
     	 //loginpage.loginToApplication();
   	 loginNew.loginToApplication();
     teamshomepage.clickOnChatRoom();
   	 chatroompage.NwayConversation();
   	// long messageSentTime =chatroompage.enterOnChatTextNway(text);
   	long messageSentTime = chatroompage.enterOnChatTextNway(File_input.getNumber()+t);
   	 System.out.println("Sent Time "+messageSentTime);

   	 Thread.sleep(3000);
    	
   	 System.out.println("===============================================================");
   	 System.out.println("====================EWS Validation for Nway====================");
   	 System.out.println("===============================================================");
   	 
   
		try {
			Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
					"FaceTime@123");

			Calendar localCalendar = Calendar.getInstance();
			localCalendar.add(Calendar.MINUTE, -2);

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
						
					}
				}
				if (!found)
				Thread.sleep(5000);//wait if not found only
				
					Stat = "Pending";
				System.out.println("Status" + ":" + Stat);
				elapsedTime = System.currentTimeMillis() - startTime;

			}
				
						
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

	
	@Test(invocationCount = 1,enabled=false)
    public void NwayChat_Mixed() throws InterruptedException, IOException, Throwable{
    	//init();
   	 long actualReceiveTime = 0;
   	 long Timetaken=0;
   	 long delayTime=0;
   	 long foundTimeEWS=0;
   	 String Stat=null;
   	 
   	File_input = new stringSet();
	String text = File_input.getAllMixed()+t;
	System.out.println("Mixed"+" : "+text);
   	 String Type="Nway"; 
   	 //loginpage= new LoginPage(driver);
   	 loginNew = new LoginPageNEW(driver);
   	 teamshomepage =new TeamsHomePage(driver);
   	 chatroompage=new ChatRoomPage(driver);
   	 
     	 //loginpage.loginToApplication();
   	 loginNew.loginToApplication();
     teamshomepage.clickOnChatRoom();
   	 chatroompage.NwayConversation();
   	// long messageSentTime =chatroompage.enterOnChatTextNway(text);
   	long messageSentTime = chatroompage.enterOnChatTextNway(File_input.getAllMixed()+t);
   	 System.out.println("Sent Time "+messageSentTime);

   	 Thread.sleep(3000);
    	
   	 System.out.println("===============================================================");
   	 System.out.println("====================EWS Validation for Nway====================");
   	 System.out.println("===============================================================");
   	 
   
		try {
			Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
					"FaceTime@123");

			Calendar localCalendar = Calendar.getInstance();
			localCalendar.add(Calendar.MINUTE, -2);

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
						
					}
				}
				if (!found)
				Thread.sleep(5000);//wait if not found only
				
					Stat = "Pending";
				System.out.println("Status" + ":" + Stat);
				elapsedTime = System.currentTimeMillis() - startTime;

			}
				
						
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

	@Test(invocationCount = 1,enabled=false)
    public void NwayChat_XML() throws InterruptedException, IOException, Throwable{
    	//init();
   	 long actualReceiveTime = 0;
   	 long Timetaken=0;
   	 long delayTime=0;
   	 long foundTimeEWS=0;
   	 String Stat=null;
   	 
   	File_input = new stringSet();
	String text = File_input.getXML()+t;
	System.out.println("xml"+" : "+text);
   	 String Type="Nway"; 
   	 //loginpage= new LoginPage(driver);
   	 loginNew = new LoginPageNEW(driver);
   	 teamshomepage =new TeamsHomePage(driver);
   	 chatroompage=new ChatRoomPage(driver);
   	 
     	 //loginpage.loginToApplication();
   	 loginNew.loginToApplication();
     teamshomepage.clickOnChatRoom();
   	 chatroompage.NwayConversation();
   	// long messageSentTime =chatroompage.enterOnChatTextNway(text);
   	long messageSentTime = chatroompage.enterOnChatTextNway(File_input.getXML()+t);
   	 System.out.println("Sent Time "+messageSentTime);

   	 Thread.sleep(3000);
    	
   	 System.out.println("===============================================================");
   	 System.out.println("====================EWS Validation for Nway====================");
   	 System.out.println("===============================================================");
   	 
   
		try {
			Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
					"FaceTime@123");

			Calendar localCalendar = Calendar.getInstance();
			localCalendar.add(Calendar.MINUTE, -2);

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
						
					}
				}
				if (!found)
				Thread.sleep(5000);//wait if not found only
				
					Stat = "Pending";
				System.out.println("Status" + ":" + Stat);
				elapsedTime = System.currentTimeMillis() - startTime;

			}
				
						
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

	@Test(invocationCount = 1,enabled=false)
    public void NwayChat_Smileys() throws InterruptedException, IOException, Throwable{
    	//init();
   	 long actualReceiveTime = 0;
   	 long Timetaken=0;
   	 long delayTime=0;
   	 long foundTimeEWS=0;
   	 String Stat=null;
   	 
   	String text = getObject("chatInput") + t;
   	 String Type="Nway"; 
   	 //loginpage= new LoginPage(driver);
   	 loginNew = new LoginPageNEW(driver);
   	 teamshomepage =new TeamsHomePage(driver);
   	 chatroompage=new ChatRoomPage(driver);
   	 
     	 //loginpage.loginToApplication();
   	 loginNew.loginToApplication();
     teamshomepage.clickOnChatRoom();
   	 chatroompage.NwayConversation();
   	// long messageSentTime =chatroompage.enterOnChatTextNway(text);
   	 long messageSentTime = chatroompage.enterSmileys(text);
		System.out.println("Actual sent time =======" + messageSentTime);

   	 Thread.sleep(3000);
    	
   	 System.out.println("===============================================================");
   	 System.out.println("====================EWS Validation for Nway====================");
   	 System.out.println("===============================================================");
   	 
   
		try {
			Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
					"FaceTime@123");

			Calendar localCalendar = Calendar.getInstance();
			localCalendar.add(Calendar.MINUTE, -2);

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
						
					}
				}
				if (!found)
				Thread.sleep(5000);//wait if not found only
				
					Stat = "Pending";
				System.out.println("Status" + ":" + Stat);
				elapsedTime = System.currentTimeMillis() - startTime;

			}
				
						
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

	


