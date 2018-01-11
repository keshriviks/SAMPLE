package com.actiance.test.sharepointNewApproach.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.facetime.ftcore.util.Base64;

public class TestUtilsAdditionalContentType {
	
	HashMap<String, HashMap<String, String>> testResultMap = new HashMap<>();
	
	public HashMap<String, CSVObjectAddtionalAndCustomContentType> getCSVObjects(String filePath) throws IOException{
		
		BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"utf-8"));
		String currentLine = null;
		boolean firstRow = true;
		String[] currentLineData;
		CSVObjectAddtionalAndCustomContentType obj;
		
		HashMap<String, String> InterIDAndResult;
		
		HashMap<String, CSVObjectAddtionalAndCustomContentType> objectsMap = new HashMap<>();
		
		while((currentLine = file.readLine())!=null){
			if(firstRow){
				firstRow = false;
				continue;
			}
			//System.out.println("Row: "+i++);
			obj = new CSVObjectAddtionalAndCustomContentType();
			currentLineData = currentLine.split(",");
			
			obj.setTestCaseNo(currentLineData[0]);
			obj.setType(currentLineData[1]);
			obj.setTime1(currentLineData[2]);
			obj.setTime2(currentLineData[3]);
			obj.setName(currentLineData[4]);
			obj.setTitle(currentLineData[5]);
			obj.setBody(currentLineData[6]);
			obj.setDescription(currentLineData[7]);
			obj.setField1(currentLineData[8]);
			obj.setField2(currentLineData[9]);
			obj.setField3(currentLineData[10]);
			obj.setField4(currentLineData[11]);
			obj.setField5(currentLineData[12]);
			obj.setField6(currentLineData[13]);
			obj.setField7(currentLineData[14]);
			obj.setField8(currentLineData[15]);
			obj.setField9(currentLineData[16]);
			obj.setField10(currentLineData[17]);
			obj.setField11(currentLineData[18]);
			obj.setField12(currentLineData[19]);
			obj.setField13(currentLineData[20]);
			obj.setField14(currentLineData[21]);
			obj.setField15(currentLineData[22]);
			obj.setField16(currentLineData[23]);
			obj.setField17(currentLineData[24]);
			obj.setField18(currentLineData[25]);
			obj.setField19(currentLineData[26]);
			obj.setField20(currentLineData[27]);
			obj.setField21(currentLineData[28]);
			obj.setField22(currentLineData[29]);
			obj.setField23(currentLineData[30]);
			obj.setField24(currentLineData[31]);
			obj.setField25(currentLineData[32]);
			obj.setFileName(currentLineData[33]);
			obj.setFileType(currentLineData[34]);
			obj.setParentIdentifier(currentLineData[35]);
			obj.setContentType(currentLineData[36]);
			obj.setContentSubType(currentLineData[37]);
			obj.setActionType(currentLineData[38]);
			obj.setResourceName(currentLineData[39]);
			obj.setResourceURL(currentLineData[40]);
			obj.setTestcaseScenario(currentLineData[41]);
			obj.setExecute(currentLineData[42]);
			
			
			
			
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
		if(endTime - startTime >= 80){
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
	
	public String extractString(String text, String value){
		int index = text.indexOf("\""+value+"\"");
		String tempText = text.substring(index+value.length()+3);
		return tempText.substring(0, tempText.indexOf("</attr>"));
	}
	

}

