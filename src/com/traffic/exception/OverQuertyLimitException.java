package com.traffic.exception;

import java.io.IOException;
import java.net.URL;

public class OverQuertyLimitException extends IOException {

	private static final long serialVersionUID = -5652438122459013543L;
	
	public OverQuertyLimitException(String response, URL url) {
		super("OVER_QUERY_LIMIT : " + response + " : " + url);
	}

}
