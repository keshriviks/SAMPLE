package Sam.com.actiance.Teams;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseUtils1 {
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
      
		// TODO Auto-generated method stub
		public  boolean result(String sender,String content,ArrayList<String> reciever) throws SQLException{
			System.out.println(sender);
			System.out.println(content);
			System.out.println(reciever);
		  String connectionUrl = "jdbc:sqlserver://192.168.114.18:1433;" +  
			         "databaseName=Priya_automation3;user=sa;password=FaceTime@123";  

			      // Declare the JDBC objects.  
			      
			      try {
					con = DriverManager.getConnection(connectionUrl);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			      stmt=con.createStatement();
			         if(con!=null){
			        	 System.out.println("connection success");
			         }
			         else
			         {
			         System.out.println("connection failed");
			         }	
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
			        
	
public static void main(String[] args) throws ClassNotFoundException, SQLException {
	 
	DatabaseUtils1 h= new DatabaseUtils1();
	Message msg = new Message();
	msg.setMessage("Vantage automation reply172880293182784");
	msg.setSender("1504940209015");
	a.add(0," 39");
	//a.add(1," 40");
	
	msg.setReceiver(a);
	
	h.result(msg.getMessage(),msg.getSender(),msg.getReceiver());
	
}
}