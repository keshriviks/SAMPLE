package com.actiance.test.sharepointNew.tests;

import java.util.ArrayList;
import java.io.UnsupportedEncodingException;

import com.independentsoft.share.File;
import com.independentsoft.share.Service;
import com.independentsoft.share.ServiceException;

import com.independentsoft.share.FieldValue;
import com.independentsoft.share.File;
import com.independentsoft.share.List;
import com.independentsoft.share.ListItem;
import com.independentsoft.share.Service;
import com.independentsoft.share.ServiceException;
import com.independentsoft.share.Site;

public class TestTemp {

    public static void main(String[] args)
    {
    	try
    	{
    		Service service = new Service("https://actianceengg.sharepoint.com/VantageQA/EntWiki", "achandra@ActianceEngg.onmicrosoft.com", "FaceTime@123");
    		//Site s = service.getSite();
    		//System.out.println("Site Title:" + s.getTitle());
    		
    		//Service service = new Service("https://actianceengg.sharepoint.com/Vantage QA", "achandra@ActianceEngg.onmicrosoft.com", "FaceTime@123");
    		//Service service = new Service("http://qasp13web/VJ14092015NEWQUEEN/", "customer", "FaceTime@123");
 //   		ArrayList <List> lists =  (ArrayList<List>) service.getLists();
 //  		ArrayList Temp = (ArrayList<List>) service.getFieldByTitle(title)();
//vijjubs adding new data
//    		    		
//	         String text = "vijjubs says Simple text file content";
//            byte[] buffer;
//			try {
//				System.out.println("Inside try block after buffer ");
//				buffer = text.getBytes("UTF-8");
//				File file = service.createFile("Shared Documents/doc4.txt", buffer);
//				
//				System.out.println("ServerRelativeUrl: " + file.getServerRelativeUrl());
//		         System.out.println("UniqueId: " + file.getUniqueId());
//				
//				
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

            
            
	         
    		
///This is the end vijjubs added the test data    		
    		
  /*  		
           for (int i = 0; i < lists.size(); i++)
            {
            	System.out.println("Name: " + lists.get(i).getTitle());                    
            	System.out.println("Itemcount: " + (lists.get(i)).getItemCount());
            	System.out.println("Itemtype full name : " + lists.get(i).getListItemEntityTypeFullName());
            	System.out.println("LastModifiedTime: " + lists.get(i).getLastItemModifiedDate());
            	System.out.println("Sub list items are " + lists.subList(0, lists.size()));
            	System.out.println("Item id is  " +  lists.get(i).getId());
            	System.out.println("---------------------------------------------");
            }
    	*/	

/*           List <File> files2 = service.getFiles("/Shared Documents");

            for (int i = 0; i < files2.size(); i++)
            {
            	System.out.println("Name: " + files2.get(i).getName());                    
            	System.out.println("Path: " + files2.get(i).getServerRelativeUrl());
            	System.out.println("Length: " + files2.get(i).getLength());
            	System.out.println("LastModifiedTime: " + files2.get(i).getLastModifiedTime());
            	System.out.println("---------------------------------------------");
            }
*/            
    		ArrayList<List> lists1 = (ArrayList<List>) service.getLists();
    	       System.out.println(lists1.size());
    	       for(List li : lists1)
    	       {
    	        if(li.getTitle().equals("Pages")){
    	         System.out.println("Id is: "+li.getId());
    	         System.out.println("Title is: "+li.getTitle());
    	         ArrayList<ListItem> items = (ArrayList<ListItem>) service.getListItems(li.getId());
//    	         System.out.println("EntityTypeName is: "+li.getEntityTypeName());
//    	         System.out.println("DocumentTemplateUrl is: "+li.getDocumentTemplateUrl());
//    	         System.out.println("ItemCount is: "+li.getItemCount());
//    	         System.out.println("ListItemEntityTypeFullName is: "+li.getListItemEntityTypeFullName());
//    	         System.out.println("ParentWebUrl is: "+li.getParentWebUrl());
//    	         System.out.println("BaseTemplate is: "+li.getBaseTemplate());
//    	         System.out.println("BaseType is: "+li.getBaseType());
//    	         System.out.println("Direction is: "+li.getDirection());
//    	         System.out.println("DraftVersionVisibility is: "+li.getDraftVersionVisibility());
//    	         System.out.println("Description is: "+li.getDescription());
//    	        // System.out.println("Description is: "+li.get());
//    	         System.out.println("-----------------------------");
//    	        
    	         for(ListItem item : items){
    	        	 if(item.getTitle().equals("new page pra")){
    	        		 ArrayList<FieldValue> fieldValues = (ArrayList<FieldValue>) service.getFieldValues("7bac5f0e-141b-444e-9ebc-89a8b7234468", item.getId());
            	         System.out.println("fieldValues.size"+fieldValues.size());
            	         for(FieldValue fi : fieldValues){
            	          System.out.println("-----------------------------------");
            	          System.out.println("Name is: "+ fi.getName());
            	          System.out.println("Value is: "+ fi.getValue());
            	          System.out.println("-----------------------------------");
            	         }
    	        	 }
    	        	 
    	         }
    	         
    	        }
    			}
    		}
   
        catch (ServiceException ex)
        {
        	System.out.println("Error: " + ex.getMessage());
        	System.out.println("Error: " + ex.getErrorCode());
        	System.out.println("Error: " + ex.getErrorString());
        	System.out.println("Error: " + ex.getRequestUrl());

        	ex.printStackTrace();
        }
	}
}



	

