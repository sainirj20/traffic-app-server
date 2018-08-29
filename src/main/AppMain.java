package main;

import java.io.IOException;

import com.traffic.congestion.Processor;
import com.traffic.utils.StopWatch;

public class AppMain {

	public static void main(String[] args) throws IOException {
		StopWatch stopWatch = new StopWatch();
		new Processor().process();
		System.out.println(stopWatch.totalTime());
	}

}
