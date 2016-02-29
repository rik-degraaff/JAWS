package jaws.business.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * An abstract class to represent both HTTP requests and responses
 *
 * @author Rik
 *
 * @param <T> used to make method chaining work for objects that inherit from it. Must be the same type as extending class.
 * 
 * @see jaws.business.http.HTTPRequest
 * @see jaws.business.http.HTTPResponse
 */
abstract class HTTPObject<T extends HTTPObject<T>> {

	private Map<String, String> headerFields = new HashMap<>();
	private String body;

	/**
	 * Sets the header for the key to the value.
	 *
	 * @param key the header key.
	 * @param value the value of the header field.
	 * @return the HTTP object for method chaining.
	 */
	@SuppressWarnings("unchecked")
	final public T header(String key, String value) {

		headerFields.put(key, value);
		return (T) this;
	}

	/**
	 * Gets the value of a header field for a key.
	 *
	 * @param key the key of the header field.
	 * @return the value of the field.
	 */
	final public String header(String key) {

		return headerFields.get(key);
	}

	/**
	 * Sets the body of the HTTP object.
	 *
	 * @param body the body of the HTTP object as a string.
	 * @return the HTTP object for method chaining.
	 */
	@SuppressWarnings("unchecked")
	final public T body(String body) {

		this.body = body;
		return (T) this;
	}

	/**
	 * Gets the body of the HTTP object.
	 *
	 * @return the body of the HTTP object as a string.
	 */
	final public String body() {

		return body;
	}

	/**
	 * Gets the first line of the HTTP object.
	 *
	 * @return the first line of the HTTP object.
	 */
	abstract protected String getFirstLine();
	
	/**
	 * Can be used to convert the HTTPObject to a syntactically correct String.
	 * 
	 * @return a string representation of the HTTP object.
	 */
	final public String toString() {

		final String nl = System.lineSeparator();
		return getFirstLine() + nl
				+ headerFields.entrySet().stream()
				                         .map(e -> e.getKey() + ": " + e.getValue() + nl)
				                         .reduce("", String::concat)
				+ nl
				+ body;
	}
	
	/**
	 * Parses everything but the first line of a HTTP object.
	 * 
	 * @param httpObject the HTTPObject to parse for.
	 * @param reader the lines to parse, the first line is assumed to be the first header.
	 * @return the HTTPObject passed to this function.
	 * @throws IOException 
	 */
	static <T extends HTTPObject<T>> T parseHeadersAndBody(T httpObject, BufferedReader reader) throws IOException {
		
		String line;
		while (!(line = reader.readLine()).isEmpty()) {
			
			String[] parts = line.split(":", 2);
			httpObject.header(parts[0].trim(), parts[1].trim());
		}
		
		int bodyLength = Integer.parseInt(httpObject.header("Content-Length"));
		
		char[] chars = new char[bodyLength];
		reader.read(chars, 0, bodyLength);
		
		return httpObject.body(new String(chars));
	}
}
