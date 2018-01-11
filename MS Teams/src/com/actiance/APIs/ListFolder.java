package com.actiance.APIs;

import com.independentsoft.exchange.FindFolderResponse;
import com.independentsoft.exchange.Service;
import com.independentsoft.exchange.ServiceException;
import com.independentsoft.exchange.StandardFolder;

public class ListFolder {

	
	public static void main(String[] args)
    {
        try
        {
        	 Service service = new Service("https://outlook.office365.com/EWS/Exchange.asmx", "achandra@actiance.co.in", "FaceTime@123");

            FindFolderResponse findFolderResponse1 = service.findFolder(StandardFolder.CONVERSATION_HISTORY);
           // FindFolderResponse findFolderResponse2 = service.findFolder(StandardFolder.PUBLIC_FOLDERS_ROOT);

            //List first level mailbox folders
            for (int i = 0; i < findFolderResponse1.getFolders().size(); i++)
            {
                System.out.println(findFolderResponse1.getFolders().get(i).getDisplayName());
                System.out.println(findFolderResponse1.getFolders().get(i).getFolderId());
            }

            System.out.println("-------------------------------------------------------------------");

            //List first level public folders
//            for (int i = 0; i < findFolderResponse2.getFolders().size(); i++)
//            {
//                System.out.println(findFolderResponse2.getFolders().get(i).getDisplayName());
//                System.out.println(findFolderResponse2.getFolders().get(i).getFolderId());
//            }
        }
        catch (ServiceException e)
        {
            System.out.println(e.getMessage());
            System.out.println(e.getXmlMessage());

            e.printStackTrace();
        }
    }

}
