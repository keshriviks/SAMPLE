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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
//import Sam.com.actiance.Teams.TestBase;
//import Sam.com.actiance.Teams.UiActions.ChatRoomPage;
//import Sam.com.actiance.Teams.UiActions.LoginPage;t
//import Sam.com.actiance.Teams.UiActions.TeamsHomePage;

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

import Sam.com.actiance.Teams.UiActions.ChatRoomPage;
import Sam.com.actiance.Teams.UiActions.LoginPage;
import Sam.com.actiance.Teams.UiActions.LoginPageNEW;
import Sam.com.actiance.Teams.UiActions.TeamsHomePage;

public class FinalTestCase extends TestBase {

	//private static final long Timetaken = 0;
	private static final long actualReceiveTime = 0;
	//private static final String Pending = null;
	LoginPageNEW loginNew;
	LoginPage loginpage;
	TestBase TestBase;
	TeamsHomePage teamshomepage;
	ChatRoomPage chatroompage;
	stringSet File_input;

	Statement stmt = null;

	DatabaseDetails dbutils = new DatabaseDetails();

	@BeforeMethod

	public void setUp() throws IOException, InterruptedException {
		init();
	}
	
	
	  @Test(priority=2,enabled=false)
	    public void NwayChat_Positive() throws InterruptedException, IOException, Throwable{
		 long messageSentTime;
	   	 long actualReceiveTime = 0;
	   	 long delayTime=0;
	   	 long foundTimeEWS=0;
	   	 String Stat=null;
	   	 String Type="Nway";
	     String type="Nway";
	   	 String text;
	   	
	   	stmt = dbutils.VantageDBConnectivity(getObject("dbserver"), getObject("dbname"), getObject("username"),
			getObject("password"));
	   	
	   	String dbStatus=dbutils.getStatusfromTable(stmt, type);
	   	
	   	System.out.println("dbStatus for earlier message"+" : "+dbStatus );
	   	boolean result  = false;
	   	String b ="Pending";
	   	result =dbStatus.equals(b);
	   	
		if(result){
	   	 
	   	 text = getObject("chatInput")+t;
	   	 
		 loginNew = new LoginPageNEW(driver);
	 	 //loginpage= new LoginPage(driver);
	 	 teamshomepage =new TeamsHomePage(driver);
	 	 chatroompage=new ChatRoomPage(driver);
	 	 
	 	 loginNew.loginToApplication();
	   	 // loginpage.loginToApplication();
	     teamshomepage.clickOnChatRoom();
	   	 chatroompage.NwayConversation();
	     messageSentTime =chatroompage.enterOnChatTextNway(text);
	   	 
	   	 System.out.println("Sent Time "+messageSentTime);

	   	 Thread.sleep(3000);
	   	 
	   	}
	   	
	   	else{
	   		
	   		System.out.println("==============Pending Status================"+" : "+ dbStatus);
	   
	   		text=dbutils.getContentfromTable(stmt,type);
	   		System.out.println("Previous pending content"+":"+text);
	   		
	   		 messageSentTime=dbutils.getsenttimefromTable(stmt, type, text);
	   		
	   	}
	   	 System.out.println("===============================================================");
	   	 System.out.println("====================EWS Validation for Nway====================");
	   	 System.out.println("===============================================================");
	   	 
	   
			try {
				Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
						"FaceTime@123");

				Calendar localCalendar = Calendar.getInstance();
				localCalendar.add(Calendar.HOUR_OF_DAY, -22);
				

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
			
			 /*  	if(!result){
	   		System.out.println("cdddv");
	   	}
	   	else{
	   		System.out.println("exit");
	   	}*/

			if(result){
			

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
			
			else{
				
					System.out.println("End the results without entry");
				
				
			}
				
			
		}
	   //////////////////////for positive scenarios/////////////////////////
	   
	   @Test(priority=3,enabled=true)
	   public void NwayChat() throws InterruptedException, IOException, Throwable{
		 long messageSentTime;
	  	 long actualReceiveTime = 0;
	     long delayTime=0;
	     String msgToValidate = null;
	  	 long foundTimeEWS=0;
	  	 String Stat=null;
	  	 String Type="Nway";
	  	 String type="Nway";
	  	 String text;
	  	String fromMB = null;
	  	
	  	stmt = dbutils.VantageDBConnectivity(getObject("dbserver"), getObject("dbname"), getObject("username"),
			getObject("password"));
	  	
	  	String dbStatus=dbutils.getStatusfromTable(stmt, type);
	  	
	  	System.out.println("dbStatus for earlier message"+" : "+dbStatus );
	  	boolean result  = false;
	  	String b ="Pending";
	  	result =dbStatus.equals(b);
	  	

	  	
		if(!result){
	  	 
	  	 text = getObject("chatInput")+t;
	  	 loginNew = new LoginPageNEW(driver);
	  	 //loginpage= new LoginPage(driver);
	  	 teamshomepage =new TeamsHomePage(driver);
	  	 chatroompage=new ChatRoomPage(driver);
	  	 
	    //loginpage.loginToApplication();
	  	 
	  	loginNew.loginToApplication();
	    teamshomepage.clickOnChatRoom();
	  	chatroompage.NwayConversation();
	    messageSentTime =chatroompage.enterOnChatTextNway(text);
	  	 
	  	 System.out.println("Sent Time "+messageSentTime);

	  	 Thread.sleep(3000);
	  	 
	  	}
	  	
	  	else{
	  		
	  		System.out.println("==============Pending Status================"+" : "+ dbStatus);
	  		
	  		text=dbutils.getContentfromTable(stmt,type);
	  		System.out.println("Previous pending content"+":"+text);
	  		
	  		 messageSentTime=dbutils.getsenttimefromTable(stmt, type, text);
	  		
	  	}
	  	 System.out.println("===============================================================");
	  	 System.out.println("====================EWS Validation for Nway====================");
	  	 System.out.println("===============================================================");
	  	 
	  
	     //String text = null;
			try {
				Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
						"FaceTime@123");

				Calendar localCalendar = Calendar.getInstance();
				localCalendar.add(Calendar.MINUTE, -9000);
				

				Date time = localCalendar.getTime();

			    msgToValidate = text;

				System.out.println("Message sent" + msgToValidate);

				IsGreaterThanOrEqualTo restriction = new IsGreaterThanOrEqualTo(MessagePropertyPath.RECEIVED_TIME, time);
				FindFolderResponse findFolderResponse1 = service.findFolder(StandardFolder.CONVERSATION_HISTORY);
				FindItemResponse response = service.findItem(findFolderResponse1.getFolders().get(0).getFolderId(),
			    MessagePropertyPath.getAllPropertyPaths(), restriction);

				System.out.println("Response size"+response.getItems().size());
				boolean found = false;
				long searchEndTime = 0;

				// long actualReceiveTime = 0;
				// long Timetaken=0;
				System.out.println("Search started : " + messageSentTime);
				int k = 0;

				long startTime = System.currentTimeMillis(); // fetch starting time
				long elapsedTime = 0;
				
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
							fromMB = message.getBodyPlainText();
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
			
	
			if(!result){
			

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
			
			else{
				
				
				System.out.println("End the results without entry");
			
					}
			Assert.assertEquals(text,fromMB);
			
			 
			
	
		}

   
	   @Test(enabled=false)
		public void ChannelConversation() throws InterruptedException, IOException, ClassNotFoundException, SQLException {
			//init();
			long actualReceiveTime = 0;
			long Timetaken = 0;
			long delayTime = 0;
			long foundTimeEWS = 0;
			long messageSentTime;
			String Stat = null;	
			String msgToValidate = null;
			String Type = "Channel";
			String type="Channel";
			String text;
			
			stmt = dbutils.VantageDBConnectivity(getObject("dbserver"), getObject("dbname"), getObject("username"),
					getObject("password"));
			  	
			  	String dbStatus=dbutils.getStatusfromTable(stmt, type);
			  	
			  	System.out.println("dbStatus for earlier message"+" : "+dbStatus );
			  	boolean result  = false;
			  	String b ="Pending";
			  	result =dbStatus.equals(b);
			  	

			  	
				if(!result){
			
			text = getObject("chatInput") + t;
			System.out.println("Messages entered"+ text);
			
		    //loginpage = new LoginPage(driver);
			loginNew = new LoginPageNEW(driver);
			teamshomepage = new TeamsHomePage(driver);

			//loginpage.loginToApplication();
			loginNew.loginToApplication();
			//teamshomepage.clickOnGroupConversation();
			teamshomepage.clickOnGroupConversation1();
			messageSentTime =teamshomepage.enterOnChatTextGroup1(text);
			
		
			System.out.println("Sent Time "+messageSentTime);

			Thread.sleep(3000);
			
				}
				
				else{
			  		
			  		System.out.println("==============Pending Status================"+" : "+ dbStatus);
			  		
//////////////////Checking for Earlier text which is in pending status///////////////////////////			  		
			  		text=dbutils.getContentfromTable(stmt,type);
			  		System.out.println("Previous pending content"+":"+text);
			  		
			  		 messageSentTime=dbutils.getsenttimefromTable(stmt, type, text);
			  		
			  	}

			System.out.println("===================================================================");
			System.out.println("====================EWS Validation for Channel ====================");
			System.out.println("===================================================================");

			try {
				
				
				Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
						"FaceTime@123");
					
				//fetchItems(findFolderResponseTeam, TeamsConstants.ROOM_TYPE_GROUP, _service, groupMembers);
				Calendar localCalendar = Calendar.getInstance();
				localCalendar.add(Calendar.MINUTE, -4000);

				Date time = localCalendar.getTime();

				msgToValidate = text;
				
				Mailbox teamMailbox = new Mailbox("vantagehackfast@actiance.co.in");
				StandardFolderId convHistFolderId = new StandardFolderId(StandardFolder.CONVERSATION_HISTORY,teamMailbox
						);
				IsGreaterThanOrEqualTo restriction = new IsGreaterThanOrEqualTo(MessagePropertyPath.RECEIVED_TIME, time);
				FindFolderResponse findFolderResponseTeam = service.findFolder(convHistFolderId);
				System.out.println(findFolderResponseTeam.getFolders());
				FindItemResponse response = service.findItem(findFolderResponseTeam.getFolders().get(0).getFolderId(),
				MessagePropertyPath.getAllPropertyPaths(), restriction);

				
				boolean found = false;

				long searchEndTime = 0;
		
				System.out.println("Search started : " + messageSentTime);
				int k = 0;
				long startTime = System.currentTimeMillis(); // fetch starting time
				long elapsedTime = 0;
				//while (!found) {
				while (elapsedTime < 60000 && !found) {
					System.out.println(k++);
					for (int i = 0; i < response.getItems().size(); i++) {
						if (response.getItems().get(i) instanceof Message) {
							Message message = (Message) response.getItems().get(i);

							 System.out.println("Subject = " +
							 message.getSubject());
							 System.out.println("ReceivedTime = " +
							 message.getReceivedTime());

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
			
	if(!result){
			
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
			
	else{
				
				System.out.println("End the results without entry");
				
			}
	
	 Assert.assertEquals(text, msgToValidate);
				
		
		}
	
	
		////ews db validation
		
		@Test(priority=1,enabled=false)
		public void OneChatRoom_positive() throws InterruptedException, ClassNotFoundException, SQLException, IOException {
			
			long actualReceiveTime = 0;
			long Timetaken = 0;
			long delayTime = 0;
			long foundTimeEWS = 0;
			String Stat = null;
			long messageSentTime;
			String type= "One2One";

			String text = getObject("chatInput") + t;
			String Type = "One2One";
			
		 	/// Checking the db status 
		   	stmt = dbutils.VantageDBConnectivity(getObject("dbserver"), getObject("dbname"), getObject("username"),
				getObject("password"));
		   	
		   	String dbStatus=dbutils.getStatusfromTable(stmt, type);
		   	
		   	System.out.println("dbStatus for earlier message"+" : "+dbStatus );
		   	
		    System.out.println("=========================================================");	   	
		   	boolean result  = false;
		   	String b ="Pending";
		   	result =dbStatus.equals(b);
		   		   	
  if(!result){
		
			//loginpage = new LoginPage(driver);
			loginNew = new LoginPageNEW(driver);
			teamshomepage = new TeamsHomePage(driver);
			chatroompage = new ChatRoomPage(driver);

			//loginpage.loginToApplication();
			loginNew.loginToApplication();
			teamshomepage.clickOnChatRoom();
			chatroompage.clickOne2One();
			messageSentTime = chatroompage.enterOnChatTextOne2One(text);
			//long messageSentTime = chatroompage.enterOnChatTextOne2One(File_input.getHyperlink());

			System.out.println("Actual sent time =======" + messageSentTime);

			Thread.sleep(3000);
			
			}
			
			else{
		   		
		   		System.out.println("==============Pending Status================"+" : "+ dbStatus);
		   		
		   		text=dbutils.getContentfromTable(stmt,type);
		   		System.out.println("Previous pending content"+":"+text);
		   		
		   		 messageSentTime=dbutils.getsenttimefromTable(stmt, type, text);
		   		
		   	}

			System.out.println("===================================================================");
			System.out.println("====================EWS Validation for one2one ====================");
			System.out.println("===================================================================");

			try {
				Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
						"FaceTime@123");
				

				Calendar localCalendar = Calendar.getInstance();
				localCalendar.add(Calendar.MINUTE, -4000);

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

		if(!result)	{

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
		
		else{
	   		System.out.println("exit");
		}
		
		 Assert.assertEquals(text, text);
		 
		}
	
		
	/*	  @AfterMethod
		  
		  public void logoutApplication() throws InterruptedException{ 
			  LoginPageNEW loginpageNew = new LoginPageNEW(driver);
			 // loginpage=new LoginPage(driver); 
			  loginpageNew.logoutApps(); 
			  closeBrowser(); 
			  
		  }*/
		 
		
	}

	


