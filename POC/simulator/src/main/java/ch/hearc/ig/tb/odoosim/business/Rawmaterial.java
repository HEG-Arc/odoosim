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
public class Rawmaterial extends Product {
    
    private Supplier supplier;

    public Rawmaterial(String code, String name, Double purchasePrice) {
        super(code, name, purchasePrice);
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
}
