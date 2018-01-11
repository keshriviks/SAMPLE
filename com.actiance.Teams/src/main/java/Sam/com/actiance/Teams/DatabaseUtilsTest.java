package Sam.com.actiance.Teams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseUtilsTest extends TestBase {
	//static String interaction ;
		String message;
		 static Connection con = null;  
	     static Statement stmt = null;  
	     static ResultSet rs = null;
	     static int z= 0;
	     static int w =0;
	     static int l =0 ;
	     static boolean b=false;
	     static boolean f=false;
	     static ArrayList<String> a= new ArrayList<String>();
	     static ArrayList<String> g= new ArrayList<String>();

	
	public Statement VantageDBConnectivity(String DBSERVERIP,String DBName,String sDBUser,String sDBPass) throws IOException
	   {
	  
	   	Statement VantageDBstmt=null;
	   	   Connection VantagedbConn;
	   	   String  VantagedbURL = null;
	   	   String  driver = null;
	   	   System.out.println("dbtype is "+OR.getProperty("dbType"));
	   	   
	       try
	       {
	       if (OR.getProperty("dbType").equalsIgnoreCase("Oracle")){
	       driver= "oracle.jdbc.driver.OracleDriver"; // Start JDBC  
	       //VantagedbURL="jdbc:oracle:thin:@"+DBSERVERIP+":1521:"+DBName+";user="+sDBUser+";password="+sDBPass;
	       VantagedbURL="jdbc:oracle:thin:nidhi1_ci_2014r2/facetime@192.168.117.169:1521:autodb"; 
		   System.out.println("vantage db url now is "+VantagedbURL);

	       }
	       else if
	    	    (OR.getProperty("dbType").equalsIgnoreCase("sql"))
	       {
	       driver= "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // Start JDBC
	       VantagedbURL="jdbc:sqlserver://"+DBSERVERIP+";databaseName="+DBName+";user="+sDBUser+";password="+sDBPass;
	      
	       
	       }
	        
	       Class.forName(driver);
	       VantagedbConn = DriverManager.getConnection(VantagedbURL);
	       VantageDBstmt = VantagedbConn.createStatement(); 
	       
	       if(VantagedbConn!=null){
	        	 System.out.println("connection success");
	         }
	         else
	         {
	         System.out.println("connection failed");
	         }	
	       
	       Thread.sleep(30000);
	       }
	       catch(Exception e)
	       {
	           e.printStackTrace();
	       }
	       finally
	       {
	           return VantageDBstmt;
	       }
	   }
	
	
	 public int VerifyVantageDB(Statement stamt,String sMatch)
	   {
	       boolean bRet=false;
	       int interID=0;
	       ResultSet rSet;
	    //   String query="select count(*) from Interactions where  interID in(select interID from Messages  where text like '%"+sMatch+"%' or attributes like '%"+sMatch+"%')";
	       String query="select interId from Interactions where text like '%"+sMatch+"%' )";
	       
	       try
	  	    {
	           //logger.info("interID query-->"+query);
	           rSet = stamt.executeQuery(query);
	           rSet.next();
	           interID = rSet.getInt(1); 
	           System.out.println("InterId for "+sMatch+" is "+interID);
	           
	  	         
	   	       
	   	    }
	   	    catch(Exception e)
	   	    {
	   	        e.printStackTrace();
	   	    }
	   	    finally
	   	    {
	   	        return interID;
	   	    }
	   	
	   	 
	   }
	           
	       
	       
	   
	       
	       public  boolean result(Statement stamt, String sender,String content,ArrayList<String> reciever) throws SQLException{
				System.out.println(sender);
				System.out.println(content);
				System.out.println(reciever);
				//interaction="Select * from interactions";
		         for(String result:a){
		        	 
		        	 System.out.println("IN THE ARRAY LIST"+result);
		     rs= stmt.executeQuery("select interid from Messages where text ="+"'"+sender+"'"+" and buddyName="+result+ "and auditSentTime="+content+"" );
		         
		         }
		    // rs= stmt.executeQuery("Select xyz from Interactions where interid =  ");
		    
		     
		     while(rs.next()){
		    	System.out.println(rs.getInt(1) );
		    	z= rs.getInt(1);
		     }
		     
		     System.out.println("value of inetrid:"+z);
		     
		     rs = stmt.executeQuery("select count(*) from interactions where interid ="+z+"");
		     
		     
		     
		     while(rs.next()){
			    	System.out.println(rs.getInt(1) );
			    	w= rs.getInt(1);
			    	
			    	
		     }
		     rs = stmt.executeQuery("Select buddyNAME from Participants where interID="+z+"");
		     while(rs.next()){
			    	//System.out.println(rs.getInt(1) );
			    	g.add(rs.getString(1));
		     }
		     System.out.println("buddyname are for inteID"+z+""+g);
		     if (w > 0 && g.contains(reciever))
		     {
		    	 System.out.println("pass");
		    	 b = true;
		    	 System.out.println(b);
			    	
		    	 
		     }else
		     {
		    	 System.out.println(b);
		     }
		     
		  
		  
		      return b;
	       }    

	}
	
