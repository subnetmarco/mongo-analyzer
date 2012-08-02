# Mongo Analyzer

This is Java library to analyze, and fix, inconsistencies in MongoDB references across multiple collections.

# Usage

```java
Mongo mongo = new Mongo("127.0.0.1");
DB database = mongo.getDB("mydatabase");

// Analyze the whole database, without fixing inconsitencies
new MongoConsistency().analyze(database);

// Analyze the whole database and fix inconsistencies by removing dead references
new MongoConsistency().analyzeAndUpdate(database);

// Analyze a specific collection, without fixing inconsitencies
new MongoConsistency().analyze("mycollection", database);

// Analyze a specific collection and fix inconsistencies by removing dead references
new MongoConsistency().analyzeAndUpdate("mycollection", database);

// Analyze the whole database, excluding the collections in the blacklist
List<String> blacklist = new ArrayList<String>();
blacklist.add("collection1");
blacklist.add("collection2");

new MongoConsistency().analyzeWithBlacklist(database, blacklist);

// Analyze the whole database, excluding the collections in the blacklist, and fix inconsistencies by removing dead references
List<String> blacklist = new ArrayList<String>();
blacklist.add("collection1");
blacklist.add("collection2");

new MongoConsistency().analyzeWithBlacklistAndUpdate(database, blacklist);
```

# Custom progress analyzer

By default `MongoConsistency` uses a simple implementation of `AnalyzerProgress` that outputs the results to `stdout` (that is `BasicAnalyzerProgress`). You can build your own implementation, like:

```
public class CustomProgress implements AnalyzerProgress {

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
```

And then initialize `MongoConsistency` like:

```
new MongoConsistency(new CustomProgress());
```
