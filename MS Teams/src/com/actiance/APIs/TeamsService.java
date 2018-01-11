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



import static com.actiance.APIs.TeamsGraphApiClient.getTeamsGraphApiClient;

import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.actiance.APIs.CursorDBMgr;
import com.actiance.APIs.TeamsGraphApiClient;
import com.actiance.teams.app.data.error.HistoricalCompletedException;
import com.actiance.teams.app.data.error.TeamsException;
import com.actiance.teams.common.TeamsConstants;
import com.actiance.teams.model.TeamsConnectorContext;
import com.actiance.teams.model.TeamsTimeRange;
import com.facetime.ftcore.alarm.Alarm;
import com.facetime.ftcore.alarm.AlarmInterface;
import com.facetime.ftcore.alarm.Alarmable;
import com.facetime.ftcore.util.FTBasicLogMgr;
import com.facetime.ftcore.util.FTCalcSyncTime;
import com.facetime.ftcore.util.FTEventChannel;
import com.facetime.ftcore.util.FTEventChannelFactory;
import com.facetime.ftcore.util.FTException;
import com.facetime.ftcore.util.FTHashtable;
import com.facetime.ftcore.util.FTLogChannel;
import com.facetime.ftcore.util.FTProperties;
import com.facetime.ftcore.util.FTPropsKeyNotFoundException;
import com.facetime.ftcore.util.FTPropsValueIllegalException;
import com.facetime.ftcore.util.FTSecurityUtils;
import com.facetime.ftcore.util.FTServiceMgr;
import com.facetime.ftcore.util.FTTimeUtil;
import com.facetime.imcoreserver.admin.IMAdminEventConstants;
import com.facetime.imcoreserver.admin.IMAdminProps;
import com.facetime.imcoreserver.admin.IMAuditorLicenseReader;
import com.facetime.imcoreserver.db.IMAuditChatDBMgr;
import com.facetime.imcoreserver.db.IMAuditChatLoggingDBMgr;
import com.facetime.imcoreserver.db.IMConfigDBMgr;
import com.facetime.imcoreserver.db.IMDBException;
import com.facetime.imcoreserver.db.IMDBNotFoundException;
import com.facetime.imcoreserver.db.IMDatabaseMgr;
import com.facetime.imcoreserver.db.IMInvalidPropertyTypeException;
import com.facetime.imcoreserver.export.admin.IMEAdminProps;
import com.facetime.imcoreserver.importer.ImporterUtil;
import com.facetime.imcoreserver.monitor.AuditUtil;
import com.facetime.imstack.im.IMConstants;

/**
 * Teams Service is alarmable class triggered by Teams Service Manager. For each
 * configuration, one Teams Service is initialized.
 * 
 * Author : smaylavaram@actiance.com Name : Sandeep kumar M
 * 
 * @author SMaylavaram
 *
 */
public class TeamsService implements IMEAdminProps, Alarmable {
	
	private static final int MSEC_PER_SEC = 1000, MSEC_PER_MIN = 60 * MSEC_PER_SEC;
	// MSEC_PER_HOUR = 60 * MSEC_PER_MIN;
	// MSEC_PER_DAY = 24 * MSEC_PER_HOUR,
	// HRS_PER_DAY = 24;
	public static final long MAX_TIME_TO_RUN = 15 * 60 * 1000; // 15 mins max
	// run time per
	// interaction.
	public static final int MAX_SERVICES_PER_SERVER = -1; // Allow an unlimited
	// number of
	// s to run
	// at the same time.
	private static final long
	// WAIT_FOR_ANOTHER_TEAMS_SERVICE = 1 * FTTimeUtil.MILLI_SECONDS_IN_MINUTE,
	WAIT_FOR_CONFIG_CHANGE = 10 * FTTimeUtil.MILLI_SECONDS_IN_MINUTE,
			// WAIT_FOR_DB = 1 * FTTimeUtil.MILLI_SECONDS_IN_MINUTE,
			WAIT_FOR_START_MINS = 5, WAIT_FOR_START = WAIT_FOR_START_MINS * FTTimeUtil.MILLI_SECONDS_IN_MINUTE,
			STOP_NOW = 0;

	private static final String[] TIME_FORMAT = { "hh:mm a", // 3:45 AM, 03:45
																// AM, 3:45 PM,
																// 03:45 PM
			"hh:mma", // 3:45AM, 03:45AM, 3:45PM, 03:45PM
			"HH:mm" // 3:45, 03:45, 15:45, 15:45
	};

	private static final String HISTORICAL_DATE_FROM = "from", HISTORICAL_DATE_TO = "to",
			HISTORICAL_DATE_BETWEEN = "between";
	private static final String TEAMS_PROP_COMMON_PREFIX = PROP_teamsService + ".common." + PROP_teamsConfig + ".";

	public static final String LOG_DIR = ".." + File.separator + "teamsService" + File.separator; // VAN-57678
	
	private boolean _debugFlag = true;
	private boolean _initedFlag;
	private boolean _activeFlag;
	private boolean _enabledFlag;
	private boolean _startupFlag;
	private CursorDBMgr cursorMgr;

