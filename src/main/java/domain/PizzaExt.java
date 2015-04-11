package domain;

public class PizzaExt extends Pizza {
	
	private int ver = 1;
	private int amount = 0;
	
	public PizzaExt() {
		super();
	}
	
	public int getVer() {
		return ver;
	}
	public void setVer(int ver) {
		this.ver = ver;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
}
