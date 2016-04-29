/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business;

import java.util.ArrayList;

/**
 *
 * @author tomant
 */
public class Manufacturer {
    
    protected int identifer;
    protected String name;
    protected ArrayList<Product> products;

    public Manufacturer(int identifer, String name) {
        this.identifer = identifer;
        this.name = name;
        this.products = new ArrayList();
    }

    public void addProduct(Product p) {
        this.products.add(p);
    }
    
}
