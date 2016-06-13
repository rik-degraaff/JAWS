package jaws.business.net;

import static trycrash.Try.tryCrash;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;

import jaws.module.http.HTTPRequest;
import jaws.module.http.HTTPResponse;

/**
 * A wrapper class to represent a Handler method and ease access to the containing method.
 * 
 * @author Roy
 *
 */
public class Handler {

	private Method method;

	private Handler(Method method) {

		this.method = method;
	}

	/**
	 * Tries to create and return a Handler from the specified method, returns an empty {@link java.util.Optional} if it fails.
	 * 
	 * @param method the Handler method.
	 * @return An {@link java.util.Optional} containing the Handler, or an empty {@link java.util.Optional} if the creation fails.
	 */
	public static Optional<Handler> from(Method method) {

		if(Arrays.equals(method.getParameterTypes(), new Class<?>[] {HTTPRequest.class, HTTPResponse.class, File.class})
				&& method.getReturnType() == HTTPResponse.class
				&& Modifier.isStatic(method.getModifiers())) {
			return Optional.of(new Handler(method));
		}
		return Optional.empty();
	}

	/**
	 * Invokes the containing method to let it handle the request.
	 * 
	 * @param request the request from the client.
	 * @param response a response for the Handler to modify and return.
	 * @param webroot the configured webroot.
	 * @return the modified response.
	 */
	public HTTPResponse handle(HTTPRequest request, HTTPResponse response, File webroot) {

		return tryCrash(() -> (HTTPResponse) method.invoke(null, request, response, webroot));
	}
}
