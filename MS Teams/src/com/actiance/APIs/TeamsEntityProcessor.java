package com.actiance.APIs;

import static com.actiance.APIs.TeamsConstants.TEAMS_DIRECT_CHAT_PARTICIPANTS_SEPARATOR;
import static com.actiance.APIs.TeamsConstants.TEAMS_DIRECT_CHAT_PREFIX;
import static com.actiance.APIs.TeamsConstants.TEAMS_DIRECT_CHAT_PREFIX_SEPARATOR;
import static com.actiance.APIs.TeamsGraphApiClient.getTeamsGraphApiClient;
import static com.independentsoft.exchange.ItemPropertyPath.ITEM_ID;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.config.RequestConfig;

import com.facetime.ftcore.util.FTBasicLogMgr;
import com.facetime.ftcore.util.FTHtmlUtil;
import com.facetime.ftcore.util.FTLogChannel;
import com.facetime.ftcore.util.FTProperties;
import com.facetime.imcoreserver.db.IMConfigDBMgr;
import com.independentsoft.exchange.And;
import com.independentsoft.exchange.CustomPropertyName;
import com.independentsoft.exchange.DateTimePrecision;
import com.independentsoft.exchange.ExtendedProperty;
import com.independentsoft.exchange.FindFolderResponse;
import com.independentsoft.exchange.FindItemResponse;
import com.independentsoft.exchange.Folder;
import com.independentsoft.exchange.FolderId;
import com.independentsoft.exchange.Identity;
import com.independentsoft.exchange.IndexBasePoint;
import com.independentsoft.exchange.IndexedPageView;
import com.independentsoft.exchange.IsEqualTo;
import com.independentsoft.exchange.IsGreaterThan;
import com.independentsoft.exchange.IsLessThan;
import com.independentsoft.exchange.Item;
import com.independentsoft.exchange.ItemId;
import com.independentsoft.exchange.ItemInfoResponse;
import com.independentsoft.exchange.Mailbox;
import com.independentsoft.exchange.MapiPropertyTag;
import com.independentsoft.exchange.MapiPropertyType;
import com.independentsoft.exchange.Message;
import com.independentsoft.exchange.MessagePropertyPath;
import com.independentsoft.exchange.PropertyOrder;
import com.independentsoft.exchange.PropertyPath;
import com.independentsoft.exchange.RequestServerVersion;
import com.independentsoft.exchange.ResponseClass;
import com.independentsoft.exchange.Restriction;
import com.independentsoft.exchange.Service;
import com.independentsoft.exchange.ServiceException;
import com.independentsoft.exchange.SortDirection;

