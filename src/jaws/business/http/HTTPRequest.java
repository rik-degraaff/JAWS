package jaws.business.http;

import java.util.Arrays;
import java.util.List;

import jaws.business.http.util.RequestMethod;

/**
 * A class to represent an HTTP request to the webserver.
 * 
 * @author Rik
 * 
 * @see jaws.business.http.HTTPResponse
 */
public class HTTPRequest extends HTTPObject<HTTPRequest> {

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
	 * @param requestString the String to parse.
	 * @return the parsed HTTPRequest.
	 */
	public static HTTPRequest parse(String requestString) {
		
		HTTPRequest request = new HTTPRequest();

		final String nl = System.lineSeparator();

		List<String> lines = Arrays.asList(requestString.split(nl));
		String[] firstLineItems = lines.get(0).split(" ");

		request.requestMethod = RequestMethod.valueOf(firstLineItems[0]);
		request.url = firstLineItems[1];
		request.httpVersion = firstLineItems[2];
		
		request = HTTPObject.parseHeadersAndBody(request, lines.subList(1, lines.size() - 1));

		return request;
	}
}
