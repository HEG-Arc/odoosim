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
public class Product {
    protected int identifer;
    protected String name;
    protected Double price;
    protected Manufacturer vendor;

    public Product(int identifer, String name, Double price, Manufacturer vendor) {
        this.identifer = identifer;
        this.name = name;
        this.price = price;
        this.vendor = vendor;
    }

    
}
