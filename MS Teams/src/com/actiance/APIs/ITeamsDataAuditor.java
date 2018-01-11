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



import com.actiance.APIs.IConversation;
import com.actiance.APIs.Response;
import com.actiance.teams.app.data.model.TeamsMsgEvent;
import com.actiance.APIs.TeamsConnectorContext;
import com.facetime.ftcore.util.FTServiceMgr;

public interface ITeamsDataAuditor {
	public void init(FTServiceMgr ftServiceMgr, IConversation sdkHandle, TeamsConnectorContext context) throws Exception;
	public Response auditEvent(TeamsMsgEvent event) throws Exception;
}
