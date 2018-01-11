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



import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsAccessToken {

	@JsonProperty("token_type")
	String tokenType;
	
	@JsonProperty("expires_in")
	long expiresIn;
	
	@JsonProperty("ext_expires_in")
	long extExpiresIn;
	
	@JsonProperty("expires_on")
	private	
	long expiresAtInSeconds;
	
	@JsonProperty("access_token")
	String accessToken;
	
	private long tokenObtainedAt;

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public long getExtExpiresIn() {
		return extExpiresIn;
	}

	public void setExtExpiresIn(long extExpiresIn) {
		this.extExpiresIn = extExpiresIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getExpiresAtInSeconds() {
		return expiresAtInSeconds;
	}

	public void setExpiresOnInSeconds(long expiresAtInSeconds) {
		this.expiresAtInSeconds = expiresAtInSeconds;
	}

	@Override
	public String toString() {
		return "TeamsAccessToken [tokenType=" + tokenType + ", expiresIn=" + expiresIn + ", extExpiresIn="
				+ extExpiresIn + ", expiresAtInSeconds=" + expiresAtInSeconds + ", accessToken=" + accessToken + ", tokenObtainedAt=" + new Date(tokenObtainedAt) + "]";
	}

	public long getTokenObtainedAt() {
		return tokenObtainedAt;
	}

	public void setTokenObtainedAt(long tokenObtainedAt) {
		this.tokenObtainedAt = tokenObtainedAt;
	}
	
}