package ch.hearc.ig.tb.odoosim.business;

import java.util.*;

public class Consumer {

	private Collection<Demand> demands;
	private int id;
	private String code;

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

}