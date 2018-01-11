package com.actiance.APIs;

import java.nio.channels.FileChannel;

import com.actiance.teams.app.ITeamsDataAuditor;
import com.actiance.teams.app.TeamsService;
import com.actiance.teams.app.data.model.TeamsUser;
import com.actiance.teams.common.TeamsConstants;
import com.actiance.teams.model.TeamsConnectorContext;
import com.facetime.ftcore.util.FTBasicLogMgr;
import com.facetime.ftcore.util.FTLogChannel;
import com.independentsoft.exchange.FindFolderResponse;
import com.independentsoft.exchange.Service;
import com.independentsoft.exchange.StandardFolder;

/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsUserProcessor extends TeamsEntityProcessor implements Runnable {

	private TeamsUser user;
	private FTLogChannel log;

	public TeamsUserProcessor(TeamsUser user, TeamsConnectorContext context, ITeamsDataAuditor auditor, ITeamsEventErrorManager errorManager, TeamsService teamsService, TeamsDiagnostics teamsDiagnostics) throws Exception {
		super(context, user.getUserPrincipalName(), auditor, errorManager, teamsService, teamsDiagnostics);
		this.log = FTBasicLogMgr.instance().createLogChannel(TeamsUserProcessor.class, TeamsUserProcessor.class.getSimpleName());
		this.user = user;
	}

	@Override
	public void run() {
		init();
		processUser(user);
	}
	
	private void processUser(TeamsUser user) {
		long startTime = System.currentTimeMillis();
		try {
			String mail = user.getUserPrincipalName();
			if(mail == null || mail.isEmpty()) {
				log.warning("Not processing the user. Reason: User does not have an email id: " + user);
				//Without email we can't impersonate the user to get items using EWS.
				return;
			}
			if(log.debug()) {
				log.debug("------------- Processing user " + user.getUserPrincipalName() + " ------------------");
			}
			Service service = createService(user.getUserPrincipalName());
			FindFolderResponse findFolderResponse = service.findFolder(StandardFolder.CONVERSATION_HISTORY);
			fetchItems(findFolderResponse, TeamsConstants.ROOM_TYPE_DIRECT, service, null);
		} catch (Exception e) {
			String msg = e.getMessage();
			if(msg != null && msg.equals(TEAM_CHAT_FOLDER_NOT_FOUND)) {
				//The user have an email account but don't have Teams chat folder.
				log.warning("User don't have Teams chat folder: " + user.getUserPrincipalName());
			} else if(msg != null && msg.contains("The primary SMTP address must be specified when referencing a mailbox.")) {
				//The user don't have Teams account or permission.
				log.warning("User don't have Teams account or permission: " + user.getUserPrincipalName());
			} else {
				log.warning("Exception occured while processing user: " + user, e);
			} 
		} finally {
			long stopTime = System.currentTimeMillis();
		    long elapsedTime = stopTime - startTime;
		    if(log.debug()) {
		    	log.debug("Processing user took " + elapsedTime + " ms");
		    }
		}
	}
}
