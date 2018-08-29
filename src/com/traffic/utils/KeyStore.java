package com.traffic.utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class KeyStore {

	private final int size = 4;
	private final BasicNameValuePair[] keys = new BasicNameValuePair[size];

	private int pointer = 0;

	KeyStore() {
		keys[0] = new BasicNameValuePair("key", "AIzaSyCd3gQRLIhHL7RPXWuMp2xwv3qlv662h7k");
		keys[1] = new BasicNameValuePair("key", "AIzaSyB7Qbh8p3IbWZMGnKKRZ-oDpCBPsDHzQL0");
		keys[2] = new BasicNameValuePair("key", "AIzaSyCRIN3hdePOGMQ4bx6h5OwnzX0Fde-jWMI");
		keys[3] = new BasicNameValuePair("key", "AIzaSyD3BzMVeXweNpE6ah60r0kr3FmI2ZTIYAc");
	}

	public synchronized NameValuePair getKey() {
		NameValuePair key = keys[pointer];
		pointer = (pointer + 1) % size;
		return key;
	}
}
