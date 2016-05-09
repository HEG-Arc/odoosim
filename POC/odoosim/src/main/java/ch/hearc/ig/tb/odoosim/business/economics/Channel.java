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
public abstract class Channel {
    protected String name;
    protected int paymentTerms;
    protected ArrayList<Exchange> transactions;

    public Channel(String name, int paymentTerms) {
        this.name = name;
        this.paymentTerms = paymentTerms;
        transactions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(int paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public ArrayList<Exchange> getTransactions() {
        return transactions;
    }

    public void setTransactions(Exchange ex) {
        transactions.add(ex);
    }
    
    
}
