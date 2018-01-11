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



import javax.xml.bind.annotation.XmlRootElement;

/**
 * Event handler response.
 *
 * @author madnan
 */
@XmlRootElement
public class Response {
	
	private int reasonCode_;
    private String policyMsg_;
    private String action_;
    
	public Response() {
        this(ReasonCode.SUCCESS);
    }        

    public enum ReasonCode {
        SUCCESS(0, "success"), NETWORK_NOT_SUPPORTED(1, "networkNotSupported"), PERMISSION_DENIED(2, "permissionDenied"), 
        FAILED_TO_START_SESSION(3, "failedToStartSession"), FAILED_TO_START_CONVERSATION(4, "failedToStartConversation"), 
        FAILED_TO_CREATE_ROOM(5, "failedToCreateRoom"), NOT_ALLOWED_TO_CREATE_ROOM(6, "notAllowedToCreateroom"),
        CONVERSATION_NOT_FOUND(7, "conversationNotFound"), REASON_DB_ERROR(8, "DBError"), REASON_SERVER_MEMORY_TOO_LOW(9, "serverMemoryTooLow"), 
        REASON_SYSTEM_ERROR(10, "systemError"), REASON_SERVICE_FAILURE(11, "serviceFailure"), POLICY_DO_NOT_LOG(12, "DoNotLog"), POLICY_LOG(13, "log"),
        FILE_PROCESSING_FAILED(14, "fileProcessingFailed"), COLLAB_PROCESSING_FAILED(15, "collabProcessingFailed"), 
        AUTHORIZATION_FAILED(16, "authorizationFailed"), AUTHORIZATION_FAILED_BAD_REQUEST(17, "authorizationFailed-badRequest"), 
        CHALLENGE_BLOCKED(18, "challenge blocked"), FAILED_TO_END_SESSION(19, "failedToEndSession"), FAILED_TO_END_CONVERSATION(20, "failedToEndConversation"),
        CONVERSATION_PROCESSING_FAILED(21, "conversationProcessingFailed");
        
        private int value;
        private String reasonDesc;
        
        private ReasonCode(int value, String reasonDesc) {
        	this.value = value;
        	this.reasonDesc = reasonDesc;
        }
        
        public int getValue() {
        	return this.value;
        }
        
        public String getReasonDesc() {
        	return this.reasonDesc;
        }        
        
    }

    /*public Response SUCCESS = new Response(ReasonCode.SUCCESS);
    public Response NETWORK_NOT_SUPPORTED = new Response(ReasonCode.NETWORK_NOT_SUPPORTED);
    public Response PERMISSION_DENIED = new Response(ReasonCode.PERMISSION_DENIED);
    public Response FAILED_TO_START_SESSION = new Response(ReasonCode.FAILED_TO_START_SESSION);
    public Response FAILED_TO_START_CONVERSATION = new Response(ReasonCode.FAILED_TO_START_CONVERSATION);
    public Response FAILED_TO_CREATE_ROOM = new Response(ReasonCode.FAILED_TO_CREATE_ROOM);
    public Response NOT_ALLOWED_TO_CREATE_ROOM = new Response(ReasonCode.NOT_ALLOWED_TO_CREATE_ROOM);
    public Response CONVERSATION_NOT_FOUND = new Response(ReasonCode.CONVERSATION_NOT_FOUND);
    public Response DB_ERROR = new Response(ReasonCode.REASON_DB_ERROR);
    public Response SERVER_MEMORY_TOO_LOW = new Response(ReasonCode.REASON_SERVER_MEMORY_TOO_LOW);
    public Response REASON_SYSTEM_ERROR = new Response(ReasonCode.REASON_SYSTEM_ERROR);
    public Response REASON_SERVICE_FAILURE = new Response(ReasonCode.REASON_SERVICE_FAILURE);
    public Response POLICY_DO_NOT_LOG = new Response(ReasonCode.POLICY_DO_NOT_LOG);
    public Response POLICY_LOG = new Response(ReasonCode.POLICY_LOG);  
    public Response FILE_PROCESSING_FAILED = new Response(ReasonCode.FILE_PROCESSING_FAILED);  
    public Response COLLAB_PROCESSING_FAILED = new Response(ReasonCode.COLLAB_PROCESSING_FAILED);
    public Response AUTHORIZATION_FAILED = new Response(ReasonCode.AUTHORIZATION_FAILED, "401");
    public Response AUTHORIZATION_FAILED_BAD_REQUEST = new Response(ReasonCode.AUTHORIZATION_FAILED, "400");
    public Response CHALLENGE_BLOCKED = new Response(ReasonCode.CHALLENGE_BLOCKED);
    public Response FAILED_TO_END_SESSION = new Response(ReasonCode.FAILED_TO_END_SESSION);  
    public Response FAILED_TO_END_CONVERSATION = new Response(ReasonCode.FAILED_TO_END_CONVERSATION);
    public Response CONVERSATION_PROCESSING_FAILED = new Response(ReasonCode.CONVERSATION_PROCESSING_FAILED);*/
       

    public Response(ReasonCode reasonCode) {
        this(reasonCode, "", reasonCode.getReasonDesc());       

    }

    public Response(ReasonCode reasonCode, String policyMsg) {
        this(reasonCode, policyMsg, reasonCode.getReasonDesc());
    }

    public Response(ReasonCode reasonCode, String policyMsg, String action) {
        reasonCode_ = reasonCode.getValue();
        policyMsg_ = policyMsg;
        action_ = action;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action_ == null) ? 0 : action_.hashCode());
		result = prime * result + reasonCode_;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (null == obj)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Response other = (Response) obj;
		if (action_ == null) {
			if (other.action_ != null)
				return false;
		} else if (!action_.equals(other.action_))
			return false;
		
		if (reasonCode_ != other.reasonCode_)
			return false;
		
		return true;
	}

	public int getReasonCode() {
        return reasonCode_;
    }

    public String getPolicyMessage() {
        return policyMsg_;
    }

    public String getAction() {
        return action_;
    }

	public void setReasonCode(int reasonCode_) {
		this.reasonCode_ = reasonCode_;
	}

	public void setPolicyMessage(String policyMsg_) {
		this.policyMsg_ = policyMsg_;
	}

	public void setAction(String action_) {
		this.action_ = action_;
	}
    
    

    
    
}
