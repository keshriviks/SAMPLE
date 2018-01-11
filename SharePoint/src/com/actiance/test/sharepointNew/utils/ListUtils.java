package com.actiance.test.sharepointNew.utils;

import java.util.ArrayList;

import com.independentsoft.share.FieldValue;
import com.independentsoft.share.List;
import com.independentsoft.share.ListItem;
import com.independentsoft.share.ListTemplateType;

public class ListUtils {
	
	TestUtils utils = new TestUtils();
	
	//Get Field Values for Creating Discussion
	public ArrayList<FieldValue> getFieldValuesForCreatingDiscussionBoardItem(String title) {
		FieldValue field1 = new FieldValue("FileLeafRef", title);
		FieldValue field2 = new FieldValue("Title", title);
		ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
		fields.add(field1);
		fields.add(field2);
		return fields;
	}
	
	//Get Field Values for creating Links Items
	public ArrayList<FieldValue> getFieldValuesForCreatingLinks(String url){
		FieldValue field1 = new FieldValue("URL", url);
		ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
		fields.add(field1);
		return fields;
	}
	
	public ArrayList<FieldValue> getFieldValuesForCreatingLinks(String url, String description){
		FieldValue field1 = new FieldValue("URL", url+","+description);
		ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
		fields.add(field1);
		return fields;
	}
	
	public ArrayList<FieldValue> getFieldValuesForCreatingLinks(String url, String description, String notes){
		ArrayList<FieldValue> fields = getFieldValuesForCreatingLinks(url);
		FieldValue field2 = new FieldValue("Comments", notes);
		FieldValue field3 = new FieldValue("FileLeafRef", description);
		fields.add(field2);
		fields.add(field3);
		return fields;
	}
	
	//Get Field Values for creating Issue Tracking items
	public ArrayList<FieldValue> getFieldValuesForCreatingIssueTrackingItem(String title){
		ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
		FieldValue field1 = new FieldValue("Title", title);
		fields.add(field1);
		return fields;
	}
	
