package jaws.business.net;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import jaws.business.http.DefaultHTTPRequest;
import jaws.business.http.DefaultHTTPResponse;
import jaws.data.net.Connection;
import jaws.module.http.HTTPRequest;
import jaws.module.http.HTTPResponse;

public class RequestProcessor {
	
	private Function<String, Optional<Handler>> handlerGetter;
	
	public RequestProcessor(Function<String, Optional<Handler>> handlerGetter) {
		
		this.handlerGetter = handlerGetter;
	}

	public void handle(Connection client) {
		
		try {
			
			HTTPRequest request = DefaultHTTPRequest.parse(client.read());
			HTTPResponse response = new DefaultHTTPResponse().httpVersion("HTTP/1.1").statusCode(200).reason("OK")
			                                          .header("Content-Type", "text/html");
			Handler handler = handlerGetter.apply(request.url().substring(request.url().lastIndexOf('.') + 1)).get();
			
			response = handler.handle(request, response, new File("D:\\Projects\\www")); //TODO read webroot from config file
			client.write(response.getOutputStream());
		} catch (IOException | NoSuchElementException e) {
			
			String body = "<h1>500 - Internal Server Error</h1>";
			HTTPResponse response = new DefaultHTTPResponse().httpVersion("HTTP/1.1").statusCode(500).reason("Internal Server Error")
			                                          .header("Content-Type", "text/html")
			                                          .header("Content-Length", Integer.toString(body.length()))
			                                          .body(body);
			
			for(int i=0; i<3; i++) {
				
				try {
					client.write(response.getBytes());
					break;
				} catch (IOException e1) {
					continue;
				}
			}
		}
	}
}
