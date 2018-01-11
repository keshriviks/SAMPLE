package com.actiance.APIs;

/*
 * Copyright (c) 2017 Actiance Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Actiance
 * Inc. ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Actiance Inc.
 *
 * ACTIANCE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. ACTIANCE SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */



/**
 * Constant File
 * 
 * Author : smaylavaram@actiance.com 
 * Name : Sandeep kumar Maylavaram
 * @author SMaylavaram
 * 
 * */


public interface TeamsConstants
{
	public final String TEAMS_RESOURCE_KEY = "resource";
	public final String TEAMS_TYPE_KEY = "type";
	
	public final String TEAMS_RESOURCE_VALUE_MSG = "messages";
	public final String TEAMS_RESOURCE_VALUE_MEM = "memberships";
	
	public final String TEAMS_TYPE_VALUE_CREATED = "created";
	public final String TEAMS_TYPE_VALUE_DELETED = "deleted";
	
	public final String TEAMS_DATA_ROOM_ID = "data.roomId";
	
	public final String DEFAULT_BASE_URL = "https://graph.microsoft.com/v1.0";
	
	public final int HTTP_429 = 429;
	public final String HTTP_RETRY_AFTER = "Retry-After";
	
	public final String TEAMS_ROOM_TYPE = "data.roomType";
	public final String TEAMS_ROOM_TYPE_DIRECT = "direct";
	public final String TEAMS_ROOM_TYPE_GROUP = "group";
	
	public final String STANDARD_DATE_FORMAT = "MMM dd, YYYY hh:mm:ss a";
	public final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	/**
	 * Used to form the error event files along with config ID.
	 */
	public final String NAME_RETRY = "retry";
	public final String NAME_FAILED = "failed";
	
	/**
	 * Event Constants
	 */
	public final String EVENT_IS_FAILED = "isFailed";
	public final String EVENT_RESOURCE = "resource";
	public final String EVENT_ID = "id";
	public final String EVENT_ACTOR_ID ="actorId";
	public final String EVENT_TYPE="type";
	public final String EVENT_CREATED ="created";
	public final String EVENT_MESSAGES_EVENT = "messages";
	public final String EVENT_MEMBERSHIP_EVENT = "memberships";
	public final String EVENT_DATA = "data";
	public final String EVENT_ROOM_ID ="roomId";
	public final String EVENT_PERSON_ID = "personId";
	public final String EVENT_PERSON_EMAIL = "personEmail";
	public final String EVENT_ROOM_TYPE = "roomType";
	public final String EVENT_TEXT = "text";
	public final String EVENT_HTML = "html";
	public final String EVENT_FILES = "files";
	public final String EVENT_MENTIONED_PEOPLE = "mentionedPeople";
	public final String EVENT_PERSON_DISPLAY_NAME = "personDisplayName";
	public final String EVENT_PERSON_ORG_ID = "personOrgId";
	public final String EVENT_IS_MODERATOR = "isModerator";
	public final String EVENT_IS_MONITOR = "isMonitor";
	
	/**
	 * Teams Helper Constants
	 */
	public final String HELPER_EMAIL = "email";
	public final String HELPER_MACHINE = "machine";
	public final String HELPER_BOT = "bot";
	
	public final String HISORICAL_OCCURANCE_FREQUENCY_EVERY = "every";
	public final String HISORICAL_OCCURANCE_FREQUENCY_ONCE = "once";
	
	public final String TEAMS_DEFAULT_TEAM = "Microsoft Teams Team";
	
	public final String TEAMS_DIRECT_CHAT_DEFAULT_TITLE = "Direct Chat";
	public final String TEAMS_BOT = "teams-ms-it-admin-bot@microsoft.com";
	public final String TEAMS_DIRECT_CHAT_PREFIX = "DIRECT_CHAT";
	public final String TEAMS_DIRECT_CHAT_PREFIX_SEPARATOR = " - ";
	public final String TEAMS_DIRECT_CHAT_PARTICIPANTS_SEPARATOR = ", ";
	
    public static String CREATE_EVENT = "CREATED";
    public static String EDIT_EVENT = "EDITED";
    public static String DELETE_EVENT = "DELETED";
    
    public static String ROOM_TYPE_GROUP = "GROUP";
    public static String ROOM_TYPE_DIRECT = "DIRECT";
    public static final int CONNECT_TIMMEOUT = 60000;
    public static final int READ_TIMMEOUT = 60000;
    
    public static final String TEAMS_HTTPCLIENT_TIMEOUT_MILLIS 				= "teams.httpclient.timeout.millis";
	public static final String TEAMS_HTTPCLIENT_RETRY_WAIT_MILLIS 			= "teams.httpclient.retry.wait.time.millis";
	public static final String TEAMS_HTTPCLIENT_MAX_RETRY_COUNT 			= "teams.httpclient.max.retry.count";
	public static final String TEAMS_HTTPCLIENT_MAX_CONNECTIONS 			= "teams.httpclient.max.connections";
	public static final String TEAMS_HTTPCLIENT_MAX_CONNECTIONS_PER_ROUTE 	= "teams.httpclient.max.connections.per.route";
	public static final String TEAMS_API_BATCH_SIZE 						= "teams.api.batch.size";
	public static final String TEAMS_AMQ_ERROR_HANDLING_MAX_RETRY			= "teams.amq.error.handling.max.retry";
	public static final String TEAMS_THREADPOOL_INITIAL_SIZE				= "teams.threadpool.initial.size";
	public static final String TEAMS_THREADPOOL_MAX_SIZE					= "teams.threadpool.max.size";
	public static final String TEAMS_THREAD_KEEPALIVE_TIME_SEC				= "teams.thread.keepalive.time.seconds";
	public static final String TEAMS_FIDDLER_PROXY_ENABLED					= "teams.fiddler.proxy.enabled";
	public static final String TEAMS_FIDDLER_PROXY_HOST						= "teams.fiddler.proxy.host";
	public static final String TEAMS_FIDDLER_PROXY_PORT						= "teams.fiddler.proxy.port";

}
