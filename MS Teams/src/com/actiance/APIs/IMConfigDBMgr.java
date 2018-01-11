package com.actiance.APIs;

/*
 * Copyright (c) 2004 FaceTime Communications, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of FaceTime
 * Communications, Inc. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with FaceTime.
 *
 * FACETIME MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. FACETIME SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED
 * BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE
 * OR ITS DERIVATIVES.
 */



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import com.facetime.ftcore.alarm.Alarm;
import com.facetime.ftcore.alarm.Alarmable;
import com.facetime.ftcore.alarm.lowresalarm.LowResAlarm;
import com.facetime.ftcore.service.ServiceException;
import com.facetime.ftcore.service.ServiceInterface;
import com.facetime.ftcore.service.ServiceLocatorInterface;
import com.facetime.ftcore.service.ServiceStateException;
import com.facetime.ftcore.sql.FTSQLConnection;
import com.facetime.ftcore.sql.FTSQLPreparedStatement;
import com.facetime.ftcore.sql.FTSQLSeroptoOracleDriver;
import com.facetime.ftcore.sql.FTSQLTransaction;
import com.facetime.ftcore.sql.FTSQLUtils;
import com.facetime.ftcore.util.FTConfigMgr;
import com.facetime.ftcore.util.FTConfigUpdateListener;
import com.facetime.ftcore.util.FTErrorConst;
import com.facetime.ftcore.util.FTException;
import com.facetime.ftcore.util.FTHashtable;
import com.facetime.ftcore.util.FTLogMgr;
import com.facetime.ftcore.util.FTProperties;
import com.facetime.ftcore.util.FTPropsValueIllegalException;
import com.facetime.ftcore.util.FTServiceMgr;
import com.facetime.ftcore.util.FTServiceStateException;
import com.facetime.imcoreserver.admin.CompanyManager;
import com.facetime.imcoreserver.admin.IMAdminProps;
import com.facetime.imcoreserver.logging.LogHelper;
import com.facetime.imcoreserver.logging.LogHelperImpl;
import com.facetime.imcoreserver.reports.display.ResBundleHelper;
import com.facetime.imcoreserver.utils.DirectoryAssistance;
import com.facetime.imcoreserver.utils.IPCMessageUtil;
import com.facetime.imstack.imapi.IMConstants;

/**
 * @author	John Onusko
 */
