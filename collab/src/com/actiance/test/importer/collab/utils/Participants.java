package com.actiance.test.importer.collab.utils;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class Participants {

	@XmlElement(name = "Participant")
	private ArrayList<Participant> participant;

	public ArrayList<Participant> getParticipant() {
		return participant;
	}

	public void setParticipant(ArrayList<Participant> participant) {
		this.participant = participant;
	}

}
