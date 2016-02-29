package jaws.net.util;

import java.io.IOException;

import jaws.business.http.HTTPRequest;
import jaws.business.http.HTTPResponse;

public class RequestHandler {

	public void handle(Connection client) {
		
		try {
			HTTPRequest httpRequest = HTTPRequest.parse(client.read());
			client.write(new HTTPResponse().statusCode(200).reason("ok").body("<h1>Hello, World!</h1>").toString());
		} catch (IOException e) {
			
			HTTPResponse response = new HTTPResponse().statusCode(500)
			                                          .reason("Internal Server Error")
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
