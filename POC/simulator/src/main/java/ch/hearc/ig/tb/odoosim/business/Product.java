package ch.hearc.ig.tb.odoosim.business;

import java.util.*;

public class Product {

	private Collection<Offer> suppliers;
	private int id;
	private String code;
	private String name;
	private Double indicativePrice;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getIndicativePrice() {
		return this.indicativePrice;
	}

	public void setIndicativePrice(Double indicativePrice) {
		this.indicativePrice = indicativePrice;
	}

}