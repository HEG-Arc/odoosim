/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business;

/**
 *
 * @author tomant
 */
public class BusinessToBusiness extends Exchange {

    public BusinessToBusiness(Consumer purchaser, Offer vendor, int quantity, double price) {
        super(purchaser, vendor, quantity, price);
        this.tva = 1.08;
    }
    
    @Override
    protected void calculAmount() {
        amount = (quantity * price) * tva;
    }
    
}
