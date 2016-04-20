package jaws.business.http;

import java.io.BufferedReader;
import java.io.IOException;

import jaws.module.http.HTTPRequest;
import jaws.module.http.HTTPResponse;

public class HTTPObjectFactory {

	public static HTTPRequest createRequest() {

		return new DefaultHTTPRequest();
	}

	public static HTTPRequest parseRequest(BufferedReader reader) throws IOException {

		return DefaultHTTPRequest.parse(reader);
	}

	public static HTTPResponse createResponse() {

		return new DefaultHTTPResponse();
	}
}
