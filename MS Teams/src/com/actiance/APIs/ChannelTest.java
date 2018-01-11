package com.actiance.APIs;



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
//import Sam.com.actiance.Teams.TestBase;
//import Sam.com.actiance.Teams.UiActions.ChatRoomPage;
//import Sam.com.actiance.Teams.UiActions.LoginPage;t
//import Sam.com.actiance.Teams.UiActions.TeamsHomePage;

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

public class ChannelTest extends TestBase {

	private static final long Timetaken = 0;
	private static final long actualReceiveTime = 0;
	LoginPage loginpage;
	TeamsHomePage teamshomepage;
	ChatRoomPage chatroompage;

	Statement stmt = null;

	DatabaseDetails dbutils = new DatabaseDetails();

	@BeforeMethod

	public void setUp() throws IOException, InterruptedException {
		init();
	}

	@Test(priority = 1, enabled = false)
	public void OneChatRoom() throws InterruptedException, IOException, ClassNotFoundException, SQLException {
		long actualReceiveTime = 0;
		long Timetaken = 0;
		long delayTime = 0;
		long foundTimeEWS = 0;
		String Stat = null;

		String text = getObject("chatInput") + t;
		String Type = "One2One";
		loginpage = new LoginPage(driver);
		teamshomepage = new TeamsHomePage(driver);
		chatroompage = new ChatRoomPage(driver);

		loginpage.loginToApplication();
		teamshomepage.clickOnChatRoom();
		chatroompage.clickOne2One();
		long messageSentTime = chatroompage.enterOnChatTextOne2One(text);

		System.out.println("Actual sent time =======" + messageSentTime);

		Thread.sleep(3000);

		System.out.println("===================================================================");
		System.out.println("====================EWS Validation for one2one ====================");
		System.out.println("===================================================================");

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
						Thread.sleep(5000);//wait if not found only
					}
				}

				
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

	 @Test(priority=2,enabled=false)
     public void NwayChat() throws InterruptedException, IOException, Throwable{
    	 
    	 long actualReceiveTime = 0;
    	 long Timetaken=0;
    	 long delayTime=0;
    	 long foundTimeEWS=0;
    	 String Stat=null;
    	 
    	 String text = getObject("chatInput")+t;
    	 String Type="Nway"; 
    	 loginpage= new LoginPage(driver);
    	 teamshomepage =new TeamsHomePage(driver);
    	 chatroompage=new ChatRoomPage(driver);
    	 
      	 loginpage.loginToApplication();
     	 teamshomepage.clickOnChatRoom();
    	 chatroompage.NwayConversation();
    	 long messageSentTime =chatroompage.enterOnChatTextNway(text);
    	 
    	 System.out.println("Sent Time "+messageSentTime);

    	 Thread.sleep(3000);
     	
    	 System.out.println("===============================================================");
    	 System.out.println("====================EWS Validation for Nway====================");
    	 System.out.println("===============================================================");
    	 
    
    	 try
         {
             Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in", "FaceTime@123");

             
            
             Calendar localCalendar = Calendar.getInstance();
             localCalendar.add(Calendar.MINUTE, -2);

             Date time = localCalendar.getTime();
             
             String msgToValidate = "text";
            		
             
             System.out.println("Message sent" + msgToValidate);

             IsGreaterThanOrEqualTo restriction = new IsGreaterThanOrEqualTo(MessagePropertyPath.RECEIVED_TIME, time);
             FindFolderResponse findFolderResponse1 = service.findFolder(StandardFolder.CONVERSATION_HISTORY);
             FindItemResponse response = service.findItem(findFolderResponse1.getFolders().get(0).getFolderId(), MessagePropertyPath.getAllPropertyPaths(), restriction);
 
             System.out.println(response.getItems().size());
             boolean found = false;
             long searchEndTime = 0;
             
             //long actualReceiveTime = 0;
             //long Timetaken=0;
             System.out.println("Search started : "+messageSentTime);
             int k =0;
             
             long startTime = System.currentTimeMillis(); //fetch starting time
             long elapsedTime = 0 ;
      		while(!found)
      		{
             while(elapsedTime < 60000){
 	              
 	         System.out.println(k++);
             for (int i = 0; i < response.getItems().size(); i++)
             {
                 if (response.getItems().get(i) instanceof Message)
                 {
                     Message message = (Message) response.getItems().get(i);

                     System.out.println("Subject = " + message.getSubject());
                     System.out.println("ReceivedTime = " + message.getReceivedTime());

                     if (message.getFrom() != null)
                     {
                         System.out.println("From = " + message.getFrom().getName());
                         
                     }
      String fromMB =  message.getBodyPlainText();
                     System.out.println("Body Preview = " +fromMB);
                     if(fromMB.equals(msgToValidate))
                     {
                    	 
                    	actualReceiveTime = message.getReceivedTime().getTime();
                    	foundTimeEWS = System.currentTimeMillis();
                    	 System.out.println("ReceivedTime = " +actualReceiveTime);
                    	 found = true;
                    	 
                    	  
                     	
                     }
                     System.out.println("----------------------------------------------------------------");
                 }
             }
   Thread.sleep(5000); 
   elapsedTime = System.currentTimeMillis() - startTime;
   
 }
             if (found){
            	 Stat= "Comp";
            	 System.out.println("Found"+ Stat);
             }
             else{
            	Stat="Pending";
            	System.out.println("Not found"+Stat);
             
      		}
             break;
             
      		}
 System.out.println("Match Found at started : "+searchEndTime);
 

 


 System.out.println("Time taken :" + (actualReceiveTime - messageSentTime));
// delayTime= (actualReceiveTime - messageSentTime);
 System.out.println("Delay time"+ delayTime);
         }
         catch (ServiceException e)
         {
             System.out.println(e.getMessage());
             System.out.println(e.getXmlMessage());

             e.printStackTrace();
         }
    	 
    	delayTime= (actualReceiveTime - messageSentTime);
    	 System.out.println("==================================================================");
    	 System.out.println("==================DB Insertion of data============================");
    	 System.out.println("==================================================================");
    	 
    	stmt= dbutils.VantageDBConnectivity(getObject("dbserver"), getObject("dbname"),
    			 getObject("username"), getObject("password"));
    	
    	
    	
    	
    
    	
System.out.println("column name"+text +" :" +messageSentTime+" :" + Type+" :" + messageSentTime+" :" + actualReceiveTime+" :" + foundTimeEWS+" :" + delayTime+" :" + Stat);
		
		
		dbutils.teamsdataInsertIntable(stmt, Type, text, messageSentTime, actualReceiveTime, foundTimeEWS, delayTime, Stat);
		
    	  }
    	 

	@Test(priority = 3, enabled = true)
	public void ChannelConversation() throws InterruptedException, IOException {
		String text = getObject("chatInput") + t;
		loginpage = new LoginPage(driver);
		teamshomepage = new TeamsHomePage(driver);

		loginpage.loginToApplication();
		teamshomepage.clickOnGroupConversation();
		long messageSentTime = teamshomepage.enterOnChatTextGroup(text);

		Thread.sleep(3000);

		System.out.println("===================================================================");
		System.out.println("====================EWS Validation for Channel ====================");
		System.out.println("===================================================================");

		try {
			
			
			Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in",
					"FaceTime@123");
			
			Identity id = new Identity();
 			id.setPrincipalName("eff988dd.actiance.co.in@amer.teams.ms");
 			System.out.println("#################################### "+id.getPrincipalName());
 			service.setRequestServerVersion(RequestServerVersion.EXCHANGE_2013);                 
 			service.setExchangeImpersonation(id);
 			
 			RequestConfig requestConfig = RequestConfig.copy(service.getRequestConfig())
 					.setConnectionRequestTimeout(600*1000) //default timeout is 60 seconds. Increase it here 
 					.setSocketTimeout(600*1000) //default timeout is 60 seconds. Increase it here
 					.setConnectionRequestTimeout(600*1000) //default timeout is 60 seconds. Increase it here
 					.build();
 			service.setRequestConfig(requestConfig);
			
			Calendar localCalendar = Calendar.getInstance();
			localCalendar.add(Calendar.MINUTE, -2);

			Date time = localCalendar.getTime();

			String msgToValidate = text;

			System.out.println("Message sent" + msgToValidate);
			
			Mailbox teamMailbox = new Mailbox("eff988dd.actiance.co.in@amer.teams.ms");
			IsGreaterThanOrEqualTo restriction = new IsGreaterThanOrEqualTo(MessagePropertyPath.RECEIVED_TIME, time);
			StandardFolderId convHistFolderId = new StandardFolderId(StandardFolder.CONVERSATION_HISTORY, teamMailbox);
			FindFolderResponse findFolderResponse1 = service.findFolder(convHistFolderId);
			FindItemResponse response = service.findItem(findFolderResponse1.getFolders().get(0).getFolderId(),
		    MessagePropertyPath.getAllPropertyPaths(), restriction);
			System.out.println(response.getItems().size());
			
			///Mailbox teamMailbox = new Mailbox(teamsGroup.getMail());
			//StandardFolderId convHistFolderId = new StandardFolderId(StandardFolder.CONVERSATION_HISTORY, teamMailbox);
			//FindFolderResponse findFolderResponseTeam = _service.findFolder(convHistFolderId);
			//fetchItems(findFolderResponseTeam, TeamsConstants.ROOM_TYPE_GROUP, _service, groupMembers);
			
			boolean found = false;

			long searchEndTime = 0;
			long actualReceiveTime = 0;

			System.out.println("Search started : " + messageSentTime);
			int k = 0;
			while (!found) {

				System.out.println(k++);
				for (int i = 0; i < response.getItems().size(); i++) {
					if (response.getItems().get(i) instanceof Message) {
						Message message = (Message) response.getItems().get(i);

						// System.out.println("Subject = " +
						// message.getSubject());
						// System.out.println("ReceivedTime = " +
						// message.getReceivedTime());

						if (message.getFrom() != null) {
							System.out.println("From = " + message.getFrom().getName());

						}
						String fromMB = message.getBodyPlainText();
						System.out.println("Body Preview = " + fromMB);
						if (fromMB.equals(msgToValidate)) {

							actualReceiveTime = message.getReceivedTime().getTime();
							System.out.println("ReceivedTime = " + actualReceiveTime);
							/*
							 * DateTimeFormatter formatter =
							 * DateTimeFormatter.ofPattern (
							 * "EEE MMM d HH:mm:ss zzz yyyy" , Locale.ENGLISH );
							 * long epoch = new
							 * java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
							 * .parse("01/01/1970 01:00:00").getTime() / 1000;
							 * searchEndTime = System.currentTimeMillis();
							 */
							found = true;

						}
						System.out.println("----------------------------------------------------------------");
					}
				}
				Thread.sleep(5000);
			}
			System.out.println("Match Found at started : " + searchEndTime);

			System.out.println("Time taken :" + (actualReceiveTime - messageSentTime));

		} catch (ServiceException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getXmlMessage());

			e.printStackTrace();
		}

	}

	/*
	 * @AfterMethod
	 * 
	 * public void logoutApplication() throws InterruptedException{ loginpage=
	 * new LoginPage(driver); loginpage.logoutApps(); closeBrowser(); }
	 */

}
