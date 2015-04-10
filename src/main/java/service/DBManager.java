package service;

import java.net.UnknownHostException;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import domain.User;

public class DBManager {

	// connect
	public static MongoClient connect() throws UnknownHostException {
		//@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		return mongoClient;
	}
	
	// insert user
	public void insertUser(User user) throws UnknownHostException {
		MongoClient mongoClient = connect();
		MongoDatabase db = mongoClient.getDatabase("pv");
		MongoCollection<Document> coll = db.getCollection("users");
		Document element = new Document();
		element.put("_id", user.get_id());
		element.put("login", user.getLogin());
		element.put("password", user.getPassword());
		coll.insertOne(element);
		mongoClient.close();
	}
	
	// find user
	public User findUser(String login) throws UnknownHostException {
		User userfound = new User();
		MongoClient mongoClient = connect();
		MongoDatabase db = mongoClient.getDatabase("pv");
		MongoCollection<Document> coll = db.getCollection("users");
		FindIterable<Document> cursor = coll.find(new BasicDBObject("login", login)).limit(1);
		if (cursor.iterator().hasNext()) {
			userfound.setLogin(cursor.iterator().next().getString("login"));
			userfound.setPassword(cursor.iterator().next().getString("password"));
		}
		mongoClient.close();
		return userfound;
	}
	
}
