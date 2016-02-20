package jaws.business.http;

import java.util.HashMap;

/**
 * An abstract class to represent both HTTP requests and responses
 * 
 * @author Rik
 *
 * @param <T> used to make method chaining work for objects that inherit from it.
 */
abstract class HTTPObject<T extends HTTPObject<?>> {
	
	private HashMap<String, String> headerFields;
	private String body;
	
	/**
	 * Sets the header for the key to the value.
	 * 
	 * @param key the header key.
	 * @param value the value of the header field.
	 * @return the HTTP object for method chaining.
	 */
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
	
	final public String toString() {
		
		final String nl = System.lineSeparator();
		return getFirstLine() + nl
				+ headerFields.entrySet().stream()
				                         .map(e -> e.getKey() + ": " + e.getValue() + nl)
				                         .reduce("", String::concat)
				+ nl
				+ body;
	}
}
