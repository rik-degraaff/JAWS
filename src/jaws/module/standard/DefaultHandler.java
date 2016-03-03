package jaws.module.standard;

import java.io.File;

import jaws.business.http.HTTPRequest;
import jaws.business.http.HTTPResponse;
import jaws.module.base.Handle;

public class DefaultHandler {
	
	@Handle(extensions = {"*"}, priority = Integer.MIN_VALUE)
	public HTTPResponse handle(HTTPRequest request, HTTPResponse response, File webRoot) {
		
		File file = new File(webRoot, request.url().substring(1));
		
	}
}
