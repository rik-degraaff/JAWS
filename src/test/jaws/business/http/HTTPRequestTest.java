package test.jaws.business.http;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import jaws.business.http.HTTPRequest;
import jaws.business.http.util.RequestMethod;

public class HTTPRequestTest {
	
	private final String nl = System.lineSeparator();
	private RequestMethod method;
	private String url, httpVersion;
	private List<String> keys, values;
	private String body;
	
	@Before
	public void setUp() {
		
		method = RequestMethod.GET;
		url = "http://jaws.test/path/";
		httpVersion = "HTTP/1.1";

		keys = Arrays.asList("key1", "anotherKey", "key3", "key-key-key");
		values = Arrays.asList("a_value", "another-value", "the_third-value", "goodvalue.");
		
		body = "This is the body" + nl
		     + "of the request." + nl
		     + "cool huh?";
	}
	
	@Test
	public void testParse() {
		
		String requestString = method + " " + url + " " + httpVersion + nl
                             + keys.get(0) + ": " + values.get(0) + nl
                             + keys.get(1) + ": " + values.get(1) + nl
                             + keys.get(2) + ": " + values.get(2) + nl
                             + keys.get(3) + ": " + values.get(3) + nl
                             + nl
                             + body;
		
		HTTPRequest request = HTTPRequest.parse(requestString);
		
		assertEquals("Requestmethod was not parsed correctly.", method, request.method());
		assertEquals("Url was not parsed correctly.", url, request.url());
		assertEquals("HTTP version was not parsed correctly.", httpVersion, request.httpVersion());
		
		for (int i = 0; i < Math.min(keys.size(), values.size()); ++i) {
			
			assertEquals("header nr." + (i + 1) + " was not parsed correctly.", values.get(i), request.header(keys.get(i)));
		}
		
		assertEquals("Body was not parsed correctly.", body, request.body());
	}

}