	public ArrayList<FieldValue> getFieldValuesForCreatingIssueTrackingItem(String title, String description, String status, 
			String priority, String comments, String assignedTo){
		ArrayList<FieldValue> fields = getFieldValuesForCreatingIssueTrackingItem(title);
		FieldValue field2 = new FieldValue("Comment", description);
		FieldValue field3 = new FieldValue("Status", status);
		FieldValue field4 = new FieldValue("Priority", priority);
		FieldValue field5 = new FieldValue("V3Comments", comments);
		//FieldValue field6 = new FieldValue("AssignedTo", assignedTo);
		FieldValue field7 = new FieldValue("DueDate", utils.addDaysInCurrentDate(1));
		fields.add(field2);
		fields.add(field3);
		fields.add(field4);
		fields.add(field5);
		//fields.add(field6);
		fields.add(field7);
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
	
	
//  Field Values For Custom Event
    public ArrayList<FieldValue> getFieldValueForEvent(TestDataObject obj){
        ArrayList<FieldValue> getFV = new ArrayList();
		FieldValue fv2 = new FieldValue("Category",obj.getItemDescription());
		FieldValue fv3 = new FieldValue("Comments",obj.getParam3());
		FieldValue fv4 = new FieldValue("Location",obj.getParam4());
		FieldValue fv5 = new FieldValue("StartDate",obj.getParam5());
//		FieldValue fv6 = new FieldValue("fAllDayEvent",obj.getParam4());
//		FieldValue fv7 = new FieldValue("EndDate",obj.getParam5()); // jshare issue not able to set it through an exception
//		FieldValue fv8 = new FieldValue("fRecurrence",obj.getParam6	());
		
		
		getFV.add(fv2);
		getFV.add(fv3);
		getFV.add(fv4);
		getFV.add(fv5);
//		getFV.add(fv6);
//		getFV.add(fv7);
//		getFV.add(fv8);
        return getFV;
    }

	
	// Get Field values for Blog Post
    public ArrayList<FieldValue> getFieldValuesForCreatingBlogPost(String publishingDate){
           ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
           FieldValue field1 = new FieldValue("PublishedDate", publishingDate);
           
           fields.add(field1);
           return fields;
    }
    
    public ArrayList<FieldValue> getFieldValuesForCreatingBlogPost( String publishingDate, String body , String category){
           ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
//           FieldValue field1 = new FieldValue("Category", category);
           FieldValue field2 = new FieldValue("PublishedDate", publishingDate);
           FieldValue field3 = new FieldValue("Body", body);
           
//           fields.add(field1);
           fields.add(field2);
           fields.add(field3);
           return fields;
    }
    
    public ArrayList<FieldValue> getFieldValuesForEditingBlogPost(String title, String publishingDate, String body, String category){
           ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
           FieldValue field1 = new FieldValue("Title", title);
           FieldValue field2 = new FieldValue("PublishedDate", publishingDate);
           FieldValue field3 = new FieldValue("Body", body);
           FieldValue field4 = new FieldValue("Category", category);
           
           fields.add(field1);
           fields.add(field2);
           fields.add(field3);
           fields.add(field4);
           return fields;
    }

    
    public ArrayList<FieldValue> getFieldValuesForEditingBlogComment(String title, String body){
           ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
           FieldValue field1 = new FieldValue("Title", title);
           FieldValue field2 = new FieldValue("body", body);
           
           fields.add(field1);
           fields.add(field2);
           return fields;
    }
    public ArrayList<FieldValue> getFieldValuesForCreatingBlogComment(String title){
           ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
           FieldValue field1 = new FieldValue("Title", title);
           
           fields.add(field1);
           return fields;
    }

//  Annoucement field Value
    
    public ArrayList<FieldValue> getFieldValueForAnno(String body, String expireDate){
		ArrayList<FieldValue> fields = new ArrayList<FieldValue>();
		FieldValue field1 = null;
		FieldValue field2 = null;

		if (!body.equalsIgnoreCase("NA")) {
			field1 = new FieldValue("Body", body);
			fields.add(field1);

		}

		if (!expireDate.equalsIgnoreCase("NA")) {
			field2 = new FieldValue("Expires", expireDate);
			fields.add(field2);
		}

		return fields;
    }
    
    public List getListObject(String title, String description, String template){
    	List list = new List();
    	list.setTitle(title);
    	list.setContentTypesEnabled(true);
        list.setDescription(description);
    	switch (template) {
		case "Tasks":
			list.setBaseTemplate(ListTemplateType.TASKS);
			break;
		case "Announcement":
			list.setBaseTemplate(ListTemplateType.ANNOUNCEMENTS);
			break;
		case "Survey":
			list.setBaseTemplate(ListTemplateType.SURVEY);
			break;
		case "Discussion Board":
			list.setBaseTemplate(ListTemplateType.DISCUSSION_BOARD);
			break;
		case "Issue Tracking":
			list.setBaseTemplate(ListTemplateType.ISSUE_TRACKING);
			break;
			
		case "Contact":
			list.setBaseTemplate(ListTemplateType.CONTACTS);
			break;
			
		case "Calendar":
			list.setBaseTemplate(ListTemplateType.EVENTS);
		default:
			System.out.println("Invalid List Template");
			break;
		}
    	return list;
    }
    
    public ArrayList<FieldValue> getFieldVAluesForCreatingDiscussionBoardItem(String title,
    	String body){
    	ArrayList<FieldValue> fields = getFieldValuesForCreatingDiscussionBoardItem(title);
    	FieldValue field3 = new FieldValue("Body", body);
    	fields.add(field3);
    	return fields;
    }
    		 
    public ArrayList<FieldValue> getFieldVAluesForCreatingDiscussionBoardItem(String title,
       String body, boolean isQuestion){
       ArrayList<FieldValue> fields = getFieldVAluesForCreatingDiscussionBoardItem(title, body);
       FieldValue field4 = new FieldValue("IsQuestion", isQuestion+"");
       fields.add(field4);
       return fields;
    }
    		 
    public ArrayList<FieldValue> getFieldVAluesForCreatingDiscussionBoardItem(String title,
       String body, boolean isQuestion, boolean isAnswered){
       ArrayList<FieldValue> fields = getFieldVAluesForCreatingDiscussionBoardItem(title, body, isQuestion);
       FieldValue field5 = new FieldValue("IsAnswered", isAnswered+"");
       fields.add(field5);
       return fields;
    }

    public ArrayList<FieldValue> getFieldValueForContact(TestDataObject obj){
    	ArrayList<FieldValue> inputFv = new ArrayList();
    	
    	FieldValue fv1 = new FieldValue("FirstName",obj.getItemDescription());
//    	FieldValue fv2 = new FieldValue("FullName","");
    	FieldValue fv3 = new FieldValue("Email",obj.getParam3());
    	FieldValue fv4 = new FieldValue("Company",obj.getParam4());
    	FieldValue fv5 = new FieldValue("JobTitle",obj.getParam5());
//    	FieldValue fv6 = new FieldValue("WorkAddress","");
//    	FieldValue fv7 = new FieldValue("WorkCity","");
//    	FieldValue fv8 = new FieldValue("WorkState","");
//    	FieldValue fv9 = new FieldValue("WorkZip","");
//    	FieldValue fv10 = new FieldValue("Comments","");
//    	FieldValue fv11 = new FieldValue("HomePhone","");
    	FieldValue fv12 = new FieldValue("CellPhone",obj.getParam6());
//    	FieldValue fv13 = new FieldValue("WorkPhone","");
//    	FieldValue fv13 = new FieldValue("WorkCountry","");
//    	FieldValue fv13 = new FieldValue("WorkFax","");
    	
    	inputFv.add(fv1);
    	inputFv.add(fv3);
    	inputFv.add(fv4);
    	inputFv.add(fv5);
    	inputFv.add(fv12);
    	
    	return inputFv;
    }
  
    /*
     * Methods For Custom Content Type 
     * */
    
// 	Field Values For Custom Fixed Value Indicator
	 public ArrayList<FieldValue> getFVForCFixedValueIndicator(TestDataObjectCContent obj){
	   	ArrayList<FieldValue> getFV = new ArrayList();
	   	FieldValue fv1 = new FieldValue("ContentTypeId","0x00A747");//0EADF4194E2E9ED1031B61DA088401009ECCDB3614590447A488FFB2FB070475");
	   	FieldValue fv2 = new FieldValue("KpiDescription",obj.getItemDesc());
	   	FieldValue fv3 = new FieldValue("KpiComments",obj.getParam1());
	   	FieldValue fv4 = new FieldValue("Value",obj.getParam2());
	   	FieldValue fv5 = new FieldValue("Goal",obj.getParam3());
	   	FieldValue fv6 = new FieldValue("Warning",obj.getParam4());
	   	
	   	getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);
		getFV.add(fv4);
	   	getFV.add(fv5);
		getFV.add(fv6);
	 	
	   	return getFV;
	   }
	 
//    Field Values For Custom Category
    public ArrayList<FieldValue> getFVForCCategory(TestDataObjectCContent obj){
        ArrayList<FieldValue> getFV = new ArrayList();
        FieldValue fv1 = new FieldValue("ContentTypeId","0x010019");//72BB2A38F0DB49C3A96CF4FA85529956");
		FieldValue fv2 = new FieldValue("CategoryDescription",obj.getItemDesc());
		
		getFV.add(fv1);
		getFV.add(fv2);
        return getFV;
    }
    
//  Field Values For Custom Community Member
    public ArrayList<FieldValue> getFVForCCommunityMember(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x010027");//FC2137D8DE4B00A40E14346D070D5201");
    	
