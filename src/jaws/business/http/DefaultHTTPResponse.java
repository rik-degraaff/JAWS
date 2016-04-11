package jaws.business.http;

import jaws.module.http.HTTPResponse;

/**
 * A class to represent an HTTP response from the webserver.
 * 
 * @author Rik
 * 
 * @see jaws.business.http.DefaultHTTPRequest
 */
public class DefaultHTTPResponse extends DefaultHTTPObject<HTTPResponse> implements HTTPResponse {
	
	private int statusCode;
	private String reason;
	private String httpVersion;
	
	@Override
	protected String getFirstLine() {
		
		return httpVersion + " " + statusCode + " " + reason;
	}
	
	@Override
	public int statusCode() {
		
		return statusCode;
	}
	
	@Override
	public HTTPResponse statusCode(int code) {
		
		statusCode = code;
		return this;
	}
	
	@Override
	public String reason() {
		
		return reason;
	}
	
	@Override
	public HTTPResponse reason(String reason) {
		
		this.reason = reason;
		return this;
	}
	
	@Override
	public String httpVersion() {
		
		return httpVersion;
	}
	
	@Override
	public HTTPResponse httpVersion(String httpVersion) {
		
		this.httpVersion = httpVersion;
		return this;
	}
}
