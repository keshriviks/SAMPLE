package com.actiance.test.importer.collab.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class File {

	@XmlElement(name = "FileID")
	private String fileID;

	@XmlElement(name = "FileName")
	private String fileName;

	@XmlElement(name = "MimeType")
	private String mimeType;
	
	@XmlElement(name ="RemoteFileData")
	private RemoteFileData remoteData;

	@XmlElement(name ="Attributes")
	private Attributes attributes;
	
	
	public RemoteFileData getRemoteData() {
		return remoteData;
	}

	public void setRemoteData(RemoteFileData remoteData) {
		this.remoteData = remoteData;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
