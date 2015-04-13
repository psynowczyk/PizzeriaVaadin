package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

public class Order extends BasicDBObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private ObjectId _id = new ObjectId();
	private ObjectId owner;
	private List<Pizza> order = new ArrayList<Pizza>();
	private String status = "incomplete";
	private Double OrderPrice = new Double(0.00);
	
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
	public List<Pizza> getOrder() {
		return order;
	}
	public void setOrder(List<Pizza> order) {
		this.order = order;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Double getOrderPrice() {
		return OrderPrice;
	}
	public void setOrderPrice(Double orderPrice) {
		OrderPrice = orderPrice;
	}
}
