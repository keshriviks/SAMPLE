package com.actiance.test.sharepointNewApproach.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.Arrays;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.facetime.ftcore.util.Base64;

public class VerifyFields {
	
	ResultSet rs = null;
	TestUtilsSubSiteLevelLists utils = new TestUtilsSubSiteLevelLists();
	String titleFromDB;
	String subjectFromDB;
	String nameFromDB;
	String bodyFromDB;
	String descriptionFromDB;
	String datePictureTakenFromDB;
	String keywordFromDB;
	String fileNameFromDB;
	String pageFileNameFromDB;
	String commentsFromDB;
	String authorFromDB;
	String previewImageURLFromDB;
	String copyrightFromDB;
	String expiresFromDB;
	String dueDateFromDB;
	String notesFromDB;
	String URLFromDB;
	String publishedDateFromDB;
	String fileLeafRefFromDB;
	String originalContentFromDB;
	String categoryFromDB;
	String assignedToFromDB;
	String priorityFromDB;
	String taskStatusFromDB;
	String percentageCompletedFromDB;
	String predecessorsFromDB;
	String isQuestionFromDB;
	String replyFromDB;
	String categoryDescriptionFromDB;
	String categoryImage;
	String issueStatusFromDB;
	String categoryTitleFromDB;
	String commentAsV3CommentFromDB;
	String taskDueDateFromDB;
	String linkURLFromDB;
	String commentAsNote;
	String startDateFromDB;
	
	
	
