package war;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;

import com.vaadin.data.util.BeanItemContainer;

import domain.User;

public class UserManager {
	
	private List<User> db = new ArrayList<User>();
	
	// ADD USER
	public void addUser(User user) {
		User newuser = new User(user.getLogin(), user.getPassword());
		newuser.set_id(new ObjectId());
		db.add(newuser);
	}
	
	// FIND USER
	public boolean findUser(String login) {
		boolean result = false;
		for (User u: db) {
			if (u.getLogin().compareTo(login) == 0) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	// GET USER FROM DB
	public User getUser(String login) {
		User result = new User();
		for (User u: db) {
			if (u.getLogin().compareTo(login) == 0) {
				result.setLogin(login);
				result.setPassword(u.getPassword());
				result.set_id(u.get_id());
				break;
			}
		}
		return result;
	}
	
	// FIND ALL
	public List<User> findAll() {
		return db;
	}
	
	// DELETE USER
	public void delete(User user) {
		User toRemove = null;
		for (User u: db) {
			if (u.get_id().compareTo(user.get_id()) == 0) {
				toRemove = u;
				break;
			}
		}
		db.remove(toRemove);
	}
	
}
