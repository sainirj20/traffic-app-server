package com.traffic.exception;

import java.io.IOException;
import java.net.URL;

public class InvalidArgumentException extends IOException {
	private static final long serialVersionUID = -5710798695975896459L;
	public InvalidArgumentException(String response, URL url) {
		super("INVALID_ARGUMENT : " + response + " : " + url);
	}

}
