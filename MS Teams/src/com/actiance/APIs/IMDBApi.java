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
 


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import junit.framework.Assert;

import com.facetime.ftcore.sql.FTSQLClosedConnException;
import com.facetime.ftcore.sql.FTSQLConnection;
import com.facetime.ftcore.sql.FTSQLException;
import com.facetime.ftcore.sql.FTSQLTransaction;
import com.facetime.ftcore.util.ConnPoolStmt;
import com.facetime.ftcore.util.FTLogChannel;
import com.facetime.ftcore.util.FTLogMgr;
import com.facetime.ftcore.util.FTProperties;
import com.facetime.imcoreserver.security.exception.PasswordStrengthException;

public abstract class IMDBApi 
	implements 	IMDBApiInterface {
	
	// Retries
	protected static final int RETRY_WAIT 	= 100;		// 100 msecs.
	private static final int QUERY_TIMEOUT_VALUE = 60 * 3; // 3 minutes.
	protected	String 		        _poolName;
	protected	FTProperties		_props;
	protected	Object				_waitLock;		// Object used for retries.
 	protected	FTLogChannel		_log;
	protected	boolean				_initedFlag;

	
	protected IMDBApi() {
	}
	
	/**
	 * Initialize the DB API.
	 * 
	 * @param 	connPool		Connection pool.
	 * @param 	props			Initialization properties.
	 * @param	logMgr			Log manager.
	 * @throws 	IMDBException	Could not initialize.
	 */
	public synchronized void init(	String          poolName,
									FTProperties	props,
									FTLogMgr		logMgr)	
		throws 	IMDBException {
		
    	_log 			= logMgr.createLogChannel(getClass(), getDBApiName() + "[" + poolName + "]");     
    	_poolName 		= poolName;
    	_props			= props;
		_waitLock		= new Object();

		// _initedFlag should be set by implementing API.
	}
	
    /**
     * Reset the DB API.
     */
    public synchronized void reset() {
   	
    	// Cleanup.
    	_poolName 		= null;
    	_props	 		= null;
    	
    	// _initedFlag should be cleared by implementing API.
    }
    
    /** 
     * Return true if API is inited.
     * 
     * @return	true if API is inited.
     */
    public synchronized boolean isInited() {
    	return _initedFlag;
    }
    
    /** 
     * Check if API is inited. If not, throw exception.
     * 
     * @exception	IMDBException	API not inited.
     */
    public synchronized void initedCheck() 
    	throws IMDBException {

    	if (!_initedFlag) {
    		throw new IMDBException(getDBApiName() + " not inited.");
    	}
    }
    
    /**
     * Get this API's connection pool.
     * @param _intCompanyID TODO
     * 
     * @return this API's connection pool.
     */
    public IMDBConnPool getDBConnPool(int _intCompanyID) {
    	IMDBConnPool _connPool=null;
    	Integer dbID= IMDatabaseMgr.instance().getDBId(_intCompanyID);
			try {				
				if(dbID==null){
		    		_log.warning("Could not get DBID for intcompanyID : "+_intCompanyID);
		    		throw new IMDBNotFoundException("Could not find database for intCompanyID :"+ _intCompanyID);
		    	}
				_connPool=IMDatabaseMgr.instance().getDBConnPool(dbID, _poolName);
				if(_connPool==null){
					_log.warning("Could not get connection pool for intcompanyID : "+_intCompanyID);
		    		throw new IMDBNotFoundException("Could not get connection pool for intcompanyID :"+ _intCompanyID);
				}
			} catch (IMDBNotFoundException e) {
				_log.warning("Exception occurred while getting DB connection pool " + e.getMessage());
				_log.warning("Cause: " + e.getCause());
			}
    	return _connPool;
    }
    
    /**
	 * Get a DB API for this pool.
	 * 
	 * @param		dbApiName				DB API name.
	 * @return								DB API.
	 * @exception	IMDBNotFoundException	If the API doesn't exist.
	 */
	public IMDBApi getDBApi(String dbApiName) 
		throws IMDBNotFoundException {
		IMDBApi instance=IMDatabaseMgr.instance().getDBApiMgrs(_poolName).get(dbApiName);
		if(instance==null){
			_log.warning("Could not get IMDBAPI instance : "+dbApiName);
    		throw new IMDBNotFoundException("Could not get IMDBAPI instance : "+dbApiName);    
		}
		return instance;
	}
    
	
	
	/**
	 * Get a DB API for this pool.
	 * 
	 * @param		dbApiName				DB API name.
	 * @return								DB API.
	 * @exception	IMDBNotFoundException	If the API doesn't exist.
	 */
	public IMDBApi getDBApi(String poolName,String dbApiName) 
		throws IMDBNotFoundException {
		IMDBApi instance=IMDatabaseMgr.instance().getDBApiMgrs(poolName).get(dbApiName);
		if(instance==null){
			_log.warning("Could not get IMDBAPI instance : "+dbApiName);
    		throw new IMDBNotFoundException("Could not get IMDBAPI instance : "+dbApiName);    
		}
		return instance;
	}
	/**
     * Return true if the DB connection is up.
	 * @param _intCompanyID TODO
     *
     * @return true if the DB connection is up.
     */
    public boolean isConnectionUp(int _intCompanyID) {
    	IMDBConnPool _connPool= getDBConnPool(_intCompanyID);
        return  _connPool.isConnectionUp();
    }
    
	protected void _checkDbIsUp(int _intCompanyID) throws IMDBClosedConnException {
		if (!isConnectionUp(_intCompanyID)) throw new IMDBClosedConnException("DB is down.", true);
	}

	
	public void prepareStatements(String[][] sqlStrs) throws IMDBException {
		try {
			prepareStatements(sqlStrs, QUERY_TIMEOUT_VALUE);
		} catch (IMDBException e) {
			throw new IMDBException(e);
		}
	}
	
	public void prepareStatements(String[][] sqlStrs, int queryTimeout) throws IMDBException {
		try {
			String 			name 	= null;
			String 			sql 	= null;
				
				// Store each statement.
				for (int i = 0; i < sqlStrs.length; i++) {
					name = sqlStrs[i][0];
					sql = sqlStrs[i][1];
					_putStmt(name, sql, queryTimeout);
				}	

		} catch (IMDBException e) {
			throw new IMDBException(e);
		}
	}
	
	public void setQueryTimeout(String name, int queryTimeout) throws IMDBException {
		try {
			ConnPoolStmt connPoolStmt = _getStmt(name);
			 
			 if (connPoolStmt == null) {
				throw new FTSQLException("Prepared statement " + name + " does not exist.");
			 }
			 connPoolStmt.setQueryTimeout(queryTimeout);

		} catch (FTSQLException e) {
			throw new IMDBException(e);
		}
	}

	
	public PreparedStatement getStmt(FTSQLConnection dbConn, String name,
			int intCompanyID) throws IMDBException {

		// It didn't exist yet. Get it from the pool.
		ConnPoolStmt connPoolStmt = _getStmt(name);

		if (connPoolStmt == null) {
			throw new IMDBException("Prepared statement " + name
					+ " does not exist.");
		}

		String sql = connPoolStmt.getSQL().trim();
		if (_log.trace())
			_log.trace("Getting connection: " + dbConn.getConnID()
					+ ", statement: " + name + ", SQL: " + sql);

		try {
			IMDBConnPool _connPool = getDBConnPool(intCompanyID);
			return _connPool.getConnectionPool().getStmt(dbConn, connPoolStmt);
		} catch (FTSQLClosedConnException e) {
			throw new IMDBClosedConnException(e, true);
		} catch (FTSQLException e) {
			throw new IMDBException(e);
		}
	}

	public FTSQLConnection getDbConnection(Object lockResource,int intCompanyID) throws IMDBException {
		try {
			IMDBConnPool _connPool=getDBConnPool(intCompanyID);
			if(_connPool == null) {
				throw new IMDBException("Couldn't get connection pool for intCompanyID:"+intCompanyID);
			}
			return _connPool.getConnectionPool().getDbConnection(lockResource);
		} catch (FTSQLClosedConnException e) {
			throw new IMDBClosedConnException(e, true);
		} catch (FTSQLException e) {
			throw new IMDBException(e);
		}
	}

	public FTSQLConnection getDbConnection(int _intCompanyID) throws IMDBException {
		return getDbConnection(null, _intCompanyID);
	}

	public void releaseDbConnection(Object lockResource, FTSQLConnection conn, int intCompanyID) {
		IMDBConnPool _connPool=getDBConnPool(intCompanyID);
		// Asserting that the connection is being released to the pool from 
		// where it was taken
		if(!conn.getConnectionInfo().getUrl().equalsIgnoreCase(_connPool.getDB().getDbURL())) {
		    Assert.assertEquals(conn.getConnectionInfo().getUrl(), _connPool.getDB().getDbURL());
		}
		if (_log.debug())
			_log.debug("Returning Connection to Connection pool for intCompanyID: " + intCompanyID
					+ ", Pool name: " + _connPool._poolName + ", Database ID: " + _connPool.getDbID());

		_connPool.getConnectionPool().releaseDbConnection(lockResource, conn);
	}

	public void releaseDbConnection(FTSQLConnection conn, int intCompanyID) {
		releaseDbConnection(null, conn,intCompanyID);
	}

    /**
	 * Handle an exception and decide to retry.
	 *
	 * @param 		e							exception to handle.
     * @param 		errMsg						error message to print and return.
     * @param 		retryCnt					current retry count.
     * @param 		conn						database connection (optional - used for debugging).
     * @param intCompanyID TODO
	 * @exception 	IMDBClosedConnException		connection was closed.
	 * @exception 	IMDBException				database error.
	 */
	protected int handleException(Exception e, String errMsg, int retryCnt, FTSQLConnection conn, int intCompanyID)
		throws 	IMDBClosedConnException,
				IMDBException {

		return handleException(e, errMsg, retryCnt, null, conn,intCompanyID);
	}
	
	/**
	 * Handle exception and never retry.
	 *
	 * @param 		e							exception to handle.
	 * @param 		errMsg						error message to print and return.
	 * @param 		conn						database connection (optional - used for debugging).
	 * @param intCompanyID TODO
	 * @exception 	IMDBClosedConnException		connection was closed.
	 * @exception 	IMDBException				database error.
	 */
	protected int handleException(Exception e, String errMsg, FTSQLConnection conn, int intCompanyID)
		throws 	IMDBClosedConnException,
				IMDBException{

		return handleException(e, errMsg, Integer.MAX_VALUE, null, conn,intCompanyID);
	}

	/**
	 * Handle exception and never retry. If a transaction was used, roll it back.
	 *
	 * @param 		e							exception to handle.
	 * @param 		errMsg						error message to print and return.
	 * @param 		trans						transaction to roll back if not null.
	 * @param 		conn						database connection (optional - used for debugging).
	 * @exception 	IMDBClosedConnException		connection was closed.
	 * @exception 	IMDBException				database error.
	 */
	protected int handleException(Exception e, String errMsg, FTSQLTransaction trans, FTSQLConnection conn,int intCompanyID)
		throws 	IMDBClosedConnException,
				IMDBException{

		return handleException(e, errMsg, Integer.MAX_VALUE, trans, conn,intCompanyID);
	}
	
	/**
	 * Handle exception and decide to retry. If a transaction was used, roll it back.
	 *
	 * @param 		e							exception to handle.
	 * @param 		errMsg						error message to print and return.
	 * @param 		retryCnt					current retry count.
	 * @param 		trans						transaction to roll back if not null.
	 * @param 		conn						database connection (optional - used for debugging).
	 * @param intCompanyID TODO
	 * @exception 	IMDBClosedConnException		connection was closed.
	 * @exception 	IMDBException				database error.
	 */
	protected int handleException(Exception e, String errMsg, int retryCnt, FTSQLTransaction trans, FTSQLConnection conn, int intCompanyID)
		throws 	IMDBClosedConnException,
				IMDBException{

		boolean debugFlag = _log.debug();
		
		String connID = conn != null ? "ConnID: " + conn.getConnID() + ", " : "";
		IMDBConnPool _connPool=getDBConnPool(intCompanyID);
		try {
			// Check if a closed connection.
			if (e.getClass() == FTSQLClosedConnException.class) {

				// Tell the transaction it was closed.
				if (trans != null) {
					trans.setClosedFlag();
				}

				FTSQLException ftsqle = (FTSQLException) e;

				// Check if we should retry.
				if (ftsqle.isRecoverable() && retryCnt++ < _connPool.getConnectionPool().getMaxRetries()) {
					_log.info(connID + "Found closed connection exception: " + ftsqle.getMessage() + ", going to wait and retry, retryCnt = " + retryCnt +" For companyID :"+intCompanyID);
					synchronized (_waitLock) {
						try {
							_waitLock.wait(RETRY_WAIT);
						} catch (Exception unExp) {
							_log.warning("Retry wait returned unexpectedly."+" For companyID :"+intCompanyID);
						}
					}
					return retryCnt;
				}

				_log.info(connID + errMsg);
				IMDBClosedConnException e2 = new IMDBClosedConnException(errMsg, ftsqle.isRecoverable());
				e2.setStackTrace(e.getStackTrace());
				throw e2;

			// Check if a retryable exception.
			} else if (FTSQLException.class.isAssignableFrom(e.getClass())) {

				FTSQLException ftsqle = (FTSQLException) e;

				// Check if we should retry.
				if (ftsqle.isRecoverable() && retryCnt++ < _connPool.getConnectionPool().getMaxRetries()) {
					_log.info(connID + "Found recoverable exception: " + ftsqle.getMessage() + ", going to wait and retry, retryCnt = " + retryCnt, e+" For companyID :"+intCompanyID);

					synchronized (_waitLock) {
						try {
							_waitLock.wait(RETRY_WAIT);
						} catch (Exception unExp) {
							_log.warning("Retry wait returned unexpectedly."+" For companyID :"+intCompanyID);
						}
					}
					return retryCnt;
				}

				_log.info(connID + errMsg);
				IMDBException e2 = new IMDBException(errMsg, ftsqle.isRecoverable());
				e2.setStackTrace(e.getStackTrace());
				throw e2;

			// Check for a sql exception.
			} else if (e.getClass() == SQLException.class) {

				_log.info(connID + errMsg);
				IMDBException e2 = new IMDBException(errMsg);
				e2.setStackTrace(e.getStackTrace());
				throw e2;

			// Check for a DB exception.
			} else if (IMDBException.class.isAssignableFrom(e.getClass())) {

				_log.info(connID + errMsg);
				throw (IMDBException) e;

			// Check for a misc. exception.
			} 
			
			else if (PasswordStrengthException.class.isAssignableFrom(e.getClass())) {
				_log.info(connID + "PasswordStrengthException: " + errMsg + " For companyID :"+intCompanyID, e);
				IMDBException e2 = new IMDBException(errMsg + " For companyID :" + intCompanyID);
				e2.setStackTrace(e.getStackTrace());
				throw e2;
			} 
			
			else {

				_log.info(connID + "Unexpected exception: " + errMsg+" For companyID :"+intCompanyID, e);
				IMDBException e2 = new IMDBException("Unexpected exception: " + errMsg+" For companyID :"+intCompanyID);
				e2.setStackTrace(e.getStackTrace());
				throw e2;
			}

		} finally {
			// Check if a transaction.
			if (trans != null) {
				// Roll it back.
				try {
					trans.endTransaction(false);
				} catch (Exception e2) {
					// Should not happen on a rollback.
					_log.warning(connID + "End transaction failed on rollback, reason: "+" For companyID :"+intCompanyID + e2.getMessage(), e2);
				}
			}
		}
	}
    
    /**
     * Set the log level for debugging.
     * @param level the log level.
     */
    public void setLogLevel(int level) {
        _log.setLogLevel(level);
    }
    
   
	public static boolean canPrepareStatements(String[][] sqls) {
		if (sqls != null) {
			return (sqls[0][0] != null);
		} else {
			return false;
		}
	}
	
	public String getDbType(){
		return IMDatabaseMgr.instance().getDBType(_poolName);
	}
	
	
    // Storage of SQL and query timeouts.
    private HashMap<String,ConnPoolStmt>	
    	_stmts = new HashMap<String,ConnPoolStmt>();
    
    
    // Get a statement for the pool.
 	private ConnPoolStmt _getStmt(String name) {
 		synchronized (_stmts) {
 			if (_stmts.containsKey(name)) {
 				return _stmts.get(name);
 			} else {
 				return null;
 			}
 		}
 	}
 	
 // Put a statement in the pool.
 	private void _putStmt(String name, String sql, int queryTimeout) 
 		throws IMDBException {
 		
 		synchronized (_stmts) {
 			if (_stmts.containsKey(name)) {
 				ConnPoolStmt connPoolStmt = _stmts.get(name);
 				if (!connPoolStmt.getSQL().equals(sql) || connPoolStmt.getQueryTimeout() != queryTimeout) {
 					_log.warning("Duplicate statement found, name: " + name + ", orig SQL: " + connPoolStmt.getSQL() + ", new SQL: " + sql);
 					throw new IMDBException("Duplicate SQL statement found: " + name);
 				}
 				if (_log.trace()) _log.trace("Already inited: " + name + ", SQL: " + sql);
 				return;
 			}
 			
 			if (_log.trace()) _log.trace("Initing: " + name + ", SQL: " + sql);
 			_stmts.put(name, new ConnPoolStmt(name, sql, queryTimeout));
 		}
 	}
 	
 	
	
	
	

	/**
	 * Get the SQL for a statement.
	 * @param name		Statement name.
	 * @return			the SQL for a statement.
	 * @throws FTSQLException	SQL statement does not exist.
	 */
	public String getSQL(String name)
		throws IMDBException {
		
		// Get it from the pool.
		ConnPoolStmt connPoolStmt = _getStmt(name);
			
		if (connPoolStmt == null) {
			throw new IMDBException("SQL statement " + name + " does not exist.");
		}
		
		return connPoolStmt.getSQL();
	}
	

	/**
	 * Determine if the argument exception indicates a broken JDBC connection.
	 *
	 * @param e SQL exception thrown by JDBC operation
	 * @param intCompanyID TODO
	 * @return true iff argument indicates connection to database is broken
	 */
    public boolean isLostConnection(SQLException e, int intCompanyID) {
    	IMDBConnPool _connPool=getDBConnPool(intCompanyID);
    	return _connPool.isLostConnection(e);
    }	
	
    
    
    public String getSchemaName(int intCompanyID){
    	IMDBConnPool _connPool=getDBConnPool(intCompanyID);
    	return _connPool.getSchemaName();    	
    }
    
   
}
