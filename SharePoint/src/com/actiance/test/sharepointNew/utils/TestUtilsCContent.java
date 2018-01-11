package com.actiance.test.sharepointNew.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestUtilsCContent {
public HashMap<String, ArrayList<TestDataObjectCContent>> getTestObjects(String file){
		
		LinkedHashMap<String,TestDataObjectCContent> csvMap = new LinkedHashMap<String, TestDataObjectCContent>();
		HashMap<String, ArrayList<TestDataObjectCContent>> featuresMap = new HashMap<String, ArrayList<TestDataObjectCContent>>();
		HashSet<String> features = new HashSet<String>();
		String line = "";
		TestDataObjectCContent obj;
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null){
				String[] values = line.split(",");
				obj = new TestDataObjectCContent();
				obj.setTestCaseID(values[0]);
				obj.setFeature(values[1]);
				obj.setType(values[2]);
				obj.setAction(values[3]);
				obj.setFeatureName(System.getProperty("listName"));
				
				obj.setItemTitle(values[5]);
				obj.setItemDesc(values[6]);
				obj.setParam1(values[7]);
				obj.setParam2(values[8]);
				obj.setParam3(values[9]);
				obj.setParam4(values[10]);
				obj.setParam5(values[11]);
				obj.setParam6(values[12]);
				obj.setParam7(values[13]);
				obj.setParam8(values[14]);
				obj.setParam9(values[15]);
				obj.setParam10(values[16]);
				obj.setParam11(values[17]);
				obj.setParam12(values[18]);
				obj.setParam13(values[19]);
				obj.setParam14(values[20]);
				obj.setParam15(values[21]);
				obj.setParam16(values[22]);
				obj.setParam17(values[23]);
				obj.setParam18(values[24]);
				obj.setParam19(values[25]);
				obj.setParam20(values[26]);
				obj.setContentType(values[27]);
				obj.setContentSubType(values[28]);
				obj.setResourceName(System.getProperty("listName"));
				obj.setTestDesc(values[30]);
				obj.setExecute(values[31]);
				
				
				features.add(values[1]);
				csvMap.put(values[0], obj);
				
			}
		}catch(Exception e){
			System.out.println("Error while reading CSV File");
			e.printStackTrace();
		}finally{
			try{
				if(br != null){
					br.close();
				}
			}catch(Exception e){
				System.out.println("Error while closing the file connection");
				e.printStackTrace();
			}
		}
		
		//Devide by features
		for(String str : features){
			ArrayList<TestDataObjectCContent> objects = new ArrayList<TestDataObjectCContent>();
			for(Map.Entry<String, TestDataObjectCContent> entry : csvMap.entrySet()){
				if(str.equals(entry.getValue().getFeature())){
					objects.add(entry.getValue());
				}
			}
			featuresMap.put(str, objects);
		}
		return featuresMap;
	}


	public  String extractString(String sData, String extractPart){
	    String value = null;
	    int indexPart = sData.indexOf("\""+extractPart+"\">");
	    String localString= sData.substring(indexPart);
	    int newIndex = extractPart.length()+3;
	    int index1 = localString.indexOf("</attr");
	    value = localString.substring(newIndex,index1);
	    return value;
	}
}
