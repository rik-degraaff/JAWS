package jaws.business.net;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;

import jaws.business.http.HTTPObjectFactory;
import jaws.context.Context;
import jaws.data.net.Connection;
import jaws.module.http.HTTPRequest;
import jaws.module.http.HTTPResponse;
import jaws.module.http.RequestMethod;

public class RequestProcessor {

	private String webroot;

	private BiFunction<String, RequestMethod, Optional<Handler>> handlerGetter;

	public RequestProcessor(BiFunction<String, RequestMethod, Optional<Handler>> handlerGetter, String webRoot) {

		this.handlerGetter = handlerGetter;
		this.webroot = webRoot;
	}

	public void handle(Connection client) {

		try {

			//HTTPRequest request = DefaultHTTPRequest.parse(client.read());
			HTTPRequest request = HTTPObjectFactory.parseRequest(client.read());
			Context.logger.info("An http request has come in:" + System.lineSeparator()
			                    + request.toString(), "request");
			HTTPResponse response = HTTPObjectFactory.createResponse().httpVersion("HTTP/1.1").statusCode(200).reason("OK")
			                                                          .header("Content-Type", "text/html");
			Handler handler = handlerGetter.apply(request.url().substring(request.url().lastIndexOf('.') + 1), request.method()).get();

			response = handler.handle(request, response, new File(webroot));
			client.write(response.getOutputStream());
		} catch (IOException | NoSuchElementException e) {

			String body = "<h1>500 - Internal Server Error</h1>";
			HTTPResponse response = HTTPObjectFactory.createResponse().httpVersion("HTTP/1.1").statusCode(500).reason("Internal Server Error")
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
