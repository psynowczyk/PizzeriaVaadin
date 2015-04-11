package domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.bson.types.ObjectId;

public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private ObjectId _id = new ObjectId();
	@Size(min=3,max=10)
	@NotNull
	private String login = "";
	@Size(min=3,max=10)
	@NotNull
	private String password = "";
	@Size(min=3,max=10)
	@NotNull
	private String repassword = "";
	private String type = "user";
	private Boolean useable = true;
	
	public User() {
		super();
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
	public String getRepassword() {
		return repassword;
	}
	public void setRepassword(String repassword) {
		this.repassword = repassword;
	}
	public Boolean getUseable() {
		return useable;
	}
	public void setUseable(Boolean useable) {
		this.useable = useable;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
