package com.actiance.test.sharepointNew.utils;

import java.text.ParseException;
import java.util.ArrayList;

import com.independentsoft.share.FieldValue;
import com.independentsoft.share.ListItem;
import com.independentsoft.share.SiteCreationInfo;

public class SiteUtils {
	
	SiteCreationInfo siteInfo;
	
	TestUtils utils = new TestUtils();
	
	public SiteCreationInfo getSiteCreationInfoObject(String title, String url, String wikiType, boolean permission){
		siteInfo = new SiteCreationInfo();
		siteInfo.setTitle(title);
		siteInfo.setUrl(url);
		siteInfo.setWebTemplate(wikiType);
		siteInfo.setSamePermissionsAsParentSite(permission);
		return siteInfo;
	}
	
	public SiteCreationInfo getSiteCreationInfoObject(String title, String description, String url, String wikiType, boolean permission){
		siteInfo = getSiteCreationInfoObject(title, url, wikiType, permission);
		siteInfo.setDescription(description);
		return siteInfo;
	}
	
	//Get Field Values for Creating Tasks
	public ArrayList<FieldValue> getFieldValuesForCreatingTask(String title){
		FieldValue field1 = new FieldValue("Title", title);
		//Set Due Date to next day of the current Date
		FieldValue field2 = new FieldValue("DueDate", utils.addDaysInCurrentDate(1));
		//AssignedTo is not being considered so commenting it as of now
		//FieldValue field3 = new FieldValue("AssignedTo", "Prabhakar Members");
		
		ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
		fields.add(field1);
		fields.add(field2);
		return fields;
	}
	
	//Get Field Values for Creating Discussion
	public ArrayList<FieldValue> getFieldValuesForCreatingDiscussionBoard(String subject, String body, String category){
		FieldValue field1 = new FieldValue("Title", subject);
		FieldValue field2 = new FieldValue("FileLeafRef", subject);
		FieldValue field3 = new FieldValue("Body", body);
		
		ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
		fields.add(field1);
		fields.add(field2);
		fields.add(field3);
		return fields;
	}
	
	//Get Field Values for Creating Discussion
	public ArrayList<FieldValue> getFieldValuesForAddingReplyToDiscussion(String body, String parentID) {
		FieldValue field1 = new FieldValue("Body", body);
		FieldValue field2 = new FieldValue("ParentItemID", parentID);
		FieldValue field3 = new FieldValue("ParentFolderId", parentID);
		ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
		fields.add(field1);
		fields.add(field2);
		fields.add(field3);
		return fields;
	}
	
	public ListItem getListItem(ArrayList<ListItem> items, String name){
		for(ListItem item : items){
			if(item.getTitle().equals(name)){
				return item;
			}
		}
		return null;
	}

	public ListItem getListItemById(ArrayList<ListItem> items, int id){
		for(ListItem item : items){
			if(item.getId() == id){
				return item;
			}
		}
		return null;
	}

}