package jaws.net.util;

import java.io.IOException;

import jaws.business.http.HTTPRequest;
import jaws.business.http.HTTPResponse;

public class RequestHandler {

	public void handle(Connection client) {
		
		try {
			@SuppressWarnings("unused")
			HTTPRequest httpRequest = HTTPRequest.parse(client.read());
			String body = "<h1>Hello, World!</h1>";
			client.write(new HTTPResponse().httpVersion("HTTP/1.1").statusCode(200).reason("OK")
			                               .header("Content-Type", "text/html")
			                               .header("Content-Length", Integer.toString(body.length()))
			                               .body(body).toString());
		} catch (IOException e) {
			
			String body = "<h1>Hello, World!</h1>";
			HTTPResponse response = new HTTPResponse().httpVersion("HTTP/1.1").statusCode(500).reason("Internal Server Error")
			                                          .header("Content-Type", "text/html")
			                                          .header("Content-Length", Integer.toString(body.length()))
			                                          .body("<h1>500 - Internal Server Error</h1>");
			
			for(int i=0; i<3; i++) {
				
				try {
					client.write(response.toString());
					break;
				} catch (IOException e1) {
					continue;
				}
			}
		}
	}
}