	@SuppressWarnings("unused")
	private long _stopTime = -1;

	private String _prop_prefix;
	private IMConfigDBMgr configDBMgr;
	private int _serverID;

	private DateFormat _logDateFormat;
	private SimpleDateFormat[] _timeFormats;

	@SuppressWarnings("unused")
	private DateFormat dateFormat;

	private FTCalcSyncTime ftCalcSyncTime;

	private FTLogChannel logger = null;
	private FTProperties ftProperties;
	private AlarmInterface alarmMansger = null;
	private Alarm alarm = null;

	private long lastConfigUpdateTime;
	private HashSet<Integer> serverIDs;
	private boolean alertFlag = false;

	private FTServiceMgr _service;

	// Online scheduling properties
	private long _onlineProcessEvery;
	private boolean _onlineEnabled = false;

	// Historical scheduling properties
	private String _historicalTimeZone;
	private String _historicalOccuranceFrequency;
	private String _historicalOccuranceOnceValue;
	private String _historicalOccursEveryFrequency;
	private String _historicalOccursEveryValue;
	private String _historicalOccursEveryStartFrom;
	private String _historicalOccursEveryEndAt;
	private String _historicalDateRangeFrequency;
	private boolean _historicalEnabled = false;
	private boolean _historicalCompleted = false;

	// Common teams properties
	private String _baseUrl;
	private String _clientID;
	private int configID = 1; //hardcoded for now
	private String _clientSecret;

	// scheduling related properties
	private String teamsServiceName;
	private int teamsServiceNum;
	private long teamsServiceState;

	// hidden properties for teams service
	private long _onlineFromDateLong;
	private long _historicalDateRangeFromLong;
	private long _historicalDateRangeToLong;
	private long _historicalDateRangeBetweenFromLong;
	private long _historicalDateRangeBetweenToLong;

	private int intCompanyID;

	@SuppressWarnings("unused")
	private String companyID;

	private boolean needTeamsServiceAlarm = false;
	private Boolean cursorReset = false;
	private TeamsConnectorContext teamsContext;
	private String _domains;
	private String _graphEndPoint;
	private String _authEndPoint;
	private String _ewsEndPoint;
	private String _ewsUsername;
	private String _ewsPassword;
	private String _tenantID;
	private IMAuditChatDBMgr _chatDBMgr = null;;
	private IMAuditChatLoggingDBMgr _chatLoggingDBMgr = null;

	public TeamsService(int teamsServiceNum, String teamsServiceName) {
		this(-1, null, teamsServiceNum, teamsServiceName);
	}

	public TeamsService(int intCompanyID, String companyID, int teamsServiceNum, String teamsServiceName) {
		this(intCompanyID, companyID, teamsServiceNum, teamsServiceName, true);
	}

	public TeamsService(int intCompanyID, String companyID, int teamsServiceNum, String teamsServiceName,
			boolean teamsServiceAlarm) {
		this.intCompanyID = intCompanyID;
		this.companyID = companyID;
		this.teamsServiceName = teamsServiceName;
		this.teamsServiceNum = teamsServiceNum;
		this.teamsServiceState = 1L << teamsServiceNum;
		this.needTeamsServiceAlarm = teamsServiceAlarm;
		try {
			this.cursorMgr = (CursorDBMgr) IMDatabaseMgr.instance().getDBApi(
					IMAdminProps.INT_COMPANYID, IMAdminProps.DB_CONFIG_POOL,
					CursorDBMgr.CURSOR_MGR_NAME);
		} catch (IMDBNotFoundException e) {
			logger.fatal("Coudlnot get cursorMgr - ", e.toString());
			throw new TeamsException("Couldnot get cursorMgr", e);
		}
		
		try {
			_chatDBMgr = (IMAuditChatDBMgr) IMDatabaseMgr.instance().getDBApi(IMAdminProps.INT_COMPANYID,
					IMAdminProps.DB_CONFIG_POOL, IMAuditChatDBMgr.DB_API_NAME);
			_chatLoggingDBMgr = (IMAuditChatLoggingDBMgr) IMDatabaseMgr.instance().getDBApi(IMAdminProps.INT_COMPANYID,
					IMAdminProps.DB_AUDIT_POOL, IMAuditChatLoggingDBMgr.DB_API_NAME);
		} catch (IMDBNotFoundException e) {
			logger.warning("could not initialize chatDBMgr and chatLoggingDBMgr", e);
		}
	}

	long getLastConfigUpdateTime() {
		return lastConfigUpdateTime;
	}

	/**
	 * Called by Teams Service Manager every time UI config is updated.
	 * 
	 * @return
	 * @throws TeamsException
	 */
	public synchronized boolean reInit() throws TeamsException {

		_debugFlag = logger.debug();

		// Check if inited.
		if (!_initedFlag) {
			return false;
		}

		try {
			logger.info("Restarting...");

			disable();

			initializeConfigurations();

			if (logger.debug())
				logger.debug("enable flag: " + _enabledFlag + " alarm flag: " + needTeamsServiceAlarm);

			// Check if enabled and if enabled on this server.
			if (_enabledFlag == true) {
				if ((serverIDs.contains(new Integer(_serverID)) || serverIDs.contains(-1)) && needTeamsServiceAlarm) {
					enable();
				}
				logger.info("Restart complete: teams service enabled for serverID(s): " + serverIDs.toString());
			} else {
				logger.info("Restart complete: teams service disabled.");
			}

		} catch (Exception e) {
			logger.warning(e.getMessage());
			throw new TeamsException("Could not restart teams service. Reason: " + e.getMessage(), e);
		}

		return true;
	}

