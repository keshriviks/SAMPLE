
/**
 * 	Created By : Manish Garg
 *  Date : 11-Apr-2016
 *  
 * 
 */
package com.actiance.test.exporterNew.tests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.actiance.test.exporterNew.selenium.CreateExporter;
import com.actiance.test.exporterNew.selenium.InteractionExporters;
import com.actiance.test.exporterNew.utils.CollabProcessing;
import com.actiance.test.exporterNew.utils.CommonUtils;
import com.actiance.test.exporterNew.utils.DBUtils;
import com.actiance.test.exporterNew.utils.EmailProcessing;
import com.actiance.test.exporterNew.utils.Headers;
import com.actiance.test.exporterNew.utils.IMProcessing;
import com.actiance.test.exporterNew.utils.TestUtils;


public class ExporterTest {

	CollabProcessing collabProcess ;
	IMProcessing imProcess;
	CommonUtils commonUtils;
	EmailProcessing emailProcessing ; 
	
	String sTimeAppend = null;
	
	public static boolean isExporterConfigured = false;
    public static DBUtils dbUtils ;
    public static boolean isCleanupDone = false;
	public static String vantageIP = null;
	public static long defaultWaitSeconds = 60000;
	public static String smtpEmlPath = null;
	public static String emlDumpPath = null;
	public int fileLoop = 0;
	public static boolean restartVantage = false;
//	public String powerShellLocation = null;
	int exporterTempleteNumber = 0;
	public String failedScenarioDumpPath = null;
	public int exporterInitialTemplete = 0;
	public InteractionExporters interactionExporter;
	
/**
 * 
 * @param driverName
 * @param db_url
 * @param dbName
 * @param user
 * @param password
 * @param numberOfExporter
 * @param testScenarioFolder
 * @param vantageFile
 * @param smtpServer
 * @param fromHeader
 * @param toHeader
 * @param smtpRecipients
 * @param smtpSender
 * @param serverIDs
 * @param emlDumpPath
 * @param failedDumpPath
 * @param smtpEmlsPath
 * @param powerShellFileLocation
 * @param vantageIP
 * @param defaultExporterTempletePath
 */
	
 
	@Parameters({"driverName","db_url","dbName","user","password","numberOfExporter","testScenarioFolder","smtpServer","fromHeader","toHeader","smtpRecipients","smtpSender","smtpPort","serverIDs","emlDumpPath","failedDumpPath","smtpEmlsPath","powerShellFileLocation", "vantageIP", "vanUsername", "vanPassword", "profileName", "defaultExporterTempletePath","defaultXSLTPath"})
	@BeforeSuite
	public void beforeSuite(String driverName, String db_url,String dbName, String user, String password, String numberOfExporter, String testScenarioFolder, String smtpServer, String fromHeader, String toHeader, String smtpRecipients, String smtpSender, String smtpPort, String serverIDs, String emlDumpPath,String failedDumpPath,String smtpEmlsPath,String powerShellFileLocation, String vantageIP, String vanUsername, String vanPassword, String profileName, String defaultExporterTempletePath, String defaultXSLTPath){
		
		
		
//		Setting Parameters Values in System Variable
		System.setProperty("driverName", driverName);
		System.setProperty("db_url", db_url);
		System.setProperty("dbName", dbName);
		System.setProperty("user", user);
		System.setProperty("password", password);
		
		System.setProperty("numberOfExporter", numberOfExporter);
		System.setProperty("testScenarioFolder", testScenarioFolder);
//		System.setProperty("vantageFile", vantageFile);
		
//		SMTP details
		System.setProperty("smtpServer", smtpServer);
		System.setProperty("fromHeader", fromHeader);
		System.setProperty("toHeader", toHeader);
		System.setProperty("smtpRecipients", smtpRecipients);
		System.setProperty("smtpSender", smtpSender);
		System.setProperty("smtpPort", smtpPort.trim());
		System.setProperty("serverIDs", serverIDs);
		
		System.out.println("Server Id : "+System.getProperty("serverIDs"));
		
		
		System.setProperty("emlDumpPath", emlDumpPath);
		System.setProperty("failedDumpPath", failedDumpPath);
		System.setProperty("smtpEmlsPath", smtpEmlsPath);
		System.setProperty("powerShellLocation", powerShellFileLocation);
		
//		Vantage details
		System.setProperty("vantageIP", vantageIP);
		
		System.setProperty("vanUsername", vanUsername.trim());
		System.setProperty("vanPassword", vanPassword.trim());
		System.setProperty("profileName", profileName.trim());
		
		
		System.setProperty("defaultExporterTempletePath",defaultExporterTempletePath);
		System.setProperty("defaultXSLTPath", defaultXSLTPath);
		
		
//		Create Exporter
		CreateExporter createTemplete = new CreateExporter();
//		File file = new File(vantageFile);
		System.out.println("************************************");
		System.out.println("Creating Exporter............... ");
		System.out.println("************************************");
		System.out.println("\n");
//		Creating N numbers Exporter Templete Through Selenium Code
		interactionExporter = createTemplete.createNewTemplate(Integer.valueOf(numberOfExporter));
		System.out.println("****************Exporter Created*****************");
		
//		Getting Last Exporter Templete Number before Creating new Exporter Templete
		exporterTempleteNumber = CommonUtils.getExporterNum(createTemplete.getExporterName());
		System.out.println(exporterTempleteNumber);
		exporterInitialTemplete = exporterTempleteNumber;
//		System.out.println("Done");
		this.vantageIP = vantageIP;
	}
	

