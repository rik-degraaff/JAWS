package jaws.net.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import jaws.business.http.HTTPRequest;
import jaws.business.http.HTTPResponse;

public class Handler {
	
	private Method method;
	
	private Handler(Method method) {
		
		this.method = method;
	}
	
	public Optional<Handler> from(Method method) {
		
		if(method.getParameterTypes() == new Class<?>[] {HTTPRequest.class, HTTPRequest.class, File.class}
				&& method.getReturnType() == HTTPResponse.class) {
			return Optional.of(new Handler(method));
		}
		return Optional.empty();
	}
	
	public HTTPResponse handle(HTTPRequest request, HTTPRequest response, File webroot) {
		
		try {
			return (HTTPResponse) method.invoke(null, request, response, webroot);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// meh
		}
		
		return null; // won't happen
	}
}
