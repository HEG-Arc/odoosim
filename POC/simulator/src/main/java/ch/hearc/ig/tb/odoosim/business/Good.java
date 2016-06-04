/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author tomant
 */
public class Good extends Product {
    
    private Collection<Offer> vendors;
    private Double indicativeSalePrice;

    public Good(Double indicativeSalePrice, String code, String name, Double purchasePrice) {
        super(code, name, purchasePrice);
        this.vendors = new ArrayList<>();
        this.indicativeSalePrice = indicativeSalePrice;
    }

    public Collection<Offer> getVendors() {
        return vendors;
    }

    public void setVendors(Collection<Offer> vendors) {
        this.vendors = vendors;
    }

    public Double getIndicativeSalePrice() {
        return indicativeSalePrice;
    }

    public void setIndicativeSalePrice(Double indicativeSalePrice) {
        this.indicativeSalePrice = indicativeSalePrice;
    }
    
}
