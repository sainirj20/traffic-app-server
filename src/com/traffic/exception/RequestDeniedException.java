package com.traffic.exception;

import java.io.IOException;
import java.net.URL;

public class RequestDeniedException extends IOException {
	private static final long serialVersionUID = 4322236695466492592L;

	public RequestDeniedException(String response, URL url) {
		super("REQUEST_DENIED : " + response + " : " + url);
	}
}
