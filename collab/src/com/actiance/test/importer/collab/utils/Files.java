package com.actiance.test.importer.collab.utils;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class Files {
	
	@XmlElement(name="File")
	ArrayList<File> file = new ArrayList<File>();
	
	
	public ArrayList<File> getFile() {
		return file;
	}

	public void setFile(ArrayList<File> file) {
		this.file = file;
	}

}
