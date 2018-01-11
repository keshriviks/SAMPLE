package com.actiance.APIs;

/*
 * Copyright (c) 2017 Actiance Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Actiance
 * Inc. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Actiance.
 *
 * ACTIANCE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. ACTIANCE SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED
 * BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE
 * OR ITS DERIVATIVES.
 */



import java.text.SimpleDateFormat;
import java.util.Date;

public class TeamsTimeRange {

	private static final String LOG_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
	
	private long startTime;
	
	private long endTime;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	
	@Override
	public String toString() {
		return "TeamsTimeRange [startTime=" + startTime + ", endTime=" + endTime + "]";
	}

	public String toLogString() {
		SimpleDateFormat sdf = new SimpleDateFormat(LOG_DATE_FORMAT);
		String from = sdf.format(new Date(startTime));
		String to;
		if(endTime > 0) {
			//historical compliance
			to = sdf.format(new Date(endTime));
		} else {
			to = "currentTime";
		}
		return from + " to " + to;
	}

}
