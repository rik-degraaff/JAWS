package jaws.module.standard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jaws.business.http.HTTPRequest;
import jaws.business.http.HTTPResponse;
import jaws.module.base.Handle;

public class DefaultHandler {
	
	private static final Map<String, String> mimeTypes = new HashMap<>();
	
	static {
		mimeTypes.put("html", "text/html");
		mimeTypes.put("htm", "text/html");
		mimeTypes.put("xml", "text/xml");
		mimeTypes.put("txt", "text/plain");
		mimeTypes.put("css", "text/css");
		mimeTypes.put("js", "text/javascript");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("bmp", "image/bmp");
		mimeTypes.put("bm", "image/bmp");
	}
	
	@Handle(extensions = {".*"}, priority = Integer.MIN_VALUE)
	public static HTTPResponse handle(HTTPRequest request, HTTPResponse response, File webRoot) throws IOException {
		
		final String nl = System.lineSeparator();

		try {
			File file = new File(webRoot, request.url().substring(1));
			// if the requested path is a folder, try to get the 'index.html' file
			if(file.isDirectory() && new File(file, "index.html").exists()) {
				file = new File(file, "index.html");
			} else if(file.isDirectory()) {
				String body = "";
				String[] fileNames = file.list();
				for(String fileName : fileNames) {
					body += "<a href=\"" + request.url() + "/" + fileName + "\">" + fileName + "</a><br>";
				}
				
				response.body(body);
			}
			String content = new String(Files.readAllBytes(file.toPath()));
			
			String fileExtension = request.url().substring(request.url().lastIndexOf('.') + 1);
			if(mimeTypes.containsKey(fileExtension)) {
				response.header("Content-Type", mimeTypes.get(fileExtension));
			}
			
			response.body(content);
		}  catch(NoSuchFileException e) {
			response.statusCode(404).reason("Not Found").body("<h1>404 - Not Found</h1>");
		}
		
		System.out.println(response.body());
		
		return response.header("Content-Length", Integer.toString(response.body().length));
	}
}
