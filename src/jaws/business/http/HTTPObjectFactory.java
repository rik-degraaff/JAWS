package jaws.business.http;

import java.io.BufferedReader;
import java.io.IOException;

import jaws.module.http.HTTPRequest;
import jaws.module.http.HTTPResponse;

/**
 * A static class that allows the creation of HTTP Objects
 * 
 * @author Rik
 *
 */
final public class HTTPObjectFactory {
	
	private HTTPObjectFactory() {}
	
	/**
	 * Creates an empty HTTP request.
	 * 
	 * @return the HTTP request.
	 */
	public static HTTPRequest createRequest() {

		return new DefaultHTTPRequest();
	}

	/**
	 * Creates a HTTP request by parsing a BufferedReader.
	 * 
	 * @return the parsed HTTP request.
	 */
	public static HTTPRequest parseRequest(BufferedReader reader) throws IOException {

		return DefaultHTTPRequest.parse(reader);
	}

	/**
	 * Creates an empty HTTP response.
	 * 
	 * @return the HTTP response.
	 */
	public static HTTPResponse createResponse() {

		return new DefaultHTTPResponse();
	}
}
