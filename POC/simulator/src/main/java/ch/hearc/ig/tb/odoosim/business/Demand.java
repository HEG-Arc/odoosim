package ch.hearc.ig.tb.odoosim.business;

import java.util.*;

public class Demand {

	private Product product;
	private int id;
	private int day;
	private int quantity;
	private Collection<Exchange> exchanges;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDay() {
		return this.day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}