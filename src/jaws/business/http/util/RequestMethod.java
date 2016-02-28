package jaws.business.http.util;

import java.util.Optional;

public enum RequestMethod {

	GET("GET"),
	POST("POST"),
	HEAD("HEAD"),
	OPTIONS("OPTIONS");

	private String name;

	private RequestMethod(String name) {

		this.name = name;
	}

	public String toString() {

		return this.name;
	}
}