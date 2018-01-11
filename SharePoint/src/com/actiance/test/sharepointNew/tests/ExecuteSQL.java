package com.actiance.test.sharepointNew.tests;

import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class ExecuteSQL {

private static String driverName= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
private static String DB_URL = "jdbc:sqlserver://192.168.117.169:1433";
private static String USER = "sa";
private static String PASS = "FaceTime@123";

public static void createEmptyDBs(int onpremOrmt, int schOrman){
switch (onpremOrmt){
case 1:
switch (schOrman) {
case 1:
executeSQLMaster("USE master IF EXISTS(select * from sys.databases where name='nayana2014r2sp1qfe') DROP DATABASE nayana2014r2sp1qfe CREATE DATABASE nayana2014r2sp1qfe");
break;
case 2:
executeSQLMaster("USE master IF EXISTS(select * from sys.databases where name='nayana2014r2sp1qfe') DROP DATABASE nayana2014r2sp1qfe CREATE DATABASE nayana2014r2sp1qfe");
break;
}
break;
case 2:
switch (schOrman) {
case 1:
executeSQLMaster("USE master IF EXISTS(select * from sys.databases with (nolock) where name='nidhi_PS_CI_2014R2') DROP DATABASE nidhi_PS_CI_2014R2 CREATE DATABASE nidhi_PS_CI_2014R2");
executeSQLMaster("USE master IF EXISTS(select * from sys.databases with (nolock) where name='nidhi_CDS_CI_2014R2') DROP DATABASE nidhi_CDS_CI_2014R2 CREATE DATABASE nidhi_CDS_CI_2014R2");
executeSQLMaster("USE master IF EXISTS(select * from sys.databases with (nolock) where name='nidhi_T1_CI_2014R2') DROP DATABASE nidhi_T1_CI_2014R2 CREATE DATABASE nidhi_T1_CI_2014R2");
executeSQLMaster("USE master IF EXISTS(select * from sys.databases with (nolock) where name='nidhi_T2_CI_2014R2') DROP DATABASE nidhi_T2_CI_2014R2 CREATE DATABASE nidhi_T2_CI_2014R2");
break;
case 2:
executeSQLMaster("USE master IF EXISTS(select * from sys.databases with (nolock) where name='nidhi_PS_CI1_2014R2') DROP DATABASE nidhi_PS_CI1_2014R2 CREATE DATABASE nidhi_PS_CI1_2014R2");
executeSQLMaster("USE master IF EXISTS(select * from sys.databases with (nolock) where name='nidhi_CDS_CI1_2014R2') DROP DATABASE nidhi_CDS_CI1_2014R2 CREATE DATABASE nidhi_CDS_CI1_2014R2");
executeSQLMaster("USE master IF EXISTS(select * from sys.databases with (nolock) where name='nidhi_T1_CI1_2014R2') DROP DATABASE nidhi_T1_CI1_2014R2 CREATE DATABASE nidhi_T1_CI1_2014R2");
executeSQLMaster("USE master IF EXISTS(select * from sys.databases with (nolock) where name='nidhi_T2_CI1_2014R2') DROP DATABASE nidhi_T2_CI1_2014R2 CREATE DATABASE nidhi_T2_CI1_2014R2");
break;
}
break;
default:
System.out.println("Invalid value");
break;
}
}

public static void updatePasswords(int onpremOrmt, int schOrman) {
switch (onpremOrmt){
case 1:
switch (schOrman) {
case 1:
executeSQLDB("update EmployeeAttributes set value='WYZPGY94XHVE65FWMZW69N7RBADC6' where intEmployeeID=0 and attrID=15", "nayana2014r2sp1qfe");
executeSQLDB("insert into EmployeeAttributes values(1,15,'WYZPGY94XHVE65FWMZW69N7RBADC6')", "nayana2014r2sp1qfe");
executeSQLDB("delete from EmployeeAttributes where attrID=92", "nayana2014r2sp1qfe");
break;
case 2:
executeSQLDB("update EmployeeAttributes set value='WYZPGY94XHVE65FWMZW69N7RBADC6' where intEmployeeID=0 and attrID=15", "nayana2014r2sp1qfe");
executeSQLDB("insert into EmployeeAttributes values(1,15,'WYZPGY94XHVE65FWMZW69N7RBADC6')", "nayana2014r2sp1qfe");
executeSQLDB("delete from EmployeeAttributes where attrID=92", "nayana2014r2sp1qfe");
break;
default:
System.out.println("Invalid value");
break;
}
break;
case 2:
switch (schOrman) {
case 1:
executeSQLDB("update EmployeeAttributes set value='WYZPGY94XHVE65FWMZW69N7RBADC6' where intEmployeeID=0 and attrID=15", "nidhi_T1_CI_2014R2");
executeSQLDB("insert into EmployeeAttributes values(1,15,'WYZPGY94XHVE65FWMZW69N7RBADC6')", "nidhi_T1_CI_2014R2");
executeSQLDB("delete from EmployeeAttributes where attrID=92", "nidhi_T1_CI_2014R2");
executeSQLDB("update EmployeeAttributes set value='WYZPGY94XHVE65FWMZW69N7RBADC6' where intEmployeeID=0 and attrID=15", "nidhi_T2_CI_2014R2");
executeSQLDB("insert into EmployeeAttributes values(1,15,'WYZPGY94XHVE65FWMZW69N7RBADC6')", "nidhi_T2_CI_2014R2");
executeSQLDB("delete from EmployeeAttributes where attrID=92", "nidhi_T2_CI_2014R2");
break;
case 2:
executeSQLDB("update EmployeeAttributes set value='WYZPGY94XHVE65FWMZW69N7RBADC6' where intEmployeeID=0 and attrID=15", "nidhi_T1_CI1_2014R2");
executeSQLDB("insert into EmployeeAttributes values(1,15,'WYZPGY94XHVE65FWMZW69N7RBADC6')", "nidhi_T1_CI1_2014R2");
executeSQLDB("delete from EmployeeAttributes where attrID=92", "nidhi_T1_CI1_2014R2");
executeSQLDB("update EmployeeAttributes set value='WYZPGY94XHVE65FWMZW69N7RBADC6' where intEmployeeID=0 and attrID=15", "nidhi_T2_CI1_2014R2");
executeSQLDB("insert into EmployeeAttributes values(1,15,'WYZPGY94XHVE65FWMZW69N7RBADC6')", "nidhi_T2_CI1_2014R2");
executeSQLDB("delete from EmployeeAttributes where attrID=92", "nidhi_T2_CI1_2014R2");
break;
default:
System.out.println("Invalid value");
break;
}
break;
default:
System.out.println("Invalid value");
break;
}
}

public static void registerAPINetworks(int onpremOrmt, int schOrman) {
switch (onpremOrmt){
case 1:
switch (schOrman) {
case 1:
executeSQLScriptDB("createJiveAPINetwork.sql", "nayana2014r2sp1qfe");
executeSQLScriptDB("ChatterAPINetworkRegistrationSQLExecution.sql", "nayana2014r2sp1qfe");
executeSQLScriptDB("CreateJiveXMLnew.sql", "nayana2014r2sp1qfe");
break;
case 2:
executeSQLScriptDB("createJiveAPINetwork.sql", "nayana2014r2sp1qfe");
executeSQLScriptDB("ChatterAPINetworkRegistrationSQLExecution.sql", "nayana2014r2sp1qfe");
executeSQLScriptDB("CreateJiveXMLnew.sql", "nayana2014r2sp1qfe");
break;
}
break;
case 2:
switch (schOrman) {
case 1:
executeSQLScriptDB("createJiveAPINetwork.sql", "nidhi_T1_CI_2014R2");
executeSQLScriptDB("ChatterAPINetworkRegistrationSQLExecution_MT.sql", "nidhi_T1_CI_2014R2");
executeSQLScriptDB("createJiveAPINetwork.sql", "nidhi_T2_CI_2014R2");
executeSQLScriptDB("ChatterAPINetworkRegistrationSQLExecution_MT.sql", "nidhi_T2_CI_2014R2");
break;
case 2:
executeSQLScriptDB("createJiveAPINetwork.sql", "nidhi_T1_CI1_2014R2");
executeSQLScriptDB("ChatterAPINetworkRegistrationSQLExecution_MT.sql", "nidhi_T1_CI1_2014R2");
executeSQLScriptDB("createJiveAPINetwork.sql", "nidhi_T2_CI1_2014R2");
executeSQLScriptDB("ChatterAPINetworkRegistrationSQLExecution_MT.sql", "nidhi_T2_CI1_2014R2");
break;
}
break;
default:
System.out.println("Invalid value");
break;
}
}

private static Connection getConnection(String dbName) {
try {
Class.forName(driverName);
} catch(ClassNotFoundException ex) {
ex.printStackTrace();
}
Connection conn = null;
try {
if (dbName != null) {
conn = DriverManager.getConnection(DB_URL + ";database=" + dbName, USER, PASS);
} else {
conn = DriverManager.getConnection(DB_URL, USER, PASS);
}
} catch(SQLException e) {
e.printStackTrace();
}
return conn;
}

private static void executeSQLMaster(String sql) {
Connection conn = null;
Statement stmt = null;
try {
conn = getConnection(null);
stmt = conn.createStatement();
stmt.executeUpdate(sql);
} catch(SQLException ex) {
ex.printStackTrace();
}
finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
}

private static void executeSQLDB(String sql, String dbName) {
Connection conn = null;
Statement stmt = null;
try {
conn = getConnection(dbName);
stmt = conn.createStatement();
stmt.executeUpdate(sql);
} catch(SQLException ex) {
ex.printStackTrace();
}
finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
}


private static void executeSQLScriptDB(String sqlFile, String dbName) {
BufferedReader in = null;
Connection conn = null;
Statement stmt = null;
String sql = null;
try {
conn = getConnection(dbName);
stmt = conn.createStatement();
Scanner s = new Scanner(new BufferedReader(new FileReader(sqlFile))).useDelimiter(";\\n");
for (int lineNum=1; s.hasNext(); lineNum++) {
   sql = s.next();
   System.out.print("Line number " + lineNum + ": " + sql);
   stmt.executeUpdate(sql);
   //Thread.sleep(1000);
}
} catch(NullPointerException npe) {
//do nothing
}
catch(Exception e) {
e.printStackTrace();
}
finally {
try {
in.close();
} catch(NullPointerException npe) {
//do nothing
}
catch(Exception e){
e.printStackTrace();
}
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try

}

}

public static void createImporter(int onpremOrmt, int schOrman) {
switch (onpremOrmt){
case 1:
switch (schOrman) {
case 1:

createChatterImporter("ChatterImporterConfigs_DBproperties.csv", "nayana2014r2sp1qfe","4","gmail.com");
createJiveXMLImporter("JiveXMLnewConfig_DBproperties.csv","nayana2014r2sp1qfe","8","actiance.com");
break;
case 2:
createChatterImporter("ChatterImporterConfigs_DBproperties.csv", "nayana2014r2sp1qfe","4", "gmail.com");
createJiveXMLImporter("JiveXMLnewConfig_DBproperties.csv","nayana2014r2sp1qfe","8","actiance.com");
break;
}
break;
case 2:
switch (schOrman) {
case 1:
createChatterImporter("ChatterImporterConfigs_DBproperties.csv", "nidhi_T1_CI_2014R2", "1", "gmail.com");
//createChatterImporter("ChatterImporterConfigs_DBproperties.csv", "nidhi_T2_CI_2014R2", "4", "gmail.com");
break;
case 2:
createChatterImporter("ChatterImporterConfigs_DBproperties.csv", "nidhi_T1_CI1_2014R2", "1", "gmail.com");
//createChatterImporter("ChatterImporterConfigs_DBproperties.csv", "nidhi_T2_CI1_2014R2", "4", "gmail.com");
break;
}
break;
default:
System.out.println("Invalid value");
break;
}
}

public static void createOrganization(int onpremOrmt, int schOrman) {
switch (onpremOrmt){
case 1:
switch (schOrman) {
case 1:
//createOrganization("SCSettings.csv", "nayana2014r2sp1qfe");
break;
case 2:
//createOrganization("SCSettings.csv", "nayana2014r2sp1qfe");
break;
}
break;
case 2:
switch (schOrman) {
case 1:
createOrganization_T("SCSettings_T.csv", "nidhi_T1_CI_2014R2");
createOrganization_CDS("SCSettings_CDS.csv", "nidhi_CDS_CI_2014R2");
break;
case 2:
createOrganization_T("SCSettings_T.csv", "nidhi_T1_CI1_2014R2");
createOrganization_CDS("SCSettings_CDS.csv", "nidhi_CDS_CI1_2014R2");
break;
}
break;
default:
System.out.println("Invalid value");
break;
}
}

private static void createJiveXMLImporter(String ImporterCreationChattercsv, String dbName,
			String importernumberforchatter,
			String Importerinternaldomainforchatter) {
	System.out.println("###########INSIDE JiveXML IMPORTER CREATION############");
		Connection conn = null;
		Statement stmt = null;
		BufferedReader bufRdr = null;
		try {
			conn = getConnection(dbName);
			stmt = conn.createStatement();
			System.out
					.println("importernumber value set as per testng xml is : "
							+ importernumberforchatter);
			System.out
					.println("csvPath value set as per testng xml for importer creation is  : "
							+ ImporterCreationChattercsv);
			System.out
					.println("Importerinternaldomainforchatter value set as per testng xml is : "
							+ Importerinternaldomainforchatter);
			PreparedStatement preparedStatement = null;
			String insertTableSQL = "INSERT INTO SERVERPROPERTIES"
					+ "(name,value,defaultValue,type,description,intCompanyID,serverID,modTime,replicaType) VALUES"
					+ "(?,?,?,?,?,'-1','-1'," + System.currentTimeMillis()
					+ ",2)";
			preparedStatement = conn.prepareStatement(insertTableSQL);
			int linenumber = 0;
			String[] temp;
			String line = "";
			String mystring[] = new String[10000];
			String filename = ImporterCreationChattercsv;
			// reading the server properties and their values from the csv file
			// for creating importer
			FileInputStream csvfile = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(csvfile);
			bufRdr = new BufferedReader(new InputStreamReader(csvfile));
			while ((line = bufRdr.readLine()) != null) {
				mystring[linenumber] = line;
				temp = line.split(",");
				preparedStatement.setString(1, temp[0]);
				preparedStatement.setString(2, temp[1]);
				preparedStatement.setString(3, temp[2]);
				preparedStatement.setString(4, temp[3]);
				preparedStatement.setString(5, temp[4]);
				preparedStatement.addBatch();
				System.out.println("Inserted values are :" + temp[0] + " "
						+ temp[1] + " " + temp[2] + " " + temp[3] + " "
						+ temp[4] + " .");
				preparedStatement.executeBatch();
				conn.commit();
			}

			stmt.executeUpdate(" update ServerProperties set value='"
					+ System.currentTimeMillis()
					+ "' where name like 'imimport." + importernumberforchatter
					+ ".lastConfigUpdateTime'");
			System.out.println("added the relevent serverproperties from "
					+ filename + "for importer ");
			stmt.executeUpdate("update serverproperties set replicaType=NULL where name like '%numberofthread%'");
			stmt.executeUpdate("update serverproperties set value='"
					+ Importerinternaldomainforchatter
					+ "' where name like '%imai.internal.email.domains%'");
			System.out.println("internal email domain is changed to "
					+ Importerinternaldomainforchatter);
			System.out.println("Importer creation finished");
		} catch (Exception e) {

		} finally {
			if (bufRdr != null)
				try {
					bufRdr.close();
				} catch (IOException e) {

				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {

				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {

				}
		}
	}

private static void createChatterImporter(String ImporterCreationChattercsv, String dbName,
			String importernumberforchatter,
			String Importerinternaldomainforchatter) {
	System.out.println("###########INSIDE Chatter IMPORTER CREATION############");
		Connection conn = null;
		Statement stmt = null;
		BufferedReader bufRdr = null;
		try {
			conn = getConnection(dbName);
			stmt = conn.createStatement();
			System.out
					.println("importernumber value set as per testng xml is : "
							+ importernumberforchatter);
			System.out
					.println("csvPath value set as per testng xml for importer creation is  : "
							+ ImporterCreationChattercsv);
			System.out
					.println("Importerinternaldomainforchatter value set as per testng xml is : "
							+ Importerinternaldomainforchatter);
			PreparedStatement preparedStatement = null;
			String insertTableSQL = "INSERT INTO SERVERPROPERTIES"
					+ "(name,value,defaultValue,type,description,intCompanyID,serverID,modTime,replicaType) VALUES"
					+ "(?,?,?,?,?,'-1','-1'," + System.currentTimeMillis()
					+ ",2)";
			preparedStatement = conn.prepareStatement(insertTableSQL);
			int linenumber = 0;
			String[] temp;
			String line = "";
			String mystring[] = new String[10000];
			String filename = ImporterCreationChattercsv;
			// reading the server properties and their values from the csv file
			// for creating importer
			FileInputStream csvfile = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(csvfile);
			bufRdr = new BufferedReader(new InputStreamReader(csvfile));
			while ((line = bufRdr.readLine()) != null) {
				mystring[linenumber] = line;
				temp = line.split(",");
				preparedStatement.setString(1, temp[0]);
				preparedStatement.setString(2, temp[1]);
				preparedStatement.setString(3, temp[2]);
				preparedStatement.setString(4, temp[3]);
				preparedStatement.setString(5, temp[4]);
				preparedStatement.addBatch();
				System.out.println("Inserted values are :" + temp[0] + " "
						+ temp[1] + " " + temp[2] + " " + temp[3] + " "
						+ temp[4] + " .");
				preparedStatement.executeBatch();
				conn.commit();
			}

			stmt.executeUpdate(" update ServerProperties set value='"
					+ System.currentTimeMillis()
					+ "' where name like 'imimport." + importernumberforchatter
					+ ".lastConfigUpdateTime'");
			System.out.println("added the relevent serverproperties from "
					+ filename + "for importer ");
			stmt.executeUpdate("update serverproperties set replicaType=NULL where name like '%numberofthread%'");
			stmt.executeUpdate("update serverproperties set value='"
					+ Importerinternaldomainforchatter
					+ "' where name like '%imai.internal.email.domains%'");
			System.out.println("internal email domain is changed to "
					+ Importerinternaldomainforchatter);
			System.out.println("Importer creation finished");
		} catch (Exception e) {

		} finally {
			if (bufRdr != null)
				try {
					bufRdr.close();
				} catch (IOException e) {

				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {

				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {

				}
		}
	}


private static void createOrganization_T(String OrganizationCreationcsv, String dbName) {
                Connection conn = null;
                Statement stmt = null;
                BufferedReader bufRdr = null;
                try {
                        conn = getConnection(dbName);
                        stmt = conn.createStatement();
                        PreparedStatement preparedStatement = null;
                        /*String insertTableSQL = "INSERT INTO SERVERPROPERTIES"
                                        + "(name,value,defaultValue,type,description,intCompanyID,serverID,modTime,replicaType) VALUES"
                                        + "(?,?,?,?,?,'-1','-1'," + System.currentTimeMillis()
                                        + ",NULL)";*/

                    String updateTableSQL = "UPDATE SERVERPROPERTIES SET value=?,modTime=" + System.currentTimeMillis() + "where name=?";
                        preparedStatement = conn.prepareStatement(updateTableSQL);
                        int linenumber = 0;
                        String[] temp;
                        String line = "";
                        String mystring[] = new String[10000];
                        String filename = OrganizationCreationcsv;
                        // reading the server properties and their values from the csv file
                        // for creating importer
                        FileInputStream csvfile = new FileInputStream(filename);
                        DataInputStream in = new DataInputStream(csvfile);
                        bufRdr = new BufferedReader(new InputStreamReader(csvfile));
                        while ((line = bufRdr.readLine()) != null) {
                                mystring[linenumber] = line;
                                temp = line.split(",");
                                preparedStatement.setString(1, temp[1]);
                                preparedStatement.setString(2, temp[0]);
                                //preparedStatement.setString(3, temp[2]);
                                //preparedStatement.setString(4, temp[3]);
                                //preparedStatement.setString(5, temp[4]);
                                preparedStatement.addBatch();
                                System.out.println("Inserted values are :" + temp[0] + " "
                                                + temp[1] + " " + temp[2] + " " + temp[3] + " "
                                                + temp[4] + " .");
                        }
                        preparedStatement.executeBatch();
                        conn.commit();
                        stmt.executeUpdate(" update ServerProperties set value='"
                                        + System.currentTimeMillis()
                                        + "' where name like 'smartcloud.connections.config.updated'");
		        stmt.executeUpdate(" update ServerProperties set value='"
                                        + System.currentTimeMillis()
                                        + "' where name like 'smartcloud.sub.0.lastModifiedTime'");
                        stmt.executeUpdate(" update ServerProperties set value='"
                                        + System.currentTimeMillis()
                                        + "' where name like 'smartcloud.subs.updated'");
                        System.out.println("Organization creation finished");
                } catch (Exception e) {
             e.printStackTrace();
                } finally {
                        if (bufRdr != null)
                                try {
                                        bufRdr.close();
                                } catch (IOException e) {

                                }
                        if (stmt != null)
                                try {
                                        stmt.close();
                                } catch (SQLException e) {

                                }
                        if (conn != null)
                                try {
                                        conn.close();
                                } catch (SQLException e) {

                                }
                }
        }

private static void createOrganization_CDS(String OrganizationCreationcsv, String dbName) {
                Connection conn = null;
                Statement stmt = null;
                BufferedReader bufRdr = null;
                try {
                        conn = getConnection(dbName);
                        stmt = conn.createStatement();
                        PreparedStatement preparedStatement = null;
                        /*String insertTableSQL = "INSERT INTO SERVERPROPERTIES"
                                        + "(name,value,defaultValue,type,description,intCompanyID,serverID,modTime,replicaType) VALUES"
                                        + "(?,?,?,?,?,'-1','-1'," + System.currentTimeMillis()
                                        + ",NULL)";*/

                    String updateTableSQL = "UPDATE SERVERPROPERTIES SET value=?,modTime=" + System.currentTimeMillis() + "where name=?";
                        preparedStatement = conn.prepareStatement(updateTableSQL);
                        int linenumber = 0;
                        String[] temp;
                        String line = "";
                        String mystring[] = new String[10000];
                        String filename = OrganizationCreationcsv;
                        // reading the server properties and their values from the csv file
                        // for creating importer
                        FileInputStream csvfile = new FileInputStream(filename);
                        DataInputStream in = new DataInputStream(csvfile);
                        bufRdr = new BufferedReader(new InputStreamReader(csvfile));
                        while ((line = bufRdr.readLine()) != null) {
                                mystring[linenumber] = line;
                                temp = line.split(",");
                                preparedStatement.setString(1, temp[1]);
                                preparedStatement.setString(2, temp[0]);
                                //preparedStatement.setString(3, temp[2]);
                                //preparedStatement.setString(4, temp[3]);
                                //preparedStatement.setString(5, temp[4]);
                                preparedStatement.addBatch();
                                System.out.println("Inserted values are :" + temp[0] + " "
                                                + temp[1] + " " + temp[2] + " " + temp[3] + " "
                                                + temp[4] + " .");
                        }
                        preparedStatement.executeBatch();
                        conn.commit();
                        stmt.executeUpdate(" update ServerProperties set value='"
                                        + System.currentTimeMillis()
                                        + "' where name like 'sc.connector.config.updated'");
                         System.out.println("Organization creation finished");
                } catch (Exception e) {
             e.printStackTrace();
                } finally {
                        if (bufRdr != null)
                                try {
                                        bufRdr.close();
                                } catch (IOException e) {

                                }
                        if (stmt != null)
                                try {
                                        stmt.close();
                                } catch (SQLException e) {

                                }
                        if (conn != null)
                                try {
                                        conn.close();
                                } catch (SQLException e) {

                                }
                }

        }
}