	public synchronized void reset() {

		_debugFlag = logger.debug();

		// Check if inited.
		if (!_initedFlag) {
			return;
		}

		logger.info("Stopping...");

		disable();

		logger.info("Stopped.");

		// _initedFlag = false;
	}

	public synchronized void enable() throws IMDBException {

		_debugFlag = logger.debug();

		if (_debugFlag)
			logger.debug("Enabling...");

		// Check if already enabled.
		if (_activeFlag) {
			if (_debugFlag)
				logger.debug("Already enabled.");
			return;
		}
		
		if(_historicalEnabled && _historicalCompleted) {
			logger.info("Historical is completed, hence not enabling the scheduler.");
			return;
		}

		try {
			long timeToWait = getNextAlarmTime(false);

			// Make sure alarm is only set once.
			if (alarm == null) {
				alarm = alarmMansger.createAlarm(this, timeToWait, null);
				String date = _logDateFormat.format(new Date(System.currentTimeMillis() + timeToWait));
				logger.info("Next run at: " + date + " for TeamsService : " + teamsServiceName);
			} else {
				logger.info("Alarm already enabled.");
			}

			_activeFlag = true;

		} catch (IMDBException e) {
			logger.warning(e.getMessage());
			throw e;
		}

		if (_debugFlag)
			logger.debug("Enabled.");

		// reset the schedule, we do not want any state
		ftCalcSyncTime.reset();

	}

	private long getNextAlarmTime(boolean moreToProcess) throws IMDBException {
		ftCalcSyncTime = new FTCalcSyncTime();
		long timeToWait = 0;

		if (_onlineEnabled) {
			long interval = _onlineProcessEvery * MSEC_PER_MIN;

			if (interval <= 0) {
				logger.info("Online frequency value for \"Process Every\" is set to ZERO. Waiting for config changes.");
				return WAIT_FOR_CONFIG_CHANGE;
			}

			return interval;

		} else if (_historicalEnabled) {
			try {
				if(_historicalCompleted) {
					timeToWait = STOP_NOW;
				}
				else if (_historicalOccuranceFrequency
						.equalsIgnoreCase(TeamsConstants.HISORICAL_OCCURANCE_FREQUENCY_EVERY)) {
					ftCalcSyncTime.init(_historicalTimeZone, Integer.parseInt(_historicalOccursEveryValue),
							_historicalOccursEveryFrequency, _historicalOccursEveryStartFrom,
							_historicalOccursEveryEndAt);

					timeToWait = ftCalcSyncTime.calcSyncTime();
				} else if (_historicalOccuranceFrequency
						.equalsIgnoreCase(TeamsConstants.HISORICAL_OCCURANCE_FREQUENCY_ONCE)) {
					ftCalcSyncTime.init(_historicalTimeZone, _historicalOccuranceOnceValue);
					timeToWait = ftCalcSyncTime.calcSyncTime();

				}
			} catch (NumberFormatException ne) {
				logger.warning(ne.getMessage(), ne);
			} catch (FTException fe) {
				logger.warning(fe.getMessage(), fe);
			}

			return timeToWait;
		}
		// If we are starting up, wait a few minutes.
		else if (_startupFlag && timeToWait < WAIT_FOR_START) {
			// Wait 5 minutes for the caches to load before processing.
			timeToWait = WAIT_FOR_START;
			logger.info("Starting first service in " + WAIT_FOR_START_MINS + " minutes.");
		} else {
			logger.info("Both Online and Historical are not enabled. Waiting for config changes.");
			return WAIT_FOR_CONFIG_CHANGE;
		}

		return timeToWait;
	}