    	getFV.add(fv1);
    	
    	return getFV;
    }

//  Field Values For Custom Site Membership
    public ArrayList<FieldValue> getFVForCSiteMembership(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x010027FC2137D8DE4B00A40E14346D070D5200A632F2C1E05D7F4B8DC64C33100F8E3D");
    	
    	getFV.add(fv1);
  	
    	return getFV;
    }
    
//  Field Values For Custom Discussion
    public ArrayList<FieldValue> getFVForCDiscussion(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x012002");//00004430BC50FFEF4FA0B4F1D1F919A70C");
    	FieldValue fv2 = new FieldValue("Body", obj.getItemDesc());
    	getFV.add(fv1);
    	getFV.add(fv2);
  	
    	return getFV;
    }
    
//  Field Values For Custom Circulation
    public ArrayList<FieldValue> getFVForCCirculation(TestDataObjectCContent obj){
        ArrayList<FieldValue> getFV = new ArrayList();
        FieldValue fv1 = new FieldValue("ContentTypeId","0x01000F");//72BB2A38F0DB49C3A96CF4FA85529956");
		FieldValue fv2 = new FieldValue("Body",obj.getItemDesc());
		FieldValue fv3 = new FieldValue("V3Comments",obj.getParam1());
		
		getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);
        return getFV;
    }
    
