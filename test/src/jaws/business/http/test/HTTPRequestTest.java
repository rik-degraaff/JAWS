package jaws.business.http.test;

import static org.junit.Assert.assertEquals;
import static trycrash.Try.tryCrash;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jaws.business.http.HTTPObjectFactory;
import jaws.module.http.HTTPRequest;
import jaws.module.http.RequestMethod;

public class HTTPRequestTest {

	private final String nl = System.lineSeparator();
	private RequestMethod method;
	private String url, httpVersion;
	private List<String> keys, values;
	private String body;
	int bodyLength;

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

		bodyLength = body.length();
	}

	@Test
	public void testParse() {

		String requestString = method + " " + url + " " + httpVersion + nl
                             + keys.get(0) + ": " + values.get(0) + nl
                             + keys.get(1) + ": " + values.get(1) + nl
                             + keys.get(2) + ": " + values.get(2) + nl
                             + keys.get(3) + ": " + values.get(3) + nl
                             + "Content-Length: " + bodyLength + nl
                             + nl
                             + body;

		BufferedReader reader = new BufferedReader(new StringReader(requestString));
		HTTPRequest request = null;
		request = tryCrash(() -> HTTPObjectFactory.parseRequest(reader));

		assertEquals("Requestmethod was not parsed correctly.", method, request.method());
		assertEquals("Url was not parsed correctly.", url, request.url());
		assertEquals("HTTP version was not parsed correctly.", httpVersion, request.httpVersion());

		for (int i = 0; i < Math.min(keys.size(), values.size()); ++i) {

			assertEquals("header nr." + (i + 1) + " was not parsed correctly.", values.get(i), request.header(keys.get(i)));
		}

		assertEquals("Body was not parsed correctly.", body, new String(request.body()));
	}

}
