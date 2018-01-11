package com.actiance.test.importer.collab.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {

	public String extractFromCDATA(String data) {
		String sdata;
		int indexC = data.indexOf("CDATA[");
		int indexL = data.lastIndexOf("]]>");
		sdata = data.substring(indexC + 6, indexL);

		return sdata;

	}
	
	private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");

	public static String removeTags(String string) {
	    if (string == null || string.length() == 0) {
	        return string;
	    }

	    Matcher m = REMOVE_TAGS.matcher(string);
	    return m.replaceAll("");
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
