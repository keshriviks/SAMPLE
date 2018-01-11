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



import java.net.HttpURLConnection;

public class TeamsException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HttpURLConnection httpURLConnection;
	private int responseCode;

	public TeamsException() {
    }

    public TeamsException(String message) {
        super(message);
    }

    public TeamsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TeamsException(Throwable cause) {
        super(cause);
    }
    
    public TeamsException(String message, int responseCode, HttpURLConnection httpURLConnection ) {
    	super(message);
    	this.httpURLConnection = httpURLConnection;
    	this.responseCode = responseCode;
    }

	public HttpURLConnection getHttpURLConnection() {
		return httpURLConnection;
	}

	public int getResponseCode() {
		return responseCode;
	}

}
