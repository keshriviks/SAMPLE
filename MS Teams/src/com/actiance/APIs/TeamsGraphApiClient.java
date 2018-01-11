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



import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;

import com.actiance.APIs.TeamsAccessDeniedException;
import com.actiance.APIs.TeamsGraphApiError;
import com.actiance.APIs.TeamsItemNotFoundException;
import com.actiance.APIs.TeamsUnauthorizedException;
import com.actiance.APIs.TeamsAccessToken;
import com.actiance.APIs.TeamsFile;
import com.actiance.APIs.TeamsGroup;
import com.actiance.APIs.TeamsObjectModule;
import com.actiance.APIs.TeamsUser;
import com.actiance.APIs.TeamsConstants;
import com.actiance.APIs.TeamsConnectorContext;
import com.facetime.ftcore.util.Base64;
import com.facetime.ftcore.util.FTBasicLogMgr;
import com.facetime.ftcore.util.FTLogChannel;
import com.facetime.ftcore.util.FTProperties;
import com.facetime.imcoreserver.db.IMConfigDBMgr;

/**
 * 
 * @author VSriramulu
 *
 */
public class TeamsGraphApiClient {

	/**Request payload for url where admin consent is done.**/
	private static final String ACCESS_TOKEN_PAYLOAD_FORMAT = "client_id=#{clientId}"
			+ "&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default" + "&client_secret=#{secretKey}"
			+ "&grant_type=client_credentials";
	
	/** Content type of request payload. **/
	private static final String ACCESS_TOKEN_REQUEST_CONTENT_TYPE = "application/x-www-form-urlencoded";
	
	private String accessToken;

	private ObjectMapper objectMapper;
	
	private PoolingClientConnectionManager connectionMgr;
	private DefaultHttpClient httpclient;
	/**
	 * Thread that prints HTTP connection stats for every 10 secs.
	 * The statistics printed by this thread could be used to detect connection leakages.
	 */
	private TeamsHttpClientConnMonitor connMonitor;
	
	private static final int UNAUTHORIZED = 401;
	private static final int ACCESS_DENIED = 403;
	private static final int RESOURCE_NOT_FOUND = 404;
	private static final int TOO_MANY_REQUESTS = 429;
	private static final String MSG_ACCESS_TOKEN_EXPIRED = "Access token has expired.";
	private static final String HTTP_METHOD_GET = "GET";
	private static final String HTTP_METHOD_POST = "POST";
	private static final int MINUTE_10 = 1000 * 60 * 10;
	
	private int networkTimeoutMillis = 5 * 60 * 1000; // 5 minute
	private int networkFailureWaitTimeMillis = 30 * 1000; // 1/2 minute
	private int networkFailureMaxRetry = 3;
	private int networkMaxConnections = 100;
	private int networkMaxConnectionsPerRoute = 20;
	
	//Fiddler related fields.
	private boolean fiddlerEnabled = false;
	private String fiddlerHost = "localhost";
	private int fiddlerPort = 8888;
	
	private static TeamsGraphApiClient INSTANCE;
	private TeamsAccessToken accessTokenObj;
	private TeamsConnectorContext teamsContext;
	
	private static FTLogChannel log = FTBasicLogMgr.instance().createLogChannel(TeamsGraphApiClient.class, TeamsGraphApiClient.class.getSimpleName());;