//  Field Values For Custom  Holiday
    public ArrayList<FieldValue> getFVForCHoliday(TestDataObjectCContent obj){
        ArrayList<FieldValue> getFV = new ArrayList();
        FieldValue fv1 = new FieldValue("ContentTypeId","0x01009B");//72BB2A38F0DB49C3A96CF4FA85529956");
		FieldValue fv2 = new FieldValue("V4HolidayDate",obj.getItemDesc());
		FieldValue fv3 = new FieldValue("Category",obj.getParam1());
//		FieldValue fv4 = new FieldValue("IsNonWorkingDay",obj.getParam2()); jshare Issue
		
		getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);
//		getFV.add(fv4);
		
        return getFV;
    }
    
//  Field Values For Custom New Word
    public ArrayList<FieldValue> getFVForCNewWord(TestDataObjectCContent obj){
        ArrayList<FieldValue> getFV = new ArrayList();
        FieldValue fv1 = new FieldValue("ContentTypeId","0x010018");//72BB2A38F0DB49C3A96CF4FA85529956");
		FieldValue fv2 = new FieldValue("IMEDisplay",obj.getItemDesc());
		FieldValue fv3 = new FieldValue("IMEComment1",obj.getParam1());
		FieldValue fv4 = new FieldValue("IMEComment2",obj.getParam2());
		FieldValue fv5 = new FieldValue("IMEComment3",obj.getParam3());
		FieldValue fv6 = new FieldValue("IMEPos",obj.getParam4());
		
		getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);
		getFV.add(fv4);
		getFV.add(fv5);
		getFV.add(fv6);
		
        return getFV;
    }
    
//  Field Values For Custom Official Notice
    public ArrayList<FieldValue> getFVForCOfficialNotice(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x01007CE30DD1206047728BAFD1C39A85012000");
    	FieldValue fv2 = new FieldValue("Body",obj.getItemDesc());
  	
		getFV.add(fv1);
		getFV.add(fv2);
		
    	return getFV;
    }

//  Field Values For Custom Resource
    public ArrayList<FieldValue> getFVForCResource(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x01004C");//9F4486FBF54864A7B0A33D02AD19B100");
    	
    	getFV.add(fv1);
    	
    	return getFV;
    }
    
//  Field Values For Custom Resource Group
    public ArrayList<FieldValue> getFVForCResourceGroup(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x0100CA13F2F8D61541B180952DFB25E3E8E400");
  	
    	getFV.add(fv1);
    	
    	return getFV;
    }
    
//  Field Values For Custom Time Card
    public ArrayList<FieldValue> getFVForCTimeCard(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x0100C3");//0DDA8EDB2E434EA22D793D9EE4205800");
    	FieldValue fv2 = new FieldValue("Date",obj.getParam1());
    	FieldValue fv3 = new FieldValue("DayOfWeek",obj.getParam2());
    	FieldValue fv4 = new FieldValue("Start",obj.getParam3());
    	FieldValue fv5 = new FieldValue("End",obj.getParam4());
    	FieldValue fv6 = new FieldValue("In",obj.getParam5());
    	FieldValue fv7 = new FieldValue("Out",obj.getParam6());
    	FieldValue fv8 = new FieldValue("Break",obj.getParam7());
    	FieldValue fv9 = new FieldValue("ScheduledWork",obj.getParam8());
    	FieldValue fv10 = new FieldValue("Overtime",obj.getParam9());
    	FieldValue fv11 = new FieldValue("NightWork",obj.getParam10());
    	FieldValue fv12 = new FieldValue("HolidayNightWork",obj.getParam11());
    	FieldValue fv13 = new FieldValue("Late",obj.getParam12());
    	FieldValue fv14 = new FieldValue("LeaveEarly",obj.getParam13());
    	FieldValue fv15 = new FieldValue("Vacation",obj.getParam14());
    	FieldValue fv16 = new FieldValue("NumberOfVacation",obj.getParam15());
    	
		getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);
		getFV.add(fv4);
		getFV.add(fv5);
		getFV.add(fv6);
		getFV.add(fv7);
		getFV.add(fv8);
		getFV.add(fv9);
		getFV.add(fv10);
		getFV.add(fv11);
		getFV.add(fv12);
		getFV.add(fv13);
		getFV.add(fv14);
		getFV.add(fv15);
		getFV.add(fv16);
    	
    	return getFV;
    }
    
