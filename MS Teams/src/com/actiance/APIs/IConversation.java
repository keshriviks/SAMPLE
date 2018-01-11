package com.actiance.APIs;

/*
 * Copyright (c) 2012 Actiance, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of FaceTime
 * Communications, Inc. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with FaceTime.
 *
 * FACETIME MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. FACETIME SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED
 * BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE
 * OR ITS DERIVATIVES.
 */


/**
 * Interface that defines standard methods for handling conversation related events. 
 * See ConversationMessageProcessor.java for implementation. 
 * 
 * @author MAdnan
 *
 */
public interface IConversation {
	
	public static String SVC = "IConversation";
	Response sessionStart(SessionEvent sessionEvent);
	void sessionStarted(SessionEvent sessionEvent);
    Response sessionEnd(SessionEvent sessionEvent);
    void sessionEnded(SessionEvent sessionEvent);
	Response conversationStart(ConversationEvent conversationEvent);
	void conversationStarted(ConversationEvent conversationEvent);
	Response conversationJoin(ConversationEvent conversationEvent);
	void conversationJoined(ConversationEvent conversationEvent);
	Response conversationLeave(ConversationEvent conversationEvent);
	void conversationLeft(ConversationEvent conversationEvent);
	Response conversationEnd(ConversationEvent conversationEvent);
	void conversationEnded(ConversationEvent conversationEvent);
	Response messageSend(MessageEvent messageEvent);
	void messageSent(MessageEvent messageEvent);
	Response fileSend(FileEvent fileEvent);
	void fileSent(FileEvent fileEvent);
}