	public synchronized boolean init(FTServiceMgr service)
			throws TeamsException, SQLException, IMInvalidPropertyTypeException {
		this._service = service;
		// Check if already inited.
		if (_initedFlag) {
			return true;
		}

		try {
			alarmMansger = this._service.getAlarmMgr();

			logger = FTBasicLogMgr.instance().createLogChannel(getClass(), "teams service[" + teamsServiceNum + "]"
					+ (intCompanyID != -1 ? ("[" + intCompanyID + "]") : ""));
			logger.info("Initing Teams Service...");

			configDBMgr = (IMConfigDBMgr) IMDatabaseMgr.instance().getDBApi(IMAdminProps.INT_COMPANYID,
					IMAdminProps.DB_CONFIG_POOL, IMConfigDBMgr.DB_API_NAME);

			_serverID = configDBMgr.getServerID();

			// init the time formats.
			_timeFormats = new SimpleDateFormat[TIME_FORMAT.length];

			TimeZone gmtTZ = TimeZone.getTimeZone("GMT");

			// init the time formats.
			for (int i = 0; i < _timeFormats.length; i++) {
				_timeFormats[i] = new SimpleDateFormat(TIME_FORMAT[i], Locale.US);
				_timeFormats[i].setTimeZone(gmtTZ);
			}

			dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			_logDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL);

			initializeConfigurations();
			_startupFlag = true;
			if (logger.debug()) {
				logger.debug("enable flag: " + _enabledFlag + " alarm flag: " + needTeamsServiceAlarm);
			}

			// Check if enabled and if enabled on this server.
			if (_enabledFlag == true) {
				if ((serverIDs.contains(new Integer(_serverID)) || serverIDs.contains(-1)) && needTeamsServiceAlarm) {
					enable();
				}
				logger.info("Init complete: teams service enabled for serverID(s): " + serverIDs.toString()
						+ ", teamsState: " + "0x" + Long.toHexString(teamsServiceState).toUpperCase());
			} else {
				logger.info("Init complete: teams service disabled.");
			}

			_startupFlag = false;
			_initedFlag = true;
			raiseEvent();
		} catch (IMDBException e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
			raiseEvent("DB Error during teams service init.");
			throw new TeamsException("Could not start teams service. Reason: " + e.getMessage(), e);
		} catch (IMInvalidPropertyTypeException ie) {
			raiseEvent("Invalid setting for teams service setting.");
			throw ie;
		}

