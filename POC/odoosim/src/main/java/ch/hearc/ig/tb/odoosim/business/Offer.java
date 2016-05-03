package ch.hearc.ig.tb.odoosim.business;

public class Offer {
    private Corporation supplier;
    private Good good;
    private int quantity;
    private double price;

    public Offer(Corporation supplier, Good good, int quantity, double price) {
        this.supplier = supplier;
        this.good = good;
        this.quantity = quantity;
        this.price = price;
    }
    
}