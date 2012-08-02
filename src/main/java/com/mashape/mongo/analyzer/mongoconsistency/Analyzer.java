package com.mashape.mongo.analyzer.mongoconsistency;

import java.util.Set;

import org.bson.types.ObjectId;

import com.mashape.mongo.analyzer.progress.AnalyzerProgress;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

class Analyzer {
	
	private AnalyzerProgress analyzerProgress;
	
	public Analyzer(AnalyzerProgress analyzerProgress) {
		this.analyzerProgress = analyzerProgress;
	}

	private boolean analyzeFields(DB database, Object parent, Object object) {
		boolean update = false;
		if (object instanceof DBObject) {
			Set<String> fieldNames = ((DBObject)object).keySet();
			for (String fieldName : fieldNames) {
				Object field = ((DBObject)object).get(fieldName);
				if (field instanceof BasicDBObject) {
					update = update || analyzeFields(database, object, field);
				} else if (field instanceof BasicDBList) {
					BasicDBList list = (BasicDBList) field;
					for (int i=0;i<list.size();i++) {
						Object child = list.get(i);
						update = update || analyzeFields(database, list, child);
					}
				} else if (field instanceof DBRef) {
					update = update || analyzeDbRef(database, parent, (DBRef) field);
				}
			}
		} else if (object instanceof DBRef) {
			update = update || analyzeDbRef(database, parent, (DBRef) object);
		}
		return update;
	}
	
	private boolean analyzeDbRef(DB database, Object parent, DBRef dbRef) {
		if (!exists(database, dbRef.getId().toString(), dbRef.getRef())) {
			analyzerProgress.info("Missing object \"" + dbRef.getId().toString() + "\" in \"" + dbRef.getRef() + "\".");
			if (parent instanceof BasicDBObject) {
				((BasicDBObject) parent).remove(dbRef);
			} else if (parent instanceof BasicDBList) {
				for (int i=0;i<((BasicDBList) parent).size();i++) {
					if (((BasicDBList) parent).get(i).equals(dbRef)) {
						((BasicDBList) parent).remove(i);
						break;
					}
				}
			} else {
				analyzerProgress.error("Unknown Parent: " + parent.getClass().getName());
			}
			return true;
		}
		return false;
	}
	
	public void analyzeCollection(boolean update, DB database, String collectionName) {
		analyzerProgress.info("Processing \"" + collectionName + "\"");
		DBCollection collection = database.getCollection(collectionName);
		DBCursor cursor = collection.find();
		long total = collection.count();
		long current = 0;
		while (cursor.hasNext()) {
			current++;
			analyzerProgress.processing(current, total);
			DBObject parent = cursor.next();
			if (analyzeFields(database, parent, parent)) {
				if (update) {
					analyzerProgress.info("Updating entity \"" + parent.get("_id") + "\" in \"" + collectionName + "\"");
					collection.save(parent);
				} else {
					analyzerProgress.info("Skipping update of entity \"" + parent.get("_id") + "\" in \"" + collectionName + "\"");
				}
			}
		}
	}
	
	private boolean exists(DB database, String id, String collectionName) {
		DBCollection plans = database
				.getCollection(collectionName);
		BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        DBObject findOne = plans.findOne(query);
        return findOne != null;
	}
	
}
