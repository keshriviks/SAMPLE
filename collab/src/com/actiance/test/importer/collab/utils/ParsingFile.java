package com.actiance.test.importer.collab.utils;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class ParsingFile {
//
//	public static ContentEvents getContentEvents(String fileName) {
//
//		String fileLocation = System.getProperty("folderLocation"
//				+ "//processed//" + fileName);
//		File file = new File(fileLocation);
//		ContentEvents content = new ContentEvents();
//
//		try {
//
//			JAXBContext context = JAXBContext.newInstance(ContentEvents.class);
//			Unmarshaller unmarshall = context.createUnmarshaller();
//
//			content = (ContentEvents) unmarshall.unmarshal(file);
//			// System.out.println(content.getContentEvent().size());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return content;
//
//	}
	
	public static ContentEvents parseFile(String fileName) {

		File file = new File(System.getProperty("folderLocation")+"/"+ fileName);
		ContentEvents content = null;
		
		try {

			JAXBContext context = JAXBContext.newInstance(ContentEvents.class);
			Unmarshaller unmarshall = context.createUnmarshaller();
			content = (ContentEvents) unmarshall.unmarshal(file);
			
		} catch (Exception e) {
			System.out.println("Error while Parsing file");
			e.printStackTrace();
		}

		return content;
	}
}
