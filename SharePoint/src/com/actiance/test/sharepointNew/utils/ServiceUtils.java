package com.actiance.test.sharepointNew.utils;

import com.independentsoft.share.Service;

public class ServiceUtils {
	
	Service service;
	
	
	public Service getServiceObject(String tenant, String user, String password){
		return new Service(tenant, user, password);
	}
}
