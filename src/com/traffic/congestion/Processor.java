package com.traffic.congestion;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.traffic.dao.CongestionDao;
import com.traffic.dao.PlacesDao;
import com.traffic.map.GraphGenerator;
import com.traffic.map.PlacesGenerator;
import com.traffic.model.Congestion;
import com.traffic.model.Place;

public class Processor {
	final PlacesDao placesDao;
	final CongestionDao congestionDao;
	final Map<String, Congestion> congestionsFromDB;
	
	final PlacesGenerator placesGenerator = new PlacesGenerator();
	final GraphGenerator graphGenerator = new GraphGenerator();

	public Processor() {
		placesDao = new PlacesDao();
		congestionDao = new CongestionDao();
		congestionsFromDB = congestionDao.getAll();
	}

	public void process() throws IOException {
		Map<String, Place> placesMap = placesGenerator.generatePlaces();

//		for (Entry<String, Place> entry : placesMap.entrySet()) {
//			Place place = entry.getValue();
//			if (place.isPlaceCongested()) {
//				yes(place);
//			} else {
//				no(place);
//			}
//		}
//		System.out.println("Congestion size :: " + congestionsFromDB.size());
	}

	private void yes(Place place) {
		boolean isNewCongestedPlace = true;
		for (Congestion congestion : congestionsFromDB.values()) {
			boolean congestionUpdated = true;
			if (congestion.contains(place)) {
				// do stuff
			} 
//			else if (graphGenerator.areConsecutivePlaces(congestion.getFirstPlace(), place)) {
//				congestion.addFirst(place);
//				// updateThePlace check
//				// congestion utils stuff
//			} else if (graphGenerator.areConsecutivePlaces(congestion.getLastPlace(), place)) {
//				congestion.addLast(place);
//				// congestion utils stuff
//			} 
			else {
				congestionUpdated = false;
			}

			if (congestionUpdated) {
				isNewCongestedPlace = false;
				//congestionDao.addOrUpdate(congestion);
			}
		}

		if (isNewCongestedPlace) {
			Congestion newCongestion = new Congestion(place);
			// updateThePlace check
			// congestion utils stuff
			//congestionDao.addOrUpdate(newCongestion);
			congestionsFromDB.put(newCongestion.getCongestionId(), newCongestion);
		}

	}

	private void no(Place place) {
		// Add this place to resolved congestion if it is a part of existing congestion
		for (Congestion congestion : congestionsFromDB.values()) {
			if (congestion.contains(place)) {
				// updateThePlace
				congestion.addToResolved(place);
			}
		}
	}
}