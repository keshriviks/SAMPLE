package com.actiance.test.sharepointNew.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;



public class TestUtils {
	
	public HashMap<String, ArrayList<TestDataObject>> getTestObjects(String file){
		
		LinkedHashMap<String,TestDataObject> csvMap = new LinkedHashMap<String, TestDataObject>();
		HashMap<String, ArrayList<TestDataObject>> featuresMap = new HashMap<String, ArrayList<TestDataObject>>();
		HashSet<String> features = new HashSet<String>();
		String line = "";
		TestDataObject obj;
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			while((line = br.readLine()) != null){
				String[] values = line.split(",");
				obj = new TestDataObject();
				obj.setTestCaseID(values[0]);
				obj.setFeature(values[1]);
				obj.setType(values[2]);
				obj.setAction(values[3]);
				obj.setSubAction(values[4]);
				obj.setTenant(System.getProperty("siteURL")+values[5]);
				obj.setUserName(System.getProperty("username"));
				obj.setPassword(System.getProperty("userPassword"));
				obj.setSiteTitle(values[8]);
				obj.setSiteDescription(values[9]);
				obj.setAttachment(values[10]);
				obj.setExpiry(values[11]);
				obj.setItemTitle(values[12]);
				obj.setItemDescription(values[13]);
				obj.setParam3(values[14]);
				obj.setParam4(values[15]);
				obj.setParam5(values[16]);
				obj.setParam6(values[17]);
				obj.setParam7(values[18]);
				obj.setParam8(values[19]);
				obj.setParam9(values[20]);
				obj.setParam10(values[21]);
				obj.setContentType(values[22]);
				obj.setContentSubType(values[23]);
				obj.setActionType(values[24]);
				obj.setResourceName(values[25]);
				obj.setExecute(values[26]);
				
				
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
			ArrayList<TestDataObject> objects = new ArrayList<TestDataObject>();
			for(Map.Entry<String, TestDataObject> entry : csvMap.entrySet()){
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
	
	public String addDaysInCurrentDate(int days){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, days);
		Date d = cal.getTime();
		DateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
		return newFormat.format(d)+"";
	}
	
	/*public static void main(String[] args) throws ParseException{
		TestUtils ut = new TestUtils();
		System.out.println(ut.addDayInCurrentDate());
	}*/
}
