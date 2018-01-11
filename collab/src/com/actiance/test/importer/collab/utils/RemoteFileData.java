package com.actiance.test.importer.collab.utils;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class RemoteFileData {
	@XmlElement(name ="FileDownloadProperties")
	ArrayList<FileDownloadProperties> fileDownloadProperties =null;

	public ArrayList<FileDownloadProperties> getFileDownloadProperties() {
		return fileDownloadProperties;
	}

	public void setFileDownloadProperties(
			ArrayList<FileDownloadProperties> fileDownloadProperties) {
		this.fileDownloadProperties = fileDownloadProperties;
	}
	
	
	

}