		return true;
	}

	// Success event
	private void raiseEvent() {

		FTEventChannel ftEventChannel = FTEventChannelFactory.createEventChannel();
		if (ftEventChannel != null && alertFlag) {
			FTHashtable<String, String> event = new FTHashtable<String, String>();
			event.put("teamsServiceName", teamsServiceName);
			event.put("teamsServiceNumber", teamsServiceNum);
			event.put("status", "0");
			ftEventChannel.getEventListener().onEvent(IMAdminEventConstants.TEAMSSERVICE_FAILURE_NOTIFICATION_TYPE,
					event);
		}
	}

	// Failed event
	private void raiseEvent(String errMessage) {

		alertFlag = true;
		FTEventChannel ftEventChannel = FTEventChannelFactory.createEventChannel();
		if (ftEventChannel != null) {
			FTHashtable<String, String> event = new FTHashtable<String, String>();
			event.put("teamsServiceName", teamsServiceName);
			event.put("teamsServiceNumber", teamsServiceNum);
			event.put("status", "1");
			event.put("reason", errMessage);
			ftEventChannel.getEventListener().onEvent(IMAdminEventConstants.TEAMSSERVICE_FAILURE_NOTIFICATION_TYPE,
					event);
		}
	}

	/**
	 * Initialize the configuration.
	 *
	 * @exception IMDBException
	 *                Could not get properties.
	 */
	private synchronized void initializeConfigurations() throws IMDBException, SQLException, IMInvalidPropertyTypeException {

		// Get the required properties.
		ftProperties = configDBMgr.getFreshProps(intCompanyID);
		_prop_prefix = PROP_teamsService + "." + teamsServiceNum + "." + PROP_teamsConfig + ".";

		try {
			String teamsServiceNamePropValue = ftProperties.getStringValue(_prop_prefix + PROP_teamsService_online_fromDate);

			if (teamsServiceNamePropValue != null && !teamsServiceNamePropValue.equals(teamsServiceName)) {
				teamsServiceName = teamsServiceNamePropValue;
				logger = FTBasicLogMgr.instance().createLogChannel(getClass(),
						"TeamsService" + teamsServiceNum + "[" + teamsServiceName + "][" + intCompanyID + "]");
			}

			String serverIDsAsString;

			_enabledFlag = ftProperties.getBooleanValue(_prop_prefix + PROP_teamsService_enabled);
			lastConfigUpdateTime = ftProperties.getLongValue(_prop_prefix + PROP_teamsService_lastConfigUpdateTime);
			_enabledFlag = ftProperties.getBooleanValue(_prop_prefix + PROP_teamsService_enabled);

			if (!readProperties(ftProperties, _prop_prefix)) {
				throw new TeamsException("All properties were not read. The service cannot be registered");
			}

			if (_historicalEnabled)
				serverIDsAsString = ftProperties.getStringValue(_prop_prefix + PROP_teamsService_serverIDs_historical);
			else
				serverIDsAsString = ftProperties.getStringValue(_prop_prefix + PROP_teamsService_online_serverIDs);

			serverIDs = new HashSet<Integer>();

			if (serverIDsAsString != null && serverIDsAsString.length() > 0) {
				StringTokenizer stringTokenizer = new StringTokenizer(serverIDsAsString, " ");

				while (stringTokenizer.hasMoreTokens()) {
					String serverID = stringTokenizer.nextToken();
					serverIDs.add(new Integer(serverID));
				}
			}
			if (_debugFlag) {
				logger.debug("ServerIDs: " + serverIDs.toString());
			}

			// _dateFormat.setTimeZone(TimeZone.getTimeZone(_timezoneToProcessInters));
			
			if(_onlineEnabled) {
				cursorReset = Boolean.valueOf(ftProperties.getStringValue(
						_prop_prefix + PROP_teamsService_cursor_reset));
			} else if(_historicalEnabled) {
				cursorReset = Boolean.valueOf(ftProperties.getStringValue(
						_prop_prefix + PROP_teamsService_cursor_reset_historical));
			}

		} catch (Exception e) {
			throw new IMDBException("Could not get server property: " + e.getMessage(), e);
		}
	}

	private boolean readProperties(FTProperties props, String prop_prefix) {
		boolean completed = false;
		try {
			_enabledFlag = props.getBooleanValue(prop_prefix + PROP_teamsService_enabled);
			_baseUrl = props.getStringValue(prop_prefix + PROP_teamsService_base_url);
			_tenantID = props.getStringValue(prop_prefix + PROP_teamsService_tenantID);
			_clientID = FTSecurityUtils.ungarbleString(props.getStringValue(prop_prefix + PROP_teamsService_clientID));
			_clientSecret = FTSecurityUtils
					.ungarbleString(props.getStringValue(prop_prefix + PROP_teamsService_clientSecret));
			_domains = props.getStringValue(prop_prefix + PROP_teamsService_domainNames);
			_graphEndPoint = props.getStringValue(TEAMS_PROP_COMMON_PREFIX + PROP_teamsService_graphEndpointUrl);
			_authEndPoint = prepareAuthEndPointUrl(props,_tenantID);
			_ewsEndPoint = props.getStringValue(TEAMS_PROP_COMMON_PREFIX + PROP_teamsService_ewsEndpointUrl);
			_onlineEnabled = props.getBooleanValue(prop_prefix + PROP_teamsService_enabled_Online);
			_historicalEnabled = props.getBooleanValue(prop_prefix + PROP_teamsService_historical_enabled);
			_ewsUsername = props.getStringValue(prop_prefix + PROP_teamsService_ewsUsername);
			_ewsPassword = FTSecurityUtils
					.ungarbleString(props.getStringValue(prop_prefix + PROP_teamsService_ewsPassword));
			
			if (_onlineEnabled) {
				_onlineProcessEvery = props
						.getLongValue(prop_prefix + PROP_teamsService_continuous_checkForProcessData);
				_onlineFromDateLong = props.getLongValue(prop_prefix + PROP_teamsService_online_fromDate);
			}

			if (_historicalEnabled) {
				// Historical scheduling properties
				_historicalTimeZone = props.getStringValue(prop_prefix + PROP_teamsService_historical_timezone);
				_historicalOccuranceFrequency = props
						.getStringValue(prop_prefix + PROP_teamsService_historical_occurance);
				_historicalOccuranceOnceValue = props.getStringValue(prop_prefix + PROP_teamsService_historical_time);
				_historicalOccursEveryFrequency = props
						.getStringValue(prop_prefix + PROP_teamsService_historical_hourOrMin);
				_historicalOccursEveryValue = props
						.getStringValue(prop_prefix + PROP_teamsService_historical_everyHourMin);
				_historicalOccursEveryStartFrom = props
						.getStringValue(prop_prefix + PROP_teamsService_historical_startingAt);
				_historicalOccursEveryEndAt = props.getStringValue(prop_prefix + PROP_teamsService_historical_endingAt);
				_historicalDateRangeFrequency = props
						.getStringValue(prop_prefix + PROP_teamsService_historical_dateRange);

				/**
				 * Check historical from date if from date is greater than
				 * cursor then pick from date else pick cursor.
				 */
				if (_historicalDateRangeFrequency.equalsIgnoreCase(HISTORICAL_DATE_FROM)) {
					_historicalDateRangeFromLong = props
							.getLongValue(prop_prefix + PROP_teamsService_historical_fromDate);
				}

				else if (_historicalDateRangeFrequency.equalsIgnoreCase(HISTORICAL_DATE_TO)) {
					_historicalDateRangeToLong = props.getLongValue(prop_prefix + PROP_teamsService_historical_toDate);
				}

				else if (_historicalDateRangeFrequency.equalsIgnoreCase(HISTORICAL_DATE_BETWEEN)) {
					_historicalDateRangeBetweenFromLong = props
							.getLongValue(prop_prefix + PROP_teamsService_historical_fromBetweenDate);
					_historicalDateRangeBetweenToLong = props
							.getLongValue(prop_prefix + PROP_teamsService_historical_toBetweenDate);
				}

				_historicalCompleted = props
						.getBooleanValue(prop_prefix + PROP_teamsService_historical_import_completed);
			}
			completed = true;
		} catch (FTPropsKeyNotFoundException fp) {
			logger.warning(fp.getMessage(), fp);
			completed = false;
		} catch (FTPropsValueIllegalException fe) {
			logger.warning(fe.getMessage(), fe);
			completed = false;
		}
		return completed;
	}
	
	private String prepareAuthEndPointUrl(FTProperties props, String tenantId) throws FTPropsKeyNotFoundException {
		String authEndPointPattern = props.getStringValue(TEAMS_PROP_COMMON_PREFIX + PROP_teamsService_authEndpointUrl);
		return authEndPointPattern.replace("%{tenantId}", tenantId);
	}

	/**
	 * Disable the teams service.
	 */
	public synchronized void disable() {

		_debugFlag = logger.debug();

		if (_debugFlag)
			logger.debug("Disabling...");

		// Check if enabled.
		if (!_activeFlag) {
			if (_debugFlag)
				logger.debug("Already disabled.");
			return;
		}

		// Check if we have an alarm.
		if (alarm != null) {
			alarmMansger.removeAlarm(alarm);
			alarm = null;
		}

		_activeFlag = false;

		if (_debugFlag)
			logger.debug("Disabled.");
	}

	private void invokeService() throws Exception {
		logger.info("Teams service started..");

		String baseUrl = (_baseUrl == null || _baseUrl.isEmpty()) ? TeamsConstants.DEFAULT_BASE_URL : _baseUrl;

		teamsContext = new TeamsConnectorContext();
		teamsContext.setBaseUrl(baseUrl);
		teamsContext.setGraphEndPoint(_graphEndPoint);
		teamsContext.setAuthEndPoint(_authEndPoint);
		teamsContext.setEwsEndPoint(_ewsEndPoint);
		teamsContext.setClientId(_clientID);
		teamsContext.setClientSecret(_clientSecret);
		teamsContext.setDomains(getDomainNameList());
		teamsContext.setServiceNum(teamsServiceNum+"");
		teamsContext.setConfigId(teamsServiceNum);
		teamsContext.setCompanyId(intCompanyID);
		teamsContext.setNetworkId(IMConstants.NETWORK_TEAMS);
		teamsContext.setExchangeUsername(_ewsUsername);
		teamsContext.setExchangePassword(_ewsPassword);
		
		//Check if we are able to retrieve the access token. Else return from here.
		String accessToken = null;
		try {
			TeamsGraphApiClient teamsGraphApiClient = getTeamsGraphApiClient(teamsContext);
			accessToken = teamsGraphApiClient.fetchAccessToken();
		} catch (Exception e) {
			throw new TeamsException("Failed to fetch accessToken", e);
		}

		if (accessToken == null || accessToken.trim().isEmpty()) {
			throw new TeamsException("Invalid access token");
		}

		if (logger.debug()) {
			logger.debug("Checking if the access token is valid.");
		}
		
		ITeamsController teamsController = new TeamsControllerImpl(this);
		teamsController.init(teamsContext);
		teamsController.execute();

		if (logger.debug()) {
			logger.debug("Updating Server Properties now.");
		}
		boolean serverPropertiesUpdate = updateServerProperties();
		if (!serverPropertiesUpdate) {
			logger.warning("Server Properties update failed. Please check log for further details.");
		} else {
			if (logger.debug()) {
				logger.debug("Server Properties updated successfully.");
			}
		}
		logger.info("Teams service finished..");
	}

	private List<String> getDomainNameList() {
		if(StringUtils.isNotEmpty(_domains )) {
			String[] domains = _domains.trim().split(",");
			List<String> domainList = new ArrayList<String>();
			for (String domain : domains) {
				domainList.add(domain.trim().toLowerCase());
			}
			return domainList;
		}
		return null;
	}

	/**
	 * Update the server properties with latest cursors after a successful run.
	 * 
	 * @return
	 */
	private boolean updateServerProperties() {
		boolean status = false;
		try {
			if (ftProperties == null) {
				ftProperties = configDBMgr.getFreshProps(intCompanyID);
			}

			_prop_prefix = PROP_teamsService + "." + teamsServiceNum + "." + PROP_teamsConfig + ".";

			if (_historicalEnabled) {
				//Here we say the job is completed after the first run because there is no batching concept.
				_historicalCompleted = isHistoricalCompleted();
				IMConfigDBMgr.instance().updateServerProp(_prop_prefix + PROP_teamsService_historical_import_completed,
						String.valueOf(_historicalCompleted), intCompanyID);

				if (logger.debug()) {
					logger.debug("Server Property Updated : " + _prop_prefix
							+ PROP_teamsService_historical_import_completed);
				}
			}

			status = true;
		} catch (IMDBException e) {
			status = false;
			logger.warning(e.getMessage(), e);
		}
		return status;
	}
	
	private boolean isHistoricalCompleted() throws IMDBException {
		List<Long> allCursorValues = cursorMgr.getAllCursorValues(configID, intCompanyID);
		long historicalEndTime = getHistoricalEndTime();
		boolean isCompleted = true;
		for (Long cursorValue : allCursorValues) {
			if (cursorValue != null) {
				isCompleted &= isCompleted(cursorValue, historicalEndTime);
			}
		}
		return isCompleted;
	}
	
	private boolean isCompleted(long start, long end) {
		return start >= end;
	}

	// Return the time until the next alarm.
	private synchronized long rescheduleAlarm(long msecsUntilAlarm) {

		if (msecsUntilAlarm != STOP_NOW) {
			String date = _logDateFormat.format(new Date(System.currentTimeMillis() + msecsUntilAlarm));
			logger.info("Teams Service Next run at : " + date);
		} else {
			logger.info("Alarm stopped.");
		}

		// Clear the stop time if it was set.
		_stopTime = -1;

		return msecsUntilAlarm;
	}

	/**
	 * Called by the alarm to start teams service.
	 *
	 * @param alarm
	 *            this alarm object.
	 * @param msec
	 *            time of alarm.
	 * @param o
	 *            pass thru object.
	 * @return time until the next alarm.
	 */

	public long alarm(Alarm alarm, long msec, Object o) {

		_debugFlag = logger.debug();

		try {
			if (_debugFlag) {
				logger.debug("Running...");
				logger.debug("Checking config...");
			}

			// Check for config changes.
			initializeConfigurations();
			
			if(!isNetworkEnabled()) {
				logger.warning("Teams Network not enabled, interrupting task");
				return rescheduleAlarm(WAIT_FOR_CONFIG_CHANGE);
			}

			// Check if enabled and enabled on this server.
			if (_enabledFlag == true && (serverIDs.contains(new Integer(_serverID)) || serverIDs.contains(-1))) {
				if (_debugFlag) {
					logger.debug("Enabled.");
				}
			} else {
				if (_debugFlag) {
					logger.debug("Not enabled.");
				}
				return rescheduleAlarm(STOP_NOW);
			}

			if (_debugFlag) {
				logger.debug("Setting teams service server...");
			}

			String property = PROP_maxTeamsServicePerServer;
			int maxTeamsServicesPerServer = ImporterUtil.getMaxProcessesPerServer(property, _serverID, "Teams Service",
					teamsServiceNum, intCompanyID, logger);
			if (maxTeamsServicesPerServer == 0) {
				logger.warning(
						"Property " + property + "NOT found, or does not have proper value. Waiting for 10 minutes");
				return rescheduleAlarm(WAIT_FOR_CONFIG_CHANGE);
			}

			// Check if still enabled.
			if (!_enabledFlag) {
				if (_debugFlag) {
					logger.debug("Not enabled.");
				}
				return rescheduleAlarm(STOP_NOW);
			}
			
			if(_historicalEnabled && _historicalCompleted) {
				logger.info("Historical completed, Hence stopping scheduler.");
				return rescheduleAlarm(STOP_NOW);
			}

			// Handling stale interactions at the start of service
			logger.info("Started ending stale interactions for Teams.");
			AuditUtil.endStaleInters(
						_chatDBMgr, _chatLoggingDBMgr, System.currentTimeMillis(), intCompanyID,
						logger, IMConstants.NETWORK_TEAMS);
			logger.info("Completed ending stale interactions for Teams.");
			
			String svcName = ftProperties.getStringValue(_prop_prefix + PROP_teamsService_serviceName);

			logger.info("Service Running for : " + svcName);
			long startTime = System.currentTimeMillis();
			invokeService();

			logger.info("Total time taken by " + svcName + " : " + (System.currentTimeMillis() - startTime)
					+ " milliseconds");

			try {
				long timeToWait = getNextAlarmTime(true);

				if (_debugFlag)
					logger.debug("Finished running. Waiting for next check in " + (timeToWait > 0 ? timeToWait / 1000 : 0)
							+ " secs.");

				return rescheduleAlarm(timeToWait);
			} catch (Exception e) {
				logger.warning(e.getMessage(), e);
				return rescheduleAlarm(WAIT_FOR_CONFIG_CHANGE);
			}

		} catch (IMDBException e1) {
			logger.warning(e1.getMessage(), e1);
			return rescheduleAlarm(WAIT_FOR_CONFIG_CHANGE);
		} catch (FTPropsKeyNotFoundException e1) {
			logger.warning(e1.getMessage(), e1);
			return rescheduleAlarm(WAIT_FOR_CONFIG_CHANGE);
		} catch (Exception e1) {
			logger.warning(e1.getMessage(), e1);
			return rescheduleAlarm(WAIT_FOR_CONFIG_CHANGE);
		} finally {
			// reset the schedule, we do not want any state
			ftCalcSyncTime.reset();
		}
	}
	
	
	private boolean isNetworkEnabled() {
		return IMAuditorLicenseReader.getInstance().isNetworkEnabled(IMConstants.NETWORK_TEAMS, intCompanyID);
	}
	
	
	
	/**
	 * @param time
	 * @return one year before input time
	 */
	private long getBeforeTime(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		int toYear = cal.get(Calendar.YEAR);
		cal.set(Calendar.YEAR, toYear - 1);
		return cal.getTimeInMillis();
	}
	
	private long getCursor(String entityID) {
		try {
			return cursorMgr.getCursorValue(configID, entityID, intCompanyID);
		} catch (IMDBException e) {
			logger.debug("Cursor not found for entity: "+entityID);	
		}
		return -1;
	}
	
	private boolean updateCursor(String entityID, long cursor) throws IMDBException {
		try {
			if(logger.debug()) {
				logger.debug("updating cursor for entity: " + entityID);
			}
			cursorMgr.updateCursor(configID, entityID,
					cursor, intCompanyID);
			logger.debug("Updated cursor for entity: "+entityID+","
					+ " cursorValue: "+ cursor);
			return true;
		} catch (IMDBException e) {
			//site cursor not found, insert it
			//Don't print the stack trace as it is expected when record is not found in db.
			if(logger.debug()) {
				logger.debug("Cursor not found for entity " + entityID + ", inserting it.");
			}
			cursorMgr.insertCursor(configID, entityID, cursor, intCompanyID);
			if(logger.debug()) {
				logger.debug("Inserted cursor for entity: "+entityID+", cursorValue: "+cursor);
			}
		}
		return false;		
	}

	private TeamsTimeRange getOnlineTimeRange(String entityID) throws IMDBException {
		long cursor = getCursor(entityID);
		long endTime = 0;
		// [TODO] : CHeck _onlineFromDateLongCursor is populating by default when i save the date
		TeamsTimeRange range = new TeamsTimeRange();
		if(cursor == -1) {
			cursor = _onlineFromDateLong;
			updateCursor(entityID, cursor);
		}
		endTime = -1;
		range.setStartTime(cursor);
		range.setEndTime(endTime);		
		return range;
	}
	
	private TeamsTimeRange getHistoricalTimeRange(String entityID) throws IMDBException {	
		long cursor = getCursor(entityID);
		long startTime = 0, endTime = 0;
		// [TODO] : CHeck _onlineFromDateLongCursor is populating by default when i save the date
		TeamsTimeRange range = new TeamsTimeRange();
		if(cursor == -1) {
			if (_historicalDateRangeFrequency.equalsIgnoreCase(HISTORICAL_DATE_FROM)) {
				cursor = startTime = _historicalDateRangeFromLong;
			} else if (_historicalDateRangeFrequency.equalsIgnoreCase(HISTORICAL_DATE_TO)) {
				cursor = startTime = getBeforeTime(_historicalDateRangeToLong);
			} else if (_historicalDateRangeFrequency.equalsIgnoreCase(HISTORICAL_DATE_BETWEEN)) {
				cursor = startTime = _historicalDateRangeBetweenFromLong;
			}
			updateCursor(entityID, cursor);
		} else {
			startTime = cursor;
		}
		endTime = getHistoricalEndTime();
		if(isCompleted(cursor, endTime)) {
			throw new HistoricalCompletedException();
		}
		
		range.setStartTime(startTime);
		range.setEndTime(endTime);		
		return range;
	}
	
	private long getHistoricalEndTime() {
		if (HISTORICAL_DATE_FROM.equalsIgnoreCase(_historicalDateRangeFrequency)) {
			long upperBound;
			try {
				upperBound = ftProperties.getLongValue(
						PROP_teamsService + "." + teamsServiceNum + "." +
						PROP_teamsConfig + ".from.cursor.upper.bound");
			} catch (FTPropsKeyNotFoundException e) {
				logger.warning("From cursor upperbound not found");
				upperBound = System.currentTimeMillis();
			} catch (FTPropsValueIllegalException e) {
				logger.warning("From cursor upperbound has illegal value");
				upperBound = System.currentTimeMillis();
			}
			return upperBound;
		} else if (HISTORICAL_DATE_TO.equalsIgnoreCase(_historicalDateRangeFrequency)) {
			return _historicalDateRangeToLong;
		} else if (HISTORICAL_DATE_BETWEEN.equalsIgnoreCase(_historicalDateRangeFrequency)) {
			return _historicalDateRangeBetweenToLong;
		}
		return -1;
	}
	
	public TeamsTimeRange getTimeRange(String entityID) {
		try {
			if(_historicalEnabled) {
				if(cursorReset) {
					logger.info("'From' date has been updated for configID: "+
							configID+" ... Reseting all Cursor Values for this configuration");
					//RESET all cursors to -1 for this configID
					cursorMgr.resetCursors(configID, -1, intCompanyID);
					logger.info("All site cursors for configID: "+
							configID+" has been reset.");
					// Update the sp.farm.%.cursor.updated server props to FALSE
					configDBMgr.updateServerProp(_prop_prefix +
							PROP_teamsService_cursor_reset_historical, "false", intCompanyID);
					ftProperties.put(_prop_prefix +
							PROP_teamsService_cursor_reset_historical, "false");
					cursorReset = false;
				
				}
				return getHistoricalTimeRange(entityID);
			} else if(_onlineEnabled) {
				if(cursorReset) {
				logger.info("'From' date has been updated for configID: "+
						configID+" ... Reseting all Cursor Values for this configuration");
				//RESET all cursors to -1 for this configID
				cursorMgr.resetCursors(configID, -1, intCompanyID);
				logger.info("All site cursors for configID: "+
						configID+" has been reset.");
				configDBMgr.updateServerProp(_prop_prefix +
						PROP_teamsService_cursor_reset, "false", intCompanyID);
				ftProperties.put(_prop_prefix +
						PROP_teamsService_cursor_reset, "false");
				cursorReset = false;
			
				}
				return getOnlineTimeRange(entityID);
			}
		}catch (IMDBException e) {
			logger.warning("failed to get cursor value for entity - " + entityID);
		}
		return new TeamsTimeRange();
	}
	

	public boolean updateTimeRange(
			String entityID, long cursor) throws IMDBException {
		return updateCursor(entityID, cursor);
	}

	public FTServiceMgr getFTServiceMgr() {
		return _service;
	}

	public TeamsConnectorContext getTeamsContext() {
		return teamsContext;
	}
}
