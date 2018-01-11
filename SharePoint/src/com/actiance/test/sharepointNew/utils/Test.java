package com.actiance.test.sharepointNew.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.independentsoft.share.FileVersion;
import com.independentsoft.share.Service;
import com.independentsoft.share.ServiceException;

public class Test {

    public static void main(String[] args)
    {
    	try
    	{
    		Service service = new Service("https://actianceinc.sharepoint.com/DeeTestSite", "bbabin@blrresearch.com", "FaceTime@123");
	
            List<FileVersion> versions = service.getFileVersions("/Shared Documents/Test.docx");

            for (int i = 0; i < versions.size(); i++)
            {
            	System.out.println("Id: " + versions.get(i).getId());
                System.out.println("Label: " + versions.get(i).getLabel());
                System.out.println("Length: " + versions.get(i).getLength());
                System.out.println("CreatedTime: " + versions.get(i).getCreatedTime());
                System.out.println("IsCurrentVersion: " + versions.get(i).isCurrentVersion());
                System.out.println("Url: " + versions.get(i).getUrl());
            }

            //download single version
            FileVersion fileVersion = service.getFileVersion("/Shared Documents/Test.docx", 512);
            
            if(fileVersion != null)
            {
            	InputStream inputStream = null;
            	FileOutputStream outputStream = null;
            	
		        try
		        {		        	
		            inputStream = service.getInputStream(fileVersion.getUrl());
	
		            outputStream = new FileOutputStream("e:\\Test-Version1.docx");
		    	
	                byte[] buffer = new byte[8192];
	                int len = 0;
	
	                while ((len = inputStream.read(buffer, 0, buffer.length)) > 0)
	                {
	                    outputStream.write(buffer, 0, len);
	                }
		        }
		    	finally
		    	{   		
		    		if(inputStream != null)
		    		{
		    			inputStream.close();
		    		}
		    		
		    		if(outputStream != null)
		    		{
		    			outputStream.close();
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
        catch (IOException ex)
        {
        	System.out.println("Error: " + ex.getMessage());
        	
        	ex.printStackTrace();
        }
	}
}
                