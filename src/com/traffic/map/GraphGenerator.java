package com.traffic.map;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.traffic.dao.PlaceIdGraphDao;
import com.traffic.model.Place;
import com.traffic.model.PlaceIdNode;

public class GraphGenerator {

	private PlaceIdGraphDao dao = new PlaceIdGraphDao();

	private void taskRunner(PlaceIdNode node, Place beforePlace, Place afterPlace)
			throws InterruptedException, ExecutionException {
		final int threadPoolSize = 2;
		ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
		List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
		List<Future<Integer>> futures = null;

		callables.add(new RoutesTask(beforePlace, afterPlace));
		callables.add(new SnappedPointsTask(beforePlace, afterPlace));

		futures = executorService.invokeAll(callables);
		for (Future<Integer> future : futures) {
			Integer result = future.get();
			if (1 == result) {
				node.addNext(afterPlace.getPlaceId());
			}
		}
		callables.clear();
	}

	public boolean areConsecutivePlaces(Place beforePlace, Place afterPlace) {
		if (null == beforePlace || null == afterPlace) {
			return false;
		}
		PlaceIdNode node = dao.getByPlaceId(beforePlace.getPlaceId());
		if (null == node) {
			node = new PlaceIdNode(beforePlace.getPlaceId());
		} else if (node.getProcessedIds().contains(afterPlace.getPlaceId())) {
			if (node.getNext().contains(afterPlace.getPlaceId())) {
				return false;
			}
			return false;
		}
		try {
			taskRunner(node, beforePlace, afterPlace);
		} catch (InterruptedException | ExecutionException e) {
			System.out.println(e.getMessage());
		}
		node.addProcessedIds(afterPlace.getPlaceId());
		dao.addOrUpdate(node);
		return false;
	}

}
