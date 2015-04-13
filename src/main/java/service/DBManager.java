package service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;

import domain.Order;
import domain.Pizza;
import domain.User;

public class DBManager {

	
	// connect
	public static DB connect() throws UnknownHostException {
		DB db = (new MongoClient("localhost", 27017)).getDB("pv");
		return db;
	}
	
	
	// insert user
	public void insertUser(User user) throws UnknownHostException {
		DB db = connect();
		DBCollection coll = db.getCollection("users");
		BasicDBObject element = new BasicDBObject();
		element.put("_id", user.get_id());
		element.put("login", user.getLogin());
		element.put("password", user.getPassword());
		element.put("repassword", user.getRepassword());
		element.put("useable", user.getUseable());
		element.put("type", user.getType());
		coll.insert(element);
	}
	
	// find user by login
	public User findUser(User userfound) throws UnknownHostException {
		DB db = connect();
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject();
		query.put("login", userfound.getLogin());
		userfound.setLogin("");
		DBCursor cursor = coll.find(query).limit(1);
		while (cursor.hasNext()) {
			cursor.next();
			userfound.set_id((ObjectId)cursor.curr().get("_id"));
			userfound.setLogin((String)cursor.curr().get("login"));
			userfound.setPassword((String)cursor.curr().get("password"));
			userfound.setRepassword((String)cursor.curr().get("repassword"));
			userfound.setUseable((Boolean)cursor.curr().get("useable"));
			userfound.setType((String)cursor.curr().get("type"));
		}
		cursor.close();
		return userfound;
	}
	
	// find user by login and password
	public User findUser(String login, String password) throws UnknownHostException {
		DB db = connect();
		User userfound  = new User();
		DBCollection coll = db.getCollection("users");
		BasicDBObject query = new BasicDBObject();
		query.put("login", login);
		query.put("password", password);
		DBCursor cursor = coll.find(query).limit(1);
		while (cursor.hasNext()) {
			cursor.next();
			userfound.setLogin((String)cursor.curr().get("login"));
			userfound.setPassword((String)cursor.curr().get("password"));
			userfound.setRepassword((String)cursor.curr().get("repassword"));
			userfound.setUseable((Boolean)cursor.curr().get("useable"));
			userfound.setType((String)cursor.curr().get("type"));
		}
		cursor.close();
		return userfound;
	}
	
	// find all pizzas
	public List<Pizza> findPizzas() throws UnknownHostException {
		DB db = connect();
		List<Pizza> pizzas = new ArrayList<Pizza>();
		Pizza pizza;
		DBCollection coll = db.getCollection("pizzas");
		DBCursor cursor = coll.find();
		while (cursor.hasNext()) {
			pizza = new Pizza();
			cursor.next();
			pizza.set_id((ObjectId)cursor.curr().get("_id"));
			pizza.setName((String)cursor.curr().get("name"));
			pizza.setIngredients((String)cursor.curr().get("ingredients"));
			pizza.setPrice40((Double)cursor.curr().get("price40"));
			pizza.setPrice50((Double)cursor.curr().get("price50"));
			pizzas.add(pizza);
		}
		cursor.close();
		return pizzas;
	}
	
	// find order by owner and status
	public Order findOrder(Order order) throws UnknownHostException {
		DB db = connect();
		DBCollection coll = db.getCollection("orders");
		BasicDBObject query = new BasicDBObject();
		query.put("owner", order.getOwner());
		query.put("status", order.getStatus());
		DBCursor cursor = coll.find(query);
		while (cursor.hasNext()) {
			cursor.next();
			order.set_id((ObjectId)cursor.curr().get("_id"));
			order.setOrder((List<Pizza>)cursor.curr().get("order"));
		}
		cursor.close();
		return order;
	}
	
	// upsert order
	public void upsertOrder(Order order) throws UnknownHostException {
		DB db = connect();
		DBCollection coll = db.getCollection("orders");
		BasicDBObject query = new BasicDBObject();
		query.put("owner", order.getOwner());
		query.put("status", order.getStatus());
		BasicDBObject newDocument = new BasicDBObject();
		
		newDocument.append("$set", new BasicDBObject().append("order", order.getOrder()));
		coll.update(query, newDocument, true, false);
	}
}
