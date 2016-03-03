package jaws.module.standard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import jaws.business.http.HTTPRequest;
import jaws.business.http.HTTPResponse;
import jaws.module.base.Handle;

public class DefaultHandler {
	
	@Handle(extensions = {".*"}, priority = Integer.MIN_VALUE)
	public HTTPResponse handle(HTTPRequest request, HTTPResponse response, File webRoot) {
		
		final String nl = System.lineSeparator();
		
		File file = new File(webRoot, request.url().substring(1));
		try {
			List<String> contentLines = Files.readAllLines(file.toPath());
			String content = "";
			for(String line : contentLines) {
				content += line + nl;
			}
			return response.body(content.substring(0, Math.max(0, content.length() - nl.length())));
		} catch (IOException e) {
			return response.statusCode(500)
			               .reason("Internal Server Error");
		}
	}
}
