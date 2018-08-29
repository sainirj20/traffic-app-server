package com.traffic.map;

import java.util.Map;
import java.util.concurrent.Callable;

import com.traffic.model.Place;
import com.traffic.utils.GoogleAPIsUtil;
import com.traffic.utils.URLBuilder;

public class PlaceDetailsTask implements Callable<Place> {
	Place place = null;

	public PlaceDetailsTask(Place place) {
		this.place = place;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Place call() throws Exception {
		if (null == place) {
			return null;
		}
		try {
			Map<String, Object> jsonFromUrl = GoogleAPIsUtil.getResponse(URLBuilder.getPlaceApiURL(place.getPlaceId()));
			Map<String, Object> result = (Map<String, Object>) jsonFromUrl.get("result");
			Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
			Map<String, Number> location = (Map<String, Number>) geometry.get("location");

			place.setLat(Double.parseDouble(location.get("lat").toString()));
			place.setLng(Double.parseDouble(location.get("lng").toString()));
			place.setAddress((String) result.get("formatted_address"));
		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + " :: " + e.getMessage());
			return null;
		}
		return this.place;
	}
}
