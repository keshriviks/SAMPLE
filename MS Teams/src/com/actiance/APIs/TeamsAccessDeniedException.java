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

/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsAccessDeniedException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private URI uri;

	private String msg;

	public TeamsAccessDeniedException(String msg, URI uri) {
		super(msg);
		this.msg = msg;
		this.uri = uri;
	}

	public TeamsAccessDeniedException(Throwable t) {
		super(t);
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	public String getMessage() {
		return "Request to URL \"" + uri.toString() + "\" failed because of the server returned \"" + msg + "\"";
	}

}