//  Field Values For Custom User
    public ArrayList<FieldValue> getFVForCUser(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x0100FB");//EEE6F0C500489B99CDA6BB16C398F700");
  	
    	getFV.add(fv1);
    	
    	return getFV;
    }
    
//  Field Values For Custom Whats New Notification
    public ArrayList<FieldValue> getFVForCWhatsNewNotification(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x0100A2");//CA87FF01B442AD93F37CD7DD0943EB00");
    	FieldValue fv2 = new FieldValue("AssignTo",obj.getItemDesc());
    	getFV.add(fv1);
    	getFV.add(fv2);
  	
    	return getFV;
    }
    
//  Field Values For Custom Announcement
    public ArrayList<FieldValue> getFVForCAnnouncement(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x010400");
    	FieldValue fv2 = new FieldValue("Body",obj.getItemDesc());
    	FieldValue fv3 = new FieldValue("Expires", obj.getParam1());
    	
    	getFV.add(fv1);
    	getFV.add(fv2);
    	getFV.add(fv3);
  	
    	return getFV;
    }
    
//  Field Values For Custom Comment
    public ArrayList<FieldValue> getFVForCComment(TestDataObjectCContent obj){
        ArrayList<FieldValue> getFV = new ArrayList();
        FieldValue fv1 = new FieldValue("ContentTypeId","0x011100");//72BB2A38F0DB49C3A96CF4FA85529956");
		FieldValue fv2 = new FieldValue("Body",obj.getItemDesc());
		
		getFV.add(fv1);
		getFV.add(fv2);
        return getFV;
    }

//  Field Values For Custom Contact
    public ArrayList<FieldValue> getFVForCContact(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv0 = new FieldValue("ContentTypeId","0x010600175E2159688B5E40B4959824B08955D4");
    	
    	FieldValue fv = new FieldValue("FirstName",obj.getItemDesc());
 		FieldValue fv1 = new FieldValue("FullName",obj.getItemDesc()+obj.getItemTitle());
 		FieldValue fv2 = new FieldValue("EMail",obj.getParam1());
 		FieldValue fv3 = new FieldValue("Company",obj.getParam2());
 		FieldValue fv4 = new FieldValue("JobTitle",obj.getParam3());
 		FieldValue fv5 = new FieldValue("WorkAddress",obj.getParam4());
 		FieldValue fv6 = new FieldValue("WorkCity",obj.getParam5());
 		FieldValue fv7 = new FieldValue("WorkState",obj.getParam6());
 		FieldValue fv8 = new FieldValue("WorkZip",obj.getParam7());
// 		FieldValue fv9 = new FieldValue("WebPage","https://actianceengg.sharepoint.com/AutomationLib1 , https://actianceengg.sharepoint.com/AutomationLib1");
 		FieldValue fv10 = new FieldValue("Comments",obj.getParam8());
 		FieldValue fv11 = new FieldValue("HomePhone",obj.getParam9());
 		FieldValue fv12 = new FieldValue("CellPhone",obj.getParam10());
 		FieldValue fv13 = new FieldValue("WorkPhone",obj.getParam11());
 		FieldValue fv14 = new FieldValue("WorkCountry",obj.getParam12());
 		FieldValue fv15 = new FieldValue("WorkFax",obj.getParam13());
 		
 		getFV.add(fv0);
 		getFV.add(fv);
 		getFV.add(fv1);
 		getFV.add(fv2);
 		getFV.add(fv3);
 		getFV.add(fv4);
 		getFV.add(fv5);
 		getFV.add(fv6);
 		getFV.add(fv7);
 		getFV.add(fv8);
// 		getFV.add(fv9);
 		getFV.add(fv10);
 		getFV.add(fv11);
 		getFV.add(fv12);
 		getFV.add(fv13);
 		getFV.add(fv14);
 		getFV.add(fv15);
  	
    	return getFV;
    }
    
