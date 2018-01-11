package com.actiance.test.importer.collab.utils;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
@XmlAccessorType(XmlAccessType.FIELD)
public class ContentEvent {

	@XmlElement(name = "ObjectID")
	private String objectID;

	@XmlElement(name = "ObjectURI")
	private String objectURI;

	@XmlElement(name = "CorrelationID")
	private String correlationID;

	@XmlElement(name = "EventTime")
	private Long eventTime;

	@XmlElement(name = "ContentType")
	private String contentType;

	@XmlElement(name = "ContentSubType")
	private String contentSubType;

	@XmlElement(name = "Action")
	private String action;

	@XmlElement(name = "UserID")
	private String userID;

	@XmlElement(name = "ResourceName")
	private String resourceName;

	@XmlElement(name = "ResourceURI")
	private String resourceURI;

	@XmlElement(name = "ResourceID")
	private String resourceID;

	@XmlElement(name = "Title")
	private String title;

	@XmlElement(name = "ContentText")
	private ContentText contentText;

	@XmlElement(name = "EmployeeInfo")
	private EmployeeInfo employeeInfo;

	@XmlElement(name = "Attributes")
	private Attributes attributes;

	@XmlElement(name = "Files")
	private Files files;

	@XmlElement(name = "Participants")
	private Participants participants;

	public String getObjectID() {
		return objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public String getObjectURI() {
		return objectURI;
	}

	public void setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}

	public String getCorrelationID() {
		return correlationID;
	}

	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}

	public Long getEventTime() {
		return eventTime;
	}

	public void setEventTime(Long eventTime) {
		this.eventTime = eventTime;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentSubType() {
		return contentSubType;
	}

	public void setContentSubType(String contentSubType) {
		this.contentSubType = contentSubType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceURI() {
		return resourceURI;
	}

	public void setResourceURI(String resourceURI) {
		this.resourceURI = resourceURI;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ContentText getContentText() {
		return contentText;
	}

	public void setContentText(ContentText contentText) {
		this.contentText = contentText;
	}

	public EmployeeInfo getEmployeeInfo() {
		return employeeInfo;
	}

	public void setEmployeeInfo(EmployeeInfo employeeInfo) {
		this.employeeInfo = employeeInfo;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public Files getFiles() {
		return files;
	}

	public void setFiles(Files files) {
		this.files = files;
	}

	public Participants getParticipants() {
		return participants;
	}

	public void setParticipants(Participants participants) {
		this.participants = participants;
	}

}