	public boolean verifyCommentAsTitle(String attributes, String comment){
		
		if(comment != null && !comment.equals("")){
			commentsFromDB = utils.extractString(attributes, "Title");
			commentsFromDB = commentsFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			if(comment.equals(commentsFromDB)){
				System.out.println("Comments Matches with the Database Value");
			}else{
				System.out.println("Comments did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyReplyAsOriginalContent(String attributes, String reply){
		if(reply != null && !reply.equals("")){
			replyFromDB = utils.extractString(attributes, "Original Content");
			if(reply.equals(replyFromDB)){
				System.out.println("Reply Matches with the Database Value");
			}else{
				System.out.println("Reply did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyIsQuestion(String attributes, String isQuestion){
		if(isQuestion != null && !isQuestion.equals("")){
			isQuestionFromDB = utils.extractString(attributes, "Question");
			if(isQuestion.equals(isQuestionFromDB)){
				System.out.println("Question Matches with the Database Value");
			}else{
				System.out.println("Question did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyPredecessors(String attributes, String predecessors){
		if(predecessors != null && !predecessors.equals("")){
			predecessorsFromDB = utils.extractString(attributes, "Predecessors");
			if(predecessors.equals(predecessorsFromDB)){
				System.out.println("Predecessors Matches with the Database Value");
			}else{
				System.out.println("Predecessors did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyPercentageCompleted(String attributes, String percentage){
		if(percentage != null && !percentage.equals("")){
			percentageCompletedFromDB = utils.extractString(attributes, "Percentage Completed");
			if(percentage.equals(percentageCompletedFromDB)){
				System.out.println("Percentage Completed Matches with the Database Value");
			}else{
				System.out.println("Percentage Completed did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyTaskStatus(String attributes, String status){
		if(status != null && !status.equals("")){
			taskStatusFromDB = utils.extractString(attributes, "Task Status");
			if(status.equals(taskStatusFromDB)){
				System.out.println("Task Status Matches with the Database Value");
			}else{
				System.out.println("Task Status did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyPriority(String attributes, String priority){
		if(priority != null && !priority.equals("")){
			priorityFromDB = utils.extractString(attributes, "Priority");
			if(priority.equals(priorityFromDB)){
				System.out.println("Priority Matches with the Database Value");
			}else{
				System.out.println("Priority did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyAssignedTo(String attributes, String assignedTo){
		if(assignedTo != null && !assignedTo.equals("")){
			System.out.println(assignedTo);
			assignedToFromDB = utils.extractString(attributes, "Assigned To");
			System.out.println(assignedToFromDB);
			if(assignedTo.equals(assignedToFromDB)){
				System.out.println("Assigned To Matches with the Database Value");
			}else{
				System.out.println("Assigned To did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyCategory(String attributes, String category){
		if(category != null && !category.equals("")){
			categoryFromDB = utils.extractString(attributes, "Category");
			if(category.equals(categoryFromDB)){
				System.out.println("Category Matches with the Database Value");
			}else{
				System.out.println("Category did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyCategoryAsWikiCategory(String attributes, String category){
		if(category != null && !category.equals("")){
			categoryFromDB = utils.extractString(attributes, "Wiki Categories");
			//System.out.println("InCat: "+category);
			//System.out.println("OuCat: "+categoryFromDB);
			if(category.equals(categoryFromDB)){
				System.out.println("Wiki Categories Matches with the Database Value");
			}else{
				System.out.println("Wiki Categories did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyText(String inputText, String outputText){
		if(inputText != null && !inputText.equals("")){
			System.out.println("Input Text: "+inputText);
			System.out.println("Output Text: "+outputText);
			if(outputText != null){
				if(isEqual(inputText, outputText)){
					System.out.println("Text Matches with the Database Value");
				}else{
					System.out.println("Text did not Match with the Database Value");
					return false;
				}
			}else{
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyTitleAsOriginalContent(String attributes, String title){
		if(title != null && !title.equals("")){
			originalContentFromDB = utils.extractString(attributes, "Original Content");
			originalContentFromDB = originalContentFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			System.out.println("In: "+title);
			System.out.println("Ou: "+originalContentFromDB);
			if(title.equals(originalContentFromDB)){
				System.out.println("Title Matches with the Database Value");
			}else{
				System.out.println("Title did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyFileLeafRef(String attributes, String file){
		if(file != null && !file.equals("")){
			fileLeafRefFromDB = utils.extractString(attributes, "FileLeafRef");
			if(file.equals(fileLeafRefFromDB)){
				System.out.println("FileLeafRef Matches with the Database Value");
			}else{
				System.out.println("FileLeafRef did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyTitle(String attributes, String title){
		if(title != null && !title.equals("")){
			titleFromDB = utils.extractString(attributes, "Title");
			titleFromDB = titleFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//			System.out.println("In: "+title);
//			System.out.println("Ou: "+titleFromDB);
			if(isEqual(title,titleFromDB)){
				System.out.println("Title Matches with the Database Value");
			}else{
				System.out.println("Title did not match with the Database Value");
				return false;
			}
		}
		return true;		
	}
	
	public boolean verifySubjectAsTitle(String attributes, String subject){
		if(subject != null && !subject.equals("")){
			subjectFromDB = utils.extractString(attributes, "Title");
			subjectFromDB = subjectFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			System.out.println("In: "+subject);
			System.out.println("Ou: "+subjectFromDB);
			if(subject.equals(subjectFromDB)){
				System.out.println("Subject Matches with the Database Value");
			}else{
				System.out.println("Subject did not match with the Database Value");
				return false;
			}
		}
		return true;		
	}
	
	public boolean verifyName(String attributes, String name){
		if(name != null && !name.equals("")){
			nameFromDB = utils.extractString(attributes, "Title");
			nameFromDB = nameFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			if(name.equals(nameFromDB)){
				System.out.println("Name Matches with the Database Value");
			}else{
				System.out.println("Name did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyBody(String attributes, String body){
		if(body != null && !body.equals("")){
			bodyFromDB = utils.extractString(attributes, "Description2");
			bodyFromDB = bodyFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			if(bodyFromDB.contains(body)){
				System.out.println("Body Matches with the Database Value");
			}else{
				System.out.println("Body did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyDescription(String attributes, String description){
		if(description != null && !description.equals("")){
			descriptionFromDB = utils.extractString(attributes, "Description2");
			descriptionFromDB = descriptionFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			if(descriptionFromDB.contains(description)){
				System.out.println("Description Matches with the Database Value");
			}else{
				System.out.println("Description did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyDescriptionAsComments(String attributes, String description){
		if(description != null && !description.equals("")){
			descriptionFromDB = utils.extractString(attributes, "Description2");
			System.out.println(descriptionFromDB);
			System.out.println(description);
			if(description.equals(descriptionFromDB)){
				System.out.println("Description Matches with the Database Value");
			}else{
				System.out.println("Description did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyDescription2ForIssue(String attributes, String description){
		if(description != null && !description.equals("")){
			descriptionFromDB = utils.extractString(attributes, "Description2");
			System.out.println(descriptionFromDB);
			System.out.println(description);
			if(description.equals(descriptionFromDB)){
				System.out.println("Description Matches with the Database Value");
			}else{
				System.out.println("Description did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	
	
	public boolean verifyDatePictureTaken(String attributes, String datePictureTaken){
		if(datePictureTaken != null && !datePictureTaken.equals("")){
			datePictureTakenFromDB = utils.extractString(attributes, "Date Of Picture Taken");
			if(datePictureTakenFromDB.contains(datePictureTaken)){
				System.out.println("Date Of Picture Taken Matches with the Database Value");
			}else{
				System.out.println("Date Of Picture Taken did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyKeyword(String attributes, String keyword){
		if(keyword != null && !keyword.equals("")){
			keywordFromDB =  utils.extractString(attributes, "Keywords");
			if(keyword.equals(keywordFromDB)){
				System.out.println("Keywords Matches with the Database Value");
			}else{
				System.out.println("Keywords did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyFileName(String attributes, String fileName){
		if(fileName != null && !fileName.equals("")){
			fileNameFromDB =  utils.extractString(attributes, "FileName");
			System.out.println(fileName+" "+fileNameFromDB);
			if(fileName.equals(fileNameFromDB)){
				System.out.println("FileName Matches with the Database Value");
			}else{
				System.out.println("FileName did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyFileNameForPages(String attributes, String pagefileName){
		if(pagefileName != null && !pagefileName.equals("")){
			fileNameFromDB =  utils.extractString(attributes, "FileLeafRef");
			if(pagefileName.equals(fileNameFromDB)){
				System.out.println("FileName Matches with the Database Value");
			}else{
				System.out.println("FileName did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyCommens(String attributes, String comments){
		if(comments != null && !comments.equals("")){
			commentsFromDB = utils.extractString(attributes, "Description");
			if(comments.equals(commentsFromDB)){
				System.out.println("Comments Matches with the Database Value");
			}else{
				System.out.println("Comments did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyAuthor(String attributes, String author){
		if(author != null && !author.equals("")){
			authorFromDB = utils.extractString(attributes, "Author");
			//System.out.println(author+" "co);
			if(author.equals(authorFromDB)){
				System.out.println("Author Matches with the Database Value");
			}else{
				System.out.println("Author did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyPreviewImageURL(String attributes, String previewImageURL){
		if(previewImageURL != null && !previewImageURL.equals("")){
			previewImageURLFromDB = utils.extractString(attributes, "Preview Image URL");
			if(previewImageURLFromDB.contains(previewImageURL)){
				System.out.println("Preview Image URL Matches with the Database Value");
			}else{
				System.out.println("Preview Image URL did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyLinkURL(String attributes, String linkURL){
		if(linkURL != null && !linkURL.equals("")){
			linkURLFromDB = utils.extractString(attributes, "Preview Image URL");
			System.out.println("DB : "+ linkURLFromDB);
			System.out.println("In : "+ linkURL);
			if(linkURLFromDB.contains(linkURL)){
				System.out.println("Preview Image URL Matches with the Database Value");
			}else{
				System.out.println("Preview Image URL did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	
	
	public boolean verifyCopyright(String attributes, String copyright){
		if(copyright != null && !copyright.equals("")){
			copyrightFromDB = utils.extractString(attributes, "Copyright");
			if(copyright.equals(copyrightFromDB)){
				System.out.println("Copyright Matches with the Database Value");
			}else{
				System.out.println("Copyright did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyExpires(String attributes, String expires){
		if(expires != null && !expires.equals("")){
			expiresFromDB = utils.extractString(attributes, "Expires");
			if(expires.equals(expiresFromDB)){
				System.out.println("Expires Matches with the Database Value");
			}else{
				System.out.println("Expires did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyDueDate(String attributes, String dueDate){
		if(dueDate != null && !dueDate.equals("")){
			dueDateFromDB = utils.extractString(attributes, "Due Date");
			if(dueDate.equals(dueDateFromDB)){
				System.out.println("Due Date Matches with the Database Value");
			}else{
				System.out.println("Due Date did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyNotes(String attributes, String notes){
		if(notes != null && !notes.equals("")){
			notesFromDB = utils.extractString(attributes, "Notes");
			notesFromDB = notesFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			//System.out.println("In Des: "+description+", out Des: "+descriptionFromDB);
			if((notes).equals(notesFromDB)){
				System.out.println("Notes Matches with the Database Value");
			}else{
				System.out.println("Notes did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyURL(String attributes, String URL){		
		if(URL != null && URL.equals("")){
			URLFromDB = utils.extractString(attributes, "URL");
			if(URL.equals(URLFromDB)){
				System.out.println("URL Matches with the Database Value");
			}else{
				System.out.println("URL did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyPublishedDate(String attributes, String publishedDate){
		if(publishedDate != null && !publishedDate.equals("")){
			publishedDateFromDB = utils.extractString(attributes, "Published");
			System.out.println("DB : "+publishedDateFromDB);
			System.out.println("In : "+ publishedDate);
			if(publishedDateFromDB.contains(publishedDate)){
				System.out.println("Published Matches with the Database Value");
			}else{
				System.out.println("Published did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
	public boolean verifyDescriptionAsCategoryDescription(String attributes, String description){
		if(description != null && !description.equals("")){
			categoryDescriptionFromDB = utils.extractString(attributes, "Description");
			System.out.println(categoryDescriptionFromDB);
			System.out.println(description);
			if(description.equals(categoryDescriptionFromDB)){
				System.out.println("Description Matches with the Database Value");
			}else{
				System.out.println("Description did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyCategoryUrl(String attributes, String url){
		if(url != null && !url.equals("")){
			categoryImage = utils.extractString(attributes, "Category Picture");
			if(categoryImage.contains(url)){
				System.out.println("Category Image URL Matched with the Database Value");
			}else{
				System.out.println("Category Image URL not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	public boolean verifyCategoryTypeOfDescription(String attributes, String url, String typeOfDesc){
		if(typeOfDesc != null && !typeOfDesc.equals("")){
			categoryImage = utils.extractString(attributes, "Category Picture");
			System.out.println("DB :"+categoryImage);
			System.out.println("In :"+url+ ", "+typeOfDesc);
			if(categoryImage.equals(url+ ", "+typeOfDesc)){
				System.out.println("Category Image Desc Matched with the Database Value");
			}else{
				System.out.println("Category Image Desc did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyIssueStatus(String attributes, String status){
		if(status != null && !status.equals("")){
			issueStatusFromDB = utils.extractString(attributes, "IssueStatus");
			if(status.equals(issueStatusFromDB)){
				System.out.println("Issue Status Matches with the Database Value");
			}else{
				System.out.println("Issue Status did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifycategoryTitleForIssue(String attributes, String categoryTitle){
		if(categoryTitle != null && !categoryTitle.equals("")){
			categoryTitleFromDB = utils.extractString(attributes, "RelatedIssues");
			if(categoryTitle.equals(categoryTitleFromDB)){
				System.out.println("Category Title Matches with the Database Value");
			}else{
				System.out.println("Category Title did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyCommentAsV3Comments(String attributes, String comment){
		if(comment != null && !comment.equals("")){
			commentAsV3CommentFromDB = utils.extractString(attributes, "V3Comments");
			System.out.println("DB : "+ commentAsV3CommentFromDB);
			System.out.println("IN : "+ comment);
			if(comment.equals(commentAsV3CommentFromDB)){
				System.out.println("Comment Matches with the Database Value");
			}else{
				System.out.println("Comment did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}

	
	public boolean verifyTaskDueDate(String attributes, String dueDate){
		if(dueDate != null && !dueDate.equals("")){
			taskDueDateFromDB = utils.extractString(attributes, "TaskDueDate");
			if(taskDueDateFromDB.equals(dueDate)){
				System.out.println("Task Due Date Matches with the Database Value");
			}else{
				System.out.println("Task Due Date did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyCommentAsNote(String attributes, String comment){
		if(comment != null && !comment.equals("")){
			commentAsNote = utils.extractString(attributes, "Notes");
//			System.out.println("DB : "+ commentAsNote);
//			System.out.println("In : "+ comment);
			if(comment.equals(commentAsNote)){
				System.out.println("Comment Matches with the Database Value");
			}else{
				System.out.println("Comment did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyStartDate(String attributes, String startDate){
		if(startDate != null && !startDate.equals("")){
			startDateFromDB = utils.extractString(attributes, "Start Date");
			if(startDateFromDB.equals(startDate)){
				System.out.println("Start Date Matches with the Database Value");
			}else{
				System.out.println("Start Date did not Match with the Database Value");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyAttributeValue(String attributes, String findString, String valueOfString){
		String valueFromDB;
//		String nul = null;
		if(valueOfString != null && !valueOfString.equals("")){
			valueFromDB = utils.extractString(attributes, findString );
			valueFromDB = valueFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("  ", " ");
			System.out.println("DB : "+valueFromDB);
			System.out.println("Input : "+valueOfString);
			if(isEqual(valueOfString, valueFromDB)){
				System.out.println(findString + " is matched");
				return true;
			}else{
				System.out.println(findString + " did not matched");
				return false;
			}
		}
		return true;
	}
	
	public boolean verifyAttributeValueWithContainMethod(String attributes, String findString, String valueOfString){
		String valueFromDB;
		if(valueOfString != null && !valueOfString.equals("")){
			valueFromDB = utils.extractString(attributes, findString );
			if(valueFromDB.contains(valueOfString)){
				System.out.println(findString + " is matched");
				return true;
			}else{
				System.out.println(findString + " did not matched");
				return false;
			}
		}
		return true;
	}
	
	public String getAttributeValue(Statement stmt, int interId, String attrName) throws XPathExpressionException, SQLException{
		String attrValue = null;
		System.out.println("Attr name "+attrName);
		SQLXML interactionAttr = getAttributes(stmt, "Interactions", interId);
		XPath xpath = XPathFactory.newInstance().newXPath();
		DOMSource domSource = interactionAttr.getSource(DOMSource.class);
        Document document = (Document) domSource.getNode();
        String expression = "//name[.='"+attrName+"']/following-sibling::*[1][name()='value']";
        attrValue = (xpath.evaluate(expression, document));
        System.out.println("Value From SQl things : "+ attrValue);
        return attrValue;
	}
	
	
	public SQLXML getAttributes(Statement stmt, String tableName , int interId) {
		SQLXML interAttributes = null;
		String query = "select attributes from " +tableName+ " where interId ="	+ interId;
		try {
			rs = stmt.executeQuery(query);
			rs.next();
			interAttributes = rs.getSQLXML("attributes");
		} catch (SQLException se) {
			System.out.println("Unable to fetch attributes");
		}
		return interAttributes;
	}
	
	
	public boolean isEqual(String body, String textFromDB) {
		String str1 = new String(Base64.decode(body.getBytes()));
		String str2 = new String(Base64.decode(textFromDB.getBytes()));
		return str1.equals(str2);
	}
	
}
