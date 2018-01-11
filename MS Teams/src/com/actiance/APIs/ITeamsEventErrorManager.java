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
import java.util.ArrayList;

import com.actiance.APIs.TeamsMsgEvent;


/**
 * 
 * @author VSriramulu
 *
 */
public interface ITeamsEventErrorManager {
	
	/**
	 * Write Event to be retrieved later.
	 * @param configId 
	 * @param retry
	 * @throws IOException 
	 * 
	 * */
	public boolean writeEvent(TeamsMsgEvent event, boolean retry, int configId);
	
	/**
	 * Read events back and return the list.
	 * Also, deletes the read events.
	 * @param teamsServiceNum - config id. each retry/failed file is uniquely identified by combination of event id and config id.
	 * @param readRetry - flag to indicate type(i.e retry or fail) of events to read events from. If true, then read from Retry Folder else Failed folder 
	 * @return ArrayList<Event> which will be then processed by the Teams Event Manager.
	 * @throws IOException 
	 */
	public ArrayList<TeamsMsgEvent> readEvents(boolean readRetry, int teamsServiceNum);

}