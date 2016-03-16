package jaws.business.http;

import static trycrash.Try.tryCrash;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jaws.module.http.HTTPObject;

/**
 * An abstract class to represent both HTTP requests and responses
 *
 * @author Rik
 *
 * @param <T> used to make method chaining work for objects that inherit from it. Must be the same type as extending class.
 * 
 * @see jaws.business.http.DefaultHTTPRequest
 * @see jaws.business.http.DefaultHTTPResponse
 */
abstract class DefaultHTTPObject<T extends HTTPObject<T>> {

	private Map<String, String> headerFields = new HashMap<>();
	private ByteArrayOutputStream body = new ByteArrayOutputStream();

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
	 * @param body the body of the HTTP object as a byte array.
	 * @return the HTTP object for method chaining.
	 */
	@SuppressWarnings("unchecked")
	final public T body(byte[] body) {

		this.body = new ByteArrayOutputStream();
		tryCrash(() -> this.body.write(body));
		return (T) this;
	}

	/**
	 * Sets the body of the HTTP object.
	 *
	 * @param body the body of the HTTP object as a string.
	 * @return the HTTP object for method chaining.
	 */
	@SuppressWarnings("unchecked")
	final public T body(String body) {

		this.body = new ByteArrayOutputStream();
		tryCrash(() -> this.body.write(body.getBytes()));
		return (T) this;
	}

	/**
	 * Gets the body of the HTTP object.
	 *
	 * @return the body of the HTTP object as a byte array.
	 */
	final public byte[] body() {

		return body.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	final public T body(ByteArrayOutputStream stream) {
		
		this.body = stream;
		return (T) this;
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
	
	final public byte[] getBytes() {

		final String nl = System.lineSeparator();
		String header = getFirstLine() + nl
				+ headerFields.entrySet().stream()
                                         .map(e -> e.getKey() + ": " + e.getValue() + nl)
                                         .reduce("", String::concat)
                + nl;
		byte[] headerBytes = header.getBytes();
		byte[] bytes = Arrays.copyOf(headerBytes, headerBytes.length + body().length);
		System.arraycopy(body(), 0, bytes, headerBytes.length, body.size());
		return bytes;
	}
	
	final public ByteArrayOutputStream getOutputStream() {
		
		final String nl = System.lineSeparator();
		String header = getFirstLine() + nl
				+ headerFields.entrySet().stream()
                                         .map(e -> e.getKey() + ": " + e.getValue() + nl)
                                         .reduce("", String::concat)
                + nl;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		tryCrash(() -> {
			out.write(header.getBytes());
			body.writeTo(out);
		});
		
		return out;
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
		
		String blstr = httpObject.header("Content-Length");
		int bodyLength = (blstr != null) ? Integer.parseInt(blstr) : 0;
		
		char[] chars = new char[bodyLength];
		reader.read(chars, 0, bodyLength);
		
		return httpObject.body(new String(chars));
	}
}
