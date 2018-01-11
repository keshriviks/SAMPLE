package com.actiance.APIs;

/*
 * Copyright (c) 2017 Actiance, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Actiance
 * , Inc. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Actiance.
 *
 * FACETIME MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. Actiance SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED
 * BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE
 * OR ITS DERIVATIVES.
 */


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facetime.ftcore.sql.FTSQLConnection;
import com.facetime.ftcore.sql.FTSQLTransaction;
import com.facetime.ftcore.util.FTLogMgr;
import com.facetime.ftcore.util.FTProperties;
import com.facetime.imcoreserver.db.IMDBApi;
import com.facetime.imcoreserver.db.IMDBClosedConnException;
import com.facetime.imcoreserver.db.IMDBException;
import com.facetime.imcoreserver.db.IMDBRecordAlreadyExistsException;
import com.facetime.imcoreserver.db.IMDBRecordNotFoundException;
/**
 * 
 * @author ARKumar
 *
 */
public class CursorDBMgr extends IMDBApi{

	private static final String SQL_CHECK_CURSOR = "checkCursor";
	private static final String SQL_SELECT_MAX_ID = "selectMaxID";
	private static final String SQL_INSERT_CURSOR = "insertCursor";
	private static final String SQL_UPDATE_CURSOR = "updateCursor";
	private static final String SQL_SELECT_CURSOR_VALUE = "selectCursorValue";
	private static final String SQL_SELECT_CURSOR_VALUES = "selectCursorvalues";
	private static final String SQL_RESET_CURSORS = "resetCursors";
	
	/** API name. */
	public static final String 	CURSOR_MGR_NAME = "CursorDBMgr";
	private static final String MSG_CURSOR_NOT_EXIST = "Cursor does not exist: ";
	
	@Override
	public String getDBApiName() {
		return CURSOR_MGR_NAME;
	}

	@Override
	public void initCompanyData(int intCompanyID) throws IMDBException {

	}

	public synchronized void init(String connPool,FTProperties props,FTLogMgr logMgr)	
			throws  IMDBException 
	{
		if (_initedFlag) 
		{
    		_log.debug("Already inited.");
    		throw new IMDBException("Already inited.");
    	}
		
		super.init(connPool, props, logMgr);

		_log.debug("Initing...");
		
		String[][] sqlStrs;
        if (getDbType().equals("MSSQL")) 
        {
            sqlStrs = CursorDB_MSSQL.sqlStrs;
        } 
        else if (getDbType().equals("Oracle")) 
        {
            sqlStrs = CursorDB_Oracle.sqlStrs;
        }
        else 
        {
            throw new IMDBException("Unsupported database type.");
        }

        if (IMDBApi.canPrepareStatements(sqlStrs)) 
		{
			prepareStatements(sqlStrs);
		}

        if (IMDBApi.canPrepareStatements(CursorDB_Common.sqlStrs)) {
			prepareStatements(CursorDB_Common.sqlStrs);
		}
        
		_initedFlag = true;

		_log.debug("Inited.");
		
	}
	
	public synchronized void reset() 
    {
    	if (!_initedFlag) {
    		return;
    	}

    	_log.debug("Resetting...");

    	// Reset base API.
    	super.reset();

    	_initedFlag	= false;

    	_log.debug("Reset.");
    }
	
