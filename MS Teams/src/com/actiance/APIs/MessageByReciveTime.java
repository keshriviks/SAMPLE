package com.actiance.APIs;

import java.util.Calendar;
import java.util.Date;

import com.independentsoft.exchange.IsGreaterThanOrEqualTo;
import com.independentsoft.exchange.Message;
import com.independentsoft.exchange.MessagePropertyPath;
import com.independentsoft.exchange.FindFolderResponse;
import com.independentsoft.exchange.FindItemResponse;
import com.independentsoft.exchange.Service;
import com.independentsoft.exchange.ServiceException;
import com.independentsoft.exchange.StandardFolder;


//////Find messages by receive time//////////
//MessageByReciveTime

public class MessageByReciveTime {
	
	

    public static void main(String[] args) throws InterruptedException
    {
        try
        {
            Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in", "FaceTime@123");

            Calendar localCalendar = Calendar.getInstance();
            localCalendar.add(Calendar.MINUTE, -25);

            Date time = localCalendar.getTime();
            
            String msgToValidate = "123zxc";

            IsGreaterThanOrEqualTo restriction = new IsGreaterThanOrEqualTo(MessagePropertyPath.RECEIVED_TIME, time);
            FindFolderResponse findFolderResponse1 = service.findFolder(StandardFolder.CONVERSATION_HISTORY);
            FindItemResponse response = service.findItem(findFolderResponse1.getFolders().get(0).getFolderId(), MessagePropertyPath.getAllPropertyPaths(), restriction);
System.out.println(response.getItems().size());
boolean found = false;

long messageSentTime = System.currentTimeMillis();
long searchEndTime = 0;

System.out.println("Search started : "+messageSentTime);
int k =0;
while(!found){
	
	System.out.println(k++);
            for (int i = 0; i < response.getItems().size(); i++)
            {
                if (response.getItems().get(i) instanceof Message)
                {
                    Message message = (Message) response.getItems().get(i);

                   // System.out.println("Subject = " + message.getSubject());
                    //System.out.println("ReceivedTime = " + message.getReceivedTime());

                    if (message.getFrom() != null)
                    {
                        System.out.println("From = " + message.getFrom().getName());
                    }
     String fromMB =  message.getBodyPlainText();
                    System.out.println("Body Preview = " +fromMB);
                    if(fromMB.equals(msgToValidate))
                    {
                    	
                    	searchEndTime = System.currentTimeMillis();
                    	found = true;
                    	
                    }
                    System.out.println("----------------------------------------------------------------");
                }
            }
  Thread.sleep(5000);          
}
System.out.println("Match Found at started : "+searchEndTime);

System.out.println("Time taken :" + (searchEndTime - messageSentTime));

        }
        catch (ServiceException e)
        {
            System.out.println(e.getMessage());
            System.out.println(e.getXmlMessage());

            e.printStackTrace();
        }
    }
}
                