//  Field Values For Custom East Asia Contact
    public ArrayList<FieldValue> getFVForCEastAsiaContact(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv0 = new FieldValue("ContentTypeId","0x011600994C9D50F862F243BCF19C221E70104F");
  	
    	FieldValue fv = new FieldValue("FirstName",obj.getItemDesc());
 		FieldValue fv1 = new FieldValue("FullName",obj.getItemDesc()+obj.getItemTitle());
 		FieldValue fv2 = new FieldValue("EMail",obj.getParam1());
 		FieldValue fv3 = new FieldValue("Company",obj.getParam2());
 		FieldValue fv4 = new FieldValue("JobTitle",obj.getParam3());
 		FieldValue fv5 = new FieldValue("WorkAddress",obj.getParam4());
 		FieldValue fv6 = new FieldValue("WorkCity",obj.getParam5());
 		FieldValue fv7 = new FieldValue("WorkState",obj.getParam6());
 		FieldValue fv8 = new FieldValue("WorkZip",obj.getParam7());
// 		FieldValue fv9 = new FieldValue("WebPage","https://actianceengg.sharepoint.com/AutomationLib1 , https://actianceengg.sharepoint.com/AutomationLib1");
 		FieldValue fv10 = new FieldValue("Comments",obj.getParam8());
 		FieldValue fv11 = new FieldValue("HomePhone",obj.getParam9());
 		FieldValue fv12 = new FieldValue("CellPhone",obj.getParam10());
 		FieldValue fv13 = new FieldValue("WorkPhone",obj.getParam11());
 		FieldValue fv14 = new FieldValue("WorkCountry",obj.getParam12());
 		FieldValue fv15 = new FieldValue("WorkFax",obj.getParam13());
 		
 		getFV.add(fv0);
 		getFV.add(fv);
 		getFV.add(fv1);
 		getFV.add(fv2);
 		getFV.add(fv3);
 		getFV.add(fv4);
 		getFV.add(fv5);
 		getFV.add(fv6);
 		getFV.add(fv7);
 		getFV.add(fv8);
// 		getFV.add(fv9);
 		getFV.add(fv10);
 		getFV.add(fv11);
 		getFV.add(fv12);
 		getFV.add(fv13);
 		getFV.add(fv14);
 		getFV.add(fv15);
    	
    	return getFV;
    }
    
//  Field Values For Custom Event
    public ArrayList<FieldValue> getFVForCEvent(TestDataObjectCContent obj){
        ArrayList<FieldValue> getFV = new ArrayList();
        FieldValue fv1 = new FieldValue("ContentTypeId","0x010200");//72BB2A38F0DB49C3A96CF4FA85529956");
		FieldValue fv2 = new FieldValue("Category",obj.getItemDesc());
		FieldValue fv3 = new FieldValue("Comments",obj.getParam1());
		FieldValue fv4 = new FieldValue("Location",obj.getParam2());
		FieldValue fv5 = new FieldValue("StartDate",obj.getParam3());
//		FieldValue fv6 = new FieldValue("fAllDayEvent",obj.getParam4());
//		FieldValue fv7 = new FieldValue("EndDate",obj.getParam5()); // jshare issue not able to set it through an exception
//		FieldValue fv8 = new FieldValue("fRecurrence",obj.getParam6	());
		
		
		getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);
		getFV.add(fv4);
		getFV.add(fv5);
//		getFV.add(fv6);
//		getFV.add(fv7);
//		getFV.add(fv8);
        return getFV;
    }
//  Field Values For Custom Issue
    public ArrayList<FieldValue> getFVForCIssue(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x010300");//52B775BC4793C64F9025745BEC50FB38");
		FieldValue fv2 = new FieldValue("Comment", obj.getItemDesc());
//		FieldValue fv3 = new FieldValue("Status", obj.getParam1()); // jshare Issue for C Content Type For Issue Status and Du
		FieldValue fv4 = new FieldValue("Priority",obj.getParam2());
		FieldValue fv5 = new FieldValue("V3Comments", obj.getParam3());
		//FieldValue fv6 = new FieldValue("AssignedTo", assignedTo);
		FieldValue fv7 = new FieldValue("DueDate", utils.addDaysInCurrentDate(1));
		
		getFV.add(fv1);
		getFV.add(fv2);
//		getFV.add(fv3);
		getFV.add(fv4);
		getFV.add(fv5);
