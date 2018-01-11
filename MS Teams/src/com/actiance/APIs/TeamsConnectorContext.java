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



import java.util.List;

public class TeamsConnectorContext {
	private String baseUrl;
	private String eventUrl;
	private String graphEndPoint;
	private String authEndPoint;
	private String ewsEndPoint;
	private String clientId;
	private String clientSecret;
	private List<String> domains;
	private String serviceNum;
	private int configId;
	private int companyId;
	private int networkId;
	private String exchangeUsername;
	private String exchangePassword;
	private boolean proxyEnabled;
	private String proxyHost;
	private int proxyPort;
	private String proxyDomain;
	private String proxyUsername;
	private String proxyPassword; 
	private boolean isImporterRunSuccess;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getEventUrl() {
		return eventUrl;
	}

	public void setEventUrl(String eventUrl) {
		this.eventUrl = eventUrl;
	}

	public String getGraphEndPoint() {
		return graphEndPoint;
	}

	public void setGraphEndPoint(String graphEndPoint) {
		this.graphEndPoint = graphEndPoint;
	}

	public String getAuthEndPoint() {
		return authEndPoint;
	}

	public void setAuthEndPoint(String authEndPoint) {
		this.authEndPoint = authEndPoint;
	}

	public String getEwsEndPoint() {
		return ewsEndPoint;
	}

	public void setEwsEndPoint(String ewsEndPoint) {
		this.ewsEndPoint = ewsEndPoint;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getServiceNum() {
		return serviceNum;
	}

	public void setServiceNum(String serviceNum) {
		this.serviceNum = serviceNum;
	}

	public int getConfigId() {
		return configId;
	}

	public void setConfigId(int configId) {
		this.configId = configId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public int getNetworkId() {
		return networkId;
	}

	public String getExchangeUsername() {
		return exchangeUsername;
	}

	public void setExchangeUsername(String exchangeUsername) {
		this.exchangeUsername = exchangeUsername;
	}

	public String getExchangePassword() {
		return exchangePassword;
	}

	public void setExchangePassword(String exchangePassword) {
		this.exchangePassword = exchangePassword;
	}

	public boolean isImporterRunSuccess() {
		return isImporterRunSuccess;
	}

	public void setImporterRunSuccess(boolean isImporterRunSuccess) {
		this.isImporterRunSuccess = isImporterRunSuccess;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}
	
	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	public void setProxyEnabled(boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getProxyDomain() {
		return proxyDomain;
	}

	public void setProxyDomain(String proxyDomain) {
		this.proxyDomain = proxyDomain;
	}

	@Override
	public String toString() {
		return "TeamsConnectorContext [baseUrl=" + baseUrl + ", eventUrl=" + eventUrl + ", graphEndPoint="
				+ graphEndPoint + ", authEndPoint=" + authEndPoint + ", ewsEndPoint=" + ewsEndPoint + ", clientId="
				+ clientId + ", clientSecret=" + clientSecret + ", domains="
				+ domains + ", serviceNum=" + serviceNum + ", configId=" + configId + ", companyId=" + companyId
				+ ", networkId=" + networkId + ", exchangeUsername=" + exchangeUsername + ", exchangePassword="
				+ exchangePassword + ", proxyEnabled=" + proxyEnabled + ", proxyHost=" + proxyHost + ", proxyPort="
				+ proxyPort + ", proxyDomain=" + proxyDomain + ", proxyUsername=" + proxyUsername + ", proxyPassword="
				+ proxyPassword + ", isImporterRunSuccess=" + isImporterRunSuccess + "]";
	}

	public List<String> getDomains() {
		return domains;
	}

	public void setDomains(List<String> domains) {
		this.domains = domains;
	}

}