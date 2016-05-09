/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hearc.ig.tb.odoosim.business.economics;

/**
 *
 * @author tomant
 */
public class Offer implements Comparable<Offer> {
    private Good good;
    private int quantity;
    private Double price;

    public Offer(Good good, int quantity, Double price) {
        this.good = good;
        this.quantity = quantity;
        this.price = price;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public int compareTo(Offer o) {
        if(this.price>o.price)
            return 1;
        else if(this.price==o.price) {
            if(this.quantity>o.quantity)
                return 1;
            else if(this.quantity==o.quantity)
                return 0;
            else
                return -1;
        }
        else
            return -1;
    }
    
}
