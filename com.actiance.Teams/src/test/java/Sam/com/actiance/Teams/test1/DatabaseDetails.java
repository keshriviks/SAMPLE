package Sam.com.actiance.Teams.test1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseDetails extends GetdataFromPropertiesfile {
	
	
	private static final Statement VantageDBstmt = null;

	public Statement VantageDBConnectivity(String DBSERVERIP,String DBName,String sDBUser,String sDBPass) throws ClassNotFoundException, SQLException, InterruptedException  
	{
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
			
		}

       



