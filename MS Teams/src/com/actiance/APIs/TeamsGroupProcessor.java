package com.actiance.APIs;

import com.actiance.APIs.ITeamsDataAuditor;
import com.actiance.APIs.TeamsService;  //to check teams service
import com.actiance.APIs.TeamsGroup;
import com.actiance.APIs.TeamsUser;
import com.actiance.APIs.TeamsConstants;
import com.actiance.APIs.TeamsConnectorContext;
//import com.facetime.ftcore.util.FTBasicLogMgr;
//import com.facetime.ftcore.util.FTLogChannel;
import com.independentsoft.exchange.FindFolderResponse;
import com.independentsoft.exchange.Mailbox;
import com.independentsoft.exchange.Service;
import com.independentsoft.exchange.StandardFolder;
import com.independentsoft.exchange.StandardFolderId;

/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsGroupProcessor extends TeamsEntityProcessor implements Runnable {

	private TeamsGroup group;
	private FTLogChannel log;
	//service for admin user which is used to get group items where the admin is part of that group.
	private static Service SERVICE_ADMIN_MAILBOX;

	public TeamsGroupProcessor(TeamsGroup group, TeamsConnectorContext context, ITeamsDataAuditor auditor, ITeamsEventErrorManager errorManager, TeamsService teamsService, TeamsDiagnostics teamsDiagnostics) throws Exception {
		super(context, group.getMail(), auditor, errorManager, teamsService, teamsDiagnostics);
		this.log = FTBasicLogMgr.instance().createLogChannel(TeamsGroupProcessor.class, TeamsGroupProcessor.class.getSimpleName());
		this.group = group;
	}

	@Override
	public void run() {
		init();
		processGroup(group);
	}
	
	private void processGroup(TeamsGroup teamsGroup) {
		long startTime = System.currentTimeMillis();
		try {
			String mail = teamsGroup.getMail();
			if(mail == null || mail.isEmpty()) {
				log.warning("Not processing the group. Reason: Group does not have an email id: " + teamsGroup);
				//Without email we can't impersonate the group to get items using EWS.
				return;
			}
			if(log.debug()) {
				log.debug("------------- Processing group " + teamsGroup.getMail() + " ------------------");
			}
			Service _service;
			TeamsUser[] groupMembers = teamsGraphApiClient.fetchGroupMembers(teamsGroup.getId());
			//If admin is not part of the group we will receive access denied error.
			if(isAdminPartOfGroup(groupMembers)) {
				//Use the admin service to get items.
				if(SERVICE_ADMIN_MAILBOX == null) {
					synchronized (TeamsGroupProcessor.class) {
						if(SERVICE_ADMIN_MAILBOX == null) {
							SERVICE_ADMIN_MAILBOX = createService();
						}
					}
				}
				_service = SERVICE_ADMIN_MAILBOX;
			} else {
				//Impersonate a member of that group to fetch items.
				TeamsUser groupMember = getAnyGroupMember(groupMembers);
				_service = createService(groupMember.getUserPrincipalName());
			}
			Mailbox teamMailbox = new Mailbox(teamsGroup.getMail());
			StandardFolderId convHistFolderId = new StandardFolderId(StandardFolder.CONVERSATION_HISTORY, teamMailbox);
			FindFolderResponse findFolderResponseTeam = _service.findFolder(convHistFolderId);
			fetchItems(findFolderResponseTeam, TeamsConstants.ROOM_TYPE_GROUP, _service, groupMembers);
		} catch (Exception e) {
			log.warning("Exception occured while processing group: " + group, e);
		} finally {
			long stopTime = System.currentTimeMillis();
		    long elapsedTime = stopTime - startTime;
		    if(log.debug()) {
		    	log.debug("Processing group took " + elapsedTime + " ms");
		    }
		}
	}

	public static TeamsUser getAnyGroupMember(TeamsUser[] groupMembers) {
		return groupMembers[0];
	}

	private boolean isAdminPartOfGroup(TeamsUser[] groupMembers) {
		String _username = context.getExchangeUsername().toLowerCase();
		for (TeamsUser groupMember : groupMembers) {
			if(groupMember.getUserPrincipalName().toLowerCase().equals(_username)) {
				return true;
			}
		}
		return false;
	}
	
}

