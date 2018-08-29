package com.traffic.map;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.traffic.model.Pair;
import com.traffic.model.Place;
import com.traffic.utils.DistanceUtil;
import com.traffic.utils.GoogleAPIsUtil;
import com.traffic.utils.URLBuilder;

public class SnappedPointsTask implements Callable<Integer> {

	private final Place source;
	private final Place dest;

	public SnappedPointsTask(Place source, Place dest) {
		this.source = source;
		this.dest = dest;
	}

	@Override
	public Integer call() throws Exception {
		return isNext(source, dest);
	}

	@SuppressWarnings("unchecked")
	public int isNext(Place source, Place dest) {
		if (source.getLat() == dest.getLat() && source.getLng() == dest.getLng()) {
			return 0;
		}
		if (DistanceUtil.getDistance(source, dest) > (0.2 * 1.0)) {
			return 0; // Return false if dist between two lat lng is greater than 2km
		}

		try {
			Map<String, Object> response = GoogleAPIsUtil
					.getResponse(URLBuilder.getSnapToRoadsURL(new Pair(source), new Pair(dest)));
			List<Object> snappedPoints = (List<Object>) response.get("snappedPoints");
			if (snappedPoints.size() == 2) {
				return 1;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return -1;
		}

		return 0;
	}
}