	@BeforeMethod
	public void setup() {
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sTimeAppend = sdf.format(cal.getTime()).toString();
//			System.out.println(sTimeAppend);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testExporter(){
		boolean restartVantage = false; // this will ensure that will restart only once.
		boolean testOutput = true;
		HashMap<String,Boolean> result= null;
		String templateToUse = "imexport.";
		
		
		System.out.println("Inside Test Method");
		
//		DB Details..
		ArrayList<String> dbParams = new ArrayList();
		dbParams.add(System.getProperty("driverName"));
		dbParams.add(System.getProperty("dbName"));
		dbParams.add(System.getProperty("db_url"));
		dbParams.add(System.getProperty("user"));
		dbParams.add(System.getProperty("password"));
		dbUtils = new DBUtils(dbParams);
		
		try{
			String folderLocation = System.getProperty("testScenarioFolder");
			File folderOfTestFiles = new File(folderLocation);
			
	//		Getting all the Scenario(Test case) file from source location
			File[] testFiles = folderOfTestFiles.listFiles();
			
	//		 Numbers of Scenario want to run in a batch 
			int numberOfScenarioAtOnce = Integer.valueOf(System.getProperty("numberOfExporter"));
			
	//		 Finding the Loop length
			int loopSize = testFiles.length/numberOfScenarioAtOnce;
			int loopDiff = testFiles.length%numberOfScenarioAtOnce;
			if(loopDiff > 0){
				loopSize++;
			}
			
			System.out.println("Loop Size = "+ loopSize);
			int j = 0;
	//		Running the exporter till loop length
			for(int i = 0 ; i < loopSize; i++){
				result = new HashMap();
				HashMap<Integer,ArrayList<String>> failedInterId = null;
				int smtpUser = 1;
				exporterTempleteNumber = exporterInitialTemplete;
				
				ArrayList<String> scenarioTemplete = new ArrayList();
				HashMap<String,File> templeteIdWithScenarioFile = new HashMap();
				LinkedHashMap<String,String> lastActivityOnTime = null;
				HashMap<String,HashMap<String,String>> scenarioWithTempleteMap = new HashMap();
				HashMap<String,HashMap<String,String>> scenarioMap;
				HashMap<String,String> scenarioName_TempleteName = null;
				ArrayList<String> finishedExporterList = new ArrayList();
				ArrayList<String> exporterTempletes = new ArrayList();
				ArrayList<String> runningExporterList = new ArrayList();
				HashMap<String,Integer> exporterWithsmtpUser = new HashMap(); 
				HashMap<String,String> runEx_EmlLocation = null;
				
				Statement stmt = null;
				System.out.println("-------------------------------------------------------------------------------");
				
//				ArrayList<String> exporterNames = new ArrayList();
				
				for(int h = j ; h < testFiles.length ; h++){
					String exporterNames = null;
					commonUtils = new CommonUtils();
					stmt = DBUtils.getConnection(dbParams).createStatement();
//					System.setProperty("serverIDs", String.valueOf(dbUtils.getServerId(stmt)));
					collabProcess = new CollabProcessing();
					imProcess = new IMProcessing();
					
					scenarioMap = new HashMap();
					scenarioName_TempleteName = new HashMap();
					System.out.println("**************************************************");
	//				 getting scenario File
					File file = testFiles[j];
					System.out.println("Running Scenario File : "+file.getName());
					int exportNumber = ++exporterTempleteNumber ;
					
					exporterNames = "Exporter "+ exportNumber;
					
					System.out.println(exportNumber);
	//				 Creating Exporter Templete Name "imexport.no."
					String exporterTemplete = templateToUse + exportNumber + ".";
					System.out.println("Exporter Templete Id : "+ exporterTemplete);
					int smtpUserNo = smtpUser;
					System.out.println("SMTP User Number : "+ smtpUserNo);
	//				 Generating Configuration Map for Scenario
					HashMap<String,String> map = commonUtils.generateMapNew(file.getAbsolutePath(), exporterTemplete,smtpUserNo);
					System.out.println("Map generated For Exporter Templete  : "+ exporterTemplete);
//					System.out.println(map);
	//				Increase smtp user name by 1
					smtpUser++;
					
					isExporterConfigured = ConfigureExporter(map);
					
	//				mapping smtp user no with exporter templete and putting into map
					exporterWithsmtpUser.put(exporterTemplete, smtpUserNo);
					
	//				mapping exporter configuration map with exporter templete Id and putting into map.
					scenarioWithTempleteMap.put(exporterTemplete, map);
					
					if(isExporterConfigured){
						System.out.println("Configured Vantage Templete : " + exporterTemplete + " For Scenario : " + file.getName());
	//					if Exporter Successfully Configured than putting Exporter Templete Name into Map
						runningExporterList.add("Exporter "+ exportNumber);
						System.out.println("Running Exporter List : "+ runningExporterList );
	//					Putting exporter Templete Id into Arraylist
						exporterTempletes.add(exporterTemplete);
						System.out.println("Running Exporter Templete Ids : "+ exporterTempletes);
	//					putting scenario file name with its Configurable Map
						scenarioMap.put(file.getName(), map);
						System.out.println(scenarioMap);
						scenarioName_TempleteName.put(exporterTemplete, file.getName());
						templeteIdWithScenarioFile.put(exporterTemplete, file);
						
						System.out.println("trying to click on OK button for Exporter" + exporterNames);
						interactionExporter.selectExporterAndClickGo(exporterNames);
					}
	//				Get Last Activity On Time (Default)
									
					j++;
					
//					Restart Vantage Services
					if(j % numberOfScenarioAtOnce == 0){
//						if(!restartVantage){ // Restart Vantage only First Time
//							commonUtils.restartVantage(vantageIP, "60000");
//							Thread.sleep(defaultWaitSeconds);
//							restartVantage = true;
//						}
						break;
					}
					
				}
				
				lastActivityOnTime = dbUtils.getLastActivityOnTime(exporterTempletes, dbParams);
				dbUtils = new DBUtils(dbParams);
				LinkedHashMap<String,String> exporterMap = new LinkedHashMap();
				exporterMap = lastActivityOnTime;
				System.out.println(exporterMap);
				Set<String> expMap = exporterMap.keySet();
				System.out.println(expMap);
				
	//			HashSet<String> eMap = new HashSet();
	//			eMap = (HashSet)expMap;
				int checkExporterCounter = 6;
				Long waitTime = 60000L;
				for(int f = 0 ; f < runningExporterList.size(); f++){
					if(checkExporterCounter > 0){
						--checkExporterCounter;
						runEx_EmlLocation = new HashMap();
						finishedExporterList = dbUtils.getCompletedExporterList(exporterMap, dbParams,waitTime);
						if(finishedExporterList.size() != 0){
		//					exporterMap =new LinkedHashMap();
							f += finishedExporterList.size();
						}else{
							f = f;
							continue;
							
						}
						System.out.println("***********************************************");
						System.out.println("Exporter templete Id , which finished its job");
						System.out.println(finishedExporterList);
						System.out.println("***********************************************");
						
		//				Start Verification for Exporter which Finished It's Job...
						
						
						for(int i1 = 0; i1<finishedExporterList.size(); i1++ ){
							Headers header = new Headers();
							boolean validation = false;
	//						getting one finished exporter templete name
							String runningExporterTemplete = finishedExporterList.get(i1);
							HashMap<String,String> runningExporterMap = scenarioWithTempleteMap.get(runningExporterTemplete);
							
//							get all the Headers and its token from test file, if it is there
							
							HashMap<String,String> headersFromTestFile = commonUtils.getHeaderMapFromTestFile(templeteIdWithScenarioFile.get(runningExporterTemplete));
							
							scenarioTemplete.add(runningExporterTemplete);
							System.out.println("Start Verification for Exporter Templete : "+ runningExporterTemplete);

							String getConversationType = commonUtils.getModality(runningExporterMap);
							String getNetworkName  = new TestUtils().checkNetworkIDForReuter(stmt, runningExporterMap);
							
							if(getNetworkName != null){
								if(getConversationType.equals("email")){
									getNetworkName = "BloombergEmail";
								}
								if(getNetworkName.equals("Reuters")){
									getConversationType = "email";
								}		
							}
						
							
							System.out.println("Modality Type : " + getConversationType);
							
							
	//						getting expected Interaction Id's which is supposed to be exported
	//						ArrayList<Integer> interIDsExported = dbUtils.getInterIDsBasedOnFilters(getConversationType, CommonUtils.returnGroupIDs(scenarioWithTempleteMap.get(finishedExporterList.get(i1))), CommonUtils.returnNetworkIDs(scenarioWithTempleteMap.get(finishedExporterList.get(i1))),false, false, "", "",dbParams);
							
							ArrayList<Integer> interIDsExported = dbUtils.getInterIDsBasedOnFilters(runningExporterMap,dbParams);
							
							System.out.println("---------Expected Interaction Ids to be Exported -----------");
							System.out.println(interIDsExported);
							
							String userForRunningTemplete = CommonUtils.getSmtpUser(exporterWithsmtpUser.get(runningExporterTemplete));
							
							System.out.println("SMTP User name for Running Exporter Templete : "+ userForRunningTemplete);
							
//							Getting SMTP path, where exporter dumps .emls
							smtpEmlPath = System.getProperty("smtpEmlsPath")+"/" + CommonUtils.getUser(userForRunningTemplete);
							File folder = new File(smtpEmlPath);
							if(!folder.exists()){
								System.out.println(smtpEmlPath);
//								System.out.println("Above SMTP dump Location is Empty");
								continue;
							}else{
//								new path for eml file 
								emlDumpPath = System.getProperty("emlDumpPath")+"/" + CommonUtils.getUser(userForRunningTemplete);
								

								
								runEx_EmlLocation.put(runningExporterTemplete, emlDumpPath);  // this is for failed scenario, copying all the file for failed scenario
								
								System.out.println("*****Folder location where SMTP dumping all the emls*****");
								System.out.println(smtpEmlPath);
								
								System.out.println("*****Folder location where Code is dumping all the emls*****");
								System.out.println(emlDumpPath);
								
								System.out.println("Start Dumping the File on above location");
	//							create new directory for emlDumpPath
								CommonUtils.createDirectory(emlDumpPath);
	//							copying all EML's from SMTP to emlDumpPath with changed name (Conversation#interId).
								CommonUtils.changeFileNamesAndCopy(smtpEmlPath, emlDumpPath);
								System.out.println("Dumping Process is Done");

//								get all the Headers and its values from emls for one test case
								HashMap<Integer,HashMap<String,String>> getHeaderValuesFromEmls= header.getHeaderMapFromEml(emlDumpPath);
								
								System.out.println("--------------Start Verification------------");
								System.out.println();
								
	//							check XSLT or Normal Formatted, if No XSLT than execute if block code
								if(!runningExporterMap.get(runningExporterTemplete+"converterClass").toString().equalsIgnoreCase("com.actiance.coreserver.export.converter.InteractionToXML")){
	
	//								check conversation type
									if(getConversationType.equals("Collaboration")){
	//									verify Data for Collab
										System.out.println("Test Case is for Collabration");
										HashMap<Integer, HashMap<String, String>> DBMap = dbUtils.getXMLMessageDataForCollabFromDB(interIDsExported);
										
										if(runningExporterMap.get(runningExporterTemplete+"converterClass").toString().contains("XML")){
											HashMap<Integer, HashMap<String, String>> fileMap = collabProcess.getXMLDataForCollabFromEML(emlDumpPath);
											validation = collabProcess.verifyXMLForCollab(fileMap, DBMap, runningExporterMap.get(runningExporterTemplete+"converterClass").toString(), headersFromTestFile,getHeaderValuesFromEmls,runningExporterTemplete);
											
										}else if(runningExporterMap.get(runningExporterTemplete+"converterClass").toString().contains("Text")){
											DBMap = dbUtils.getMessageDataForCollabForText(interIDsExported);
											HashMap<Integer, String> fileMap = collabProcess.getMessage(emlDumpPath);
											validation = collabProcess.verifyTextFormatForCollab(DBMap, fileMap, runningExporterMap.get(runningExporterTemplete+"converterClass").toString() ,headersFromTestFile,getHeaderValuesFromEmls,runningExporterTemplete);
											
										}else if(runningExporterMap.get(runningExporterTemplete+"converterClass").toString().contains("HTML")){
											HashMap<Integer, HashMap<String, String>> fileMap = collabProcess.getHTMLContentForCollab(emlDumpPath);
											validation = collabProcess.verifyXMLForCollab(fileMap, DBMap, runningExporterMap.get(runningExporterTemplete+"converterClass").toString(), headersFromTestFile,getHeaderValuesFromEmls,runningExporterTemplete);
										}
	//									Verify Data for IM
									}else if(getConversationType.equals("IM")){
										System.out.println("Test Case is for IM");

//										Skip Duplicate Checking
										HashMap<String,String> tempMap = new HashMap();
										tempMap = runningExporterMap;
										if(CommonUtils.skipDuplicate(runningExporterMap)){
											ArrayList<Integer> intFromEmls = CommonUtils.getInterIdsFromEmls(emlDumpPath);
											CommonUtils.checkInteractionDuplicate(interIDsExported, intFromEmls, tempMap, runningExporterTemplete);
										}
										
//										Skip Empty Checking
										if(CommonUtils.skipEmpty(runningExporterMap)){
											ArrayList<Integer> interIds_From_EMLs = CommonUtils.getInterIdsFromEmls(emlDumpPath);
											ArrayList<Integer> getEmptyInterIdsFromExpectedInterIds = dbUtils.getEmptyInteractions(interIDsExported);
											CommonUtils.checkEmptyInteractions(interIds_From_EMLs, getEmptyInterIdsFromExpectedInterIds);
										}
										
										HashMap<Integer, HashMap<String, ArrayList<String>>> DBMap = dbUtils.getMessageData(interIDsExported,dbParams);
	//									Verify XML formatted Data for IM
										if(runningExporterMap.get(runningExporterTemplete+"converterClass").toString().contains("XML")){
											System.out.println("Format Type is : XML");
											HashMap<Integer, HashMap<String, ArrayList<String>>> fileMap = imProcess.getXMLContent(emlDumpPath);
											validation = imProcess.verifyXMLData(DBMap, fileMap, runningExporterMap.get(runningExporterTemplete +"converterClass"),headersFromTestFile,getHeaderValuesFromEmls,runningExporterTemplete);
	//										Verify Text Formatted Data For IM
										}else if(runningExporterMap.get(runningExporterTemplete+"converterClass").toString().contains("Text")){
											HashMap<Integer, String> fileMap = CommonUtils.getMessage(emlDumpPath);
											validation = imProcess.verifyTextFormatData(DBMap, fileMap ,headersFromTestFile,getHeaderValuesFromEmls,runningExporterTemplete);
	//										Verify HTML Formatted Data
										}else if(runningExporterMap.get(runningExporterTemplete+"converterClass").toString().contains("HTML")){
											HashMap<Integer, HashMap<String, ArrayList<String>>> fileMap = CommonUtils.getHTMLContent(emlDumpPath);
											validation = imProcess.verifyHTMLContent(DBMap, fileMap ,headersFromTestFile,getHeaderValuesFromEmls,runningExporterTemplete);//
										}
									}else {
										System.out.println("Conversation is for Email");
										emailProcessing = new EmailProcessing();
										failedInterId = emailProcessing.processingEmails(stmt, getNetworkName , emlDumpPath);
									}
								}else{// If XSLT than this will execute
									String xsltName = System.getProperty("XSLTName");
	//								String folderPath = null;
	//			Get Data from DB
									
									ArrayList<Integer> interIDs = interIDsExported;
									
									if(xsltName.contains("collab")){
										HashMap<Integer,HashMap<String,String>> dbMap = dbUtils.getXMLMessageDataForCollabFromDB(interIDsExported);	
										if(xsltName.contains("csv")){
	//										TestUtils.check_CSV_XSLT_Type_Validation(emlDumpPath);
											HashMap<Integer,HashMap<String,String>> fileMap = ParsingXSLTs.parseCSV_Xslt(emlDumpPath);
											validation = collabProcess.verifyXMLForCollab(fileMap, dbMap, null, headersFromTestFile, getHeaderValuesFromEmls, runningExporterTemplete);
											
										}else if(xsltName.contains("html")){
	//										TestUtils.check_HTML_XSLT_Type_Validation(emlDumpPath);
											dbMap = dbUtils.getMessageDataOfCollabForHTML(interIDsExported);
											HashMap<Integer,HashMap<String,String>> fileMap = new ParsingXSLTs().parseHTML_XSLT_Collab(emlDumpPath);
											validation = collabProcess.verifyHTMLContentForCollab(dbMap, fileMap, headersFromTestFile, getHeaderValuesFromEmls, runningExporterTemplete);
											
										}else if(xsltName.contains("text")){
//											TestUtils.check_Text_XSLT_Type_Validation(emlDumpPath);
											dbMap = dbUtils.getMessageDataForCollabForText(interIDsExported);
											HashMap<Integer,String> fileMap = CommonUtils.getMessage(emlDumpPath);
											validation = collabProcess.verifyTextFormatForCollab(dbMap, fileMap, null, headersFromTestFile, getHeaderValuesFromEmls, runningExporterTemplete);
										}else if(xsltName.contains("xml")){
											HashMap<Integer,HashMap<String,String>> fileMap = CommonUtils.getXMLDataForCollabFromEML(emlDumpPath);
											validation = collabProcess.verifyXMLForCollab(fileMap, dbMap, null, headersFromTestFile, getHeaderValuesFromEmls, runningExporterTemplete);
											
										}
										
									}else if(xsltName.contains("im")){
										HashMap<Integer,HashMap<String,ArrayList<String>>> fileMap =null;
										HashMap<Integer,HashMap<String,ArrayList<String>>> dbMap = dbUtils.getMessageData(interIDsExported, dbParams);
										if(xsltName.contains("html")){
											fileMap = ParsingXSLTs.getHTMLContentOfXSLTForIM(emlDumpPath);
											validation = CommonUtils.verifyHTMLContent(dbMap, fileMap, headersFromTestFile, getHeaderValuesFromEmls, runningExporterTemplete);
										}else if(xsltName.contains("text")){
											HashMap<Integer,String> fileMapText = CommonUtils.getMessage(emlDumpPath);
											validation = CommonUtils.verifyTextFormatData(dbMap, fileMapText, headersFromTestFile, getHeaderValuesFromEmls, runningExporterTemplete);
										}else if(xsltName.contains("xml")){
											fileMap = ParsingXSLTs.getXMLContentOfXSLTForIM(emlDumpPath);
											validation = CommonUtils.verifyXMLData(dbMap, fileMap, null, headersFromTestFile, getHeaderValuesFromEmls, runningExporterTemplete);
										}
									}
								}
								
								if(getConversationType.equalsIgnoreCase("email")){
									failedInterId = CommonUtils.getFailedInterIDs();	
								}
								
								if(failedInterId.size() != 0){
									System.out.println("********Printing Failed Interaction******** ");
									for(Integer interId : failedInterId.keySet()){
										System.out.println("Interaction : "+ interId);
										System.out.println("And Failed Because of : "+ failedInterId.get(interId));
									
									}
									System.out.println("*********************************************");
								}
								result.put(finishedExporterList.get(i1), Boolean.valueOf(validation));
							}
						}
						
						
						
		//				cet = Completed Exporter Templates
						for(int l = 0 ; l < finishedExporterList.size(); l++){
							exporterMap =new LinkedHashMap();
							for(String exmpMap : exporterMap.keySet()){
								if(!finishedExporterList.get(l).equalsIgnoreCase(exmpMap)){
									exporterMap.put(exmpMap, lastActivityOnTime.get(exmpMap));
								}	
							}// for closed (exmpMap)
							
						} // for loop closed (l)
					}else{
						if(exporterMap.size() > 0){
							for(String exporterNo : exporterMap.keySet()){
								System.out.println("Exporter "+ exporterMap.get(exporterNo) + " is not Exported Any Interactions,Please check configuration on VantageUI");	
							}
							
						}
					}
				} // for closed (f)
				
	//			 Copying all failed scenario files into location which is mention in xml parameter "failedDumpPath" , for Debugging Purpose.
				failedScenarioDumpPath = System.getProperty("failedDumpPath");
				for(String resultKey : result.keySet()){
					String sourceLocation = null;
					if(!result.get(resultKey)){
						
						String getScenarioName = CommonUtils.getScenarioName(scenarioName_TempleteName.get(resultKey));
						failedScenarioDumpPath = failedScenarioDumpPath+"/"+getScenarioName;
					
						sourceLocation = runEx_EmlLocation.get(resultKey);
	
						System.out.println("Start Dumping the all the File for failed Scanerio ");
//						create new directory for emlDumpPath
						CommonUtils.createDirectory(failedScenarioDumpPath);
//						copying all emls from SMTP to emlDumpPath with changed name (Conversation#interId).
						CommonUtils.changeFileNamesAndCopy(sourceLocation, failedScenarioDumpPath);
						System.out.println("Dumping Process is Done");
					}
//					delete all Data from emlDump(Data validation) Folder
					CommonUtils.deleteDir(sourceLocation);
				}
	//			resetting the All the Automation Exporters
				cleanUp(scenarioTemplete);
				
//				Delete All the data from SMTP Server Directory for all users(Take Code From Anurag) , Delete All folders which is inside test.com Folder
				String smtpEmlDumpPath = System.getProperty("smtpEmlsPath");
				
//				delete data from smtp dump folder for all users
				CommonUtils.deleteDir(smtpEmlDumpPath);
				System.out.println("Deleting the Files from SMTP Folder");
				
			} // for closed (i) outer Loop
			
	//		System.exit(1);		
		interactionExporter.closeBrowser();
			
		}catch(Exception e){
			System.out.println("Some Exception Occur, Please check the Stack Trace....");
			e.printStackTrace();
		}
	}
	
	
	public boolean ConfigureExporter(HashMap<String,String> map) throws SQLException
	{
		boolean flag = true;
		ArrayList<String> dbParams = new ArrayList();
		dbParams.add(System.getProperty("driverName"));
		dbParams.add(System.getProperty("dbName"));
		dbParams.add(System.getProperty("db_url"));
		dbParams.add(System.getProperty("user"));
		dbParams.add(System.getProperty("password"));
		
		
		flag = DBUtils.updateExporter(map, dbParams);
				return flag;
		
	}
	
	
	
