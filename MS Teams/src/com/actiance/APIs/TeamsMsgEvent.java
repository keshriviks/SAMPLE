package com.actiance.APIs;

/*
 * Copyright (c) 2017 Actiance Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Actiance
 * Inc. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Actiance.
 *
 * ACTIANCE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. ACTIANCE SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED
 * BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE
 * OR ITS DERIVATIVES.
 */



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;


/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsMsgEvent extends TeamsEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String messageId;
	private String roomId;
	private String roomType; // direct chat || channel
	private String roomTitle;
	private String personId;
	private String personEmail;
	private Date createdTime;
	private String eventType; // created || edited || deleted
	private LinkedHashSet<String> participantList;
	private String textContent;
	private String htmlContent;
	private String threadId;
	private ArrayList<TeamsFile> files;
	private ArrayList<String> mentionedPeople;
	//fields carrying metadata info for retry.
	private int retryCount;
	private boolean isFailed = false;
	private boolean isCompleteWithData = false;
	private String itemIdMetadata;
	private String mailMetadata;
	private String roomTypeMetadata;
	private TeamsUser[] groupMembersMetadata;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getRoomTitle() {
		return roomTitle;
	}

	public void setRoomTitle(String roomTitle) {
		this.roomTitle = roomTitle;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getPersonEmail() {
		return personEmail;
	}

	public void setPersonEmail(String personEmail) {
		this.personEmail = personEmail;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public LinkedHashSet<String> getParticipantList() {
		return participantList;
	}

	public void setParticipantList(LinkedHashSet<String> participantList) {
		this.participantList = participantList;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public ArrayList<TeamsFile> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<TeamsFile> files) {
		this.files = files;
	}

	public ArrayList<String> getMentionedPeople() {
		return mentionedPeople;
	}

	public void setMentionedPeople(ArrayList<String> mentionedPeople) {
		this.mentionedPeople = mentionedPeople;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
		result = prime * result + ((roomId == null) ? 0 : roomId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeamsMsgEvent other = (TeamsMsgEvent) obj;
		if (messageId == null) {
			if (other.messageId != null)
				return false;
		} else if (!messageId.equals(other.messageId))
			return false;
		if (roomId == null) {
			if (other.roomId != null)
				return false;
		} else if (!roomId.equals(other.roomId))
			return false;
		return true;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public boolean isFailed() {
		return isFailed;
	}

	public void setFailed(boolean isFailed) {
		this.isFailed = isFailed;
	}

	public boolean isCompleteWithData() {
		return isCompleteWithData;
	}

	public void setCompleteWithData(boolean isCompleteWithData) {
		this.isCompleteWithData = isCompleteWithData;
	}

	public void setError() {
		isFailed = true;
		retryCount++;
	}

	public String getRoomTypeMetadata() {
		return roomTypeMetadata;
	}

	public void setRoomTypeMetadata(String roomTypeMetadata) {
		this.roomTypeMetadata = roomTypeMetadata;
	}

	public TeamsUser[] getGroupMembersMetadata() {
		return groupMembersMetadata;
	}

	public void setGroupMembersMetadata(TeamsUser[] groupMembersMetadata) {
		this.groupMembersMetadata = groupMembersMetadata;
	}

	public String getMailMetadata() {
		return mailMetadata;
	}

	public void setMailMetadata(String mailMetadata) {
		this.mailMetadata = mailMetadata;
	}

	public String getItemIdMetadata() {
		return itemIdMetadata;
	}

	public void setItemIdMetadata(String itemIdMetadata) {
		this.itemIdMetadata = itemIdMetadata;
	}

	@Override
	public String toString() {
		return "TeamsMsgEvent [messageId=" + messageId + ", roomId=" + roomId + ", roomType=" + roomType
				+ ", roomTitle=" + roomTitle + ", personId=" + personId + ", personEmail=" + personEmail
				+ ", createdTime=" + createdTime + ", eventType=" + eventType + ", participantList=" + participantList
				+ ", textContent=" + textContent + ", htmlContent=" + htmlContent + ", threadId=" + threadId + ", files=" + files
				+ ", mentionedPeople=" + mentionedPeople + ", retryCount=" + retryCount + ", isFailed=" + isFailed
				+ ", isCompleteWithData=" + isCompleteWithData + ", itemIdMetadata=" + itemIdMetadata
				+ ", mailMetadata=" + mailMetadata + ", roomTypeMetadata=" + roomTypeMetadata
				+ ", groupMembersMetadata=" + Arrays.toString(groupMembersMetadata) + "]";
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

}