	private void configureConstants() {
		StringBuilder sb = new StringBuilder();
		try {
			FTProperties serverProps = IMConfigDBMgr.instance().getFreshConfig();
			if(serverProps.containsKey(TeamsConstants.TEAMS_HTTPCLIENT_TIMEOUT_MILLIS)) {
				networkTimeoutMillis = serverProps.getIntValue(TeamsConstants.TEAMS_HTTPCLIENT_TIMEOUT_MILLIS);
				sb.append(TeamsConstants.TEAMS_HTTPCLIENT_TIMEOUT_MILLIS + " = " + networkTimeoutMillis + ", ");
			}
			if(serverProps.containsKey(TeamsConstants.TEAMS_HTTPCLIENT_RETRY_WAIT_MILLIS)) {
				networkFailureWaitTimeMillis = serverProps.getIntValue(TeamsConstants.TEAMS_HTTPCLIENT_RETRY_WAIT_MILLIS);
				sb.append(TeamsConstants.TEAMS_HTTPCLIENT_RETRY_WAIT_MILLIS + " = " + networkFailureWaitTimeMillis + ", ");
			}
			if(serverProps.containsKey(TeamsConstants.TEAMS_HTTPCLIENT_MAX_RETRY_COUNT)) {
				networkFailureMaxRetry = serverProps.getIntValue(TeamsConstants.TEAMS_HTTPCLIENT_MAX_RETRY_COUNT);
				sb.append(TeamsConstants.TEAMS_HTTPCLIENT_MAX_RETRY_COUNT + " = " + networkFailureMaxRetry + ", ");
			}
			if(serverProps.containsKey(TeamsConstants.TEAMS_HTTPCLIENT_MAX_CONNECTIONS)) {
				networkMaxConnections = serverProps.getIntValue(TeamsConstants.TEAMS_HTTPCLIENT_MAX_CONNECTIONS);
				sb.append(TeamsConstants.TEAMS_HTTPCLIENT_MAX_CONNECTIONS + " = " + networkMaxConnections + ", ");
			}
			if(serverProps.containsKey(TeamsConstants.TEAMS_HTTPCLIENT_MAX_CONNECTIONS_PER_ROUTE)) {
				networkMaxConnectionsPerRoute = serverProps.getIntValue(TeamsConstants.TEAMS_HTTPCLIENT_MAX_CONNECTIONS_PER_ROUTE);
				sb.append(TeamsConstants.TEAMS_HTTPCLIENT_MAX_CONNECTIONS_PER_ROUTE + " = " + networkMaxConnectionsPerRoute + ", ");
			}
			if(serverProps.containsKey(TeamsConstants.TEAMS_FIDDLER_PROXY_ENABLED)) {
				fiddlerEnabled = serverProps.getBooleanValue(TeamsConstants.TEAMS_FIDDLER_PROXY_ENABLED);
				sb.append(TeamsConstants.TEAMS_FIDDLER_PROXY_ENABLED + " = " + fiddlerEnabled + ", ");
			}
			if(serverProps.containsKey(TeamsConstants.TEAMS_FIDDLER_PROXY_HOST)) {
				fiddlerHost = serverProps.getStringValue(TeamsConstants.TEAMS_FIDDLER_PROXY_HOST);
				sb.append(TeamsConstants.TEAMS_FIDDLER_PROXY_HOST + " = " + fiddlerHost + ", ");
			}
			if(serverProps.containsKey(TeamsConstants.TEAMS_FIDDLER_PROXY_PORT)) {
				fiddlerPort = serverProps.getIntValue(TeamsConstants.TEAMS_FIDDLER_PROXY_PORT);
				sb.append(TeamsConstants.TEAMS_FIDDLER_PROXY_PORT + " = " + fiddlerPort);
			}
		} catch (Exception e) {
			log.warning("Error occured while populating constants for graph api client.", e);
		} finally {
			if(log.debug()) {
				log.debug("Teams Parameters: " + sb.toString());
			}
		}
	}
	
	public static TeamsGraphApiClient getTeamsGraphApiClient(TeamsConnectorContext teamsContext) throws Exception {
		if(INSTANCE == null) {
			synchronized (TeamsGraphApiClient.class) {
				if(INSTANCE == null) {
					INSTANCE = new TeamsGraphApiClient(teamsContext);
				}
			}
		}
		return INSTANCE;
	}
	
