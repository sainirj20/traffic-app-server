package com.traffic.congestion;

import java.util.Calendar;

import org.joda.time.Interval;

import com.traffic.dao.CongestionHistoryDao;
import com.traffic.model.Congestion;
import com.traffic.model.Congestion.AlertStatus;
import com.traffic.utils.DistanceUtil;
import com.traffic.model.Place;

public class CongestionManager {
	
	private CongestionHistoryDao cHistory = new CongestionHistoryDao();

	public void updateLastTime(Congestion congestion) {
		congestion.setLastUpdatedBy("Cron Job");
		congestion.setLastUpdatedTime(Calendar.getInstance().getTimeInMillis());
	}

	public void updateThePlace(Congestion congestion, Place place) {
		int placeIndex = congestion.getPlacesIds().indexOf(place.getPlaceId());
		congestion.getPlaces().remove(placeIndex);
		congestion.getPlaces().add(placeIndex, place);
		congestion.getPlacesIds().remove(placeIndex);
		congestion.getPlacesIds().add(placeIndex, place.getPlaceId());
	}

	public void updateCongestion(Congestion congestion) {
		if (congestion.getPlaces().size() == congestion.getResolvedPlaces().size()) {
			congestion.setStatus(AlertStatus.RESOLVED);
		}

		Place lastPlace = null;
		Place firstPlace = null;
		Integer congestionDistance = 100;
		Integer averageSpeed = 0;
		for (Place place : congestion.getPlaces()) {
			if (firstPlace == null) {
				firstPlace = place;
			}
			if (lastPlace != null) {
				congestionDistance += (int) DistanceUtil.getDistance(lastPlace, place);
			}
			lastPlace = place;

			Integer speed = place.getCurrentSpeed();
			averageSpeed += speed;
		}
		averageSpeed = averageSpeed / congestion.getPlaces().size();
		congestion.setCongestionDuration((int) new Interval(congestion.getStartTime(), congestion.getLastUpdatedTime())
				.toDuration().getStandardMinutes());
		Integer delayCaused = (int) (((float) congestionDistance / (float) (1000 * averageSpeed)) * 60);
		updateSeverityLevel(congestion, delayCaused);

		isCongestionUsusal(congestion);
		congestion.setDelayCaused(delayCaused);
		congestion.setCongestionDistance(congestionDistance);
		congestion.setAverageSpeed(averageSpeed);
		congestion.setLastPlace(lastPlace);

		boolean firstPlaceUpdated = true;
		if (firstPlace.equals(congestion.getFirstPlace())) {
			firstPlaceUpdated = false;
		}
		congestion.setFirstPlace(firstPlace);
		congestion.setShortLat(String.valueOf(((int) (firstPlace.getLat() * 10)) / 10.0));
		congestion.setShortLong(String.valueOf(((int) (firstPlace.getLng() * 10)) / 10.0));
		congestion.setLatValue(firstPlace.getLat());
		congestion.setLngValue(firstPlace.getLng());

		if (firstPlaceUpdated || congestion.getAddress() == null || congestion.getAddress().trim().equals("")) {
			congestion.setAddress(firstPlace.getAddress());
		}
	}

	private void updateSeverityLevel(Congestion congestion, Integer delayCaused) {
		Integer severityLevel = delayCaused / 10; // TODO Add congestion duration as a parameter
		if (severityLevel > 5) {
			severityLevel = 5;
		}
		congestion.setSeverityLevel(severityLevel);
		congestion.setDelayCaused(delayCaused);
	}
	
	private void isCongestionUsusal(Congestion congestion) {
		Integer usual = 0, unUsual = 0;
		for (Place place : congestion.getPlaces()) {
			if (congestion.getResolvedPlaces().contains(place.getPlaceId())) {
				continue;
			}
			if (cHistory.containsPlaceId(place.getPlaceId())) {
				usual++; // Usual place
				System.out.println("Usual Place " + place.getPlaceId());
			} else {
				unUsual++;
				System.out.println("Unusual Place " + place.getPlaceId());
			}
		}
		congestion.setIsUsualCongestion(unUsual < usual);
	}

}
