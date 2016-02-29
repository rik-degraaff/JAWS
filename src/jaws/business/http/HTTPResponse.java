package jaws.business.http;

/**
 * A class to represent an HTTP response from the webserver.
 * 
 * @author Rik
 * 
 * @see jaws.business.http.HTTPRequest
 */
public class HTTPResponse extends HTTPObject<HTTPResponse> {
	
	private int statusCode;
	private String reason;
	private String httpVersion;
	
	@Override
	protected String getFirstLine() {
		
		return httpVersion + " " + statusCode + " " + reason;
	}
	
	/**
	 * Gets the status code of the HTTP response.
	 * 
	 * @return the status code as an integer.
	 */
	public int statusCode() {
		
		return statusCode;
	}
	
	/**
	 * Sets the status code of the HTTP response.
	 * 
	 * @param code the status code as an integer.
	 * @return the HTTPResponse for method chaining.
	 */
	public HTTPResponse statusCode(int code) {
		
		statusCode = code;
		return this;
	}
	
	/**
	 * Gets the reason of the HTTP response. 
	 * 
	 * @return the reason as a String.
	 */
	public String reason() {
		
		return reason;
	}
	
	/**
	 * Sets the reason of the HTTP response.
	 * 
	 * @param reason the reason of the HTTP response.
	 * @return the HTTPResopnse for method chaining.
	 */
	public HTTPResponse reason(String reason) {
		
		this.reason = reason;
		return this;
	}
	
	/**
	 * Gets the HTTP version of the HTTP Response.
	 * 
	 * @return the HTTP version as a String.
	 */
	public String httpVersion() {
		
		return httpVersion;
	}
	
	/**
	 * Sets the HTTP version of the HTTP Response.
	 * 
	 * @param httpVersion the HTTP version as a String.
	 * @return the HTTPResponse for method chaining.
	 */
	public HTTPResponse httpVersion(String httpVersion) {
		
		this.httpVersion = httpVersion;
		return this;
	}
}
