package com.traffic.utils;

public class StopWatch {
	private long startTime = System.currentTimeMillis();
	private long totalTime = 0;

	public StopWatch() {
		startTime = System.currentTimeMillis();
		totalTime = 0;
	}

	public void reset() {
		startTime = System.currentTimeMillis();
		totalTime = 0;
	}

	public String lap() {
		long endTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000;
		totalTime += endTimeInSeconds;
		long minutes = endTimeInSeconds / 60;
		long seconds = endTimeInSeconds % 60;
		startTime = System.currentTimeMillis();
		return "[That took :: " + minutes + " minutes " + seconds + " seconds]";
	}

	public String totalTime() {
		totalTime += (System.currentTimeMillis() - startTime) / 1000;
		long minutes = totalTime / 60;
		long seconds = totalTime % 60;
		startTime = System.currentTimeMillis();
		return "[Total time taken :: " + minutes + " minutes " + seconds + " seconds]";
	}
}
