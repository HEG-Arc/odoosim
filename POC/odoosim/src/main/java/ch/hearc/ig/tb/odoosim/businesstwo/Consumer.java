/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.businesstwo;

import java.util.ArrayList;

/**
 *
 * @author tomant
 */
public class Consumer {
    private int identifier;
    private String name;
    private ArrayList<Need> needs;
    private ArrayList<Exchange> transactions;
    private Region localisation;

    public Consumer(int identifier, String name, Region region) {
        this.identifier = identifier;
        this.name = name;
        localisation = region;
        needs = new ArrayList<>();
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
    
    public ArrayList<Need> getNeeds() {
        return needs;
    }

    public ArrayList<Exchange> getTransactions() {
        return transactions;
    }
    
    public void addNeed(Need need, Market m) {
        needs.add(need);
        m.setAllNeeds(need);
    }
    
    public void addTransaction(Exchange ex) {
        transactions.add(ex);
    }
}
