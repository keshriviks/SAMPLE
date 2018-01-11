package com.actiance.test.sharepointNewApproachCustomField.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TestUtils {
	
	HashMap<String, HashMap<String, String>> testResultMap = new HashMap<>();
	
	public HashMap<String, TestDataObject> getCSVObjects(String filePath) throws IOException{
		
		BufferedReader file = new BufferedReader(new FileReader(filePath));
		String currentLine = null;
		boolean firstRow = true;
		String[] currentLineData;
		TestDataObject obj;
		
		HashMap<String, String> InterIDAndResult;
		HashMap<String, TestDataObject> ObjectsMap = new HashMap<>();
		
		while((currentLine = file.readLine())!=null){
			if(firstRow){
				firstRow = false;
				continue;
			}
			//System.out.println("Row: "+i++);
			obj = new TestDataObject();
			currentLineData = currentLine.split(",");
			obj.setTestCaseNo(currentLineData[0]);
			obj.setFeature(currentLineData[1]);
			obj.setType(currentLineData[2]);
			obj.setAction(currentLineData[3]);
			obj.setSubAction(currentLineData[4]);
			obj.setTime1(currentLineData[5]);
			obj.setTime2(currentLineData[6]);
			obj.setName(currentLineData[7]);
			obj.setTitle(currentLineData[8]);
			obj.setSubject(currentLineData[9]);
			obj.setBody(currentLineData[10]);
			obj.setDescription(currentLineData[11]);
			obj.setExpires(currentLineData[12]);
			obj.setAddress(currentLineData[13]);
			obj.setIsQuestion(currentLineData[14]);
			obj.setReply(currentLineData[15]);
			obj.setAssignedTo(currentLineData[16]);
			obj.setIssueStatus(currentLineData[17]);
			obj.setPriority(currentLineData[18]);
			obj.setCategory(currentLineData[19]);
			obj.setRelatedIssues(currentLineData[20]);
			obj.setComments(currentLineData[21]);
			obj.setStartDate(currentLineData[22]);
			obj.setDueDate(currentLineData[23]);
			obj.setuRL(currentLineData[24]);
			obj.setNotes(currentLineData[25]);
			obj.setPercentageCompletion(currentLineData[26]);
			obj.setQuestion(currentLineData[27]);
			obj.setAnswer(currentLineData[28]);
			obj.setIsResponseRequired(currentLineData[29]);
			obj.setEnforceUnique(currentLineData[30]);
			obj.setChoice1(currentLineData[31]);
			obj.setChoice2(currentLineData[32]);
			obj.setChoice3(currentLineData[33]);
			obj.setDisplayChoiceUsing(currentLineData[34]);
			obj.setFillIn(currentLineData[35]);
			obj.setDefaultValue(currentLineData[36]);
			obj.setResponce(currentLineData[37]);
			obj.setDocument(currentLineData[38]);
			obj.setData(currentLineData[39]);
			obj.setVersion(currentLineData[40]);
			obj.setDatePictureTaken(currentLineData[41]);
			obj.setKeywords(currentLineData[42]);
			obj.setPreviewImage(currentLineData[43]);
			obj.setTypethedescription(currentLineData[44]);
			obj.setReportDescription(currentLineData[45]);
			obj.setOwner(currentLineData[46]);
			obj.setReportCategory(currentLineData[47]);
			obj.setReportStatus(currentLineData[48]);
			obj.setUpload(currentLineData[49]);
			obj.setAuthor(currentLineData[50]);
			obj.setPreviewImageURL(currentLineData[51]);
			obj.setCopyRight(currentLineData[52]);
			obj.setCustomField1(currentLineData[53]);
			obj.setCustomField2(currentLineData[54]);
			obj.setFile(currentLineData[55]);
			obj.setFileType(currentLineData[56]);
			obj.setContentType(currentLineData[57]);
			obj.setContentSubType(currentLineData[58]);
			obj.setActionType(currentLineData[59]);
			obj.setResourceName(currentLineData[60]);
			obj.setResourceURL(currentLineData[61]);
			obj.setTestScenario(currentLineData[62]);
			
			//System.out.println(currentLineData.length);
			
			//features.add(currentLineData[1]);
			ObjectsMap.put(currentLineData[0], obj);
			
			//Set Test Result Map
			InterIDAndResult = new HashMap<>();
			InterIDAndResult.put("InterID", "-1");
			InterIDAndResult.put("Result", "Fail");
			testResultMap.put(currentLineData[0], InterIDAndResult);
		}
		file.close();
		
		return ObjectsMap;
	}
	
	public boolean endTest(Long startTime, Long endTime){
		if(endTime - startTime >= 1000){
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
	
	public HashMap<String, HashMap<String, String>> getTestResultMap(HashMap<String, TestDataObject> CSVObjectMap){
		return testResultMap;
	}
	
	public  String extractString(String text, String value){
		int index = text.indexOf("\""+value+"\"");
		String tempText = text.substring(index+value.length()+3);
		return tempText.substring(0, tempText.indexOf("</attr>"));
	}
}
