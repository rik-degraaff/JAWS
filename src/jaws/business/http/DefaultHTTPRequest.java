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
	
	@Override
	public RequestMethod method() {
		
		return requestMethod;
	}
	
	@Override
	public DefaultHTTPRequest method(RequestMethod method) {
		
		requestMethod = method;
		return this;
	}
	
	@Override
	public String url() {
		
		return url;
	}
	
	@Override
	public DefaultHTTPRequest url(String url) {
		
		this.url = url;
		return this;
	}
	
	@Override
	public String httpVersion() {
		
		return httpVersion;
	}
	
	@Override
	public DefaultHTTPRequest httpVersion(String httpVersion) {
		
		this.httpVersion = httpVersion;
		return this;
	}
}
