package com.actiance.test.sharepointNew.utils;

import com.independentsoft.share.Service;
import com.independentsoft.share.ServiceException;

public class LibrariesUtils {
	
	public Service createService(String url,String userName, String password)
	{
		System.out.println(url);
		System.out.println(userName);
		System.out.println(password);
		return (new Service(url,userName, password));
		
	//	return service;
	}

}
