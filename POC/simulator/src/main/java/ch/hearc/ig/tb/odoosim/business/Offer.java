package ch.hearc.ig.tb.odoosim.business;

import java.util.*;

public class Offer implements Comparable<Offer>{
    private Company owner;
    private Good product;
    private int id;
    private int day;
    private Double quantity;
    private Double price;
    
    public Offer(Good product, Company owner) {
        this.product = product;
        this.owner = owner;
        //exchanges = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Company getOwner() {
        return owner;
    }

    public void setOwner(Company owner) {
        this.owner = owner;
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

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Good getProduct() {
        return product;
    }

    public void setProduct(Good product) {
        product.addOffer(this);
        this.product = product;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.owner);
        hash = 11 * hash + Objects.hashCode(this.product);
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
        final Offer other = (Offer) obj;
        if (!Objects.equals(this.owner, other.owner)) {
            return false;
        }
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Offer o) {
        if(this.price> o.price)
            return 1;
        else if (this.price == o.price) {
            if(this.quantity>o.quantity)
                return 1;
            else if (this.quantity == o.quantity)
                return 0;
            else
                return -1;
        } else
            return -1;
    }

}
