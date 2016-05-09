/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business.economics;

import java.util.ArrayList;

/**
 *
 * @author tomant
 */
public class Vendor {
    private int identifier;
    private String name;
    private Region localisation;
    private ArrayList<Offer> warehouses;
    private ArrayList<Exchange> transactions;

    public Vendor(int identifier, String name, Region region) {
        this.identifier = identifier;
        this.name = name;
        localisation = region;
        warehouses = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Region getLocalisation() {
        return localisation;
    }

    public void setLocalisation(Region localisation) {
        this.localisation = localisation;
    }
    
    public ArrayList<Offer> getWarehouses() {
        return warehouses;
    }

    public ArrayList<Exchange> getTransactions() {
        return transactions;
    }

    public void addOffer(Offer offer, Market m) {
        warehouses.add(offer);
        m.setAllOffers(offer);
    }
    
    public void addTransaction(Exchange ex) {
        transactions.add(ex);
    }
}