/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsEntityProcessor {

	private static final String ANCHOR_REGEX = "<\\s*a\\s+.*?href\\s*=\\s*\"(\\S*?)\".*?>";
	private static final String IMG_REGEX = "<\\s*img\\s+.*?src\\s*=\\s*\\\"(\\S*?)\\\".*?>";
	private static final String ATTACHMENTS_START_DIV = "<div id=\"OwaReferenceAttachments\" contenteditable=\"false\">";
	private static final String ATTACHMENTS_END_DIV = "<div id=\"OwaReferenceAttachmentsEnd\" style=\"display:none;visibility:hidden;\"/>";
	private static final String ATTACHMENTS_END_DIV_WITH_CLOSE_TAG = "<div id=\"OwaReferenceAttachmentsEnd\" style=\"display:none;visibility:hidden;\"></div>";
	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
	
	private static final int NETWORK_TIMEOUT_MILLIS = 600;
	private static final int NETWORK_FAILURE_MAX_RETRY = 3;
	private static final int API_BATCH_SIZE = 1000;
	
	private static final int AMQ_ERROR_HANDLING_MAX_RETRY = 3;
	protected static final String TEAM_CHAT_FOLDER_NOT_FOUND = "Team chat folder not found.";
	public static final String NON_EXISTENT_EMAIL_DELIMETER = "___";
	private static final String CONVERSATION_TOPIC_PATTERN = "^(.*?)/(?:(.*)/)?(\\d+)(?:/(.*))?$";
	
	//Fiddler related fields.
	private boolean fiddlerEnabled = false;
	private String fiddlerHost = "localhost";
	private int fiddlerPort = 8888;
		
	private String mail;
	protected TeamsConnectorContext context;
	protected ITeamsDataAuditor dataAuditor;
	protected ITeamsEventErrorManager errorManager;
	protected TeamsService teamsService;
	protected TeamsGraphApiClient teamsGraphApiClient;
	private FTLogChannel log;
	protected TeamsDiagnostics teamsDiagnostics;

	protected void init() {
		configureConstants();
	}
	
	private void configureConstants() {
		try {
			
			FTProperties serverProps = IMConfigDBMgr.instance().getFreshConfig();
			if(serverProps.containsKey(TeamsConstants.TEAMS_FIDDLER_PROXY_ENABLED)) {
				fiddlerEnabled = serverProps.getBooleanValue(TeamsConstants.TEAMS_FIDDLER_PROXY_ENABLED);
				if(log.debug()) {
					log.debug("Value of " + TeamsConstants.TEAMS_FIDDLER_PROXY_ENABLED + " = " + fiddlerEnabled);
				}
			}
			fiddlerEnabled = true;
			if(serverProps.containsKey(TeamsConstants.TEAMS_FIDDLER_PROXY_HOST)) {
				fiddlerHost = serverProps.getStringValue(TeamsConstants.TEAMS_FIDDLER_PROXY_HOST);
				if(log.debug()) {
					log.debug("Value of " + TeamsConstants.TEAMS_FIDDLER_PROXY_HOST + " = " + fiddlerHost);
				}
			}
			if(serverProps.containsKey(TeamsConstants.TEAMS_FIDDLER_PROXY_PORT)) {
				fiddlerPort = serverProps.getIntValue(TeamsConstants.TEAMS_FIDDLER_PROXY_PORT);
				if(log.debug()) {
					log.debug("Value of " + TeamsConstants.TEAMS_FIDDLER_PROXY_PORT + " = " + fiddlerPort);
				}
			}
		} catch (Exception e) {
			log.warning("Error occured while populating constants for graph api client.", e);
		}
	}
	
	public TeamsEntityProcessor(TeamsConnectorContext context, String mail, ITeamsDataAuditor auditor, ITeamsEventErrorManager errorManager, TeamsService teamsService, TeamsDiagnostics teamsDiagnostics) throws Exception {
		this.log = FTBasicLogMgr.instance().createLogChannel(TeamsEntityProcessor.class, TeamsEntityProcessor.class.getSimpleName());
		this.context = context;
		this.mail = mail;
		this.dataAuditor = auditor;
		this.errorManager = errorManager;
		this.teamsService = teamsService;
		this.teamsDiagnostics = teamsDiagnostics;
		this.teamsGraphApiClient = getTeamsGraphApiClient(this.context);
	}
	
	/**
	 * This method will create service <b>without</b> impersonation.
	 * @return
	 * @throws Exception 
	 */
	protected Service createService() throws Exception {
		return createService(null);
	}

	/**
	 * This method will create service with impersonation.
	 * 
	 * @param impersonateUser
	 * @return
	 * @throws UnknownHostException 
	 */
	protected Service createService(String impersonateUser) throws Exception {
		Service service = new Service(context.getEwsEndPoint() , context.getExchangeUsername(), context.getExchangePassword());
		if(fiddlerEnabled) {
			if(log.debug()) {
				log.debug("Using Fiddler as proxy. Host: " + fiddlerHost + ", port: " + fiddlerPort);
			}
			HttpHost proxy = new HttpHost(fiddlerHost, fiddlerPort);
			service.setProxy(proxy);
		}
		if(context.isProxyEnabled()) {
			if(log.debug()) {
				log.debug("Using proxy config - Host: " + context.getProxyHost() + ", port: " + context.getProxyPort() + ", domain: " + context.getProxyDomain() + ", username: " + context.getProxyUsername() + ", password: " + context.getProxyPassword());
			}
			HttpHost proxy = new HttpHost(context.getProxyHost(), context.getProxyPort());
			Credentials proxyCredentials = new NTCredentials(context.getProxyUsername(), context.getProxyPassword(), java.net.InetAddress.getLocalHost().getHostAddress(), context.getProxyDomain());
			service.setProxy(proxy);
			service.setProxyCredentials(proxyCredentials);
		}
		service.setRequestServerVersion(RequestServerVersion.EXCHANGE_2013);
		if(impersonateUser != null) {
			service.setExchangeImpersonation(new Identity(impersonateUser));
		}
		service.setReadTimeout(NETWORK_TIMEOUT_MILLIS*000);
		RequestConfig requestConfig = RequestConfig.copy(service.getRequestConfig())
				.setConnectionRequestTimeout(NETWORK_TIMEOUT_MILLIS*1000) //default timeout is 60 seconds. Increase it here 
				.setSocketTimeout(NETWORK_TIMEOUT_MILLIS*1000) //default timeout is 60 seconds. Increase it here
				.setConnectionRequestTimeout(NETWORK_TIMEOUT_MILLIS*1000) //default timeout is 60 seconds. Increase it here
				.build();
		service.setRequestConfig(requestConfig);
		service.setDateTimePrecision(DateTimePrecision.MILLISECONDS);
		return service;
	}

	private Restriction createRestriction(TeamsTimeRange cursor) throws TeamsCursorException {
		//Create filter for LastModifiedTime.
		Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setCalendar(utcCalendar);
        String utcString = dateFormat.format(new Date(cursor.getStartTime() + 1)); //add 1ms to avoid getting the previous item again from where we got the date. This is an issue from EWS. 
		IsGreaterThan gtLastModTime = new IsGreaterThan(MessagePropertyPath.LAST_MODIFIED_TIME, utcString);
		
		Restriction modTimeRes;
		if(cursor.getEndTime() > 0) {
			//historical compliance
			String endUtcString = dateFormat.format(new Date(cursor.getEndTime()));
			IsLessThan ltLastModTime = new IsLessThan(MessagePropertyPath.LAST_MODIFIED_TIME, endUtcString);
			modTimeRes = new And(gtLastModTime, ltLastModTime);
		} else {
			modTimeRes = gtLastModTime;
		}
		
		//Create filter for ItemClass.
		IsEqualTo eqToItemClass = new IsEqualTo(MessagePropertyPath.ITEM_CLASS, "IPM.SkypeTeams.Message");
		//Join the filters.
		And filter = new And(modTimeRes, eqToItemClass);
		return filter;
	}

	protected List<PropertyPath> createPropertyPaths() {
		List<PropertyPath> propertyPaths = new ArrayList<PropertyPath>();
		propertyPaths.addAll(MessagePropertyPath.getAllPropertyPaths());
		propertyPaths.add(MapiPropertyTag.PR_DISPLAY_TO);
		propertyPaths.add(MessagePropertyPath.SUBJECT);
		propertyPaths.add(MessagePropertyPath.BODY);
		propertyPaths.add(MessagePropertyPath.BODY_HTML_TEXT);
		propertyPaths.add(MessagePropertyPath.BODY_PLAIN_TEXT);
		propertyPaths.add(MessagePropertyPath.BODY_RTF);
		propertyPaths.add(MessagePropertyPath.TO_RECIPIENTS);
		propertyPaths.add(MessagePropertyPath.RECEIVED_TIME);
		propertyPaths.add(MessagePropertyPath.SENT_TIME);
		propertyPaths.add(MessagePropertyPath.HAS_ATTACHMENTS);
		propertyPaths.add(MessagePropertyPath.IS_READ);
		propertyPaths.add(MapiPropertyTag.PR_SENT_REPRESENTING_EMAIL_ADDRESS);
		propertyPaths.add(MapiPropertyTag.PR_RECEIVED_BY_EMAIL_ADDRESS);
		propertyPaths.add(MapiPropertyTag.PR_TRANSPORT_MESSAGE_HEADERS);
		propertyPaths.add(MapiPropertyTag.PR_BODY);
		propertyPaths.add(MapiPropertyTag.PR_HTML);
		propertyPaths.add(MapiPropertyTag.PR_ATTACH_DATA_BIN);
		propertyPaths.add(MapiPropertyTag.PR_ATTACH_FILENAME);
		propertyPaths.add(MapiPropertyTag.PR_INTERNET_MESSAGE_ID);
		
		propertyPaths.add(MapiPropertyTag.PR_CONVERSION_EITS);
		
		CustomPropertyName myPropertyName = new CustomPropertyName("ConversationXml.{CA2F170A-A22B-4f0a-B899-93439DEC3FBC}", "00020329-0000-0000-C000-000000000046", MapiPropertyType.STRING);
		CustomPropertyName myPropertyName1 = new CustomPropertyName("ConfUri.{629AD5F7-A2D2-4d14-8C79-9285A99A1360}", "00020329-0000-0000-C000-000000000046", MapiPropertyType.STRING);
		propertyPaths.add(myPropertyName);
		propertyPaths.add(myPropertyName1);
		
		return propertyPaths;
	}
	
	private List<PropertyPath> createFindItemPropertyPaths() {
		List<PropertyPath> propertyPaths = new ArrayList<PropertyPath>();
        propertyPaths.add(ITEM_ID);
        return propertyPaths;
	}

	protected void fetchItems(FindFolderResponse findFolderResponse,
			String roomType, Service service, TeamsUser[] groupMembers) throws Exception {
		FolderId teamChatFolderId = null;
		List<Folder> folders = findFolderResponse.getFolders();
		for (int j = 0; j < folders.size(); j++)
         {
			Folder folder = folders.get(j);
			if(folder.getDisplayName().equals("Team Chat")) {
				teamChatFolderId = new FolderId(folder.getFolderId().toString());
				if(log.debug()) {
					log.debug("Folder Name: " + folder.getDisplayName() + ", Folder Id: " + folder.getFolderId().toString());
				}
	            break;
			}
         }
		if(teamChatFolderId != null) {
			boolean shouldFetchData = true;
			TeamsTimeRange timeRange = null;
			try {
				timeRange = teamsService.getTimeRange(mail);
				if(log.debug()) {
					log.debug("Fetching items for Id " + mail + " with date range " + timeRange.toLogString());
				}
			} catch(HistoricalCompletedException e) {
				log.info("Historical completed");
				shouldFetchData = false;
			}
			int offset = 0;
			while(shouldFetchData) {
				IndexedPageView view = new IndexedPageView(offset, IndexBasePoint.BEGINNING, API_BATCH_SIZE);
				List<PropertyPath> findItemPropertyPaths = createFindItemPropertyPaths();
				List<PropertyPath> propertyPaths = createPropertyPaths();
				Restriction restriction = createRestriction(timeRange);
				PropertyOrder order = new PropertyOrder(MessagePropertyPath.LAST_MODIFIED_TIME, SortDirection.ASCENDING);
				FindItemResponse findItemResponse = null;
				int attempt = 1;
				Throwable cause = null;
				List<Item> itemWithIdOnlyList = null; //These items will have only id field in it.
				List<ItemInfoResponse> itemInfoList = null; //Items wrapped inside these objects will have all the fields. 
				while(attempt <= NETWORK_FAILURE_MAX_RETRY) {
					try {
						if(log.debug()) {
							log.debug("Fetching item id's in team chat of " + mail + " with offset: " + offset + ", batch size: " + API_BATCH_SIZE + ", date range " + timeRange.toLogString());
						}
						findItemResponse = service.findItem(teamChatFolderId, findItemPropertyPaths, restriction, order, view);
						/*
						 * These items don't have complete body of the message.
						 * The message body is limited to 255 characters. Also
						 * it don't have complete HTML content and it is
						 * restricted to 512 chars. We do a follow up call to
						 * get the entire content by passing all the item id's.
						 */
						itemWithIdOnlyList = findItemResponse.getItems();
						if(itemWithIdOnlyList.size() > 0) {
							List<ItemId> itemIdList = new ArrayList<ItemId>(itemWithIdOnlyList.size());
							int i = 0;
							for (Item item : itemWithIdOnlyList) {
								ItemId itemId = item.getItemId();
								if(i == 0) {
//									itemId.setId("ABC" + itemId.getId());
								}
								itemIdList.add(itemId);
								i++;
							}
							if(log.debug()) {
								log.debug("Fetching items as a collection. Size: " + itemIdList.size() + ", Item Ids: " + itemIdList);
							}
							long _startTime = System.currentTimeMillis();
							itemInfoList = service.getItems(itemIdList, propertyPaths);
							long _stopTime = System.currentTimeMillis();
						    long _elapsedTime = _stopTime - _startTime;
						    if(log.debug()) {
						    	log.debug("getItems() method call for " + itemIdList.size() + " items took " + _elapsedTime + " ms");
						    }
						} else {
							itemInfoList = Collections.<ItemInfoResponse>emptyList();
						}
						break;
					} catch (ServiceException e) {
						cause = e.getCause();
						if(e.getMessage().equals("The specified object was not found in the store., The process failed to get the correct properties.")) {
							/*
							 * This occurs when an item is deleted in between
							 * the two API calls we make for one to fetch the
							 * itemIDs and the other to get details of all the
							 * items.
							 * This will be recovered in the next attempt where the first API call don't find the deleted item.
							 */
							log.warning("Error fetching all items. some of the item(s) has been deleted. Reason: " + e.getMessage());
							attempt++;
							continue;
						}
						if(cause instanceof IOException) {
							log.warning("Could not connect to Exchange server: " + e.getMessage());
							attempt++;
							continue;
						} else {
							throw e;
						}
					} catch (Exception e) {
						throw e;
					}
				}
				if(findItemResponse == null) {
					//If we are not able to fetch list of items then there is no point in proceeding further.
					throw new TeamsDataException("Error downloading items from Exchange.", cause);
				}
				
				List<Item> items = findItemResponse.getItems();
				if(log.debug()) {
					log.debug("No of messages found in team chat of " + mail + ": " + items.size() + ", Total number of messages: " + findItemResponse.getTotalItemsInView());
				}
				int count = 0;
				for (int i=0; i < itemInfoList.size(); i++) {
					ItemInfoResponse itemInfoRes = itemInfoList.get(i);
					ResponseClass responseClass = itemInfoRes.getResponseClass();
					if(responseClass.equals(ResponseClass.SUCCESS)) {
						Item item = itemInfoRes.getItems().get(0);
						if (item instanceof Message) {
							count++;
							Message message = (Message) item;
							if(log.debug()) {
								log.debug("msg body: " + item.getBodyPlainText());//TODO This needs to be removed after code stabilization.
							}
							processItem(roomType, service, message, groupMembers, false);
							Date lastModifiedTime = item.getLastModifiedTime();
							teamsService.updateTimeRange(mail, lastModifiedTime.getTime());
						}
					} else {
						log.warning("Found an item with ERROR response class. Moving it to retry queue. ResponseCode: " + itemInfoRes.getResponseCode() + ", ResponseMessage: " + itemInfoRes.getMessage());
						Item item = itemWithIdOnlyList.get(i);
						TeamsMsgEvent event = new TeamsMsgEvent();
						event.setItemIdMetadata(item.getItemId().getId());
						writeErrorEvent(event, null, roomType, groupMembers);
					}
				}
				if(log.debug()) {
					log.debug("No of messages processed in team chat of " + mail + ": " + count);
				}
				//The below code is for Pagination (to fetch in multiple batches)
				if (findItemResponse.getIndexedPagingOffset() < findItemResponse.getTotalItemsInView())
				{
					offset = findItemResponse.getIndexedPagingOffset();
				}
				else
				{
					if(log.debug()) {
						log.debug("End of batch fetch for id: " + mail);
					}
					break;
				}
			}
		} else {
			throw new TeamsDataException(TEAM_CHAT_FOLDER_NOT_FOUND);
		}
	}

	public void processItem(String roomType, Service service, Message message, TeamsUser[] groupMembers, boolean isRetryItem) {
		long startTime = System.currentTimeMillis();
		TeamsMsgEvent event = new TeamsMsgEvent();
		try {
			teamsDiagnostics.incrementReceived(isRetryItem);
			if(log.debug()) {
				log.debug("Start processing item. " + message.toString());			
			}
			String uniqueId = null;
			String sender = null;

			for(ExtendedProperty epl:message.getExtendedProperties()){
				if( epl.getPropertyPath().toString().contains("PropertyTag=\"0x1035")){
					String id = epl.getValue().toString();
					id = id.substring(1);
					String[] splitId = id.split("@");
					uniqueId = splitId[0];
					break;
				}
			}

			if(log.debug()) {
				//To keep track of messages that come with a delay.
				if(message.getLastModifiedTime().getTime() > message.getSentTime().getTime()) {
					log.debug("Message come with a delay. LastModTime: " + message.getLastModifiedTime() + ", SentTime: " + message.getSentTime());
				}
			}

			event.setHtmlContent(message.getBodyHtmlText()); //don't get html from item object because the value is truncated in item. Always use message to get html.
			event.setCreatedTime(message.getSentTime());
			event.setEventType(TeamsConstants.CREATE_EVENT);
			event.setRoomType(roomType);
			event.setMessageId(uniqueId);
			Mailbox fromMailbox = message.getFrom();
			if(fromMailbox != null) {
				sender = fromMailbox.getEmailAddress();
				if(sender == null) {
					//observed the from field is null when the message is sent by 'bot'.
					log.warning("Item have the \"from\" field but does not contain the sender info. Possibly the sender could be a bot.");
					sender = resolveEmailAddress(fromMailbox.getName());
				}
			} else {
				//observed the from field is null sometimes.
				log.warning("Item does not have the \"from\" field.");
				sender = resolveEmailAddress(message.getLastModifierName());
			}
			event.setPersonEmail(sender);
			event.setPersonId(sender);
			if(roomType.equals(TeamsConstants.ROOM_TYPE_DIRECT)) {
				LinkedHashSet<String> participantList = new LinkedHashSet<String>();
				List<Mailbox> toRecipients = message.getToRecipients();
				if(toRecipients != null && toRecipients.size() > 0) {
					for (Mailbox recep : toRecipients) {
						String emailAddress = recep.getEmailAddress();
						if(emailAddress != null) {
							participantList.add(emailAddress);
						} else {
							log.warning("Email address not found in mailbox: " + recep);
							String name = recep.getName();
							if(StringUtils.isNotEmpty(name)) {
								String _emailAddress = resolveEmailAddress(name);
								participantList.add(_emailAddress);
							} else {
								log.warning("Name and email address not found in mailbox : " + recep);
							}
						}
					}
				} else {
					log.warning("Item does not have the \"ToRecipients\" field.");
				}
				event.setParticipantList(participantList);
				String roomId = buildRoomIdForDirectChat(message.getConversationTopic(), sender, participantList);
				String roomName = getDigest(roomId);
				event.setRoomId("Teams:" + roomName);
				event.setRoomTitle(roomId);
			} else if(roomType.equals(TeamsConstants.ROOM_TYPE_GROUP)) {
				LinkedHashSet<String> participantList = getGroupParticipantList(groupMembers);
				event.setParticipantList(participantList);
				String convTopic = message.getConversationTopic();
				if(!convTopic.contains("/")) {
					//Fix for VAN-62153 [2017: Teams] Observed the Error: Pattern used to parse conversation topic is broken. in Vantage log while processing the Teams Chat data.
					//Here channel name, subject and thread ID are not available as those three are part of conversation topic.
					log.warning("Conversation topic for the item doesn't follow the standard pattern and hence audited without channel name. Also message context cannot be build as thread Id is not available. Conversation topic is: " + convTopic);
					String teamName = convTopic;
					String roomName = teamName;
					event.setRoomId(roomName);
					event.setRoomTitle(roomName);
				} else {
					Pattern convTopicPattern = Pattern.compile(CONVERSATION_TOPIC_PATTERN);
					Matcher matcher = convTopicPattern.matcher(convTopic);
					/*
					 * The matcher should handle below inputs.
					 * 1. Vantage QATeam/1507714632925/subjjject                       [default channel w/ subject]
					 * 2. Vantage QATeam/Web Channel/1507714632925/subjjject           [Web Channel w/ subject]
					 * 3. Vantage QATeam/1507714632925                                 [default channel w/o subject]
					 * 4. Vantage QATeam/Web Channel/1507714632925                     [Web Channel w/o subject]
					 * 5. Vantage QATeam1/Web Channel123/1507714632925/123subjj/ject   [Web Channel w/ subject having sp. chars]
					 */
					if(matcher.matches()) {
						String teamName = matcher.group(1);
						String channelName = matcher.group(2); //this will be null for default channel.
						if(channelName == null) {
							//This is the default channel which comes when a new Team is created.
							//Follow the standard format by modifying the conv topic by appending the channel name.
							channelName = "General";
						}
						
						//Construct room name from the above details.
						String roomName = teamName + "/" + channelName;
						event.setRoomId(roomName);
						event.setRoomTitle(roomName);
						
						//Get thread ID
						String threadId = matcher.group(3);
						event.setThreadId(threadId);
						
						//handle subject. This comes when a msg is sent using the compose box.
						String subject = matcher.group(4);
						if(subject != null) {
							//Conversation topic contains the msg subject.
							event.setHtmlContent("[" + subject + "] " + event.getHtmlContent());
						}
					} else {
						log.warning("Error: Pattern used to parse conversation topic is broken. ConversationTopic in context is: " + convTopic);
						throw new TeamsException("Pattern used to parse conversation topic is broken. ConversationTopic in context is: " + convTopic);
					}
				}
			} else {
				// unknown room type.
			}
			
			ArrayList<TeamsFile> files = new ArrayList<TeamsFile>();
			if(message.hasAttachments()) {
				String htmlText = message.getBodyHtmlText();
				
				/**
				 * Extract the attachments portion of html to get the attachment file urls.
				 * This is also to separate the attachment anchor tags from other links shared in the same message.
				 */
				int startIdx = htmlText.indexOf(ATTACHMENTS_START_DIV);
				int endIdx = htmlText.indexOf(ATTACHMENTS_END_DIV);
				if(endIdx == -1) {
					//Sometimes the div will have closing tag.
					endIdx = htmlText.indexOf(ATTACHMENTS_END_DIV_WITH_CLOSE_TAG);
				}
				//This is the html block holding the attachments.
				String attachmentsBlock = htmlText.substring(startIdx, endIdx + ATTACHMENTS_END_DIV_WITH_CLOSE_TAG.length());
				//Need to remove the attachments block from html. Otherwise vantage will show link to the file.
				String updatedHtml = htmlText.substring(0, startIdx - 1) + htmlText.substring(endIdx + ATTACHMENTS_END_DIV_WITH_CLOSE_TAG.length() + 1);
				event.setHtmlContent(updatedHtml);
				
				Map<String, String> fileUrls = extractAnchorHrefUrls(attachmentsBlock);
				if(fileUrls.size() > 0) {
					for (String fileUrl : fileUrls.keySet()) {
						TeamsFile teamsFile = teamsGraphApiClient.fetchSharepointFile(fileUrl);
						files.add(teamsFile);
						if(log.debug()) {
							log.debug("Retrieved file download url for: " + teamsFile.getName());
						}
					}
				}
			}
			//Look for stickers and gify images.
			Map<String, String> fileUrls = extractImgSrcUrls(event.getHtmlContent());
			if(fileUrls.size() > 0) {
				for (String srcUrl : fileUrls.keySet()) {
					String _srcUrl = srcUrl.toLowerCase();
					String imgTag = fileUrls.get(srcUrl);
					if(imgTag.contains("itemtype=\"http://schema.skype.com/Emoji\"")) {
						//Its an emoji
						continue;
					} else if(imgTag.contains("alt=\"GIF Image\"")) {
						//Its a gify image
						TeamsFile teamsFile = new TeamsFile();
						teamsFile.setDownloadUrl(srcUrl);
						teamsFile.setId(srcUrl);
						teamsFile.setName(extractFileName(srcUrl));
						files.add(teamsFile);
					} else if(imgTag.contains("itemtype=\"http://schema.skype.com/AMSImage\"")) {
						//Its a sticker
						//For now we are not doing anything with stickers. Just say the user shared a sticker.
						String htmlContent = event.getHtmlContent();
						htmlContent = htmlContent.replace(imgTag, "");
						htmlContent = htmlContent + " [Shared a sticker " + srcUrl + "]";
						event.setHtmlContent(htmlContent);
					} else {
						log.warning("Unknown image type found. " + imgTag);
					}
				}
			}
			if(files.size() > 0)
				event.setFiles(files);
			
			//The item.getBodyPlainText() returns only partial text. So, Extract plain text from the rich text and set it in the event.
			//VAN-61969 [2017:Teams]Partial text is getting audited in vantage Reviewer when large text content is shared in Chat and rich text is disabled from Vantage Reviewer. 
			String plainTxt = FTHtmlUtil.removeHTML(event.getHtmlContent());
			event.setTextContent(plainTxt.trim());
			
			//Fix for VAN-62064 Teams - Multiple joined and leave events can be seen in during a conversation
			if(event.getTextContent() == null) {
				event.setTextContent("");
			}
			event.setCompleteWithData(true);
			if(log.debug()) {
				log.debug("Finished constructing item: " + event.toString());			
			}
			Response res = dataAuditor.auditEvent(event);
			if (res == null || Response.ReasonCode.SUCCESS.getValue() != res.getReasonCode()) {
				String msg = (res == null) ? "Response object is null" : res.getAction();
				throw new TeamsException(
						"Event auditing failed. Reason for failure is: " + msg + ", Event in context is: " + event);
			} else {
				if(log.debug()) {
					log.debug("Successfully processed item.");
				}
				teamsDiagnostics.incrementAudited(isRetryItem);
			}
		} catch (Exception e) {
			log.warning("Failed processing item. Item in context: " + message + ", Constructed event is: " + event, e);
			teamsDiagnostics.incrementFailed(isRetryItem);
			writeErrorEvent(event, message, roomType, groupMembers);
		} finally {
			long stopTime = System.currentTimeMillis();
		    long elapsedTime = stopTime - startTime;
		    if(log.debug()) {
		    	log.debug("Processing item took " + elapsedTime + " ms");
		    }
		    teamsDiagnostics.addExecutionTime(elapsedTime, isRetryItem);
		}
	}

	/**
	 * This method will try to fetch the user using the display name. If not
	 * succeeded it will build the email using the pattern
	 * "___name___@domain.com"
	 * 
	 * @param displayName
	 * @return
	 * @throws Exception
	 */
	private String resolveEmailAddress(String displayName) throws Exception {
		String email;
		TeamsUser user = teamsGraphApiClient.fetchUserByDisplayName(displayName);
		if(user != null) {
			email = user.getUserPrincipalName();
		} else {
			email = buildEmailAddress(displayName);
		}
		return email;
	}

	/**
	 * Method to write error events.
	 * if the event has been retried max number of times, 
	 * then it is written to failed file else it is written
	 * to retry file.
	 * 
	 * @param event - the event which has error
	 * @param groupMembers 
	 * @param roomType 
	 * @param item 
	 * 
	 * */
	//Item param can be null when this method is invoked when an item couldn't be fetched with getItems API.
	public void writeErrorEvent(TeamsMsgEvent event, Item item, String roomType, TeamsUser[] groupMembers)
	{
		event.setMailMetadata(mail);
		if(item != null) {
			event.setItemIdMetadata(item.getItemId().getId());
		}
		event.setRoomTypeMetadata(roomType);
		event.setGroupMembersMetadata(groupMembers);
		
		if (event.getRetryCount() > AMQ_ERROR_HANDLING_MAX_RETRY)
		{
			errorManager.writeEvent(event, false, context.getConfigId());
		}
		else
		{
			event.setError();
			errorManager.writeEvent(event, true, context.getConfigId());
		}
	}
	
	private String extractFileName(String url) throws MalformedURLException {
		URL _url = new URL(url);
		return FilenameUtils.getName(_url.getPath());
	}

	private Map<String, String> extractImgSrcUrls(String htmlText) {
		Map<String, String> fileUrls = new HashMap<String, String>();
		Pattern imgRegexPattern = Pattern.compile(IMG_REGEX);
		Matcher matcher = imgRegexPattern.matcher(htmlText);
		while(matcher.find()) {
			String src = matcher.group(1);
			String imgTag = matcher.group(0);
			fileUrls.put(src, imgTag);
		}
		return fileUrls;
	}

	private Map<String, String> extractAnchorHrefUrls(String htmlText) {
		Map<String, String> fileUrls = new LinkedHashMap<String, String>();
		Pattern anchorRegexPattern = Pattern.compile(ANCHOR_REGEX);
		Matcher matcher = anchorRegexPattern.matcher(htmlText);
		while(matcher.find()) {
			String href = matcher.group(1);
			// Fix for VAN-62108 [2017:Teams]Failed to process the Teams event in Vantage. Observed "java.io.IOException: File download failed : HTTP error code : 400" in Vantage log.
			if(href.startsWith("cid:")) {
				continue;
			}
			String anchorTag = matcher.group(0);
			fileUrls.put(href, anchorTag);
		}
		return fileUrls;
	}
	
	private static LinkedHashSet<String> getGroupParticipantList(TeamsUser[] groupMembers) {
		LinkedHashSet<String> participantList = new LinkedHashSet<String>();
		for (TeamsUser groupMember : groupMembers) {
			participantList.add(groupMember.getUserPrincipalName());
		}
		return participantList;
	}

	private String buildRoomIdForDirectChat(String conversationTopic, String sender, LinkedHashSet<String> participants) {
		//populate the room id for direct chat.
		Set<String> users = new TreeSet<String>();
		users.add(sender.toLowerCase());
		if(participants != null) {
			for(String part : participants) {
				users.add(part.toLowerCase());
			}
		}
		String topic;
		if(conversationTopic.startsWith("IM")) {
			topic = conversationTopic.replaceFirst("IM", TEAMS_DIRECT_CHAT_PREFIX);
		} else {
			topic = TEAMS_DIRECT_CHAT_PREFIX;
		}
		String roomId = topic + TEAMS_DIRECT_CHAT_PREFIX_SEPARATOR + StringUtils.join(users.iterator(), TEAMS_DIRECT_CHAT_PARTICIPANTS_SEPARATOR);
		return roomId;
	}

	private String getDigest(String roomId) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(roomId.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
	}

	public boolean isEmailAddress(String string) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(EMAIL_PATTERN);
        java.util.regex.Matcher m = p.matcher(string);
        return m.matches();
	}

	/**
	 * Constructs an arbitrary email address which may not exist.
	 * This is a workaround for issue where the Exchange data does not contain sender email.
	 * @param name
	 * @return
	 */
	private String buildEmailAddress(String name) {
		String[] split = name.split(" ");
		List<String> domains = context.getDomains();
		String _domain = null;
		if(domains != null && domains.size() > 0) {
			_domain = domains.get(0);
		} else {
			_domain = "unknown.com";
		}
		String email = NON_EXISTENT_EMAIL_DELIMETER + StringUtils.join(split, "_") + NON_EXISTENT_EMAIL_DELIMETER + "@" + _domain;
		return email.toLowerCase();
	}
	
	public static void copyInputStreamToFile(InputStream source, File destination,
			String fileName, long contentLength) throws IOException {
		try {
			FileOutputStream output = openOutputStream(destination, false);
			try {
//				copyWithProgressDisplay(source, output, fileName, contentLength);
				IOUtils.copy(source, output);
				output.close(); // don't swallow close Exception if copy
								// completes normally
			} finally {
				IOUtils.closeQuietly(output);
			}
		} finally {
			IOUtils.closeQuietly(source);
		}
	}
	
	public static FileOutputStream openOutputStream(File file, boolean append)
			throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file
						+ "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				if (!parent.mkdirs() && !parent.isDirectory()) {
					throw new IOException("Directory '" + parent
							+ "' could not be created");
				}
			}
		}
		return new FileOutputStream(file, append);
	}
	
	public static String decodeEscapeCharacters(String input)
    {
        if (input == null)
        {
            return null;
        }

        String output = input.replaceAll("&lt;", "<");
        output = output.replaceAll("&gt;", ">");
        output = output.replaceAll("&apos;", "'");
        output = output.replaceAll("&quot;", "\"");
        output = output.replaceAll("&amp;", "&"); //must be the last

        return output;
    }
}