	public void insertCursor(int configID, String entityID, long cursorValue,
			int intCompanyID) throws IMDBException{
		if(_log.debug()) {
			_log.debug("Inside insertCursor method.");
		}
		int retryCnt = 0;
		int ID = -1;
		while(true){
			int argNum = 1;
			int nRows =0;
			ResultSet   		rs          = null;
			PreparedStatement	stmt 	= null;
			FTSQLConnection 	dbConn 		= getDbConnection(intCompanyID);
			FTSQLTransaction	trans 		= null;
			try{
				dbConn.setAutoCommit(false);
				trans = new FTSQLTransaction(dbConn, _log);
				trans.startTransaction(Connection.TRANSACTION_READ_COMMITTED);
				stmt = getStmt(dbConn, SQL_CHECK_CURSOR, intCompanyID);
				stmt.setInt(1, configID);
				stmt.setString(2, entityID);
				rs = stmt.executeQuery();
				if (rs.next() == true)
				{
					throw new IMDBRecordAlreadyExistsException("Cursor for"
							+ " entityID: " + entityID + ", configID" +
							configID + " already exists.");
				}
				rs.close();
				rs = null;
				stmt =getStmt(dbConn, SQL_SELECT_MAX_ID, intCompanyID);
				rs = stmt.executeQuery();
				if(rs.next()){
					ID = rs.getInt("maxID") + 1;
				}
				else{
					throw new IMDBException("CursorValue for entityID " +
				entityID + ", configID" +configID+" could not be inserted"
						+ " into the TeamsApiSiteCursors table.");
				}
				
				_log.debug("Inserting cursor value");
				stmt = getStmt(dbConn, SQL_INSERT_CURSOR, intCompanyID);
				stmt.setInt( argNum++,  ID);
				stmt.setInt( argNum++,  configID);
				stmt.setString( argNum++, entityID);
				stmt.setLong(argNum++, cursorValue);
				nRows = stmt.executeUpdate();
				if (nRows == 0)
				{
					throw new IMDBException("CursorValue for entityID " + 
							entityID +  ", configID" +configID+" could not be"
							+ " inserted into the TeamsApiSiteCursors table.");
				}
				//Commit the transaction
				trans.endTransaction(true);
				return;
			}
			catch(Exception e){
				try {
					//Rollback the transaction.
					trans.endTransaction(false);
				} catch (Exception e2) {
					_log.warning("Could not rollback the transaction.", e2);
				}
				retryCnt = handleException(e, "Insert cursor failed. Reason: " 
						+ e.getMessage(), retryCnt, dbConn, intCompanyID);
			}
			finally
			{
				if (rs != null) 
				{
					try 
					{
						rs.close();
					} catch (Exception e) 
					{
						// Ignore.
					}
				}
				try {
    				//reseting back the autocommit
					dbConn.setAutoCommit(true);
				} catch (SQLException e2) {
					_log.warning("Could not set the auto-commit to true.", e2);
				}
				releaseDbConnection(dbConn, intCompanyID);
			}
		}
		
	}
	
	public void updateCursor(int configID, String entityID, long cursorValue,
			int intCompanyID) throws IMDBException{
		if(_log.debug()) {
			_log.debug("Inside updateCursor method.");
		}
		int retryCnt = 0;
		while(true){
			int argNum = 1;
			int nRows =0;
			ResultSet rs = null;
			PreparedStatement stmt = null;
			FTSQLConnection dbConn 	= getDbConnection(intCompanyID);
			FTSQLTransaction	trans 		= null;
			try{
				dbConn.setAutoCommit(false);
				trans = new FTSQLTransaction(dbConn, _log);
				trans.startTransaction(Connection.TRANSACTION_READ_COMMITTED);
				stmt = getStmt(dbConn, SQL_CHECK_CURSOR, intCompanyID);
				stmt.setInt(1, configID);
				stmt.setString(2, entityID);
				rs = stmt.executeQuery();
				if (rs.next() != true)
				{
					throw new IMDBRecordNotFoundException(MSG_CURSOR_NOT_EXIST + entityID);
				}
				rs.close();
				rs = null;
				
				_log.debug("Updating cursor configID "+configID+", entityID "+ entityID);
				stmt = getStmt(dbConn, SQL_UPDATE_CURSOR, intCompanyID);
				stmt.setLong(argNum++, cursorValue);
				stmt.setLong(argNum++, configID);
				stmt.setString( argNum++, entityID);

				nRows = stmt.executeUpdate();
				if (nRows == 0)
				{
					throw new IMDBException("CursorValue for siteURL" + entityID + ","
							+ " configID" +configID+" could not be updated.");
				}
				//Commit the transaction
				trans.endTransaction(true);
				return;
			}
			catch(Exception e){
				try {
					//Rollback the transaction.
					trans.endTransaction(false);
				} catch (Exception e2) {
					_log.warning("Could not rollback the transaction.", e2);
				}
				if(e instanceof IMDBRecordNotFoundException && e.getMessage() != null
						&& e.getMessage().startsWith(MSG_CURSOR_NOT_EXIST)) {
					if(_log.debug())
						_log.debug(dbConn.getConnID() + " " + e.getMessage());
					throw (IMDBRecordNotFoundException) e;
				} else {
					retryCnt = handleException(e, "Update cursor failed. Reason:"
							+  e.getMessage(), retryCnt, dbConn, intCompanyID);
				}
			}
			finally
			{
				if (rs != null) 
				{
					try 
					{
						rs.close();
					} catch (Exception e) 
					{
						// Ignore.
					}
				}
    			try {
    				//reseting back the autocommit
					dbConn.setAutoCommit(true);
				} catch (SQLException e2) {
					_log.warning("Could not set the auto-commit to true.", e2);
				}
				releaseDbConnection(dbConn, intCompanyID);
			}
		}
		
	}
	
