package ch.hearc.ig.tb.odoosim.business;

import java.util.*;

public class Demand {

    private Good product;
    private int id;
    private int day;
    private Double quantity;
    private Exchange transaction;
    private Retailer owner;

    public Demand(Good product, int day, Double quantity) {
        this.product = product;
        this.day = day;
        this.quantity = quantity;
    }

    public int getId() {
        return this.id;
    }

    public Good getProduct() {
        return product;
    }

    public void setProduct(Good product) {
        this.product = product;
    }

    public Exchange getTransaction() {
        return transaction;
    }

    public void setTransaction(Exchange transaction) {
        this.transaction = transaction;
    }

    public Retailer getOwner() {
        return owner;
    }

    public void setOwner(Retailer owner) {
        this.owner = owner;
    }
    
    

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.product);
        hash = 89 * hash + Objects.hashCode(this.owner);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Demand other = (Demand) obj;
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        if (!Objects.equals(this.owner, other.owner)) {
            return false;
        }
        return true;
    }

}