	public void cleanUp(ArrayList<String> exporterTempleteNames)
	{
		try{
			for(String exporterTemplete : exporterTempleteNames){
				HashMap<String,String> map = CommonUtils.generateMap(System.getProperty("defaultExporterTempletePath"),exporterTemplete);
				System.out.println("Resetting the Exporter Templete : "+ exporterTemplete);
				
				ArrayList<String> dbParams = new ArrayList();
				dbParams.add(System.getProperty("driverName"));
				dbParams.add(System.getProperty("dbName"));
				dbParams.add(System.getProperty("db_url"));
				dbParams.add(System.getProperty("user"));
				dbParams.add(System.getProperty("password"));
				System.out.println(map);
				
					isExporterConfigured = ConfigureExporter(map);
					
					System.out.println("Clean up Block : Exporter Name : " + exporterTemplete);
					String exporterNumber = new Headers().getExporterNumber(exporterTemplete);
					DBUtils.resetExporterRecords(dbParams,exporterNumber);
			}
		}catch(Exception e){
				System.out.println("Error Occured During cleaning Exporter Template");
			}
			
//			CommonUtils.restartVantage(vantageIP, "180000");
	}
	
	
	
	@DataProvider
	public Object[][] dp() {
		String dirName = System.getProperty("testScenarioFolder");
		System.out.println("here1");
		System.out.println("dirname: " + dirName);
		File[] files = getArgs(dirName);
		Object[][] params = new Object[files.length][2];
		int count = 0;
		for (File file : files) {
			String tc = "TC000" + count;
			Object[] result = new Object[] { tc, file.getAbsolutePath() };
			System.out.println(result[1]);
			params[count] = result;
			count = count + 1;
			
		}
//		System.out.println(params);
//		System.out.println("Size of params : "+params.length);
//		System.out.println(params[0][0]);
//		System.out.println(params[0][1]);
		return params;
	}
	
	
	private File[] getArgs(String dirName) {
//		System.out.println("here2");
		File dir = new File(dirName);
		FilenameFilter fileNameFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {
					int lastIndex = name.lastIndexOf('.');
					String str = name.substring(lastIndex);
					if (str.equals(".properties")) {
						return true;
					}
				}
				return false;
			}
		};

		File[] files = dir.listFiles(fileNameFilter);
		return files;
	}
	
	
	
}

