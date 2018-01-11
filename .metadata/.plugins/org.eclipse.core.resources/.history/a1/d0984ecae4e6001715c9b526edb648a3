package com.actiance.test.sharepointNewApproachCustomField.utils;

public class VerifyFields {
	
	TestUtils utils = new TestUtils();
	String titleFromDB;
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
	
	public boolean verifyTitle(String attributes, String title){
		if(title != null && !title.equals("")){
			titleFromDB = utils.extractString(attributes, "Title");
			titleFromDB = titleFromDB.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
			System.out.println("Out : "+ titleFromDB);
			if(title.equals(titleFromDB)){
				System.out.println("Title Matches with the Database Value");
			}else{
				System.out.println("Title did not match with the Database Value");
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
			bodyFromDB = utils.extractString(attributes, "Description");
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
			descriptionFromDB = utils.extractString(attributes, "Description");
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
			descriptionFromDB = utils.extractString(attributes, "Comments");
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
			fileNameFromDB =  utils.extractString(attributes, "Name");
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
			if(publishedDateFromDB.contains(publishedDate)){
				System.out.println("Published Matches with the Database Value");
			}else{
				System.out.println("Published did not match with the Database Value");
				return false;
			}
		}
		return true;
	}
}
