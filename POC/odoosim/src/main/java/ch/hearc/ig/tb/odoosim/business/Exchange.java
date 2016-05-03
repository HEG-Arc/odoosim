package ch.hearc.ig.tb.odoosim.business;

public abstract class Exchange {
    protected Consumer purchaser;
    protected Offer vendor;
    protected int quantity;
    protected double price;
    protected double amount;
    protected double tva;

    public Exchange(Consumer purchaser, Offer vendor, int quantity, double price) {
        this.purchaser = purchaser;
        this.vendor = vendor;
        this.quantity = quantity;
        this.price = price;
    }
    
    protected abstract void calculAmount();
}