package com.actiance.test.importer.collab.utils;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class FileDownloadProperties {
	
	@XmlElement(name="property")
	ArrayList<Property> property = null;

	public ArrayList<Property> getProperty() {
		return property;
	}

	public void setProperty(ArrayList<Property> property) {
		this.property = property;
	}
	
	
	
	
	

}
