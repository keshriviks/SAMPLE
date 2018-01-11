package com.actiance.test.sharepointNewApproach.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class TestUtilsLibraries {
	
	HashMap<String, HashMap<String, String>> testResultMap = new HashMap<>();
	
	public HashMap<String, CSVObjectsLibraries> getCSVObjects(String filePath) throws IOException{
		
		BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"utf-8"));
		String currentLine = null;
		boolean firstRow = true;
		String[] currentLineData;
		CSVObjectsLibraries obj;
		
		HashMap<String, String> InterIDAndResult;
		
		HashMap<String, CSVObjectsLibraries> objectsMap = new HashMap<>();
		
		while((currentLine = file.readLine())!=null){
			if(firstRow){
				firstRow = false;
				continue;
			}
			//System.out.println("Row: "+i++);
			obj = new CSVObjectsLibraries();
			
			currentLineData = currentLine.split(",");
			
			obj.setTestcase(currentLineData[0]);
			obj.setFeature(currentLineData[1]);
			obj.setType(currentLineData[2]);
			obj.setActions(currentLineData[3]);
			obj.setSubAction(currentLineData[4]);
			obj.setTime1(currentLineData[5]);
			obj.setTime2(currentLineData[6]);
			obj.setName(currentLineData[7]);
			obj.setTitle(currentLineData[8]);
			obj.setBody(currentLineData[9]);
			obj.setDescription(currentLineData[10]);
			obj.setKeyword(currentLineData[11]);
			obj.setComments(currentLineData[12]);
			obj.setAuthor(currentLineData[13]);
			obj.setSharedWithUsers(currentLineData[14]);
			obj.setDatePictureTaken(currentLineData[15]);
			obj.setCopyright(currentLineData[16]);
			obj.setLinkToVideo(currentLineData[17]);
			obj.setReportCategory(currentLineData[18]);
			obj.setReportStatus(currentLineData[19]);
			obj.setPageName(currentLineData[20]);
			obj.setPageTitle(currentLineData[21]);
			obj.setLocation(currentLineData[22]);
			obj.setLayout(currentLineData[23]);
			obj.setStatusIndicator(currentLineData[24]);
			obj.setUDCPurpose(currentLineData[25]);
			obj.setConnectionType(currentLineData[26]);
			obj.setFile(currentLineData[27]);
			obj.setFileType(currentLineData[28]);
			obj.setContentType(currentLineData[29]);
			obj.setContentSubType(currentLineData[30]);
			obj.setActionType(currentLineData[31]);
			obj.setResourceName(currentLineData[32]);
			obj.setResourceURL(currentLineData[33]);
			obj.setParentIdentifier(currentLineData[34]);
			obj.setTestScenario(currentLineData[35]);
			obj.setExecute(currentLineData[36]);
			
			//features.add(currentLineData[1]);
			if(obj.getExecute().equalsIgnoreCase("Yes")){
				objectsMap.put(currentLineData[0], obj);	
			}
			
			
			//Set Test Result Map
			InterIDAndResult = new HashMap<>();
			InterIDAndResult.put("InterID", "-1");
			InterIDAndResult.put("Result", "Fail");
			testResultMap.put(currentLineData[0], InterIDAndResult);
		}
		
		file.close();
		
		return objectsMap;
	}
	
	public HashMap<String, HashMap<String, String>> getTestResultMap(){
		return testResultMap;
	}
	
	public boolean endTest(Long startTime, Long endTime){
		if(endTime - startTime >= 3000){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean endTest(HashMap<String, HashMap<String, String>> testResultMap){
		for(String testCaseNo : testResultMap.keySet()){
			if(Integer.parseInt(testResultMap.get(testCaseNo).get("InterID")) == -1 ){
				return false;
			}
		}
		return true;
	}
	
	public  String extractString(String text, String value){
		int index = text.indexOf("\""+value+"\"");
		String tempText = text.substring(index+value.length()+3);
		return tempText.substring(0, tempText.indexOf("</attr>"));
	}
}