	public void resetCursors(int configID, long cursorValue, int intCompanyID) throws IMDBException{
		int retryCnt = 0;
		while(true){
			int argNum = 1;
			ResultSet   		rs          = null;
			PreparedStatement	stmt 	= null;
			FTSQLConnection 	dbConn 		= getDbConnection(intCompanyID);
			try{
				
				_log.debug("Resetting cursor values for configID "+configID);
				stmt = getStmt(dbConn, SQL_RESET_CURSORS, intCompanyID);
				stmt.setLong(argNum++, cursorValue);
				stmt.setLong(argNum++, configID);

				stmt.executeUpdate();

				return;
			}
			catch(Exception e){
				retryCnt = handleException(e, "Resetting cursor failed. Reason:"
						+ e.getMessage(), retryCnt, dbConn, intCompanyID);
			}
			finally
			{
				if (rs != null) 
				{
					try 
					{
						rs.close();
					} catch (Exception e) 
					{
						if(_log.debug()) {
							_log.debug("Failed to close ResultSet");
						}
					}
				}
				releaseDbConnection(dbConn, intCompanyID);
			}
		}
		
	}
	
	public List<Long> getAllCursorValues(int configID, int intCompanyID) throws IMDBException {

		int retryCnt = 0;
		List<Long> cursorValues = new ArrayList<Long>();
		while (true) {

			ResultSet rs = null;
			FTSQLConnection dbConn = getDbConnection(intCompanyID);

			try {
				PreparedStatement stmt = getStmt(dbConn, SQL_SELECT_CURSOR_VALUES, intCompanyID);
				stmt.setInt(1, configID);
				rs = stmt.executeQuery();
				while (rs.next() == true) {
					long cursorValue = rs.getLong("cursorValue");
					cursorValues.add(cursorValue);
				}
				rs.close();
				rs = null;
				return cursorValues;
			} catch (Exception e) {
				retryCnt = handleException(e, "Get cursor values failed. Reason:" + e.getMessage(), retryCnt, dbConn,
						intCompanyID);

			} finally {
				// Clean up.
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception ignore) {
						// Ignore.
					}
				}
				releaseDbConnection(dbConn, intCompanyID);
			}
		}
	}
	
	public long getCursorValue(int configID, String entityID,
			int intCompanyID) throws IMDBException{
		
		int retryCnt = 0;
		
		while (true) {

			ResultSet rs = null;
			FTSQLConnection dbConn = getDbConnection(intCompanyID);

			try {
				PreparedStatement stmt = getStmt(dbConn, 
						SQL_SELECT_CURSOR_VALUE, intCompanyID);
				stmt.setMaxRows(1);
				stmt.setInt(1, configID);
				stmt.setString(2, entityID);
				rs = stmt.executeQuery();
				long cursorValue = -1;
				if (rs.next() == true) {
					cursorValue = rs.getLong("cursorValue");
				}
				rs.close();
				rs = null;
				return cursorValue;
			} catch (Exception e) {

				retryCnt = handleException(e, "Get cursor value failed. Reason:"
						+ e.getMessage(), retryCnt, dbConn, intCompanyID);

			} finally {
				// Clean up.
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception ignore) {
						// Ignore.
					}
				}
				releaseDbConnection(dbConn, intCompanyID);
			}
		}

	}
	
	public List<Long> getCursorValues(int configID, int intCompanyID,
		List<String> entityIDs) throws IMDBException{
		
		List<Long> cursorValues = new ArrayList<Long>();
		for(String entityID: entityIDs){
			long cursorValue = getCursorValue(configID, entityID, intCompanyID);
			cursorValues.add(cursorValue);
		}
		
		return cursorValues;
		
	}
}