public class IMConfigDBMgr
	extends 	IMDBApi
	implements 	IMAdminProps,
				FTConfigMgr,
				ServiceInterface
				,Alarmable 
				{

	public static final String 	DB_API_NAME 				= "IMConfigDBMgr";	// API name.
	public static final long 	DEFAULT_REFRESH_INTERVAL 	= 60 * 1000;		// Default refresh interval in msecs - 1 minute.

    
	private LogHelper mLogHelper;
    
	/**
     * This method returns the first created instance of the
     * <code>IMConfigDBMgr</code> component. For backward
     * compatibility.
     *
     * @return  The <code>IMConfigDBMgr</code> instance.
     */
    public static IMConfigDBMgr instance() {
    	return (IMConfigDBMgr) IMDatabaseMgr.instance().getDBApi(DB_CONFIG_POOL, DB_API_NAME);    			
    }

    // Class to store company props.
	private class CompanyProps {
		int						_intCompanyID;
		FTProperties			_props;
		HashMap<String,Integer>	_priorities;

		CompanyProps(int intCompanyID) {
			_intCompanyID	= intCompanyID;
			_props			= new FTProperties();
			_priorities		= new HashMap<String,Integer>();
			// Init the global perms.
			_props.put(PROP_im_globalPerms, new FTHashtable<String,String>());
		}
		
		CompanyProps(int intCompanyID, FTProperties initProps) {
			_intCompanyID	= intCompanyID;
			_props			= new FTProperties(initProps);
			_priorities		= new HashMap<String,Integer>();
			// Init the global perms.
			_props.put(PROP_im_globalPerms, new FTHashtable<String,String>());
		}

		int getIntCompanyID() {
			return _intCompanyID;
		}
		
		FTProperties getProps() {
			return _props;
		}
		
		HashMap<String,Integer> getPriorities() {
			return _priorities;
		}
	}
	
	protected HashMap<Integer,CompanyProps>	_companyPropsMap;		// map of server properties by company ID.
	private long              				_cacheRefreshInterval;  // cache refresh interval in msecs.
	private Map<Integer, Long>				_cacheUpdateTimeMap;		// last cache update time.
	protected int								_serverID;				// server ID.
	protected String 							_productName;
	protected String 							_fullProductName;
	protected	boolean							_initedFlag;
	private LowResAlarm						_lowResAlarm;
	private Alarm							_updateAlarm;
	//private boolean                         _isOracleDb = false; //Not being used
	private CompanyManager					_companyManager;

	/**
	 * This constructor creates and initializes a <code>IMConfigDBMgr</code>.
	 */
	public IMConfigDBMgr() {
		_companyPropsMap		= new HashMap<Integer,CompanyProps>();
		_cacheRefreshInterval   = DEFAULT_REFRESH_INTERVAL;
		_cacheUpdateTimeMap     = new HashMap<Integer, Long>();
		_initedFlag 			= false;
	}

	/**
	 * Initialize the DB API.
	 *
	 * @param 	connPool		Connection pool.
	 * @param 	props			Initialization properties.
	 * @param	logMgr			Log manager.
	 * @throws 	IMDBException	Could not initialize.
	 */
	public synchronized void init(	String 	connPool,
									FTProperties	props,
									FTLogMgr		logMgr)
		throws  IMDBException {
		
	   	if (_initedFlag) {
    		_log.debug("Already inited.");
    		throw new IMDBException("Already inited.");
    	}

		super.init(connPool, props, logMgr);

		try {
			_serverID = props.getIntValue(IMAdminProps.COM_FACETIME_SERVER_ID);

			_log.debug("Initing for serverID: " + _serverID + "...");
			
			_listeners = Collections.synchronizedList(new ArrayList<FTConfigUpdateListener>());
	    	_prefixes = Collections.synchronizedList(new ArrayList<ArrayList<String>>());
	    	
			_initDB();

			try {
				ResourceBundle bundle = ResBundleHelper.getInstance().getBundle(ResBundleHelper.RES_COMINFO, Locale.getDefault());
			
				_productName 		= bundle.getString("title.productName");
				_fullProductName 	= bundle.getString("title.fullProductName");
			} catch (MissingResourceException mre) {
				_log.warning("Resource bundle is missing: " + mre.getMessage());
			}
			
			if (_productName == null) {
				_productName = "USG";
			}
			if (_fullProductName == null) {
				_fullProductName = "USG";
			}
			
			_cacheUpdateTimeMap.put(IMDatabaseMgr.instance().getDBId(IMDatabaseMgr.COMMON_DATA_STORE_ID), -1L);
			// Init the global props.
			_loadGlobalProps();
			
			//Store the TimeZone offset to the DB
			//startTimeZoneOffsetTimer(); //VAN-62168 commenting as it is belongs to USG.

			
            //--------------------------------------------
            // Initialize: System wide log files
            //--------------------------------------------
            mLogHelper = new LogHelperImpl(getProps(IMCompany.DEFAULT_COMPANY_ID));            

            // Create an update alarm.
            _lowResAlarm = new LowResAlarm(_log, 1);
            _updateAlarm = _lowResAlarm.createAlarm(this, _cacheRefreshInterval, null);
            _log.debug("Started update alarm."); 
            
           
			_initedFlag = true;

			_log.debug("Inited.");
			//_isOracleDb = getDbType().equalsIgnoreCase(FTSQLSeroptoOracleDriver.DATABASE_TYPE);

		} catch (IMDBException e) {
			throw e;
		} catch (FTException e) {
			throw new IMDBException ("Could not initialize. Reason: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Initialize the DB for this API.
	 *
	 * @exception 	IMDBException  	Could not initialize DB.
	 */
	protected void _initDB()
		throws	IMDBException {

		final int QUERY_TIMEOUT_VALUE = 30; // 30 seconds.

		String[][] sqlStrs;

        // Initialize stmt array based on db type
        if (getDbType().equals("MSSQL")) {
            sqlStrs = IMConfigDB_MSSQL.sqlStrs;

        } else if (getDbType().equals("Oracle")) {
            sqlStrs = IMConfigDB_Oracle.sqlStrs;

        } else if (getDbType().equals("MySQL")) {
            sqlStrs = IMConfigDB_MySQL.sqlStrs;

        } else {
            throw new IMDBException("Unsupported database type.");
        }

        if (IMDBApi.canPrepareStatements(sqlStrs)) {
        	prepareStatements(sqlStrs, QUERY_TIMEOUT_VALUE);
		}
		
		if (IMDBApi.canPrepareStatements(IMConfigDB_Common.sqlStrs)) {
			prepareStatements(IMConfigDB_Common.sqlStrs, QUERY_TIMEOUT_VALUE);
		}  
	}

    /**
     * Reset the DB API.
     */
    public synchronized void reset() {
    	if (!_initedFlag) {
    		return;
    	}

    	// Cleanup.
    	if (_lowResAlarm != null) {
    		_lowResAlarm.removeAlarm(_updateAlarm);
    		_lowResAlarm.kill();
	    	_updateAlarm = null;
	    	_lowResAlarm = null;
    	}
    	
    	_companyPropsMap.clear();
    	_initedFlag		= false;
    }

	/**
	 * Get the DB API name.
	 *
	 * @return the DB APi name.
	 */
	public String getDBApiName() {
		return DB_API_NAME;
	}

	/**
	 * Return the serverID.
	 *
	 * @return the serverID.
	 */
	public int getServerID() {
		return _serverID;
	}
	
    /**
     * It gets the LogHelper, which provides log file names managed by all
     * modules.  Such log files typically roll over and in some cases, the
     * rolled-over files will be also managed for uploading to a remote
     * server and/or for deletion.
     * @return  LogHelper       the helper class providing useful log file
     *                          management methods.
     */
    public LogHelper getLogHelper() {
        return mLogHelper;
    }

	/**
	 * Get a fresh copy of the server properties as a name/value map.
	 * NOTE: this method is almost never needed. Use getProps().
	 *
	 * @return						the server properties.
	 * @exception	IMDBException	Database error.
	 */
	/*public FTProperties getFreshProps()
		throws	IMDBException {

		_updateCache(-1);
		
		CompanyProps companyProps = null;
		synchronized (_companyPropsMap) {
			// Global props are always loaded.
			companyProps = (CompanyProps) _companyPropsMap.get(-1);
		}
		
		return companyProps.getProps();
	}
*/
	/**
	 * FTProperties doesn't removes the entries though respective records are deleted from DB. Calling this method 
	 * after deleting records will help to keep FTProperties updated.
	 * @param keyLike : specifies the key, FTProperty entry whose key contains this key will be removed. 
	 * @param companyId
	 */
	public void removeProperties(String keyLike, int companyId){
		FTProperties properties = getProps(companyId);
		Set<String> keys = properties.keySet();
		Iterator<String> keyIterator = keys.iterator();
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			if(key.contains(keyLike)){
				properties.remove(key);
			}
		}
			
	}
	
	/**
	 * Get a fresh copy of the server properties as a name/value map.
	 * NOTE: this method is almost never needed. Use getProps().
	 *
	 * @param		intCompanyID	the internal company ID or -1 for global only.
	 * @return						the server properties.
	 * @exception	IMDBException	Database error.
	 */
	public FTProperties getFreshProps(int intCompanyID)
		throws	IMDBException {

		CompanyProps companyProps = null;
		
		/*synchronized (_companyPropsMap) {
			companyProps = (CompanyProps) _companyPropsMap.get(intCompanyID);
		}*/
		
		if (intCompanyID == IMDatabaseMgr.COMMON_DATA_STORE_ID) { // intCompany should be Default one and not CDS ID
			/**
			 *  In VAN-53185 it was seen that a LDAP process with companyID -300 was running
			 *  When call with any company id is made, an entry  is made with that company id  in the cache
			 *  Subsequently listeners etc could be notified on that companyID
			 *  
			 */
			intCompanyID = IMCompany.DEFAULT_COMPANY_ID;  // if it is CDS ID, assign default id to intCompanyID
			if (_log.debug()) {
				/**
				 * Prints something like this...may be need to be replace by Default Company ID
				 * java.lang.Thread.getStackTrace(Thread.java:1479) <-- com.facetime.imcoreserver.db.IMConfigDBMgr.getFreshProps(IMConfigDBMgr.java:393) 
				 * <-- com.facetime.imcoreserver.db.IMConfigDBMgr.getProps(IMConfigDBMgr.java:482) <--
				 *  com.facetime.imcoreserver.registration.AuthenticationServletFilter.init(AuthenticationServletFilter.java:163) <-- 
				 * Don't want to print the complete trace, so we stop appending after facetime/actiance elements end.
				 */
				_log.debug("CommonDataStore ID used instead of Default Company ID in this call: " 
						 );
				StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
				StringBuilder sb = new StringBuilder();
				boolean actianceCodeStackStarted = false;
				for(StackTraceElement ste:stackTrace) {
					//consider only facetime or actiance calls
					String str = ste.toString();
					if (str != null && str.startsWith("com.facetime") || str.startsWith("com.actiance")) {
						actianceCodeStackStarted = true;
						sb.append(str + " <-- ");
					} else {
						if (actianceCodeStackStarted == true) {
							break;
						} else {
							sb.append(str + " <-- ");
						}
					}
				}
				_log.debug(sb.toString());
			}
		
		}
		
		// check if its default, usually happens for OnPrem
		if(intCompanyID == IMCompany.DEFAULT_COMPANY_ID) {
			_updateCache(IMDatabaseMgr.COMMON_DATA_STORE_ID,IMCompany.DEFAULT_COMPANY_ID);
		}
		else {
			synchronized (_companyPropsMap) {
				companyProps = (CompanyProps) _companyPropsMap.get(intCompanyID);
			}
			if(companyProps == null) {
				// Try to load company. It will also update the cache.
				_updateCache(intCompanyID,intCompanyID);
			} else {
				// this company is already loaded, get the delta only
				_updateCache(intCompanyID,IMCompany.DEFAULT_COMPANY_ID);
			}
		}
		
		/*// Check if company was loaded.
		if (companyProps == null) {
			// Try to load company. It will also update the cache.
			_updateCache(intCompanyID);
		} else {	
			_updateCache(-1);
		}*/
		
		synchronized (_companyPropsMap) {
			companyProps = (CompanyProps) _companyPropsMap.get(intCompanyID);
		}
		
		return companyProps.getProps();
	}

	/**
	 * Get the server properties as a name/value map.
	 *
	 * @return the server properties.
	 */
	public FTProperties getProps() {
		
		CompanyProps companyProps = null;
		
		synchronized (_companyPropsMap) {
			// Global props are always loaded.
			companyProps = (CompanyProps) _companyPropsMap.get(IMCompany.DEFAULT_COMPANY_ID);
		}
		
		return companyProps.getProps();
	}

	/**
	 * Get the server properties as a name/value map.
	 *
	 * @param		intCompanyID	the internal company ID or -1 for global only.
	 * @return						the server properties.
	 */
	public FTProperties getProps(int intCompanyID) {
		
		CompanyProps companyProps = null;
		
		synchronized (_companyPropsMap) {
			companyProps = (CompanyProps) _companyPropsMap.get(intCompanyID);
		}
		
		// Check if company was loaded.
		if (companyProps == null) {
			try {
				// Try to load company.
				return getFreshProps(intCompanyID);
			} catch (IMDBException e) {
				// XXX - this is not ideal. We should throw an exception, but it will break too much code.
				_log.warning("Could not load company server properties. Reason: " + e.getMessage());
				return null;
			} catch (Exception e) {
				// XXX - this is not ideal. We should throw an exception, but it will break too much code.
				_log.warning("Could not load company server properties. Reason: " + e.getMessage(), e);
				return null;
			}
		} else {
			return companyProps.getProps();
		}
		
	}
	
	//
	// Alias API's.
	//
	
	/**
	 * Get the server properties as a name/value map.
	 *
	 * @return the server properties.
	 */
	/*public FTProperties getConfig() {
		return getProps();
	}*/

	/**
	 * Get the server properties as a name/value map.
	 *
	 * @param		intCompanyID	the internal company ID or -1 for global only.
	 * @return						the server properties.
	 */
	public FTProperties getConfig(int intCompanyID) {
		return getProps(intCompanyID);
	}

	/**
	 * Get a fresh copy of the server properties as a name/value map.
	 * NOTE: this method is almost never needed. Use getProps().
	 *
	 * @return						the server properties.
	 * @exception	IMDBException	Database error.
	 */
	/*public FTProperties getFreshConfig() {
		try {
			return getFreshProps(-1); //need to change to intCompanyID
		} catch (IMDBException e) {
			_log.warning(FTErrorConst.FT_WARNING_REFRESH_SERVER_PROPS_FAILED,e.getMessage());
			return null;
		}
	}*/

	/**
	 * Get the server properties as a name/value map.
	 * NOTE: this method is almost never needed. Use getProps().
	 *
	 * @param		intCompanyID	the internal company ID or -1 for global only.
	 * @return						the server properties.
	 */
	public FTProperties getFreshConfig(int intCompanyID) {
		try {
			return getFreshProps(intCompanyID);
		} catch (IMDBException e) {
			_log.warning(FTErrorConst.FT_WARNING_REFRESH_SERVER_PROPS_FAILED,e.getMessage());
			return null;
		}
	}
		
	//
	// DB API's
	//
	
	/**
	 * Get all of the server properties from the database.
	 *
	 * @return									the server properties.
	 * @exception	IADBSvcException			Database error.
	 */
    public IMServerProperty[] getServerPropsList(int intCompanyID)
        throws  IMDBException {

		boolean debug = _log.debug();
		boolean trace = _log.trace();
		
		if (debug) _log.debug("getServerPropsList: (noargs)");
				
		int retryCnt = 0;

		while (true) {

			FTSQLConnection dbConn 	= getDbConnection(intCompanyID);
			ResultSet 		rs 		= null;
			PreparedStatement stmt = null;
	        try {
				ArrayList<IMServerProperty> list = new ArrayList<IMServerProperty>();

				stmt = getStmt(dbConn, "selectAllPropsStmt", intCompanyID);

	    		rs = stmt.executeQuery();

	        	// Get all of the server properties.
	        	while (rs.next() == true) {

	        		String	name			= rs.getString("name");
	        		String	value			= rs.getString("value");
	        		String	defaultValue	= rs.getString("defaultValue");
	        		String	type			= rs.getString("type");
	        		String	description		= rs.getString("description");
	        		int		companyID	= rs.getInt("intCompanyID");
	        		if (rs.wasNull()) {
	        			companyID = -1;
	        		}
					int		serverID		= rs.getInt("serverID");
					long    modTime         = rs.getLong("modTime");

	        		list.add(new IMServerProperty(name, (value == null ? "" : value), (defaultValue == null ? "" : defaultValue), type, description, companyID, serverID, modTime));
				}

				rs.close();
				rs = null;

	        	// Check for server properties.
	        	if (list.isEmpty()) {
	        		if (debug) _log.debug("getServerPropsList: No server properties found.");

					return new IMServerProperty[0];
	        	}

	        	IMServerProperty[] props = new IMServerProperty[list.size()];

				props = (IMServerProperty[]) list.toArray(props);

				if (trace) {
					for (int i = 0; i < props.length; i++) {
						_log.trace("getServerPropsList: Server property found: " + props[i].toString());
					}
				}
				
				if (debug) _log.debug("getServerPropsList: found " + props.length + " props.");

				return props;

	    	} catch (Exception e) {
	    		if(e instanceof SQLException){
	    			_log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)stmt).getSQLString());
	    		}
				if (rs != null) {
	    			try {
	    				rs.close();
						rs = null;
	    			} catch (Exception e2) {
	    				// Ignore.
	    			}
	    		}

				retryCnt = handleException(e, "Get all server properties failed. Reason: " + e.getMessage(), retryCnt, dbConn, intCompanyID);
			} finally {
				releaseDbConnection(dbConn, intCompanyID);
			}
		}
    }

	/**
	 * Get all of the server properties from the database for a serverID.
	 *
	 * @param		serverID					the server ID.
	 * @return									the server properties.
	 * @exception	IMDBException				Database error.
	 */
	/*public IMServerProperty[] getServerPropsList(int serverID)
		throws  IMDBException {

		return getServerPropsList(null, -1, serverID);
	}*/

	/**
	 * Get all of the server properties from the database for an internal companyID and a serverID.
	 *
	 * @param		intCompanyID				the internal company ID or -1 for global only.
	 * @param		serverID					the server ID.
	 * @return									the server properties.
	 * @exception	IMDBException				Database error.
	 */
	public IMServerProperty[] getServerPropsList(int intCompanyID,
													int serverID)
		throws  IMDBException {

		return getServerPropsList(null, intCompanyID, serverID, -1L);
	}

	/**
	 * Get all of the server properties from the database for a serverID that
	 * were updated after the update time.
	 *
	 * @param		serverID					the server ID.
	 * @param		updateTime					Get properties updated after this time.
	 * @return									the server properties.
	 * @exception	IMDBException				Database error.
	 */
    public IMServerProperty[] getServerPropsList(	int 	serverID,
													long 	updateTime,
													int intCompanyID)
    	throws IMDBException {

    	return getServerPropsList(null, intCompanyID, serverID, -1L);
    }

	/**
	 * Get all of the server properties from the database for a serverID.
	 *
	 * @param		serverID					the server ID.
	 * @return									the server properties.
	 * @exception	IMDBException				Database error.
	 */
	/*public IMServerProperty[] getServerPropsList(	String 	startsWith,
													int 	serverID)
		throws  IMDBException {

		return getServerPropsList(startsWith, -1, serverID);
	}*/

	/**
	 * Get all of the server properties from the database for an internal companyID and a serverID.
	 *
	 * @param		intCompanyID				the internal company ID or -1 for global only.
	 * @param		serverID					the server ID.
	 * @return									the server properties.
	 * @exception	IMDBException				Database error.
	 */
	public IMServerProperty[] getServerPropsList(	String 	startsWith,
													int 	intCompanyID,
													int 	serverID)
		throws  IMDBException {

		return getServerPropsList(startsWith, intCompanyID, serverID, -1L);
	}
	
	
	 public synchronized IMServerProperty[] getServerPropsList(	String	startsWith,
				int 	intCompanyID,
				int 	serverID,
				long 	updateTime) throws IMDBException{
		 return getServerPropsList(startsWith, intCompanyID, serverID, updateTime, -1);
	 }

    /**
	 * Get all of the server properties from the database for an internal companyID and a serverID that
	 * were updated after the update time. This properly returns a company's property over a global and
	 * a serverID property over a global. The precedence is:
	 * 	1) serverID, companyID
	 * 	2) serverID, global companyID
	 * 	3) global serverID, companyID
	 * 	4) global serverID, global companyID
	 *
	 * @param		startsWith					return props that start with string or all if null.
	 * @param		intCompanyID				the internal company ID or -1 for global only.
	 * @param		serverID					the server ID.
	 * @param		updateTime					Get properties updated after this time.
	 * @param       replicaType                 policy/MT or empty 
	 * @return									the server properties.
	 * @exception	IMDBException				Database error.
	 */
    public IMServerProperty[] getServerPropsList(	String	startsWith,
													int 	intCompanyID,
													int 	serverID,
													long 	updateTime,
													int	replicaType)
        throws  IMDBException {

		boolean debug = _log.debug();
		boolean trace = _log.trace();
		
		if (debug) _log.debug("getServerPropsList: "
				+ "startsWith: " + startsWith
				+ ", intCompanyID: " + intCompanyID
				+ ", serverID: " + serverID
				+ ", updateTime: " + updateTime);
		
		int retryCnt = 0;

		while (true) {

			FTSQLConnection dbConn 	= getDbConnection(intCompanyID);
			ResultSet 		rs 		= null;
			PreparedStatement stmt = null;
	        try {
				ArrayList<IMServerProperty> list = new ArrayList<IMServerProperty>();

				stmt = getStmt(dbConn, "selectPropsStmt", intCompanyID);

				startsWith = startsWith == null ? null : startsWith + "%";

				stmt.setString(	1, startsWith);
				stmt.setString(	2, startsWith);
				stmt.setLong(	3, updateTime);
				stmt.setInt(	4, serverID);
				stmt.setInt(	5, intCompanyID);
				stmt.setInt(	6, serverID);
				stmt.setInt(	7, serverID);
				stmt.setInt(	8, intCompanyID);
				stmt.setInt(	9, intCompanyID);
				stmt.setInt(	10, serverID);
				stmt.setInt(	11, intCompanyID);
				stmt.setInt(	12, serverID);
				stmt.setInt(	13, serverID);
				stmt.setInt(	14, intCompanyID);
				stmt.setInt(	15, serverID);
				stmt.setInt(	16, intCompanyID);
				stmt.setInt(    17, replicaType);
				stmt.setInt(    18, replicaType);

	    		rs = stmt.executeQuery();

	        	// Get all of the server properties.
	        	while (rs.next() == true) {

	        		String	name			= rs.getString("name");
	        		String	value			= rs.getString("value");
	        		String	defaultValue	= rs.getString("defaultValue");
	        		String	type			= rs.getString("type");
	        		String	description		= rs.getString("description");
	        		int		tempTntCompanyID	= rs.getInt("intCompanyID");
	        		long    modTime         = rs.getLong("modTime");
	        		replicaType        = rs.getInt("replicaType");

	        		list.add(new IMServerProperty(	name,
	        										(value == null ? "" : value),
													(defaultValue == null ? "" : defaultValue),
													type,
													description,
													tempTntCompanyID,
													serverID,
													modTime,replicaType));
	        	}

				rs.close();
				rs = null;

	        	// Check for server properties.
	        	if (list.isEmpty()) {
	        		if (debug) _log.debug("getServerPropsList: No server properties found.");

					return new IMServerProperty[0];
	        	}

	        	IMServerProperty[] props = new IMServerProperty[list.size()];

				props = (IMServerProperty[]) list.toArray(props);

				if (trace) {
					for (int i = 0; i < props.length; i++) {
						_log.trace("getServerPropsList: Server property found: " + props[i].toString());
					}
				}

				if (debug) _log.debug("getServerPropsList: found " + props.length + " props.");
				
				return props;

			} catch (Exception e) {
				if(e instanceof SQLException){
					_log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)stmt).getSQLString()+
							" with parameters: "+startsWith+" is Null = "+", name like = "+startsWith+", modTime = "+updateTime+", serverID = "+serverID+", intCompanyID = "+intCompanyID
							+", serverID = "+serverID+", serverID = "+serverID+", intCompanyID = "+intCompanyID+", intCompanyID = "+intCompanyID+", serverID = "+serverID
							+", intCompanyID = "+intCompanyID+", serverID = "+serverID+", serverID = "+serverID+", intCompanyID = "+intCompanyID+", serverID = "+serverID
							+", intCompanyID = "+intCompanyID+", -1 = "+replicaType+", replicaType = "+replicaType );
				}
    				
				if (rs != null) {
	    			try {
	    				rs.close();
						rs = null;
	    			} catch (Exception e2) {
	    				// Ignore.
	    			}
	    		}

				retryCnt = handleException(e, "Get all server properties failed. Reason: " + e.getMessage(), retryCnt, dbConn, intCompanyID);
			} finally {
				releaseDbConnection(dbConn, intCompanyID);
			}
		}
    }
    
    /**
     * Retrieve the records(Map of name and values) from serverproperties table which has the name like given
     *  name and then return the same.
     */
    public Map<String, String> getServerProperties(String name, int intCompanyID) throws IMDBException, SQLException{
    	
    	FTSQLConnection dbConn = null;
    	Map<String, String> nameVal = new HashMap<String, String>();
    	int dbCompanyID = (intCompanyID == IMCompany.DEFAULT_COMPANY_ID ? IMDatabaseMgr.COMMON_DATA_STORE_ID : intCompanyID );
    	PreparedStatement stmt = null;
    	try{
    		dbConn = getDbConnection(dbCompanyID);
	    	List<String> values = new ArrayList<String>();
	    	stmt = getStmt(dbConn, "getServerPropValueByName", dbCompanyID);
	    	stmt.setString(1, name);
	    	ResultSet result = stmt.executeQuery();
	    	while (result.next()) {
	    		nameVal.put(result.getString("name"), result.getString("value"));
			}
    	}catch(SQLException e){
    		_log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)stmt).getSQLString()+
					" with parameters: "+" name = "+name );
    		throw e;
    	}finally{
    		releaseDbConnection(dbConn, dbCompanyID);
    	}
    	return nameVal;
    }
    
    
    
    // By Sujit to get Importer value .
    
    public List getImporterNameList(int intCompanyID)
			throws IMDBException {

		boolean debug = _log.debug();
		boolean trace = _log.trace();

		int retryCnt = 0;

		while (true) {

			FTSQLConnection dbConn = getDbConnection(intCompanyID);
			ResultSet rs = null;
			PreparedStatement stmt = null;
			try {
				List list = new ArrayList();

				stmt = getStmt(dbConn, "getImporterNames", intCompanyID);
				rs = stmt.executeQuery();

				// Get all of the Importer Value
				while (rs.next() == true) {

					String value = rs.getString("name");
					list.add(value);
				}

				rs.close();
				rs = null;

				// Check for Importer value.
				if (list.isEmpty()) {
					if (debug)
						_log
								.debug("getServerPropsList: No Importer value is found.");

					return list;
				}

				return list;

			} catch (Exception e) {
				if(e instanceof SQLException){
					_log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)stmt).getSQLString());
				}
				if (rs != null) {
					try {
						rs.close();
						rs = null;
					} catch (Exception e2) {
						// Ignore.
					}
				}

				retryCnt = handleException(e,
						"Get all Importer value failed. Reason: "
								+ e.getMessage(), retryCnt, dbConn, intCompanyID);
			} finally {
				releaseDbConnection(dbConn, intCompanyID);
			}
		}
}
    
    public List getNetworkImporterNameList(int intCompanyID)
			throws IMDBException {

		boolean debug = _log.debug();
		boolean trace = _log.trace();

		int retryCnt = 0;

		while (true) {

			FTSQLConnection dbConn = getDbConnection(intCompanyID);
			ResultSet rs = null;

			try {
				List list = new ArrayList();

				PreparedStatement stmt = getStmt(dbConn, "getNetworkImporterNames", intCompanyID);
				rs = stmt.executeQuery();

				// Get all of the Importer Value
				while (rs.next() == true) {

					String value = rs.getString("name");
					list.add(value);
				}

				rs.close();
				rs = null;

				// Check for Importer value.
				if (list.isEmpty()) {
					if (debug)
						_log
								.debug("getServerPropsList: No Importer value is found.");

					return list;
				}

				return list;

			} catch (Exception e) {
				if (rs != null) {
					try {
						rs.close();
						rs = null;
					} catch (Exception e2) {
						// Ignore.
					}
				}

				retryCnt = handleException(e,
						"Get all Network Importer value failed. Reason: "
								+ e.getMessage(), retryCnt, dbConn, intCompanyID);
			} finally {
				releaseDbConnection(dbConn, intCompanyID);
			}
		}
}
   
    /**
	 * Get all of the server properties from the database for all companies for a serverID that
	 * were updated after the update time. This properly returns a serverID property over a global.
	 *  The precedence is:
	 * 	1) serverID
	 * 	2) global serverID
	 *
	 * @param		startsWith					return props that start with string or all if null.
	 * @param		serverID					the server ID.
	 * @param		updateTime					Get properties updated after this time.
	 * @return									the server properties.
	 * @exception	IMDBException				Database error.
	 */
    public IMServerProperty[] getServerPropsList(	String	startsWith,
													int 	serverID,
													long 	updateTime,
													int intCompanyID)
        throws  IMDBException {

		boolean debug = _log.debug();
		boolean trace = _log.trace();
		
		if (debug) _log.debug("getServerPropsList: "
				+ "startsWith: " + startsWith
				+ ", serverID: " + serverID
				+ ", updateTime: " + updateTime);

		int retryCnt = 0;

		while (true) {

			FTSQLConnection dbConn 	= getDbConnection(intCompanyID);
			ResultSet 		rs 		= null;
			PreparedStatement stmt = null;
	        try {
				ArrayList<IMServerProperty> list = new ArrayList<IMServerProperty>();
				
				stmt = getStmt(dbConn, "selectAllCompaniesPropsStmt", intCompanyID);

				startsWith = startsWith == null ? null : startsWith + "%";

				stmt.setString(	1, startsWith);
				stmt.setString(	2, startsWith);
				stmt.setLong(	3, updateTime);
				stmt.setInt(	4, serverID);
				stmt.setInt(	5, serverID);

	    		rs = stmt.executeQuery();

	        	// Get all of the server properties.
	        	while (rs.next() == true) {

	        		String	name			= rs.getString("name");
	        		String	value			= rs.getString("value");
	        		String	defaultValue	= rs.getString("defaultValue");
	        		String	type			= rs.getString("type");
	        		String	description		= rs.getString("description");
	        		int		companyID		= rs.getInt("intCompanyID");
	        		long    modTime         = rs.getLong("modTime");

	        		list.add(new IMServerProperty(	name,
	        										(value == null ? "" : value),
													(defaultValue == null ? "" : defaultValue),
													type,
													description,
													companyID,
													serverID,
													modTime));
	        	}

				rs.close();
				rs = null;
	    		
	        	// Check for server properties.
	        	if (list.isEmpty()) {
	        		if (debug) _log.debug("getServerPropsList: No server properties found.");

					return new IMServerProperty[0];
	        	}

	        	IMServerProperty[] props = new IMServerProperty[list.size()];

				props = (IMServerProperty[]) list.toArray(props);

				if (trace) {
					for (int i = 0; i < props.length; i++) {
						_log.trace("getServerPropsList: Server property found: " + props[i].toString());
					}
				}

				if (debug) _log.debug("getServerPropsList: found " + props.length + " props.");
				
				return props;

			} catch (Exception e) {
				if(e instanceof SQLException){
					_log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)stmt).getSQLString()+
							" with parameters: startsWith  Null = "+", like  "+startsWith+", modTime >  "+updateTime+", serverID = "+serverID+", serverID = "+serverID );
				}
				if (rs != null) {
	    			try {
	    				rs.close();
						rs = null;
	    			} catch (Exception e2) {
	    				// Ignore.
	    			}
	    		}

				retryCnt = handleException(e, "Get all server properties failed. Reason: " + e.getMessage(), retryCnt, dbConn, intCompanyID);
			} finally {
				releaseDbConnection(dbConn, intCompanyID);
			}
		}
    }

	/**
     * Retrieve a server property value from the database.
     * NOTE: this method is almost never needed. Use getProps().
     *
     * @param		name						property name.
     * @exception	IMDBRecordNotFoundException	property not in database.
     * @exception	IADBSvcException			Database error.
     */
    /*public IMServerProperty getServerProp(String name) throws
        IMDBRecordNotFoundException,
        IMDBException {

    	return getServerProp(name, -1);
    }*/

    /**
     * Retrieve a server property value from the database.
     * NOTE: this method is almost never needed. Use getProps().
     *
     * @param		name						property name.
     * @param		intCompanyID				the internal company ID or -1 for global only.
	 * @exception	IMDBRecordNotFoundException	property not in database.
     * @exception	IADBSvcException			Database error.
     */
    public IMServerProperty getServerProp(String name, int intCompanyID) throws
        IMDBRecordNotFoundException,
        IMDBException {

    	boolean debug = _log.debug();
		
		if (debug) _log.debug("getServerProp: "
				+ "name: " + name
				+ ", intCompanyID: " + intCompanyID);
		
        int retryCnt = 0;
        int dbComapnyID = (intCompanyID == IMCompany.DEFAULT_COMPANY_ID ? IMDatabaseMgr.COMMON_DATA_STORE_ID : intCompanyID );
        while (true) {

        	FTSQLConnection dbConn 	= getDbConnection(dbComapnyID);
			ResultSet 		rs 		= null;
            PreparedStatement stmt = null;
            try {
                // Check for valid name.
                if (name == null || name.length() == 0) {
                    throw new IMDBException(
                        "Server property not retrieved. Reason: Property name can not be null.");
                }
                
                stmt = getStmt(dbConn, "selectPropStmt", dbComapnyID);

                stmt.setString(	1, name);
                stmt.setInt(	2, intCompanyID);
				stmt.setInt(	3, _serverID);
				stmt.setString(	4, name);
				stmt.setInt(	5, _serverID);

                rs = stmt.executeQuery();

                if (rs.next() == false) {
                	rs.close();
                	rs = null;
                    throw new IMDBRecordNotFoundException("Retrieve property " +
                        name +
                        " failed. Reason: Property not found in database.");
                }

                String 	value 			= rs.getString("value");
                String 	defaultValue 	= rs.getString("defaultValue");
                String 	type 			= rs.getString("type");
                String 	description 	= rs.getString("description");
                int 	icID 			= rs.getInt("intCompanyID");
                long 	modTime 		= rs.getLong("modTime");
                int 	serverID 		= rs.getInt("serverID");

                rs.close();
                rs = null;
                
                return new IMServerProperty(name,
                                            (value == null ? "" : value),
                                            (defaultValue == null ? "" :
                                             defaultValue),
                                            type,
                                            description,
                                            icID,
                                            serverID,
                                            modTime);
            } catch (Exception e) {
            	if(e instanceof SQLException){
            		_log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)stmt).getSQLString()+
            				" with parameters: "+" name = "+name+", intCompanyID = "+intCompanyID+", serverID = "+_serverID+", name = "+name+", serverID = "+_serverID);
            	}
            	if (rs != null) {
                    try {
                        rs.close();
                        rs = null;
                    }
                    catch (Exception e2) {
                        // Ignore.
                    }
                }

                retryCnt = handleException(e,
                                           "Retrieve property " + name + " failed. Reason: " +
                                           e.getMessage(), retryCnt, dbConn, intCompanyID);
            } finally {
				releaseDbConnection(dbConn, dbComapnyID);
			}
        }
    }
    
	/**
	 * Update a server properties in the database. Note, this does
	 * not update the cache until the next cache refresh.
	 *
	 * @param		props						properties to update.
	 * @param		intCompanyID				the internal company ID or -1 for global only.
	 * @exception	IMDBRecordNotFoundException	property not in database.
	 * @exception	IADBSvcException			Database error.
	 */
    public void updateServerProps(FTProperties props, int intCompanyID)
        throws  IMDBRecordNotFoundException,
				IMDBException {
		for(Object key : props.keySet()) {
			String name = (String) key;
			String value = props.getStringValue(name, null);
			_updateServerProp(name, value, intCompanyID, _serverID, false);
		}
	}

	/**
	 * Update a server property value in the database. Note, this does
	 * not update the cache until the next cache refresh.
	 *
	 * @param		name						property name.
	 * @param		value						property value.
	 * @exception	IMDBRecordNotFoundException	property not in database.
	 * @exception	IADBSvcException			Database error.
	 */
    /*public void updateServerProp(String name, String value)
        throws  IMDBRecordNotFoundException,
				IMDBException {
		_updateServerProp(name, value, -1, _serverID, false);
	}
*/
	/**
	 * Update a server property value in the database. Note, this does
	 * not update the cache until the next cache refresh.
	 *
	 * @param		name						property name.
	 * @param		value						property value.
	 * @param		intCompanyID				the internal company ID or -1 for global only.
	 * @exception	IMDBRecordNotFoundException	property not in database.
	 * @exception	IADBSvcException			Database error.
	 */
    public void updateServerProp(String name, String value, int intCompanyID)
        throws  IMDBRecordNotFoundException,
				IMDBException {
		_updateServerProp(name, value, intCompanyID, _serverID, false);
	}
    
    /**
	 * Update a server property value in the database. Note, this does
	 * not update the cache until the next cache refresh.
	 *
	 * @param		name						property name.
	 * @param		value						property value.
	 * @param		intCompanyID				the internal company ID or -1 for global only.
	 * @param		serverID					the server ID or -1 for global only.
	 * @exception	IMDBRecordNotFoundException	property not in database.
	 * @exception	IADBSvcException			Database error.
	 */
    public void updateServerProp(String name, String value, int intCompanyID, int serverID)
    	throws  IMDBRecordNotFoundException,
    			IMDBException {
    	_updateServerProp(name, value, intCompanyID, serverID, false);
    }

    /**
     * Update a server property value in the database. Note, this does
	 * not update the cache until the next cache refresh.
     *
     * @param       name                        property name.
     * @param       value                       property value.
     * @param       intCompanyID                the internal company ID or -1 for global only.
     * @exception   IMDBRecordNotFoundException property not in database.
     * @exception   IADBSvcException            Database error.
     */
    public void updateServerProp(Map<String,String> serverProps, int intCompanyID)
        throws  IMDBRecordNotFoundException,
                IMDBException 
    {
        for (Iterator<String> iter = serverProps.keySet().iterator(); iter.hasNext();) {
            String name =  iter.next();
            String value = serverProps.get(name);
            _updateServerProp(name, value, intCompanyID, _serverID, false);
        }
    }
    
	/**
	 * Update a server property value in the database. Note, this does
	 * not update the cache until the next cache refresh.
	 *
	 * @param		name						property name.
	 * @param		value						property value.
	 * @param		intCompanyID				the internal company ID or -1 for global only.
	 * @param		serverID					server ID or serverID of the property.
	 * @param		serverIDFlag				If true serverID is the serverID of the property, else it is the server's serverID.
	 * @exception	IMDBRecordNotFoundException	property not in database.
	 * @exception	IADBSvcException			Database error.
	 */
    private void _updateServerProp(String name, String value, int intCompanyID, int serverID, boolean serverIDFlag)
        throws  IMDBRecordNotFoundException,
				IMDBException {

		boolean debug = _log.debug();
		
		if (debug) {
			if (name.toLowerCase().contains("password")) {
				_log.debug("updateServerProp: "
						+ "name: " + name
						+ ", value: ********, intCompanyID: " + intCompanyID
						+ ", serverID: " + serverID
						+ ", serverIDFlag: " + serverIDFlag);
			} else if (name.toLowerCase().contains("client.secret")) {
				_log.debug("updateServerProp: "
						+ "name: " + name
						+ ", value: ************************************, intCompanyID: " + intCompanyID
						+ ", serverID: " + serverID
						+ ", serverIDFlag: " + serverIDFlag);
			} else {
				_log.debug("updateServerProp: "
						+ "name: " + name
						+ ", value: " + value
						+ ", intCompanyID: " + intCompanyID
						+ ", serverID: " + serverID
						+ ", serverIDFlag: " + serverIDFlag);
			}
		}
		
		int retryCnt = 0;

		int dbCompanyID = (intCompanyID == IMCompany.DEFAULT_COMPANY_ID ? IMDatabaseMgr.COMMON_DATA_STORE_ID : intCompanyID);
		if(intCompanyID == IMCompany.DEFAULT_COMPANY_ID) {
			dbCompanyID = IMDatabaseMgr.COMMON_DATA_STORE_ID;
		}
		
		while (true) {

			FTSQLConnection dbConn = getDbConnection(dbCompanyID);
			
			long modTime =  0;
			try {
				// Check for valid name.
				if (name == null || name.length() == 0) {
					throw new IMDBException("Server property not updated. Reason: Property name can not be null.");
				}

				modTime = System.currentTimeMillis();
				
				CallableStatement cstmt = (CallableStatement) getStmt(dbConn, "updateServerProp", dbCompanyID);
                   
                cstmt.setString(1, name);
                cstmt.setString(2, value);
                cstmt.setLong(3, modTime);
                cstmt.setInt(4, serverID);
                cstmt.setBoolean(5, serverIDFlag);
                cstmt.setInt(6, intCompanyID);
                
                cstmt.executeUpdate();	
                cstmt.clearParameters();       
                
                if (debug)  {
                	if (name.toLowerCase().contains("password")) {
                		_log.debug("Updated DB - name: " + name + ", value: ********, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime));
                	} else if (name.toLowerCase().contains("client.secret")) {
                		_log.debug("Updated DB - name: " + name + ", value: ************************, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime));
                	} else{
                		_log.debug("Updated DB - name: " + name + ", value: " + value + ", serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime));
                	}
                }
                
				return;

			} catch (Exception e) {
				if(e instanceof SQLException){
				_log.info("DB Exception occured while running CallableStatement: updateServerProp "+ 
    					" with parameters: "+" name = "+name+", value = "+value+", modTime = "+modTime+", serverID = "+serverID+", serverIDFlag = "+serverIDFlag+", intCompanyID = "+intCompanyID );
				}
				//Throwing exception in case the property not found in the database. This is handled outside by adding it.
				if(e.getMessage().contains("Property not found in database")){
					throw new IMDBRecordNotFoundException();
				}
				retryCnt = handleException(e, "Update property " + name + " failed. Reason: " + e.getMessage(), retryCnt, dbConn, dbCompanyID);
			} finally {
				releaseDbConnection(dbConn, dbCompanyID);
			}
		}
    }

	/**
	 * Update a server property value in the database. Note, this does
	 * not update the cache until the next cache refresh.
	 *
	 * @param		name						property name.
	 * @param		value						property value.
	 * @param		intCompanyID				the internal company ID or -1 for global only.
	 * @exception	IMDBRecordNotFoundException	property not in database.
	 * @exception	IADBSvcException			Database error.
	 */
    public void updateServerPropsByServerId(String name, String value, int intCompanyID, int serverID)
        throws  IMDBRecordNotFoundException,
        IMDBException {
    	
    	_updateServerProp(name, value, intCompanyID, serverID, true);
    }


    /**
     * Insert a server property into the database. Skip the prop if it already exists.
     * Note, this does not update the cache until the next cache refresh.
     * 
     * @param       prop			The server property to add.
     * @exception   IMDBException	Database error.
     */
    public void addServerProp(IMServerProperty prop, int intCompanyID) throws IMDBException {
    	IMServerProperty[] props = { prop };
    	addServerProp(props, intCompanyID);
    }

	/**
     * Insert server properties into the database. Skip a prop if it already exists.
     * Note, this does not update the cache until the next cache refresh.
     *
     * @param       props			  The server properties to add.
     * @return                        The number of props added.
     * @exception	IMDBException	Database error.
     */
    public int addServerProp(IMServerProperty[] props, int intCompanyID) throws IMDBException {
    	
        // Check for at least one prop.
        if (props == null || props.length == 0 ) {
            return 0;
        }

        boolean debug = _log.debug();
        
        if (debug) _log.debug("addServerProp: "
				+ "props: " + props.length);
        
        int retryCnt = 0;
        int dbCompanyID = (intCompanyID == IMCompany.DEFAULT_COMPANY_ID ? IMDatabaseMgr.COMMON_DATA_STORE_ID : intCompanyID);
        while (true) {

        	FTSQLConnection 	dbConn 	= getDbConnection(dbCompanyID);
			FTSQLTransaction 	trans 	= props.length > 1 ? new FTSQLTransaction(dbConn, _log) : null;
			int                 added   = 0;
			PreparedStatement stmt      = null;
			IMServerProperty prop1 = null;
			long modTime = 0;
			 int     replicaType     = 0;
            try {
            	// Only start a trans if more than one prop.
                if (trans != null) trans.startTransaction(Connection.TRANSACTION_READ_COMMITTED);
                stmt = getStmt(dbConn, "insertPropStmt", dbCompanyID);
                 modTime = System.currentTimeMillis();
                               
                for (IMServerProperty prop : props) {
                	prop1 =prop;
					String 	name 			= prop.getName();
	                String 	value 			= prop.getValue();
	                String 	defaultValue 	= prop.getDefaultValue();
	                String 	type 			= prop.getType();
	                String 	description 	= prop.getDescription();
	                int 	serverID		= prop.getServerID();
	                int 	companyID	= prop.getIntCompanyID()==-300?-1:prop.getIntCompanyID();
	                replicaType     = prop.getReplicaType();
	                
	                int arg = 1;
	                stmt.setString(arg++, name);
	                stmt.setString(arg++, value);
	                stmt.setString(arg++, defaultValue);
	                stmt.setString(arg++, type);
	                stmt.setString(arg++, description);
					stmt.setInt(arg++, companyID);
	                stmt.setInt(arg++, serverID);
	                stmt.setLong(arg++, modTime);
	                if(replicaType == -1) {
	                	stmt.setNull(arg++, Types.INTEGER);
	                } else {
	                	stmt.setInt(arg++,replicaType);
	                }
	                
	                stmt.setString(arg++, name);
	                stmt.setInt(arg++, companyID);
	                stmt.setInt(arg++, serverID);	                 
                    
                    added += stmt.executeUpdate();
	        	}
                          
                if (trans != null) trans.endTransaction(true);

                if (debug) {
	                for (IMServerProperty prop : props) {
	                	if (prop.getName().toLowerCase().contains("password")) {
	                		_log.debug("Added DB - name: " + prop.getName() + ", value: ********, serverID: " + prop.getServerID() + ", intCompanyID: " + prop.getIntCompanyID() + ", modTime: " + new Timestamp(prop.getModTime()));
	                	} else if (prop.getName().toLowerCase().contains("client.secret")) {
	                		_log.debug("Added DB - name: " + prop.getName() + ", value: **********************, serverID: " + prop.getServerID() + ", intCompanyID: " + prop.getIntCompanyID() + ", modTime: " + new Timestamp(prop.getModTime()));
	                	} else {
	                		_log.debug("Added DB - name: " + prop.getName() + ", value: " + prop.getValue() + ", serverID: " + prop.getServerID() + ", intCompanyID: " + prop.getIntCompanyID() + ", modTime: " + new Timestamp(prop.getModTime()));
	                	}
	                }
                }
                
                return added;

            }catch(SQLException e){
            	 if(prop1 != null){
            		 _log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)stmt).getSQLString()+
         					" with parameters: "+" name = "+prop1.getName()+", value = "+prop1.getValue()+", defaultValue = "+prop1.getDefaultValue()
                     		+", type = "+prop1.getType()+", description = "+prop1.getDescription()+", companyID = "+prop1.getIntCompanyID()
                     		+", serverID = "+prop1.getServerID()+", maxTime = "+modTime+", replicaType = "+replicaType+", name = "+prop1.getName()
                     		+", companyID = "+prop1.getIntCompanyID()+", serverID = "+prop1.getServerID());
            	 }
            	 else{
            		 _log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)stmt).getSQLString(), e);
            	 }
            	 
				  retryCnt = handleException(e, "Insert server property failed. Reason: " + e.getMessage(),
                                                 retryCnt, trans, dbConn,intCompanyID);
            }
            catch (Exception e) {      
                retryCnt = handleException(e,
                    "Insert server property failed. Reason: " + e.getMessage(),
                                           retryCnt, trans, dbConn,intCompanyID);
            } finally {
            	if (dbConn != null) {
            		releaseDbConnection(dbConn, dbCompanyID);
            	}
			}
        }
    }
     
    /**
     * Delete a server property from the database. 
     * NOTE: This method should not be used because there is no way to signal other servers that the property was removed.
     *
     * @param       name			Property name.
     * @param       serverID		Server ID.
     * @param       intCompanyID	Internal company ID.
     * @exception   IMDBException	Database error.
     */
    public void deleteServerProp(String name, int serverID, int intCompanyID) throws IMDBException {

        boolean debug = _log.debug();
        
        if (debug) _log.debug("deleteServerProp: "
				+ "name: " + name
				+ ", serverID: " + serverID
				+ ", intCompanyID: " + intCompanyID);
        
    	int retryCnt = 0;
    	int dbCompanyID = intCompanyID;
    	if(intCompanyID == IMCompany.DEFAULT_COMPANY_ID) {
    		dbCompanyID = IMDatabaseMgr.COMMON_DATA_STORE_ID;
    	}
    	while (true) {
    		FTSQLConnection dbConn 	= getDbConnection(dbCompanyID);
    		
    		PreparedStatement selectStmt = null;
    		try {
    			 selectStmt = getStmt(dbConn, "deletePropStmt", dbCompanyID);

    			// Try to get the prop.
    			selectStmt.setString( 1, name);
    			selectStmt.setInt(    2, serverID);
    			selectStmt.setInt(    3, intCompanyID);
    			
    			int updateCount = selectStmt.executeUpdate();

    			if (debug) {
    				if (updateCount > 0) {
    					_log.debug("Property " + name + " deleted.");             
    				} else {
    					_log.debug("Property " + name + " did not exist.");          
    				}
    			}
    			
    			return;

    		}catch(SQLException e){
    			_log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)selectStmt).getSQLString()+
    					" with parameters: "+" name = "+name+", serverID = "+serverID+", intCompanyID = "+intCompanyID );
    			retryCnt = handleException(e, "Delete server property failed. Reason: " + e.getMessage(),
    					retryCnt, dbConn, dbCompanyID);
    		} catch (Exception e) {
    			retryCnt = handleException(e,
    					"Delete server property failed. Reason: " + e.getMessage(),
    					retryCnt, dbConn, dbCompanyID);
    		} finally {
    			if (dbConn != null) {
    				releaseDbConnection(dbConn, dbCompanyID);
    			}
    		}
    	}
    }   

    //
    // Listener methods.
    //
    
    protected List<FTConfigUpdateListener> _listeners;
    protected List<ArrayList<String>> _prefixes;
        
    /** Add a conversation listener to be notified of conversation creation events */
    public void addConfigUpdateListener(FTConfigUpdateListener listener) {    	
    	_listeners.add(listener);
    	_prefixes.add(null);
	}

    /** Remove a conversation listener */
    public void removeConfigUpdateListener(FTConfigUpdateListener listener) {    	
    	int idx = -1;
    	for (FTConfigUpdateListener l : _listeners) {
    		if (l == listener)
    			break;
    		if (idx == -1) idx = 0; 
    		else ++idx;
    	}
    	if (idx != -1) {
    		_listeners.remove(idx);
    		_prefixes.remove(idx);
    	}
	}

	public void addConfigUpdateListener(FTConfigUpdateListener listener, String[] keyPrefix) {	
		_listeners.add(listener);
		ArrayList<String> prefixes = null;
		if (keyPrefix != null && keyPrefix.length > 0) {
			prefixes = new ArrayList<String>();
			for (String s : keyPrefix)
				prefixes.add(s);
		}			
    	_prefixes.add(prefixes);
	}

	private void _notifyListeners(Set<String> changedNameSet, FTProperties props, int intCompanyID) {
		// Check for updated props.
		if (changedNameSet == null || changedNameSet.isEmpty()) {
			return;		
		}
		
		boolean debug = _log.debug();
				
		String[] changed = (String[]) changedNameSet.toArray(new String[changedNameSet.size()]);
		int i = 0;		
		/* Copying _listeners to listeners temporary list, because in the for loop 
		 * adding some listeners to _listeners list, 
		 * hence for loop is throwing ConcurrentModificationException
		 * This is fix for bug #13702
		 */
		List<FTConfigUpdateListener> listeners = 
			         Collections.synchronizedList(new ArrayList<FTConfigUpdateListener>(_listeners));
		
		if (debug) {
			StringBuilder sb = new StringBuilder();
			for (String name : changedNameSet) {
				if (sb.length() != 0) {
					sb.append(",");
				}
				sb.append(name);
			}
			_log.debug("Updating " + listeners.size() + " listeners with props: " + sb.toString());
		}

		for (FTConfigUpdateListener listener : listeners) {
			try {
				ArrayList<String> prefixes = _prefixes.get(i++); 
				if (prefixes == null) 
					listener.configUpdated(intCompanyID, props, changedNameSet);
				else {
					Collection<String> set = null;
					for (String prefix : prefixes) {
						for (String c : changed) {
							if (c.startsWith(prefix)) {
								if (set == null)
									set = new HashSet<String>();
								set.add(c);							
							}
						}
					}
					if (set != null) {
						if (debug) _log.debug("Updating " + listener.toString() + " with " + set.size() + " props.");
						listener.configUpdated(intCompanyID, props, set);
					}
					set = null;
				}
			} catch (Throwable t) {
				_log.warning("Could not update listener. Reason: " + t.getMessage(), t);
			}
		}
	}
	
	//
	// Cache update methods.
	//

	// Alarm to handle cache updates.
    public long alarm(Alarm alarm, long msec, Object o) {
		
		// Check if we are cleared.
		if (_updateAlarm == null) {
			return 0;
		}

		// update for companies
		// Get the list of DatabaseID
		Set<Integer> dbIDs = new HashSet<Integer>();
		if(_companyManager!=null){
			Map<Integer, Set<Integer>> dbIDMap = _companyManager.getDBIDToCompanyMap();
			if(dbIDMap == null) {
				// this shouldn't happen
				if(_log.debug()) _log.debug("Couldn't find any companies");
			} 
			
			dbIDs.add(IMDatabaseMgr.instance().getDBId(IMDatabaseMgr.COMMON_DATA_STORE_ID));
			dbIDs.addAll(dbIDMap.keySet());
			long start = 0;
			Iterator<Integer> dbIDIter = dbIDs.iterator();
			while(dbIDIter.hasNext()) {
				int dbID = dbIDIter.next();
				try { 
					if(_log.debug()) {
						start = System.currentTimeMillis();
					}
					int dbCmpID = IMDatabaseMgr.COMMON_DATA_STORE_ID;
					Set<Integer> dbCmps = dbIDMap.get(dbID);
					if(dbCmps != null) {
						// get any cmpID
						dbCmpID = dbCmps.iterator().next();
					}
					// pass -1 to always get delta.
					_updateCache(dbCmpID,-1);
					if(_log.debug()) {
						_log.debug("Time took for updating companyID:"+dbCmpID+" is "+(System.currentTimeMillis()-start)+" (msec)");
					}
				} catch (IMDBException e) {
					_log.warning("Could not update server props for DB ID '"+dbID+"' : " + e.getMessage());
				} catch (Exception e) {
					_log.warning("Could not update server props for DB ID '"+dbID+"' : " + e.getMessage(), e);
				}
			}
		}else{
			try { 
				// get any cmpID
				_updateCache(IMDatabaseMgr.COMMON_DATA_STORE_ID,-1);
			} catch (IMDBException e) {
				_log.warning("Could not update server props in CDS: " + e.getMessage());
			} catch (Exception e) {
				_log.warning("Could not update server props in CDS: " + e.getMessage(), e);
			}	
		}
		return _cacheRefreshInterval;
	}
    
	// Update the cache and optionally load a company. Keep this synchronized.
    // dbCompanyID is used to get database ID, database connection
    // if loadIntCompanyID is not -1, then all properties for that specific company will be loaded.
	private synchronized void _updateCache(int dbCompanyID, int loadIntCompanyID)
		throws IMDBException {

		boolean debug = _log.debug();
		int dbID = IMDatabaseMgr.instance().getDBId(dbCompanyID);
		
		// check if cacheUpdateTime per database is available
		if(_cacheUpdateTimeMap.get(dbID) == null) {
			_cacheUpdateTimeMap.put(dbID, -1L);
		}
		
		if (debug) {
			String loadMsg = "";
			if (loadIntCompanyID != IMCompany.DEFAULT_COMPANY_ID) {
				loadMsg = ". Loading intCompanyID: " + loadIntCompanyID;
			}
			_log.debug("Refreshing server props modified since: " + new Timestamp(_cacheUpdateTimeMap.get(dbID)) + loadMsg);
		}

		// Get the list of intCompanyIDs loaded.
		int[] loadedIntCompanyIDs = null;
		/*synchronized (_companyPropsMap) {
			Set<Integer> set = _companyPropsMap.keySet();
			if (set == null || set.isEmpty()) {
				if (debug) _log.debug("No companies to update.");
				return;
			}
			loadedIntCompanyIDs = new int[set.size()];
			int i = 0;
			for (Integer id : set) {
				loadedIntCompanyIDs[i++] = id.intValue();
			}
		}*/
		
		Set<Integer> intCompanies = new HashSet<Integer>();
		intCompanies.add(IMCompany.DEFAULT_COMPANY_ID);
		Set<Integer> cmpList = _companyManager.getDBIDToCompanyMap().get(dbID);
		if(cmpList != null) {
			intCompanies.addAll(cmpList);
		}
		int i = 0;
		loadedIntCompanyIDs = new int[intCompanies.size()];
		for(int id : intCompanies) {
			loadedIntCompanyIDs[i++] = id;
		}
		
		int count = 0;
		
		// Get all the updated props.
		HashMap<Integer,ArrayList<IMServerProperty>> updatedPropsMap = _getUpdatedServerProps(_cacheUpdateTimeMap.get(dbID), loadedIntCompanyIDs, loadIntCompanyID,dbCompanyID);
		
		// Check for updated props or a company to load.
		if (updatedPropsMap.isEmpty() && loadIntCompanyID == -1) {
			if (debug) _log.debug("No server props to update.");
			return;
		}
		
		// Pull out the global props.
		ArrayList<IMServerProperty> globalUpdatedPropsList = updatedPropsMap.remove(-1); 
		
		CompanyProps globalProps = null;
		
		// Get the global props now, we might need them below.
		synchronized (_companyPropsMap) {
			globalProps = _companyPropsMap.get(IMCompany.DEFAULT_COMPANY_ID);
		}
		
		// Check if they exist.
		if (globalProps == null) {
			// This should never happen. We load them during the init.
			// throw new IMDBException("Global props were not loaded.");
		    globalProps = new CompanyProps(IMCompany.DEFAULT_COMPANY_ID);
		}
		
		// Update the global props.
		if (globalUpdatedPropsList != null && !globalUpdatedPropsList.isEmpty()) {
			Set<String> changedNameSet = new HashSet<String>();
			
			if (debug) _log.debug("Refreshing " + globalUpdatedPropsList.size() + " global props.");
			
			// Update the global props.
			count += _updateProps(globalProps, globalUpdatedPropsList, changedNameSet, dbID);
			
			_notifyListeners(changedNameSet, globalProps.getProps(), loadIntCompanyID);
		} else {
			if (debug) _log.debug("No global props to update.");
		}
		
		// Get the set of companies with company specific props updated. One of them may be a load.
		Set<Integer> updatedIntCompanyIDs = updatedPropsMap.keySet();
		
		if (updatedIntCompanyIDs != null && !updatedIntCompanyIDs.isEmpty()) {
			// Update the company specific props.
			for (Integer iCID : updatedIntCompanyIDs) {
				int intCompanyID = iCID.intValue();
			
				Set<String> changedNameSet = null;
				
				// Check if this was the loaded company.
				if (intCompanyID != loadIntCompanyID) {
					changedNameSet = new HashSet<String>();
				} else {
					if (debug) _log.debug("Loading company props for intCompanyID: " + intCompanyID + "...");
				}
	
				// Get the company's props.
				CompanyProps companyProps = null;
				synchronized (_companyPropsMap) {
					companyProps = _companyPropsMap.get(intCompanyID);
				}
				
				boolean createProps = false;
				
				// Check if they exist.
				if (companyProps == null) {
					// Make sure it was our loaded company.
					// Its possible that its a new company added as loading of props for default company in alarm
					/*if (intCompanyID != loadIntCompanyID) {
						// This can never happen.
						_log.warning("Company props were missing for intCompanyID: " + intCompanyID);
						throw new IMDBException("Company props were missing for intCompanyID: " + intCompanyID);
					}*/
					// It is new, create it and init the props using the globals.
					if (debug) _log.debug("Initing with " + globalProps.getProps().size() + " global props.");
					companyProps = new CompanyProps(intCompanyID, globalProps.getProps());
					createProps = true;
				} else {
					changedNameSet = new HashSet<String>();
				}
				
				// Get the properties.
				ArrayList<IMServerProperty> updatedPropsList = updatedPropsMap.get(intCompanyID); 
				
				if (debug) {
					String updateType = "Refreshing ";
					if (createProps) {
						updateType = "Initing ";
					} 
					_log.debug(updateType + updatedPropsList.size() + " company specific props for intCompanyID: " + intCompanyID);
				}
				
				// Update company specific props.
				count += _updateProps(companyProps, updatedPropsList, changedNameSet, dbID);
				
				if (debug) _log.debug("Refreshing " + (globalUpdatedPropsList != null ? globalUpdatedPropsList.size() : 0) + " global props for intCompanyID: " + intCompanyID);
				
				// Update the company globals. Even do this on a load, just to get the perms modTime set correctly.
				count += _updateProps(companyProps, globalUpdatedPropsList, changedNameSet, dbID);
				
				// Check if we created new props.
				if (createProps) {
					synchronized (_companyPropsMap) {
						_companyPropsMap.put(intCompanyID, companyProps);
					}
					if (debug) _log.debug(count + " props loaded for intCompanyID: " + intCompanyID);
				} else {
					_notifyListeners(changedNameSet, companyProps.getProps(), intCompanyID);
				}
			}
		} else {
			if (debug) _log.debug("No company props to update.");
		}
				
		// Check if any global props were updated.
		if (globalUpdatedPropsList != null && !globalUpdatedPropsList.isEmpty()) {
			
			// Get all companies we manage.
			Collection<CompanyProps> allCompanyProps = null;
			synchronized (_companyPropsMap) {
				allCompanyProps = _companyPropsMap.values();
			}
			
			// Update the global props for each company.
			for (CompanyProps companyProps : allCompanyProps) {
				int intCompanyID = companyProps.getIntCompanyID();
				
				// Skip the global.
				if (intCompanyID == -1) {
					continue;
				}
				
				// Skip any that we already updated.
				if (updatedIntCompanyIDs.contains(intCompanyID)) {
					continue;
				}
	
				Set<String> changedNameSet = new HashSet<String>();
				
				if (debug) _log.debug("Refreshing " + globalUpdatedPropsList.size() + " global props for intCompanyID: " + intCompanyID);
				
				// Update the company globals.
				count += _updateProps(companyProps, globalUpdatedPropsList, changedNameSet, dbID);
			
				_notifyListeners(changedNameSet, companyProps.getProps(), intCompanyID);
			}
		}
		
		// Do a quick check to make sure the loaded company was loaded. It may not have had any company props.
		if (loadIntCompanyID != -1) {
			// Get the company's props.
			CompanyProps companyProps = null;
			synchronized (_companyPropsMap) {
				companyProps = _companyPropsMap.get(loadIntCompanyID);
			}
			
			// Check if they exist.
			if (companyProps == null) {
				if (debug) _log.debug("Loading company props for intCompanyID: " + loadIntCompanyID + "...");
				
				// It is new, create it and init the props using the globals.
				if (debug) _log.debug("Initing with " + globalProps.getProps().size() + " global props.");
				companyProps = new CompanyProps(loadIntCompanyID, globalProps.getProps());
				synchronized (_companyPropsMap) {
					_companyPropsMap.put(loadIntCompanyID, companyProps);
				}
				if (debug) _log.debug(companyProps.getProps().size() + " props loaded for intCompanyID: " + loadIntCompanyID);
			}
		}

		try {
			_cacheRefreshInterval = globalProps.getProps().getLongValue(PROP_im_configRefreshInterval, 60) * 1000;
		} catch (FTPropsValueIllegalException e) {
			_log.warning(FTErrorConst.FT_WARNING_INVALID_IM_PROPERTY,PROP_im_configRefreshInterval);
		}	

		if (debug) _log.debug(count + " server props updated. Last modified time was " + new Timestamp(_cacheUpdateTimeMap.get(dbID)));
	}
	
	// Update props for a company.
	private int _updateProps(	CompanyProps 				companyProps, 
								ArrayList<IMServerProperty> propsList,
								Set<String> 				changedNameSet,
								int 						dbID) 
		throws IMDBException {
		
		// Check for updated props.
		if (propsList == null || propsList.isEmpty()) {
			return 0;
		}
		
		boolean					debug			= _log.debug();
		boolean					trace			= _log.trace();
		FTProperties 			props 			= companyProps.getProps();
		HashMap<String,Integer>	priorities		= companyProps.getPriorities();
		int						count			= 0;	
		long 					permsModTime = -1;

		// Get the largest permission modTime.
		try {
			permsModTime = props.getLongValue(PROP_im_globalPermsModTime, -1);
		} catch (FTPropsValueIllegalException e) {
			// Can not happen.
			_log.warning("Illegal value for: " + PROP_im_globalPermsModTime);
		}

		FTHashtable<String,String> serverPerms = (FTHashtable<String,String>) props.get(PROP_im_globalPerms);

		if (serverPerms == null) {
			// This should never happen. We create them when the props are created.
			throw new IMDBException("Server perms were not created.");
		}

		// Fill it with the properties.
		for (IMServerProperty prop : propsList) {
			String name 		= prop.getName();
			String value 		= prop.getValue();
			long   modTime 		= prop.getModTime();
			int    serverID 	= prop.getServerID();
			int	   priority		= prop.getPriority();
			int	   intCompanyID = prop.getIntCompanyID();
			
			// Skip props assigned to another server.
			if (serverID != -1 && serverID != _serverID) {
				String msg;
				if (name.toLowerCase().contains("password")) {
					msg = "Skipping cache update - name: " + name + ", value: ********, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime);
				} else if (name.toLowerCase().contains("client.secret")) {
					msg = "Skipping cache update - name: " + name + ", value: ********************, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime);
				} else {
					msg = "Skipping cache update - name: " + name + ", value: " + value + ", serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime);
				}
				
				if (changedNameSet == null) {
					_log.trace(msg);
				} else {
					_log.debug(msg);
				}
				continue;
			}
			
			// Check the priority first.
			if (priorities.containsKey(name)) {
				int oldPriority = priorities.get(name);
				
				// Only update if priority is equal or higher.
				if (priority < oldPriority) {
					if (debug || trace) {
						String msg;
						if (name.toLowerCase().contains("password")) {
							msg = "Skipping cache update - name: " + name + ", value: ********, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", priority: " + priority + ", oldPriority: " + oldPriority + ", modTime: " + new Timestamp(modTime);
						} else if (name.toLowerCase().contains("client.secret")) {
							msg = "Skipping cache update - name: " + name + ", value: ********, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", priority: " + priority + ", oldPriority: " + oldPriority + ", modTime: " + new Timestamp(modTime);
						} else {
							msg = "Skipping cache update - name: " + name + ", value: " + value + ", serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", priority: " + priority + ", oldPriority: " + oldPriority + ", modTime: " + new Timestamp(modTime);
						}
						
						if (changedNameSet == null) {
							_log.trace(msg);
						} else {
							_log.debug(msg);
						}
					}
					continue;
				}
			}
			
			// Replace product name tokens in all props.
			value = value.replaceAll("\\$\\{productName\\}", _productName);
			value = value.replaceAll("\\$\\{fullProductName\\}", _fullProductName);

			// Check for the newest modTime.
			if (_cacheUpdateTimeMap.get(dbID) < modTime) {
				_cacheUpdateTimeMap.put(dbID,modTime);
			}

			// Check if a permission.
			if (name.startsWith(IMConstants.PERM_type)) {

				// Save the perm in the perm hashtable.
				serverPerms.put(name, value);

				// Update the largest permission modTime.
				if (modTime > permsModTime) {
					permsModTime = modTime;
				}
			} else if (name.startsWith(IMAdminProps.POLICY_CONFLICT_RESOLUTION_TYPE)) {
				// Bug fix: 11747. Check if a policy conflict resolution
				if (modTime > permsModTime) {
					permsModTime = modTime;
				}
			} else if (name.startsWith(IMAdminProps.UC_AUDIO_POLICY_PREFIX)
			        || name.startsWith("uc.audio.enterprise")) {
                //consider UC Audio recording policies also
                if (modTime > permsModTime) {
                    permsModTime = modTime;
                }
            }

			// Add to changed list if not there.
			if (changedNameSet != null && !changedNameSet.contains(name)) {
				changedNameSet.add(name);
			}

			// Update the prop.
			props.put(name, value);
			
			// Set the priority for the prop.
			priorities.put(name, priority);
			
			count++;
			
			if (debug || trace) {
				String msg;
				if (name.toLowerCase().contains("password")) {
					msg = "Updated cache - name: " + name + ", value: ********, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", priority: " + priority + ", modTime: " + new Timestamp(modTime);
				} else if (name.toLowerCase().contains("client.secret")) {
					msg = "Updated cache - name: " + name + ", value: ***********************, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", priority: " + priority + ", modTime: " + new Timestamp(modTime);
				} else {
					msg = "Updated cache - name: " + name + ", value: " + value + ", serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", priority: " + priority + ", modTime: " + new Timestamp(modTime);
				}
				
				if (changedNameSet == null) {
					_log.trace(msg);
				} else {
					_log.debug(msg);
				}
			}
		}

		props.setLongValue(PROP_im_globalPermsModTime, permsModTime);
		
		return count;
	}
	
	// Load the global props. Keep this synchronized. It is only called on startup.
	private synchronized void _loadGlobalProps()
		throws IMDBException {

		boolean debug = _log.debug();
		
		if (debug) _log.debug("Loading global props...");

		// Get the global props.
		CompanyProps globalProps = null;
		synchronized (_companyPropsMap) {
			globalProps = _companyPropsMap.get(IMCompany.DEFAULT_COMPANY_ID);
		}
		
		// Check if they exist.
		if (globalProps != null) {
			// Should never happen.
			return;
		}
		
		// Create new props.
		globalProps = new CompanyProps(IMCompany.DEFAULT_COMPANY_ID);	
         
		// Get all of the global props. For global prop use central database id
		ArrayList<IMServerProperty> globalPropsList = _getCompanyProps(IMDatabaseMgr.COMMON_DATA_STORE_ID,IMCompany.DEFAULT_COMPANY_ID);
		
		// Check if we have global props.
		if (globalPropsList.isEmpty()) {
			// Should never happen
			throw new IMDBException("Global server properties do not exist.");
		}
		
		if (debug) _log.debug("Initing " + globalPropsList.size() + " global props.");
			
		// Update the global props.
		int count = _updateProps(globalProps, globalPropsList, null, IMDatabaseMgr.instance().getDBId(IMDatabaseMgr.COMMON_DATA_STORE_ID));
		
		synchronized (_companyPropsMap) {
			_companyPropsMap.put(IMCompany.DEFAULT_COMPANY_ID, globalProps);	
		}
		
		if (debug) _log.debug(count + " global props loaded.");
	}
	
    /**
	 * Get all of the updated server properties organized by intCompanyID for the loaded companies.
	 * If optional loadIntCompanyID is set, get all of its props.
	 * 
	 * Note, we need to load and update at the same time otherwise there is a race condition
	 * and we can mess up the _cacheUpdateTime.
	 *
	 * @param		updateTime					get properties updated after this time.
	 * @param		intCompanyIDs				loaded internal company IDs.
	 * @param		loadIntCompanyID			load this internal company ID.
	 * @return									the server properties organized by intCompanyID.
	 * @exception	IMDBException				Database error.
	 */
    private HashMap<Integer,ArrayList<IMServerProperty>> _getUpdatedServerProps(long 	updateTime,
    																			int[] 	intCompanyIDs,
    																			int		loadIntCompanyID,
    																			int 	dbCompanyID)
    	throws IMDBException {

    	String sqlID = "selectUpdatedPropsSQL";
    	
    	if (loadIntCompanyID != -1) {
    		sqlID = "selectUpdatedPropsAndLoadSQL";
    	}
    	
		boolean debug = _log.debug();
		boolean trace = _log.trace();

		if (debug) _log.debug("_getUpdatedServerProps: " +
				"updateTime: " 			+ new Timestamp(updateTime) +
				", intCompanyIDs: " 	+ FTSQLUtils.toCommaString(intCompanyIDs) +
				", loadIntCompanyID: " 	+ loadIntCompanyID);

		int retryCnt = 0;

		while (true) {
			FTSQLConnection dbConn 	= getDbConnection(dbCompanyID);
			ResultSet 		rs 		= null;
			Statement 		stmt 	= null;
			String sql =  null;
	        try {
	        	HashMap<Integer,ArrayList<IMServerProperty>> map = new HashMap<Integer,ArrayList<IMServerProperty>>();
	        		
				 sql = getSQL(sqlID);

				sql = FTSQLUtils.replaceParam(sql, updateTime);
				sql = FTSQLUtils.replaceParam(sql, intCompanyIDs);
				
				// Check for optional load.
				if (loadIntCompanyID != -1) {
					sql = FTSQLUtils.replaceParam(sql, loadIntCompanyID);
				}
				
				sql = FTSQLUtils.replaceDone(sql);
                
                if (trace) _log.trace("SQL: " + sql);
                
                stmt = dbConn.createStatementWithStats(sqlID);
                
                rs = stmt.executeQuery(sql);    
                
                int updatedCount = 0;

	        	// Get all of the server properties.
	        	while (rs.next() == true) {
	        		String	name			= rs.getString("name");
	        		String	value			= rs.getString("value");
	        				value 			= (value == null) ? "" : value;
	        		String	type			= rs.getString("type");
	        		int		serverID		= rs.getInt("serverID");
	        		int		intCompanyID	= rs.getInt("intCompanyID");
	        		long    modTime         = rs.getLong("modTime");

	        		if (name == null || name.length() == 0) {
	        			// Should never happen.
	        			_log.warning("Empty server property found.");
	        			continue;
	        		}
	        		
	        		ArrayList<IMServerProperty> list = null;
	        		
	        		// Check if list is in map.
	        		list = map.get(intCompanyID);
	        		
	        		if (list == null) {
	        			list = new ArrayList<IMServerProperty>();
	        			map.put(intCompanyID, list);
	        		}
	        		
	        		try {
		        		// Add to list.
		        		IMServerProperty prop = new IMServerProperty(	name,
						        										value,
						        										null,
						        										type,
						        										null,
																		intCompanyID,
																		serverID,
																		modTime);
		        		
		        		list.add(prop);
		        		if (trace) {
		        			if (name.toLowerCase().contains("password")) {
		        				_log.trace("Found updated prop: " + name + ", value: ********, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime));
		        			} else if (name.toLowerCase().contains("client.secret")) {
		        				_log.trace("Found updated prop: " + name + ", value: ********************, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime));
		        			} else {
		        				_log.trace("Found updated prop: " + name + ", value: " + value + ", serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime));
		        			}
		        		}
		        			updatedCount++;
	        		} catch (Exception e) {
	        			// Should not happen.
	        			_log.warning("Server property error: " + e.getMessage());
	        		}
	        	}

				rs.close();
				rs = null;
				stmt.close();
				stmt = null;

				if (debug) _log.debug("Found " + updatedCount + " props.");
				
				return map;

			} catch (Exception e) {
				if(e instanceof SQLException){
					_log.info("DB Exception occured while running query:- "+sql);
				}
				if (rs != null) {
	    			try {
	    				rs.close();
						rs = null;
	    			} catch (Exception e2) {
	    				// Ignore.
	    			}
	    		}
				if (stmt != null) {
	    			try {
	    				stmt.close();
	    				stmt = null;
	    			} catch (Exception e2) {
	    				// Ignore.
	    			}
	    		}

				retryCnt = handleException(e, "Get updated server properties failed. Reason: " + e.getMessage(), retryCnt, dbConn, dbCompanyID);
			} finally {
				releaseDbConnection(dbConn, dbCompanyID);
			}
		}
    }
	
    /**
	 * Get server properties for a company.
	 *
	 * @param		intCompanyID				internal company ID.
	 * @return									the company properties.
	 * @exception	IMDBException				Database error.
	 */
    private ArrayList<IMServerProperty> _getCompanyProps(int dbCompanyID, int intCompanyID)
        throws  IMDBException {

		boolean debug = _log.debug();
		boolean trace = _log.trace();
		
		int retryCnt = 0;
		
		PreparedStatement stmt = null;
		while (true) {

			FTSQLConnection dbConn 	= getDbConnection(dbCompanyID);
			ResultSet 		rs 		= null;

	        try {
	        	ArrayList<IMServerProperty> list = new ArrayList<IMServerProperty>();
	        		
				if (debug) _log.debug("_getCompanyProps: intCompanyID: " + intCompanyID);

			   stmt = getStmt(dbConn, "selectCompanyPropsStmt",dbCompanyID);

				stmt.setLong(1, intCompanyID);

	    		rs = stmt.executeQuery();

	        	// Get all of the server properties.
	        	while (rs.next() == true) {
	        		String	name			= rs.getString("name");
	        		String	value			= rs.getString("value");
	        				value 			= (value == null) ? "" : value;
	        		String	type			= rs.getString("type");
	        		int		serverID		= rs.getInt("serverID");
	        		long    modTime         = rs.getLong("modTime");

	        		if (name == null || name.length() == 0) {
	        			// Should never happen.
	        			_log.warning("Empty server property found.");
	        			continue;
	        		}
	        		
	        		try {
		        		// Add to list.
		        		IMServerProperty prop = new IMServerProperty(	name,
						        										value,
						        										null,
						        										type,
						        										null,
																		intCompanyID,
																		serverID,
																		modTime);
		        		
		        		list.add(prop);
		        		if (trace) { 
		        			if (name.toLowerCase().contains("password")) {
		        				_log.trace("Found prop: " + name + ", value: ********, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime));
		        			} else if (name.toLowerCase().contains("client.secret")) {
		        				_log.trace("Found prop: " + name + ", value: ******************, serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime));
		        			} else {
		        				_log.trace("Found prop: " + name + ", value: " + value + ", serverID: " + serverID + ", intCompanyID: " + intCompanyID + ", modTime: " + new Timestamp(modTime));
		        			}
		        			
		        		}
	        		} catch (Exception e) {
	        			// Should not happen.
	        			_log.warning("Server property error: " + e.getMessage());
	        		}
	        	}

				rs.close();
				rs = null;

				if (debug) _log.debug("Found " + list.size() + " props.");
				
				return list;

			} catch (Exception e) {
				if(e instanceof SQLException){
					_log.info("DB Exception occured while running query:- "+ ((FTSQLPreparedStatement)stmt).getSQLString()+
							" with parameters: "+" intCompanyID = "+intCompanyID );
				}
				if (rs != null) {
	    			try {
	    				rs.close();
						rs = null;
	    			} catch (Exception e2) {
	    				// Ignore.
	    			}
	    		}

				retryCnt = handleException(e, "Get company properties failed. Reason: " + e.getMessage(), retryCnt, dbConn, dbCompanyID);
			} finally {
				releaseDbConnection(dbConn, dbCompanyID);
			}
		}
    }
    
    
    public long getNumberOfFilesInDB(int intCompanyID)
    throws  IMDBException {

    	boolean debug = _log.debug();
    	boolean trace = _log.trace();
    	if (debug) _log.debug("getNumberOfFilesInDB: (noargs)");
		int retryCnt = 0;
		long count = 0;

		while (true) {
			ResultSet   	rs      = null;
			Statement stmt			= null;
			FTSQLConnection dbConn 	= getDbConnection(intCompanyID);
			String sql = null;
			try {
				
        	String sqlId = "getNumberOfFilesInDB_nonEmail";
        	sql = getSQL(sqlId);
        	sql = getSQL(sqlId);
        	stmt = dbConn.createStatementWithStats(sqlId);
        	rs = stmt.executeQuery(sql);
    		
    		while (rs.next()) {
            	count = rs.getLong("count");
            }

            if (rs != null) {
                try {
                    rs.close();
					rs = null;
                } catch (Exception ignore) {
                    // Ignore.
                }
            }
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (Exception ignore) {
					// Ignore.
				}
			}
			
            return count;

    	} catch (Exception e) {
    		if(e instanceof SQLException){
    			_log.info("DB Exception occured while running query:- "+sql);
    		}
			if (rs != null) {
    			try {
    				rs.close();
					rs = null;
    			} catch (Exception e2) {
    				// Ignore.
    			}
    		}

			retryCnt = handleException(e, "getNumberOfFilesInDB Failed. Reason: " + e.getMessage(), retryCnt, dbConn, intCompanyID);
		} finally {
			releaseDbConnection(dbConn, intCompanyID);
		}
	}
}
    
    public long getNumberOfExtractedFilesInDB(int intCompanyID)
    throws  IMDBException {

    	boolean debug = _log.debug();
    	boolean trace = _log.trace();
    	if (debug) _log.debug("getNumberOfExtractedFilesInDB: (noargs)");
		int retryCnt = 0;
		long count = 0;

		while (true) {
		FTSQLConnection dbConn 	= getDbConnection(intCompanyID);
		ResultSet 		rs 		= null;
		Statement stmt			= null;
		String sql = null;
		try {
        	String sqlId = "getNumberOfExtractedFilesInDB";
        	sql = getSQL(sqlId);
        	sql = getSQL(sqlId);
        	stmt = dbConn.createStatementWithStats(sqlId);
        	rs = stmt.executeQuery(sql);
    		
    		while (rs.next()) {
            	count = rs.getLong("count");
            }

            if (rs != null) {
                try {
                    rs.close();
					rs = null;
                } catch (Exception ignore) {
                    // Ignore.
                }
            }
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (Exception ignore) {
					// Ignore.
				}
			}
			
            return count;

    	} catch (Exception e) {
    		if(e instanceof SQLException){
    			_log.info("DB Exception occured while running query:- "+sql); 
    		}
			if (rs != null) {
    			try {
    				rs.close();
					rs = null;
    			} catch (Exception e2) {
    				// Ignore.
    			}
    		}

			retryCnt = handleException(e, "getNumberOfExtractedFilesInDB failed. Reason: " + e.getMessage(), retryCnt, dbConn, intCompanyID);
		} finally {
			releaseDbConnection(dbConn, intCompanyID);
		}
	}
}    
	//
	// Service Interface
	// 

	private int _state = STATE_CREATED;

	protected ServiceLocatorInterface _locator;

	public Class getServiceInterface() {
		return getClass();
	}

	public void init(ServiceLocatorInterface locator) throws ServiceException {
		if (!_initing()) return;
		_locator = locator;
		_inited();
	}

	synchronized public void start() throws ServiceException {
		if (!_starting()) return;
		_started();
	}

	synchronized public void stop() {
		if (!_stopping()) return;
		_stopped();
	}

	public synchronized boolean isCreated() {
		return _state == STATE_CREATED;
	}

	public synchronized boolean isInited() {
		return _state == STATE_INITED;
	}

	public synchronized boolean isStarted() {
		return _state == STATE_STARTED;
	}

	public synchronized boolean isStopped() {
		return _state == STATE_STOPPED;
	}

	public int getState() {
		return _state;
	}

	synchronized private boolean _initing() {
		if (_state == STATE_CREATED) {
			_state = STATE_INITING;
			return true;
		}
		return false;
	}

	synchronized private void _inited() throws FTServiceStateException {
		if (_state == STATE_INITING) {
			_state = STATE_INITED;
		} else {
			throw new FTServiceStateException(_state, STATE_INITING);
		}
	}

	synchronized private boolean _starting() {
		if (_state == STATE_INITED) {
			_state = STATE_STARTING;
			return true;
		}
		return false;
	}

	synchronized private void _started() throws FTServiceStateException {
		if (_state == STATE_STARTING) {
			_state = STATE_STARTED;
		} else {
			throw new FTServiceStateException(_state, STATE_STARTING);
		}
	}

	synchronized private boolean _stopping() {
		if (_state != STATE_STOPPING && _state != STATE_STOPPED) {
			_state = STATE_STOPPING;
			return true;
		}
		return false;
	}

	synchronized private void _stopped() throws FTServiceStateException {
		if (_state == STATE_STOPPING) {
			_state = STATE_STOPPED;
		} else {
			throw new ServiceStateException(_state, STATE_STOPPING);
		}
	}



	////////////////////////////////////

	/*static public void main(String[] args) {

		IMConfigDBMgr config  = new IMConfigDBMgr();

		FTProperties props = new FTProperties();


		config.addConfigUpdateListener(new FTConfigUpdateListener() {
				public void configUpdated(FTProperties props, Collection keys) {
					System.out.println("Updated a");
				}
			}, new String[]{"a"});

		config.addConfigUpdateListener(new FTConfigUpdateListener() {
				public void configUpdated(FTProperties props, Collection keys) {
					System.out.println("Updated b or c");
				}
			}, new String[]{"b", "c"});
		config.addConfigUpdateListener(new FTConfigUpdateListener() {
				public void configUpdated(FTProperties props, Collection keys) {
					System.out.println("Updated all");
				}
			});

		Set changed = new HashSet();

		changed.add("wow");

		System.out.println("Notifying about wow");

		config._notifyListeners(changed, props);

		changed.add("c");
		System.out.println("Notifying about wow and c");
		config._notifyListeners(changed, props);

		changed.add("bob");
		System.out.println("Notifying about wow and c and bob");
		config._notifyListeners(changed, props);

		changed.add("alice");
		System.out.println("Notifying about wow and c and bob and alice");
		config._notifyListeners(changed, props);

	} */
	
	//
	// XXX - the following code does not belong in this file!!!
	//

	private static volatile boolean isTimerStarted;

	/**
     * Start the Job to store TimeOffset every night at 12:15 PM
     */
    private void startTimeZoneOffsetTimer() {
        synchronized(this){
            if (!isTimerStarted){
                Timer timer = new Timer("TimeZone Offset Timer", true);
                TimeZoneOffsetScheduler tzScheduler = new TimeZoneOffsetScheduler();
                              
                int totaldelay = 3000;      // 3 sec        
                int interval = 60*60*1000;  // 1 hr
                
                timer.schedule(tzScheduler, totaldelay, interval); 
                isTimerStarted = true;
            }
        }
    }

	@Override
	public void initCompanyData(int intCompanyID) throws IMDBException {
		// Do nothing
	}

    
	@Override
	public FTProperties getConfig() {
		try{
			return getConfig(IMCompany.DEFAULT_COMPANY_ID);
		}catch(Exception e){
			_log.warning("this method should not be used. Instead use the method getConfig(int intCompanyID)", e.getMessage());
		}
		return null;
	}

	
	@Override
	public FTProperties getFreshConfig() {
		try{
			return getFreshConfig(IMCompany.DEFAULT_COMPANY_ID);
		}catch(Exception e){
			_log.warning("this method should not be used. Instead use the method getFreshConfig(int intCompanyID)", e.getMessage());
		}
		return null;
	}

	public void setCompanyManager(CompanyManager _companyMgr) {
		this._companyManager=_companyMgr;
	}
}

/**
 * Class for storing the offset of the TimeZones
 * @author Anurag Kumar
 *
 */
class TimeZoneOffsetScheduler extends TimerTask{
    
    private long waitPeriodOnFailure = 60000;
    private final long incrementChunk = 20000;
    private Properties timeZoneOffsetProps;
    private boolean firstExecution = true;
    
    public TimeZoneOffsetScheduler(){
        timeZoneOffsetProps = new Properties();
        File file = new File(DirectoryAssistance.getInstance().getConfigDir() + 
                File.separator + "tzOffset.properties");
        if (file.exists()){
            try{
                FileInputStream fis = new FileInputStream(file);
                timeZoneOffsetProps.load(fis);
                fis.close();
            }catch(IOException e){
                //ignore
            }          
        }
    }

    /* (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        if (!firstExecution){
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            int day = cal.get(Calendar.DAY_OF_WEEK);
            if (day != Calendar.SUNDAY){
                return;
            }
        }
                
        boolean success = false;
        do{
            success = storeTimeZonesOffset(); 
        }while(!success);
        waitPeriodOnFailure = 60000;
        firstExecution = false;       
    }
    
    /**
     * Stores the Offset of every TimeZone from the UTC
     */
    private boolean storeTimeZonesOffset(){
        File file = new File(DirectoryAssistance.getInstance().getConfigDir() + 
                File.separator + "tzOffset.properties");
        
        try {
            Properties props = new Properties();
            String[] zones = TimeZone.getAvailableIDs();
            for (int i = 0; i < zones.length; i++){
                props.put(zones[i], 
                        String.valueOf(TimeZone.getTimeZone(zones[i]).getOffset(System.currentTimeMillis())));
            }
            if (!timeZoneOffsetProps.equals(props)){
                FileOutputStream fos = new FileOutputStream(file);
                props.store(fos, null);
                timeZoneOffsetProps = props;
                fos.close();
                IPCMessageUtil.getInstance().sendTimeZoneOffsetMessage(true);
            }
            return true;
            
        } catch (IOException e) {
            try {
                Thread.sleep(waitPeriodOnFailure);
                waitPeriodOnFailure = waitPeriodOnFailure + incrementChunk;
            } catch (InterruptedException ex) {
                //Ignore
                waitPeriodOnFailure = waitPeriodOnFailure + incrementChunk;
            }
            return false;
        }
    }
}
