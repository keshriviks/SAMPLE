package com.actiance.test.importer.collab.utils;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ContentEvents")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContentEvents {

	@XmlElement(name="ContentEvent")
	private ArrayList<ContentEvent> contents;

	public ContentEvents() {
		super();
	}

	public ContentEvents(ArrayList<ContentEvent> contents) {
		super();
		this.contents = contents;
	}

	public ArrayList<ContentEvent> getContentEvent() {
		return contents;
	}

	public void setContentEvent(ArrayList<ContentEvent> contents) {
		this.contents = contents;
	}

}