	@SuppressWarnings("deprecation")
	private TeamsGraphApiClient(TeamsConnectorContext teamsContext) throws Exception {
		this.teamsContext = teamsContext;
		configureConstants();
        if(!fiddlerEnabled) {
        	connectionMgr = new PoolingClientConnectionManager();
		} else {
			//This logic is to support Fiddler proxy.
			if(log.debug()) {
				log.debug("Fiddler proxy is enabled..");
			}
			SSLContext sslContext = SSLContext.getInstance("SSL");
			KeyManager[] keyMgr = null;
			TrustManager[] trustMgr = new TrustManager[] { new TeamsDummyTrustManager() };
			SecureRandom sr = new SecureRandom();
			// set up a TrustManager that trusts everything
			sslContext.init(keyMgr, trustMgr, sr);
			SchemeSocketFactory sf = new SSLSocketFactory(sslContext);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
	        schemeRegistry.register(new Scheme("https", 443, sf));
	        connectionMgr = new PoolingClientConnectionManager(schemeRegistry);
		}
        connectionMgr.setMaxTotal(networkMaxConnections);
		connectionMgr.setDefaultMaxPerRoute(networkMaxConnectionsPerRoute);
		
		httpclient = new DefaultHttpClient(connectionMgr);
		HttpParams params = new BasicHttpParams();
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, networkTimeoutMillis);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, networkTimeoutMillis);
		httpclient.setParams(params);
		httpclient.setHttpRequestRetryHandler(new StandardHttpRequestRetryHandler(networkFailureMaxRetry, true) {
			
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				final HttpClientContext clientContext = HttpClientContext.adapt(context);
		        final HttpRequest request = clientContext.getRequest();
		        String url = request.getRequestLine().getUri();
				if(log.debug()) {
					long waitTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(networkFailureWaitTimeMillis);
					log.debug("Waiting for " + waitTimeMinutes + " seconds before retrying request. Url: " + url);
				}
				try {
					Thread.sleep(networkFailureWaitTimeMillis);
				} catch (InterruptedException e) {
					log.warning("Thread interrupted while waiting before retry. Url: " + url, e);
				}
				if(log.debug()) {
					log.debug("retrying request. Url: " + url);
				}
				return super.retryRequest(exception, executionCount, context);
			}
		});
		configureHttpClient();
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.registerModule(new TeamsObjectModule());
	}
	
	@SuppressWarnings("deprecation")
	private void configureHttpClient() {
		if(fiddlerEnabled) {
			if(log.debug()) {
        		log.debug("Fiddler proxy config - host: " + fiddlerHost + ", port: " + fiddlerPort);	        		
        	}
			HttpHost proxy = new HttpHost(fiddlerHost, fiddlerPort);
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		} else if(teamsContext.isProxyEnabled()) { 
			try {
				httpclient.getCredentialsProvider().setCredentials(new AuthScope(teamsContext.getProxyHost(), teamsContext.getProxyPort()), 
						new NTCredentials(teamsContext.getProxyUsername(), teamsContext.getProxyPassword(), java.net.InetAddress.getLocalHost().getHostAddress(), teamsContext.getProxyDomain()));
			} catch (UnknownHostException e) {
				//don't throw the exception as the credentials are already verified in controller class.
				log.warning("Error occured while setting proxy credentials to http client.", e);
			}
			HttpHost proxy = new HttpHost(teamsContext.getProxyHost(), teamsContext.getProxyPort());
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);																					
		} else {
			log.debug("Vantage Teams integration support only proxy of https/https with NTLM authentication");
		}
	}
	
	public String executeRequest(String httpMethod, URI uri, Map<String, String> requestHeaders, String requestPayload) throws Exception {
		accessToken = fetchAccessToken();
		try {
			if(httpMethod.equals(HTTP_METHOD_GET)) {
				return executeGet(uri, accessToken);
			} else if(httpMethod.equals(HTTP_METHOD_POST)) {
				return executePost(uri, requestPayload, requestHeaders, accessToken);
			} else {
				log.warning("unknown http method passed.");
				return null;
			}
		} catch (Exception e) {
			if(e instanceof TeamsUnauthorizedException) {
				TeamsUnauthorizedException unauthorizedEx = (TeamsUnauthorizedException) e;
				TeamsGraphApiError err = unauthorizedEx.getErrObject();
				if(err.getMessage().equals(MSG_ACCESS_TOKEN_EXPIRED)) {
					if(log.debug()) {
						log.debug("Access token expired. Trying to refresh the access token.");
					}
					//refresh the access token
					accessTokenObj = null;
					return executeRequest(httpMethod, uri, requestHeaders, requestPayload);
				} else {
					log.warning("Exception occurred while executing request. Http Method: " + httpMethod + ", URL: " + uri + ", Request Headers: " + requestHeaders + ", Request Payload: " + requestPayload, e);
					throw e;
				}
			} else if(e instanceof TeamsItemNotFoundException) {
				log.warning("TeamsItemNotFoundException: " + e.getMessage());
				throw e;
			} else {
				log.warning("Exception occurred while executing request. Http Method: " + httpMethod + ", URL: " + uri + ", Request Headers: " + requestHeaders + ", Request Payload: " + requestPayload, e);
				throw e;
			}
		}
	}
	
	public String fetchAccessToken() throws Exception {
		long now = System.currentTimeMillis();
		if (accessTokenObj == null
				|| (now - accessTokenObj.getTokenObtainedAt()) > ((accessTokenObj.getExpiresIn() * 1000) - MINUTE_10)) {
			synchronized (this) {
				if (accessTokenObj == null) {
					if(log.debug()) {
						log.debug("Fetching AccessToken. Token is expired or not initialized");
					}
					Map<String, String> requestHeaders = new LinkedHashMap<String, String>();
					requestHeaders.put("Content-Type", ACCESS_TOKEN_REQUEST_CONTENT_TYPE);
					String accessTokenPayload = getAccessTokenPayload();
					if(log.debug()) {
						log.debug("Credentials used in Payload to get token - ClientId: " + teamsContext.getClientId() + ", ClientSecret: " + "***********************");
					}
					long tokenObtTimeMillis = System.currentTimeMillis();
					String json = executePost(new URI(teamsContext.getAuthEndPoint()), accessTokenPayload, requestHeaders, null);
					accessTokenObj = objectMapper.readValue(json, TeamsAccessToken.class);
					//Set the time at which the token is obtained. This is used to check if the token is expired.
					accessTokenObj.setTokenObtainedAt(tokenObtTimeMillis);
					if(log.debug()) {
						log.debug("Access token obtained at time " + new Date(tokenObtTimeMillis));
						log.debug("Access token details: " + accessTokenObj);
					}
				}
			}
		} else {
			if(log.debug()) {
				long expiresInMillis = (accessTokenObj.getExpiresIn() * 1000) - (now - accessTokenObj.getTokenObtainedAt());
				long expiresInMinutes = TimeUnit.MILLISECONDS.toMinutes(expiresInMillis);
				log.debug("Access token expires in " + expiresInMinutes + " minutes");
			}
		}
		return accessTokenObj.getAccessToken();
	}

	private String getAccessTokenPayload() {
		String value = ACCESS_TOKEN_PAYLOAD_FORMAT;
		value = value.replace("#{clientId}", teamsContext.getClientId());
		value = value.replace("#{secretKey}", teamsContext.getClientSecret());
		return value;
	}
	
	public TeamsFile fetchSharepointFile(String fileUrl) throws Exception {
		String base64Value = new String(Base64.encode(fileUrl.getBytes()), Charset.forName("UTF-8"));
		String encodedUrl = "u!" + StringUtils.stripEnd(base64Value, "=").replace('/', '_').replace('+', '-');
		String url = teamsContext.getBaseUrl() + "/shares/" + encodedUrl + "/root";
		String json = executeRequest(HTTP_METHOD_GET, new URI(url), null, null);
		TeamsFile teamsFile = objectMapper.readValue(json, TeamsFile.class);
		teamsFile.setSharepointFile(true);
		teamsFile.setOriginalUrl(fileUrl);
		return teamsFile;
	}
	
	public TeamsGroup[] fetchGroups() throws Exception {
		String url = teamsContext.getBaseUrl() + "/groups";
		String json = executeRequest(HTTP_METHOD_GET, new URI(url), null, null);
		ObjectReader reader = objectMapper.reader();
		JsonNode root = reader.readTree(json);
		JsonNode array = root.get("value");
		TeamsGroup[] teamsGroup = objectMapper.readValue(array, TeamsGroup[].class);
		return teamsGroup;
	}
	
	public TeamsUser[] fetchGroupMembers(String groupId) throws Exception {
		String url = teamsContext.getBaseUrl() + "/groups/" + groupId + "/members";
		String json = executeRequest(HTTP_METHOD_GET, new URI(url), null, null);
		ObjectReader reader = objectMapper.reader();
		JsonNode root = reader.readTree(json);
		JsonNode array = root.get("value");
		TeamsUser[] teamsGroup = objectMapper.readValue(array, TeamsUser[].class);
		return teamsGroup;
	}
	
	public TeamsUser[] fetchUsers() throws Exception {
		String url = teamsContext.getBaseUrl() + "/users";
		String json = executeRequest(HTTP_METHOD_GET, new URI(url), null, null);
		ObjectReader reader = objectMapper.reader();
		JsonNode root = reader.readTree(json);
		JsonNode array = root.get("value");
		TeamsUser[] teamsUsers = objectMapper.readValue(array, TeamsUser[].class);
		return teamsUsers;
	}
	
	public TeamsUser fetchUserByDisplayName(String displayName) throws Exception {
		String filter = URLEncoder.encode("displayName eq '" + displayName + "'", "UTF-8");
		String url = teamsContext.getBaseUrl() + "/users?$filter=" + filter;
		String json = executeRequest(HTTP_METHOD_GET, new URI(url), null, null);
		ObjectReader reader = objectMapper.reader();
		JsonNode root = reader.readTree(json);
		JsonNode array = root.get("value");
		if(array.size() == 0) {
			//It means user does not exist for the display name.
			log.warning("User does not exist for the display name: " + displayName);
			return null;
		} else if(array.size() == 1) {
			//We got the user with that display name.
			TeamsUser[] teamsUsers = objectMapper.readValue(array, TeamsUser[].class);
			return teamsUsers[0];
		} else {
			//More than one user exist for the same display name.
			log.warning("More than one user exist for the same display name: " + displayName);
			return null;
		}
	}
	
	/**
	 * Return the user details.
	 * @param id User Id (or) user principal name.
	 * @return
	 * @throws Exception
	 */
	public TeamsUser fetchUser(String id) throws Exception {
		String url = teamsContext.getBaseUrl() + "/users/" + id;
		String json = executeRequest(HTTP_METHOD_GET, new URI(url), null, null);
		TeamsUser teamsUser = objectMapper.readValue(json, TeamsUser.class);
		return teamsUser;
	}

	/**
	 * This method accepts only json response.
	 * @param url
	 * @param accessToken
	 * @param accessToken2 
	 * @param requestHeaders 
	 * @return The json response as string.
	 * @throws IOException 
	 */
	private String executePost(URI uri, String requestPayload, Map<String, String> requestHeaders, String accessToken) throws IOException {
			//	log.debug("HTTP POST - " + uri.toString());
				InputStreamReader input = null;
				ByteArrayOutputStream output = null;
				HttpPost request = null;
				HttpEntity entity = null;
				try {
					request = new HttpPost(uri);
					if(accessToken != null) {
						//Request to retrieve access token will pass null value for this parameter.
						request.addHeader("Authorization", "Bearer " + accessToken);
					}
					request.addHeader("Accept", "application/json");
					
					//add user passed headers.
					if(requestHeaders != null && requestHeaders.size() > 0) {
						Set<String> hdrKeys = requestHeaders.keySet();
						for (String hdrKey : hdrKeys) {
							request.addHeader(hdrKey, requestHeaders.get(hdrKey));
						}
					}
					
					//set the request payload.
					if(requestPayload != null) {
						StringEntity se = new StringEntity(requestPayload);      
						request.setEntity(se);
					}
					
					HttpResponse response = httpclient.execute(request);

					int responseCode = response.getStatusLine().getStatusCode();
					entity = response.getEntity();
					if (responseCode / 100 != 2) {
						if(entity != null) {
							String errJson = EntityUtils.toString(entity, HTTP.UTF_8);
							log.debug("Non 200 response body: " + errJson);
						}
						if(responseCode == ACCESS_DENIED) {
							log.debug("Permission issue: " + uri.toString() + ", " + responseCode + " " + 
									response.getStatusLine().getReasonPhrase());
							throw new TeamsAccessDeniedException(responseCode + " Access denied", uri);
						} else if(responseCode == RESOURCE_NOT_FOUND) {
							log.debug("404 - item not found => " + response.getStatusLine().getReasonPhrase() + ". URL: " + uri);
							throw new TeamsItemNotFoundException(uri);
						} else if(responseCode == TOO_MANY_REQUESTS) {
							log.debug("429 - Rate limit exceeded => " + response.getStatusLine().getReasonPhrase() + ". URL: " + uri);
							Thread.sleep(30 * 1000);
							return executePost(uri, requestPayload, requestHeaders, accessToken); // retry
						} else {
							log.debug("Error occured while making api request : Response code => " + responseCode, response.getStatusLine().getReasonPhrase() + ". URL: " + uri.toString());
							throw new IOException("File download failed : HTTP error code : " + responseCode);
						}
					}

					if(entity != null) {
						String json = EntityUtils.toString(entity, HTTP.UTF_8);
						return json;
					} else {
						log.info("Couldn't fetch json from url " + uri);
						throw new IOException("The entity returned is null for api endpoint" + uri);
					}
				} catch(SSLHandshakeException sslEx) { 
					// In some environments we may get this error.
					log.debug("Certificate issue: " + uri.toString(), sslEx);
					throw new IOException(sslEx);
				} catch(Throwable t) {
					if(t instanceof TeamsItemNotFoundException) {
						// Don't log stack trace here. The method caller will print the stack trace.
						throw (TeamsItemNotFoundException)t;
					} else if(t instanceof TeamsAccessDeniedException) {
						// Don't log stack trace here. The method caller will print the stack trace.
						throw (TeamsAccessDeniedException) t;
					} else {
						log.debug("Error occured while making API call" + ". URL: " + uri, t);
						throw new IOException(t);
					}
				} finally {
					try {
						if(input != null)
							input.close();
						if(output != null)
							output.close();
						if(entity != null)
							EntityUtils.consume(entity);
						if(request != null)
							request.releaseConnection();
					} catch (IOException e) {
						//log.warning("Failure in cleaning up http entity." + e.getMessage(), e);
					}
				}
	}

	/**
	 * This method accepts only json response.
	 * @param uri
	 * @return json string
	 * @throws IOException
	 */
	private String executeGet(URI uri, String accessToken) throws IOException {
		log.debug("HTTP GET - " + uri.toString());
		InputStreamReader input = null;
		ByteArrayOutputStream output = null;
		HttpGet request = null;
		HttpEntity entity = null;
		try {
			request = new HttpGet(uri);
			if(accessToken != null) {
				request.addHeader("Authorization", "Bearer " + accessToken);
			}
			request.addHeader("Accept", "application/json");
			
			HttpResponse response = httpclient.execute(request);

			int responseCode = response.getStatusLine().getStatusCode();
			entity = response.getEntity();
			if (responseCode / 100 != 2) {
				String errJson = null;
				if(entity != null) {
					errJson = EntityUtils.toString(entity, HTTP.UTF_8);
					if(log.debug()) {
						log.debug("Non 200 response body: " + errJson);
					}
				}
				if(responseCode == ACCESS_DENIED) {
					log.warning("Permission issue: " + uri.toString() + ", " + responseCode + " " + 
							response.getStatusLine().getReasonPhrase());
					throw new TeamsAccessDeniedException(responseCode + " Access denied", uri);
				} else if(responseCode == UNAUTHORIZED) {
					if(log.debug()) {
						log.debug("access token issue: " + uri.toString() + ", " + responseCode + " " + 
								response.getStatusLine().getReasonPhrase());
					}
					throw new TeamsUnauthorizedException(responseCode + " Unauthorized", uri, errJson);
				} else if(responseCode == RESOURCE_NOT_FOUND) {
					if(log.debug()) {
						log.debug("404 - item not found => " + response.getStatusLine().getReasonPhrase() + ". URL: " + uri);
					}
					throw new TeamsItemNotFoundException(uri);
				} else if(responseCode == TOO_MANY_REQUESTS) {
					if(log.debug()) {
						log.debug("429 - Rate limit exceeded => " + response.getStatusLine().getReasonPhrase() + ". URL: " + uri);
					}
					Thread.sleep(30 * 1000);
					return executeGet(uri, accessToken); // retry
				} else {
					log.warning("Error occured while making api request : Response code => " + responseCode, response.getStatusLine().getReasonPhrase() + ". URL: " + uri.toString());
					throw new IOException("File download failed : HTTP error code : " + responseCode);
				}
			}

			if(entity != null) {
				String json = EntityUtils.toString(entity, HTTP.UTF_8);
				return json;
			} else {
				log.warning("Couldn't fetch json from url " + uri);
				throw new IOException("The entity returned is null for api endpoint" + uri);
			}
		} catch(SSLHandshakeException sslEx) { 
			// In some environments we may get this error.
			log.warning("Certificate issue: " + uri.toString(), sslEx);
			throw new IOException(sslEx);
		} catch(Throwable t) {
			if(t instanceof TeamsItemNotFoundException) {
				// Don't log stack trace here. The method caller will print the stack trace.
				throw (TeamsItemNotFoundException)t;
			} else if(t instanceof TeamsAccessDeniedException) {
				// Don't log stack trace here. The method caller will print the stack trace.
				throw (TeamsAccessDeniedException) t;
			} else if(t instanceof TeamsUnauthorizedException) {
				// Don't log stack trace here. The method caller will print the stack trace.
				throw (TeamsUnauthorizedException) t;
			} else {
				log.warning("Error occured while making API call" + ". URL: " + uri, t);
				throw new IOException(t);
			}
		} finally {
			try {
				if(input != null)
					input.close();
				if(output != null)
					output.close();
				if(entity != null)
					EntityUtils.consume(entity);
				if(request != null)
					request.releaseConnection();
			} catch (IOException e) {
				log.warning("Failure in cleaning up http entity." + e.getMessage(), e);
			}
		}
	}
}
