# Mongo Analyzer

This is a Java library to discover, and fix, MongoDB inconsistencies in references across multiple collections. It can just analyze the database and report inconsistencies, or optionally fix them automatically by removing dead references.

# Maven

You can use it with Maven:

```xml
<repository>
    <id>mashape-snapshots</id>
    <url>https://github.com/Mashape/MVNRepo/raw/master/snapshots</url>
</repository>

<dependency>
    <groupId>com.mashape.mongo.analyzer</groupId>
    <artifactId>mongo-analyzer</artifactId>
    <version>1.0</version>
</dependency>
```

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
new MongoConsistency().analyzeWithBlacklistAndUpdate(database, blacklist);
```

# Custom progress analyzer

By default `MongoConsistency` uses a simple implementation of `AnalyzerProgress` that outputs the results to `stdout` (that is `BasicAnalyzerProgress`). You can build your own implementation, like:

```java
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

```java
new MongoConsistency(new CustomProgress());
```
