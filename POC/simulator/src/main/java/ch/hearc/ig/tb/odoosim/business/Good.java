/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author tomant
 */
public class Good extends Product {
    
    private Collection<Offer> vendors;
    private Double indicativeSalePrice;
    //  Key = id Raw Material, Value = quantity of RM
    private Map<Integer, Double> billOfMaterials;
    
    public Good(String name) {
        super(name);
    }

    public Good(Double indicativeSalePrice, String code, String name, Double purchasePrice) {
        super(code, name, purchasePrice);
        this.vendors = new ArrayList<>();
        this.indicativeSalePrice = indicativeSalePrice;
        this.billOfMaterials = new HashMap();
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

    public Map getBillOfMaterials() {
        return billOfMaterials;
    }

    public void setBillOfMaterials(HashMap billOfMaterials) {
        this.billOfMaterials = billOfMaterials;
    }
    
    public void addBOM(int idProduct, Double quantity) {
        this.billOfMaterials.put(idProduct, quantity);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }
    
    @Override
    public boolean equals(Object good) {
        if(this == good)
            return true;
        if(this.getClass() != good.getClass())
            return false;
        
        Good gd = (Good) good;
        if((Objects.equals(this.getName(), gd.getName())) || (Objects.equals(this.getCode(), gd.getCode())))
            return true;
        else
            return false;
    }
    
}
