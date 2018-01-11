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


import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;


/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsObjectModule extends SimpleModule {

	public TeamsObjectModule() {
		super(TeamsObjectModule.class.getSimpleName(), new Version(1, 0, 0, null));
	}

	@Override
	public void setupModule(SetupContext context) {
//		context.setMixInAnnotations(AccessToken.class, AccessTokenMixin.class);
	}

}
