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



import java.io.IOException;
import java.net.URI;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import com.facetime.ftcore.util.FTBasicLogMgr;
import com.facetime.ftcore.util.FTLogChannel;

/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsUnauthorizedException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private URI uri;

	private String msg;

	private String errJson;
	
	private TeamsGraphApiError errObject;

	private FTLogChannel logger;
	
	public TeamsUnauthorizedException(String msg, URI uri, String errJson) {
		super(msg);
		this.msg = msg;
		this.uri = uri;
		this.errJson = errJson;
		logger = FTBasicLogMgr.instance().createLogChannel(TeamsUnauthorizedException.class, TeamsUnauthorizedException.class.getSimpleName());
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
			errObject = objectMapper.readValue(errJson, TeamsGraphApiError.class);
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
	}

	public TeamsUnauthorizedException(Throwable t) {
		super(t);
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getErrJson() {
		return errJson;
	}

	public void setErrJson(String errJson) {
		this.errJson = errJson;
	}
	
	@Override
	public String getMessage() {
		String _msg = "Request to URL \"" + uri.toString() + "\" failed because of the server returned \"" + msg + "\".";
		if(errObject != null) {
			_msg = _msg + " " + errObject.getMessage();
		}
		return _msg;
	}

	public TeamsGraphApiError getErrObject() {
		return errObject;
	}

	public void setErrObject(TeamsGraphApiError errObject) {
		this.errObject = errObject;
	}

}
