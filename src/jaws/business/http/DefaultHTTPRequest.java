package jaws.business.http;

import java.io.BufferedReader;
import java.io.IOException;

import jaws.module.http.HTTPRequest;
import jaws.module.http.RequestMethod;

/**
 * A class to represent an HTTP request to the webserver.
 * 
 * @author Rik
 * 
 * @see jaws.business.http.DefaultHTTPResponse
 */
public class DefaultHTTPRequest extends DefaultHTTPObject<HTTPRequest> implements HTTPRequest {

	private RequestMethod requestMethod;
	private String url;
	private String httpVersion;
	
	@Override
	protected String getFirstLine() {
		return requestMethod + " " + url + " " + httpVersion;
	}
	
	/**
	 * Parses a String into an HTTPRequest.
	 * 
	 * @param reader the String to parse.
	 * @return the parsed HTTPRequest.
	 * @throws IOException 
	 */
	public static HTTPRequest parse(BufferedReader reader) throws IOException {
		
		HTTPRequest request = new DefaultHTTPRequest();

		String[] firstLineItems = reader.readLine().split(" ");

		request.method(RequestMethod.valueOf(firstLineItems[0]))
		       .url(firstLineItems[1])
		       .httpVersion(firstLineItems[2]);
		
		request = DefaultHTTPObject.parseHeadersAndBody(request, reader);

		return request;
	}
	
	/**
	 * Gets the method of the HTTP Request.
	 * 
	 * @return the method of the HTTP Request as an Enum.
	 */
	@Override
	public RequestMethod method() {
		
		return requestMethod;
	}
	
	/**
	 * Sets the method of the HTTP Request.
	 * 
	 * @param method the method of the HTTP Request as an Enum.
	 * @return the HTTPRequest for method chaining.
	 */
	@Override
	public DefaultHTTPRequest method(RequestMethod method) {
		
		requestMethod = method;
		return this;
	}
	
	/**
	 * Gets the url of the HTTP Request.
	 * 
	 * @return the url as a String.
	 */
	@Override
	public String url() {
		
		return url;
	}
	
	/**
	 * Sets the url of the HTTP Request.
	 * 
	 * @param url the url as a String.
	 * @return the HTTPRequest for method chaining.
	 */
	@Override
	public DefaultHTTPRequest url(String url) {
		
		this.url = url;
		return this;
	}
	
	/**
	 * Gets the HTTP version of the HTTP Request.
	 * 
	 * @return the HTTP version as a String.
	 */
	@Override
	public String httpVersion() {
		
		return httpVersion;
	}
	
	/**
	 * Sets the HTTP version of the HTTP Request.
	 * 
	 * @param httpVersion the HTTP version as a String.
	 * @return the HTTPRequest for method chaining.
	 */
	@Override
	public DefaultHTTPRequest httpVersion(String httpVersion) {
		
		this.httpVersion = httpVersion;
		return this;
	}
}
