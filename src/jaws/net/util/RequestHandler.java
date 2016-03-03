package jaws.net.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;

import jaws.business.http.HTTPRequest;
import jaws.business.http.HTTPResponse;

public class RequestHandler {
	
	private List<Entry<String, Method>> handlers;
	
	private Optional<Method> getHandler(String extension) {
		
		return handlers.stream()
		               .filter(e -> extension.matches(e.getKey()))
		               .limit(1)
		               .map(e -> e.getValue())
		               .findFirst();
	}

	public void handle(Connection client) {
		
		try {
			@SuppressWarnings("unused")
			HTTPRequest request = HTTPRequest.parse(client.read());
			String body = "<h1>Hello, World!</h1>";
			HTTPResponse response = new HTTPResponse().httpVersion("HTTP/1.1").statusCode(200).reason("OK")
			                                          .header("Content-Type", "text/html");
			Method handler = getHandler(request.url().substring(request.url().lastIndexOf('.') + 1)).get();
			
			handler.
		} catch (IOException | NoSuchElementException e) {
			
			String body = "<h1>500 - Internal Server Error</h1>";
			HTTPResponse response = new HTTPResponse().httpVersion("HTTP/1.1").statusCode(500).reason("Internal Server Error")
			                                          .header("Content-Type", "text/html")
			                                          .header("Content-Length", Integer.toString(body.length()))
			                                          .body(body);
			
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
