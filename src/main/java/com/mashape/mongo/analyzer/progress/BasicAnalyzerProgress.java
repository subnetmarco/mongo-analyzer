package com.mashape.mongo.analyzer.progress;

public class BasicAnalyzerProgress implements AnalyzerProgress {

	private static int messageLength = 0;
	
	@Override
	public void processing(long current, long total) {
		for(int i=1;i<= messageLength;i++) {
			System.out.print("\b");
		}
		String message = "* Processing " + current + " of " + total;
		messageLength = message.length();
		System.out.print(message);
	}

	@Override
	public void error(String message) {
		System.err.println("\n" + message);
	}

	@Override
	public void info(String info) {
		System.out.println("\n" + info);
	}
	
}
