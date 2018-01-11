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



/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsDiagnostics {

	private volatile int received;
	private volatile int audited;
	private volatile int failed;
	private volatile int receivedFromRetryQueue;
	private volatile int auditedFromRetryQueue;
	private volatile int failedFromRetryQueue;
	private volatile long totalExecutionTime;
	private volatile long totalExecutionTimeFromRetryQueue;
	
	private Object lockRecieved = new Object();
	private Object lockAudited = new Object();
	private Object lockFailed = new Object();
	//Lock not required for items from queue because the execution is sequential.
	private Object lockExecutionTime = new Object();

	public int getReceived() {
		return received;
	}

	public void incrementReceived(boolean isRetryItem) {
		if(isRetryItem) {
			this.receivedFromRetryQueue++;
		} else {
			synchronized (lockRecieved) {
				this.received++;
			}
		}
	}

	public int getAudited() {
		return audited;
	}

	public void incrementAudited(boolean isRetryItem) {
		if(isRetryItem) {
			this.auditedFromRetryQueue++;
		} else {
			synchronized (lockAudited) {
				this.audited++;
			}
		}
	}

	public int getFailed() {
		return failed;
	}

	public void incrementFailed(boolean isRetryItem) {
		if(isRetryItem) {
			this.failedFromRetryQueue++;
		} else {
			synchronized (lockFailed) {
				this.failed++;
			}
		}
	}

	public int getReceivedFromRetryQueue() {
		return receivedFromRetryQueue;
	}

	public int getAuditedFromRetryQueue() {
		return auditedFromRetryQueue;
	}

	public int getFailedFromRetryQueue() {
		return failedFromRetryQueue;
	}

	public long getTotalExecutionTime() {
		return totalExecutionTime;
	}
	
	public void addExecutionTime(long executionTime, boolean isRetryItem) {
		if(isRetryItem) {
			totalExecutionTimeFromRetryQueue += executionTime;
		} else {
			synchronized (lockExecutionTime) {
				this.totalExecutionTime += executionTime;
			}
		}
	}

	public long getTotalExecutionTimeFromRetryQueue() {
		return totalExecutionTimeFromRetryQueue;
	}

	@Override
	public String toString() {
		return "TeamsDiagnostics [received=" + received + ", audited=" + audited + ", failed=" + failed
				+ ", receivedFromRetryQueue=" + receivedFromRetryQueue + ", auditedFromRetryQueue="
				+ auditedFromRetryQueue + ", failedFromRetryQueue=" + failedFromRetryQueue + "]";
	}
}
