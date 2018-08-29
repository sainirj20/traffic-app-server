package com.traffic.map;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.traffic.model.Place;
import com.traffic.utils.DistanceUtil;
import com.traffic.utils.GoogleAPIsUtil;
import com.traffic.utils.URLBuilder;

public class RoutesTask implements Callable<Integer> {
	private final Place source;
	private final Place dest;

	public RoutesTask(Place source, Place dest) {
		this.source = source;
		this.dest = dest;
	}

	@Override
	public Integer call() throws Exception {
		return isNext(source, dest);
	}

	@SuppressWarnings("unchecked")
	public int isNext(Place source, Place dest) {
		if (DistanceUtil.getDistance(source, dest) > (3 * 1.0)) {
			return 0;
		}
		List<Object> routes;
		try {
			Map<String, Object> response = GoogleAPIsUtil.getResponse(URLBuilder.getDirectionApi(source, dest));
			routes = (List<Object>) response.get("routes");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return -1; // need re-factor
		}

		for (Object obj : routes) {
			try {
				Map<String, Object> route = (Map<String, Object>) obj;
				List<Object> legs = (List<Object>) route.get("legs");
				Map<String, Object> leg = (Map<String, Object>) legs.get(0);
				List<Object> steps = (List<Object>) leg.get("steps");
				if (steps.size() == 1) {
					return 1;
				}
			} catch (Exception e) {
				return -1;
			}
		}
		return 0;
	}
}
