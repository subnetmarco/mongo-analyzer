package com.mashape.mongo.analyzer.mongoconsistency;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mashape.mongo.analyzer.progress.AnalyzerProgress;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class asd {

	public void b() throws UnknownHostException, MongoException {
		Mongo mongo = new Mongo("127.0.0.1");
		DB database = mongo.getDB("mydatabase");
		
		List<String> blacklist = new ArrayList<String>();
		blacklist.add("collection1");
		blacklist.add("collection2");
		new MongoConsistency(new CustomProgress()).analyze(database);
	}
	
	class CustomProgress implements AnalyzerProgress {

		@Override
		public void processing(long current, long total) {
			// This is triggered every time an item is being processed
			System.out.println("Processing " + current + " of " + total);
		}

		@Override
		public void error(String message) {
			// This is triggered when an error occurs
			throw new RuntimeException(message);
		}

		@Override
		public void info(String info) {
			// This is triggered when additional information are shown about the current operation
			System.out.println(info);
		}
	}
	
}
