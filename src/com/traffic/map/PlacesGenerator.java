package com.traffic.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.traffic.dao.PlacesDao;
import com.traffic.model.Place;
import com.traffic.utils.GoogleAPIsUtil;
import com.traffic.utils.StopWatch;
import com.traffic.utils.URLBuilder;

public class PlacesGenerator {
	private Map<String, Place> placesMap = new LinkedHashMap<String, Place>();

	@SuppressWarnings("unchecked")
	private void fetchFreeflowSpeed() throws IOException {
		Map<String, Object> response = GoogleAPIsUtil
				.getResponse(URLBuilder.getRoadApiURL(732, 474, URLBuilder.RoadApi.freeFlow));

		List<String> placeIds = (List<String>) response.get("placeIds");
		List<Integer> freeFlowSpeeds = (List<Integer>) response.get("freeflowSpeeds");
		for (int index = 0; index < placeIds.size(); index++) {
			String placeId = placeIds.get(index);
			if (placesMap.containsKey(placeId)) {
				Place place = placesMap.get(placeId);
				place.setFreeFlowSpeed(freeFlowSpeeds.get(index));
			} else {
				Place place = new Place(placeId);
				place.setFreeFlowSpeed(freeFlowSpeeds.get(index));
				placesMap.put(place.getPlaceId(), place);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void fetchCurrentSpeeds() throws IOException {
		Map<String, Object> response = GoogleAPIsUtil
				.getResponse(URLBuilder.getRoadApiURL(732, 474, URLBuilder.RoadApi.current));

		List<String> placeIds = (List<String>) response.get("placeIds");
		List<Integer> currentSpeeds = (List<Integer>) response.get("currentSpeeds");
		for (int index = 0; index < placeIds.size(); index++) {
			String placeId = placeIds.get(index);
			if (placesMap.containsKey(placeId)) {
				Place place = placesMap.get(placeId);
				if (place == null) {
					System.out.println(placeId);
				}
				place.setCurrentSpeed(currentSpeeds.get(index));
			} else {
				Place place = new Place(placeId);
				place.setCurrentSpeed(currentSpeeds.get(index));
				placesMap.put(place.getPlaceId(), place);
			}
		}
	}

	private void fetchPlacesDetails() {
		final PlacesDao placesDao = new PlacesDao();

		final int threadPoolSize = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
		List<Callable<Place>> callables = new ArrayList<Callable<Place>>();
		List<Future<Place>> futures = null;

		int threadCtr = 0, index = 0;
		for (Entry<String, Place> entry : placesMap.entrySet()) {
			Place place = entry.getValue();
			try {
				Place placeFromDB = placesDao.getByPlaceId(place.getPlaceId());
				placeFromDB.copySpeedDetails(place);
				if (!placeFromDB.hasLocationDetails()) {
					if (threadCtr < threadPoolSize) {
						callables.add(new PlaceDetailsTask(placeFromDB));
						threadCtr++;
					}
				}
				if (threadCtr == threadPoolSize || (threadCtr > 0 && index == placesMap.size() - 1)) {
					System.out.println("execute batch");
					futures = executorService.invokeAll(callables);
					for (Future<Place> future : futures) {
						Place placeFuture = future.get();
						if (null != placeFuture) {
							placesDao.addOrUpdate(placeFuture);
						}
					}
					callables.clear();
					threadCtr = 0;
				}
				placesDao.addOrUpdate(placeFromDB);
			} catch (Exception e) {
				System.out.println("Error at: " + place.getPlaceId() + "  " + e.getMessage());
			}
			index++;
		}
		executorService.shutdown();
	}

	public Map<String, Place> generatePlaces() throws IOException {
		StopWatch stopWatch = new StopWatch();
		fetchFreeflowSpeed();
		System.out.println("FreeflowSpeed fetched :: " + stopWatch.lap());
		fetchCurrentSpeeds();
		System.out.println("CurrentSpeeds fetched :: " + stopWatch.lap());
		fetchPlacesDetails();
		System.out.println("Places Generated :: " + placesMap.size() + " :: " + stopWatch.totalTime());
		return placesMap;
	}
}
