package ch.hearc.ig.tb.odoosim.business;

import java.util.*;

public class Company {

    private Collection<Offer> offers;
    private int id;
    /**
     * This code is an business field. It takes the number of simulation with
     * the datetime and the number of the team
     */
    private String code;
    private String name;
    private String erp;
    private int uidapiaccess;
    private Collection<Collaborator> collaborators;
    private Banker banker;
    private Shareholder shareholder;

    public Company(String name) {
        this.name = name;
        this.collaborators = new ArrayList<>();
        this.offers = new ArrayList<>();
    }

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

    public Collection<Offer> getOffers() {
        return offers;
    }

    public void setOffers(Collection<Offer> offers) {
        this.offers = offers;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public int getUidapiaccess() {
        return uidapiaccess;
    }

    public void setUidapiaccess(int uidapiaccess) {
        this.uidapiaccess = uidapiaccess;
    }

    public Collection<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Collection<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public Banker getBanker() {
        return banker;
    }

    public void setBanker(Banker banker) {
        this.banker = banker;
    }

    public Shareholder getShareholder() {
        return shareholder;
    }

    public void setShareholder(Shareholder shareholder) {
        this.shareholder = shareholder;
    }
    
    public void addCollaborators(Collaborator c) {
        c.setCompany(this);
        this.collaborators.add(c);
    }
    
    public void addOffer(Offer o) {
        this.offers.add(o);
    }
}
