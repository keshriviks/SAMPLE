package com.actiance.test.sharepointNewApproach.tests;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.actiance.test.sharepointNewApproachCustomField.utils.DatabaseUtils;

public class LocalTest {
	static ResultSet rs;
	static Statement stmt;
	public static void main(String[] args) throws ClassNotFoundException, SQLException, XPathExpressionException, IOException {
		stmt = getStatement();
		System.out.println(getAttributeValue(stmt, "Content Type"));
		
	}
	
	

	public static String getAttributeValue(Statement stmt, String attrName) throws XPathExpressionException, SQLException, IOException{
		String attrValue = null;
		System.out.println("Attr name "+attrName);
		SQLXML interactionAttr = getAttributes(stmt, "Messages", 12936);
		XPath xpath = XPathFactory.newInstance().newXPath();
		DOMSource domSource = interactionAttr.getSource(DOMSource.class);
        Document document = (Document) domSource.getNode();
        String expression = "//name[.='"+attrName+"']/following-sibling::*[1][name()='value']";
        attrValue = (xpath.evaluate(expression, document));
        System.out.println("Value From SQl things : "+ attrValue);
        return attrValue;
	}
	public static SQLXML getAttributes(Statement stmt, String tableName , int interId) throws IOException {
		SQLXML interAttributes = null;
		String query = "select attributes from " +tableName+ " where interId ="	+ interId;
		try {
			System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.next();
			interAttributes = rs.getSQLXML("attributes");
			System.out.println(interAttributes.toString());
		} catch (SQLException se) {
			System.out.println("Unable to fetch attributes");
		}
		return interAttributes;
	}
	
	
	public static Statement getStatement() throws ClassNotFoundException, SQLException{
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection con = DriverManager.getConnection("jdbc:sqlserver://192.168.114.214:1433;database=SPCustomTest", "sa", "FaceTime@123");
		stmt = con.createStatement();
		con.setAutoCommit(false);
		return stmt;
	}
}
