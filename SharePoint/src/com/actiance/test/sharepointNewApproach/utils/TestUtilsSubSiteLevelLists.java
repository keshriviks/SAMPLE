package com.actiance.test.sharepointNewApproach.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class TestUtilsSubSiteLevelLists {
	
	HashMap<String, HashMap<String, String>> testResultMap = new HashMap<>();
	
	public HashMap<String, CSVSubSiteLevelLists> getCSVObjects(String filePath) throws IOException{
		
		BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"utf-8"));
		String currentLine = null;
		boolean firstRow = true;
		String[] currentLineData;
		CSVSubSiteLevelLists obj;
		
		HashMap<String, String> InterIDAndResult;
		
		HashMap<String, CSVSubSiteLevelLists> objectsMap = new HashMap<>();
		
		while((currentLine = file.readLine())!=null){
			if(firstRow){
				firstRow = false;
				continue;
			}
			//System.out.println("Row: "+i++);
			obj = new CSVSubSiteLevelLists();
			currentLineData = currentLine.split(",");
			
			obj.setTestCaseNo(currentLineData[0]);
			obj.setFeature(currentLineData[1]);
			obj.setType(currentLineData[2]);
			obj.setMethodType(currentLineData[3]);
			obj.setAction(currentLineData[4]);
			obj.setSubAction(currentLineData[5]);
			obj.setTime1(currentLineData[6]);
			obj.setTime2(currentLineData[7]);
			obj.setName(currentLineData[8]);
			obj.setTitle(currentLineData[9]);
			obj.setSubject(currentLineData[10]);
			obj.setBody(currentLineData[11]);
			obj.setDescription(currentLineData[12]);
			obj.setExpires(currentLineData[13]);
			obj.setPublish(currentLineData[14]);
			obj.setAddress(currentLineData[15]);
			obj.setIsQuestion(currentLineData[16]);
			obj.setReply(currentLineData[17]);
			obj.setAssignedTo(currentLineData[18]);
			obj.setIssueStatus(currentLineData[19]);
			obj.setPriority(currentLineData[20]);
			obj.setCategory(currentLineData[21]);
			obj.setRelatedIssues(currentLineData[22]);
			obj.setComments(currentLineData[23]);
			obj.setStartDate(currentLineData[24]);
			obj.setDueDate(currentLineData[25]);
			obj.setuRL(currentLineData[26]);
			obj.setNotes(currentLineData[27]);
			obj.setPercentageCompletion(currentLineData[28]);
			obj.setQuestion(currentLineData[29]);
			obj.setAnswer(currentLineData[30]);
			obj.setIsResponseRequired(currentLineData[31]);
			obj.setEnforceUnique(currentLineData[32]);
			obj.setChoice1(currentLineData[33]);
			obj.setChoice2(currentLineData[34]);
			obj.setChoice3(currentLineData[35]);
			obj.setDisplayChoiceUsing(currentLineData[36]);
			obj.setFillIn(currentLineData[37]);
			obj.setDefaultValue(currentLineData[38]);
			obj.setResponce(currentLineData[39]);
			obj.setDocument(currentLineData[40]);
			obj.setData(currentLineData[41]);
			obj.setVersion(currentLineData[42]);
			obj.setDatePictureTaken(currentLineData[43]);
			obj.setKeywords(currentLineData[44]);
			obj.setPreviewImage(currentLineData[45]);
			obj.setTypethedescription(currentLineData[46]);
			obj.setReportDescription(currentLineData[47]);
			obj.setOwner(currentLineData[48]);
			obj.setReportCategory(currentLineData[49]);
			obj.setReportStatus(currentLineData[50]);
			obj.setUpload(currentLineData[51]);
			obj.setAuthor(currentLineData[52]);
			obj.setPreviewImageURL(currentLineData[53]);
			obj.setCopyRight(currentLineData[54]);
			obj.setFile(currentLineData[55]);
			obj.setFileType(currentLineData[56]);
			obj.setContentType(currentLineData[57]);
			obj.setContentSubType(currentLineData[58]);
			obj.setActionType(currentLineData[59]);
			obj.setResourceName(currentLineData[60]);
			obj.setResourceURL(currentLineData[61]);
			obj.setTestScenario(currentLineData[62]);
			
			//features.add(currentLineData[1]);
			objectsMap.put(currentLineData[0], obj);
			
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
