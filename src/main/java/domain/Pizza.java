package domain;

import java.io.Serializable;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

public class Pizza extends BasicDBObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private ObjectId _id;
	private String name = "";
	private String ingredients = "";
	private Double price40 = 0.00;
	private Double price50 = 0.00;
	private Integer ver = 1;
	private Integer amount = 1;
	
	public Pizza() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIngredients() {
		return ingredients;
	}
	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}
	public Double getPrice40() {
		return price40;
	}
	public void setPrice40(Double price40) {
		this.price40 = price40;
	}
	public Double getPrice50() {
		return price50;
	}
	public void setPrice50(Double price50) {
		this.price50 = price50;
	}
	public Integer getVer() {
		return ver;
	}
	public void setVer(Integer ver) {
		this.ver = ver;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
}
