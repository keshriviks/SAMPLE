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

public class TeamsItemNotFoundException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private URI uri;

	public TeamsItemNotFoundException(URI uri) {
		this.uri = uri;
	}

	public TeamsItemNotFoundException(Throwable t) {
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
		return "The Item is either deleted or not found. API Url of the item is " + uri;
	}

}
