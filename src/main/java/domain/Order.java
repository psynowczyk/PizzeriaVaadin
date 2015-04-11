package domain;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

public class Order {
	
	private ObjectId _id;
	private ObjectId owner;
	private List<PizzaExt> order = new ArrayList<PizzaExt>();
	
	public Order() {
		super();
	}
	
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public ObjectId getOwner() {
		return owner;
	}
	public void setOwner(ObjectId owner) {
		this.owner = owner;
	}
	public List<PizzaExt> getOrder() {
		return order;
	}
	public void setOrder(List<PizzaExt> order) {
		this.order = order;
	}
	
}
