package Sam.com.actiance.Teams;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDetails extends TestBase{
	
	
	private static final Statement VantageDBstmt = null;

	public Statement VantageDBConnectivity(String DBSERVERIP,String DBName,String sDBUser,String sDBPass) throws ClassNotFoundException, SQLException, InterruptedException  
	{
		Statement stmt = null;
	        String  VantagedbURL = null;
	        //Loading the required JDBC Driver class
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
			
			DBSERVERIP=getObject("dbserver");
			
			DBName= getObject("dbname");
			
			sDBUser=getObject("username");
			
			sDBPass=getObject("password");
			
			System.out.println(" dbsername: "+ DBSERVERIP +" dbname: "+DBName +" username: "+sDBUser +"Password: "+sDBPass);
			
			VantagedbURL = "jdbc:sqlserver://"+DBSERVERIP+";databaseName="+DBName+";user="+sDBUser+";password="+sDBPass;
			//Creating a connection to the database
			Connection conn = DriverManager.getConnection(VantagedbURL);
			
			//Executing SQL query and fetching the result
			Statement st = conn.createStatement();
			 Thread.sleep(30000);
			 
			return st;
	}
			
			public void teamsdataInsertIntable(Statement Sqlstamt,String type,String text, long senttime, long receivetime, 
			long timetaken, long delay, String status) throws SQLException{
			
				String query=null;
				ResultSet rs = null;
			
				
			query = "Insert into TeamsTest(type,content,sentTime,receivedTime,Foundtime,delay,status) values("+"'"+type+"'"+","+"'"+text+"'"+","+senttime+","+receivetime+","+timetaken+","+delay+","+"'"+status+"'"+")";
		
			System.out.println("query details"+ query );
			
			
				int i = Sqlstamt.executeUpdate(query);
			
				
				System.out.println("=============whats uppppppppppp+++++++++++============");
				
			}
			
			public String getContentfromTable(Statement Sqlstamt, String type) throws SQLException, InterruptedException{
				ResultSet rSet;
			  	String query="";
			  	String text = null;
			  
			  	query="Select Top 1 Content from TeamsTest where Status='Pending'and Type='"+type+"' order by Senttime desc";
			  	
			  	
			  	System.out.println("query details"+" : "+ query );
			  	
			  	List<String> list = new ArrayList<String>();
			  	
			  	 Thread.sleep(5000);
		  	        rSet = Sqlstamt.executeQuery(query);
		  	        while(rSet.next())
		  	        {
		  	      	text = rSet.getString("Content");
  	    	        list.add(text); 
  	    	       // System.out.println("text message for penging status " +text);
		  	        }
					return text;
					
			}
					
					public long getsenttimefromTable(Statement Sqlstamt, String type, String message) throws SQLException, InterruptedException{
						ResultSet rSet;
					  	String query="";
					  	long ltime=0;
					  	
					  
					  	query="Select Top 1 Senttime from TeamsTest where Content='"+message+"' order by Content desc";
					  	
					  	System.out.println("query details"+" : "+ query );
					 
				        
					  	 Thread.sleep(5000);
				  	        rSet = Sqlstamt.executeQuery(query);
				  	        rSet.next();
				  	       
				  	      	ltime = rSet.getLong(1);
		  	    	       
		  	    	       System.out.println("Previous text for senttime" +" : " +ltime);
				  	        
							return  ltime;
			  	
			}

					
					public String getStatusfromTable(Statement Sqlstamt, String type) throws SQLException, InterruptedException{
						ResultSet rSet;
					  	String query="";
					  	String lstatus=null;
					  	
					  
					  	//query="Select * from TeamsTest where Status='"+statusText+"' ";
					query= "select Top 1 Status from TeamsTest where Type='"+type+"'order by Senttime desc";	
					  	System.out.println("query details"+" : "+ query );
					 
				        
					  	 Thread.sleep(5000);
				  	        rSet = Sqlstamt.executeQuery(query);
				  	        rSet.next();
				  	       
				  	      	lstatus = rSet.getString(1);
		  	    	       
		  	    	       System.out.println("Db status " +lstatus);
				  	        
							return  lstatus;
			  	
			}

			
			public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException, IOException {
				TestBase.loadData();
				DatabaseDetails db = new DatabaseDetails();
				Statement stmt = db.VantageDBConnectivity(getObject("dbserver"), getObject("dbname"), getObject("username"),
						getObject("password"));
				
                 String type= "Nway";
				String values=db.getContentfromTable(stmt,type);
				System.out.println("pending status"+ " : "+values);
				
				Statement stmt1 = db.VantageDBConnectivity(getObject("dbserver"), getObject("dbname"), getObject("username"),
						getObject("password"));
				
                 
				long sentTime=db.getsenttimefromTable(stmt1, type, values);
				
				System.out.println("Previous senttime"+ " : "+sentTime);
					
			String dbStatus= db.getStatusfromTable(stmt1, type);
			System.out.println("Previous dbstatus"+ " : "+dbStatus);
			
				
				
			}
			
		}

       



