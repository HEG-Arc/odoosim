package ch.hearc.ig.tb.odoosim.business;

import java.util.*;

public class Collaborator {

	private Company company;
	private int id;
	private String name;
	private String mail;
	private String password;

        public Collaborator(String name) {
            this.company = null;
            this.id = 0;
            this.name = name;
            this.mail = "";
            this.password = "";
        }
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return this.mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
        
        

}