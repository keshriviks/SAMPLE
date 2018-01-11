package com.actiance.test.exporterNew.tests;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.actiance.test.exporterNew.utils.CollabProcessing;
import com.actiance.test.exporterNew.utils.CommonUtils;
import com.actiance.test.exporterNew.utils.DBUtils;
import com.actiance.test.exporterNew.utils.Headers;
import com.actiance.test.exporterNew.utils.IMProcessing;

public class LocalByPassTest {

	public static void main(String... s) throws Exception{
		String filePath = "D:\\VijayTest\\Test";
		CollabProcessing cp = new CollabProcessing();
		DBUtils db = new DBUtils(getDBParams());
		IMProcessing ip = new IMProcessing();
//		Get Headers From EMLs
		HashMap<Integer,HashMap<String,String>> headers = null;//getHeaders(filePath);

		ArrayList<String> dbParams = getDBParams();
		ArrayList<Integer> interIDs = RecursiveFileDumpRename.changeFileName(filePath);
		System.out.println(interIDs);
		HashMap<String,String> inputProps = new HashMap();
//		
		Statement stmt = DBUtils.getConnection(dbParams).createStatement();
		
		System.out.println("Statement Created"); 
		
/*		if(stmt != null){
			System.out.println("Statement got Created");
		}
		*/
		
		
		
//		CommonUtils commonUtils = new CommonUtils();
//		commonUtils.getEmlFromJournalEmail(filePath);
	
// Start Collab	for TXT	

/*		HashMap<Integer,String> cpEMLMapTXT = cp.getMessage(filePath);
		System.out.println("EML Map : "+ cpEMLMapTXT);
		
		HashMap<Integer,HashMap<String,String>> dnMapCPTxt = db.getMessageDataForCollabForText(interIDs);
		boolean validation1 = cp.verifyTextFormatForCollab(dnMapCPTxt, cpEMLMapTXT, "V1", inputProps, headers, "exporter.1.");
		
		System.out.println(validation1);
*/
// end Collab	
		
//Start Collab for XMLs
/*		
		HashMap<Integer,HashMap<String,String>> emlMap = cp.getXMLDataForCollabFromEML(filePath);
		
		System.out.println(emlMap.size());
		
		HashMap<Integer,HashMap<String,String>> dbMap = db.getXMLMessageDataForCollabFromDB(interIDs);
		
		System.out.println(dbMap.size()); 
		
		boolean validation = cp.verifyXMLForCollab(emlMap, dbMap, "X3", inputProps, headers, "imexport.1");
		
		System.out.println(validation);*/
//		Default Format Tested		
//		Generate Text Version Data From Emls
		/*
		HashMap<Integer,HashMap<String,String>> cpEmlMapHTML = cp.getHTMLContentForCollab(filePath);
		System.out.println("EML Map : "+ cpEmlMapHTML);
*/
	
	
	/*	HashMap<Integer,HashMap<String,String>> cpEmlMapXML = cp.getXMLDataForCollabFromEML(filePath);
		System.out.println("EML Map : "+  cpEmlMapXML);
	*/
//		Generate Map Data From DB ....
		
//		HashMap<Integer,HashMap<String,String>> dbMapCPHTML = db.getMessageDataOfCollabForHTML(interIDs);
		
		
		
		
//		System.out.println("DB map : "+ dnMapCPTxt);
//		HashMap<Integer,HashMap<String,String>> dbMapCPXML = db.getXMLMessageDataForCollabFromDB(interIDs);
//		System.out.println("DB Map : "+ dbMapCPXML);
//		System.out.println("DB MAP : "+ dbMap);
//		boolean validation = CommonUtils.verifyTextFormatForCollab(dbMap, emlMap, "V1", inputProps, headers, "exporter.5.");
//		boolean validation1 = ip.verifyTextFormatData(dbMap, emlMap, inputProps, headers, "export.6.");
		
		
		
		
//		boolean validation1 = cp.verifyXMLForCollab(cpEmlMapXML, dbMapCPXML, "X3", inputProps, headers, "exporter.1.");
//		boolean validation1 = cp.verifyHTMLContentForCollab(dbMapCPHTML, cpEmlMapHTML, inputProps, headers, "exporter.5.");
//		boolean validation2 = CommonUtils.verifyHTMLContent(dbMap, emlMap, inputProps, headers, "exporter.5.");
		
/*		for(Integer in : cp.getFailedId().keySet()){
			System.out.println("Inter Id : "+ in);
//			System.out.println(cp.getFailedId().get(in));
		}*/
//		System.out.pri7ntln(validation1);
				
//		XSLT Verification for Collab
		
/*		
		ParsingXSLTs parse = new ParsingXSLTs();
		
		HashMap<Integer,HashMap<String,Strin	g>> xsltCPHTML = parse.parseHTML_XSLT_Collab(filePath);
		System.out.println("EML Map : " + xsltCPHTML);
		
		HashMap<Integer,HashMap<String,String>> dbMapCPHTML = db.getMessageDataOfCollabForHTML(interIDs);
		
		System.out.println("DB Map : "+ dbMapCPHTML);
		boolean validation1 = cp.verifyHTMLContentForCollab(dbMapCPHTML, xsltCPHTML, inputProps, headers, "exporter.1.");
		
		System.out.println(validation1);
		*/
	// IM Test
//	
//		HashMap<Integer,String> emlMapTXT = ip.getMessage(filePath);
//		System.out.println(emlMapTXT);
//		
	
// Start IM

		HashMap<Integer,String> emlMapTXT = ip.getMessage(filePath);
		System.out.println(emlMapTXT);
		HashMap<Integer,HashMap<String,ArrayList<String>>> dbMap = db.getMessageData(interIDs, dbParams);
		System.out.println(dbMap);
//		
		boolean validation = ip.verifyTextFormatData(dbMap, emlMapTXT, inputProps, headers, "exporter.6.");
//		
		System.out.println(validation);
	
		
//		System.out.println("\n\n\n");
//		System.out.println("*********************Result *********************");
//		
//		for(Integer in : ip.getFailedId().keySet()){
//			System.out.println("Inter Id : "+ in);
//			System.out.println(ip.getFailedId().get(in));
//		}
//		
//		System.out.println(validation);

		
	}
	
	public static HashMap<Integer,HashMap<String,String>> getHeaders(String emlFilePath)throws Exception{
		Headers he = new Headers();
		HashMap<Integer,HashMap<String,String>> headers = new HashMap<>();
		headers = he.getHeaderMapFromEml(emlFilePath);
		return headers;
				
	}
	
	public static ArrayList<String> getDBParams(){
		   ArrayList<String> db = new ArrayList();
		   db.add("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		   db.add("Anoop_R3_16032");
//		   db.add("Sant_15244_Externalization");
		   db.add("jdbc:sqlserver://192.168.116.215:1433");
//		   db.add("jdbc:sqlserver://192.168.116.215:1433");
		   db.add("sa");
//		   db.add("facetime");
		   db.add("FaceTime@123");
		   return db;
	}
}

