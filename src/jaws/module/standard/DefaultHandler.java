package jaws.module.standard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;

import jaws.business.http.HTTPRequest;
import jaws.business.http.HTTPResponse;
import jaws.module.base.Handle;

public class DefaultHandler {
	
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
				return response.body(body);
			}
			List<String> contentLines = Files.readAllLines(file.toPath());
			String content = "";
			for(String line : contentLines) {
				content += line + nl;
			}
			return response.body(content.substring(0, Math.max(0, content.length() - nl.length())));
		}  catch(NoSuchFileException e) {
			return response.statusCode(404).reason("Not Found").body("<h1>404 - Not Found</h1>");
		}
	}
}
