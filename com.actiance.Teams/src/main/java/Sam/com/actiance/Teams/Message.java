package Sam.com.actiance.Teams;

import java.util.ArrayList;

public class Message {

	String sender;
	String message;
	ArrayList<String> receiver = new ArrayList<String>();
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ArrayList<String> getReceiver() {
		return receiver;
	}
	public void setReceiver(ArrayList<String> receiver) {
		this.receiver = receiver;
	}
}


