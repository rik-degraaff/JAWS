package jaws.business.defaultmodule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import jaws.module.http.HTTPRequest;
import jaws.module.http.HTTPResponse;
import jaws.module.http.RequestMethod;
import jaws.module.net.Handle;

public class DefaultHandler {

	private static final Map<String, String> mimeTypes = new HashMap<>();

	static {

		// plain text types
		mimeTypes.put("html", "text/html");
		mimeTypes.put("htm", "text/html");
		mimeTypes.put("xml", "text/xml");
		mimeTypes.put("txt", "text/plain");
		mimeTypes.put("css", "text/css");
		mimeTypes.put("js", "text/javascript");

		// image types
		mimeTypes.put("png", "image/png");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("bmp", "image/bmp");
		mimeTypes.put("bm", "image/bmp");

		// video types
		mimeTypes.put("mp4", "video/mp4");
		mimeTypes.put("avi", "video/avi");
	}

	@Handle(extensions = {".*"}, priority = Integer.MIN_VALUE)
	public static HTTPResponse handle(HTTPRequest request, HTTPResponse response, File webRoot) throws IOException {
		
		//if(Math.random() > 0.8)
		//	throw new RuntimeException("lol");

		File file = new File(webRoot, request.url().substring(1));
		if (file.exists()) {
			if (request.method() == RequestMethod.OPTIONS) {
				
				response.statusCode(200)
				        .header("Allow", "GET, HEAD, OPTIONS");
			} else {
				
				// if the requested path is a folder, try to get the 'index.html' file
				if(file.isDirectory() && !new File(file, "index.html").exists() && request.method() != RequestMethod.HEAD) {
					
					String body = "";
					final String[] fileNames = file.list();
					final String url = request.url();
					for(String fileName : fileNames) {
						body += "<a href=\"" + url + ((url.charAt(url.length() - 1) == '/') ? "" : "/") + fileName + "\">"
					          + fileName + (new File(file, fileName).isDirectory()?"/":"")
					          + "</a><br>";
					}
	
					response.body(body);
				} else {
					
					if(file.isDirectory() && new File(file, "index.html").exists()) {
						file = new File(file, "index.html");
					}
					
					if (request.method() != RequestMethod.HEAD) {
						
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						response.body(out);
						Files.copy(file.toPath(), out);
					}
					String fileExtension = request.url().substring(request.url().lastIndexOf('.') + 1);
					if(mimeTypes.containsKey(fileExtension)) {
						response.header("Content-Type", mimeTypes.get(fileExtension));
					}
				}
			}
		} else {
			response.statusCode(404).reason("Not Found").body("<h1>404 - Not Found</h1>");
		}

		return response.header("Content-Length", Integer.toString(response.body().length));
	}
}