//		getFV.add(fv6);
//		getFV.add(fv7);
		
    	return getFV;
    }

//  Field Values For Custom Item
    public ArrayList<FieldValue> getFVForCItem(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x010022A0B8F8F0648942A52E3C8D521201CD");
    	
    	getFV.add(fv1);
    	return getFV;
    }
    
//  Field Values For Custom Link
    public ArrayList<FieldValue> getFVForCLink(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x010500");//D56581A42BA5A8428ECBBD8D3C0C19CA");
    	FieldValue fv2 = new FieldValue("Comments", obj.getItemDesc());
		FieldValue fv3 = new FieldValue("URL", obj.getItemTitle());
		
		getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);
		
    	return getFV;
    }
    
//  Field Values For Custom Message
    public ArrayList<FieldValue> getFVForCMessage(TestDataObjectCContent obj){
        ArrayList<FieldValue> getFV = new ArrayList();
        FieldValue fv1 = new FieldValue("ContentTypeId","0x010700");//72BB2A38F0DB49C3A96CF4FA85529956");
		FieldValue fv2 = new FieldValue("Body",obj.getItemDesc());
		
		getFV.add(fv1);
		getFV.add(fv2);
		
        return getFV;
    }
    
//  Field Values For Custom Post
    public ArrayList<FieldValue> getFVForCPost(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x011000");//025EC52103899F408841E05374D565C3");
    	FieldValue fv2 = new FieldValue("Body",obj.getItemDesc());
    	FieldValue fv3 = new FieldValue("PublishedDate",obj.getParam1());
    	
		getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);

		return getFV;
    }
    
    
//  Field Values For Summary Task
    public ArrayList<FieldValue> getFVForCSummaryTask(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x0120040047DF024F4AB1644581B239DDAE27868C");
    	FieldValue fv2 = new FieldValue("Body",obj.getItemDesc());
    	
		getFV.add(fv1);
		getFV.add(fv2);

		return getFV;
    }
    
    
//  Field Values For Custom Schedule
    public ArrayList<FieldValue> getFVForCSchedule(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x0102007DBDC1392EAF4EBBBF99E41D8922B264");
    	FieldValue fv2 = new FieldValue("Location",obj.getItemDesc());
    	FieldValue fv3 = new FieldValue("StartDate",obj.getParam1());
//    	FieldValue fv4 = new FieldValue("EndDate",obj.getParam2());
//    	FieldValue fv5 = new FieldValue("fAllDayEvent",obj.getParam3());
//    	FieldValue fv6 = new FieldValue("fAllDayEvent",obj.getParam4());
    	FieldValue fv7 = new FieldValue("Category",obj.getParam5());
    	
    	getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);
//		getFV.add(fv4);
//    	getFV.add(fv5);
//		getFV.add(fv6);
		getFV.add(fv7);
  	
    	return getFV;
    }
    
//  Field Values For Custom Schedule And Reservation
    public ArrayList<FieldValue> getFVForCScheduleAndReservation(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x01020072BB2A38F0DB49C3A96CF4FA85529956");
    	FieldValue fv2 = new FieldValue("Location",obj.getItemDesc());
    	FieldValue fv3 = new FieldValue("StartDate",obj.getParam1());
//    	FieldValue fv4 = new FieldValue("EndDate",obj.getParam2()); // JShare Issue
//    	FieldValue fv5 = new FieldValue("fAllDayEvent",obj.getParam3());
    	FieldValue fv7 = new FieldValue("Category",obj.getParam5());
    	
    	getFV.add(fv1);
		getFV.add(fv2);
		getFV.add(fv3);
//		getFV.add(fv4);
//    	getFV.add(fv5);
		getFV.add(fv7);
		
    	return getFV;
    }
    
//  Field Values For Custom Task
    public ArrayList<FieldValue> getFVForCTask(TestDataObjectCContent obj){
    	ArrayList<FieldValue> getFV = new ArrayList();
    	FieldValue fv1 = new FieldValue("ContentTypeId","0x0108000C5A30A67EB6544FB142623EB12EBA87");
//    	FieldValue fv3 = new FieldValue("DueDate", utils.addDaysInCurrentDate(1)); // jshare iSsue
    	
    	getFV.add(fv1);
//    	getFV.add(fv3);
    	
    	return getFV;
    }
}
