package com.traffic.utils;

import com.traffic.model.Place;

public class DistanceUtil {

	public static double getDistance(Place p1, Place p2) {
		double lon1 = p1.getLng();
		double lat1 = p1.getLat();
		double lat2 = p2.getLat();
		double lon2 = p2.getLng();
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;
		return (dist);
	}

	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

	private DistanceUtil() {
	}
}
