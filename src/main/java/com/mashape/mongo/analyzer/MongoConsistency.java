package com.mashape.mongo.analyzer;

import java.util.List;
import java.util.Set;

import com.mashape.mongo.analyzer.progress.AnalyzerProgress;
import com.mashape.mongo.analyzer.progress.BasicAnalyzerProgress;
import com.mongodb.DB;

/**
 * This is the main class that initializes different types of analysis on a Mongo database 
 * @author marco
 */

public class MongoConsistency {

	private Analyzer analyzer;
	
	/**
	 * @param analyzerProgress Implementation of AnalyzerProgress for tracking the progress of the operation. This library comes with a simple implementation called BasicAnalyzerProgress that outputs to stdout.
	 * @see AnalyzerProgress
	 * @see BasicAnalyzerProgress
	 */
	public MongoConsistency(AnalyzerProgress analyzerProgress) {
		this.analyzer = new Analyzer(analyzerProgress);
	}
	
	/**
	 * Uses BasicAnalyzerProgress by default for tracking the progress, and outputs to stdout.
	 * @see BasicAnalyzerProgress
	 */
	public MongoConsistency() {
		this(new BasicAnalyzerProgress());
	}
	
	/**
	 * Analyze the whole database and just notifies inconsistencies
	 */
	public void analyze(DB database) {
		analyzeCollections(database, false);
	}
	
	/**
	 * Analyze the whole database and automatically updates the database to fix inconsistencies
	 */
	public void analyzeAndUpdate(DB database) {
		analyzeCollections(database, true);
	}
	
	private void analyzeCollections(DB database, boolean update) {
		Set<String> collectionNames = database.getCollectionNames();
		
		for (String collectionName : collectionNames) {
			analyzer.analyzeCollection(update, database, collectionName);
		}
	}
	
	/**
	 * Analyze the database excluding the collection names in the blacklist
	 * @param collectionBlackList The collections to exclude
	 */
	public void analyzeWithBlacklist(DB database, List<String> collectionBlackList) {
		analyzeWithBlacklist(database, collectionBlackList, false);
	}
	
	private void analyzeWithBlacklist(DB database, List<String> collectionBlackList, boolean update) {
		Set<String> collectionNames = database.getCollectionNames();
		
		for (String collectionName : collectionNames) {
			if (!collectionBlackList.contains(collectionName)) {
				analyzer.analyzeCollection(update, database, collectionName);
			}
		}
	}
	
	/**
	 * Analyze the database excluding the collection names in the blacklist, and automatically updates the database to fix the inconsistencies
	 * @param collectionBlackList The collections to exclude
	 */
	public void analyzeWithBlacklistAndUpdate(DB database, List<String> collectionBlackList) {
		analyzeWithBlacklist(database, collectionBlackList, true);
	}
	
	/**
	 * Analyze only the specified collection and automatically updates it to fix the inconsistencies
	 */
	public void analyzeAndUpdate(String collectionName, DB database) {
		analyzer.analyzeCollection(true, database, collectionName);
	}
	
	/**
	 * Analyze only the specified collection without updating the database
	 */
	public void analyze(String collectionName, DB database) {
		analyzer.analyzeCollection(false, database, collectionName);
	}
	
}
