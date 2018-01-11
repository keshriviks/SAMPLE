package com.actiance.test.importer.collab.utils;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class Attributes {
	
	@XmlElement(name="Attribute")
	ArrayList<Attribute> attribute = new ArrayList();


	public ArrayList<Attribute> getAttribute() {
		return attribute;
	}

	public void setAttribute(ArrayList<Attribute> attribute) {
		this.attribute = attribute;
	}

}
