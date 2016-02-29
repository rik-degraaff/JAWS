package jaws.business.http.util;

/**
 * A class to represent the request method of a {@link jaws.business.http.HTTPRequest}.
 * 
 * @author Rik
 *
 */
public enum RequestMethod {

	GET("GET"),
	POST("POST"),
	HEAD("HEAD"),
	OPTIONS("OPTIONS");

	private String name;

	private RequestMethod(String name) {

		this.name = name;
	}
	
	/**
	 * String representation of the method, suitable for the String representation of the HTTP request.
	 */
	public String toString() {

		return this.name;
	}
}