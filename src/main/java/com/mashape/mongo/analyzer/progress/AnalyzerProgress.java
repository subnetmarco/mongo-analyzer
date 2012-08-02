package com.mashape.mongo.analyzer.progress;

public interface AnalyzerProgress {

	void processing(long current, long total);
	
	void error(String message);
	
	void info(String info);
	
}
