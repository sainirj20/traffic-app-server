package com.traffic.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.exception.InvalidArgumentException;
import com.traffic.exception.OverQuertyLimitException;
import com.traffic.exception.RequestDeniedException;

/** utility class for Google API */
public class GoogleAPIsUtil {

	private static ObjectMapper objectMapper = new ObjectMapper();
	private static TypeReference<Map<String, Object>> ref = new TypeReference<Map<String, Object>>() {
	};

	private GoogleAPIsUtil() {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static Map<String, Object> getResponse(URL url) throws IOException {

		Map<String, Object> response = null;
		response = objectMapper.readValue(url, ref);
		String status = (String) response.get("status");

		switch (status != null ? status : "") {
		case "REQUEST_DENIED":
			throw new RequestDeniedException((String) response.get("error_message"), url);
		case "OVER_QUERY_LIMIT":
			throw new OverQuertyLimitException((String) response.get("error_message"), url);
		case "INVALID_ARGUMENT":
			throw new InvalidArgumentException((String) response.get("error_message"), url);
		default:
			break;
		}

		return response;
	}

}
