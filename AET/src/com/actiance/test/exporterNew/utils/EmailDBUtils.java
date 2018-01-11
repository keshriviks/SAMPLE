package com.actiance.test.exporterNew.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtil;

public class EmailDBUtils {
	
//	Generate Database Statement Object
	public Statement getStatement(ArrayList<String> dbParams){
		Statement stmt = null;
//		"jdbc:sqlserver://192.168.116.215;database=Aut_2015R2SP2_14132","sa","FaceTime@123"
		try{
			Class.forName(dbParams.get(0));
			Connection con = DriverManager.getConnection("jdbc:sqlserver://"+dbParams.get(2)+";database="+ dbParams.get(1),dbParams.get(3),dbParams.get(4));
			stmt = con.createStatement();
		}catch(SQLException | ClassNotFoundException ce){
			ce.printStackTrace();
		}
		return stmt;
	}
	
//	Generate Array of byte from Base64 coded Data(XML)
	public byte[] decodeBase64(String base64Data){
		return Base64.decodeBase64(base64Data.getBytes());
	}
	
//	Generate HashMap with file Name and file data in bytes from Database
	public byte[] getFileBytesFromDB(Statement stmt, int interId){
		byte[] dbBytes = null;
//		HashMap<String,byte[]> result =  new HashMap();
		ResultSet rs;
		InputStream is = null;
		String query = "select fileData from filexfers where interId ="+ interId ;
		try{
			rs = stmt.executeQuery(query);
			while(rs.next()){
				dbBytes = new byte[1024*1024*1024];
				is = rs.getBinaryStream("fileData");
				dbBytes = IOUtil.toByteArray(is);
			}
			
		}catch(SQLException | IOException se){
			System.out.println("Not able to fetch File Data");
			se.printStackTrace();
		}
		return dbBytes;
	}

//	Generate file from bytes of Array
	public static File getFile(byte[] bytes, String fileNameWithPath)throws IOException{
		File f = new File(fileNameWithPath);
		FileOutputStream fos = new FileOutputStream(fileNameWithPath);
		fos.write(bytes);
		fos.close();
		return f;
	}
	
//	Generate Checksum from bytes
//	ALGO : MD5(128 bit),SHA-1,SHA-128
	
	public static String getCheckSumBasedOnBytes_ALGO_MD5(byte[] bytes)throws Exception{
		MessageDigest md = MessageDigest.getInstance("MD5");
	    byte[] dataBytes = new byte[1024];

	    int nread = 0;
	    md.update(bytes);
	    byte[] mdbytes = md.digest();

	    //convert the byte to hex format
	    StringBuffer sb = new StringBuffer("");
	    for (int i = 0; i < mdbytes.length; i++) {
	    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
//	    System.out.println("Digest(in hex format):: " + sb.toString());
	    return sb.toString();
	}
	
}
