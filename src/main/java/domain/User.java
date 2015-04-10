package domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.bson.types.ObjectId;

public class User implements Serializable {
	private ObjectId _id;
	@Size(min=3,max=10)
	@NotNull
	private String login = "";
	@Size(min=3,max=10)
	@NotNull
	private String password = "";
	
	public User() {
	}
	
	public User(String login, String password) {
		super();
		this.login = login;
		this.password = password;
	}
	
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
